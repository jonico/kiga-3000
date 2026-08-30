#!/usr/bin/env python3
"""Reconstruct an arm's actual ordered actions from its subagent transcript.

This is derived after the fact from the transcript the harness writes anyway, so it
costs the arm nothing and cannot influence what the arm did. It is the objective
counterpart to the arm's own self-report: if an arm claims a recipe did the work but
the step log shows twenty hand edits, the step log wins.

usage: extract-steps.py <subagent.jsonl> [--full]
"""
import json
import re
import sys
from collections import Counter

TRUNC = 160


def describe(name, inp, truncate=True):
    """One compact line describing a tool call.

    truncate=False returns the untruncated string; signal detection must use that,
    otherwise a recipe id or plugin goal past the display cutoff is silently missed.
    """
    if name == "Bash":
        cmd = " ".join((inp.get("command") or "").split())
        return cmd[:TRUNC] if truncate else cmd
    if name in ("Edit", "Write", "Read", "NotebookEdit"):
        path = inp.get("file_path") or ""
        if truncate:
            return path
        # Recipe ids also live inside written files (e.g. rewrite.yml), so signal
        # detection needs the payload, not just the path.
        body = inp.get("content") or inp.get("new_string") or ""
        return f"{path}\n{body}"
    if name in ("Grep", "Glob"):
        return f"{inp.get('pattern','')}  in {inp.get('path','.')}"
    if name == "WebFetch":
        return inp.get("url", "")
    if name == "WebSearch":
        return inp.get("query", "")
    if name == "ToolSearch":
        return inp.get("query", "")
    # fall back to a short json rendering
    return json.dumps(inp)[:TRUNC]


def main():
    path = sys.argv[1]
    full = "--full" in sys.argv

    steps = []      # (tool, truncated description) - for display
    raw = []        # (tool, full description)      - for signal detection
    counts = Counter()
    with open(path) as fh:
        for line in fh:
            try:
                o = json.loads(line)
            except json.JSONDecodeError:
                continue
            msg = o.get("message") or {}
            content = msg.get("content")
            if not isinstance(content, list):
                continue
            for c in content:
                if not isinstance(c, dict) or c.get("type") != "tool_use":
                    continue
                name = c.get("name", "?")
                inp = c.get("input") or {}
                counts[name] += 1
                steps.append((name, describe(name, inp)))
                raw.append((name, describe(name, inp, truncate=False)))

    print(f"# tool calls: {sum(counts.values())}")
    for n, c in counts.most_common():
        print(f"  {c:4d}  {n}")

    # Signals that separate "recipe did it" from "I typed it"
    mvn_rewrite = [
        s for n, s in raw
        if n == "Bash" and "rewrite" in s.lower() and "mvn" in s.lower()
    ]
    # Recipe ids appear both in commands and inside rewrite.yml written via Write;
    # scan the full text of every call, and drop bare coordinates like
    # "org.openrewrite.recipe" / "org.openrewrite.maven" that are groupIds, not recipes.
    NOT_RECIPES = {"org.openrewrite.recipe", "org.openrewrite.maven", "org.openrewrite"}
    recipe_ids = sorted({
        m for _, s in raw
        for m in re.findall(r"org\.openrewrite\.[A-Za-z0-9_.]*[A-Za-z0-9_]", s)
        if m not in NOT_RECIPES
    })
    # paths only, from the truncated view - raw carries whole file bodies
    edits = [s for n, s in steps if n in ("Edit", "Write", "NotebookEdit")]
    print(f"\n# rewrite-plugin invocations: {len(mvn_rewrite)}")
    print(f"# distinct openrewrite recipe ids seen in commands: {len(recipe_ids)}")
    for r in recipe_ids:
        print(f"  {r}")
    print(f"\n# file-modifying calls (Edit/Write): {len(edits)}")
    for f in sorted(set(edits)):
        print(f"  {f}")

    if full:
        print("\n# ordered step log")
        for i, (n, s) in enumerate(steps, 1):
            print(f"{i:4d}. {n:<12} {s}")


if __name__ == "__main__":
    main()
