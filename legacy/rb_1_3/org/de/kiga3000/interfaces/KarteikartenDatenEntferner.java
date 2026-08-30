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
 * Dieses Interface stellt die Möglichkeit zur Verfügung, ein Kind in eine gelöschte Gruppe zu verschieben,
 * bis es wegen Datenschutzgründen richtig gelöscht wird 
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface KarteikartenDatenEntferner {
	/**
	 * 
	 * @param i Karteikarte, aus der die Daten entnommen wurden
	 * @param j Gruppe, welche als Löschplatz fungierenm soll
	 */
	public void deleteKarteikarte(int primaryKey, byte j);
}
