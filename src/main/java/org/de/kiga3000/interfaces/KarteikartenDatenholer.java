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
public interface KarteikartenDatenholer {
	/**
	 * Führt eine Suchabfrage mit den erforderlichen Parametern durch
	 * @return true, falls Datensatz bezüglich des primary key vorhanden war
	 * @param i primary key
	 */
	public boolean holeDaten(int i); 
	/**
	 *  @param i In dieses Karteikarte werden die zuletzt gefundenen Datensätze eingetragen
	 *
	 */
	public void fill(Karteikarte i);
}
