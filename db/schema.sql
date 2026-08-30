-- KiGa 3000 schema, modernized from the 2006 `datenbanktabelle` file.
--
-- Four things in the original DDL no longer work, or no longer should:
--
--   1. `TYPE=MyISAM`   - the `TYPE=` clause was deprecated in MySQL 4.1 and REMOVED
--                        in 5.5. Replaced by `ENGINE=`.
--
--   2. MyISAM          - replaced by InnoDB. MyISAM has no transactions: it accepts
--                        BEGIN/COMMIT and silently ignores them. That was tolerable
--                        while every write was a single autocommit statement, but the
--                        card CRUD now runs through JPA, which expects a real
--                        transaction boundary. InnoDB also gives crash recovery and
--                        row-level locking, both of which matter for data about
--                        children.
--
--   3. zero dates      - `DATE NOT NULL DEFAULT '0000-00-00'` is rejected while
--                        NO_ZERO_DATE / NO_ZERO_IN_DATE are in sql_mode, which is the
--                        default since MySQL 5.7. The application used the literal
--                        '0000-00-00' to mean "no date", which only worked because the
--                        JDBC URL relaxed sql_mode to ALLOW_INVALID_DATES and set
--                        zeroDateTimeBehavior=ROUND.
--
--                        The five date columns are now NULLABLE and NULL means "no
--                        date". This is what SQL has always had for the purpose, it
--                        needs no relaxed sql_mode, and `java.sql.Date` can represent
--                        it - '0000-00-00' cannot, which is why JPA had nowhere to put
--                        it. Both driver workarounds are gone from the JDBC URL as a
--                        result.
--
--                        The Swing layer is unaffected: it still exchanges date
--                        strings, and an empty string maps to NULL in both directions.
--
--   4. latin1 default  - the database is created as utf8mb4 so German umlauts
--                        survive; the 2006 server defaulted to latin1.
--
-- For an existing database, use db/migrate-01-nullable-dates.sql instead of this file.

CREATE DATABASE IF NOT EXISTS Kindergarten
  CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE Kindergarten;

DROP TABLE IF EXISTS `Karteikarte`;
CREATE TABLE `Karteikarte` (
  `id` int(11) NOT NULL auto_increment,
  `gruppe` tinyint(3) unsigned NOT NULL default '0',
  -- Set to the current date whenever a card is created or updated.
  `lastaccess` date default NULL,
  -- The five dates. NULL means "not recorded".
  `kindgeburt` date default NULL,
  `eintritt` date default NULL,
  `eintrittsgrund` varchar(50) NOT NULL default '',
  `austritt` date default NULL,
  `austrittsgrund` varchar(50) NOT NULL default '',
  `kindnachname` varchar(50) NOT NULL default '',
  `kindvorname` varchar(50) NOT NULL default '',
  `geburtsort` varchar(100) NOT NULL default '',
  `kindwohnung` varchar(100) NOT NULL default '',
  `religion` varchar(25) NOT NULL default '',
  `staat` varchar(40) NOT NULL default '',
  `kindtelefon` varchar(30) NOT NULL default '',
  `vatername` varchar(70) NOT NULL default '',
  `vatergeburt` date default NULL,
  `vaterberuf` varchar(50) NOT NULL default '',
  `muttername` varchar(70) NOT NULL default '',
  `muttergeburt` date default NULL,
  `mutterberuf` varchar(70) NOT NULL default '',
  `sorgeperson` varchar(100) NOT NULL default '',
  `arbeitsort1` varchar(50) NOT NULL default '',
  `arbeittel1` varchar(30) NOT NULL default '',
  `arbeitsort2` varchar(50) NOT NULL default '',
  `arbeittel2` varchar(30) NOT NULL default '',
  `geschwgeburt` varchar(50) NOT NULL default '',
  `famstand` tinyint(3) unsigned NOT NULL default '0',
  `elternort` varchar(100) NOT NULL default '',
  `elterntel` varchar(30) NOT NULL default '',
  `anzahlgeschw` tinyint(3) unsigned NOT NULL default '0',
  `impfung` varchar(100) NOT NULL default '',
  `tetanuszeit` varchar(50) NOT NULL default '',
  `krankheiten` smallint(6) NOT NULL default '0',
  `wkrankheit` varchar(50) NOT NULL default '',
  `gesundheit` varchar(255) NOT NULL default '',
  `arztort` varchar(50) NOT NULL default '',
  `arzttel` varchar(30) NOT NULL default '',
  `krankenkasse` varchar(30) NOT NULL default '',
  `sonstiges` varchar(255) NOT NULL default '',
  PRIMARY KEY  (`id`),
  KEY `gruppe` (`gruppe`,`lastaccess`,`kindgeburt`,`kindnachname`)
) ENGINE=InnoDB COMMENT='Daten der Kinder';
