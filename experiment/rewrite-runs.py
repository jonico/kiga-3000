#!/usr/bin/env python3
"""List every rewrite-maven-plugin invocation an arm made, in order, and mark which
were rejected - so a denied recipe run can be checked against later retries.

usage: rewrite-runs.py <subagent-transcript.jsonl>
"""
import json
import re
import sys


def main():
    objs = []
    for line in open(sys.argv[1]):
        try:
            objs.append(json.loads(line))
        except json.JSONDecodeError:
            pass

    # tool_use_id -> command, for the ones that are rewrite invocations
    calls = {}
    order = []
    for o in objs:
        msg = o.get("message")
        if not isinstance(msg, dict):
            continue
        content = msg.get("content")
        if not isinstance(content, list):
            continue
        for x in content:
            if not isinstance(x, dict):
                continue
            if x.get("type") == "tool_use" and x.get("name") == "Bash":
                cmd = " ".join((x.get("input") or {}).get("command", "").split())
                if "rewrite:run" in cmd or "rewrite:dryRun" in cmd or "rewrite:discover" in cmd:
                    ts = (o.get("timestamp") or "")[11:19]
                    calls[x.get("id")] = (ts, cmd)
                    order.append(x.get("id"))

    # which tool_use_ids were rejected
    rejected = set()
    for o in objs:
        if not o.get("toolDenialKind"):
            continue
        msg = o.get("message")
        if isinstance(msg, dict):
            content = msg.get("content")
            if isinstance(content, list):
                for x in content:
                    if isinstance(x, dict) and x.get("tool_use_id"):
                        rejected.add(x["tool_use_id"])

    print(f"{'time':9} {'status':9} {'goal':8} recipes")
    print("-" * 100)
    for tid in order:
        ts, cmd = calls[tid]
        goal = ("discover" if "discover" in cmd
                else "dryRun" if "dryRun" in cmd else "run")
        recipes = re.findall(r"activeRecipes=([^ ]+)", cmd)
        rec = recipes[0] if recipes else "(from pom.xml)"
        status = "REJECTED" if tid in rejected else "ok"
        print(f"{ts:9} {status:9} {goal:8} {rec}")

    print()
    print(f"total rewrite invocations: {len(order)}, rejected: "
          f"{sum(1 for t in order if t in rejected)}")


if __name__ == "__main__":
    main()
