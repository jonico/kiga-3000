-- Migration 01: replace the '0000-00-00' sentinel with NULL, and move to InnoDB.
--
-- Run this against an EXISTING Kindergarten database. For a fresh one, db/schema.sql
-- already has the new shape and this file is unnecessary.
--
-- Why: the application used the literal date '0000-00-00' to mean "no date". MySQL has
-- rejected that since 5.7 unless sql_mode is relaxed, so the JDBC URL carried
--     sessionVariables=sql_mode='ALLOW_INVALID_DATES'
--     zeroDateTimeBehavior=ROUND
-- purely to keep it working. '0000-00-00' also cannot be represented by
-- java.sql.Date, which is what blocked a clean JPA mapping. NULL is what SQL provides
-- for exactly this, needs no relaxed sql_mode, and both workarounds could then be
-- removed from the connection URL.
--
-- Safe to re-run: every step is conditional or idempotent.

USE Kindergarten;

-- The UPDATEs below must be able to READ the old zero dates, so relax sql_mode for
-- this session only. The point of the migration is that nothing needs this afterwards.
SET @old_sql_mode = @@SESSION.sql_mode;
SET SESSION sql_mode = 'ALLOW_INVALID_DATES';

-- 1. Allow NULL before writing any.
ALTER TABLE `Karteikarte`
  MODIFY `lastaccess`   date default NULL,
  MODIFY `kindgeburt`   date default NULL,
  MODIFY `eintritt`     date default NULL,
  MODIFY `austritt`     date default NULL,
  MODIFY `vatergeburt`  date default NULL,
  MODIFY `muttergeburt` date default NULL;

-- 2. Replace the sentinel. Comparing against the string form is deliberate: after the
--    ALTER these columns are nullable but the existing rows still hold '0000-00-00'.
UPDATE `Karteikarte` SET `lastaccess`   = NULL WHERE CAST(`lastaccess`   AS CHAR) = '0000-00-00';
UPDATE `Karteikarte` SET `kindgeburt`   = NULL WHERE CAST(`kindgeburt`   AS CHAR) = '0000-00-00';
UPDATE `Karteikarte` SET `eintritt`     = NULL WHERE CAST(`eintritt`     AS CHAR) = '0000-00-00';
UPDATE `Karteikarte` SET `austritt`     = NULL WHERE CAST(`austritt`     AS CHAR) = '0000-00-00';
UPDATE `Karteikarte` SET `vatergeburt`  = NULL WHERE CAST(`vatergeburt`  AS CHAR) = '0000-00-00';
UPDATE `Karteikarte` SET `muttergeburt` = NULL WHERE CAST(`muttergeburt` AS CHAR) = '0000-00-00';

-- 3. InnoDB, so that the transactions JPA opens are real rather than silently ignored.
ALTER TABLE `Karteikarte` ENGINE=InnoDB;

SET SESSION sql_mode = @old_sql_mode;

-- 4. Verify. Both counts must be zero, and the engine must be InnoDB.
SELECT
  (SELECT COUNT(*) FROM `Karteikarte`
     WHERE CAST(`kindgeburt` AS CHAR) = '0000-00-00'
        OR CAST(`eintritt` AS CHAR) = '0000-00-00'
        OR CAST(`austritt` AS CHAR) = '0000-00-00'
        OR CAST(`vatergeburt` AS CHAR) = '0000-00-00'
        OR CAST(`muttergeburt` AS CHAR) = '0000-00-00') AS remaining_zero_dates,
  (SELECT ENGINE FROM information_schema.TABLES
     WHERE TABLE_SCHEMA = 'Kindergarten' AND TABLE_NAME = 'Karteikarte') AS engine;
