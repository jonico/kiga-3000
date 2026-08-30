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
 * Jede konkrete Suchabfrage wird von Datenholer abgeleitet sein
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface SuchDatenholer {
	/**
	 * Führt eine Suchabfrage mit den erforderlichen Parametern durch
	 * @return true, falls Datensätze bezüglich des Kriteriums vorhanden waren
	 */
	public boolean holeDaten() throws SQLException, KigaException; 
	
	/**
	 *  @param i In dieses Fenster werden die zuletzt gefundenen Datensätze eingetragen
	 */
	public void fill(Suchfenster i) throws SQLException, KigaException;
}
