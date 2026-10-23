/*
 * KiGa 3000 web interface.
 *
 * Vanilla JavaScript against the JSON API on the same origin. No framework and no
 * build step: the whole client is three files served out of the jar.
 *
 * One rule worth stating because it is the mirror of Json.string() on the server: card
 * data is only ever put into the page through textContent, never through innerHTML.
 * The names in this database are user-entered German text, and the test fixture
 * deliberately contains a name with a quote and a backslash in it. Building rows by
 * string concatenation would turn that fixture into an injection.
 */
'use strict';

const api = {
  async request(method, path, body) {
    const response = await fetch(path, {
      method,
      headers: body === undefined ? {} : { 'Content-Type': 'application/json' },
      body: body === undefined ? undefined : JSON.stringify(body),
    });

    if (response.status === 204) {
      return null;
    }

    // Errors come back as {"status":n,"error":"..."}; surface that text rather than a
    // bare status code, because the server's messages name the offending field.
    let payload = null;
    const text = await response.text();
    if (text) {
      try {
        payload = JSON.parse(text);
      } catch {
        payload = null;
      }
    }

    if (!response.ok) {
      const message = payload && payload.error ? payload.error : `HTTP ${response.status}`;
      throw new Error(message);
    }
    return payload;
  },

  health: () => api.request('GET', '/api/health'),
  all: () => api.request('GET', '/api/cards'),
  byGruppe: (gruppe) => api.request('GET', `/api/groups/${gruppe}/cards`),
  create: (draft) => api.request('POST', '/api/cards', draft),
  replace: (id, draft) => api.request('PUT', `/api/cards/${id}`, draft),
  remove: (id) => api.request('DELETE', `/api/cards/${id}`),
};

const el = {
  health: document.getElementById('health'),
  filter: document.getElementById('gruppe-filter'),
  search: document.getElementById('suche'),
  neu: document.getElementById('neu'),
  body: document.getElementById('cards-body'),
  empty: document.getElementById('empty'),
  count: document.getElementById('count'),
  dialog: document.getElementById('editor'),
  form: document.getElementById('editor-form'),
  title: document.getElementById('editor-title'),
  error: document.getElementById('editor-error'),
  abbrechen: document.getElementById('abbrechen'),
  toast: document.getElementById('toast'),
};

/** The cards currently loaded, before the name filter is applied. */
let loaded = [];

/** The id being edited, or null when the dialog is creating. */
let editing = null;

// --------------------------------------------------------------------- rendering

function toast(message) {
  el.toast.textContent = message;
  el.toast.hidden = false;
  clearTimeout(toast.timer);
  toast.timer = setTimeout(() => {
    el.toast.hidden = true;
  }, 2600);
}

function cell(text, className) {
  const td = document.createElement('td');
  td.textContent = text === null || text === undefined || text === '' ? '–' : text;
  if (className) {
    td.className = className;
  }
  return td;
}

function actionButton(label, className, onClick) {
  const button = document.createElement('button');
  button.type = 'button';
  button.className = className;
  button.textContent = label;
  button.addEventListener('click', onClick);
  return button;
}

function row(card) {
  const tr = document.createElement('tr');
  tr.append(
    cell(card.id, 'num'),
    cell(card.gruppe, 'num'),
    cell(card.vorname),
    cell(card.nachname),
    cell(card.geburtsdatum),
    cell(card.eintritt),
  );

  const actions = document.createElement('td');
  actions.className = 'row-actions';
  actions.append(
    actionButton('Bearbeiten', 'link', () => openEditor(card)),
    actionButton('Löschen', 'link danger', () => remove(card)),
  );
  tr.append(actions);
  return tr;
}

function render() {
  const needle = el.search.value.trim().toLowerCase();
  const visible = needle
    ? loaded.filter((c) => `${c.vorname} ${c.nachname}`.toLowerCase().includes(needle))
    : loaded;

  el.body.replaceChildren(...visible.map(row));
  el.empty.hidden = visible.length > 0;
  el.count.textContent = needle
    ? `${visible.length} von ${loaded.length} Karteikarten`
    : `${loaded.length} Karteikarten`;
}

// ----------------------------------------------------------------------- loading

async function refresh() {
  const gruppe = el.filter.value;
  try {
    loaded = gruppe ? await api.byGruppe(gruppe) : await api.all();
    render();
  } catch (e) {
    loaded = [];
    render();
    toast(`Laden fehlgeschlagen: ${e.message}`);
  }
}

async function checkHealth() {
  try {
    const health = await api.health();
    el.health.textContent = `${health.service} · ${health.api}`;
    el.health.className = 'badge badge-ok';
  } catch {
    el.health.textContent = 'nicht erreichbar';
    el.health.className = 'badge badge-down';
  }
}

// ------------------------------------------------------------------------ editing

function openEditor(card) {
  editing = card ? card.id : null;
  el.title.textContent = card
    ? `Karteikarte ${card.id} bearbeiten`
    : 'Neue Karteikarte';
  el.form.gruppe.value = card ? card.gruppe : el.filter.value || '1';
  el.form.vorname.value = card ? card.vorname : '';
  el.form.nachname.value = card ? card.nachname : '';
  el.form.geburtsdatum.value = card ? card.geburtsdatum : '';
  el.form.eintritt.value = card ? card.eintritt : '';
  el.error.hidden = true;
  el.dialog.showModal();
}

/**
 * The five writable fields, and nothing else.
 *
 * The server refuses an unknown field with a 400 rather than dropping it, so sending
 * anything more here would be a visible error rather than a silent one - but the point
 * is that the client has no way to name a sensitive column in the first place.
 */
function draftFromForm() {
  return {
    gruppe: Number(el.form.gruppe.value),
    vorname: el.form.vorname.value.trim(),
    nachname: el.form.nachname.value.trim(),
    geburtsdatum: el.form.geburtsdatum.value.trim(),
    eintritt: el.form.eintritt.value.trim(),
  };
}

async function save(event) {
  // The form is method="dialog", which would close it before the request finishes.
  event.preventDefault();
  if (!el.form.reportValidity()) {
    return;
  }

  const draft = draftFromForm();
  try {
    const saved = editing === null
      ? await api.create(draft)
      : await api.replace(editing, draft);
    el.dialog.close();
    toast(editing === null
      ? `Karteikarte ${saved.id} angelegt`
      : `Karteikarte ${saved.id} gespeichert`);
    await refresh();
  } catch (e) {
    // Kept open with the message in place: retyping five fields because of one bad
    // date is exactly the kind of thing the 2006 dialogs did.
    el.error.textContent = e.message;
    el.error.hidden = false;
  }
}

async function remove(card) {
  const name = `${card.vorname} ${card.nachname}`.trim();
  if (!window.confirm(`Karteikarte ${card.id} (${name}) wirklich löschen?`)) {
    return;
  }
  try {
    await api.remove(card.id);
    toast(`Karteikarte ${card.id} gelöscht`);
    await refresh();
  } catch (e) {
    toast(`Löschen fehlgeschlagen: ${e.message}`);
  }
}

// ------------------------------------------------------------------------- wiring

el.filter.addEventListener('change', refresh);
el.search.addEventListener('input', render);
el.neu.addEventListener('click', () => openEditor(null));
el.form.addEventListener('submit', save);
el.abbrechen.addEventListener('click', () => el.dialog.close());

checkHealth();
refresh();
