/**
 * MySQL-backed scenario for the KiGa 3000 API mock.
 *
 * A demonstration: a local mock server that answers from the live Kindergarten
 * database rather than from canned examples. It queries the Cards MySQL Database
 * dataset through pm.datasets, so every response is built from whatever is actually
 * in the Karteikarte table at the time of the run. Change a row in MySQL and the
 * mock's answers change with it, with nothing regenerated.
 *
 * The point of the exercise is that this is indistinguishable from the real Java API
 * while requiring neither a JVM nor the API process: the seeded database is the only
 * live dependency.
 *
 * Sibling mocks:
 *   postman/mocks/kiga-3000-api  Cards Mock Fixture, a static JSON source. Offline,
 *                                no database, no Postman session needed.
 *   this one                     Cards MySQL Database, a dynamic mysql source. Needs
 *                                the seeded MySQL and a Postman session, because the
 *                                engine will not read a remote source unauthenticated.
 *
 * ---------------------------------------------------------------------------
 * The query deserves an explanation, because none of it is arbitrary.
 *
 * Despite the source being MySQL, the SQL here is executed by SQLite (3.46.1) over a
 * materialised copy of the table, not sent to MySQL. Verified: sqlite_version()
 * answers, MySQL's own VERSION() fails. So MySQL syntax is unavailable - DATE_FORMAT()
 * does not work and, thanks to SE1-294, fails with an opaque 500 rather than a syntax
 * error.
 *
 * That matters for the two date columns. Selecting kindgeburt or eintritt directly
 * yields the literal string "<unrepresentable>", because the engine has no
 * representation for a DATE value. CAST(... AS TEXT) gives an ISO instant
 * ("1984-08-11T00:00:00Z"), which is not what the API returns either. SQLite's
 * strftime does produce the German dd.MM.yyyy the API emits, so that is what is used.
 *
 * The column aliases mirror the projection in CardSummary: the table's kindvorname
 * and kindnachname surface as vorname and nachname, and the 33 columns the API
 * withholds - religion, nationality, vaccinations, illnesses, doctor, insurer, health
 * notes - are simply never selected.
 * ---------------------------------------------------------------------------
 *
 * pm.datasets only resolves when the mock is spawned by a collection run that has the
 * dataset loaded, i.e. --use-mock alongside --iteration-data-dataset. `postman mock
 * run` on its own cannot load a local dataset and every query fails [not_found].
 * Handler stderr is also not forwarded under --use-mock, so the log below is invisible
 * in that mode; read the response bodies out of the --output execution log instead.
 *
 * default.js is left exactly as generated so `postman mock generate --update` can
 * refresh it from the spec.
 *
 * Response semantics mirror KigaApiServer: read-only, 405 on any non-GET, 400 on a
 * non-numeric path segment, 404 for unknown ids and for groups outside 0..255.
 */
const http = require('http');

const DATASET_ID = 'fe7a53b2-3ba1-492a-a819-d7df1d00ebe0';
// 4011, not the generator's 4010, so this can run alongside the fixture mock.
const PORT = process.env.PORT || 4011;
const JSON_TYPE = 'application/json; charset=utf-8';

const COLUMNS = [
  'id',
  'gruppe',
  'kindvorname AS vorname',
  'kindnachname AS nachname',
  "strftime('%d.%m.%Y', kindgeburt) AS geburtsdatum",
  "strftime('%d.%m.%Y', eintritt) AS eintritt",
].join(', ');

function send(res, status, body, headers = {}) {
  res.writeHead(status, { 'Content-Type': JSON_TYPE, ...headers });
  res.end(JSON.stringify(body));
}

const error = (status, message) => ({ status, error: message });

/** Drain the async iterable a dataset query returns into a plain array. */
async function rows(sql, params = []) {
  const result = await pm.datasets(DATASET_ID).executeQuery(sql, params);
  const out = [];
  for await (const row of result.rows) {
    out.push(row);
  }
  return out;
}

const server = http.createServer(async (req, res) => {
  try {
    const pathname = new URL(req.url || '', 'http://localhost').pathname;

    // Read-only, exactly as the Java server: the method check comes first, so a POST to
    // a path that does not exist is still a 405 rather than a 404.
    if (req.method !== 'GET') {
      return send(res, 405, error(405, 'This API is read-only'), { Allow: 'GET' });
    }

    if (pathname === '/api/health') {
      return send(res, 200, { status: 'ok', service: 'kiga3000', api: 'read-only' });
    }

    if (pathname === '/api/cards') {
      return send(res, 200, await rows(`SELECT ${COLUMNS} FROM Karteikarte ORDER BY id`));
    }

    const card = pathname.match(/^\/api\/cards\/([^/]+)$/);
    if (card) {
      const raw = decodeURIComponent(card[1]);
      if (!/^-?\d+$/.test(raw)) {
        return send(res, 400, error(400, `Not a number: For input string: "${raw}"`));
      }
      const found = await rows(
        `SELECT ${COLUMNS} FROM Karteikarte WHERE id = ?`, [Number(raw)]);
      return found.length
        ? send(res, 200, found[0])
        : send(res, 404, error(404, `No card with id ${raw}`));
    }

    const group = pathname.match(/^\/api\/groups\/([^/]+)\/cards$/);
    if (group) {
      const raw = decodeURIComponent(group[1]);
      if (!/^-?\d+$/.test(raw)) {
        return send(res, 400, error(400, `Not a number: For input string: "${raw}"`));
      }
      const gruppe = Number(raw);
      if (gruppe < 0 || gruppe > 255) {
        return send(res, 404, error(404, `No such group: ${raw}`));
      }
      return send(res, 200, await rows(
        `SELECT ${COLUMNS} FROM Karteikarte WHERE gruppe = ? ORDER BY id`, [gruppe]));
    }

    return send(res, 404, error(404, `No such resource: ${pathname}`));
  } catch (e) {
    console.error('mock handler error:', e && (e.stack || e.message || String(e)));
    return send(res, 500, error(500, 'Internal error'));
  }
});

server.listen(PORT);
