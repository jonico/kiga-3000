#!/usr/bin/env python3
"""Measure one experiment arm from its own subagent transcript.

Each arm runs as a subagent and the harness writes that subagent's complete JSONL
transcript to its own file, so an arm's cost is simply the sum over that file. No
timestamp windowing and no attribution heuristics are needed.

On the token numbers: every assistant turn in an agent loop re-sends the whole
conversation, so summing input_tokens across turns is NOT double counting from a
billing point of view - each request really is billed for its full input. That is
why the headline figure is the sum, and why cache_read_input_tokens is reported
separately: it is the part that was re-sent but served from cache.

On wall clock: a tool call awaiting human approval appears as a long gap between
consecutive entries. Raw span would therefore partly measure operator reaction time
and how many prompts the arm happened to trigger, which is not what is being
compared. Gaps at or above STALL_THRESHOLD are reported as stalled, not as work.

usage: measure.py <subagent-transcript.jsonl> <label>
"""
import json
import sys
from datetime import datetime

STALL_THRESHOLD = 45.0


def parse(ts):
    return datetime.fromisoformat(ts.replace("Z", "+00:00"))


def main():
    path, label = sys.argv[1], sys.argv[2]

    tot = {
        "input_tokens": 0,
        "cache_creation_input_tokens": 0,
        "cache_read_input_tokens": 0,
        "output_tokens": 0,
    }
    turns = 0
    tool_calls = 0
    denials = 0
    models = {}
    ts = []

    with open(path) as fh:
        for line in fh:
            try:
                o = json.loads(line)
            except json.JSONDecodeError:
                continue

            if o.get("toolDenialKind"):
                denials += 1

            t = o.get("timestamp")
            if t:
                ts.append(parse(t))

            msg = o.get("message")
            if not isinstance(msg, dict):
                continue

            usage = msg.get("usage")
            if usage:
                turns += 1
                for k in tot:
                    tot[k] += usage.get(k) or 0
                m = msg.get("model")
                if m:
                    models[m] = models.get(m, 0) + 1

            content = msg.get("content")
            if isinstance(content, list):
                tool_calls += sum(
                    1 for c in content
                    if isinstance(c, dict) and c.get("type") == "tool_use"
                )

    active = stalled = 0.0
    stall_count = 0
    ordered = sorted(ts)
    for a, b in zip(ordered, ordered[1:]):
        gap = (b - a).total_seconds()
        if gap >= STALL_THRESHOLD:
            stalled += gap
            stall_count += 1
        else:
            active += gap

    total_in = (
        tot["input_tokens"]
        + tot["cache_creation_input_tokens"]
        + tot["cache_read_input_tokens"]
    )

    print(json.dumps({
        "arm": label,
        "transcript": path,
        # timing
        "agent_active_seconds": round(active, 1),
        "stalled_seconds": round(stalled, 1),
        "stall_events": stall_count,
        "stall_threshold_seconds": STALL_THRESHOLD,
        "raw_span_seconds": round((ordered[-1] - ordered[0]).total_seconds(), 1) if ordered else None,
        "first_entry": ordered[0].isoformat() if ordered else None,
        "last_entry": ordered[-1].isoformat() if ordered else None,
        # effort
        "assistant_turns": turns,
        "tool_calls": tool_calls,
        "tool_denials": denials,
        "models": models,
        # tokens
        "input_tokens_uncached": tot["input_tokens"],
        "cache_creation_input_tokens": tot["cache_creation_input_tokens"],
        "cache_read_input_tokens": tot["cache_read_input_tokens"],
        "input_tokens_total": total_in,
        "output_tokens": tot["output_tokens"],
        "billable_tokens_total": total_in + tot["output_tokens"],
    }, indent=2))


if __name__ == "__main__":
    main()
