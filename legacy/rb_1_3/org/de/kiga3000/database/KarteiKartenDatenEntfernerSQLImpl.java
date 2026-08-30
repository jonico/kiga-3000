package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.KarteikartenDatenEntferner;
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
public class KarteiKartenDatenEntfernerSQLImpl{
//	implements KarteikartenDatenEntferner {

//	private Connection conn;
	private PreparedStatement pst;
	
	private static Messenger messenger = new Messenger();

//	public KarteiKartenDatenEntfernerSQLImpl() throws PoolException, SQLException {
//		conn=Ressourcen.getConnection(5000);
//		conn.setAutoCommit(true);
//		pst=conn.prepareStatement("UPDATE Karteikarte SET gruppe= ? ,  lastaccess = CURDATE() WHERE id= ?");
//	}

	/* (non-Javadoc)
	 * @see KarteikartenDatenEntferner#deleteKarteikarte(Karteikarte, int)
	 */
	public void deleteKarteikarte(int i, byte j,Connection conn) throws SQLException, KigaException {
		if (i<0)
			throw new KigaException(messenger.getMessage("KiGa.NoChild"));
		pst=conn.prepareStatement("UPDATE Karteikarte SET gruppe= ? ,  lastaccess = CURDATE() WHERE id= ?");
		pst.setByte(1,j);
		pst.setInt(2,i);
		try {
			pst.executeUpdate();
		}
		catch (Exception e) {
			pst.executeUpdate();
		}
	}

}
