#!/bin/bash
# Build and launch one arm's KiGa 3000 for manual testing.
#
#   ./launch-app.sh a      # the OpenRewrite arm
#   ./launch-app.sh b      # the hand-migrated arm
#
# This script never edits anything inside the arm's source tree. Everything it
# needs to change is injected from a scratch "runtime home" that is prepended to
# the classpath, so the arms stay exactly as their agents left them and remain
# byte-comparable.
#
# Why a runtime home is needed at all: the 2006 code resolves both its online-help
# page and its logging configuration by concatenating the config key
# `KiGaLoggingPath` with a relative path. That key ships EMPTY, so out of the box
# the app builds the URL "file:////titlepage/kiga.htm", fails, and greets you with
# a modal "no online help" error dialog before you can use it. Pointing the key at
# a directory that actually contains those resources is a launcher concern, not an
# application change.
#
# Second inherited defect handled here: KiGaLogging.properties names
# `formatter.LoggingFormatter` as its log formatter. That class does not exist
# anywhere in the project or in the original CVS repository, so java.util.logging
# would spew a ClassNotFoundException at startup. The copy in the runtime home
# substitutes the JDK's own XMLFormatter, which is what the config was evidently
# reaching for (the log pattern is *.xml).

set -euo pipefail

ARM="${1:?usage: launch-app.sh <a|b|repo|baseline>}"
W="$HOME/kiga3000-work"
case "$ARM" in
  a|b)      PROJECT="$W/arm-$ARM";  M2="$W/m2-arm-$ARM" ;;
  repo)     # the deliverable: arm A's migration plus the date fix
            PROJECT="$HOME/kiga3000-reloaded"; M2="$HOME/.m2/repository" ;;
  baseline) PROJECT="$W/baseline";  M2="$HOME/.m2/repository"
            # The 2006 baseline predates the driver fix, so it cannot reach MySQL
            # at all; it is launchable only to confirm the UI and the title picture.
            JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@17}" ;;
  *)        echo "unknown target: $ARM (expected a, b, repo or baseline)" >&2; exit 1 ;;
esac
RUNTIME="$W/experiment/runtime-$ARM"
JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@21}"
export JAVA_HOME

[ -d "$PROJECT" ] || { echo "no such arm: $PROJECT" >&2; exit 1; }

echo "==> building arm-$ARM with $($JAVA_HOME/bin/java -version 2>&1 | head -1)"
( cd "$PROJECT" && mvn -B -q -Dmaven.repo.local="$M2" -DskipTests package )

echo "==> resolving runtime classpath"
( cd "$PROJECT" && mvn -B -q -Dmaven.repo.local="$M2" \
    dependency:build-classpath -Dmdep.outputFile="$RUNTIME/.cp" -Dmdep.includeScope=runtime ) \
  2>/dev/null || {
    mkdir -p "$RUNTIME"
    ( cd "$PROJECT" && mvn -B -q -Dmaven.repo.local="$M2" \
        dependency:build-classpath -Dmdep.outputFile="$RUNTIME/.cp" -Dmdep.includeScope=runtime )
  }
DEPS="$(cat "$RUNTIME/.cp")"

echo "==> preparing runtime home at $RUNTIME"
rm -rf "$RUNTIME/titlepage" "$RUNTIME/org" "$RUNTIME/cp-override"
mkdir -p "$RUNTIME/logs" "$RUNTIME/org/de/kiga3000/properties" \
         "$RUNTIME/cp-override/org/de/kiga3000/properties"

# online help page + images, taken from whatever the arm actually built
cp -R "$PROJECT/target/classes/titlepage" "$RUNTIME/titlepage"

# logging config, with the non-existent formatter class replaced
sed 's#^java\.util\.logging\.FileHandler\.formatter *=.*#java.util.logging.FileHandler.formatter = java.util.logging.XMLFormatter#' \
    "$PROJECT/target/classes/org/de/kiga3000/properties/KiGaLogging.properties" \
    > "$RUNTIME/org/de/kiga3000/properties/KiGaLogging.properties"

# Config override, prepended to the classpath so it wins over target/classes
cat > "$RUNTIME/cp-override/org/de/kiga3000/properties/Config.properties" <<EOF
# Injected by launch-app.sh for manual testing - NOT part of the application.
# Points the app at a directory that really contains titlepage/ and the logging
# properties, so the online-help panel renders instead of raising an error dialog.
KiGaLoggingPath=$RUNTIME
EOF

echo "==> database state"
/opt/homebrew/opt/mysql/bin/mysql -u root -N -e \
  'SELECT CONCAT(COUNT(*), " rows in Kindergarten.Karteikarte") FROM Kindergarten.Karteikarte;' \
  || echo "    WARNING: could not reach MySQL - start it with: brew services start mysql"

# KIGA_DRY_RUN=1 prepares everything and verifies the resources the app will read,
# without opening a window. Used to confirm the title picture is reachable.
if [ "${KIGA_DRY_RUN:-0}" = "1" ]; then
  echo "==> DRY RUN: verifying what the app will load"
  HELP="$RUNTIME/titlepage/kiga.htm"
  printf "    %-46s %s\n" "titlepage/kiga.htm" \
    "$( [ -r "$HELP" ] && echo "readable ($(wc -c <"$HELP" | tr -d ' ') bytes)" || echo MISSING )"
  for IMG in $(grep -oE '(SRC|BACKGROUND)="[^"]+"' "$HELP" 2>/dev/null | sed 's/.*"\(.*\)"/\1/'); do
    printf "    %-46s %s\n" "referenced image: $IMG" \
      "$( [ -r "$RUNTIME/titlepage/$IMG" ] && echo "readable ($(wc -c <"$RUNTIME/titlepage/$IMG" | tr -d ' ') bytes)" || echo MISSING )"
  done
  printf "    %-46s %s\n" "URL the app builds" "file:///$RUNTIME/titlepage/kiga.htm"
  printf "    %-46s %s\n" "logging config" \
    "$( [ -r "$RUNTIME/org/de/kiga3000/properties/KiGaLogging.properties" ] && echo readable || echo MISSING )"
  printf "    %-46s %s\n" "log formatter" \
    "$(grep -oE 'FileHandler\.formatter *=.*' "$RUNTIME/org/de/kiga3000/properties/KiGaLogging.properties" | sed 's/.*= *//')"
  printf "    %-46s %s\n" "Config KiGaLoggingPath" \
    "$(grep -oE '^KiGaLoggingPath=.*' "$RUNTIME/cp-override/org/de/kiga3000/properties/Config.properties" | cut -d= -f2-)"
  echo "==> dry run complete; rerun without KIGA_DRY_RUN to launch"
  exit 0
fi

echo "==> launching arm-$ARM (close the window to exit)"
echo "    logs: $RUNTIME/logs/"
cd "$RUNTIME"
exec "$JAVA_HOME/bin/java" \
  -cp "$RUNTIME/cp-override:$PROJECT/target/classes:$DEPS" \
  KiGa3000Main
