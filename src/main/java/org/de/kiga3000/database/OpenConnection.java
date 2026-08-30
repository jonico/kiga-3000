/**
 * 
 */
package org.de.kiga3000.database;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.de.kiga3000.exception.PoolException;

/**
 * @author bobohead
 * 
 */
public class OpenConnection {
	private Connection conn;

	public Connection openConnection() throws PoolException, SQLException {
		conn = Ressourcen.getConnection(5000);
		conn.setAutoCommit(true);
		return conn;
	}

	public void releaseConnection(Connection conn) {
		Ressourcen.releaseConnection(conn);
	}
}
