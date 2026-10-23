/**
 * Dataset-backed scenario for the KiGa 3000 API mock.
 *
 * Why this exists alongside the generated default.js: `postman mock generate` emits one
 * canned example per response, so GET /api/cards/{id} answers with the same card
 * whatever id you ask for. The Card Validation collection asserts that the response
 * matches the row it was driven with, so against default.js only the one iteration whose
 * id happens to match can pass.
 *
 * This handler answers from the Cards Mock Fixture dataset via pm.datasets - the same
 * dataset the collection run is driven by. Both sides read through the dataset engine,
 * so the iteration data and the mock's responses cannot drift apart: editing cards.json
 * moves both together, and the mock can answer for any row the dataset holds rather
 * than a fixed example.
 *
 * default.js is left exactly as generated, so `postman mock generate --update` can
 * refresh it from the spec without touching this file.
 *
 * Two constraints worth knowing before editing this, both established by testing:
 *
 *   1. pm.datasets only resolves when the mock is spawned by a collection run that has
 *      the dataset loaded, i.e. via --use-mock alongside --iteration-data-dataset.
 *      Starting this scenario on its own with `postman mock run` fails every query with
 *      [not_found] dataset "<id>" not found, because that command has no way to load a
 *      local dataset - it takes --environment and --globals, but no --dataset.
 *   2. Handler stderr is NOT forwarded into the collection-run output under --use-mock,
 *      so console logging here is invisible in that mode. To see it, the run has to be
 *      reproduced some other way; the practical alternative is to inspect the response
 *      bodies in the --output execution log.
 *
 * pm.datasets is local-mock only; it is unsupported in the cloud mock code editor.
 * Every method is async and rows arrive as an async iterable, hence the for await.
 *
 * Response semantics mirror KigaApiServer: read-only, 405 on any non-GET, 400 on a
 * non-numeric path segment, 404 for unknown ids and for groups outside 0..255.
 */
const http = require('http');

const DATASET_ID = 'f7537ad6-83a3-4c2d-b973-08f4b471dc49';
const PORT = process.env.PORT || 4010;
const JSON_TYPE = 'application/json; charset=utf-8';

// Named explicitly rather than SELECT *, so the response carries exactly the six
// CardSummary fields the real API exposes and nothing the dataset might add later.
const COLUMNS = 'id, gruppe, vorname, nachname, geburtsdatum, eintritt';

function send(res, status, body, headers = {}) {
  res.writeHead(status, { 'Content-Type': JSON_TYPE, ...headers });
  res.end(JSON.stringify(body));
}

const error = (status, message) => ({ status, error: message });

/**
 * Drain the async iterable a dataset query returns into a plain array.
 *
 * The rows come back already shaped for the response: the selected columns as keys,
 * with id and gruppe as numbers and the rest as strings, which is what the collection
 * compares against with eql. No type coercion is applied - it was measured to be
 * unnecessary, and adding it would hide a future change in the engine's representation
 * instead of failing loudly.
 */
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
    // Mirror the Java router's catch-all rather than leaving the socket hanging. The
    // log is invisible under --use-mock (see the header note) but reaches the terminal
    // in any mode that does forward handler stderr.
    console.error('mock handler error:', e && (e.stack || e.message || String(e)));
    return send(res, 500, error(500, 'Internal error'));
  }
});

server.listen(PORT);
