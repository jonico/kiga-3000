#!/bin/bash
# Restore the Kindergarten database to the exact pre-arm state, so that arm A and
# arm B each start against byte-identical data.
set -euo pipefail
M=/opt/homebrew/opt/mysql/bin/mysql
BASE="$HOME/kiga3000-work/baseline/db"

"$M" -u root <<'SQL'
DROP DATABASE IF EXISTS Kindergarten;
CREATE DATABASE Kindergarten CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
DROP USER IF EXISTS 'KiGa'@'localhost';
CREATE USER 'KiGa'@'localhost' IDENTIFIED BY 'Kiga3000';
GRANT ALL PRIVILEGES ON Kindergarten.* TO 'KiGa'@'localhost';
FLUSH PRIVILEGES;
SQL

"$M" -u root < "$BASE/schema.sql"
"$M" -u root < "$BASE/seed.sql"
echo "DB reset: $("$M" -u root -N -e 'SELECT COUNT(*) FROM Kindergarten.Karteikarte;') rows, user KiGa recreated"
