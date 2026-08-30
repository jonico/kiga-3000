# `legacy/` — the recovered 2004-2006 originals, untouched

Nothing in this directory is modified. It exists so that every later change is
auditable against the real starting point, and so the recovery is reproducible by
someone who no longer trusts the rest of this repository.

## Where this came from

The original project is [KiGa 3000 on SourceForge](https://sourceforge.net/projects/kiga3000/),
registered 2004-10-18, last file release 2013-03-07, licensed GPL-2.0. SourceForge
never migrated it off CVS, and CVS write access has since been retired, so the code
was recovered from the read-only CVS snapshot:

```bash
curl -L -o kiga3000-cvs.zip https://sourceforge.net/code-snapshots/cvs/k/ki/kiga3000.zip
unzip kiga3000-cvs.zip -d cvsroot            # 3.6 MB, 1525 RCS (,v) files
cvs -d "$PWD/cvsroot/kiga3000" co kiga3000   # trunk HEAD
```

## What the CVS repository actually contains

Two different codebases live in the same repository, which is the first thing that
has to be understood before anything can be built:

| | trunk HEAD | branch `RB_1_3` |
|---|---|---|
| Layout | flat, default package | `org.de.kiga3000.*`, MVC layered |
| Java files | 34 | 73 |
| Lines | 4,129 | 9,133 |
| Released? | yes — this is what `Kiga3000.jar` on SourceForge was built from | no |
| State on trunk | live | `state dead` (revision 1.1 is the "added on branch" placeholder) |

The package-structured version was never merged to trunk. Every one of its 138 files
sits in a CVS `Attic/` and exists only on branch `RB_1_2` / `RB_1_3`.

**This repository continues from `RB_1_3`**, chosen deliberately: its package
structure is better ground for automated refactoring, and its MVC split is closer to
something worth reviving. The trade-off is that it is an abandoned branch — see
"Known defects inherited from the branch" below.

### `rb_1_3/`

`cvs export -r RB_1_3 kiga3000`, verbatim, with one omission: the generated
`doc/` Javadoc tree (200+ HTML files of 2006 Javadoc noise). Everything else is as
CVS produced it, including the vendored dependency binaries under `com/`.

### `trunk-head-src-lib/`

`src/lib/` from trunk HEAD: the vendored dependency binaries, checked into CVS as
loose `.class` files rather than jars.

This directory is here for a specific reason. **`RB_1_3` does not compile against its
own vendored copy of the Liquid Look-and-Feel.** The branch calls
`LiquidLookAndFeel.setPanelTransparency(boolean)`, but the `LiquidLookAndFeel.class`
committed on the branch does not declare that method:

```
$ javap -cp legacy/rb_1_3 com.birosoft.liquid.LiquidLookAndFeel | grep setPanel
                                                    # nothing
$ javap -cp legacy/trunk-head-src-lib com.birosoft.liquid.LiquidLookAndFeel | grep setPanel
  public static void setPanelTransparency(boolean);
```

The branch was evidently being built against a newer Liquid than the one it carried. Trunk's copy is the complete Liquid 0.2.8 distribution (215 classes +
75 PNG icons vs. the branch's 155 classes), so the jars in `../lib/repo/` are built
from **this** copy. Without that, the recovered branch cannot be compiled at all.

## Provenance of the code, from the CVS metadata

**CVS commits:** `bdiemer` 1,207 revisions, `bamboocha` 23, `root` 11 (admin files
only). Range 2004-10-18 to 2006-02-19, with all application code arriving in one bulk
import at 2005-11-28 07:29 - which is why every `src/` file starts at revision 1.1 in
late 2005 though the project was registered in 2004.

**In-file `@author` tags**, which are the more informative record: 29 files are tagged
`maestro`, an earlier alias of the project lead, and 16 are tagged `bobo_local` /
`bobohead2` / `bobohead`, the later contributor. `Created on` headers date the `maestro`
files to 24.08.2004 - 07.09.2004.

So the bulk import was someone importing an existing application, not starting one. The
foundation - the entire `interfaces/` package, the entire `database/` layer, and the
principal Swing views - is the project lead's own work, despite there being no commits
under that account. The later contributor's files are the MVC layer wrapped around it
(`control/`, `check/`, `conversion/`, `data/`, `messages/`, `sorting/`) plus the
half-finished `newcard` model, and that work is what branch `RB_1_3` consists of.

## Known defects inherited from the branch

Recorded here so they are not mistaken for damage done during modernization:

1. **Half-finished data model.** `data/` and `interfaces/newcard/` introduce a second
   card model (`IFKigaCard`, `Child`, `HealthCard`, `Adult`, `Parents`, …) in parallel
   with the original `Karteikarte`. It is barely wired in — `KigaCardNew` is
   referenced from exactly one other file.
2. **`KigaCardNewTest` is not a test.** It extends `junit.framework.TestCase` but
   opens a Swing window, requires a live database, and calls `System.exit(1)` in its
   failure path, which would kill the surefire JVM. It is kept compiling but excluded
   from execution.
3. **Credentials in source.** `database/Ressourcen.java` hardcodes the JDBC URL, user
   `KiGa` and password `Kiga3000`. Left as-is; this is a 2006 artefact, and the
   database it points at holds nothing but the demo seed.
4. **No sample data survived.** The CVS repository contains no dumps and no `INSERT`
   statements. `datenbanktabelle` is DDL only. Everything in `../db/seed.sql` was
   written in 2026 — see that file's header for exactly which parts are real and
   which are invented.
