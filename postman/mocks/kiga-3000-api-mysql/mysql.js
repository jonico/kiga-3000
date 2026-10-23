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
 * Response semantics mirror KigaApiServer: GET, POST, PUT, DELETE and OPTIONS, 405 with
 * an Allow header on anything else, 400 on a non-numeric path segment or an invalid
 * body, 404 for unknown ids and for groups outside 0..255.
 *
 * Writes go through the same pm.datasets handle as reads. Verified: INSERT, UPDATE and
 * DELETE reach the MySQL table itself, not just the SQLite copy. The one thing the
 * engine does not hand back is the generated key, so a create reads the new id back
 * with MAX(id). That is racy under concurrent writers and fine for a mock that serves
 * one contract run at a time.
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


const ALLOWED = 'GET, POST, PUT, DELETE, OPTIONS';
const MAX_BODY_BYTES = 64 * 1024;
const MIN_GRUPPE = 1;
const MAX_GRUPPE = 9;
const MAX_NAME = 50;
const WRITABLE = ['gruppe', 'vorname', 'nachname', 'geburtsdatum', 'eintritt'];

class BadRequest extends Error {}
class NotFound extends Error {}

function sendEmpty(res, status, headers = {}) {
  res.writeHead(status, headers);
  res.end();
}

function readBody(req) {
  return new Promise((resolve, reject) => {
    const chunks = [];
    let size = 0;
    req.on('data', (chunk) => {
      size += chunk.length;
      if (size > MAX_BODY_BYTES) {
        reject(new BadRequest(`Request body larger than ${MAX_BODY_BYTES} bytes`));
        req.destroy();
        return;
      }
      chunks.push(chunk);
    });
    req.on('end', () => resolve(Buffer.concat(chunks).toString('utf8')));
    req.on('error', reject);
  });
}

/** A JSON object, or a 400 phrased like the Java API's. */
function parseObject(text) {
  let value;
  try {
    value = JSON.parse(text);
  } catch (e) {
    throw new BadRequest(`Malformed JSON: ${e.message}`);
  }
  if (value === null || typeof value !== 'object' || Array.isArray(value)) {
    throw new BadRequest('Malformed JSON: expected a JSON object');
  }
  return value;
}

function requiredName(fields, name) {
  const value = fields[name];
  if (typeof value !== 'string' || value.trim() === '') {
    throw new BadRequest(`${name} is required and must be a non-empty string`);
  }
  const trimmed = value.trim();
  if ([...trimmed].length > MAX_NAME) {
    throw new BadRequest(
      `${name} must be at most ${MAX_NAME} characters, was ${[...trimmed].length}`);
  }
  return trimmed;
}

/**
 * dd.MM.yyyy or yyyy-MM-dd in, an ISO date (or null for "no date") out. Round-tripping
 * through Date.UTC rejects 31.02 the way the Java side's strict parser does.
 */
function optionalDate(fields, name) {
  const value = fields[name];
  if (value === undefined || value === null) return null;
  if (typeof value !== 'string') {
    throw new BadRequest(`${name} must be a string in dd.MM.yyyy format`);
  }
  const text = value.trim();
  if (text === '') return null;
  let m = text.match(/^(\d{2})\.(\d{2})\.(\d{4})$/);
  let [d, mo, y] = m ? [m[1], m[2], m[3]] : [];
  if (!m) {
    m = text.match(/^(\d{4})-(\d{2})-(\d{2})$/);
    if (m) [y, mo, d] = [m[1], m[2], m[3]];
  }
  const probe = m && new Date(Date.UTC(Number(y), Number(mo) - 1, Number(d)));
  const real = probe && probe.getUTCFullYear() === Number(y)
    && probe.getUTCMonth() === Number(mo) - 1 && probe.getUTCDate() === Number(d);
  if (!real) {
    throw new BadRequest(
      `${name} must be a real date in dd.MM.yyyy format, was: ${text}`);
  }
  return `${y}-${mo}-${d}`;
}

/** Same checks, same order and same wording as KigaApiServer.draft. */
function draft(text) {
  const fields = parseObject(text);

  for (const name of Object.keys(fields)) {
    if (WRITABLE.includes(name)) continue;
    if (name === 'id') {
      throw new BadRequest('id is assigned by the server on create and taken from the URL'
        + ' on replace; remove it from the body');
    }
    throw new BadRequest(`Field not writable through this API: ${name}. Only gruppe,`
      + ' vorname, nachname, geburtsdatum and eintritt can be written; the rest of the'
      + ' card is special-category personal data and is desktop-only');
  }

  const gruppe = fields.gruppe;
  if (gruppe === undefined || gruppe === null) throw new BadRequest('gruppe is required');
  if (!Number.isInteger(gruppe)) {
    throw new BadRequest(`gruppe must be a whole number, was: ${gruppe}`);
  }
  if (gruppe < MIN_GRUPPE || gruppe > MAX_GRUPPE) {
    throw new BadRequest(
      `gruppe must be between ${MIN_GRUPPE} and ${MAX_GRUPPE}, was ${gruppe}`);
  }

  return {
    gruppe,
    vorname: requiredName(fields, 'vorname'),
    nachname: requiredName(fields, 'nachname'),
    geburtsdatum: optionalDate(fields, 'geburtsdatum'),
    eintritt: optionalDate(fields, 'eintritt'),
  };
}

/** The API's contract is "" for no date; strftime over NULL gives null. */
const shape = (row) => ({
  ...row,
  geburtsdatum: row.geburtsdatum ?? '',
  eintritt: row.eintritt ?? '',
});

async function cardById(id) {
  const found = await rows(`SELECT ${COLUMNS} FROM Karteikarte WHERE id = ?`, [id]);
  return found.length ? shape(found[0]) : null;
}

const parseId = (raw) => {
  if (!/^-?\d+$/.test(raw)) {
    throw new BadRequest(`Not a number: For input string: "${raw}"`);
  }
  return Number(raw);
};

async function create(req, res, pathname, segments) {
  if (segments.length !== 1 || segments[0] !== 'cards') {
    return send(res, 404, error(404, `Cannot create at ${pathname}`));
  }
  const card = draft(await readBody(req));
  await rows(
    'INSERT INTO Karteikarte (gruppe, kindvorname, kindnachname, kindgeburt, eintritt)'
    + ' VALUES (?, ?, ?, ?, ?)',
    [card.gruppe, card.vorname, card.nachname, card.geburtsdatum, card.eintritt]);
  const [{ id }] = await rows('SELECT MAX(id) AS id FROM Karteikarte');
  return send(res, 201, await cardById(id), { Location: `/api/cards/${id}` });
}

async function replace(req, res, pathname, segments) {
  if (segments.length !== 2 || segments[0] !== 'cards') {
    return send(res, 404, error(404, `Cannot replace at ${pathname}`));
  }
  const id = parseId(decodeURIComponent(segments[1]));
  const card = draft(await readBody(req));
  if (!(await cardById(id))) throw new NotFound(`No card with id ${id}`);
  await rows(
    'UPDATE Karteikarte SET gruppe = ?, kindvorname = ?, kindnachname = ?,'
    + ' kindgeburt = ?, eintritt = ? WHERE id = ?',
    [card.gruppe, card.vorname, card.nachname, card.geburtsdatum, card.eintritt, id]);
  return send(res, 200, await cardById(id));
}

async function remove(res, pathname, segments) {
  if (segments.length !== 2 || segments[0] !== 'cards') {
    return send(res, 404, error(404, `Cannot delete at ${pathname}`));
  }
  const id = parseId(decodeURIComponent(segments[1]));
  if (!(await cardById(id))) throw new NotFound(`No card with id ${id}`);
  await rows('DELETE FROM Karteikarte WHERE id = ?', [id]);
  return sendEmpty(res, 204);
}

const server = http.createServer(async (req, res) => {
  try {
    const pathname = new URL(req.url || '', 'http://localhost').pathname;

    const segments = pathname.replace(/^\/api\/?/, '').split('/');

    if (req.method === 'OPTIONS') {
      return sendEmpty(res, 204, { Allow: ALLOWED });
    }
    if (req.method === 'POST') return await create(req, res, pathname, segments);
    if (req.method === 'PUT') return await replace(req, res, pathname, segments);
    if (req.method === 'DELETE') return await remove(res, pathname, segments);
    // The method check comes first, so an unsupported method on a path that does not
    // exist is still a 405 rather than a 404.
    if (req.method !== 'GET') {
      return send(res, 405, error(405, `${req.method} is not supported`), { Allow: ALLOWED });
    }

    if (pathname === '/api/health') {
      return send(res, 200, { status: 'ok', service: 'kiga3000', api: 'read-write' });
    }

    if (pathname === '/api/cards') {
      return send(res, 200, (await rows(`SELECT ${COLUMNS} FROM Karteikarte ORDER BY id`)).map(shape));
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
        ? send(res, 200, shape(found[0]))
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
      return send(res, 200, (await rows(
        `SELECT ${COLUMNS} FROM Karteikarte WHERE gruppe = ? ORDER BY id`, [gruppe]))
        .map(shape));
    }

    return send(res, 404, error(404, `No such resource: ${pathname}`));
  } catch (e) {
    if (e instanceof BadRequest) return send(res, 400, error(400, e.message));
    if (e instanceof NotFound) return send(res, 404, error(404, e.message));
    console.error('mock handler error:', e && (e.stack || e.message || String(e)));
    return send(res, 500, error(500, 'Internal error'));
  }
});

server.listen(PORT);
