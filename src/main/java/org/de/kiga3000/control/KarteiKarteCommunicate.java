/*
 * Created on 26.01.2006
 *
 */
package org.de.kiga3000.control;

import java.text.ParseException;

import org.de.kiga3000.check.ErrorProtocoll;
import org.de.kiga3000.conversion.DateConversion;
import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.messages.Messenger;

/**
 * @author bobo_local
 *
 *  realizes the communication beetween controller and view 
 */
public class KarteiKarteCommunicate {

	/*
	 * Utility to convert input date to database date and
	 * vise versa
	 */ 
	private DateConversion convDate = new DateConversion();
   
	/*
	 *  Error Protocoll object
	 */
	private ErrorProtocoll err = new ErrorProtocoll();

    /*
     * Message Object which delivers internationalized messages
     */	
	private static Messenger messenger = new Messenger();

	/**
	 * transfers data from data container to view
	 * @param view {@link org.de.kiga3000.interfaces.Karteikarte} communiocation interface
	 * @param karteiIn {@link org.de.kiga3000.data.KarteikarteImpl} data container
	 * @throws ParseException Exception thrown by data conversion
	 */
	public void transferKarteiToView(Karteikarte view, KarteikarteImpl karteiIn)
			throws ParseException {
		// sets siblings count
		view.setAnzahlGeschwister(karteiIn.getAnzahlGeschwister());
	    // city1  parent works
		view.setArbeitsOrt1(karteiIn.getArbeitsOrt1());
		// city2 parent works
		view.setArbeitsOrt2(karteiIn.getArbeitsOrt2());
		// telephone1 at work
		view.setArbeitsTelefon1(karteiIn.getArbeitsTelefon1());
		// telephone2 at work
		view.setArbeitsTelefon2(karteiIn.getArbeitsTelefon2());
		// city of doctor
		view.setArztOrt(karteiIn.getArztOrt());
		// telephone of doctor
		view.setArztTelefon(karteiIn.getArztTelefon());
		// reason for unparticipating
		view.setAustrittsGrund(karteiIn.getAustrittsGrund());
        // reason for participating
		view.setEintrittsGrund(karteiIn.getEintrittsGrund());
		// parents city
		view.setElternOrt(karteiIn.getElternOrt());
		// parents private telephone
		view.setElternTelefon(karteiIn.getElternTelefon());
		// are parents married, divorced, single ...
		view.setFamilienStand(karteiIn.getFamilienStand());
		// place of birth
		view.setGeburtsOrt(karteiIn.getGeburtsOrt());
		// birth dates of siblings
		view.setGeschwisterGeburt(karteiIn.getGeschwisterGeburt());
		// special healt remarks
		view.setGesundheitshinweise(karteiIn.getGesundheitsHinweise());
		// group
		view.setGruppe(karteiIn.getGruppe());
		// Jaundice
		view.setImpfungen(karteiIn.getImpfungen());
		// kids last name
		view.setKindNachname(karteiIn.getKindNachname());
		// kids phone number 
		view.setKindTelefon(karteiIn.getKindTelefon());
		// kids first name
		view.setKindVorname(karteiIn.getKindVorname());
		// kids adress
		view.setKindWohnung(karteiIn.getKindWohnung());
		// health insurance
		view.setKrankenkasse(karteiIn.getKrankenkasse());
		// illnesses
		view.setKrankheiten(karteiIn.getKrankheiten());
		// mothers profession
		view.setMutterBeruf(karteiIn.getMutterBeruf());
		// mothers name
		view.setMutterName(karteiIn.getMutterName());
		// primary key
		view.setPrimaryKey(karteiIn.getPrimaryKey());
		// kids religion
		view.setReligion(karteiIn.getReligion());
		// further information
		view.setSonstiges(karteiIn.getSonstiges());
		// authorized caregivers
		view.setSorgePerson(karteiIn.getSorgePerson());
		// nationality
		view.setStaat(karteiIn.getStaat());
		// last tetanus shot
		view.setTetanusZeit(karteiIn.getTetanusZeit());
		// fathers profession
		view.setVaterBeruf(karteiIn.getVaterBeruf());
		// fathers name
		view.setVaterName(karteiIn.getVaterName());
		// further not listed illnesses
		view.setWeitereKrankheiten(karteiIn.getWeitereKrankheiten());
		// date when left 
		view.setAustrittsDatum(convDate.StringToLocalDate(karteiIn
				.getAustrittsDatum()));
		// date begin 
		view.setEintrittsDatum(convDate.StringToLocalDate(karteiIn
				.getEintrittsDatum()));
		// kids birthday
		view.setKindGeburtsDatum(convDate.StringToLocalDate(karteiIn
				.getKindGeburtsDatum()));
		// mothers birthday
		view.setMutterGeburt(convDate.StringToLocalDate(karteiIn
				.getMutterGeburt()));
		// fathers birth day
		view.setVaterGeburt(convDate.StringToLocalDate(karteiIn
				.getVaterGeburt()));
	}

	/**
	 * transfers data from view to data container
	 * @param view {@link org.de.kiga3000.interfaces.Karteikarte} communiocation interface
	 * @param karteiOut {@link org.de.kiga3000.data.KarteikarteImpl} data container
	 * @throws ParseException Exception thrown by data conversion
	 */
	public ErrorProtocoll transferViewToKartei(Karteikarte view,
			KarteikarteImpl karteiOut) throws ParseException {
		err.clear();
		karteiOut.setAnzahlGeschwister(view.getAnzahlGeschwister());
		karteiOut.setArbeitsOrt1(view.getArbeitsOrt1());
		karteiOut.setArbeitsOrt2(view.getArbeitsOrt2());
		karteiOut.setArbeitsTelefon1(view.getArbeitsTelefon1());
		karteiOut.setArbeitsTelefon2(view.getArbeitsTelefon2());
		karteiOut.setArztOrt(view.getArztOrt());
		karteiOut.setArztTelefon(view.getArztTelefon());
		karteiOut.setAustrittsGrund(view.getAustrittsGrund());
		karteiOut.setEintrittsGrund(view.getEintrittsGrund());
		karteiOut.setElternOrt(view.getElternOrt());
		karteiOut.setElternTelefon(view.getElternTelefon());
		karteiOut.setFamilienStand(view.getFamilienStand());
		karteiOut.setGeburtsOrt(view.getGeburtsOrt());
		karteiOut.setGeschwisterGeburt(view.getGeschwisterGeburt());
		karteiOut.setGesundheitshinweise(view.getGesundheitsHinweise());
		karteiOut.setGruppe(view.getGruppe());
		karteiOut.setImpfungen(view.getImpfungen());
		karteiOut.setKindNachname(view.getKindNachname());
		karteiOut.setKindTelefon(view.getKindTelefon());
		karteiOut.setKindVorname(view.getKindVorname());
		karteiOut.setKindWohnung(view.getKindWohnung());
		karteiOut.setKrankenkasse(view.getKrankenkasse());
		karteiOut.setKrankheiten(view.getKrankheiten());
		karteiOut.setMutterBeruf(view.getMutterBeruf());
		karteiOut.setMutterName(view.getMutterName());
		karteiOut.setPrimaryKey(view.getPrimaryKey());
		karteiOut.setReligion(view.getReligion());
		karteiOut.setSonstiges(view.getSonstiges());
		karteiOut.setSorgePerson(view.getSorgePerson());
		karteiOut.setStaat(view.getStaat());
		karteiOut.setTetanusZeit(view.getTetanusZeit());
		karteiOut.setVaterBeruf(view.getVaterBeruf());
		karteiOut.setVaterName(view.getVaterName());
		karteiOut.setWeitereKrankheiten(view.getWeitereKrankheiten());
		try {
			karteiOut.setAustrittsDatum(convDate.StringToMysqlDate(view
					.getAustrittsDatum()));

		} catch (ParseException excp) {
			err.addError(err.typeError, view.austrittDatum);
			throw excp;
		}
		try {
			karteiOut.setEintrittsDatum(convDate.StringToMysqlDate(view
					.getEintrittsDatum()));

		} catch (ParseException excp) {
			err.addError(err.typeError, view.eintrittDatum);
			throw excp;
		}
		try {
			karteiOut.setKindGeburtsDatum(convDate.StringToMysqlDate(view
					.getKindGeburtsDatum()));
		} catch (ParseException excp) {
			err.addError(err.typeError, view.kindgeburtDatum);
			throw excp;
		}
		try {
			karteiOut.setMutterGeburt(convDate.StringToMysqlDate(view
					.getMutterGeburt()));
		} catch (ParseException excp) {
			err.addError(err.typeError, view.muttergeburtDatum);
			throw excp;
		}
		try {
			karteiOut.setVaterGeburt(convDate.StringToMysqlDate(view
					.getVaterGeburt()));
		} catch (ParseException excp) {
			err.addError(err.typeError, view.vatergeburtDatum);
			throw excp;
		}

		return err;
	}

}
