#!/usr/bin/env python3
"""Audit a Swing view class for crossed field/component wiring.

Looks for two smells in the 2006 card view:
  1. getX() and setX() reading/writing DIFFERENT Swing components.
  2. Two different logical fields backed by the SAME component - which would make one
     field's value overwrite the other's on save.

usage: audit-view.py <ViewClass.java>
"""
import re
import sys
from collections import defaultdict

src = open(sys.argv[1], encoding="utf-8", errors="replace").read()

getters = dict(re.findall(
    r"public\s+String\s+get(\w+)\s*\(\s*\)\s*\{\s*return\s+(\w+)\s*\.\s*getText\s*\(\s*\)\s*;", src))
setters = dict(re.findall(
    r"public\s+void\s+set(\w+)\s*\(\s*String\s+\w+\s*\)\s*\{\s*(\w+)\s*\.\s*setText", src))

print("text getters: %d   text setters: %d" % (len(getters), len(setters)))
print()

print("### getter and setter disagree about the backing component ###")
bad = 0
for name in sorted(set(getters) | set(setters)):
    gc, sc = getters.get(name), setters.get(name)
    if gc and sc and gc != sc:
        print("  %-26s get -> %-22s set -> %s" % (name, gc, sc))
        bad += 1
if not bad:
    print("  (none)")
print()

print("### one component read by several logical fields ###")
rev = defaultdict(list)
for name, comp in getters.items():
    rev[comp].append(name)
dup = 0
for comp, names in sorted(rev.items()):
    if len(names) > 1:
        print("  component %-22s read by: %s" % (comp, ", ".join(sorted(names))))
        dup += 1
if not dup:
    print("  (none)")
print()

print("### one component written by several logical fields ###")
revs = defaultdict(list)
for name, comp in setters.items():
    revs[comp].append(name)
dup2 = 0
for comp, names in sorted(revs.items()):
    if len(names) > 1:
        print("  component %-22s written by: %s" % (comp, ", ".join(sorted(names))))
        dup2 += 1
if not dup2:
    print("  (none)")
