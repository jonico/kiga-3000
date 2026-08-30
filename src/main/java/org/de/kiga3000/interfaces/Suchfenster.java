package org.de.kiga3000.interfaces;

/*
 * Created on 25.08.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author maestro
 * Suchdialog wird dieses Interface implementieren
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Suchfenster {
	/**
	 *  Löscht alle zu sehenden Einträge im Suchfenster
	 */
	public void leeren();
	/**
	 * Fügt einen Datensatz, der auf das Suchkriterium passte dazu
	 * @param id primary key
	 * @param vorname Vorname
	 * @param nachname Nachname
	 * @param geburtsdatum Geburtsdatum
	 * @param anschrift Anschrift des Kindes
	 */
	public void addKind(int id, String vorname, String nachname, String geburtsdatum, String anschrift);
}
