/**
 * kiga3000 org.de.kiga3000.data KarteikarteImpl.java
 * 02.12.2005
 */
package org.de.kiga3000.data;

import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.sorting.KarteiKarteComparator;

/**
 * @author bobohead2
 * <br>this class provides database container for kids data
 */
public class KarteikarteImpl implements Karteikarte, Comparable {
	// number of siblings
	 private byte anzahlGeschwister;
	 // city of parents work1
	 private String arbeitsOrt1;
	 // city of parents work 2
	 private String arbeitsOrt2;
	 // phone number work1
	 private String arbeitsTelefon1;
	// phone number work2
	 private String arbeitsTelefon2;
	 // city of family doc
	 private String arztOrt;
	 // phone number of doc
	 private String arztTelefon;
	 // leaving date
	 private String austrittsDatum;
	 // cause why leaving
	 private String austrittsGrund;
	 // entry date
	 private String eintrittsDatum;
	 // reason why joined
	 private String eintrittsGrund;
	 // city of parents
	 private String elternOrt;
	 // parents phone number
	 private String elternTelefon;
	 // family situation
	 private byte familienStand;
	 // place of birth
	 private String geburtsOrt;
	 // birthdays of siblings
	 private String geschwisterGeburt;
	 // health informations
	 private String gesundheitsHinweise;
	 // group
	 private byte gruppe;
	 // 
	 private String impfungen;
	 // birthday of child
	 private String kindGeburtsDatum;
	 // childs last name
	 private String kindNachname;
	 // childs phone number
	 private String kindTelefon;
	 // childs first name
	 private String kindVorname;
	 // childs adress
	 private String kindWohnung;
	 // health insurance
	 private String krankenkasse;
	 // suffered illnesses
	 private short krankheiten;
	 // moters profession
	 private String mutterBeruf;
	 // mothers birtday
	 private String mutterGeburt;
	 // mothers name
	 private String mutterName;
	 // primary key
	 private int primaryKey;
	 // childs religion
	 private String religion;
	 // additional information
	 private String sonstiges;
	 // authorized care givers
	 private String sorgePerson;
	 // childs nationality
	 private String staat;
	 // last tetanus shot
	 private String tetanusZeit;
	 // fathers profession
	 private String vaterBeruf;
	 // fathers birthday
	 private String vaterGeburt;
	 // fathers name
	 private String vaterName;
	 // additional illnesses
	 private String weitereKrankheiten;
	 
	 
	 /* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		KarteiKarteComparator comp = new KarteiKarteComparator();
		comp.compare(this,arg0);
		// TODO Auto-generated method stub
		return 0;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getAnzahlGeschwister()
	 */
	public byte getAnzahlGeschwister() {
		return anzahlGeschwister;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getArbeitsOrt1()
	 */
	public String getArbeitsOrt1() {
		return arbeitsOrt1;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getArbeitsOrt2()
	 */
	public String getArbeitsOrt2() {
		return arbeitsOrt2;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getArbeitsTelefon1()
	 */
	public String getArbeitsTelefon1() {
		return arbeitsTelefon1;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getArbeitsTelefon2()
	 */
	public String getArbeitsTelefon2() {
		return arbeitsTelefon2;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getArztOrt()
	 */
	public String getArztOrt() {
		return arztOrt;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getArztTelefon()
	 */
	public String getArztTelefon() {
		return arztTelefon;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getAustrittsDatum()
	 */
	public String getAustrittsDatum() {
		return austrittsDatum;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getAustrittsGrund()
	 */
	public String getAustrittsGrund() {
		return austrittsGrund;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getEintrittsDatum()
	 */
	public String getEintrittsDatum() {
		return eintrittsDatum;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getEintrittsGrund()
	 */
	public String getEintrittsGrund() {
		return eintrittsGrund;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getElternOrt()
	 */
	public String getElternOrt() {
		return elternOrt;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getElternTelefon()
	 */
	public String getElternTelefon() {
		return elternTelefon;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getFamilienStand()
	 */
	public byte getFamilienStand() {
		return familienStand;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getGeburtsOrt()
	 */
	public String getGeburtsOrt() {
		return geburtsOrt;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getGeschwisterGeburt()
	 */
	public String getGeschwisterGeburt() {
		return geschwisterGeburt;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getGesundheitsHinweise()
	 */
	public String getGesundheitsHinweise() {
		return gesundheitsHinweise;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getGruppe()
	 */
	public byte getGruppe() {
		return gruppe;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getImpfungen()
	 */
	public String getImpfungen() {
		return impfungen;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getKindGeburtsDatum()
	 */
	public String getKindGeburtsDatum() {
		return kindGeburtsDatum;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getKindNachname()
	 */
	public String getKindNachname() {
		return kindNachname;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getKindTelefon()
	 */
	public String getKindTelefon() {
		return kindTelefon;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getKindVorname()
	 */
	public String getKindVorname() {
		return kindVorname;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getKindWohnung()
	 */
	public String getKindWohnung() {
		return kindWohnung;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getKrankenkasse()
	 */
	public String getKrankenkasse() {
		return krankenkasse;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getKrankheiten()
	 */
	public short getKrankheiten() {
		return krankheiten;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getMutterBeruf()
	 */
	public String getMutterBeruf() {
		return mutterBeruf;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getMutterGeburt()
	 */
	public String getMutterGeburt() {
		return mutterGeburt;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getMutterName()
	 */
	public String getMutterName() {
		return mutterName;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getPrimaryKey()
	 */
	public int getPrimaryKey() {
		return primaryKey;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getReligion()
	 */
	public String getReligion() {
		return religion;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getSonstiges()
	 */
	public String getSonstiges() {
		return sonstiges;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getSorgePerson()
	 */
	public String getSorgePerson() {
		return sorgePerson;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getStaat()
	 */
	public String getStaat() {
		return staat;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getTetanusZeit()
	 */
	public String getTetanusZeit() {
		return tetanusZeit;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getVaterBeruf()
	 */
	public String getVaterBeruf() {
		return vaterBeruf;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getVaterGeburt()
	 */
	public String getVaterGeburt() {
		return vaterGeburt;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getVaterName()
	 */
	public String getVaterName() {
		return vaterName;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#getWeitereKrankheiten()
	 */
	public String getWeitereKrankheiten() {
		return weitereKrankheiten;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setAnzahlGeschwister(byte)
	 */
	public void setAnzahlGeschwister(byte i) {
		 anzahlGeschwister = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setArbeitsOrt1(java.lang.String)
	 */
	public void setArbeitsOrt1(String i) {
        arbeitsOrt1 = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setArbeitsOrt2(java.lang.String)
	 */
	public void setArbeitsOrt2(String i) {
         arbeitsOrt2 = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setArbeitsTelefon1(java.lang.String)
	 */
	public void setArbeitsTelefon1(String i) {
		arbeitsTelefon1 = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setArbeitsTelefon2(java.lang.String)
	 */
	public void setArbeitsTelefon2(String i) {
        arbeitsTelefon2 = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setArztOrt(java.lang.String)
	 */
	public void setArztOrt(String i) {
		arztOrt = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setArztTelefon(java.lang.String)
	 */
	public void setArztTelefon(String i) {
         arztTelefon = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setAustrittsDatum(java.lang.String)
	 */
	public void setAustrittsDatum(String i) {
		austrittsDatum = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setAustrittsGrund(java.lang.String)
	 */
	public void setAustrittsGrund(String i) {
		austrittsGrund = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setEintrittsDatum(java.lang.String)
	 */
	public void setEintrittsDatum(String i) {
		eintrittsDatum = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setEintrittsGrund(java.lang.String)
	 */
	public void setEintrittsGrund(String i) {
	    eintrittsGrund = i;	
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setElternOrt(java.lang.String)
	 */
	public void setElternOrt(String i) {
	   elternOrt = i;	
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setElternTelefon(java.lang.String)
	 */
	public void setElternTelefon(String i) {
		elternTelefon = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setFamilienStand(byte)
	 */
	public void setFamilienStand(byte i) {
	     familienStand = i;	
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setGeburtsOrt(java.lang.String)
	 */
	public void setGeburtsOrt(String i) {
		geburtsOrt = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setGeschwisterGeburt(java.lang.String)
	 */
	public void setGeschwisterGeburt(String i) {
		geschwisterGeburt = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setGesundheitshinweise(java.lang.String)
	 */
	public void setGesundheitshinweise(String i) {
	    gesundheitsHinweise = i;	
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setGruppe(byte)
	 */
	public void setGruppe(byte i) {
		gruppe = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setImpfungen(java.lang.String)
	 */
	public void setImpfungen(String i) {
		impfungen = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setKindGeburtsDatum(java.lang.String)
	 */
	public void setKindGeburtsDatum(String i) {
		kindGeburtsDatum = i;
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setKindNachname(java.lang.String)
	 */
	public void setKindNachname(String i) {
	     kindNachname = i;	
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setKindTelefon(java.lang.String)
	 */
	public void setKindTelefon(String i) {
         kindTelefon = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setKindVorname(java.lang.String)
	 */
	public void setKindVorname(String i) {
          kindVorname = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setKindWohnung(java.lang.String)
	 */
	public void setKindWohnung(String i) {
          kindWohnung = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setKrankenkasse(java.lang.String)
	 */
	public void setKrankenkasse(String i) {
         krankenkasse = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setKrankheiten(short)
	 */
	public void setKrankheiten(short i) {
          krankheiten = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setMutterBeruf(java.lang.String)
	 */
	public void setMutterBeruf(String i) {
          mutterBeruf = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setMutterGeburt(java.lang.String)
	 */
	public void setMutterGeburt(String i) {
        mutterGeburt = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setMutterName(java.lang.String)
	 */
	public void setMutterName(String i) {
          mutterName = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setPrimaryKey(int)
	 */
	public void setPrimaryKey(int i) {
         primaryKey = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setReligion(java.lang.String)
	 */
	public void setReligion(String i) {
          religion = i;  		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setSonstiges(java.lang.String)
	 */
	public void setSonstiges(String i) {
          sonstiges = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setSorgePerson(java.lang.String)
	 */
	public void setSorgePerson(String i) {
          sorgePerson = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setStaat(java.lang.String)
	 */
	public void setStaat(String i) {
         staat = i; 		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setTetanusZeit(java.lang.String)
	 */
	public void setTetanusZeit(String i) {
        tetanusZeit = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setVaterBeruf(java.lang.String)
	 */
	public void setVaterBeruf(String i) {
           vaterBeruf = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setVaterGeburt(java.lang.String)
	 */
	public void setVaterGeburt(String i) {
         vaterGeburt = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setVaterName(java.lang.String)
	 */
	public void setVaterName(String i) {
        vaterName = i;		
	}

	/* (non-Javadoc)
	 * @see org.de.kiga3000.interfaces.Karteikarte#setWeitereKrankheiten(java.lang.String)
	 */
	public void setWeitereKrankheiten(String i) {
          weitereKrankheiten = i;		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	public int hashCode() {
		// TODO Auto-generated method stub
		return super.hashCode();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	protected void finalize() throws Throwable {
		// TODO Auto-generated method stub
		super.finalize();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	protected Object clone() throws CloneNotSupportedException {
		// TODO Auto-generated method stub
		return super.clone();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object arg0) {
		// TODO Auto-generated method stub
		return super.equals(arg0);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		// TODO Auto-generated method stub
		return kindNachname.toString();
	}

}
