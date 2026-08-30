#!/bin/bash
# Start the read-only HTTP API.
#
#   ./tools/run-api.sh            # port 8080
#   ./tools/run-api.sh 18080      # a different port
#
# Requires a running MySQL with the schema applied; see db/schema.sql.
# Bound to 127.0.0.1 only, so it is not reachable from the network.
#
# Endpoints:
#   GET /api/health
#   GET /api/cards
#   GET /api/cards/{id}
#   GET /api/groups/{n}/cards
#
# Read-only by design: anything other than GET returns 405. The response projection
# deliberately omits religion, nationality, health notes, vaccinations, doctor, insurer,
# contacts and free-text fields - see CardSummary for why.
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
