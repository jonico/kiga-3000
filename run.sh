#!/bin/bash
# Start KiGa 3000.
#
#   ./run.sh              build if needed, then start
#   ./run.sh --check      report which JDK would be used, and exit
#
# Why this script exists rather than just "java -jar target/Kiga3000.jar":
# Homebrew's openjdk formulae are keg-only, so they are deliberately NOT linked onto
# the PATH. On a Mac with no system JVM, plain `java` then hits Apple's stub, which
# answers "Unable to locate a Java Runtime" even though a perfectly good JDK is
# installed. This script finds one.
#
# Search order:
#   1. $JAVA_HOME, if it points at a usable JDK
#   2. /usr/libexec/java_home (the macOS registry of installed JDKs)
#   3. `java` already on the PATH
#   4. the usual Homebrew keg-only locations, newest first
set -euo pipefail

cd "$(dirname "$0")"

MINIMUM_MAJOR=25

find_java() {
  if [ -n "${JAVA_HOME:-}" ] && [ -x "$JAVA_HOME/bin/java" ]; then
    echo "$JAVA_HOME/bin/java"; return 0
  fi
  if [ -x /usr/libexec/java_home ]; then
    local home
    if home=$(/usr/libexec/java_home -v "$MINIMUM_MAJOR+" 2>/dev/null); then
      [ -x "$home/bin/java" ] && { echo "$home/bin/java"; return 0; }
    fi
  fi
  if command -v java >/dev/null 2>&1; then
    # Apple's stub is on the PATH even with no JDK, so make sure it actually runs.
    if java -version >/dev/null 2>&1; then
      command -v java; return 0
    fi
  fi
  # openjdk@25 first, not newest-first: that is the release the jar is compiled for
  # and the version the 2004 Liquid Look-and-Feel has actually been verified against.
  # Newer JDKs are a fallback, so the application still starts on a machine that only
  # has one of those. Java 21 is no longer listed: the jar is Java 25 bytecode
  # (class file major version 69) and a 21 runtime rejects it outright.
  local candidate
  for candidate in \
      /opt/homebrew/opt/openjdk@25/bin/java \
      /opt/homebrew/opt/openjdk/bin/java \
      /usr/local/opt/openjdk@25/bin/java \
      /usr/local/opt/openjdk/bin/java ; do
    [ -x "$candidate" ] && { echo "$candidate"; return 0; }
  done
  return 1
}

if ! JAVA=$(find_java); then
  cat >&2 <<'EOF'
No suitable Java runtime found (Java 25 or newer required).

On macOS with Homebrew:
    brew install openjdk@25

Homebrew keeps that keg-only, so either re-run this script (it looks in the
Homebrew location directly), or expose it yourself:
    export PATH="/opt/homebrew/opt/openjdk@25/bin:$PATH"
EOF
  exit 1
fi

VERSION=$("$JAVA" -version 2>&1 | head -1)

if [ "${1:-}" = "--check" ]; then
  echo "java    : $JAVA"
  echo "version : $VERSION"
  echo "jar     : $(pwd)/target/Kiga3000.jar"
  [ -f target/Kiga3000.jar ] && echo "          (present)" || echo "          (not built yet)"
  exit 0
fi

if [ ! -f target/Kiga3000.jar ]; then
  echo "==> target/Kiga3000.jar missing, building"
  JAVA_HOME="$(dirname "$(dirname "$JAVA")")" mvn -B -q -DskipTests package
fi

echo "==> $VERSION"
echo "==> starting KiGa 3000 (logs in ~/.kiga3000/logs)"
exec "$JAVA" -jar target/Kiga3000.jar "$@"
