
package org.de.kiga3000.views;
import java.awt.BorderLayout;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;

import org.de.kiga3000.control.KigaActionComands;
import org.de.kiga3000.control.KigaControl;
import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.messages.Messenger;

/*
 * Created on 07.09.2004
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
public class KarteikartenDialogSwingImpl extends KarteikartenDialog implements Karteikarte{
	
	// Karteikartenobjekte
//	private KarteikartenDatenholer karteikartenDatenholer;
//	private KarteikartenDatenAuffrischer karteikartenDatenAuffrischer;

	private KarteiKarteSwingImpl karteikarteSwi;
//	private KarteikarteImpl karteikarte = new KarteikarteImpl();
	
//	private PrintAction printAct;
	private KigaControl control;
//	private KarteiKarteDBAction updateAct;
	private KigaActionComands action;
	
	private static Messenger messenger = new Messenger();
	private static Logger _logger = Logger.getLogger(KarteikartenDialogSwingImpl.class
			.getName());
	
//	public KarteikartenDialogSwingImpl() throws PoolException, SQLException {
	public KarteikartenDialogSwingImpl(Object contO) {
		super();
		control = (KigaControl)contO;
		// Initialisieren des Karteikartenobjekte
//		karteikartenDatenAuffrischer=Ressourcen.getKarteikartenDatenAuffrischer();
//		karteikartenDatenholer=Ressourcen.getKarteikartenDatenholer();
		//TODO interface fuer Action schreiben
		karteikarteSwi=new KarteiKarteSwingImpl();
		getContentPane().add(karteikarteSwi,BorderLayout.CENTER);
		final JPanel panel = new JPanel();
		getContentPane().add(panel,BorderLayout.SOUTH);
		final JButton changeKnopf = new JButton();
//		updateAct = new KarteiKarteDBAction(karteikarte);
		changeKnopf.setActionCommand(action.actionUpdateKartei);
		changeKnopf.addActionListener(control);
		panel.add(changeKnopf);
		changeKnopf.setText(messenger.getMessage("GUI.KiGa.ModifUpd"));

		final JButton druckKnopf = new JButton();
//		printAct = new PrintAction(karteikarteSwi);
		druckKnopf.setActionCommand(action.actionDruckKartei2);
		druckKnopf.addActionListener(control);
		panel.add(druckKnopf);
		druckKnopf.setText(messenger.getMessage("GUI.KiGa.Print"));
		setDefaultCloseOperation(JDialog.HIDE_ON_CLOSE);
		setModal(true);
		setTitle(messenger.getMessage("KiGa.title"));
		setBounds(0,0,1024,768);
	}

	public void setControl(Object contO){
		control = (KigaControl)contO;
	}
    
	public KarteiKarteSwingImpl getDruck(){
		return karteikarteSwi;
	}
	
	public byte getAnzahlGeschwister() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getAnzahlGeschwister();
	}

	public String getArbeitsOrt1() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getArbeitsOrt1();
	}

	public String getArbeitsOrt2() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getArbeitsOrt2();
	}

	public String getArbeitsTelefon1() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getArbeitsTelefon1();
	}

	public String getArbeitsTelefon2() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getArbeitsTelefon2();
	}

	public String getArztOrt() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getArztOrt();
	}

	public String getArztTelefon() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getArztTelefon();
	}

	public String getAustrittsDatum() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getAustrittsDatum();
	}

	public String getAustrittsGrund() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getAustrittsGrund();
	}

	public String getEintrittsDatum() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getEintrittsDatum();
	}

	public String getEintrittsGrund() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getEintrittsGrund();
	}

	public String getElternOrt() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getElternOrt();
	}

	public String getElternTelefon() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getElternTelefon();
	}

	public byte getFamilienStand() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getFamilienStand();
	}

	public String getGeburtsOrt() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getGeburtsOrt();
	}

	public String getGeschwisterGeburt() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getGeschwisterGeburt();
	}

	public String getGesundheitsHinweise() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getGesundheitsHinweise();
	}

	public byte getGruppe() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getGruppe();
	}

	public String getImpfungen() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getImpfungen();
	}

	public String getKindGeburtsDatum() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getKindGeburtsDatum();
	}

	public String getKindNachname() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getKindNachname();
	}

	public String getKindTelefon() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getKindTelefon();
	}

	public String getKindVorname() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getKindVorname();
	}

	public String getKindWohnung() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getKindWohnung();
	}

	public String getKrankenkasse() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getKrankenkasse();
	}

	public short getKrankheiten() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getKrankheiten();
	}

	public String getMutterBeruf() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getMutterBeruf();
	}

	public String getMutterGeburt() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getMutterGeburt();
	}

	public String getMutterName() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getMutterName();
	}

	public int getPrimaryKey() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getPrimaryKey();
	}

	public String getReligion() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getReligion();
	}

	public String getSonstiges() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getSonstiges();
	}

	public String getSorgePerson() {
		/*
		 * Was "return karteikarteSwi.getSonstiges();" - a copy-paste error from the
		 * generated stub above. The setter was always correct, so the mistake only
		 * showed on save: KigaMainViewControl harvests the UPDATE from this dialog
		 * rather than from the main panel, so every update wrote the `sonstiges` text
		 * into the `sorgeperson` column. With `sonstiges` being varchar(255) and
		 * `sorgeperson` varchar(100), a long note produced
		 * "Data truncation: Data too long for column 'sorgeperson'" and the save was
		 * rejected; a shorter note was silently stored in the wrong column instead.
		 */
		return karteikarteSwi.getSorgePerson();
	}

	public String getStaat() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getStaat();
	}

	public String getTetanusZeit() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getTetanusZeit();
	}

	public String getVaterBeruf() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getVaterBeruf();
	}

	public String getVaterGeburt() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getVaterGeburt();
	}

	public String getVaterName() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getVaterName();
	}

	public String getWeitereKrankheiten() {
		// TODO Auto-generated method stub
		return karteikarteSwi.getWeitereKrankheiten();
	}

	public void setAnzahlGeschwister(byte i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setAnzahlGeschwister(i);
	}

	public void setArbeitsOrt1(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setArbeitsOrt1(i);	
	}

	public void setArbeitsOrt2(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setArbeitsOrt2(i);	
	}

	public void setArbeitsTelefon1(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setArbeitsTelefon1(i);	
	}

	public void setArbeitsTelefon2(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setArbeitsTelefon2(i);	
	}

	public void setArztOrt(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setArztOrt(i);
	}

	public void setArztTelefon(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setArztTelefon(i);	
	}

	public void setAustrittsDatum(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setAustrittsDatum(i);	
	}

	public void setAustrittsGrund(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setAustrittsGrund(i);		
	}

	public void setEintrittsDatum(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setEintrittsDatum(i);	
	}

	public void setEintrittsGrund(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setEintrittsGrund(i);	
	}

	public void setElternOrt(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setElternOrt(i);
	}

	public void setElternTelefon(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setElternTelefon(i);
	}

	public void setFamilienStand(byte i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setFamilienStand(i);		
	}

	public void setGeburtsOrt(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setGeburtsOrt(i);	
	}

	public void setGeschwisterGeburt(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setGeschwisterGeburt(i);	
	}

	public void setGesundheitshinweise(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setGesundheitshinweise(i);	
	}

	public void setGruppe(byte i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setGruppe(i);		
	}

	public void setImpfungen(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setImpfungen(i);		
	}

	public void setKindGeburtsDatum(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setKindGeburtsDatum(i);	
	}

	public void setKindNachname(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setKindNachname(i);	
	}

	public void setKindTelefon(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setKindTelefon(i);		
	}

	public void setKindVorname(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setKindVorname(i);	
	}

	public void setKindWohnung(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setKindWohnung(i);	
	}

	public void setKrankenkasse(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setKrankenkasse(i);	
	}

	public void setKrankheiten(short i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setKrankheiten(i);	
	}

	public void setMutterBeruf(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setMutterBeruf(i);	
	}

	public void setMutterGeburt(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setMutterGeburt(i);		
	}

	public void setMutterName(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setMutterName(i);		
	}

	public void setPrimaryKey(int i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setPrimaryKey(i);	
	}

	public void setReligion(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setReligion(i);		
	}

	public void setSonstiges(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setSonstiges(i);		
	}

	public void setSorgePerson(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setSorgePerson(i);		
	}

	public void setStaat(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setStaat(i);		
	}

	public void setTetanusZeit(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setTetanusZeit(i);	
	}

	public void setVaterBeruf(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setVaterBeruf(i);		
	}

	public void setVaterGeburt(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setVaterGeburt(i);	
	}

	public void setVaterName(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setVaterName(i);	
	}

	public void setWeitereKrankheiten(String i) {
		// TODO Auto-generated method stub
		karteikarteSwi.setWeitereKrankheiten(i);
	}

}
