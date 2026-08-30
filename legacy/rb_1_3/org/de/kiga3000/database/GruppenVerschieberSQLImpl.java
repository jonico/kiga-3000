package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.interfaces.Gruppenverschieber;
import org.de.kiga3000.messages.Messenger;

/*
 * Created on 26.08.2004
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
/**
 * internationlized messaging added by bdiemer 2005-11-28
 */
public class GruppenVerschieberSQLImpl implements Gruppenverschieber {
//public class GruppenVerschieberSQLImpl {

	
//	private Connection conn;
	private PreparedStatement pst;

	private static Messenger messenger = new Messenger();
	
	 public GruppenVerschieberSQLImpl() {
//	 	conn=Ressourcen.getConnection(5000);
//	 	conn.setAutoCommit(true);
	 }
	 
	/* (non-Javadoc)
		 * @see Gruppenverschieber#swapGroups(int, int, int)
	 */
	public void swapGroups(byte i, byte j, byte k,Connection conn)  throws KigaException, SQLException {
	 	pst=conn.prepareStatement("UPDATE Karteikarte SET gruppe= ? , lastaccess = CURDATE() WHERE gruppe= ?");
		if (i==j || i==k || j==k)
			throw new KigaException(messenger.getMessage("KiGa.Verschieb"));
		// Gruppe i auf Abstellplatz verschieben	
			pst.setByte(1,k);
			pst.setByte(2,i);
			try {
				pst.executeUpdate();
			}
			catch (Exception e) {
				pst.executeUpdate();
			}
			
			// Gruppe j auf i schieben
			pst.setByte(1,i);
			pst.setByte(2,j);
			pst.executeUpdate();
			
			// Gruppe von Abstellplatz auf j schieben
			pst.setByte(1,j);
			pst.setByte(2,k);
			pst.executeUpdate();
		
		// Komplette Transaktion lief in Ordnung ab, commit rollback wäre einfach zu teuer	
	}
}
