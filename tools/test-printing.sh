#!/bin/bash
# Manual check for the macOS printing crash.
#
#   ./tools/test-printing.sh            # the fixed path that ships today
#   ./tools/test-printing.sh legacy     # the 2006 path, expected to abort the JVM
#
# A window opens, then the macOS page-setup and print dialogs. Picking
# "Save as PDF" as the destination is enough - no paper required.
#
# Why this is a manual script and not a unit test: the crash lives in the native
# macOS printer surface (libawt_lwawt / OSXOffScreenSurfaceData). A headless
# PostScript StreamPrintService is pure Java and completes happily on both code
# paths, so it cannot detect the bug. Reaching it requires a real PrinterJob and
# the native print pipeline, which means a GUI and a human.
#
# You can equally just run the application itself and print from there; this
# harness exists mainly so the two code paths can be compared side by side.
set -euo pipefail

cd "$(dirname "$0")/.."
MODE="${1:-fixed}"
# The jar is Java 25 bytecode (class file major 69); a JDK 21 runtime rejects it.
JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@25}"
export JAVA_HOME

echo "==> building"
mvn -B -q -DskipTests package

echo "==> resolving classpath"
mvn -B -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt -Dmdep.includeScope=runtime

CP="target/classes:$(cat target/cp.txt)"

if [ "$MODE" = "legacy" ]; then
  cat <<'WARN'

  ############################################################
  #  LEGACY MODE                                             #
  #                                                          #
  #  This deliberately runs the 2006 code path. On macOS it   #
  #  is expected to abort the JVM with                        #
  #      Bad JNI lookup sData                                #
  #      NSGenericException: JNI Lookup Exception             #
  #  That abort is the bug being demonstrated, not a fault    #
  #  in this script.                                         #
  ############################################################

WARN
fi

echo "==> launching print check (mode=$MODE)"
exec "$JAVA_HOME/bin/java" -cp "$CP" tools/PrintCheck.java "$MODE"
