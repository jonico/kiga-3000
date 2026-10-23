# KiGa 3000

> **Note on this repository.** This is the public release of a private working repository. Two
> things were changed for publication: `db/seed.sql`'s first section previously used the names and
> business-card details of real people, and has been replaced with invented children; and the raw
> agent transcripts under `experiment/` are omitted for the same reason. Git history starts fresh
> at this commit rather than being rewritten, so the removed data is not recoverable from here.
> Some links below point at private repositories and will not resolve for everyone.
 — reloaded

A German Kindergarten card-index application, written in 2004–2006, recovered from
SourceForge CVS in 2026 and brought back to life on a current Java stack.

This repository is both the revived application and a written record of the revival:
what was recovered, what was already broken, what a recipe-driven modernization tool
did well, what it did badly, and what had to be done by hand. Findings that reflect
poorly on the original code — much of which is the repository owner's own — are
included, because leaving them out would make the rest less trustworthy.

**Original project:** <https://sourceforge.net/projects/kiga3000/> · GPL-2.0 ·
registered 2004-10-18 · last file release 2013-03-07

---

## Quick start

```bash
./run.sh              # finds a JDK, builds if needed, starts the application
./run.sh --check      # report which JDK would be used, then exit
```

A MySQL server with the schema applied is needed for anything beyond the start screen:

```bash
mysql -u root < db/schema.sql     # fresh database
mysql -u root < db/seed.sql       # 17 demo records
```

For an existing database created before 2026, run `db/migrate-01-nullable-dates.sql`
instead of `schema.sql`.

Why `run.sh` rather than `java -jar target/Kiga3000.jar`: on a Mac whose only JDK came
from Homebrew, the JDK is keg-only and therefore not on `PATH`, and `/usr/bin/java` is
Apple's stub, which answers *"Unable to locate a Java Runtime"* even though a perfectly
good JDK is installed. `run.sh` looks in `$JAVA_HOME`, then `/usr/libexec/java_home`,
then `PATH`, then the Homebrew locations. The plain `java -jar` route works fine once a
JDK is reachable.

---

## What the application is

An "extendable tool to store, manage, search and print the data records (about 50
domain specific properties) of children in a German Kindergarten". One MySQL table,
`Karteikarte`, with 39 mapped columns: the child, both parents, addresses and phone
numbers, religion and nationality, siblings, vaccinations, illnesses, doctor and health
insurer, plus free-text notes. A Swing desktop front end over it, with search, group
management, a data-retention purge, and printing.

The user interface, the identifiers and the comments are German. The source files are
declared ISO-8859-1.

---

## Recovering the source

SourceForge never migrated the project off CVS, and CVS write access has since been
retired, so the code came from the read-only snapshot:

```bash
curl -L -o kiga3000-cvs.zip https://sourceforge.net/code-snapshots/cvs/k/ki/kiga3000.zip
unzip kiga3000-cvs.zip -d cvsroot            # 3.6 MB, 1525 RCS (,v) files
cvs -d "$PWD/cvsroot/kiga3000" co kiga3000
```

### There are two codebases in there

The first thing that has to be understood before anything can be compiled:

| | trunk HEAD | branch `RB_1_3` |
|---|---|---|
| Layout | flat, default package | `org.de.kiga3000.*`, MVC layered |
| Java files | 34 | 73 |
| Lines | 4,129 | 9,133 |
| Released? | yes — `Kiga3000.jar` on SourceForge was built from it | no |
| State on trunk | live | `state dead` — revision 1.1 is the "added on branch" placeholder |

The package-structured version was never merged. All 138 of its files sit in CVS
`Attic/` directories and exist only on `RB_1_2` / `RB_1_3`.

**This repository continues from `RB_1_3`**, chosen deliberately: its package structure
is better ground for automated refactoring and its MVC split is closer to something
worth reviving. The cost is that it is an abandoned branch, with the defects noted
below.

### What the history shows

The CVS commit log and the in-file authorship tags tell different stories, and the
tags are the more informative of the two.

**CVS commits** — three accounts: one with 1,207 revisions (`bdiemer`), one with 23
(`bamboocha`), and the server's own with 11 (admin files only). Date range 2004-10-18
(project registration, `CVSROOT/cvswrappers` only) through 2006-02-19. Every file under
`src/` starts at revision 1.1 in late 2005, because **all application code arrived in a
single bulk import** at 2005-11-28 07:29.

**In-file `@author` tags** — 45 files carry one:

| Alias | Files | What |
|---|---|---|
| `maestro` | 29 | the project lead's own earlier alias |
| `bobo_local` / `bobohead2` / `bobohead` | 16 | the later contributor |

So the code predates its own CVS history by more than a year, and the bulk import was
someone importing an existing application rather than starting one. The
`Created on` headers date the `maestro` files to **24.08.2004 – 07.09.2004**, a fortnight
of work that produced the foundation:

- the whole `interfaces/` package — the domain contracts (`Karteikarte`,
  `SuchDatenholer`, `Datenschutz`, …), dated 24–25 August
- the whole `database/` layer — every JDBC DAO and `SQLHelfer`, `Ressourcen`
- the principal Swing views — `KarteiKarteSwingImpl` (1,434 lines),
  `Kiga3000MainPanel`, `KarteikartenDialogSwingImpl`
- `KigaException`

The later contributor's 16 files are the layer wrapped around that: `control/`,
`check/`, `conversion/`, `data/`, `messages/`, `listener/`, `tables/`, `sorting/`, and
the half-finished parallel `newcard` model. That work is also what the `RB_1_3` branch
consists of.

This matters for reading the rest of this document. It would be easy to conclude from
the commit log that the project lead wrote none of it — there is not a single commit
under that account. The authorship tags say the opposite: the majority of the
application, and all of its foundations, are theirs, committed by someone else.

A note on how the defects further down are written: they are described in terms of the
code, not of whoever wrote it, and no defect is attributed to an author. This was a
volunteer project two decades ago, built against Java 1.4 and MySQL 4, and most of what
broke did so because the platform moved underneath it — CLDR locale data, removed
authentication protocols, stricter `sql_mode`, a native macOS printing path. The
interesting content is the failure mode, not the attribution.

### Recovering the dependencies

Neither dependency was on Maven Central; both were committed to CVS as loose `.class`
files. They were jarred verbatim into a project-local Maven repository (`lib/repo/`):

- **Liquid Look and Feel 0.2.8** (2004) — 155 classes + 75 PNG icons. The application
  refuses to start without it.
- **MySQL Connector/J 3.0.14-production** (2004) — used for the baseline only.

One trap worth recording: **`RB_1_3` does not compile against its own vendored copy of
the Look and Feel.** The branch calls `LiquidLookAndFeel.setPanelTransparency(boolean)`,
and the `LiquidLookAndFeel.class` committed on that branch does not declare it:

```
$ javap -cp legacy/rb_1_3 com.birosoft.liquid.LiquidLookAndFeel | grep setPanel
                                                    # nothing
$ javap -cp legacy/trunk-head-src-lib com.birosoft.liquid.LiquidLookAndFeel | grep setPanel
  public static void setPanelTransparency(boolean);
```

Whoever worked on the branch was evidently building against a newer Liquid than the
branch itself carried. Trunk's copy is the complete 0.2.8 distribution, so the jars are built from
that. Without noticing this, the recovered branch cannot be compiled at all.

---

## What was already dead in 2026

Before any modernization, three things were verified broken — measured, not assumed:

**1. The 2004 JDBC driver cannot connect to any MySQL you can install today.**
MySQL 26.7 offers only `sha256_password` and `caching_sha2_password`;
`mysql_native_password` no longer exists. Connector/J 3.0.14 dies in
`MysqlIO.secureAuth411`:

```
Client does not support authentication protocol requested by server;
consider upgrading MySQL client
```

**2. The original DDL no longer parses.** `datenbanktabelle` ends with `TYPE=MyISAM`;
the `TYPE=` clause was removed in MySQL 5.5 → `ERROR 1064`.

**3. `DATE NOT NULL DEFAULT '0000-00-00'` is rejected** while `NO_ZERO_DATE` and
`NO_ZERO_IN_DATE` are in `sql_mode`, which has been the default since MySQL 5.7.

The first of these matters more than it looks: because the application could never
reach a database, whole regions of code had not executed on any modern JVM. Several of
the bugs below had been latent for a decade and surfaced the moment the driver worked.

---

## Modernization, in order

Each step is one commit, and each commit is independently buildable. `git log` is the
authoritative record; this is the summary.

| # | Commit | What |
|---|---|---|
| 1 | `9fc0f87` | Recover the 2004–2006 originals, untouched, into `legacy/` |
| 2 | `2640dfa` | Make it build again: Maven wrapper, **no source changes** |
| 3 | `832bc09` | Java 8 → 21 via OpenRewrite recipes |
| 4 | `922b564` | Fix date conversion, broken on every JDK since 9 |
| 5 | `c16b54c` | Fix wrong-column-on-save and a native JVM crash on print |
| 6 | `991885d` | Test suite: 2 assertion-free tests → 40 real ones |
| 7 | `0f59f99` | Application icon (`java.awt.Taskbar`, Java 9+) |
| 8 | `0809f46` | Load packaged resources from the classpath; runnable jar |
| 9 | `141632a` | `run.sh`, so starting it does not require finding a JDK by hand |
| 10 | `7ac0762` | Bring the window to the foreground (`Desktop.requestForeground`, Java 9+) |
| 11 | `98337f5` | Retire the `'0000-00-00'` sentinel: nullable dates, no `sql_mode` hacks |
| 12 | `bf27978` | Introduce JPA/Hibernate, Bean Validation and HikariCP |
| 13 | `0f1b411` | Cut card CRUD over to JPA; delete the superseded DAOs |

### Java 8 → 21, by recipe

Driven by `rewrite-maven-plugin` 6.46.1 from Maven Central. Recipes that did the work:

| Recipe | Effect |
|---|---|
| `org.openrewrite.java.migrate.UpgradeToJava21` | language level 1.8 → 21; via `PrimitiveWrapperClassConstructorToValueOf`, `new Byte(byte)` ×2 and `new Integer(int)` → `valueOf` |
| `org.openrewrite.staticanalysis.NoFinalizer` | removed an IDE-generated no-op `finalize()` — the only deprecated-**for-removal** API present |
| `org.openrewrite.java.migrate.lang.JavaLangAPIs` | `Class.forName(..).newInstance()` → `getDeclaredConstructor().newInstance()` |
| `org.openrewrite.java.testing.junit5.JUnit4to5Migration` | off `junit.framework.TestCase`, `@Test` added, `junit:junit:3.8.2` removed |

Three changes had no published recipe and were expressed as declarative composites in
`rewrite.yml`: `Window/Dialog.show()` → `setVisible(true)`, the Connector/J
coordinate + driver-class + URL change, and adding the Jupiter dependency.

Result: **6 production Java files, 15 lines of production code**. Bytecode major
version 65, zero deprecation or removal warnings where the baseline emitted eight.

Two things no recipe could decide, which needed real reasoning:

- `zeroDateTimeBehavior` — `getString()` returns `"0000-00-00"` verbatim in every mode,
  but `getDate()` throws under the default `EXCEPTION` and `CONVERT_TO_NULL` would NPE
  inside `SQLHelfer`. Determined by probing five URL variants against a live server.
- `new URL(String)` — `URLConstructorToURICreate` **deliberately declines** this call
  site, because the argument is a runtime concatenation and `URI.create()` rejects
  inputs `new URL()` accepts. The refusal is correct; reading the recipe's source was
  the only way to establish that rather than assume a bug.

### Architectural modernization: JDBC → JPA

The card was persisted by four hand-written DAOs whose `INSERT` and `UPDATE` each bound
**39 positional parameters** and duplicated the full column list. One misnumbered index
silently writes a value into the wrong column — and that is not hypothetical, see
"wrong column on save" below.

Now: one JPA entity, an `AttributeConverter` for the date boundary, a repository, Bean
Validation, and HikariCP. **240 lines deleted.**

Deliberately *not* done: the set-based queries (`Such*SQLImpl`,
`GruppenVerschieberSQLImpl`, `DatenschutzSQLImpl`) stay on hand-written SQL. They are
aggregate operations where SQL is the better tool, and JPQL there would be ORM for its
own sake. JDBC and JPA coexist on purpose.

Also honest about the remaining wart: a 253-line hand-rolled `ConnectionPool` with a
`Timer`-based `ConnectionKiller` still serves those queries, sitting beside HikariCP.
That halfway house is the next thing to remove.

#### Library versions are deliberately old

*(As introduced. Round 2 has since migrated all of these forward - see below.)*

Hibernate **5.6.15** with `javax.persistence`, Hibernate Validator **6.2.5**, HikariCP
**5.1.0**, SLF4J **1.7.36**, `MySQL8Dialect`. All current at nobody's idea of "latest".

This is intentional. The point was to create genuine migration surface for a second
experiment — `rewrite-hibernate` ships `MigrateToHibernate60` through `71`, and
`rewrite-migrate-java` handles the `javax` → `jakarta` namespace move. Starting on
Hibernate 7 would have made the second round a no-op.

Hibernate 5.6 on Java 21 was verified in a throwaway project *before* committing to it:
ByteBuddy 1.12.18 suffices because the entity has no associations needing proxies.

---

## Bugs found

Five defects, all latent in the 2006 code, all unreachable until the driver worked.
None were introduced by the modernization; three were found by manual testing and two
by writing tests.

### 1. No date could be parsed on any JDK since 9 — *fixed*

`DateConversion` obtained both of its formats from JDK locale data:

```java
dateToLocale = DateFormat.getDateInstance(MEDIUM, Locale.getDefault());
dateToMysql  = DateFormat.getDateInstance(MEDIUM, Locale.CHINA);
```

`Locale.CHINA` was a stand-in for the MySQL wire format: under Java 8's COMPAT locale
data, China's MEDIUM pattern was `yyyy-M-d`, close enough to ISO to parse a database
date. **JDK 9 switched to CLDR, where it became `y年M月d日`.** From that release the
fallback could not parse a database date, and the primary format only worked if the
machine's locale happened to be German. Clicking any record threw `ParseException`
five times over — once per date field.

Fixed with two explicit, locale-independent patterns. Parsing is now strict: the old
lenient behaviour silently accepted `30.02.2005` and stored it as 2 March, which is not
acceptable for a child's date of birth.

### 2. Editing a card wrote `sonstiges` into the `sorgeperson` column — *fixed*

A copy-paste slip in an IDE-generated stub, in `KarteikartenDialogSwingImpl`:

```java
public String getSonstiges()   { return karteikarteSwi.getSonstiges(); }   // correct
public String getSorgePerson() { return karteikarteSwi.getSonstiges(); }   // WRONG
```

The setter was correct, so it only showed on save — and `KigaMainViewControl` harvests
updates from *that dialog*, not from the panel the user typed into. So every update
took `sorgeperson` from the wrong getter. With `sonstiges` at `varchar(255)` and
`sorgeperson` at `varchar(100)`, a long note produced

```
Data truncation: Data too long for column 'sorgeperson' at row 1
```

Worse than the visible error: a note **under** 100 characters was accepted and silently
filed in the wrong column, overwriting the caregiver names.

Found by auditing delegating accessors for name mismatches — the only real hit out of
78 in that class. The audit script is kept as `tools/audit-delegating-accessors.py`,
because the class itself extends a `JDialog` and cannot be reached by a headless test.

### 3. Printing aborted the JVM natively on macOS — *fixed*

Not an exception, a process abort:

```
Bad JNI lookup sData
libawt_lwawt.dylib  Java_sun_java2d_OSXOffScreenSurfaceData_getSurfaceData
*** Terminating app due to uncaught exception NSGenericException,
    reason: JNI Lookup Exception
```

`PrintUtilities` painted the component into an intermediate `BufferedImage` and blitted
that onto the printer's `Graphics2D`. Drawing an offscreen surface onto a printer
surface is what reaches that native path. It now renders straight onto the printer
graphics — which is also how Swing printing is meant to work; the offscreen copy was a
1999-era workaround inherited from the original utility class.

The class had always shipped `disableDoubleBuffering`/`enableDoubleBuffering`, and its
own javadoc said they mattered for printing. Both calls were commented out.

Worth recording as a methodological point: **this could not be verified automatically.**
A headless PostScript `StreamPrintService` is pure Java and completes happily on both
the old and the new code path. Confirming the fix needed a real print job and a human;
`tools/test-printing.sh` drives that, and takes a `legacy` argument that reproduces the
2006 path for comparison.

### 4. The title picture had never been visible, on any machine — *fixed*

Both the title page and the logging configuration were located by concatenating the
`KiGaLoggingPath` configuration key with a relative path. That key ships **empty**, so
they resolved to `file:////titlepage/kiga.htm` and `/org/de/.../KiGaLogging.properties`
— absolute paths at the filesystem root. Every user therefore got a modal *"no online
help"* error dialog on startup instead of the photograph, and nothing was ever logged.

Both files are packaged inside the jar, where a filesystem path cannot reach them at
all. The application only behaved as designed if someone unpacked it and pointed the
key at the unpacked directory.

Two further defects in the same area: `KiGaLogging.properties` named
`formatter.LoggingFormatter` as its log formatter — a class that exists nowhere in the
project *or* in the original CVS repository, so `java.util.logging` threw
`ClassNotFoundException` and discarded the whole configuration; and the log pattern was
relative to the working directory despite a comment claiming it went to the user's home
directory.

### 5. The search results table is effectively unsorted — *documented, not fixed*

`KarteiKarteComparator`'s conditions are inverted. Each branch returns the comparison
of a field it has just established to be **equal**:

```java
if (surname differs) { ...nested... }
else { return compare(surname); }   // i.e. return 0
```

So equal surnames return 0 and the forename tie-break is unreachable; differing
surnames fall through to comparing the **address**. The result is non-zero only when
surname, forename and date of birth all differ *and* the addresses differ. The ordering
is also not transitive, so `List.sort` can throw *"Comparison method violates its
general contract!"* on larger result sets.

Left unfixed on purpose: it changes what the user sees. It is pinned by eight
characterisation tests in `KarteiKarteComparatorTest`, which assert what the code
actually does, so the behaviour is nailed down before anyone changes it.

### Tests

The 2006 project had two test classes and **neither asserted anything** — both printed
to stdout and caught `ParseException` by printing `Mist`. That is precisely how the
date handling could be completely broken for a decade while the suite stayed green.

Now 107 tests, none requiring a database or a display. Notably `KarteikarteImplTest`
reflectively round-trips every accessor pair with a value unique per field, so crossed
wiring — bug 2's shape — shows up immediately.

The checks that *do* need a live database are named `*Check`, not `*Test`, so surefire
ignores them: `JpaEquivalenceCheck`, `DateConversionEndToEndCheck`.

---

## The controlled experiment

The modernization was also used to answer a question honestly: **does recipe-driven
migration actually cost less than doing it by hand?**

### Method

Two arms, each a fresh agent with no shared context, given a byte-identical brief that
differed in exactly one paragraph — the mandate. Arm A had to drive the change through
Moderne/OpenRewrite recipes; Arm B had to do it by hand and was forbidden from
consulting recipe documentation. Same acceptance criteria, same environment, same
database.

Design decisions that matter for believing the numbers:

- **Fresh subagents, never forks**, so no context leaked between arms.
- **Run sequentially**, so wall-clock was not distorted by two arms competing for CPU.
- **Per-arm Maven repositories, pre-warmed before timing started**, with an identical
  warm set for both arms — including artifacts only one arm would plausibly use, so the
  warm set itself gave nothing away.
- **Tokens measured from each arm's own transcript**, not self-reported.
- **Wall-clock decomposed** into active time and stalls. A tool call awaiting approval
  shows up as a long gap, and counting operator reaction time as work would measure the
  wrong thing. Gaps ≥ 45 s are reported separately.
- **An objective step log** extracted from each transcript, independent of what the arm
  claimed, so "the recipe did it" could be checked against how many files the arm
  actually hand-edited.

Scripts and computed metrics are in [`experiment/`](experiment/). The raw subagent transcripts are **not** published: they contain the personal details of real people that were removed from `db/seed.sql` before publication.

### Round 1 — Java 8 → 21

| Metric | Arm A (recipes) | Arm B (by hand) | |
|---|---|---|---|
| Input tokens | 23.98 M | **11.02 M** | Arm B 54% fewer |
| Output tokens | 74.6 K | **58.1 K** | Arm B 22% fewer |
| Active time | 26.8 min | **16.9 min** | Arm B 37% faster |
| Assistant turns | 224 | **137** | Arm B 39% fewer |
| Tool calls | 133 | **69** | Arm B 48% fewer |
| Production diff | 15 lines | 15 lines | **identical** |

**The hand migration cost roughly half as much and finished sooner — and both arms
produced the same 15-line change.**

The obvious explanation is wrong. Research was cheap: 15 documentation retrievals
totalling ~8,200 tokens. Even accounting for that content being re-sent on every
subsequent turn, removing it entirely would save 1.46 M of 23.98 M. Recomputed with all
Moderne research excluded, Arm A still used **2.04× the tokens and 1.53× the time**.

The cost was the **iteration loop** — 224 turns of run → read failure → reconfigure →
re-run, driven by four specific obstacles:

1. `JUnit4to5Migration` removed `junit:junit` without adding Jupiter, leaving the
   project uncompilable — and because `rewrite:run` binds after
   `process-test-classes`, OpenRewrite could then no longer run to fix it.
2. `AddDependency` is a scanning recipe, so its `onlyIfUsing` marker is evaluated
   against the pre-edit tree and it cannot simply be placed after the recipe that
   removes the type it keys on.
3. A documented recipe id whose option names did not match the recipe class, binding
   null and failing later as a `NullPointerException` inside the visitor rather than as
   a validation error.
4. Two changes with no published recipe, which had to be composed by hand from
   primitives.

Three of those became upstream reports; see below.

### Round 2 — Java 21 → 25, and the persistence stack

Same method, with two deliberate differences, both disclosed because they make the two
rounds **not** directly comparable:

1. **Arm A was given the four Round 1 pitfalls up front.** This is a subsidy. It makes
   Round 2 a measurement of *expert-guided* recipe migration versus unguided hand
   migration. There is no equivalent hand-migration briefing to give Arm B, which is
   precisely why it counts as a subsidy rather than a symmetric improvement.
2. **Arm A started with the tooling already in place** — `rewrite-maven-plugin`
   configured and `rewrite.yml` populated from Round 1. That one is *not* a subsidy: it
   is how a second migration genuinely goes.

Round 1's prewarm also had a bug worth recording: it pinned a plugin version that does
not exist on Maven Central, so Arm A paid an artifact download inside its measured
window. Round 2's prewarm verifies every coordinate resolves first.

Targets: Java 25, Hibernate 5.6 → 7.4.6, `javax.persistence` → `jakarta.persistence`
3.2.0, `javax.validation` → `jakarta.validation` 3.1.1 with Hibernate Validator 9.1.3,
HikariCP 5.1 → 7.1, SLF4J 1.7 → 2.0.18, and the `MySQL8Dialect` rename.

**Arm A's result is what this repository now ships** - see the merge commit. Arm B's
tree is kept for comparison but not merged; merging both was never possible, and Arm A
was chosen for the reusable `rewrite.yml` rather than for being cheaper, which it was
not.

| Metric | Arm A (recipes, *subsidised*) | Arm B (by hand) | |
|---|---|---|---|
| Input tokens | 11.34 M | **5.85 M** | Arm B 48% fewer |
| Output tokens | 53.7 K | **30.4 K** | Arm B 43% fewer |
| Active time | 10.8 min | **6.9 min** | Arm B 36% faster |
| Assistant turns | 118 | **97** | Arm B 18% fewer |
| Tool calls | 72 | **60** | Arm B 17% fewer |
| Files changed | 10 (+233 / −74) | 9 (+96 / −76) | 144 of Arm A's added lines are `rewrite.yml` |
| Java sources hand-edited | **0** | 5 (one scripted substitution) | |

**Arm B was cheaper again, by almost the same margin — even though Arm A was the
subsidised one this time.**

The pitfall briefing clearly worked in absolute terms: Arm A went from 224 turns and
23.98 M tokens in Round 1 to 118 turns and 11.34 M here, and said explicitly that
being forewarned turned would-be debugging detours into 30-second checks. But Arm B got
cheaper too, because the second migration is intrinsically easier than the first. The
ratio barely moved.

Both arms reached an equivalent end state, and both found the same two things
independently:

- **`run.sh` was silently broken by the bytecode bump.** Its JDK fallback list still
  preferred `openjdk@21`, which cannot load a major-69 class file. No compiler error, no
  test failure — it would have failed for a user long after a green `mvn verify`. Arm B
  went further and reproduced the exact `UnsupportedClassVersionError` against JDK 21.
- **`slf4j-api` resolves to 2.0.17 while `slf4j-jdk14` is 2.0.18**, via nearest-wins
  through HikariCP. Both judged the skew harmless and declined to add an unrequested
  pin.

Where they differed is instructive:

- **Arm A hand-edited no Java at all.** Every source change came from a recipe. Its
  larger diff is mostly `rewrite.yml` — durable, re-runnable configuration.
- **Arm B's diff is smaller** and its reasoning more forensic: it found the
  `org.hibernate` → `org.hibernate.orm` groupId move by noticing that 7.4.6 exists only
  under `org/hibernate/orm/` in the local repository, established that `MySQL8Dialect`
  is gone by listing the jar, and worked out that Hibernate Validator 9.1 declares the
  EL API `provided`/`optional` by reading its pom — so the implementation must be
  supplied explicitly.
- **Arm A had to work around a recipe that overreached.** `UpgradeToJava25` includes
  `ReplaceSystemOutWithIOPrint`, which would have rewritten 39 `System.out` calls —
  including inside the very check programs whose stdout is the acceptance evidence.
  OpenRewrite cannot subtract a sub-recipe, so Arm A rebuilt the composite from the
  sub-recipes it wanted and documented the omission.
- **A published recipe could not reach the target of criterion 8.** `MigrateDialect` is
  a set of `ChangeType` recipes and therefore Java-only, but the dialect here is an XML
  attribute. Arm A reproduced the prescribed outcome with `ChangeTagAttribute`, scoped
  by `FindSourceFiles`.

The most interesting shared finding: **two Hibernate major versions cost almost nothing
in Java code.** Nothing outside `persistence.xml` names a Hibernate type — the
application only ever touches the standard JPA API — so a 5.6 → 7.4 jump reduced to
coordinates, a dialect rename and a namespace change. That is a property of the codebase
that the ORM introduction happened to create, and it flattered both arms equally.

### What not to conclude

- **n = 1 per arm.** Agents are stochastic; a rerun could differ substantially. This is
  a case study, not a benchmark.
- **The arms are not equally difficult by construction.** Arm A must discover which
  recipes exist; Arm B must type more. That asymmetry *is* the thing being measured, but
  it means the result is about *this* migration at *this* size.
- **Size matters, and this codebase is small.** 73 files. Recipe discovery is a fixed
  cost that amortises across a codebase; on 5,000 files the arithmetic plausibly
  inverts, and nothing here measures that.
- **Recipes bring guarantees a hand migration does not.** Every Arm A change is a named,
  reviewable, re-runnable transformation. Arm B's changes are equally correct here and
  entirely bespoke. On a codebase where the same migration must be applied to forty
  repositories, that difference outweighs the token count.

---

## The new-stack feature: an HTTP API and a web interface

The brief asked for functionality that is only possible on the newer stack. Three
candidates appeared along the way, and it is worth being clear about which is which.

**Two small ones, both genuinely Java 9+**, and both fixing real annoyances:

- The Dock icon uses **`java.awt.Taskbar`** (Java 9). `Window.setIconImage` has existed
  since JDK 1.0 but macOS ignores it. On Java 1.4/5 the only route was the
  non-portable `-Xdock:icon=` launcher flag, which takes a filesystem path and so cannot
  reach an image inside a jar.
- Bringing the window to the front uses **`Desktop.requestForeground`** (Java 9,
  `APP_REQUEST_FOREGROUND`). `setVisible(true)` maps a window but does not activate the
  application on macOS, and `toFront()` only reorders windows within an app that is
  already frontmost.

**The substantial one** is `org.de.kiga3000.api` — an HTTP API over the card index, and
a web interface served alongside it. See `tools/run-api.sh`.

```
GET    /                     the web interface
GET    /api/health           GET /api/cards
GET    /api/cards/{id}       GET /api/groups/{n}/cards
POST   /api/cards            create
PUT    /api/cards/{id}       replace the writable fields
DELETE /api/cards/{id}       delete
```

Why this and not, say, packaging the app as a native bundle with `jpackage` (also
Java 14+, also tempting): Swing does not run on mobile, so any path to a phone client
requires the data to be reachable over HTTP first, with the desktop client and the phone
talking to the same service. `jpackage` is desktop polish; this is a prerequisite for
somewhere to go next.

What makes it new-stack rather than merely new code:

- **Virtual threads (Java 21)** — `Executors.newVirtualThreadPerTaskExecutor()` as the
  server's executor. Each request gets its own thread, so blocking JDBC inside a handler
  is fine. On Java 8, thread-per-request over a blocking database meant a bounded
  platform-thread pool and, done properly, an asynchronous rewrite — far more than this
  feature is worth. A test fires 200 concurrent requests.
- **Records (Java 16)** for the response projection.
- **Pattern matching for `switch` (Java 21)** for routing.
- **Text blocks (Java 15)** for the JSON templates.

### Limits, deliberately

This is data about children, so the constraints are the interesting part.

The API was read-only when it was first written, and the protection was simply that
nothing could be written. Writes exist now, so that argument no longer applies and had
to be replaced with a different one:

- **The writable field set equals the readable field set.** `CardSummary` exposes id,
  group, forename, surname, date of birth and entry date — what a group list would print
  on paper anyway. `CardDraft` accepts the same five (id being server-assigned) and
  nothing else. Religion, nationality, vaccination history, illnesses, health notes,
  doctor, health insurer, contacts, addresses and free-text fields — special-category
  personal data under GDPR Article 9 — can neither be read nor written over HTTP. A
  write API whose input accepted the whole entity would let an unauthenticated caller
  fill in a child's health record even though it could never read one back, which is
  worse rather than better.

  Two tests hold that line: one asserts each of those field names is absent from a
  response, another asserts a body naming one is **refused**, and a third asserts the
  two records' component lists are still identical. Widening either half alone breaks
  the build.
- **Unknown fields are refused, not ignored.** A body containing `religion` gets a 400
  naming the field. Silently dropping it would leave the caller believing a health note
  had been stored, which is the worse failure.
- **`PATCH` is deliberately absent.** A partial update of a five-field projection buys
  nothing, and the ambiguity over whether an omitted field means "leave it alone" or
  "clear it" is exactly how a date of birth gets quietly erased. Unsupported methods
  return 405 with an `Allow` header, not a silent 404.
- **Loopback only.** Bound to `127.0.0.1`, never `0.0.0.0`; verified that a request to
  the host's LAN address is refused. With no authentication, this is what makes the
  write endpoints defensible, and it applies to the web interface too.
- **Dates are validated at the boundary, then normalised.** Not politeness:
  `DateStringConverter` maps an unparseable date to SQL `NULL` rather than failing a
  flush, so without a check here a typo in a date of birth would be accepted and stored
  as "no date". Both `dd.MM.yyyy` and ISO are accepted; only `dd.MM.yyyy` is ever
  stored, because the entity's contract says its date attributes are in display format.
- **Not started by the desktop application.** It has its own `main`, so running the Swing
  client never opens a socket.

### The web interface

`GET /` serves a small client for the same six fields: a sortable list, a group filter,
a name search, and create/edit/delete. Plain HTML, CSS and vanilla JavaScript, packaged
inside the jar — a framework and a build step in front of six fields would have cost
more than the application it is attached to.

Two details that are not arbitrary:

- **Card data reaches the page only through `textContent`, never `innerHTML`.** The
  mirror of `Json.string()` on the server: the names are user-entered German text and the
  fixtures deliberately include one containing a quote and a backslash. Building rows by
  string concatenation would turn that fixture into an injection.
- **The page states what it withholds.** Someone looking at a form with five fields would
  otherwise reasonably assume the rest of the card was missing rather than deliberately
  out of reach. A test asserts the GDPR note and the loopback limit are both in the page.

### Seeding through the API

`postman/collections/Card Seeding` inserts additional children over HTTP, one per
iteration of `postman/datafiles/new-cards.json`:

```bash
./tools/run-api.sh &
postman collection run "postman/collections/Card Seeding" \
  -d postman/datafiles/new-cards.json
```

The point is that `db/seed.sql` arrives through SQL and therefore never exercises the
write path at all. Seeding over HTTP means every seeded card has been through JSON
parsing, field-name validation, date normalisation and the column widths — so a passing
seed run is also evidence the endpoint works, and a schema change that breaks writes
breaks seeding loudly. The data file includes umlauts, an eszett, a Turkish dotless i
and a French accent, because the source files are declared ISO-8859-1 while the API
promises UTF-8, and names are where that gets tested. All of them are invented.

No JSON library: Jackson would pull a dependency tree larger than the application, for
six fields of two types. The hand-written writer escapes quotes, backslashes and control
characters — which matters rather than being boilerplate, since the data is German and
the name fields are user-entered.

---

## The experiment was repeated four more times

This repository holds experiments 1 and 2 of five. The method was later applied to larger and
cleaner corpora, and the cross-experiment picture is collected in
[`jonico/ccf-modernized-summary`](https://github.com/jonico/ccf-modernized-summary) -
see [`data/experiment-index.md`](https://github.com/jonico/ccf-modernized-summary/blob/main/data/experiment-index.md).

| # | Corpus | Token penalty for using Moderne |
|---|---|---:|
| 1 | kiga3000, Java 8→21 + JDBC→JPA (**here**) | +118% |
| 2 | kiga3000, Java 21→25 + JUnit 6 (**here**) | +94% |
| 3 | CCF, 152,339 LOC, Spring 3→6 / Hibernate 3→6 | **+20%** |
| 4 | rock_paper_scissors, 1,824 LOC, clean-corpus control | +124% |
| 5 | Yoda conditions, 4,091 sites, no recipe exists | +418% |

**On migration tasks the penalty falls monotonically with corpus size**, which supports the
"recipes pay off with more files" hypothesis in direction - though the crossover is not reached at
152,000 lines. Experiment 4 was run specifically to test whether CCF's difficulty explained the
result; it is the cleanest corpus of the five and produced the worst migration penalty, so the
answer is no.

This repository also carries `yoda-arm-a` and `yoda-arm-b` branches from experiment 5, which
convert its 61 eligible `if` comparisons to Yoda style with all 83 tests still passing. They are
branches on purpose - Yoda conditions are a stylistic experiment, not something to inherit on
`main`. The recipe itself lives in
[`jonico/yoda-reloaded`](https://github.com/jonico/yoda-reloaded).

## Upstream contributions

### Round 1

Seven candidate findings came out of Round 1. After checking each against the existing
issue trackers, **one issue and two comments** were filed — the other four were dropped,
which is the more useful outcome to record:

| Finding | Outcome |
|---|---|
| `JUnit4to5Migration` removes `junit:junit` without adding Jupiter | **Filed** — [rewrite-testing-frameworks#1113](https://github.com/openrewrite/rewrite-testing-frameworks/issues/1113) |
| `MigrateJUnitTestCase` leaves a `TestCase(String)` constructor, so the class is unrunnable under JUnit 5 | **Already reported** — added a minimal repro to [#1108](https://github.com/openrewrite/rewrite-testing-frameworks/issues/1108) |
| No recipe for the deprecated `Window/Dialog.show()` → `setVisible(true)` | **Deliberately declined upstream in 2021** — added a demand datapoint and a working workaround to the closed [rewrite-migrate-java#11](https://github.com/openrewrite/rewrite-migrate-java/issues/11) rather than reopening a scope decision |
| Docs pin versions that 404 on Maven Central | **Dropped** — not a bug. OpenRewrite publishes to the authenticated Code Genome Project repository; Central lags it. The docs are consistent with the repository they document |
| Two recipes sharing a name with different option names | **Dropped** — could not reproduce on the current release |
| `AddLiteralMethodArgument` lacks `matchOverrides` | Noted inside the `show()` issue rather than filed separately |

The filed issue is worth reading as a method note. The first draft blamed "JUnit 3
projects", which a maintainer could reasonably have closed as out of scope, since the
recipe is called *JUnit4to5*. Re-testing with `junit:junit:4.13.2` showed a genuine
**JUnit 4** project fails identically whenever a test extends
`junit.framework.TestCase` — classes that ship *inside* the JUnit 4 jar. The issue was
corrected before a maintainer saw it. The root cause is that
`AddJupiterDependencies` guards on `onlyIfUsing: org.junit..*`, which
`junit.framework.*` never matches, while the sibling `RemoveDependency` has no such
guard.

### Round 2

Round 2 produced four more candidates, all from places where the recipe path hit a
structural wall that Arm B's hand investigation then explained. Verifying each against the
recipe jars and against a minimal reproduction project left **two filed and two dropped**:

| Finding | Outcome |
|---|---|
| `MigrateDialect` silently no-ops on dialects set in `persistence.xml` / `hibernate.cfg.xml`, though it does migrate `.properties` and `.yml` | **Filed** — [rewrite-hibernate#96](https://github.com/openrewrite/rewrite-hibernate/issues/96) |
| `MigrateToHibernate70/71` installs Hibernate 7 (Jakarta Persistence 3.2) but leaves `persistence.xml` declaring `version="3.0"` | **Filed** — [rewrite-hibernate#97](https://github.com/openrewrite/rewrite-hibernate/issues/97) |
| `HibernateValidator_9_1` does not migrate the EL implementation | **Dropped — the claim was wrong.** See below |
| `UpgradeToJava25` bundles `ReplaceSystemOutWithIOPrint`, rewriting every `println` during a version upgrade | **Not filed.** Real, reproducible, and unwanted here, but it compiles and emits byte-identical output, so it is a matter of recipe scoping rather than a defect. Drafted and held |

The validator finding is the instructive one, because it survived reading the recipe YAML
and died in reproduction. The recipe really does only bump `hibernate-validator` to `9.1.x`
without touching the EL implementation, and Validator 9.1 really does declare
`jakarta.el-api` as `provided`/`optional` — both facts check out. The inference drawn from
them did not. The first reproduction paired Validator 8 with `org.glassfish:jakarta.el:4.0.2`
and observed a broken message, which looked like confirmation but was an artefact: that
pairing is already broken *before* migrating, so it demonstrated nothing. Rebuilt with a
baseline that genuinely worked (Validator 8.0.2 + `expressly` 5.0.0, EL interpolating),
applying the recipe yields Validator 9.1.3 with the EL implementation untouched — and
interpolation still works. It also works with the superseded RI. There is no failure to
report.

Two smaller traps worth recording, both of which would have produced a wrong report:

- **`{min}`/`{max}` are message parameters, not EL**, so they interpolate with no EL
  implementation present and cannot be used to detect one. Detecting it needs a real
  expression such as `${validatedValue}`.
- **Hibernate Validator disables method-call EL by default** since 6.2, so
  `${validatedValue.length()}` stays uninterpolated on a perfectly healthy classpath.

Conversely, the strongest evidence in the two filed issues came from the recipes' own test
fixtures rather than from this codebase: `META-INF/rewrite/examples.yml` shows the single
documented example for `MigrateDialect` is an `application.yml` case
(`MigrateDialectTest#replacesMySQL5DialectInYaml`), which establishes that non-Java
configuration is in scope by design and makes the XML gap an omission rather than a
boundary.

Also verified along the way, since it is easy to get wrong: the Code Genome Project
repository answers `401` to unauthenticated requests for *every* path, including
deliberately non-existent versions, so an unauthenticated check can never distinguish
"published" from "absent". With a token, `rewrite-maven-plugin` downloads but the large
recipe modules return `403` at every version — including ones freely available on
Central. That is an entitlement boundary, not a versioning problem.

---

## Repository layout

```
├── run.sh                     start the application, finding a JDK itself
├── pom.xml                    Maven build; targets Java 25, shaded runnable jar
├── rewrite.yml                declarative OpenRewrite composites used in the migrations
├── db/
│   ├── schema.sql             current schema: nullable dates, InnoDB, utf8mb4
│   ├── seed.sql               17 demo records (see its header on provenance)
│   ├── migrate-01-nullable-dates.sql   for a database created before 2026
│   └── datenbanktabelle.original.sql   the 2006 DDL, unchanged, for reference
├── legacy/                    the recovered 2004-2006 originals, untouched
│   ├── README.md              how they were recovered, and what is in them
│   ├── rb_1_3/                cvs export -r RB_1_3, verbatim
│   └── trunk-head-src-lib/    the vendored dependency binaries the jars come from
├── lib/repo/                  project-local Maven repo for the 2004 Liquid L&F
├── src/main/java/org/de/kiga3000/
│   ├── api/                   HTTP API (virtual threads, records)
│   ├── control/               MVC controllers
│   ├── conversion/            date conversion
│   ├── data/                  the JPA entity and its date converter
│   ├── database/              JPA repository + the JDBC queries that stayed
│   ├── views/                 Swing
│   └── KigaResources.java     classpath resource resolution
├── src/main/resources/web/    the web interface, served from inside the jar
└── tools/
    ├── run-api.sh             start the HTTP API and the web interface
    ├── test-printing.sh       manual print check; takes a "legacy" argument
    ├── audit-delegating-accessors.py   finds crossed delegating getters/setters
    └── audit-view-wiring.py            finds crossed Swing component wiring
```

## Reproducing

```bash
# build and test  (107 unit tests, no database or display needed)
mvn clean verify

# start the application
./run.sh

# start the API and the web interface
./tools/run-api.sh 18080
open http://127.0.0.1:18080/
curl -s http://127.0.0.1:18080/api/cards | head -c 300

# exercise the write endpoints, and seed more children through them
postman collection run "postman/collections/KiGa 3000"
postman collection run "postman/collections/Card Seeding" \
  -d postman/datafiles/new-cards.json

# checks that need a live database (named *Check, so surefire skips them)
mvn -q test-compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
CP="target/classes:target/test-classes:$(cat target/cp.txt)"
java -cp "$CP" org.de.kiga3000.database.JpaEquivalenceCheck
java -cp "$CP" org.de.kiga3000.conversion.DateConversionEndToEndCheck 1

# the print check needs a human and a print dialog; "legacy" reproduces the 2006 crash
./tools/test-printing.sh
./tools/test-printing.sh legacy

# audit for the class of bug that wrote sonstiges into the sorgeperson column
python3 tools/audit-delegating-accessors.py \
  src/main/java/org/de/kiga3000/views/KarteikartenDialogSwingImpl.java
```

## Known issues and next steps

Recorded rather than quietly left:

- **A hand-rolled `ConnectionPool` sits beside HikariCP.** 253 lines with a
  `Timer`-based `ConnectionKiller`, now serving only the JDBC search queries. Replacing
  it is the obvious next cleanup.
- **`autoReconnect=true`** remains in the JDBC URL — a Connector/J 3.x relic the current
  driver documents as unreliable. It belongs with the pool replacement.
- **Credentials in source.** `Ressourcen.java` hardcodes the JDBC URL, user and
  password, as it has since 2004. The database it points at holds only the demo seed,
  but this must not survive contact with real data.
- **The half-finished `newcard` model** (`data/`, `interfaces/newcard/`) is inherited
  from the abandoned branch and barely wired in. It should be either finished or
  deleted.
- **Raw types** throughout — `Comparable`, `Comparator`, `LinkedList`, `HashMap`.
  Generifying is a large diff with real behaviour risk, so it has not been attempted.
- **`KigaCardNewTest` compiles but never runs.** It opens a Swing window, needs a live
  database and calls `System.exit(1)`, which would kill the surefire JVM.
- **The API has no authentication, and it now accepts writes.** The narrow projection
  and the loopback bind are what make that defensible, and both are stopgaps rather than
  solutions. Anything beyond the current summary — a wider projection, a non-loopback
  bind, or a second client — needs an auth story first, and the write endpoints raise
  the stakes: read-only meant the worst case was disclosure, and now it is modification.
- **The deployed cloud mocks still describe the read-only contract.** `postman/mocks/`
  and `postman/collections/Cloud Mock Contract` were generated from the spec when the API
  refused every write, so they answer `405` with `Allow: GET` to a `POST`. The spec has
  moved; those artefacts have not. Regenerating them is straightforward, but redeploying
  a mock is an outward-facing change, so it was left as a deliberate decision rather than
  folded into this one.
- **`HEAD` returns 405.** Supported wherever `GET` is, by the HTTP spec; here it falls
  through to the unsupported-method branch. Nothing in the repository depends on it, but
  it is a wart rather than a decision.
- **The web interface has no tests beyond the server side.** The handler, the asset
  content types, the traversal refusal and the GDPR note are all asserted; the
  JavaScript that drives the forms is not. Exercising it needs a browser in the loop,
  which is the same shape of problem as the print check.

## License

GPL-2.0, inherited from the original project. `LICENSE` is the GPL-2.0 text.

The vendored Liquid Look and Feel (`lib/repo/com/birosoft/`) and the original MySQL
Connector/J 3.0.14 are third-party binaries recovered from the CVS repository, under
their own licenses.

---

*The 2006 code in `legacy/` is unmodified. Everything else was rebuilt in 2026 on top of
it, and every claim in this document was verified by running something rather than by
reading the code and assuming.*
