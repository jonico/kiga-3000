package org.de.kiga3000.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

import org.de.kiga3000.conversion.DateConversion;

/**
 * Manual smoke check for the JDBC modernization (Connector/J 3.0.14 -> 26.7.0).
 *
 * <p>Not a unit test - it needs a live MySQL server, so it is deliberately not named
 * *Test and is therefore never picked up by surefire. Run it by hand:
 *
 * <pre>
 * mvn -Dmaven.repo.local=$M2 test-compile
 * java -cp "target/classes:target/test-classes:$(mvn ... dependency:build-classpath)" \
 *      org.de.kiga3000.database.DatabaseConnectivityCheck
 * </pre>
 *
 * <p>It goes through the application's own {@link OpenConnection} -> {@link Ressourcen}
 * -> {@link ConnectionPool} path, so it exercises the real JDBC URL and driver class
 * name, then inserts / reads back / deletes one Karteikarte row. The inserted row uses
 * the application's own zero-date sentinel "0000-00-00", which is what the 2006 date
 * handling in {@link DateConversion} depends on.
 */
public final class DatabaseConnectivityCheck {

	private DatabaseConnectivityCheck() {
	}

	public static void main(String[] args) throws Exception {
		OpenConnection open = new OpenConnection();
		Connection conn = open.openConnection();
		System.out.println("1. connection acquired from Ressourcen/ConnectionPool: " + conn.getClass().getName());
		System.out.println("   driver: " + conn.getMetaData().getDriverName() + " " + conn.getMetaData().getDriverVersion());

		try (Statement s = conn.createStatement(); ResultSet r = s.executeQuery("SELECT VERSION(), @@SESSION.sql_mode")) {
			r.next();
			System.out.println("2. SELECT VERSION() -> " + r.getString(1));
			System.out.println("   session sql_mode -> " + r.getString(2));
		}

		// The application writes "0000-00-00" whenever a date field was left empty.
		DateConversion conv = new DateConversion();
		String leeresDatum = conv.StringToMysqlDate("");
		System.out.println("3. DateConversion.StringToMysqlDate(\"\") -> " + leeresDatum);

		int id;
		try (PreparedStatement p = conn.prepareStatement(
				"INSERT INTO `Karteikarte` (`gruppe`, `lastaccess`, `kindgeburt`, `eintritt`, `austritt`,"
						+ " `kindnachname`, `kindvorname`) VALUES (?, CURDATE(), ?, ?, ?, ?, ?)",
				Statement.RETURN_GENERATED_KEYS)) {
			p.setByte(1, (byte) 7);
			p.setString(2, "2021-03-01");
			p.setString(3, leeresDatum);
			p.setString(4, leeresDatum);
			p.setString(5, "Konnektivitaetstest");
			p.setString(6, "Smoke");
			p.executeUpdate();
			try (ResultSet keys = p.getGeneratedKeys()) {
				keys.next();
				id = keys.getInt(1);
			}
		}
		System.out.println("4. INSERT INTO Karteikarte -> new id " + id);

		try (PreparedStatement p = conn.prepareStatement(
				"SELECT `id`, `gruppe`, `kindgeburt`, `eintritt`, `austritt`, `kindnachname`, `kindvorname`"
						+ " FROM `Karteikarte` WHERE `id` = ?")) {
			p.setInt(1, id);
			try (ResultSet r = p.executeQuery()) {
				r.next();
				System.out.println("5. read back: id=" + r.getInt(1)
						+ " gruppe=" + r.getByte(2)
						+ " kindgeburt(getDate)=" + r.getDate(3)
						+ " kindgeburt(SQLHelfer)=" + SQLHelfer.getStringausDatum(r.getDate(3))
						+ " eintritt(getString)=" + r.getString(4)
						+ " austritt(getString)=" + r.getString(5)
						+ " name=" + r.getString(7) + " " + r.getString(6));
				System.out.println("   DateConversion.StringToLocalDate(austritt) -> \""
						+ conv.StringToLocalDate(r.getString(5)) + "\" (zero date still recognised)");
			}
		}

		try (PreparedStatement p = conn.prepareStatement("DELETE FROM `Karteikarte` WHERE `id` = ?")) {
			p.setInt(1, id);
			System.out.println("6. DELETE -> " + p.executeUpdate() + " row(s) removed");
		}

		try (Statement s = conn.createStatement(); ResultSet r = s.executeQuery("SELECT COUNT(*) FROM `Karteikarte`")) {
			r.next();
			System.out.println("7. rows left in Karteikarte: " + r.getInt(1));
		}

		open.releaseConnection(conn);
		System.out.println("OK");
		System.exit(0);
	}
}
