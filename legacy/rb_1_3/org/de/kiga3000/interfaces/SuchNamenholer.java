package org.de.kiga3000.interfaces;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;

/*
 * Created on 25.08.2004
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
public interface SuchNamenholer extends SuchDatenholer {
	/**
	 * Initialisiert die Suchabfrage, welche nach dem Nachnamen sucht
	 * @param nachname Nachname
	 * @param gruppenanfang erste Gruppe, in der gesucht werden soll
	 * @param gruppenende letzte Gruppe, in der gesucht werden soll
	 */
	public void setNachnamen(String nachname, byte gruppenanfang, byte gruppenende) throws SQLException, KigaException;
}
