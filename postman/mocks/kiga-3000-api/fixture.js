/**
 * Fixture-backed scenario for the KiGa 3000 API mock.
 *
 * Why this exists alongside the generated default.js: `postman mock generate` emits one
 * canned example per response, so GET /api/cards/{id} answers with the same card
 * whatever id you ask for. The Card Validation collection asserts that the response
 * matches the row it was driven with, so against default.js only the one iteration whose
 * id happens to match can pass.
 *
 * This handler serves from a table instead, and reads that table from the very same
 * cards.json the Cards Mock Fixture dataset uses. One file feeds both sides, so the
 * iteration data and the mock responses cannot drift apart - editing cards.json updates
 * the dataset rows and the mock's answers together.
 *
 * default.js is left exactly as generated, so `postman mock generate --update` can
 * refresh it from the spec without touching this file.
 *
 * Response semantics mirror KigaApiServer: read-only, 405 on any non-GET, 400 on a
 * non-numeric path segment, 404 for unknown ids and for groups outside 0..255.
 */
const fs = require('fs');
const http = require('http');
const path = require('path');

const FIXTURE = path.join(
  __dirname, '..', '..', 'datasets', 'Cards-Mock-Fixture', 'cards.json');

const cards = JSON.parse(fs.readFileSync(FIXTURE, 'utf8'));
const byId = new Map(cards.map((c) => [c.id, c]));

const PORT = process.env.PORT || 4010;
const JSON_TYPE = 'application/json; charset=utf-8';

function send(res, status, body, headers = {}) {
  res.writeHead(status, { 'Content-Type': JSON_TYPE, ...headers });
  res.end(JSON.stringify(body));
}

const error = (status, message) => ({ status, error: message });

const server = http.createServer((req, res) => {
  const pathname = new URL(req.url || '', 'http://localhost').pathname;

  // Read-only, exactly as the Java server: the method check comes first, so a POST to a
  // path that does not exist is still a 405 rather than a 404.
  if (req.method !== 'GET') {
    return send(res, 405, error(405, 'This API is read-only'), { Allow: 'GET' });
  }

  if (pathname === '/api/health') {
    return send(res, 200, { status: 'ok', service: 'kiga3000', api: 'read-only' });
  }

  if (pathname === '/api/cards') {
    return send(res, 200, cards);
  }

  const card = pathname.match(/^\/api\/cards\/([^/]+)$/);
  if (card) {
    const raw = decodeURIComponent(card[1]);
    if (!/^-?\d+$/.test(raw)) {
      return send(res, 400, error(400, `Not a number: For input string: "${raw}"`));
    }
    const found = byId.get(Number(raw));
    return found
      ? send(res, 200, found)
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
    return send(res, 200, cards.filter((c) => c.gruppe === gruppe));
  }

  return send(res, 404, error(404, `No such resource: ${pathname}`));
});

server.listen(PORT);
