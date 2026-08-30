REPO: openrewrite/rewrite-migrate-java
TITLE: UpgradeToJava25 bundles ReplaceSystemOutWithIOPrint, an opinionated API change, into a version upgrade

## What version of OpenRewrite are you using?

rewrite-maven-plugin 6.46.1, rewrite-migrate-java 3.42.1.

## What problem are you trying to solve?

`org.openrewrite.java.migrate.UpgradeToJava25` includes
`org.openrewrite.java.migrate.io.ReplaceSystemOutWithIOPrint`
(`META-INF/rewrite/java-version-25.yml`). Unlike the rest of the composite, that step is not
required by the upgrade: `System.out.println` compiles and behaves identically on Java 25.
It is a preference for a new API, applied to every `println` in the project.

In https://github.com/openrewrite/rewrite-maven-plugin/issues/575 recipe exclusions were
declined, with the note:

> Especially any issues that might arise from not having the option to exclude any particular
> recipe we would very much like to hear about and resolve quickly.

This is one of those cases, so I am reporting it rather than asking for exclusions.

## Reproduction

```java
package com.example;

import java.util.List;

public class ReportTool {
    public static void main(String[] args) {
        List<String> rows = List.of("alpha", "beta");
        System.out.println("name,length");
        for (String row : rows) {
            System.out.println(row + "," + row.length());
        }
        System.out.flush();
        System.err.println("done");
    }
}
```

```
mvn -U org.openrewrite.maven:rewrite-maven-plugin:6.46.1:run \
  -Drewrite.activeRecipes=org.openrewrite.java.migrate.UpgradeToJava25 \
  -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:3.42.1
```

Result:

```java
        IO.println("name,length");
        for (String row : rows) {
            IO.println(row + "," + row.length());
        }
        System.out.flush();
        System.err.println("done");
```

This compiles on JDK 25 and produces byte-identical output, so it is not a correctness bug.
The concerns are:

1. **It is not part of upgrading.** Every other step in `UpgradeToJava25` addresses something
   that changed. This one changes working code to a different API by preference. A user who
   wants Java 25 has not thereby asked to adopt `java.lang.IO`.

2. **It leaves a method less coherent than it found it.** `IO.println` now sits beside
   `System.out.flush()` and `System.err.println()` in the same block, because the recipe
   maps only `System.out.print`/`println`. Two idioms for one stream is worse than one.
   `java.lang.IO`'s own javadoc describes it as "convenient access to `System.in` and
   `System.out` for line-oriented input and output", with an API note that the expected use
   case is applications that "will not mix these calls with other techniques" — which is
   what this rewrite produces.

3. **Churn scales with the codebase and is hard to review.** It rewrote every `println` in
   the project. In my case that included command-line tools whose stdout is their contract
   and whose output is the acceptance evidence, mixed into the same commit as the real
   Java 25 changes.

4. **No way to take the upgrade without it.** Per #575 the only route is to rebuild the
   composite from the sub-recipes one wants, which then has to be re-checked against every
   release for new sub-recipes. That is what I ended up doing.

## Describe the solution you'd like

Move `ReplaceSystemOutWithIOPrint` out of `UpgradeToJava25` and into an opt-in recipe (a
Java 25 "best practices" or "adopt new APIs" composite, alongside the existing
`org.openrewrite.java.migrate.UpgradeToJava25` rather than inside it). Users who want it
would still get it in one line; users upgrading would get only what the upgrade requires.

If it stays, it may be worth also mapping `System.out.print`/`printf`/`flush` and
`System.err.println` so the result is at least internally consistent.

## Have you considered any alternatives or workarounds?

Rebuilding `UpgradeToJava25` from its sub-recipes in a local `rewrite.yml`, which is what I
did. It works but has to be maintained against upstream changes.

## Are you interested in contributing this feature to OpenRewrite?

Yes — happy to open a PR moving the recipe into an opt-in composite if you agree with the
direction.
