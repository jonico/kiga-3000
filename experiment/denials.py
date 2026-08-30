#!/usr/bin/env python3
"""List the tool calls an arm had denied, and what it was trying to do.

A denied call is an experimental confound: the arm has to find another route, which
costs it turns and tokens the other arm may not have paid. Worth reporting explicitly
rather than letting it hide inside the totals.

usage: denials.py <subagent-transcript.jsonl>
"""
import json
import sys


def main():
    path = sys.argv[1]
    lines = [l for l in open(path)]
    objs = []
    for l in lines:
        try:
            objs.append(json.loads(l))
        except json.JSONDecodeError:
            pass

    # map tool_use id -> what was requested
    requested = {}
    for o in objs:
        msg = o.get("message")
        if not isinstance(msg, dict):
            continue
        content = msg.get("content")
        if not isinstance(content, list):
            continue
        for c in content:
            if isinstance(c, dict) and c.get("type") == "tool_use":
                inp = c.get("input") or {}
                desc = inp.get("command") or inp.get("file_path") or json.dumps(inp)
                requested[c.get("id")] = (c.get("name"), " ".join(str(desc).split()))

    n = 0
    for o in objs:
        kind = o.get("toolDenialKind")
        if not kind:
            continue
        n += 1
        ts = (o.get("timestamp") or "")[:19]
        # find the tool_use_id this result belongs to
        tid = None
        msg = o.get("message")
        if isinstance(msg, dict):
            content = msg.get("content")
            if isinstance(content, list):
                for c in content:
                    if isinstance(c, dict) and c.get("tool_use_id"):
                        tid = c.get("tool_use_id")
        name, desc = requested.get(tid, ("?", "?"))
        print(f"[{ts}] kind={kind}  tool={name}")
        print(f"    {desc[:240]}")

    print(f"\ntotal denials: {n}")


if __name__ == "__main__":
    main()
