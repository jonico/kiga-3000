#!/usr/bin/env python3
"""Re-cost an arm with documentation/web retrieval excluded.

Why this is not just "subtract the fetches": in an agent loop every turn re-sends the
whole conversation, so a document fetched at turn N is charged again (as cache-read
input) on turns N+1 .. end. Deleting it therefore saves its size MULTIPLIED by the
number of turns that followed, not just once.

Model
-----
For each WebSearch/WebFetch result of estimated size T tokens arriving before turn N:
    saved_input = T * (number of assistant turns from N to the end)
Estimated tokens use 4 chars/token, the usual rough English ratio. This is an
ESTIMATE, and it is an upper bound on the saving in one respect (it assumes the
content would otherwise have been carried verbatim to the end) and a lower bound in
another (it ignores the reasoning tokens the agent spent digesting the docs).

Wall clock: the time attributable to a retrieval is the gap between the turn that
issued it and the next turn, gaps >= STALL_THRESHOLD excluded as before so operator
latency is not counted as doc-reading time.

usage: exclude-docs.py <transcript.jsonl> <label>
"""
import json
import sys
from datetime import datetime

DOC_TOOLS = {"WebSearch", "WebFetch"}
CHARS_PER_TOKEN = 4
STALL_THRESHOLD = 45.0


def parse(ts):
    return datetime.fromisoformat(ts.replace("Z", "+00:00"))


def main():
    path, label = sys.argv[1], sys.argv[2]
    objs = []
    for line in open(path):
        try:
            objs.append(json.loads(line))
        except json.JSONDecodeError:
            pass

    # ---- pass 1: turn timeline, and which turns issued doc calls ----
    turns = []          # (idx, ts, usage, issued_doc_tool_use_ids)
    doc_result_size = {}  # tool_use_id -> chars of result
    doc_call_ts = {}      # tool_use_id -> timestamp of the issuing turn

    for o in objs:
        msg = o.get("message")
        if not isinstance(msg, dict):
            continue
        ts = o.get("timestamp")
        content = msg.get("content")

        if msg.get("usage"):
            issued = []
            if isinstance(content, list):
                for c in content:
                    if (isinstance(c, dict) and c.get("type") == "tool_use"
                            and c.get("name") in DOC_TOOLS):
                        issued.append(c.get("id"))
                        doc_call_ts[c.get("id")] = ts
            turns.append([len(turns), ts, msg["usage"], issued])

        # tool results come back on user-role messages
        if isinstance(content, list):
            for c in content:
                if isinstance(c, dict) and c.get("type") == "tool_result":
                    tid = c.get("tool_use_id")
                    if tid in doc_call_ts:
                        body = c.get("content")
                        if isinstance(body, list):
                            n = sum(len(b.get("text", "")) for b in body
                                    if isinstance(b, dict))
                        else:
                            n = len(str(body))
                        doc_result_size[tid] = n

    total_turns = len(turns)

    # ---- actuals ----
    act_in = sum((t[2].get("input_tokens") or 0)
                 + (t[2].get("cache_creation_input_tokens") or 0)
                 + (t[2].get("cache_read_input_tokens") or 0) for t in turns)
    act_out = sum((t[2].get("output_tokens") or 0) for t in turns)

    # ---- modelled saving ----
    saved_input = 0
    detail = []
    for tid, chars in doc_result_size.items():
        tok = chars // CHARS_PER_TOKEN
        # index of the turn that issued it
        issuing = next((t[0] for t in turns if tid in t[3]), None)
        if issuing is None:
            continue
        carried = total_turns - issuing          # turns that re-send it
        s = tok * carried
        saved_input += s
        detail.append((tid, tok, carried, s))

    # ---- wall clock attributable to doc calls ----
    ts_list = [parse(t[1]) for t in turns if t[1]]
    doc_seconds = 0.0
    for i, t in enumerate(turns):
        if not t[3] or not t[1] or i + 1 >= len(turns) or not turns[i + 1][1]:
            continue
        gap = (parse(turns[i + 1][1]) - parse(t[1])).total_seconds()
        if gap < STALL_THRESHOLD:
            doc_seconds += gap

    active = 0.0
    ordered = sorted(ts_list)
    for a, b in zip(ordered, ordered[1:]):
        g = (b - a).total_seconds()
        if g < STALL_THRESHOLD:
            active += g

    n_calls = len(doc_call_ts)
    print(json.dumps({
        "arm": label,
        "doc_calls": n_calls,
        "doc_results_captured": len(doc_result_size),
        "doc_result_chars": sum(doc_result_size.values()),
        "doc_result_tokens_est": sum(doc_result_size.values()) // CHARS_PER_TOKEN,
        "assistant_turns": total_turns,
        "input_tokens_actual": act_in,
        "input_tokens_saved_est": saved_input,
        "input_tokens_excl_docs_est": max(0, act_in - saved_input),
        "output_tokens_actual": act_out,
        "active_seconds_actual": round(active, 1),
        "active_seconds_in_doc_calls": round(doc_seconds, 1),
        "active_seconds_excl_docs": round(active - doc_seconds, 1),
        "per_call": [
            {"tokens_est": t, "turns_carried": c, "input_saved_est": s}
            for _, t, c, s in sorted(detail, key=lambda x: -x[3])
        ],
    }, indent=2))


if __name__ == "__main__":
    main()
