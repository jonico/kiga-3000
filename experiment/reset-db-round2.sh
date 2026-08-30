#!/bin/bash
# Restore the Kindergarten database to the round-2 starting state.
#
# NOT the same as reset-db.sh, which restores the round-1 baseline: that schema still
# has DATE NOT NULL DEFAULT '0000-00-00' and MyISAM. Round 2 starts from the current
# repository schema - nullable dates and InnoDB - because the JPA mapping depends on
# both.
set -euo pipefail
M=/opt/homebrew/opt/mysql/bin/mysql
DB="$HOME/kiga3000-reloaded/db"

"$M" -u root <<'SQL'
DROP DATABASE IF EXISTS Kindergarten;
CREATE DATABASE Kindergarten CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
DROP USER IF EXISTS 'KiGa'@'localhost';
CREATE USER 'KiGa'@'localhost' IDENTIFIED BY 'Kiga3000';
GRANT ALL PRIVILEGES ON Kindergarten.* TO 'KiGa'@'localhost';
FLUSH PRIVILEGES;
SQL

"$M" -u root < "$DB/schema.sql"
"$M" -u root < "$DB/seed.sql"

ENGINE=$("$M" -u root -N -e "SELECT ENGINE FROM information_schema.TABLES WHERE TABLE_SCHEMA='Kindergarten' AND TABLE_NAME='Karteikarte';")
NULLABLE=$("$M" -u root -N -e "SELECT COUNT(*) FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='Kindergarten' AND TABLE_NAME='Karteikarte' AND COLUMN_NAME IN ('kindgeburt','eintritt','austritt','vatergeburt','muttergeburt') AND IS_NULLABLE='YES';")
ROWS=$("$M" -u root -N -e "SELECT COUNT(*) FROM Kindergarten.Karteikarte;")
echo "DB reset: $ROWS rows, engine=$ENGINE, nullable date columns=$NULLABLE/5"
