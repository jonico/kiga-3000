package org.de.kiga3000.database;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.interfaces.SuchDatenholer;
import org.de.kiga3000.interfaces.Suchfenster;
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

/* added internationalization by bdiemer 2005-11-30 */

public abstract class SuchDatenholerSQLImpl implements SuchDatenholer {

	private boolean dataavailable=false;
	private ResultSet r;
	private static Messenger messenger = new Messenger();
	/**
	 * 
	 */
	public SuchDatenholerSQLImpl() {
		super();
	}
	
	/**
	 * 
	 * @return prepared statement form subclass that is already "filled" with the right values
	 */
	public abstract PreparedStatement getPreparedStatement();

	/* (non-Javadoc)
	 * @see SuchDatenholer#holeDaten()
	 */
	public boolean holeDaten() throws SQLException, KigaException{
		try {
			r=getPreparedStatement().executeQuery();
		}
		catch (Exception e) {
			r=getPreparedStatement().executeQuery();
		}
		dataavailable=r.next();
		return dataavailable;
	}

	/* (non-Javadoc)
	 * @see SuchDatenholer#fill(Suchfenster)
	 */
	public void fill(Suchfenster i) throws SQLException, KigaException {
		if (!dataavailable)
			throw new KigaException(messenger.getMessage("KiGa.NonExistendData"));
		dataavailable=false;
		do {
			i.addKind(r.getInt(1),r.getString(2),r.getString(3),SQLHelfer.getStringausDatum(r.getDate(4)),r.getString(5));
		} while (r.next());
	}

}
