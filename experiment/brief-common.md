# Task: modernize KiGa 3000 from Java 8 to Java 21

You are working ONLY inside your own project directory, given below as `$PROJECT`.
Everything you need is already installed. You have no network restrictions but you
should not need to install any software.

## What this code is

KiGa 3000 is a Swing + JDBC desktop application that manages card-index records
("Karteikarte") for a German Kindergarten. The source is the `RB_1_3` branch of the
original SourceForge CVS repository, frozen on 2006-02-19. It is 73 Java files /
~9,100 lines in `org.de.kiga3000.*`, plus `KiGa3000Main` in the default package.
Comments, identifiers and UI strings are in German. Source files are ISO-8859-1.

The project currently builds green, and its two legacy tests pass, exactly as handed
to you. Do not start by assuming something is broken.

## Environment

- `JAVA_HOME_8_ERA=/opt/homebrew/opt/openjdk@17`  (what the baseline builds with today)
- `JAVA_HOME_TARGET=/opt/homebrew/opt/openjdk@21` (what you must end up building with)
- Maven: `mvn`, and you MUST pass `-Dmaven.repo.local=$M2` on every invocation
  (value given below). This repository is pre-populated; do not use the default `~/.m2`.
- MySQL is running on `localhost:3306`, version **26.7.0**.
  - database `Kindergarten`, user `KiGa`, password `Kiga3000`
  - table `Karteikarte` exists and is seeded with 16 demo rows (`db/seed.sql`).
    Leave that data intact, or restore it with `mysql -u root < db/seed.sql` if a
    test of yours disturbs it.
  - client binary: `/opt/homebrew/opt/mysql/bin/mysql -u root`
  - The server offers ONLY `sha256_password` and `caching_sha2_password`.
    `mysql_native_password` does not exist on this server.
  - `@@GLOBAL.sql_mode` includes `STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE`.
- `git` is available and `$PROJECT` is already a git repo with the untouched baseline
  as its single commit. Commit your work locally as you go. Never add a remote and
  never push.

## Definition of done

All of the following must hold, and you must demonstrate each one with command output.

1. **Builds and tests pass on JDK 21.**
   `JAVA_HOME=$JAVA_HOME_TARGET mvn -B -Dmaven.repo.local=$M2 clean verify` succeeds.

2. **The build actually targets Java 21**, not 1.8. `maven.compiler.source/target 1.8`
   must be gone in favour of a Java 21 setting.

3. **No deprecated-for-removal API left.** The baseline compile emits at least these:
   - `new Integer(int)` in `views/Kiga3000MainPanel.java`
   - `new Byte(byte)` in `views/KarteiKarteSwingImpl.java` (two sites)
   Replace these and any others the JDK 21 compiler flags, using mechanical,
   behaviour-preserving equivalents. Build with `-Xlint:deprecation,removal` at least
   once and show the result.

4. **Tests migrated to JUnit 5.** The two tests currently extend
   `junit.framework.TestCase` (JUnit 3):
   - `org/de/kiga3000/conversion/DateConversionTest.java` — runs today, must still run and pass
   - `org/de/kiga3000/views/KigaCard/KigaCardNewTest.java` — must still COMPILE, but stays
     excluded from execution (it opens a Swing window, needs a live DB, and calls
     `System.exit(1)`, which would kill the surefire JVM). Keep it excluded.
   The `junit:junit:3.8.2` dependency must be gone.

5. **The application can actually reach the database.** This is currently IMPOSSIBLE:
   the vendored driver is MySQL Connector/J 3.0.14 from 2004, and against this server
   it fails inside `com.mysql.jdbc.MysqlIO.secureAuth411` with *"Client does not support
   authentication protocol requested by server"*. Replace it with a current MySQL driver
   and adjust the connection code/URL as needed. The JDBC URL and driver class name are
   hardcoded in `org/de/kiga3000/database/Ressourcen.java`.
   Prove success by running a program that, using the project's own
   `Ressourcen`/`ConnectionPool` code path, opens a connection, runs
   `SELECT VERSION()`, inserts one row into `Karteikarte`, reads it back and deletes it.
   Show its output. Note that the app's date handling relies on the literal string
   `"0000-00-00"`, so think about how the modern driver treats zero dates.

6. **`db/schema.sql` remains applyable** to this server:
   `/opt/homebrew/opt/mysql/bin/mysql -u root < db/schema.sql` must succeed.
   If your driver change requires schema changes, make them and keep it applyable.

7. **No behaviour or UI changes.** This is a modernization, not a redesign. Do not
   rename domain classes, do not restructure packages, do not "improve" the German
   UI text, do not add features, do not reformat files wholesale. Keep the diff
   proportional to the task.

8. **Do not modify the vendored Liquid Look-and-Feel dependency**
   (`com.birosoft:liquidlnf:0.2.8` in `lib/repo/`). It is a binary from 2004 with no
   source and no replacement; the app calls `LiquidLookAndFeel.setPanelTransparency`
   and `setLiquidDecorations` and must keep working against that exact jar.

## Scope boundaries

- Stay inside `$PROJECT`. Do not read, list or copy from any sibling directory under
  `~/kiga3000-work` — in particular do not look for another copy of this project.
  Anything you need is inside `$PROJECT`.
- Do not add a git remote, do not push, do not open a PR.
- Do not install software.

## What to report back

Your final message is consumed by a program, not a human. Return a concise report:

- `outcome`: `complete` or `incomplete`, plus one sentence.
- `verification`: for each of the 7 numbered criteria, the exact command you ran and
  whether it passed.
- `changes`: a list of what you changed, grouped by concern (build config, language
  level, deprecated APIs, tests, JDBC/database). For each, say how you did it.
- `diffstat`: output of `git diff --stat <baseline-commit>..HEAD`.
- `files_touched`: count of files changed.
- `surprises`: anything that did not work as expected, and what you did about it.
- `manual_effort`: your honest assessment of which changes were mechanical versus
  which required real judgement.
