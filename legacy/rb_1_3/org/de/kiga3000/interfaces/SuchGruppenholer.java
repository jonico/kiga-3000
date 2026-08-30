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
public interface SuchGruppenholer extends SuchDatenholer {
	/**
	 * Setzt die Gruppe, deren Kinder zurückgegeben werden sollen
	 * @param i Gruppennumer
	 */
	void setGruppe(byte i) throws SQLException, KigaException;
}
