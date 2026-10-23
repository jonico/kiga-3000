#!/bin/bash
# Start the HTTP API and the web interface.
#
#   ./tools/run-api.sh            # port 8080
#   ./tools/run-api.sh 18080      # a different port
#
# Requires a running MySQL with the schema applied; see db/schema.sql.
# Bound to 127.0.0.1 only, so neither the API nor the web interface is reachable from
# the network. That is what stands in for the authentication story that does not exist
# yet, and it is the reason the write endpoints below are defensible at all.
#
# Endpoints:
#   GET    /                      the web interface
#   GET    /api/health
#   GET    /api/cards
#   GET    /api/cards/{id}
#   GET    /api/groups/{n}/cards
#   POST   /api/cards             create
#   PUT    /api/cards/{id}        replace the writable fields
#   DELETE /api/cards/{id}        delete
#
# Reads and writes cover the SAME six fields and no others. Religion, nationality,
# health notes, vaccinations, illnesses, doctor, insurer, contacts, addresses and the
# free-text fields can neither be read nor written here - see CardSummary and CardDraft
# for why. A body naming one of them gets a 400 that says so rather than being ignored.
# PATCH is deliberately absent; anything unsupported returns 405 with an Allow header.
set -euo pipefail

cd "$(dirname "$0")/.."
PORT="${1:-8080}"
# The jar is Java 25 bytecode (class file major 69); a JDK 21 runtime rejects it.
JAVA_HOME="${JAVA_HOME:-/opt/homebrew/opt/openjdk@25}"
export JAVA_HOME
[ -x "$JAVA_HOME/bin/java" ] || { echo "no JDK at $JAVA_HOME; see ./run.sh --check" >&2; exit 1; }

echo "==> building"
mvn -B -q -DskipTests package
mvn -B -q dependency:build-classpath -Dmdep.outputFile=target/cp.txt

echo "==> starting API on http://127.0.0.1:$PORT/api"
echo "    try: curl -s http://127.0.0.1:$PORT/api/cards | head -c 300"
exec "$JAVA_HOME/bin/java" -cp "target/classes:$(cat target/cp.txt)" \
  org.de.kiga3000.api.KigaApiServer "$PORT"
