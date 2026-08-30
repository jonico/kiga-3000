#!/usr/bin/env python3
"""Find copy-paste errors in delegating accessors.

Catches the shape:

    public String getSorgePerson() { return karteikarteSwi.getSonstiges(); }
                     ^^^^^^^^^^^^                            ^^^^^^^^^
                     asked for this               ...but returns this

which is how KiGa 3000's update path wrote `sonstiges` into the `sorgeperson`
column. The earlier .getText()-based audit could not see these, because the
methods delegate to another object rather than touching a component directly.

usage: audit-delegates.py <File.java> [...]
"""
import re
import sys

# public <Type> getX(...) { ... <target>.getY(...) ... }
GET = re.compile(
    r"public\s+[\w<>\[\]]+\s+get(\w+)\s*\([^)]*\)\s*\{(.*?)\n\t\}",
    re.S)
SET = re.compile(
    r"public\s+void\s+set(\w+)\s*\([^)]*\)\s*\{(.*?)\n\t\}",
    re.S)
CALL_GET = re.compile(r"(\w+)\s*\.\s*get(\w+)\s*\(")
CALL_SET = re.compile(r"(\w+)\s*\.\s*set(\w+)\s*\(")


def audit(path):
    src = open(path, encoding="utf-8", errors="replace").read()
    problems = []
    checked = 0

    for name, body in GET.findall(src):
        calls = CALL_GET.findall(body)
        # only consider simple one-delegation accessors
        if len(calls) != 1:
            continue
        checked += 1
        obj, target = calls[0]
        if target.lower() != name.lower():
            problems.append(("get", name, obj, target))

    for name, body in SET.findall(src):
        calls = CALL_SET.findall(body)
        if len(calls) != 1:
            continue
        checked += 1
        obj, target = calls[0]
        if target.lower() != name.lower():
            problems.append(("set", name, obj, target))

    print("=" * 78)
    print(path.split("/")[-1])
    print("  simple delegating accessors checked: %d" % checked)
    if not problems:
        print("  no mismatches")
        return 0
    print("  MISMATCHES: %d" % len(problems))
    for kind, name, obj, target in sorted(problems):
        if kind == "get":
            print("    %-3s %-24s returns %s.get%s()" % (kind, name, obj, target))
        else:
            print("    %-3s %-24s writes  %s.set%s()" % (kind, name, obj, target))
    return len(problems)


total = 0
for p in sys.argv[1:]:
    total += audit(p)
print()
print("total mismatches across %d file(s): %d" % (len(sys.argv) - 1, total))
