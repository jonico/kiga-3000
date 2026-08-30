package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.SuchNamenholer;
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
public class SuchNamenholerSQLImpl
	extends SuchDatenholerSQLImpl
	implements SuchNamenholer {

	private Connection conn;
	private PreparedStatement p;
	private static Messenger messenger = new Messenger();
			
	/**
	 * 
	 */
	public SuchNamenholerSQLImpl() throws PoolException, SQLException {
		super();
		conn=Ressourcen.getConnection(5000);
		conn.setAutoCommit(true);
		p=conn.prepareStatement("SELECT id, kindvorname, kindnachname, kindgeburt, kindwohnung FROM `Karteikarte` WHERE kindnachname = ? AND gruppe BETWEEN ? AND ? ORDER BY kindgeburt ASC");
	}	

	/* (non-Javadoc)
	 * @see SuchDatenholerSQLImpl#getPreparedStatement()
	 */
	public PreparedStatement getPreparedStatement() {
		return p;
	}

	/* (non-Javadoc)
	 * @see SuchNamenholer#setNachnamen(java.lang.String, short, short)
	 */
	public void setNachnamen(
		String nachname,
		byte gruppenanfang,
		byte gruppenende)
		throws SQLException, KigaException {
		if (gruppenanfang>gruppenende || 1>gruppenanfang)
			throw new KigaException(messenger.getMessage("KiGa.WrongGroups"));
		p.setString(1,nachname);
		p.setByte(2, gruppenanfang);
		p.setByte(3, gruppenende);	
	}

}
