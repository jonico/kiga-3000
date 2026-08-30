package org.de.kiga3000.interfaces;
import java.sql.Connection;
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
 * Diesesd interface muss die Klasse impelemntieren, die zei Gruppen vertauscht
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Gruppenverschieber {
	/**
	 * Vertauscht die beiden Gruppen miteinander 
	 * @param i Gruppe 1 (1-8) 
	 * @param j Gruppe 2 (1-8)
	 * @param k Bezeichnet swap Platz (Zwischenspeicher für eine Gruppe)
	 */
	public void swapGroups(byte i, byte j, byte k, Connection conn) throws KigaException, SQLException;
}
