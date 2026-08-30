-- ---------------------------------------------------------------------------
-- Demo/test data for KiGa 3000.
--
-- NOTHING IN THIS FILE IS REAL DATA. Every person, address, telephone number,
-- date and medical detail below was invented for this repository in 2026.
--
-- The original 2004-2006 CVS repository contained no data dumps and no INSERT
-- statements of any kind, so no historical records survived to restore.
--
-- The private working repository this was published from used the names of a
-- handful of real people in section 1 as a running joke, with their own
-- business-card-level professional details. All of that was removed before
-- publication: real and invented dates of birth attached to identifiable people
-- do not belong in a public demo fixture. Section 1 is now eight invented
-- children like section 2, and the row count, group distribution and column
-- values are otherwise unchanged, so the application behaves identically.
--
-- The data deliberately includes ae/oe/ue/ss characters and a hyphenated surname
-- so the encoding and sorting paths are exercised.
-- ---------------------------------------------------------------------------

-- Section 1 - invented children, minimal fields (see header)
-- ===========================================================================
INSERT INTO `Karteikarte`
  (`gruppe`, `kindvorname`, `kindnachname`, `kindgeburt`, `eintritt`, `eintrittsgrund`,
   `geburtsort`, `kindwohnung`, `religion`, `staat`, `kindtelefon`,
   `vatername`, `vaterberuf`, `muttername`, `mutterberuf`,
   `sorgeperson`, `arbeitsort1`, `elternort`, `elterntel`, `anzahlgeschw`, `famstand`,
   `impfung`, `tetanuszeit`, `krankheiten`, `wkrankheit`, `gesundheit`,
   `arztort`, `arzttel`, `krankenkasse`, `sonstiges`, `lastaccess`)
VALUES
  (1, 'Anna', 'Musterkind',          '2020-03-11', '2023-09-01', 'Regelaufnahme',
      'Erfundenhausen', 'Fiktivstrasse 1, 11111 Erfundenhausen', 'keine Angabe', 'Deutschland', '01111 300001',
      'Vater Musterkind', 'keine Angabe', 'Mutter Musterkind', 'keine Angabe',
      'beide Eltern', 'Erfundenhausen', 'Erfundenhausen', '01111 300001', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30'),

  (1, 'Ben', 'Beispiel',            '2020-11-20', '2023-09-01', 'Regelaufnahme',
      'Erfundenhausen', 'Fiktivstrasse 2, 11112 Erfundenhausen', 'keine Angabe', 'Deutschland', '01111 300002',
      'Vater Beispiel', 'keine Angabe', 'Mutter Beispiel', 'keine Angabe',
      'beide Eltern', 'Erfundenhausen', 'Erfundenhausen', '01111 300002', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30'),

  (1, 'Clara', 'Probst-Mueller',      '2020-07-28', '2023-09-01', 'Regelaufnahme',
      'Musterstadt', 'Fiktivstrasse 3, 11113 Musterstadt', 'keine Angabe', 'Deutschland', '01111 300003',
      'Vater Probst-Mueller', 'keine Angabe', 'Mutter Probst-Mueller', 'keine Angabe',
      'beide Eltern', 'Musterstadt', 'Musterstadt', '01111 300003', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30'),

  (2, 'David', 'Faelschle',           '2021-04-04', '2024-09-01', 'Regelaufnahme',
      'Musterstadt', 'Fiktivstrasse 4, 11114 Musterstadt', 'keine Angabe', 'Deutschland', '01111 300004',
      'Vater Faelschle', 'keine Angabe', 'Mutter Faelschle', 'keine Angabe',
      'beide Eltern', 'Musterstadt', 'Musterstadt', '01111 300004', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30'),

  (2, 'Emilia', 'Grosskopf',           '2021-03-17', '2024-09-01', 'Regelaufnahme',
      'Beispieldorf', 'Fiktivstrasse 5, 11115 Beispieldorf', 'keine Angabe', 'Deutschland', '01111 300005',
      'Vater Grosskopf', 'keine Angabe', 'Mutter Grosskopf', 'keine Angabe',
      'beide Eltern', 'Beispieldorf', 'Beispieldorf', '01111 300005', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30'),

  (2, 'Finn', 'Hoffmeister',         '2021-09-23', '2024-09-01', 'Regelaufnahme',
      'Beispieldorf', 'Fiktivstrasse 6, 11116 Beispieldorf', 'keine Angabe', 'Deutschland', '01111 300006',
      'Vater Hoffmeister', 'keine Angabe', 'Mutter Hoffmeister', 'keine Angabe',
      'beide Eltern', 'Beispieldorf', 'Beispieldorf', '01111 300006', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30'),

  (3, 'Greta', 'Klingsoehr',          '2022-02-09', '2025-09-01', 'Regelaufnahme',
      'Erfundenhausen', 'Fiktivstrasse 7, 11117 Erfundenhausen', 'keine Angabe', 'Deutschland', '01111 300007',
      'Vater Klingsoehr', 'keine Angabe', 'Mutter Klingsoehr', 'keine Angabe',
      'beide Eltern', 'Erfundenhausen', 'Erfundenhausen', '01111 300007', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30'),

  (3, 'Henri', 'Loewenstein',         '2022-06-17', '2025-09-01', 'Regelaufnahme',
      'Musterstadt', 'Fiktivstrasse 8, 11118 Musterstadt', 'keine Angabe', 'Deutschland', '01111 300008',
      'Vater Loewenstein', 'keine Angabe', 'Mutter Loewenstein', 'keine Angabe',
      'beide Eltern', 'Musterstadt', 'Musterstadt', '01111 300008', 0, 0,
      'keine Angabe', '', 0, '', 'keine Angabe',
      'keine Angabe', '-', 'keine Angabe',
      'Erfundener Datensatz fuer Demo- und Testzwecke.', '2026-08-30');

-- Section 2 - fully invented records, richly populated
-- ===========================================================================
INSERT INTO `Karteikarte`
  (`gruppe`, `kindvorname`, `kindnachname`, `kindgeburt`, `eintritt`, `eintrittsgrund`,
   `geburtsort`, `kindwohnung`, `religion`, `staat`, `kindtelefon`,
   `vatername`, `vatergeburt`, `vaterberuf`, `muttername`, `muttergeburt`, `mutterberuf`,
   `sorgeperson`, `arbeitsort1`, `arbeittel1`, `arbeitsort2`, `arbeittel2`,
   `geschwgeburt`, `famstand`, `elternort`, `elterntel`, `anzahlgeschw`,
   `impfung`, `tetanuszeit`, `krankheiten`, `wkrankheit`, `gesundheit`,
   `arztort`, `arzttel`, `krankenkasse`, `sonstiges`, `lastaccess`)
VALUES
  (1, 'Mira', 'Bergmann',      '2020-03-14', '2023-09-01', 'Regelaufnahme',
      'Erfundenhausen', 'Fiktivstraße 12, 11111 Erfundenhausen', 'rk', 'Deutschland', '01111 100001',
      'Andreas Bergmann', '1987-05-04', 'Tischler',    'Nina Bergmann',    '1989-08-22', 'Physiotherapeutin',
      'beide Eltern', 'Erfundenhausen', '01111 200001', '', '',
      '2023-06-11', 1, 'Erfundenhausen', '01111 100001', 1,
      'Masern, Mumps, Röteln', '2022-04-18', 0, '', 'keine Auffälligkeiten',
      'Erfundenhausen Mitte', '01111 300001', 'Beispielkasse', 'Schläft mittags noch', '2026-08-29'),

  (1, 'Tobias', 'Winkelmann',   '2019-07-02', '2022-09-01', 'Regelaufnahme',
      'Erfundenhausen', 'Fiktivstraße 40, 11111 Erfundenhausen', 'ev', 'Deutschland', '01111 100002',
      'Marek Winkelmann', '1984-11-09', 'Fahrer',      'Ines Winkelmann',  '1986-02-27', 'Bürokauffrau',
      'beide Eltern', 'Erfundenhausen', '01111 200002', 'Beispielstadt', '01111 200012',
      '', 1, 'Erfundenhausen', '01111 100002', 0,
      'Masern, Mumps, Röteln', '2021-09-30', 0, '', 'Pollenallergie im Frühjahr',
      'Erfundenhausen Süd', '01111 300002', 'Musterkasse', 'Wird freitags früher abgeholt', '2026-08-29'),

  (1, 'Sophie', 'Krautheim',    '2020-11-23', '2023-09-01', 'Regelaufnahme',
      'Beispielstadt', 'Testallee 7, 22222 Beispielstadt', '-', 'Deutschland', '02222 100003',
      'Lukas Krautheim', '1990-01-15', 'Softwareentwickler', 'Hanna Krautheim', '1991-06-03', 'Ärztin',
      'beide Eltern', 'Beispielstadt', '02222 200003', '', '',
      '2018-02-14', 1, 'Beispielstadt', '02222 100003', 2,
      'Masern, Mumps, Röteln, Windpocken', '2022-11-05', 0, '', 'Brille (Kurzsichtigkeit)',
      'Beispielstadt Nord', '02222 300003', 'Beispielkasse', 'Geschwisterkind in Gruppe 3', '2026-08-29'),

  (2, 'Elias', 'Roßberg',       '2019-02-08', '2022-09-01', 'Regelaufnahme',
      'Beispielstadt', 'Testallee 19, 22222 Beispielstadt', 'rk', 'Deutschland', '02222 100004',
      'Sven Roßberg',    '1983-09-21', 'Elektriker',   'Petra Roßberg',    '1985-12-30', 'Erzieherin',
      'Mutter', 'Beispielstadt', '02222 200004', '', '',
      '2021-05-19', 2, 'Beispielstadt', '02222 100004', 1,
      'Masern, Mumps, Röteln', '2021-03-12', 0, '', 'Laktoseintoleranz',
      'Beispielstadt West', '02222 300004', 'Musterkasse', 'Keine Milchprodukte zum Frühstück (ß-Encoding-Test)', '2026-08-29'),

  (2, 'Yusuf', 'Özdemir',       '2020-06-17', '2023-09-01', 'Zuzug',
      'Erfundenhausen', 'Fiktivstraße 3, 11111 Erfundenhausen', '-', 'Deutschland', '01111 100005',
      'Emre Özdemir',    '1988-04-02', 'Kfz-Mechaniker', 'Derya Özdemir',  '1990-10-11', 'Friseurin',
      'beide Eltern', 'Erfundenhausen', '01111 200005', '', '',
      '', 1, 'Erfundenhausen', '01111 100005', 0,
      'Masern, Mumps', '2022-07-25', 0, '', 'Sprachförderung Deutsch',
      'Erfundenhausen Mitte', '01111 300005', 'Beispielkasse', 'Zweisprachig (TR/DE)', '2026-08-29'),

  (2, 'Charlotte', 'Hübner',    '2019-09-29', '2022-09-01', 'Regelaufnahme',
      'Musterdorf', 'Probeweg 88, 33333 Musterdorf', 'ev', 'Deutschland', '03333 100006',
      'Jan Hübner',      '1986-07-07', 'Landwirt',     'Christine Hübner', '1988-03-18', 'Buchhalterin',
      'beide Eltern', 'Musterdorf', '03333 200006', '', '',
      '2016-08-02', 1, 'Musterdorf', '03333 100006', 3,
      'Masern, Mumps, Röteln', '2021-11-08', 0, '', 'keine Auffälligkeiten',
      'Musterdorf', '03333 300006', 'Musterkasse', 'Fährt mit dem Kindergartenbus', '2026-08-29'),

  (3, 'Leonard', 'Krautheim',   '2018-02-14', '2021-09-01', 'Regelaufnahme',
      'Beispielstadt', 'Testallee 7, 22222 Beispielstadt', '-', 'Deutschland', '02222 100003',
      'Lukas Krautheim', '1990-01-15', 'Softwareentwickler', 'Hanna Krautheim', '1991-06-03', 'Ärztin',
      'beide Eltern', 'Beispielstadt', '02222 200003', '', '',
      '2020-11-23', 1, 'Beispielstadt', '02222 100003', 2,
      'Masern, Mumps, Röteln, Windpocken', '2020-06-14', 0, '', 'keine Auffälligkeiten',
      'Beispielstadt Nord', '02222 300003', 'Beispielkasse', 'Wechselt 2026 in die Grundschule', '2026-08-29'),

  (3, 'Amelie', 'Vogtländer',   '2018-05-06', '2021-09-01', 'Regelaufnahme',
      'Musterdorf', 'Probeweg 4, 33333 Musterdorf', 'rk', 'Deutschland', '03333 100008',
      'Robert Vogtländer', '1982-12-12', 'Dachdecker', 'Sandra Vogtländer', '1984-05-25', 'Verkäuferin',
      'Vater', 'Musterdorf', '03333 200008', '', '',
      '', 3, 'Musterdorf', '03333 100008', 0,
      'Masern, Mumps, Röteln', '2020-02-20', 0, '', 'Asthma, Spray im Büro hinterlegt',
      'Musterdorf', '03333 300008', 'Beispielkasse', 'Notfallmedikament im Gruppenschrank', '2026-08-29'),

  (3, 'Bennet', 'Achterberg',   '2018-10-31', '2021-09-01', 'Regelaufnahme',
      'Erfundenhausen', 'Fiktivstraße 55, 11111 Erfundenhausen', '-', 'Deutschland', '01111 100009',
      'Dirk Achterberg', '1985-03-03', 'Koch',         'Miriam Achterberg', '1987-09-14', 'Krankenpflegerin',
      'beide Eltern', 'Erfundenhausen', '01111 200009', 'Musterdorf', '03333 200019',
      '2022-01-09', 1, 'Erfundenhausen', '01111 100009', 1,
      'Masern, Mumps, Röteln', '2020-10-01', 0, '', 'Nussallergie',
      'Erfundenhausen Süd', '01111 300009', 'Musterkasse', 'Strikt nussfreie Verpflegung', '2026-08-29');
