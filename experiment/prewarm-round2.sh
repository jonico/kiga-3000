#!/bin/bash
# Pre-warm a round-2 arm's Maven repository.
#
# Identical warm set for both arms, deliberately including artifacts only one arm is
# likely to use, so the set itself leaks no hint about which approach to take. Every
# version here was checked to exist as a STABLE release on Maven Central first - the
# round-1 prewarm silently failed because it pinned rewrite-maven-plugin 6.47.0, which
# is on the Code Genome Project repo but not on Central.
set -uo pipefail
M2="$1"; SCRATCH="$2"
export JAVA_HOME=/opt/homebrew/opt/openjdk@25
MVN=(mvn -B -q -Dmaven.repo.local="$M2")

echo "### warming $M2"
( cd "$SCRATCH" && "${MVN[@]}" clean verify ) >/dev/null 2>&1
echo "  [1/3] current build (Java 21 target, Hibernate 5.6) "

# round-2 destination artifacts
for A in \
  "org.hibernate.orm:hibernate-core:7.4.6.Final" \
  "org.hibernate.orm:hibernate-hikaricp:7.4.6.Final" \
  "jakarta.persistence:jakarta.persistence-api:3.2.0" \
  "org.hibernate.validator:hibernate-validator:9.1.3.Final" \
  "jakarta.validation:jakarta.validation-api:3.1.1" \
  "org.glassfish.expressly:expressly:6.0.0" \
  "com.zaxxer:HikariCP:7.1.0" \
  "org.slf4j:slf4j-api:2.0.18" \
  "org.slf4j:slf4j-jdk14:2.0.18" ; do
  ( cd "$SCRATCH" && "${MVN[@]}" dependency:get -Dartifact="$A" ) >/dev/null 2>&1
done
echo "  [2/3] round-2 target libraries"

# OpenRewrite plugin and the recipe modules a Hibernate/Jakarta migration needs
( cd "$SCRATCH" && "${MVN[@]}" \
    org.openrewrite.maven:rewrite-maven-plugin:6.46.1:dryRun \
    -Drewrite.recipeArtifactCoordinates=org.openrewrite.recipe:rewrite-hibernate:2.25.0,org.openrewrite.recipe:rewrite-migrate-java:3.42.1,org.openrewrite.recipe:rewrite-testing-frameworks:3.44.0,org.openrewrite.recipe:rewrite-static-analysis:2.41.0,org.openrewrite.recipe:rewrite-java-dependencies:1.60.2,org.openrewrite.recipe:rewrite-logging-frameworks:3.32.0 \
    -Drewrite.activeRecipes=org.openrewrite.java.format.AutoFormat ) >/dev/null 2>&1
echo "  [3/3] rewrite plugin 6.46.1 + 6 recipe modules"

echo "### $M2: $(du -sh "$M2" | cut -f1), $(find "$M2" -name '*.jar' | wc -l | tr -d ' ') jars"
