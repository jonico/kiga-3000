package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.SuchDatumsholer;
import org.de.kiga3000.messages.Messenger;

/*
 * Created on 07.09.2004
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

public class SuchDatumsholerSQLImpl
	extends SuchDatenholerSQLImpl
	implements SuchDatumsholer {

		private Connection conn;
		private PreparedStatement p;
		private static Messenger messenger = new Messenger();
			/**
			 * 
			 */
		public SuchDatumsholerSQLImpl() throws PoolException, SQLException {
				super();
				conn=Ressourcen.getConnection(5000);
				conn.setAutoCommit(true);
				p=conn.prepareStatement("SELECT id, kindvorname, kindnachname, kindgeburt, kindwohnung FROM `Karteikarte` WHERE  kindgeburt BETWEEN ? AND ? AND gruppe BETWEEN ? AND ? ORDER BY kindgeburt ASC");
			}

	/* (non-Javadoc)
	 * @see SuchDatenholerSQLImpl#getPreparedStatement()
	 */
	public PreparedStatement getPreparedStatement() {
		return p;
	}

	/* (non-Javadoc)
	 * @see SuchDatumsholer#setDatum(java.sql.Date, java.sql.Date, int, int)
	 */
	public void setDatum(
		String anfang,
		String ende,
		byte anfangsgruppe,
		byte endgruppe)
		throws SQLException, KigaException {
			Date danfang, dende;
			// exception muss gefangen werden, da sie sonst den falschen Grund liefern würde
			try {
				danfang=SQLHelfer.getDatumausString(anfang);
			}
			catch (KigaException e)  {
				throw new KigaException(messenger.getMessage("KiGa.StartDateWrongFormat"));
			}
			try {
				dende=SQLHelfer.getDatumausString(ende);
			}
			catch (KigaException e)  {
							throw new KigaException(messenger.getMessage("KiGa.EndDateWrongFormat"));
			}
			if (anfangsgruppe>endgruppe || 1>anfangsgruppe)
						throw new KigaException(messenger.getMessage("KiGa.WrongGroups"));
			p.setDate(1,danfang);
			p.setDate(2,dende);
			p.setByte(3,anfangsgruppe);
			p.setByte(4,endgruppe);
	}

}
