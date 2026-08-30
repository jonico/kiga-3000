package org.de.kiga3000.views;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.SQLException;
import java.util.logging.Logger;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;

import org.de.kiga3000.control.KigaActionComands;
import org.de.kiga3000.control.KigaMainViewControl;
import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.database.Ressourcen;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.interfaces.Suchfenster;
import org.de.kiga3000.listener.KarteiListSelection;
import org.de.kiga3000.messages.Messenger;
import org.de.kiga3000.messages.SystemMessenger;
import org.de.kiga3000.tables.KigaTable;

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

/* internationalisation added by bdiemer 2005-29-11 */

public class Kiga3000MainPanel extends JPanel implements Suchfenster,
		Karteikarte {

	
	//TODO all data operations and action listener should be done sxternal by
	// seperated classes
	// SuchObjekte
//	private SuchDatumsholer suchDatumsHoler;

//	private SuchNamenholer suchNamenHoler;

//	private SuchGruppenholer suchGruppenHoler;

	// Suchfenster
	private Suchfenster suchFenster;

	// Karteikartenobjekte
//	private KarteikartenDatenEntferner karteikartenDatenEntferner;

	//	private KarteikartenDatenErzeuger karteikartenDatenErzeuger;

	// Karteikartenanzeigedialog
	private KarteikartenDialog karteikartenDialog;

	private KarteikartenDialogSwingImpl karteikartenSwi;

	// Gruppenverschieber
//	private Gruppenverschieber gruppenverschieber;

//	private PrintAction printAct;

	private KigaActionComands comand;
	//	private KarteiKarteDBAction karteiAct;
//	private KigaMainViewControl karteiAct;

	private SystemMessenger sysmess = new SystemMessenger();

	private Messenger messenger = new Messenger();

	private static Logger _logger = Logger.getLogger(Kiga3000MainPanel.class
			.getName());

	private KigaTable ergebnisTabelle;

	private DefaultTableModel ergebnisModel;

	private KarteiListSelection listSelect;

	private JSlider tauschGruppe2;

	private JTextField sucheNachname;

	private SpinnerNumberModel sucheGruppenModel;

	private JTextField sucheEndDatum;

	private JTextField suchAnfangsDatum;

	private ButtonGroup suchButtonGroup = new ButtonGroup();

	private JCheckBox ehemaligeBox;

	private JRadioButton sucheDatumRadioButton;

	private JRadioButton gruppenSucheRadioButton;

	private JRadioButton namenSucheRadioButton;

	private KarteiKarteSwingImpl karteiKarteSwi;

	private KarteikarteImpl karteiKarte;

	private JSlider tauschGruppe1;

	public JButton ergebnisAnzeigeKnopf;

	public JButton ergebnisAbmeldeKnopf;

	private JButton ergebnisDruckenKnopf;

	private JButton ergebnisAbspeicherKnopf;

	private JCheckBox anschriftCheckBox;

	private JCheckBox geburtsDatumCheckBox;

	private JCheckBox ergebnisNachnameCheckBox;

	private JCheckBox ergebnisVornameCheckBox;

	private JScrollPane ergebisTabellePane;

	private static Configuration config = new Configuration();

	private final JPanel ergebnisPanel = new JPanel();
	
	public Kiga3000MainPanel(KigaMainViewControl karteiAct) throws PoolException, SQLException {
		super();
		// Initialisieren der Suchobjekte
//		suchDatumsHoler = Ressourcen.getSuchDatumsHoler();
//		suchNamenHoler = Ressourcen.getSuchNamenHoler();
//		suchGruppenHoler = Ressourcen.getSuchGruppenHoler();

		// Initialisieren des Suchfensters
		suchFenster = this;
        comand = new KigaActionComands();
		// Initialisieren der Karteikartenkomponenten
		//		karteikartenDatenEntferner=Ressourcen.getKarteikartenDatenEntferner();
		//		karteikartenDatenErzeuger=Ressourcen.getKarteikartenDatenErzeuger();

        karteikartenSwi = new KarteikartenDialogSwingImpl(karteiAct); 
		// Initialisieren des Karteikartendialoges
//		karteikartenDialog = Ressourcen.getKarteikartenDialog();
		karteikartenDialog = karteikartenSwi;
		// Initialisieren des Gruppenverschiebers
//		gruppenverschieber = Ressourcen.getGruppenVerschieber();

		setLayout(new GridBagLayout());

		final JTabbedPane tabbedPane = new JTabbedPane();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.weighty = 1.0;
		gridBagConstraints.weightx = 1.0;
		gridBagConstraints.fill = GridBagConstraints.BOTH;
		add(tabbedPane, gridBagConstraints);

		final JPanel allgemeinPanel = new JPanel();
		allgemeinPanel.setLayout(new GridBagLayout());
		tabbedPane.addTab(messenger.getMessage("GUI.KiGa.General"), null,
				allgemeinPanel, null);

		final JScrollPane ScrollPane = new JScrollPane();
		ScrollPane
				.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.weighty = 1.0;
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.fill = GridBagConstraints.BOTH;
		allgemeinPanel.add(ScrollPane, gridBagConstraints_1);

		final JTextPane allgemeinHTML = new JTextPane();
		allgemeinHTML.setEditable(false);
		try {
			String helpPath = "";
			helpPath = config.getConfig("KiGaLoggingPath");
			allgemeinHTML.setPage(new URL("file:///" + helpPath
					+ "/titlepage/kiga.htm"));
		} catch (Exception e1) {
			_logger.warning(sysmess.getMessage("KiGa.NoOnlineHelp", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysmess.getMessage(
					"KiGa.NoOnlineHelp", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
		}
		ScrollPane.setViewportView(allgemeinHTML);
		//allgemeinHTML.setText("Hier kommt die Online Hilfe und der
		// Autorenvermerk hinein");

		final JPanel neuzugangPanel = new JPanel();
		neuzugangPanel.setLayout(new BorderLayout());
		tabbedPane.addTab(messenger.getMessage("GUI.KiGa.NewEntry"), null,
				neuzugangPanel, null);
		final JPanel neuzugangKnopfleiste = new JPanel();
		neuzugangKnopfleiste.setLayout(new FlowLayout());
		neuzugangPanel.add(neuzugangKnopfleiste, BorderLayout.SOUTH);

		final JButton neuzugangAnlegenKnopf = new JButton();

		neuzugangKnopfleiste.add(neuzugangAnlegenKnopf);
		neuzugangAnlegenKnopf.setText(messenger.getMessage("GUI.KiGa.Insert"));

		final JButton leerenButton = new JButton();
		leerenButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				karteiKarteSwi.leeren();
			}
		});
		neuzugangKnopfleiste.add(leerenButton);
		leerenButton.setText(messenger.getMessage("GUI.KiGa.Empty"));

		final JPanel neuzugangKopfleistenPanel = new JPanel();
		neuzugangPanel.add(neuzugangKopfleistenPanel, BorderLayout.NORTH);

		final JLabel label = new JLabel();
		neuzugangKopfleistenPanel.add(label);
		label.setText(messenger.getMessage("GUI.KiGa.DataNewChild"));

		karteiKarteSwi = new KarteiKarteSwingImpl();
		karteiKarte = new KarteikarteImpl();
		neuzugangPanel.add(karteiKarteSwi, BorderLayout.CENTER);
		//		karteiAct = new KarteiKarteDBAction(karteiKarte);
		neuzugangAnlegenKnopf.setActionCommand(comand.actionAnlegKartei);
		neuzugangAnlegenKnopf.addActionListener(karteiAct);

		final JSplitPane splitPane = new JSplitPane();
		splitPane.setOrientation(JSplitPane.VERTICAL_SPLIT);
		tabbedPane.addTab(messenger.getMessage("GUI.KiGa.SearchUpdate"), null,
				splitPane, null);

		final JPanel suchPanel = new JPanel();
		suchPanel.setLayout(new BorderLayout());

		splitPane.setTopComponent(suchPanel);
		final JPanel sucheFussleistenPanel = new JPanel();
		suchPanel.add(sucheFussleistenPanel, BorderLayout.SOUTH);

		final JButton sucheStartenKnopf = new JButton();
		sucheStartenKnopf.setActionCommand(comand.actionSucheKartei);
		sucheStartenKnopf.addActionListener(karteiAct);
		sucheFussleistenPanel.add(sucheStartenKnopf);
		sucheStartenKnopf.setText(messenger.getMessage("GUI.KiGa.StartSearch"));

		ehemaligeBox = new JCheckBox();
		sucheFussleistenPanel.add(ehemaligeBox);
		ehemaligeBox.setText(messenger
				.getMessage("GUI.KiGa.LeftChildAddSearch"));

		final JPanel sucheHauptPanel = new JPanel();
		sucheHauptPanel.setLayout(new GridBagLayout());
		suchPanel.add(sucheHauptPanel, BorderLayout.CENTER);

		final JSeparator separator = new JSeparator();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weighty = 1.0;
		gridBagConstraints_2.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_2.gridwidth = 5;
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.fill = GridBagConstraints.HORIZONTAL;
		sucheHauptPanel.add(separator, gridBagConstraints_2);

		final JLabel label_3 = new JLabel();
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.weightx = 1.0;
		gridBagConstraints_4.anchor = GridBagConstraints.WEST;
		gridBagConstraints_4.gridwidth = 2;
		gridBagConstraints_4.gridy = 1;
		gridBagConstraints_4.gridx = 1;
		sucheHauptPanel.add(label_3, gridBagConstraints_4);
		label_3.setText(messenger.getMessage("GUI.KiGa.StartDate"));

		final JLabel label_4 = new JLabel();
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.anchor = GridBagConstraints.WEST;
		gridBagConstraints_5.weightx = 1.0;
		gridBagConstraints_5.gridy = 1;
		gridBagConstraints_5.gridx = 4;
		sucheHauptPanel.add(label_4, gridBagConstraints_5);
		label_4.setText(messenger.getMessage("GUI.KiGa.EndDate"));

		final JSeparator separator_1 = new JSeparator();
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.weighty = 1.0;
		gridBagConstraints_6.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints_6.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_6.gridwidth = 5;
		gridBagConstraints_6.gridy = 5;
		sucheHauptPanel.add(separator_1, gridBagConstraints_6);

		sucheDatumRadioButton = new JRadioButton();
		sucheDatumRadioButton.setSelected(true);
		suchButtonGroup.add(sucheDatumRadioButton);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.anchor = GridBagConstraints.WEST;
		gridBagConstraints_7.gridy = 2;
		sucheHauptPanel.add(sucheDatumRadioButton, gridBagConstraints_7);
		sucheDatumRadioButton.setText(messenger
				.getMessage("GUI.KiGa.DateSearch"));

		suchAnfangsDatum = new JTextField();
		suchAnfangsDatum.setColumns(50);
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.gridwidth = 2;
		gridBagConstraints_3.fill = GridBagConstraints.BOTH;
		gridBagConstraints_3.gridy = 2;
		gridBagConstraints_3.gridx = 1;
		sucheHauptPanel.add(suchAnfangsDatum, gridBagConstraints_3);

		sucheEndDatum = new JTextField();
		sucheEndDatum.setColumns(50);
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.fill = GridBagConstraints.BOTH;
		gridBagConstraints_8.gridy = 2;
		gridBagConstraints_8.gridx = 4;
		sucheHauptPanel.add(sucheEndDatum, gridBagConstraints_8);

		gruppenSucheRadioButton = new JRadioButton();
		suchButtonGroup.add(gruppenSucheRadioButton);
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_9.gridy = 4;
		sucheHauptPanel.add(gruppenSucheRadioButton, gridBagConstraints_9);
		gruppenSucheRadioButton.setText(messenger
				.getMessage("GUI.KiGa.GroupSearch"));

		final JLabel label_2 = new JLabel();
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.gridy = 3;
		gridBagConstraints_10.gridx = 1;
		sucheHauptPanel.add(label_2, gridBagConstraints_10);
		label_2.setText(messenger.getMessage("GUI.KiGa.Group"));

		sucheGruppenModel = new SpinnerNumberModel(1, 1, 9, 1);
		JSpinner sucheGruppenSpinner = new JSpinner(sucheGruppenModel);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.BOTH;
		gridBagConstraints_11.gridy = 4;
		gridBagConstraints_11.gridx = 1;
		sucheHauptPanel.add(sucheGruppenSpinner, gridBagConstraints_11);

		namenSucheRadioButton = new JRadioButton();
		suchButtonGroup.add(namenSucheRadioButton);
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.anchor = GridBagConstraints.EAST;
		gridBagConstraints_12.gridy = 4;
		gridBagConstraints_12.gridx = 3;
		sucheHauptPanel.add(namenSucheRadioButton, gridBagConstraints_12);
		namenSucheRadioButton.setText(messenger
				.getMessage("GUI.KiGa.NameSearch"));

		final JLabel label_5 = new JLabel();
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.anchor = GridBagConstraints.WEST;
		gridBagConstraints_13.gridy = 3;
		gridBagConstraints_13.gridx = 4;
		sucheHauptPanel.add(label_5, gridBagConstraints_13);
		label_5.setText(messenger.getMessage("GUI.KiGa.Name"));

		sucheNachname = new JTextField();
		sucheNachname.setColumns(50);
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.fill = GridBagConstraints.BOTH;
		gridBagConstraints_14.gridy = 4;
		gridBagConstraints_14.gridx = 4;
		sucheHauptPanel.add(sucheNachname, gridBagConstraints_14);

		final JPanel suchKopfleistenPanel = new JPanel();
		suchPanel.add(suchKopfleistenPanel, BorderLayout.NORTH);

		final JLabel label_1 = new JLabel();
		suchKopfleistenPanel.add(label_1);
		label_1.setText(messenger.getMessage("GUI.KiGa.ChooseSearchOpt"));

		ergebnisPanel.setLayout(new BorderLayout());
		splitPane.setRightComponent(ergebnisPanel);

		final JPanel ergebnisFussleistenPanel = new JPanel();
		ergebnisPanel.add(ergebnisFussleistenPanel, BorderLayout.SOUTH);

		ergebnisAnzeigeKnopf = new JButton();
		ergebnisAnzeigeKnopf.setEnabled(false);
		ergebnisAnzeigeKnopf.setActionCommand(comand.actionLoadKartei);
		ergebnisAnzeigeKnopf.addActionListener(karteiAct);

		ergebnisFussleistenPanel.add(ergebnisAnzeigeKnopf);
		ergebnisAnzeigeKnopf.setText(messenger.getMessage("GUI.KiGa.Show"));

		ergebnisAbmeldeKnopf = new JButton();
		ergebnisAbmeldeKnopf.setEnabled(false);
		ergebnisAbmeldeKnopf.setActionCommand(comand.actionAbmeldKartei);
		ergebnisAbmeldeKnopf.addActionListener(karteiAct); 
		ergebnisFussleistenPanel.add(ergebnisAbmeldeKnopf);
		ergebnisAbmeldeKnopf.setText(messenger
				.getMessage("GUI.KiGa.ChildRemove"));
		ergebnisDruckenKnopf = new JButton();
		ergebnisDruckenKnopf.setActionCommand(comand.actionDruckErgTab);
		ergebnisDruckenKnopf.addActionListener(karteiAct);
		ergebnisDruckenKnopf.setEnabled(false);
		ergebnisFussleistenPanel.add(ergebnisDruckenKnopf);
		ergebnisDruckenKnopf.setText(messenger.getMessage("GUI.KiGa.Print"));

		ergebnisAbspeicherKnopf = new JButton();
		ergebnisAbspeicherKnopf.setEnabled(false);
		ergebnisAbspeicherKnopf.setActionCommand(comand.actionSaveToFileKartei);
		ergebnisAbspeicherKnopf.addActionListener(karteiAct);
		ergebnisFussleistenPanel.add(ergebnisAbspeicherKnopf);
		ergebnisAbspeicherKnopf.setText(messenger.getMessage("GUI.KiGa.Save"));

		final JPanel ergebnisKopfleistenPanel = new JPanel();
		ergebnisKopfleistenPanel.setLayout(new FlowLayout());
		ergebnisPanel.add(ergebnisKopfleistenPanel, BorderLayout.NORTH);

		final JLabel label_9 = new JLabel();
		ergebnisKopfleistenPanel.add(label_9);
		label_9.setText(messenger.getMessage("Gui.KiGa.DisplayedFields"));

		ergebnisVornameCheckBox = new JCheckBox();
		ergebnisVornameCheckBox.setSelected(true);
		ergebnisKopfleistenPanel.add(ergebnisVornameCheckBox);
		ergebnisVornameCheckBox.setText(messenger
				.getMessage("GUI.KiGa.Surname"));
		ergebnisVornameCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ergebnisTabelle.zeigeVorname(ergebnisVornameCheckBox
						.isSelected());
			}
		});

		ergebnisNachnameCheckBox = new JCheckBox();
		ergebnisNachnameCheckBox.setSelected(true);
		ergebnisKopfleistenPanel.add(ergebnisNachnameCheckBox);
		ergebnisNachnameCheckBox.setText(messenger.getMessage("GUI.KiGa.Name"));
		ergebnisNachnameCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ergebnisTabelle.zeigeNachname(ergebnisNachnameCheckBox
						.isSelected());
			}
		});

		geburtsDatumCheckBox = new JCheckBox();
		geburtsDatumCheckBox.setSelected(true);
		ergebnisKopfleistenPanel.add(geburtsDatumCheckBox);
		geburtsDatumCheckBox.setText(messenger
				.getMessage("GUI.KiGa.DateOfBirth"));
		geburtsDatumCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ergebnisTabelle.zeigeGeburtstag(geburtsDatumCheckBox
						.isSelected());
			}
		});

		anschriftCheckBox = new JCheckBox();
		anschriftCheckBox.setSelected(true);
		ergebnisKopfleistenPanel.add(anschriftCheckBox);
		anschriftCheckBox.setText(messenger.getMessage("GUI.KiGa.Adress"));
		anschriftCheckBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ergebnisTabelle.zeigeAnschrift(anschriftCheckBox.isSelected());
			}
		});

		final JPanel ergebnisHauptPanel = new JPanel();
		ergebnisHauptPanel.setLayout(new GridBagLayout());
		ergebnisPanel.add(ergebnisHauptPanel, BorderLayout.CENTER);

		ergebnisModel = new DefaultTableModel(new String[] {
				messenger.getMessage("GUI.KiGa.ID"),
				messenger.getMessage("GUI.KiGa.Surname"),
				messenger.getMessage("GUI.KiGa.Name"),
				messenger.getMessage("GUI.KiGa.DateOfBirth"),
				messenger.getMessage("GUI.KiGa.Adress") }, 0);
		ergebnisTabelle = new KigaTable(ergebnisModel);
		listSelect = new KarteiListSelection(ergebnisTabelle, this);
		ergebnisTabelle.getSelectionModel()
				.addListSelectionListener(listSelect);
		ergebnisTabelle.addMouseListener(karteiAct);
		ergebnisTabelle.setEnabled(false);

		ergebisTabellePane = new JScrollPane(ergebnisTabelle);
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.fill = GridBagConstraints.BOTH;
		gridBagConstraints_21.weighty = 1.0;
		gridBagConstraints_21.gridy = 1;
		gridBagConstraints_21.weightx = 1.0;
		ergebnisHauptPanel.add(ergebisTabellePane, gridBagConstraints_21);

		final JSeparator separator_4 = new JSeparator();
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_22.fill = GridBagConstraints.HORIZONTAL;
		ergebnisHauptPanel.add(separator_4, gridBagConstraints_22);

		final JSeparator separator_5 = new JSeparator();
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints_23.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_23.gridy = 2;
		ergebnisHauptPanel.add(separator_5, gridBagConstraints_23);

		final JPanel gruppentauschPanel = new JPanel();
		gruppentauschPanel.setLayout(new BorderLayout());
		tabbedPane.addTab(messenger.getMessage("GUI.KiGa.ChangeGroup"), null,
				gruppentauschPanel, null);

		final JPanel tauschKopfleistenPanel = new JPanel();
		gruppentauschPanel.add(tauschKopfleistenPanel, BorderLayout.NORTH);

		final JLabel label_6 = new JLabel();
		tauschKopfleistenPanel.add(label_6);
		label_6.setText(messenger.getMessage("GUI.KiGa.ChangeGroupIdent"));

		final JPanel tauschFusszeilenPanel = new JPanel();
		gruppentauschPanel.add(tauschFusszeilenPanel, BorderLayout.SOUTH);

		final JButton tauschenKnopf = new JButton();
		tauschenKnopf.setActionCommand(comand.actionTauschKartei);
		tauschenKnopf.addActionListener(karteiAct);
		tauschFusszeilenPanel.add(tauschenKnopf);
		tauschenKnopf.setText(messenger.getMessage("GUI.KiGa.Exchange"));

		final JPanel tauschHauptPanel = new JPanel();
		tauschHauptPanel.setLayout(new GridBagLayout());
		gruppentauschPanel.add(tauschHauptPanel, BorderLayout.CENTER);

		final JLabel label_7 = new JLabel();
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.gridy = 1;
		gridBagConstraints_16.weightx = 1.0;
		tauschHauptPanel.add(label_7, gridBagConstraints_16);
		label_7.setText(messenger.getMessage("GUI.KiGa.FirstGroupToBeChanged"));

		final JLabel label_8 = new JLabel();
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.gridy = 3;
		gridBagConstraints_15.weightx = 1.0;
		tauschHauptPanel.add(label_8, gridBagConstraints_15);
		label_8.setText(messenger.getMessage("GUI.KiGa.SecondGroupToBeChanged"));

		tauschGruppe1 = new JSlider(1, 8, 1);
		tauschGruppe1.setLabelTable(tauschGruppe1.createStandardLabels(1));
		tauschGruppe1.setPaintLabels(true);
		tauschGruppe1.setMajorTickSpacing(1);
		tauschGruppe1.setPaintTicks(true);
		tauschGruppe1.setSnapToTicks(true);

		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.gridy = 2;
		tauschHauptPanel.add(tauschGruppe1, gridBagConstraints_17);

		tauschGruppe2 = new JSlider(1, 8, 2);
		tauschGruppe2.setLabelTable(tauschGruppe2.createStandardLabels(1));
		tauschGruppe2.setPaintLabels(true);
		tauschGruppe2.setMajorTickSpacing(1);
		tauschGruppe2.setPaintTicks(true);
		tauschGruppe2.setSnapToTicks(true);

		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.gridy = 4;
		tauschHauptPanel.add(tauschGruppe2, gridBagConstraints_18);

		final JSeparator separator_2 = new JSeparator();
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.weighty = 1.0;
		gridBagConstraints_19.anchor = GridBagConstraints.NORTH;
		gridBagConstraints_19.fill = GridBagConstraints.HORIZONTAL;
		tauschHauptPanel.add(separator_2, gridBagConstraints_19);

		final JSeparator separator_3 = new JSeparator();
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints_20.weighty = 1.0;
		gridBagConstraints_20.fill = GridBagConstraints.HORIZONTAL;
		gridBagConstraints_20.gridy = 5;
		tauschHauptPanel.add(separator_3, gridBagConstraints_20);
	}

	public Integer getKarteiID() {
		int row = ergebnisTabelle.getSelectedRow();
		Integer id = Integer.getInteger("0");
		if (row != -1) {
			//		zuerst ID herausfinden
			id = (Integer) (ergebnisModel.getValueAt(row, 0));
		}
		return id;
	}

	public int getRows() {
//		int rows = 0;
		return ergebnisTabelle.getRowCount();
	}

	public int getColumns() {
//		int columns = 0;
		return ergebnisTabelle.getColumnCount() - 1;
	}

	public KigaTable getErgebnisTable(){
		return ergebnisTabelle;
	}
	
	public JPanel getErgebnisPanel(){
		return ergebnisPanel;
	}
	
	public int getTauschGruppe1(){
		return tauschGruppe1.getValue();
	}
	
	public int getTauschgruppe2(){
		return tauschGruppe2.getValue();
	}

	public boolean getNamenSuchRadioButton(){
		return namenSucheRadioButton.isSelected();
	}
	public boolean getEhemaligeBox(){
		return ehemaligeBox.isSelected();
	}
	public String getSucheNachname(){
		return sucheNachname.getText();
	}

	public boolean getGruppenSucheRadioButton(){
		return gruppenSucheRadioButton.isSelected();
	}
	public byte getSucheGruppenModel(){
		return sucheGruppenModel.getNumber().byteValue();
	}
	
	public boolean getSucheDatumRadioButton(){
		return sucheDatumRadioButton.isSelected();
	}
	public String getSuchAnfangsDatum(){
		return suchAnfangsDatum.getText();
	}
	public String getSucheEndDatum(){
		return sucheEndDatum.getText();
	}
	
	public KarteikarteImpl getKarteiKarte(){
		return karteiKarte;
	}
    public void setKarteiKarte(KarteikarteImpl karteiKarte){
    	    this.karteiKarte = karteiKarte;
    }

    public KarteikartenDialog getDruckKartei(){
    	  return karteikartenDialog;
    }
 	/*
	 * (non-Javadoc)
	 * 
	 * @see Suchfenster#addKind(int, java.lang.String, java.lang.String,
	 *      java.lang.String, java.lang.String)
	 */
	public void addKind(int id, String vorname, String nachname,
			String geburtsdatum, String anschrift) {
		ergebnisModel.addRow(new Object[] { new Integer(id), vorname, nachname,
				geburtsdatum, anschrift });
		ergebnisTabelle.setEnabled(true);
		ergebnisDruckenKnopf.setEnabled(true);
		ergebnisAbspeicherKnopf.setEnabled(true);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getPrimaryKey()
	 */
	public int getPrimaryKey() {
		return karteiKarteSwi.getPrimaryKey();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setPrimaryKey(int)
	 */
	public void setPrimaryKey(int i) {
		karteiKarteSwi.setPrimaryKey(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getGruppe()
	 */
	public byte getGruppe() {
		return karteiKarteSwi.getGruppe();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setGruppe(short)
	 */
	public void setGruppe(byte i) {
		karteiKarteSwi.setGruppe(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getKindGeburtsDatum()
	 */
	public String getKindGeburtsDatum() {
		return karteiKarteSwi.getKindGeburtsDatum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setKindGeburtsDatum(java.util.Date)
	 */
	public void setKindGeburtsDatum(String i) {
		karteiKarteSwi.setKindGeburtsDatum(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getEintrittsDatum()
	 */
	public String getEintrittsDatum() {
		return karteiKarteSwi.getEintrittsDatum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setEintrittsDatum(java.lang.String)
	 */
	public void setEintrittsDatum(String i) {
		karteiKarteSwi.setEintrittsDatum(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getEintrittsGrund()
	 */
	public String getEintrittsGrund() {
		return karteiKarteSwi.getEintrittsGrund();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setEintrittsGrund(java.lang.String)
	 */
	public void setEintrittsGrund(String i) {
		karteiKarteSwi.setEintrittsGrund(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getAustrittsDatum()
	 */
	public String getAustrittsDatum() {
		return karteiKarteSwi.getAustrittsDatum();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setAustrittsDatum(java.lang.String)
	 */
	public void setAustrittsDatum(String i) {
		karteiKarteSwi.setAustrittsDatum(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getAustrittsGrund()
	 */
	public String getAustrittsGrund() {
		return karteiKarteSwi.getAustrittsGrund();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setAustrittsGrund(java.lang.String)
	 */
	public void setAustrittsGrund(String i) {
		karteiKarteSwi.setAustrittsGrund(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getKindNachname()
	 */
	public String getKindNachname() {
		return karteiKarteSwi.getKindNachname();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setKindNachname(java.lang.String)
	 */
	public void setKindNachname(String i) {
		karteiKarteSwi.setKindNachname(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getKindVorname()
	 */
	public String getKindVorname() {
		return karteiKarteSwi.getKindVorname();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setKindVorname(java.lang.String)
	 */
	public void setKindVorname(String i) {
		karteiKarteSwi.setKindVorname(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getGeburtsOrt()
	 */
	public String getGeburtsOrt() {
		return karteiKarteSwi.getGeburtsOrt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setGeburtsOrt(java.lang.String)
	 */
	public void setGeburtsOrt(String i) {
		karteiKarteSwi.setGeburtsOrt(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getKindWohnung()
	 */
	public String getKindWohnung() {
		return karteiKarteSwi.getKindWohnung();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setKindWohnung(java.lang.String)
	 */
	public void setKindWohnung(String i) {
		karteiKarteSwi.setKindWohnung(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getReligion()
	 */
	public String getReligion() {
		return karteiKarteSwi.getReligion();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setReligion(java.lang.String)
	 */
	public void setReligion(String i) {
		karteiKarteSwi.setReligion(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getStaat()
	 */
	public String getStaat() {
		return karteiKarteSwi.getStaat();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setStaat(java.lang.String)
	 */
	public void setStaat(String i) {
		karteiKarteSwi.setStaat(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getKindTelefon()
	 */
	public String getKindTelefon() {
		return karteiKarteSwi.getKindTelefon();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setKindTelefon(java.lang.String)
	 */
	public void setKindTelefon(String i) {
		karteiKarteSwi.setKindTelefon(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getVaterName()
	 */
	public String getVaterName() {
		return karteiKarteSwi.getVaterName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setVaterName(java.lang.String)
	 */
	public void setVaterName(String i) {
		karteiKarteSwi.setVaterName(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getVaterGeburt()
	 */
	public String getVaterGeburt() {
		return karteiKarteSwi.getVaterGeburt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setVaterGeburt(java.lang.String)
	 */
	public void setVaterGeburt(String i) {
		karteiKarteSwi.setVaterGeburt(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getVaterBeruf()
	 */
	public String getVaterBeruf() {
		return karteiKarteSwi.getVaterBeruf();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setVaterBeruf(java.lang.String)
	 */
	public void setVaterBeruf(String i) {
		karteiKarteSwi.setVaterBeruf(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getMutterName()
	 */
	public String getMutterName() {
		return karteiKarteSwi.getMutterName();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setMutterName(java.lang.String)
	 */
	public void setMutterName(String i) {
		karteiKarteSwi.setMutterName(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getMutterGeburt()
	 */
	public String getMutterGeburt() {
		return karteiKarteSwi.getMutterGeburt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setMutterGeburt(java.lang.String)
	 */
	public void setMutterGeburt(String i) {
		karteiKarteSwi.setMutterGeburt(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getMutterBeruf()
	 */
	public String getMutterBeruf() {
		return karteiKarteSwi.getMutterBeruf();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setMutterBeruf(java.lang.String)
	 */
	public void setMutterBeruf(String i) {
		karteiKarteSwi.setMutterBeruf(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getSorgePerson()
	 */
	public String getSorgePerson() {
		return karteiKarteSwi.getSorgePerson();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setSorgePerson(java.lang.String)
	 */
	public void setSorgePerson(String i) {
		karteiKarteSwi.setSorgePerson(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getArbeitsOrt1()
	 */
	public String getArbeitsOrt1() {
		return karteiKarteSwi.getArbeitsOrt1();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setArbeitsOrt1(java.lang.String)
	 */
	public void setArbeitsOrt1(String i) {
		karteiKarteSwi.setArbeitsOrt1(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getArbeitsTelefon1()
	 */
	public String getArbeitsTelefon1() {
		return karteiKarteSwi.getArbeitsTelefon1();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setArbeitsTelefon1(java.lang.String)
	 */
	public void setArbeitsTelefon1(String i) {
		karteiKarteSwi.setArbeitsTelefon1(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getArbeitsOrt2()
	 */
	public String getArbeitsOrt2() {
		return karteiKarteSwi.getArbeitsOrt2();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setArbeitsOrt2(java.lang.String)
	 */
	public void setArbeitsOrt2(String i) {
		karteiKarteSwi.setArbeitsOrt2(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getArbeitsTelefon2()
	 */
	public String getArbeitsTelefon2() {
		return karteiKarteSwi.getArbeitsTelefon2();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setArbeitsTelefon2(java.lang.String)
	 */
	public void setArbeitsTelefon2(String i) {
		karteiKarteSwi.setArbeitsTelefon2(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getGeschwisterGeburt()
	 */
	public String getGeschwisterGeburt() {
		return karteiKarteSwi.getGeschwisterGeburt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setGeschwisterGeburt(java.lang.String)
	 */
	public void setGeschwisterGeburt(String i) {
		karteiKarteSwi.setGeschwisterGeburt(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getFamilienStand()
	 */
	public byte getFamilienStand() {
		return karteiKarteSwi.getFamilienStand();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setFamilienStand(short)
	 */
	public void setFamilienStand(byte i) {
		karteiKarteSwi.setFamilienStand(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getElternOrt()
	 */
	public String getElternOrt() {
		return karteiKarteSwi.getElternOrt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setElternOrt(java.lang.String)
	 */
	public void setElternOrt(String i) {
		karteiKarteSwi.setElternOrt(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getElternTelefon()
	 */
	public String getElternTelefon() {
		return karteiKarteSwi.getElternTelefon();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setElternTelefon(java.lang.String)
	 */
	public void setElternTelefon(String i) {
		karteiKarteSwi.setElternTelefon(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getAnzahlGeschwister()
	 */
	public byte getAnzahlGeschwister() {
		return karteiKarteSwi.getAnzahlGeschwister();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setAnzahlGeschwister(short)
	 */
	public void setAnzahlGeschwister(byte i) {
		karteiKarteSwi.setAnzahlGeschwister(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getImpfungen()
	 */
	public String getImpfungen() {
		return karteiKarteSwi.getImpfungen();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setImpfungen(java.lang.String)
	 */
	public void setImpfungen(String i) {
		karteiKarteSwi.setImpfungen(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getTetanusZeit()
	 */
	public String getTetanusZeit() {
		return karteiKarteSwi.getTetanusZeit();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setTetanusZeit(java.lang.String)
	 */
	public void setTetanusZeit(String i) {
		karteiKarteSwi.setTetanusZeit(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getKrankheiten()
	 */
	public short getKrankheiten() {
		return karteiKarteSwi.getKrankheiten();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setKrankheiten(short)
	 */
	public void setKrankheiten(short i) {
		karteiKarteSwi.setKrankheiten(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getWeitereKrankheiten()
	 */
	public String getWeitereKrankheiten() {
		return karteiKarteSwi.getWeitereKrankheiten();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setWeitereKrankheiten(java.lang.String)
	 *  
	 */
	public void setWeitereKrankheiten(String i) {
		karteiKarteSwi.setWeitereKrankheiten(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getGesundheitsHinweise()
	 */
	public String getGesundheitsHinweise() {
		return karteiKarteSwi.getGesundheitsHinweise();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setGesundheitshinweise(java.lang.String)
	 */
	public void setGesundheitshinweise(String i) {
		karteiKarteSwi.setGesundheitshinweise(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getArztOrt()
	 */
	public String getArztOrt() {
		return karteiKarteSwi.getArztOrt();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setArztOrt(java.lang.String)
	 */
	public void setArztOrt(String i) {
		karteiKarteSwi.setArztOrt(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getArztTelefon()
	 */
	public String getArztTelefon() {
		return karteiKarteSwi.getArztTelefon();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setArztTelefon(java.lang.String)
	 */
	public void setArztTelefon(String i) {
		karteiKarteSwi.setArztTelefon(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getKrankenkasse()
	 */
	public String getKrankenkasse() {
		return karteiKarteSwi.getKrankenkasse();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setKrankenkasse(java.lang.String)
	 */
	public void setKrankenkasse(String i) {
		karteiKarteSwi.setKrankenkasse(i);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#getSonstiges()
	 */
	public String getSonstiges() {
		return karteiKarteSwi.getSonstiges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see Karteikarte#setSonstiges(java.lang.String)
	 */
	public void setSonstiges(String i) {
		karteiKarteSwi.setSonstiges(i);
	}

	/**
	 * 
	 * L�scht die Daten aller Felder
	 */
	public void leeren() {
		karteiKarteSwi.leeren();
		//		graue Tabelle aus
		ergebnisTabelle.setEnabled(false);
		// lösche alle Daten aus Tabelle
		ergebnisModel.setRowCount(0);
		// graue Knöpfe aus
		ergebnisDruckenKnopf.setEnabled(false);
		ergebnisAbspeicherKnopf.setEnabled(false);
	}

}
