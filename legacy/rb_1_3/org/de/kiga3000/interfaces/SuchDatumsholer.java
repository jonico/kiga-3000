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
public interface SuchDatumsholer extends SuchDatenholer {
	/**
	 *  Setzt Geburts - Zeitraum, in dem Kinder der zugelassenen Gruppen gesucht werden
	 * @param anfang AnfangsDatum
	 * @param ende EndeDatum
	 * @param anfangsgruppe kleinste Gruppe, die angezeigt wird
	 * @param endgruppe größte Gruppe, die angezeigt wird
	 */
	public void setDatum(String anfang, String ende, byte anfangsgruppe, byte endgruppe) throws SQLException, KigaException;
}
