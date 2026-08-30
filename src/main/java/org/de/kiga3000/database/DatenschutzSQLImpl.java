package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.Datenschutz;

/*
 * Created on 05.09.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author maestro
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DatenschutzSQLImpl implements Datenschutz {

	/* (non-Javadoc)
	 * @see Datenschutz#entferneAlteDaten(short)
	 *  Dedaline hier: 5 Jahre
	 */

	public void entferneAlteDaten(byte gruppe)
		throws SQLException, KigaException, PoolException  {
			Connection conn=Ressourcen.getConnection(5000);
			conn.setAutoCommit(true);
			Statement st= conn.createStatement();
			st.executeUpdate("DELETE FROM Karteikarte WHERE gruppe =  "+gruppe+" AND lastaccess <=  DATE_SUB(CURDATE(),INTERVAL 5 YEAR) ");
			st.close();
			Ressourcen.releaseConnection(conn);
	}

}
