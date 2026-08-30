#!/bin/bash
# Pre-warm a per-arm Maven local repository.
#
# Both arms get the IDENTICAL warm set, deliberately including artifacts only one
# arm is likely to use (OpenRewrite plugin + recipe modules for arm A, modern
# Connector/J + JUnit 5 for arm B). That way:
#   * neither arm pays artifact download time inside its measured window, and
#   * the warm set itself leaks no hint about which approach to take, because it
#     is the same for both.
# This is "installation", which the experiment excludes from the comparison.
set -uo pipefail

M2="$1"                  # e.g. ~/kiga3000-work/m2-arm-a
SCRATCH="$2"             # throwaway copy of the baseline project
export JAVA_HOME=/opt/homebrew/opt/openjdk@21
MVN=(mvn -B -q -Dmaven.repo.local="$M2")

echo "### warming $M2"

# 1. everything the baseline build itself needs (compiler/surefire/jar/junit3/vendored)
( cd "$SCRATCH" && "${MVN[@]}" clean package ) >/dev/null 2>&1
echo "  [1/4] baseline build plugins + deps"

# 2. OpenRewrite: plugin, and the recipe modules a Java upgrade would plausibly need
( cd "$SCRATCH" && "${MVN[@]}" \
    org.openrewrite.maven:rewrite-maven-plugin:6.47.0:dryRun \
    -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-migrate-java:RELEASE,org.openrewrite.recipe:rewrite-testing-frameworks:RELEASE,org.openrewrite.recipe:rewrite-static-analysis:RELEASE \
    -Drewrite.activeRecipes=org.openrewrite.java.format.AutoFormat ) >/dev/null 2>&1
echo "  [2/4] rewrite-maven-plugin + recipe modules"

# 3. modern runtime/test dependencies either arm might land on
for A in \
  "com.mysql:mysql-connector-j:LATEST" \
  "org.junit.jupiter:junit-jupiter:LATEST" \
  "org.junit.jupiter:junit-jupiter-api:LATEST" \
  "org.junit.jupiter:junit-jupiter-engine:LATEST" \
  "org.junit.platform:junit-platform-launcher:LATEST" \
  "com.zaxxer:HikariCP:LATEST" ; do
  ( cd "$SCRATCH" && "${MVN[@]}" dependency:get -Dartifact="$A" ) >/dev/null 2>&1
done
echo "  [3/4] modern connector / JUnit 5 / pool candidates"

# 4. plugin versions a Java 21/25 build commonly pulls
for A in \
  "org.apache.maven.plugins:maven-compiler-plugin:3.14.0" \
  "org.apache.maven.plugins:maven-surefire-plugin:3.5.4" \
  "org.apache.maven.plugins:maven-jar-plugin:3.4.2" \
  "org.apache.maven.plugins:maven-enforcer-plugin:3.5.0" ; do
  ( cd "$SCRATCH" && "${MVN[@]}" dependency:get -Dartifact="$A:pom" ) >/dev/null 2>&1
done
echo "  [4/4] build plugins"

echo "### $M2 warmed: $(du -sh "$M2" | cut -f1), $(find "$M2" -name '*.jar' | wc -l | tr -d ' ') jars"
