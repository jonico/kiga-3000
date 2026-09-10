/*
 * Created on 18.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.de.kiga3000.control;

import java.awt.event.ActionEvent;
import java.awt.event.MouseEvent;
import java.awt.print.PrinterException;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.de.kiga3000.check.CheckKarteiKarte;
import org.de.kiga3000.check.ErrorProtocoll;
import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.database.Ressourcen;
import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.interfaces.SuchDatenholer;
import org.de.kiga3000.interfaces.SuchDatumsholer;
import org.de.kiga3000.interfaces.SuchGruppenholer;
import org.de.kiga3000.interfaces.SuchNamenholer;
import org.de.kiga3000.messages.Messenger;
import org.de.kiga3000.messages.SystemMessenger;
import org.de.kiga3000.printing.PrintUtilities;
import org.de.kiga3000.tables.KigaTable;
import org.de.kiga3000.views.KarteikartenDialog;
import org.de.kiga3000.views.KarteikartenDialogSwingImpl;
import org.de.kiga3000.views.Kiga3000MainPanel;
import org.de.kiga3000.views.KarteiKarteSwingImpl;
/**
 * @author bobo_local<br>
 * 
 * this class provides control for all the actions might happen in view
 */
public class KigaMainViewControl extends KigaControl {
    // database search for date
	private SuchDatumsholer suchDatumsHoler;
    // databse search for name
	private SuchNamenholer suchNamenHoler;
    // database searc for group
	private SuchGruppenholer suchGruppenHoler;
    // Constnts class for action commands
	private static KigaActionComands comand;
    // control for database actions
	private KarteikarteControl karteiCont = new KarteikarteControl();
    // data container
	private KarteikarteImpl karteikarte;
    // view
	private Kiga3000MainPanel view;
    // view communication class (interface)
	private Karteikarte comView;
    // view to container container to view communication
	private KarteiKarteCommunicate comm;
    // check for data container
	private CheckKarteiKarte check;
    // error protocoll class
	private ErrorProtocoll prot;
	// second used view
	private KarteikartenDialogSwingImpl karteikartenDialog;
    // logger as usual
	private static Logger _logger = Logger.getLogger(KigaMainControl.class
			.getName());
    //messenger
	private static Messenger messenger = new Messenger();
    // messenger for system messages
	private static SystemMessenger sysMessenger = new SystemMessenger();
    // error protocoll class
   /* TODO check if necessary */
	private static ErrorProtocoll error = new ErrorProtocoll();

	// public KigaMainViewControl(Kiga3000MainPanel view) {
	/**
	 * Constructor for KigaMainViewControl
	 * instantiates
	 * <ul>
	 *  <li> {@link org.de.kiga3000.data.KarteikarteImpl} KarteikarteImpl</li>
	 *  <li> {@link org.de.kiga3000.control.KigaActionComands} KigaActionComands</li>
	 *  <li> {@link org.de.kiga3000.check.CheckKarteiKarte} CheckKarteiKarte</li>
	 *  <li> {@link org.de.kiga3000.check.ErrorProtocoll} ErrorProtocoll</li>
	 *  <li> {@link org.de.kiga3000.control.KarteiKarteCommunicate} KarteiKarteCommunicate</li>
	 * <li> {@link org.de.kiga3000.views.KarteikartenDialogSwingImpl} KarteikartenDialogSwingImpl</li>
	 * <li> {@link org.de.kiga3000.views.KarteikartenDialogSwingImpl} KarteikartenDialogSwingImpl</li>
	 * </ul>
	 * */
	public KigaMainViewControl() {
		// data container
		karteikarte = new KarteikarteImpl();
		// action comands
		comand = new KigaActionComands();
		// data check
		check = new CheckKarteiKarte();
		// error protocoll
		prot = new ErrorProtocoll();
		// conmtainer <-> view communication
		comm = new KarteiKarteCommunicate();
		// view
        karteikartenDialog = new KarteikartenDialogSwingImpl(this);
		try {
			// database access
			suchDatumsHoler = Ressourcen.getSuchDatumsHoler();
			suchNamenHoler = Ressourcen.getSuchNamenHoler();
			suchGruppenHoler = Ressourcen.getSuchGruppenHoler();
		} catch (SQLException e1) {
			// log it
			_logger.severe(sysMessenger.getMessage("Kiga.SuchDBError", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"Kiga.SuchDBError", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			return;
		} catch (Exception exc) {
			return;
		}

	}
    
	/**
	 * 
	 * @param view
	 */
	
	public void setView(Kiga3000MainPanel view) {
		this.view = view;
		// view.setViewControl(this);
		comView = (Karteikarte) view;
    }

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 * implements control for all sctionPerformed events
	 **/
	public void actionPerformed(ActionEvent e) {
		try {
			// insert data
			if (e.getActionCommand().equals(comand.actionAnlegKartei)) {
				// transfer view to data container
				error = comm.transferViewToKartei(view, karteikarte);
				// check data consitency
				prot = check.checkKarteiKarte(karteikarte);
				// get protocoll table
				HashMap protTab = prot.getErrorTab();
				// everything ok ?
				if (true != protTab.containsValue(prot.typeError)) {
				    // do it !
					karteiCont.insertKarteiEinzeln(karteikarte);
				}
			} else {
				// update data 
				if (e.getActionCommand().equals(comand.actionUpdateKartei)) {
					// get view object number two 
					Karteikarte karteiueb = (Karteikarte)karteikartenDialog;
					// transfer data from view
					error = comm.transferViewToKartei(karteiueb, karteikarte);
					// check data consitency
					prot = check.checkKarteiKarte(karteikarte);
					// get protocoll table
					HashMap protTab = prot.getErrorTab();
					// everything ok ?
					if (true != protTab.containsValue(prot.typeError)) {
						// do it !
						karteiCont.updateKarteiEinzeln(karteikarte);
					}
				} else {
					// delte data
					if (e.getActionCommand().equals(comand.actionDeleteKartei)) {
						// transfer data from view
						error = comm.transferViewToKartei(view, karteikarte);
						// do it
						karteiCont.deleteKarteEinzeln(karteikarte);
					} else {
						// load data
						if (e.getActionCommand()
								.equals(comand.actionLoadKartei)) {
                            // get Primary key 
							Integer Id = view.getKarteiID();
							// Convert to integer
							int karteiId = Id.intValue();
							// load via control
							karteiCont.ladeKarteiEinzel(karteikarte, karteiId);
							// polymorph a little bit
							Karteikarte karteiueb = (Karteikarte)karteikartenDialog;
							// transfer data to view
							comm.transferKarteiToView(karteiueb,karteikarte);
							// show it
							karteikartenDialog.setVisible(true);
						}
					}

				}
			}
			if (e.getActionCommand().equals(comand.actionSaveToFileKartei)) {
               // save to file
				this.saveKarteiToFile();
			}
			if (e.getActionCommand().equals(comand.actionAbmeldKartei)) {
			    // unsign kid
				this.abmeldKartei();
			}
			if (e.getActionCommand().equals(comand.actionDruckErgTab)) {
			    // print table content
				this.druckeTabelle();
			}
			if (e.getActionCommand().equals(comand.actionDruckKartei)) {
				// print data
				this.druckeKartei();
			}
			if (e.getActionCommand().equals(comand.actionDruckKartei2)) {
				// print data out of view 2
				this.druckeKarteiView2();
			}
			if (e.getActionCommand().equals(comand.actionSucheKartei)) {
			    // search
				this.suchen();
			}
			if (e.getActionCommand().equals(comand.actionTauschKartei)) {
				// change groups 
				this.tauscheGruppe();
			}
		} catch (ParseException parse) {
			JOptionPane.showMessageDialog(null, messenger.getMessage(
					"GUI.KiGa.DateConversion", parse.getLocalizedMessage()),
					messenger.getMessage("KiGa.title"),
					JOptionPane.INFORMATION_MESSAGE);

		}
	}

	/**
	 * save data to file !!!
	 */
	private void saveKarteiToFile() {
		// define table 
		KigaTable ergebnisTabelle;
		// get table direct from view
		ergebnisTabelle = view.getErgebnisTable();
		// get Panel for obscure thinmgs we are doing now !
		JPanel ergebnisPanel = view.getErgebnisPanel();
		JFileChooser ch = new JFileChooser();
		// guess what ?
		if (JFileChooser.APPROVE_OPTION == ch.showSaveDialog(ergebnisPanel)) {
			// yes a dialog !
			File file = ch.getSelectedFile();
			try {
				//get coordinates for data
				int rows = ergebnisTabelle.getRowCount();
				int columns = ergebnisTabelle.getColumnCount() - 1;
				// printwriter chanel for file
				PrintWriter pw = new PrintWriter(new BufferedOutputStream(
						new FileOutputStream(file)));
				// work on table
				for (int i = 0; i < rows; ++i) {
					for (int j = 0; j < columns; ++j) {
						// get it in the file
						pw.print(ergebnisTabelle.getValueAt(i, j));
						// seperated by tab
						pw.print('\t');
					}
					if (-1 < columns)
						pw.println(ergebnisTabelle.getValueAt(i, columns));
				}
				// close it
				pw.close();
				// tell user everthing is fine !
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.FileSaved", file.getName()), messenger
						.getMessage("KiGa.title"),
						JOptionPane.INFORMATION_MESSAGE);
			} catch (Exception ex) {
				// error happened somwhere
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.FileErr", ex.getMessage()), messenger
						.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
				return;
			}
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.MouseListener#mouseClicked(java.awt.event.MouseEvent)
	 */
	public void mouseClicked(MouseEvent e) {
		try {
			if (2 == e.getClickCount()) {
				// if double click get Primary key
				Integer Id = view.getKarteiID();
				int karteiId = Id.intValue();
				// load data
				karteiCont.ladeKarteiEinzel(karteikarte, karteiId);
//				KarteikartenDialogSwingImpl karteikartenDialog = new KarteikartenDialogSwingImpl(this);
				// do some polymorphing 
				Karteikarte karteiueb = (Karteikarte)karteikartenDialog;
			    // transfer data to view
				comm.transferKarteiToView(karteiueb,karteikarte);
				// show dialogue
				karteikartenDialog.setVisible(true);
			}
		} catch (ParseException parse) {
			// something went wrong
			JOptionPane.showMessageDialog(null, messenger.getMessage(
					"GUI.KiGa.DateConversion", parse.getLocalizedMessage()),
					messenger.getMessage("KiGa.title"),
					JOptionPane.INFORMATION_MESSAGE);

		}

	}

	public void mouseEntered(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	/**
	 *  unsign kid from kindergarden
	 */
	private void abmeldKartei() {
		// get Priimary key
		Integer Id = view.getKarteiID();
		int karteiId = Id.intValue();
		// load data
		karteiCont.ladeKarteiEinzel(karteikarte, karteiId);
		// set group to number 9
		karteiCont.deleteKarteEinzeln(karteikarte);
		// Message to user
		JOptionPane.showMessageDialog(null, messenger
				.getMessage("KiGa.ChildRemove"), messenger
				.getMessage("KiGa.title"), JOptionPane.INFORMATION_MESSAGE);
        // initialize view
		this.suchen();
	}

	/**
	 * print table content from MainPanel
	 */
	private void druckeTabelle() {
		// get table
		KigaTable ergebnisTabelle;
		ergebnisTabelle = view.getErgebnisTable();
		try {
			// print it
			PrintUtilities.printComponent(ergebnisTabelle);
		} catch (PrinterException e1) {
			// log failure
			_logger.severe(sysMessenger.getMessage("KiGa.PrintErr", e1
					.getMessage()));
			// message to user
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.PrintErr", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 *  print data
	 */
	private void druckeKartei() {
		// get view
		KarteikartenDialog karteikartenDialog =  view.getDruckKartei();
        // print view via functionality KarteiKarteSwingImpl
		this.druckeKarteiView2(); 
	}

	/**
	 * print via functionality KarteiKarteSwingImpl
	 */
	private void druckeKarteiView2() {
		// get view
		KarteiKarteSwingImpl karteiSwi = karteikartenDialog.getDruck();
		try {
			// print object
			PrintUtilities.printComponent(karteiSwi);
		} catch (PrinterException e1) {
            // log failure
			_logger.severe(sysMessenger.getMessage("KiGa.PrintErr", e1
					.getMessage()));
			// message to user
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.PrintErr", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
		}

	}

	/**
	 *  search database 
	 */
	private void suchen() {
		// empty view
		view.leeren();
		// Initialise searchobject
		try {
			SuchDatenholer dummy = null;
			// serach for name
			if (view.getNamenSuchRadioButton()) {
				if (view.getEhemaligeBox())
					// search even in group 9
					suchNamenHoler.setNachnamen(view.getSucheNachname(),
							(byte) 1, (byte) 9);
				else
					suchNamenHoler.setNachnamen(view.getSucheNachname(),
							(byte) 1, (byte) 8);
				dummy = suchNamenHoler;
			} else if (view.getGruppenSucheRadioButton()) {
				// serach in groups
				suchGruppenHoler.setGruppe(view.getSucheGruppenModel());
				dummy = suchGruppenHoler;
			} else if (view.getSucheDatumRadioButton()) {
				// serach for birthday
				if (view.getEhemaligeBox())
					// search even in group 9
					suchDatumsHoler.setDatum(view.getSuchAnfangsDatum(), view
							.getSucheEndDatum(), (byte) 1, (byte) 9);
				else
					suchDatumsHoler.setDatum(view.getSuchAnfangsDatum(), view
							.getSucheEndDatum(), (byte) 1, (byte) 8);
				dummy = suchDatumsHoler;
			}
			// get data
			if (!(dummy.holeDaten())) {
				// nothing was found
				JOptionPane.showMessageDialog(null, messenger
						.getMessage("KiGa.NothingFound"), messenger
						.getMessage("KiGa.title"), JOptionPane.WARNING_MESSAGE);
				return;
			} else
				// present data
				dummy.fill(view);
		} catch (SQLException e1) {
			// log failure
			_logger.severe(sysMessenger.getMessage("Kiga.SuchDBError", e1
					.getMessage()));
			// message to user
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"Kiga.SuchDBError", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			return;
		} catch (Exception exc) {
			return;
		}
	}

	/**
	 * change groups
	 */
	private void tauscheGruppe() {
		// change group from to
		karteiCont.verschiebeGruppe((byte) view.getTauschGruppe1(), (byte) view
				.getTauschgruppe2(), (byte) 10);
		// show message to user
		JOptionPane.showMessageDialog(null, messenger
				.getMessage("KiGa.GroupChanged"), messenger
				.getMessage("KiGa.title"), JOptionPane.INFORMATION_MESSAGE);
	}

}
