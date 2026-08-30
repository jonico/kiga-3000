package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.SuchGruppenholer;
import org.de.kiga3000.messages.Messenger;

/*
 * Created on 06.09.2004
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
/*added internationalization by bdiemer 2005-30-11 */
public class SuchGruppenholerSQLImpl extends SuchDatenholerSQLImpl  implements SuchGruppenholer {

	private Connection conn;
	private PreparedStatement p;
	private static Messenger messenger = new Messenger();
	/**
	 * 
	 */
	public SuchGruppenholerSQLImpl() throws PoolException, SQLException {
		super();
		conn=Ressourcen.getConnection(5000);
		conn.setAutoCommit(true);
		p=conn.prepareStatement("SELECT id, kindvorname, kindnachname, kindgeburt, kindwohnung FROM `Karteikarte` WHERE gruppe = ? ORDER BY kindgeburt ASC");
	}

	/* (non-Javadoc)
	 * @see SuchDatenholerSQLImpl#getPreparedStatement()
	 */
	public PreparedStatement getPreparedStatement() {
		return p;
	}

	/* (non-Javadoc)
	 * @see SuchGruppenholer#setGruppe(short)
	 */
	public void setGruppe(byte i) throws SQLException, KigaException {
		if (i<1)
			throw new KigaException(messenger.getMessage("KiGa.WroungGoupNumber"));
		p.setByte(1,i);
	}

}
