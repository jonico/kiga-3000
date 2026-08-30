package org.de.kiga3000.interfaces;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;

/*
 * Created on 25.08.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author maestro
 * Dieses Modul erledigt alle datenschutzrechtlichen Fragen
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Datenschutz {
	/**
	 * 
	 *  Die Deadline wird in der Implementierung gesetzt
	 * @param gruppe Gruppe, in der zeitweise gelöschte Kinder untergebracht werden
	 */
	
	public void entferneAlteDaten(byte gruppe ) throws SQLException, KigaException, PoolException;
}
