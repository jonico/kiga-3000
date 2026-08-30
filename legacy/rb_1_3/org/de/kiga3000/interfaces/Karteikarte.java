package org.de.kiga3000.interfaces;
/*
 * Created on 24.08.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
/**
 * @author maestro
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferenes&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public interface Karteikarte {
	public static final String austrittDatum = "AustrittsDatum";
	public static final String eintrittDatum = "AustrittsDatum";
	public static final String kindgeburtDatum = "AustrittsDatum";
	public static final String vatergeburtDatum = "AustrittsDatum";
	public static final String muttergeburtDatum = "AustrittsDatum";
	// von diesen Feldern werden keine Daten sichtbar angezeigt
	int getPrimaryKey();
	void setPrimaryKey(int i);
	
	// diese Felder werden angezeigt
	byte getGruppe();
	void setGruppe(byte i);
	String getKindGeburtsDatum();
	void setKindGeburtsDatum(String i) ;
	String getEintrittsDatum();
	void setEintrittsDatum(String i);
	String getEintrittsGrund();
	void setEintrittsGrund(String i);
	String getAustrittsDatum();
	void setAustrittsDatum(String i);
	String getAustrittsGrund();
	void setAustrittsGrund(String i);
	String getKindNachname();
	void setKindNachname(String i);
	String getKindVorname();
	void setKindVorname(String i);
	String getGeburtsOrt();
	void setGeburtsOrt(String i);
	String getKindWohnung();
	void setKindWohnung(String i);
	String getReligion();
	void setReligion(String i);
	String getStaat();
	void setStaat(String i);
	String getKindTelefon();
	void setKindTelefon(String i);
	String getVaterName();
	void setVaterName(String i);
	String getVaterGeburt();
	void setVaterGeburt(String i);
	String getVaterBeruf();
	void setVaterBeruf(String i);
	String getMutterName();
	void setMutterName(String i);
	String getMutterGeburt();
	void setMutterGeburt(String i);
	String getMutterBeruf();
	void setMutterBeruf(String i);
	String getSorgePerson();
	void setSorgePerson(String i);
	String getArbeitsOrt1();
	void setArbeitsOrt1(String i);
	String getArbeitsTelefon1();
	void setArbeitsTelefon1(String i);
	String getArbeitsOrt2();
	void setArbeitsOrt2(String i);
	String getArbeitsTelefon2();
	void setArbeitsTelefon2(String i);
	String getGeschwisterGeburt();
	void setGeschwisterGeburt(String i);
	byte getFamilienStand();
	void setFamilienStand(byte i);
	String getElternOrt();
	void setElternOrt(String i);
	String getElternTelefon();
	void setElternTelefon(String i);
	byte getAnzahlGeschwister();
	void setAnzahlGeschwister(byte i);
	String getImpfungen();
	void setImpfungen(String i);
	String getTetanusZeit();
	void setTetanusZeit(String i);
	short getKrankheiten();
	void setKrankheiten(short i);
	String getWeitereKrankheiten();
	void setWeitereKrankheiten(String i);
	String getGesundheitsHinweise();
	void setGesundheitshinweise(String i);
	String getArztOrt();
	void setArztOrt(String i);
	String getArztTelefon();
	void setArztTelefon(String i);
	String getKrankenkasse();
	void setKrankenkasse(String i);
	String getSonstiges();
	void setSonstiges(String i);
}
