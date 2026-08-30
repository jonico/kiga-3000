package org.de.kiga3000.views;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.messages.Messenger;


/*
 * Created on 03.09.2004
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

  /* internationalisation added by bdiemer 2005-29-11  */

//public class KarteiKarteSwingImpl extends JPanel implements Karteikarte {
public class KarteiKarteSwingImpl extends JPanel {

	private static final byte VERHEIRATET=1;
	private static final byte LEDIG=2;
	private static final byte GESCHIEDEN=4;
	private static final byte VERWITWET=8;
	private static final byte GETRENNT=16;
	private static final short MASERN=1;
	private static final short KEUCHHUSTEN=2;
	private static final short SCHARLACH=4;
	private static final short DIPHTERIE=8;
	private static final short MUMPS=16;
	private static final short ROETELN=32;
	private static final short WINDPOCKEN=64;
	private static final short GELBSUCHT=128;
	private static final short KINDERLAEHMUNG=256;
	private SpinnerNumberModel gruppemodel;
	private SpinnerNumberModel anzahlgeschwmodel;
	private JTextArea sonstiges;
	private JTextArea gesundheit;
	private JTextField arztTel;
	private JTextField arztOrt;
	private JTextField tetanusZeit;
	private JTextField wkrankheit;
	private JTextField krankenKasse;
	private JTextField impfung;
	private JTextField geschwgeburt;
	private JSpinner anzahlgeschw;
	private JTextField elternTel;
	private JTextField elternOrt;
	private JTextField arbeitsTel2;
	private JTextField arbeitsOrt2;
	private JTextField arbeitsTel1;
	private JTextField arbeitsOrt1;
	private JTextField sorgePerson;
	private JTextField mutterBeruf;
	private JTextField mutterGeburt;
	private JTextField mutterName;
	private JTextField vaterBeruf;
	private JTextField vaterGeburt;
	private JTextField vaterName;
	private JTextField kindTelefon;
	private JTextField staat;
	private JTextField kindWohnung;
	private JTextField religion;
	private JTextField geburtsOrt;
	private JTextField vornamekind;
	private JTextField nachnamekind;
	private JTextField austrittsGrund;
	private JTextField eintrittsGrund;
	private JSpinner gruppe;
	private JTextField kindGeburtText;
//	private JCheckBox verheiratet;			// >>> deleted by bdiemer 2005-02-12
//	private JCheckBox geschieden;			// >>> deleted by bdiemer 2005-02-12
//	private JCheckBox ledig;				// >>> deleted by bdiemer 2005-02-12
//	private JCheckBox verwitwet;			// >>> deleted by bdiemer 2005-02-12
//	private JCheckBox getrennt;				// >>> deleted by bdiemer 2005-02-12
	private JRadioButton verheiratet; 			// <<< insert by bdiemer 2005-02-12
	private JRadioButton geschieden;			// <<< insert by bdiemer 2005-02-12
	private JRadioButton ledig;				// <<< insert by bdiemer 2005-02-12
	private JRadioButton verwitwet;			// <<< insert by bdiemer 2005-02-12
	private JRadioButton getrennt;				// <<< insert by bdiemer 2005-02-12

	private JCheckBox masern;
	private JCheckBox keuchhusten;
	private JCheckBox scharlach;
	private JCheckBox diphterie;
	private JCheckBox mumps;
	private JCheckBox windpocken;
	private JCheckBox gelbsucht;
	private JCheckBox kinderlaehmung;
	private JCheckBox roeteln;
	private int primaryKey;
	
	private JTextField austritt;
	private JTextField eintritt;
	private static Messenger messenger = new Messenger();
	
	public KarteiKarteSwingImpl() {
		super();
		setMinimumSize(new Dimension(950, 650));
		setLayout(new GridBagLayout());

		final JLabel label = new JLabel();
		final GridBagConstraints gridBagConstraints_3 = new GridBagConstraints();
		gridBagConstraints_3.weightx = 1.0;
		gridBagConstraints_3.gridx = 1;
		gridBagConstraints_3.gridy = 1;
		add(label, gridBagConstraints_3);
		label.setText(messenger.getMessage("GUI.KiGa.GebDatKind"));

		final JLabel label_1 = new JLabel();
		final GridBagConstraints gridBagConstraints = new GridBagConstraints();
		gridBagConstraints.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints.gridy = 1;
		gridBagConstraints.gridx = 3;
		add(label_1, gridBagConstraints);
		label_1.setText(messenger.getMessage("GUI.KiGa.Group"));

		final JLabel label_2 = new JLabel();
		final GridBagConstraints gridBagConstraints_1 = new GridBagConstraints();
		gridBagConstraints_1.weightx = 1.0;
		gridBagConstraints_1.gridy = 1;
		gridBagConstraints_1.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_1.gridx = 7;
		add(label_2, gridBagConstraints_1);
		label_2.setText(messenger.getMessage("GUI.KiGa.EntryDate"));

		final JLabel label_3 = new JLabel();
		final GridBagConstraints gridBagConstraints_2 = new GridBagConstraints();
		gridBagConstraints_2.weightx = 1.0;
		gridBagConstraints_2.insets = new Insets(0, 0, 0, 0);
		gridBagConstraints_2.gridy = 1;
		gridBagConstraints_2.gridx = 11;
		add(label_3, gridBagConstraints_2);
		label_3.setText(messenger.getMessage("GUI.KiGa.LeaveDate"));

		kindGeburtText = new JTextField();
		kindGeburtText.setColumns(10);
		final GridBagConstraints gridBagConstraints_4 = new GridBagConstraints();
		gridBagConstraints_4.gridx = 1;
		gridBagConstraints_4.fill = GridBagConstraints.BOTH;
		gridBagConstraints_4.gridy = 2;
		add(kindGeburtText, gridBagConstraints_4);

		gruppemodel = new SpinnerNumberModel(1,1,9,1);
		gruppe = new JSpinner(gruppemodel);
		gruppe.setToolTipText(messenger.getMessage("KiGa.ToolTip.GroupAdm"));
		gruppe.setName("Gruppe");
		final GridBagConstraints gridBagConstraints_5 = new GridBagConstraints();
		gridBagConstraints_5.fill = GridBagConstraints.BOTH;
		gridBagConstraints_5.gridy = 2;
		gridBagConstraints_5.gridx = 3;
		add(gruppe, gridBagConstraints_5);

		eintritt = new JTextField();
		eintritt.setColumns(10);
		final GridBagConstraints gridBagConstraints_6 = new GridBagConstraints();
		gridBagConstraints_6.fill = GridBagConstraints.BOTH;
		gridBagConstraints_6.gridy = 2;
		gridBagConstraints_6.gridx = 7;
		add(eintritt, gridBagConstraints_6);

		austritt = new JTextField();
		austritt.setColumns(10);
		final GridBagConstraints gridBagConstraints_7 = new GridBagConstraints();
		gridBagConstraints_7.fill = GridBagConstraints.BOTH;
		gridBagConstraints_7.gridy = 2;
		gridBagConstraints_7.gridx = 11;
		add(austritt, gridBagConstraints_7);

		final JLabel label_4 = new JLabel();
		final GridBagConstraints gridBagConstraints_8 = new GridBagConstraints();
		gridBagConstraints_8.anchor = GridBagConstraints.SOUTH;
		gridBagConstraints_8.gridx = 7;
		gridBagConstraints_8.gridy = 3;
		add(label_4, gridBagConstraints_8);
		label_4.setText(messenger.getMessage("GUI.KiGa.EntryReason"));

		final JLabel label_6 = new JLabel();
		final GridBagConstraints gridBagConstraints_9 = new GridBagConstraints();
		gridBagConstraints_9.gridy = 3;
		gridBagConstraints_9.gridx = 11;
		add(label_6, gridBagConstraints_9);
		label_6.setText(messenger.getMessage("GUI.KiGa.LeaveReason"));

		eintrittsGrund = new JTextField();
		eintrittsGrund.setColumns(50);
		final GridBagConstraints gridBagConstraints_10 = new GridBagConstraints();
		gridBagConstraints_10.gridx = 7;
		gridBagConstraints_10.fill = GridBagConstraints.BOTH;
		gridBagConstraints_10.gridy = 4;
		add(eintrittsGrund, gridBagConstraints_10);

		austrittsGrund = new JTextField();
		austrittsGrund.setColumns(50);
		final GridBagConstraints gridBagConstraints_11 = new GridBagConstraints();
		gridBagConstraints_11.fill = GridBagConstraints.BOTH;
		gridBagConstraints_11.gridy = 4;
		gridBagConstraints_11.gridx = 11;
		add(austrittsGrund, gridBagConstraints_11);

		final JSeparator separator = new JSeparator();
		final GridBagConstraints gridBagConstraints_12 = new GridBagConstraints();
		gridBagConstraints_12.gridwidth = 13;
		gridBagConstraints_12.fill = GridBagConstraints.HORIZONTAL;
		add(separator, gridBagConstraints_12);

		final JSeparator separator_1 = new JSeparator();
		separator_1.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_13 = new GridBagConstraints();
		gridBagConstraints_13.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_13.gridheight = 32;
		gridBagConstraints_13.gridy = 1;
		add(separator_1, gridBagConstraints_13);

		final JSeparator separator_2 = new JSeparator();
		separator_2.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_14 = new GridBagConstraints();
		gridBagConstraints_14.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_14.gridheight = 32;
		gridBagConstraints_14.gridy = 1;
		gridBagConstraints_14.gridx = 12;
		add(separator_2, gridBagConstraints_14);

		final JSeparator separator_3 = new JSeparator();
		separator_3.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_15 = new GridBagConstraints();
		gridBagConstraints_15.gridheight = 13;
		gridBagConstraints_15.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_15.gridy = 1;
		gridBagConstraints_15.gridx = 2;
		add(separator_3, gridBagConstraints_15);

		final JSeparator separator_4 = new JSeparator();
		separator_4.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_16 = new GridBagConstraints();
		gridBagConstraints_16.gridheight = 4;
		gridBagConstraints_16.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_16.gridy = 1;
		gridBagConstraints_16.gridx = 6;
		add(separator_4, gridBagConstraints_16);

		final JLabel label_7 = new JLabel();
		label_7.setText(messenger.getMessage("GUI.KiGa.NameOfChild"));
		final GridBagConstraints gridBagConstraints_17 = new GridBagConstraints();
		gridBagConstraints_17.gridy = 5;
		gridBagConstraints_17.gridx = 1;
		add(label_7, gridBagConstraints_17);

		final JLabel label_8 = new JLabel();
		final GridBagConstraints gridBagConstraints_18 = new GridBagConstraints();
		gridBagConstraints_18.gridwidth = 5;
		gridBagConstraints_18.gridy = 5;
		gridBagConstraints_18.gridx = 3;
		add(label_8, gridBagConstraints_18);
		label_8.setText(messenger.getMessage("GUI.KiGa.SurnameOfChild"));

		nachnamekind = new JTextField();
		nachnamekind.setColumns(50);
		final GridBagConstraints gridBagConstraints_19 = new GridBagConstraints();
		gridBagConstraints_19.gridwidth = 2;
		gridBagConstraints_19.fill = GridBagConstraints.BOTH;
		gridBagConstraints_19.gridy = 6;
		gridBagConstraints_19.gridx = 1;
		add(nachnamekind, gridBagConstraints_19);

		vornamekind = new JTextField();
		vornamekind.setColumns(50);
		final GridBagConstraints gridBagConstraints_20 = new GridBagConstraints();
		gridBagConstraints_20.gridwidth = 5;
		gridBagConstraints_20.fill = GridBagConstraints.BOTH;
		gridBagConstraints_20.gridy = 6;
		gridBagConstraints_20.gridx = 3;
		add(vornamekind, gridBagConstraints_20);

		final JLabel label_9 = new JLabel();
		final GridBagConstraints gridBagConstraints_21 = new GridBagConstraints();
		gridBagConstraints_21.gridy = 7;
		gridBagConstraints_21.gridx = 1;
		add(label_9, gridBagConstraints_21);
		label_9.setText(messenger.getMessage("GUI.KiGa.CityOfBirth"));

		geburtsOrt = new JTextField();
		geburtsOrt.setColumns(50);
		final GridBagConstraints gridBagConstraints_22 = new GridBagConstraints();
		gridBagConstraints_22.fill = GridBagConstraints.BOTH;
		gridBagConstraints_22.gridy = 8;
		gridBagConstraints_22.gridx = 1;
		add(geburtsOrt, gridBagConstraints_22);

		final JLabel label_10 = new JLabel();
		final GridBagConstraints gridBagConstraints_23 = new GridBagConstraints();
		gridBagConstraints_23.gridy = 5;
		gridBagConstraints_23.gridx = 11;
		add(label_10, gridBagConstraints_23);
		label_10.setText(messenger.getMessage("GUI.KiGa.Religion"));

		religion = new JTextField();
		religion.setColumns(25);
		final GridBagConstraints gridBagConstraints_24 = new GridBagConstraints();
		gridBagConstraints_24.fill = GridBagConstraints.BOTH;
		gridBagConstraints_24.gridy = 6;
		gridBagConstraints_24.gridx = 11;
		add(religion, gridBagConstraints_24);
		religion.setText("");

		final JLabel label_11 = new JLabel();
		final GridBagConstraints gridBagConstraints_25 = new GridBagConstraints();
		gridBagConstraints_25.gridwidth = 9;
		gridBagConstraints_25.gridy = 7;
		gridBagConstraints_25.gridx = 3;
		add(label_11, gridBagConstraints_25);
		label_11.setText(messenger.getMessage("GUI.KiGa.AdressChild"));

		kindWohnung = new JTextField();
		kindWohnung.setColumns(100);
		final GridBagConstraints gridBagConstraints_26 = new GridBagConstraints();
		gridBagConstraints_26.gridwidth = 9;
		gridBagConstraints_26.fill = GridBagConstraints.BOTH;
		gridBagConstraints_26.gridy = 8;
		gridBagConstraints_26.gridx = 3;
		add(kindWohnung, gridBagConstraints_26);

		final JLabel label_12 = new JLabel();
		final GridBagConstraints gridBagConstraints_27 = new GridBagConstraints();
		gridBagConstraints_27.gridy = 9;
		gridBagConstraints_27.gridx = 1;
		add(label_12, gridBagConstraints_27);
		label_12.setText(messenger.getMessage("GUI.KiGa.Nationality"));

		final JLabel label_13 = new JLabel();
		final GridBagConstraints gridBagConstraints_28 = new GridBagConstraints();
		gridBagConstraints_28.gridwidth = 9;
		gridBagConstraints_28.gridy = 9;
		gridBagConstraints_28.gridx = 3;
		add(label_13, gridBagConstraints_28);
		label_13.setText(messenger.getMessage("GUI.KiGa.ContTelNumber"));

		staat = new JTextField();
		staat.setColumns(40);
		final GridBagConstraints gridBagConstraints_29 = new GridBagConstraints();
		gridBagConstraints_29.fill = GridBagConstraints.BOTH;
		gridBagConstraints_29.gridy = 10;
		gridBagConstraints_29.gridx = 1;
		add(staat, gridBagConstraints_29);

		kindTelefon = new JTextField();
		kindTelefon.setColumns(100);
		final GridBagConstraints gridBagConstraints_30 = new GridBagConstraints();
		gridBagConstraints_30.gridwidth = 9;
		gridBagConstraints_30.fill = GridBagConstraints.BOTH;
		gridBagConstraints_30.gridy = 10;
		gridBagConstraints_30.gridx = 3;
		add(kindTelefon, gridBagConstraints_30);

		final JLabel label_14 = new JLabel();
		final GridBagConstraints gridBagConstraints_31 = new GridBagConstraints();
		gridBagConstraints_31.gridy = 11;
		gridBagConstraints_31.gridx = 1;
		add(label_14, gridBagConstraints_31);
		label_14.setText(messenger.getMessage("GUI.KiGa.NameOfParents"));

		final JLabel label_15 = new JLabel();
		label_15.setText(messenger.getMessage("GUI.KiGa.Parent"));
		final GridBagConstraints gridBagConstraints_32 = new GridBagConstraints();
		gridBagConstraints_32.gridy = 11;
		gridBagConstraints_32.gridx = 3;
		add(label_15, gridBagConstraints_32);

		final JLabel label_16 = new JLabel();
		final GridBagConstraints gridBagConstraints_33 = new GridBagConstraints();
		gridBagConstraints_33.gridy = 11;
		gridBagConstraints_33.gridx = 7;
		add(label_16, gridBagConstraints_33);
		label_16.setText(messenger.getMessage("GUI.KiGa.DateOfBirth"));

		final JLabel label_17 = new JLabel();
		final GridBagConstraints gridBagConstraints_34 = new GridBagConstraints();
		gridBagConstraints_34.gridy = 11;
		gridBagConstraints_34.gridx = 11;
		add(label_17, gridBagConstraints_34);
		label_17.setText(messenger.getMessage("GUI.KiGa.Profession"));

		vaterName = new JTextField();
		vaterName.setColumns(70);
		final GridBagConstraints gridBagConstraints_35 = new GridBagConstraints();
		gridBagConstraints_35.fill = GridBagConstraints.BOTH;
		gridBagConstraints_35.gridy = 12;
		gridBagConstraints_35.gridx = 1;
		add(vaterName, gridBagConstraints_35);

		final JLabel label_18 = new JLabel();
		final GridBagConstraints gridBagConstraints_36 = new GridBagConstraints();
		gridBagConstraints_36.gridy = 12;
		gridBagConstraints_36.gridx = 3;
		add(label_18, gridBagConstraints_36);
		label_18.setText(messenger.getMessage("GUI.KiGa.Father"));

		vaterGeburt = new JTextField();
		vaterGeburt.setColumns(10);
		final GridBagConstraints gridBagConstraints_37 = new GridBagConstraints();
		gridBagConstraints_37.fill = GridBagConstraints.BOTH;
		gridBagConstraints_37.gridy = 12;
		gridBagConstraints_37.gridx = 7;
		add(vaterGeburt, gridBagConstraints_37);

		vaterBeruf = new JTextField();
		final GridBagConstraints gridBagConstraints_38 = new GridBagConstraints();
		gridBagConstraints_38.fill = GridBagConstraints.BOTH;
		gridBagConstraints_38.gridy = 12;
		gridBagConstraints_38.gridx = 11;
		add(vaterBeruf, gridBagConstraints_38);

		mutterName = new JTextField();
		mutterName.setColumns(70);
		final GridBagConstraints gridBagConstraints_39 = new GridBagConstraints();
		gridBagConstraints_39.fill = GridBagConstraints.BOTH;
		gridBagConstraints_39.gridy = 13;
		gridBagConstraints_39.gridx = 1;
		add(mutterName, gridBagConstraints_39);

		final JLabel label_19 = new JLabel();
		final GridBagConstraints gridBagConstraints_40 = new GridBagConstraints();
		gridBagConstraints_40.gridy = 13;
		gridBagConstraints_40.gridx = 3;
		add(label_19, gridBagConstraints_40);
		label_19.setText(messenger.getMessage("GUI.KiGa.Mother"));

		mutterGeburt = new JTextField();
		mutterGeburt.setColumns(10);
		final GridBagConstraints gridBagConstraints_41 = new GridBagConstraints();
		gridBagConstraints_41.fill = GridBagConstraints.BOTH;
		gridBagConstraints_41.gridy = 13;
		gridBagConstraints_41.gridx = 7;
		add(mutterGeburt, gridBagConstraints_41);

		mutterBeruf = new JTextField();
		final GridBagConstraints gridBagConstraints_42 = new GridBagConstraints();
		gridBagConstraints_42.fill = GridBagConstraints.BOTH;
		gridBagConstraints_42.gridy = 13;
		gridBagConstraints_42.gridx = 11;
		add(mutterBeruf, gridBagConstraints_42);

		final JLabel label_20 = new JLabel();
		final GridBagConstraints gridBagConstraints_43 = new GridBagConstraints();
		gridBagConstraints_43.gridwidth = 11;
		gridBagConstraints_43.gridy = 14;
		gridBagConstraints_43.gridx = 1;
		add(label_20, gridBagConstraints_43);
		label_20.setText(messenger.getMessage("GUI.KiGa.OtherCarePers"));

		sorgePerson = new JTextField();
		sorgePerson.setColumns(255);
		final GridBagConstraints gridBagConstraints_44 = new GridBagConstraints();
		gridBagConstraints_44.gridwidth = 11;
		gridBagConstraints_44.fill = GridBagConstraints.BOTH;
		gridBagConstraints_44.gridy = 15;
		gridBagConstraints_44.gridx = 1;
		add(sorgePerson, gridBagConstraints_44);

		final JLabel label_21 = new JLabel();
		final GridBagConstraints gridBagConstraints_45 = new GridBagConstraints();
		gridBagConstraints_45.gridwidth = 3;
		gridBagConstraints_45.gridy = 16;
		gridBagConstraints_45.gridx = 1;
		add(label_21, gridBagConstraints_45);
		label_21.setText(messenger.getMessage("GUI.KiGa.CarePersWork"));

		final JLabel label_22 = new JLabel();
		final GridBagConstraints gridBagConstraints_46 = new GridBagConstraints();
		gridBagConstraints_46.gridwidth = 5;
		gridBagConstraints_46.gridy = 16;
		gridBagConstraints_46.gridx = 7;
		add(label_22, gridBagConstraints_46);
		label_22.setText(messenger.getMessage("GUI.KiGa.CarePersWorkTel"));

		arbeitsOrt1 = new JTextField();
		arbeitsOrt1.setColumns(50);
		final GridBagConstraints gridBagConstraints_47 = new GridBagConstraints();
		gridBagConstraints_47.gridwidth = 3;
		gridBagConstraints_47.fill = GridBagConstraints.BOTH;
		gridBagConstraints_47.gridy = 17;
		gridBagConstraints_47.gridx = 1;
		add(arbeitsOrt1, gridBagConstraints_47);

		arbeitsTel1 = new JTextField();
		arbeitsTel1.setColumns(100);
		final GridBagConstraints gridBagConstraints_48 = new GridBagConstraints();
		gridBagConstraints_48.gridwidth = 5;
		gridBagConstraints_48.fill = GridBagConstraints.BOTH;
		gridBagConstraints_48.gridy = 17;
		gridBagConstraints_48.gridx = 7;
		add(arbeitsTel1, gridBagConstraints_48);

		arbeitsOrt2 = new JTextField();
		arbeitsOrt2.setColumns(50);
		final GridBagConstraints gridBagConstraints_49 = new GridBagConstraints();
		gridBagConstraints_49.gridwidth = 3;
		gridBagConstraints_49.fill = GridBagConstraints.BOTH;
		gridBagConstraints_49.gridy = 18;
		gridBagConstraints_49.gridx = 1;
		add(arbeitsOrt2, gridBagConstraints_49);

		arbeitsTel2 = new JTextField();
		arbeitsTel2.setColumns(100);
		final GridBagConstraints gridBagConstraints_50 = new GridBagConstraints();
		gridBagConstraints_50.gridwidth = 5;
		gridBagConstraints_50.fill = GridBagConstraints.BOTH;
		gridBagConstraints_50.gridy = 18;
		gridBagConstraints_50.gridx = 7;
		add(arbeitsTel2, gridBagConstraints_50);

		final JLabel label_23 = new JLabel();
		final GridBagConstraints gridBagConstraints_51 = new GridBagConstraints();
		gridBagConstraints_51.gridwidth = 3;
		gridBagConstraints_51.gridy = 19;
		gridBagConstraints_51.gridx = 1;
		add(label_23, gridBagConstraints_51);
		label_23.setText(messenger.getMessage("GUI.KiGa.ParentsAdress"));

		final JLabel label_24 = new JLabel();
		final GridBagConstraints gridBagConstraints_52 = new GridBagConstraints();
		gridBagConstraints_52.gridwidth = 5;
		gridBagConstraints_52.gridy = 19;
		gridBagConstraints_52.gridx = 7;
		add(label_24, gridBagConstraints_52);
		label_24.setText(messenger.getMessage("GUI.KiGa.ParentsTel"));

		elternOrt = new JTextField();
		elternOrt.setColumns(100);
		final GridBagConstraints gridBagConstraints_53 = new GridBagConstraints();
		gridBagConstraints_53.gridwidth = 3;
		gridBagConstraints_53.fill = GridBagConstraints.BOTH;
		gridBagConstraints_53.gridy = 20;
		gridBagConstraints_53.gridx = 1;
		add(elternOrt, gridBagConstraints_53);

		elternTel = new JTextField();
		elternTel.setColumns(100);
		final GridBagConstraints gridBagConstraints_54 = new GridBagConstraints();
		gridBagConstraints_54.gridwidth = 5;
		gridBagConstraints_54.fill = GridBagConstraints.BOTH;
		gridBagConstraints_54.gridy = 20;
		gridBagConstraints_54.gridx = 7;
		add(elternTel, gridBagConstraints_54);

		final JLabel label_5 = new JLabel();
		final GridBagConstraints gridBagConstraints_55 = new GridBagConstraints();
		gridBagConstraints_55.gridwidth = 11;
		gridBagConstraints_55.gridy = 21;
		gridBagConstraints_55.gridx = 1;
		add(label_5, gridBagConstraints_55);
		label_5.setText(messenger.getMessage("GUI.KiGa.ParentsTitel"));

		final JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
		final GridBagConstraints gridBagConstraints_56 = new GridBagConstraints();
		gridBagConstraints_56.gridwidth = 11;
		gridBagConstraints_56.gridy = 22;
		gridBagConstraints_56.gridx = 1;
		add(panel, gridBagConstraints_56);

		ButtonGroup family = new ButtonGroup();
//		verheiratet = new JCheckBox();
		verheiratet = new JRadioButton();
		panel.add(verheiratet);
		family.add(verheiratet);	
		verheiratet.setText(messenger.getMessage("GUI.KiGa.Married"));

//		geschieden = new JCheckBox();
		geschieden = new JRadioButton();
		panel.add(geschieden);
		family.add(geschieden);
		geschieden.setText(messenger.getMessage("GUI.KiGa.Divorced"));

//		ledig = new JCheckBox();
		ledig = new JRadioButton();
		panel.add(ledig);
		family.add(ledig);
		ledig.setText(messenger.getMessage("GUI.KiGa.Unmarried"));

//		verwitwet = new JCheckBox();
		verwitwet = new JRadioButton();
		panel.add(verwitwet);
		family.add(verwitwet);
		verwitwet.setText(messenger.getMessage("GUI.KiGa.Widowed"));

		getrennt = new JRadioButton();
		panel.add(getrennt);
		family.add(getrennt);
		getrennt.setText(messenger.getMessage("GUI.KiGa.Separated"));
//        panel.add(family);
		
		final JLabel label_25 = new JLabel();
		final GridBagConstraints gridBagConstraints_58 = new GridBagConstraints();
		gridBagConstraints_58.gridy = 3;
		gridBagConstraints_58.gridx = 3;
		add(label_25, gridBagConstraints_58);
		label_25.setText(messenger.getMessage("GUI.KiGa.NumberOfSiblings"));

		final JLabel label_26 = new JLabel();
		final GridBagConstraints gridBagConstraints_59 = new GridBagConstraints();
		gridBagConstraints_59.gridy = 3;
		gridBagConstraints_59.gridx = 1;
		add(label_26, gridBagConstraints_59);
		label_26.setText(messenger.getMessage("GUI.KiGa.YOBSiblings"));


		anzahlgeschwmodel = new SpinnerNumberModel(0,0,127,1);
		anzahlgeschw = new JSpinner(anzahlgeschwmodel);
		final GridBagConstraints gridBagConstraints_60 = new GridBagConstraints();
		gridBagConstraints_60.fill = GridBagConstraints.BOTH;
		gridBagConstraints_60.gridy = 4;
		gridBagConstraints_60.gridx = 3;
		add(anzahlgeschw, gridBagConstraints_60);

		geschwgeburt = new JTextField();
		geschwgeburt.setColumns(50);
		final GridBagConstraints gridBagConstraints_61 = new GridBagConstraints();
		gridBagConstraints_61.fill = GridBagConstraints.BOTH;
		gridBagConstraints_61.gridx = 1;
		gridBagConstraints_61.gridy = 4;
		add(geschwgeburt, gridBagConstraints_61);

		final JSeparator separator_5 = new JSeparator();
		separator_5.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_62 = new GridBagConstraints();
		gridBagConstraints_62.gridheight = 6;
		gridBagConstraints_62.fill = GridBagConstraints.BOTH;
		gridBagConstraints_62.gridy = 1;
		gridBagConstraints_62.gridx = 10;
		add(separator_5, gridBagConstraints_62);

		final JSeparator separator_6 = new JSeparator();
		separator_6.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_63 = new GridBagConstraints();
		gridBagConstraints_63.gridheight = 3;
		gridBagConstraints_63.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_63.gridy = 11;
		gridBagConstraints_63.gridx = 10;
		add(separator_6, gridBagConstraints_63);

		final JSeparator separator_7 = new JSeparator();
		separator_7.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_64 = new GridBagConstraints();
		gridBagConstraints_64.gridheight = 5;
		gridBagConstraints_64.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_64.gridy = 16;
		gridBagConstraints_64.gridx = 6;
		add(separator_7, gridBagConstraints_64);

		final JLabel label_27 = new JLabel();
		final GridBagConstraints gridBagConstraints_65 = new GridBagConstraints();
		gridBagConstraints_65.gridwidth = 7;
		gridBagConstraints_65.gridx = 1;
		gridBagConstraints_65.gridy = 23;
		add(label_27, gridBagConstraints_65);
		label_27.setText(messenger.getMessage("GUI.KiGa.Vaccination"));

		impfung = new JTextField();
		impfung.setColumns(100);
		final GridBagConstraints gridBagConstraints_66 = new GridBagConstraints();
		gridBagConstraints_66.gridwidth = 7;
		gridBagConstraints_66.fill = GridBagConstraints.BOTH;
		gridBagConstraints_66.gridy = 24;
		gridBagConstraints_66.gridx = 1;
		add(impfung, gridBagConstraints_66);
		impfung.setText("");

		final JLabel label_28 = new JLabel();
		final GridBagConstraints gridBagConstraints_67 = new GridBagConstraints();
		gridBagConstraints_67.gridwidth = 5;
		gridBagConstraints_67.gridx = 7;
		gridBagConstraints_67.gridy = 27;
		add(label_28, gridBagConstraints_67);
		label_28.setText(messenger.getMessage("GUI.KiGa.HealthInsurance"));

		krankenKasse = new JTextField();
		krankenKasse.setColumns(50);
		final GridBagConstraints gridBagConstraints_68 = new GridBagConstraints();
		gridBagConstraints_68.fill = GridBagConstraints.BOTH;
		gridBagConstraints_68.gridwidth = 5;
		gridBagConstraints_68.gridy = 28;
		gridBagConstraints_68.gridx = 7;
		add(krankenKasse, gridBagConstraints_68);
		krankenKasse.setText("");

		final JLabel label_29 = new JLabel();
		final GridBagConstraints gridBagConstraints_69 = new GridBagConstraints();
		gridBagConstraints_69.gridwidth = 3;
		gridBagConstraints_69.gridx = 1;
		gridBagConstraints_69.gridy = 27;
		add(label_29, gridBagConstraints_69);
		label_29.setText(messenger.getMessage("GUI.KiGa.DiffSufferedIllness"));

		wkrankheit = new JTextField();
		wkrankheit.setColumns(50);
		final GridBagConstraints gridBagConstraints_70 = new GridBagConstraints();
		gridBagConstraints_70.fill = GridBagConstraints.BOTH;
		gridBagConstraints_70.gridwidth = 3;
		gridBagConstraints_70.gridx = 1;
		gridBagConstraints_70.gridy = 28;
		add(wkrankheit, gridBagConstraints_70);
		wkrankheit.setText("");

		final JPanel panel_1 = new JPanel();
		panel_1.setLayout(new BoxLayout(panel_1, BoxLayout.X_AXIS));
		final GridBagConstraints gridBagConstraints_71 = new GridBagConstraints();
		gridBagConstraints_71.gridwidth = 11;
		gridBagConstraints_71.gridy = 26;
		gridBagConstraints_71.gridx = 1;
		add(panel_1, gridBagConstraints_71);

		final JLabel label_30 = new JLabel();
		final GridBagConstraints gridBagConstraints_72 = new GridBagConstraints();
		gridBagConstraints_72.gridwidth = 11;
		gridBagConstraints_72.gridy=25;
		gridBagConstraints_72.gridx = 1;
		add(label_30, gridBagConstraints_72);
		label_30.setText(messenger.getMessage("GUI.KiGa.SufferedIllness"));

		masern = new JCheckBox();
		panel_1.add(masern);
		masern.setText(messenger.getMessage("GUI.KiGa.Measles"));

		keuchhusten = new JCheckBox();
		panel_1.add(keuchhusten);
		keuchhusten.setText(messenger.getMessage("GUI.KiGa.WhoopingCough"));
		
		scharlach = new JCheckBox();
		panel_1.add(scharlach);
		scharlach.setText(messenger.getMessage("GUI.KiGa.ScarletFever"));		

		diphterie = new JCheckBox();
		panel_1.add(diphterie);
		diphterie.setText(messenger.getMessage("GUI.KiGa.Diphteria"));
				
		mumps = new JCheckBox();
		panel_1.add(mumps);
		mumps.setText(messenger.getMessage("GUI.KiGa.Mumps"));
				
		roeteln = new JCheckBox();
		panel_1.add(roeteln);
		roeteln.setText(messenger.getMessage("GUI.KiGa.Rubella"));
				
		windpocken = new JCheckBox();
		panel_1.add(windpocken);
		windpocken.setText(messenger.getMessage("GUI.KiGa.ChickenPox"));
				
		gelbsucht = new JCheckBox();
		panel_1.add(gelbsucht);
		gelbsucht.setText(messenger.getMessage("GUI.KiGa.Jaundice"));
				
		kinderlaehmung = new JCheckBox();
		panel_1.add(kinderlaehmung);
		kinderlaehmung.setText(messenger.getMessage("GUI.KiGa.Polio"));

		final JSeparator separator_8 = new JSeparator();
		separator_8.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_73 = new GridBagConstraints();
		gridBagConstraints_73.gridheight = 6;
		gridBagConstraints_73.fill = GridBagConstraints.BOTH;
		gridBagConstraints_73.gridy = 27;
		gridBagConstraints_73.gridx = 6;
		add(separator_8, gridBagConstraints_73);

		tetanusZeit = new JTextField();
		tetanusZeit.setColumns(50);
		final GridBagConstraints gridBagConstraints_76 = new GridBagConstraints();
		gridBagConstraints_76.fill = GridBagConstraints.BOTH;
		gridBagConstraints_76.gridy = 24;
		gridBagConstraints_76.gridx = 11;
		add(tetanusZeit, gridBagConstraints_76);

		final JLabel label_31 = new JLabel();
		final GridBagConstraints gridBagConstraints_74 = new GridBagConstraints();
		gridBagConstraints_74.gridwidth = 6;
		gridBagConstraints_74.gridx = 6;
		gridBagConstraints_74.gridy = 29;
		add(label_31, gridBagConstraints_74);
		label_31.setText(messenger.getMessage("GUI.KiGa.FamilyDoc"));

		final JLabel label_32 = new JLabel();
		final GridBagConstraints gridBagConstraints_75 = new GridBagConstraints();
		gridBagConstraints_75.gridx = 1;
		gridBagConstraints_75.gridy = 29;
		add(label_32, gridBagConstraints_75);
		label_32.setText(messenger.getMessage("GUI.KiGa.FamilyDocAdress"));

		final JLabel label_33 = new JLabel();
		label_33.setText(messenger.getMessage("GUI.KiGa.DateOfTetanus"));
		final GridBagConstraints gridBagConstraints_77 = new GridBagConstraints();
		gridBagConstraints_77.gridy = 23;
		gridBagConstraints_77.gridx = 11;
		add(label_33, gridBagConstraints_77);

		final JSeparator separator_9 = new JSeparator();
		separator_9.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_78 = new GridBagConstraints();
		gridBagConstraints_78.gridheight = 2;
		gridBagConstraints_78.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_78.gridy = 23;
		gridBagConstraints_78.gridx = 10;
		add(separator_9, gridBagConstraints_78);

		final JSeparator separator_10 = new JSeparator();
		separator_10.setOrientation(SwingConstants.VERTICAL);
		final GridBagConstraints gridBagConstraints_79 = new GridBagConstraints();
		gridBagConstraints_79.gridheight = 3;
		gridBagConstraints_79.fill = GridBagConstraints.VERTICAL;
		gridBagConstraints_79.gridy = 11;
		gridBagConstraints_79.gridx = 6;
		add(separator_10, gridBagConstraints_79);

		arztOrt = new JTextField();
		arztOrt.setColumns(50);
		final GridBagConstraints gridBagConstraints_80 = new GridBagConstraints();
		gridBagConstraints_80.gridwidth = 3;
		gridBagConstraints_80.fill = GridBagConstraints.BOTH;
		gridBagConstraints_80.gridy = 30;
		gridBagConstraints_80.gridx = 1;
		add(arztOrt, gridBagConstraints_80);

		arztTel = new JTextField();
		arztTel.setColumns(100);
		final GridBagConstraints gridBagConstraints_81 = new GridBagConstraints();
		gridBagConstraints_81.gridwidth = 5;
		gridBagConstraints_81.fill = GridBagConstraints.BOTH;
		gridBagConstraints_81.gridx = 7;
		gridBagConstraints_81.gridy = 30;
		add(arztTel, gridBagConstraints_81);

		final JLabel label_34 = new JLabel();
		final GridBagConstraints gridBagConstraints_82 = new GridBagConstraints();
		gridBagConstraints_82.gridx = 1;
		gridBagConstraints_82.gridy = 31;
		gridBagConstraints_82.gridwidth = 3;
		add(label_34, gridBagConstraints_82);
		label_34.setText(messenger.getMessage("GUI.KiGa.SpecHealthRem"));
		
		final JLabel label_35 = new JLabel();
		final GridBagConstraints gridBagConstraints_83 = new GridBagConstraints();
		gridBagConstraints_83.gridx = 7;
		gridBagConstraints_83.gridy = 31;
		gridBagConstraints_83.gridwidth = 5;
		add(label_35, gridBagConstraints_83);
		label_35.setText(messenger.getMessage("GUI.KiGa.MiscNotes"));


		final JScrollPane scrollPane = new JScrollPane();
		final GridBagConstraints gridBagConstraints_85 = new GridBagConstraints();
		gridBagConstraints_85.weighty = 1.0;
		gridBagConstraints_85.fill = GridBagConstraints.BOTH;
		gridBagConstraints_85.gridwidth = 5;
		gridBagConstraints_85.gridy = 32;
		gridBagConstraints_85.gridx = 7;
		add(scrollPane,gridBagConstraints_85 );
		
		sonstiges = new JTextArea();
		sonstiges.setLineWrap(true);
		scrollPane.setViewportView(sonstiges);
		
		final JScrollPane scrollPane_1 = new JScrollPane();
		final GridBagConstraints gridBagConstraints_84 = new GridBagConstraints();
		gridBagConstraints_84.fill = GridBagConstraints.BOTH;
		gridBagConstraints_84.gridwidth = 3;
		gridBagConstraints_84.gridy = 32;
		gridBagConstraints_84.gridx = 1;
		add(scrollPane_1, gridBagConstraints_84);
		
		gesundheit = new JTextArea();
		scrollPane_1.setViewportView(gesundheit);		

		final JSeparator separator_11 = new JSeparator();
		final GridBagConstraints gridBagConstraints_86 = new GridBagConstraints();
		gridBagConstraints_86.gridwidth = 12;
		gridBagConstraints_86.gridy = 33;
		gridBagConstraints_86.fill = GridBagConstraints.BOTH;
		add(separator_11, gridBagConstraints_86);


	}
	/* (non-Javadoc)
	 * @see Karteikarte#getPrimaryKey()
	 */
	public int getPrimaryKey() {
		return primaryKey;
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setPrimaryKey(int)
	 */
	public void setPrimaryKey(int i) {
		primaryKey=i;
	}
	
	/* (non-Javadoc)
	 * @see Karteikarte#getGruppe()
	 */
	public byte getGruppe() {
		return gruppemodel.getNumber().byteValue();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setGruppe(short)
	 */
	public void setGruppe(byte i) {
		gruppemodel.setValue(new Byte(i));
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getKindGeburtsDatum()
	 */
	public String getKindGeburtsDatum() {
		return kindGeburtText.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setKindGeburtsDatum(java.util.Date)
	 */
	public void setKindGeburtsDatum(String i) {
		kindGeburtText.setText(i);
	}
	
	/* (non-Javadoc)
	 * @see Karteikarte#getEintrittsDatum()
	 */
	public String getEintrittsDatum() {
		return eintritt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setEintrittsDatum(java.lang.String)
	 */
	public void setEintrittsDatum(String i) {
		eintritt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getEintrittsGrund()
	 */
	public String getEintrittsGrund() {
		return eintrittsGrund.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setEintrittsGrund(java.lang.String)
	 */
	public void setEintrittsGrund(String i) {
		eintrittsGrund.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getAustrittsDatum()
	 */
	public String getAustrittsDatum() {
		return austritt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setAustrittsDatum(java.lang.String)
	 */
	public void setAustrittsDatum(String i) {
		austritt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getAustrittsGrund()
	 */
	public String getAustrittsGrund() {
		return austrittsGrund.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setAustrittsGrund(java.lang.String)
	 */
	public void setAustrittsGrund(String i) {
		austrittsGrund.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getKindNachname()
	 */
	public String getKindNachname() {
		return nachnamekind.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setKindNachname(java.lang.String)
	 */
	public void setKindNachname(String i) {
		nachnamekind.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getKindVorname()
	 */
	public String getKindVorname() {
		return vornamekind.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setKindVorname(java.lang.String)
	 */
	public void setKindVorname(String i) {
		vornamekind.setText(i);		
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getGeburtsOrt()
	 */
	public String getGeburtsOrt() {
		return geburtsOrt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setGeburtsOrt(java.lang.String)
	 */
	public void setGeburtsOrt(String i) {
		geburtsOrt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getKindWohnung()
	 */
	public String getKindWohnung() {
		return kindWohnung.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setKindWohnung(java.lang.String)
	 */
	public void setKindWohnung(String i) {
		kindWohnung.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getReligion()
	 */
	public String getReligion() {
		return religion.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setReligion(java.lang.String)
	 */
	public void setReligion(String i) {
		religion.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getStaat()
	 */
	public String getStaat() {
		return staat.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setStaat(java.lang.String)
	 */
	public void setStaat(String i) {
		staat.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getKindTelefon()
	 */
	public String getKindTelefon() {
		return kindTelefon.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setKindTelefon(java.lang.String)
	 */
	public void setKindTelefon(String i) {
		kindTelefon.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getVaterName()
	 */
	public String getVaterName() {
		return vaterName.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setVaterName(java.lang.String)
	 */
	public void setVaterName(String i) {
		vaterName.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getVaterGeburt()
	 */
	public String getVaterGeburt() {
		return vaterGeburt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setVaterGeburt(java.lang.String)
	 */
	public void setVaterGeburt(String i) {
		vaterGeburt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getVaterBeruf()
	 */
	public String getVaterBeruf() {
		return vaterBeruf.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setVaterBeruf(java.lang.String)
	 */
	public void setVaterBeruf(String i) {
		vaterBeruf.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getMutterName()
	 */
	public String getMutterName() {
		return mutterName.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setMutterName(java.lang.String)
	 */
	public void setMutterName(String i) {
		mutterName.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getMutterGeburt()
	 */
	public String getMutterGeburt() {
		return mutterGeburt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setMutterGeburt(java.lang.String)
	 */
	public void setMutterGeburt(String i) {
		mutterGeburt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getMutterBeruf()
	 */
	public String getMutterBeruf() {
		return mutterBeruf.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setMutterBeruf(java.lang.String)
	 */
	public void setMutterBeruf(String i) {
		mutterBeruf.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getSorgePerson()
	 */
	public String getSorgePerson() {
		return sorgePerson.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setSorgePerson(java.lang.String)
	 */
	public void setSorgePerson(String i) {
		sorgePerson.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getArbeitsOrt1()
	 */
	public String getArbeitsOrt1() {
		return arbeitsOrt1.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setArbeitsOrt1(java.lang.String)
	 */
	public void setArbeitsOrt1(String i) {
		arbeitsOrt1.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getArbeitsTelefon1()
	 */
	public String getArbeitsTelefon1() {
		return arbeitsTel1.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setArbeitsTelefon1(java.lang.String)
	 */
	public void setArbeitsTelefon1(String i) {
		arbeitsTel1.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getArbeitsOrt2()
	 */
	public String getArbeitsOrt2() {
		return arbeitsOrt2.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setArbeitsOrt2(java.lang.String)
	 */
	public void setArbeitsOrt2(String i) {
		arbeitsOrt2.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getArbeitsTelefon2()
	 */
	public String getArbeitsTelefon2() {
		return arbeitsTel2.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setArbeitsTelefon2(java.lang.String)
	 */
	public void setArbeitsTelefon2(String i) {
		arbeitsTel2.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getGeschwisterGeburt()
	 */
	public String getGeschwisterGeburt() {
		return geschwgeburt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setGeschwisterGeburt(java.lang.String)
	 */
	public void setGeschwisterGeburt(String i) {
		geschwgeburt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getFamilienStand()
	 */
	public byte getFamilienStand() {
		byte dummy=0;
		if (verheiratet.isSelected())
			dummy|=VERHEIRATET;
		if (ledig.isSelected())
			dummy|=LEDIG;
		if (geschieden.isSelected())
			dummy|=GESCHIEDEN;
		if (verwitwet.isSelected())
			dummy|=VERWITWET;
		if (getrennt.isSelected())
			dummy|=GETRENNT;
		return dummy;
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setFamilienStand(short)
	 */
	public void setFamilienStand(byte i) {
		verheiratet.setSelected((i&VERHEIRATET)!=0);
		ledig.setSelected((i&LEDIG)!=0);
		geschieden.setSelected((i&GESCHIEDEN)!=0);
		verwitwet.setSelected((i&VERWITWET)!=0);
		getrennt.setSelected((i&GETRENNT)!=0);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getElternOrt()
	 */
	public String getElternOrt() {
		return elternOrt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setElternOrt(java.lang.String)
	 */
	public void setElternOrt(String i) {
		elternOrt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getElternTelefon()
	 */
	public String getElternTelefon() {
		return elternTel.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setElternTelefon(java.lang.String)
	 */
	public void setElternTelefon(String i) {
		elternTel.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getAnzahlGeschwister()
	 */
	public byte getAnzahlGeschwister() {
		return anzahlgeschwmodel.getNumber().byteValue();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setAnzahlGeschwister(short)
	 */
	public void setAnzahlGeschwister(byte i) {
		anzahlgeschwmodel.setValue(new Byte(i));
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getImpfungen()
	 */
	public String getImpfungen() {
		return impfung.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setImpfungen(java.lang.String)
	 */
	public void setImpfungen(String i) {
		impfung.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getTetanusZeit()
	 */
	public String getTetanusZeit() {
		return tetanusZeit.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setTetanusZeit(java.lang.String)
	 */
	public void setTetanusZeit(String i) {
		tetanusZeit.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getKrankheiten()
	 */
	public short getKrankheiten() {
		short dummy=0;
		if (masern.isSelected())
			dummy|=MASERN;
		if (keuchhusten.isSelected())
			dummy|=KEUCHHUSTEN;
		if (scharlach.isSelected())
			dummy|=SCHARLACH;
		if (diphterie.isSelected())
			dummy|=DIPHTERIE;
		if (mumps.isSelected())
			dummy|=MUMPS;
		if (roeteln.isSelected())
			dummy|=ROETELN;
		if (windpocken.isSelected())
			dummy|=WINDPOCKEN;
		if (gelbsucht.isSelected())
			dummy|=GELBSUCHT;
		if (kinderlaehmung.isSelected())
			dummy|=KINDERLAEHMUNG;
		return dummy;
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setKrankheiten(short)
	 */
	public void setKrankheiten(short i) {
		masern.setSelected((i&MASERN)!=0);
		keuchhusten.setSelected((i&KEUCHHUSTEN)!=0);
		scharlach.setSelected((i&SCHARLACH)!=0);
		diphterie.setSelected((i&DIPHTERIE)!=0);
		mumps.setSelected((i&MUMPS)!=0);
		roeteln.setSelected((i&ROETELN)!=0);
		windpocken.setSelected((i&WINDPOCKEN)!=0);
		gelbsucht.setSelected((i&GELBSUCHT)!=0);
		kinderlaehmung.setSelected((i&KINDERLAEHMUNG)!=0);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getWeitereKrankheiten()
	 */
	public String getWeitereKrankheiten() {
		return wkrankheit.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setWeitereKrankheiten(java.lang.String)

	 */
	public void setWeitereKrankheiten(String i) {
		wkrankheit.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getGesundheitsHinweise()
	 */
	public String getGesundheitsHinweise() {
		return gesundheit.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setGesundheitshinweise(java.lang.String)
	 */
	public void setGesundheitshinweise(String i) {
		gesundheit.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getArztOrt()
	 */
	public String getArztOrt() {
		return arztOrt.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setArztOrt(java.lang.String)
	 */
	public void setArztOrt(String i) {
		arztOrt.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getArztTelefon()
	 */
	public String getArztTelefon() {
		return arztTel.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setArztTelefon(java.lang.String)
	 */
	public void setArztTelefon(String i) {
		arztTel.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getKrankenkasse()
	 */
	public String getKrankenkasse() {
		return krankenKasse.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setKrankenkasse(java.lang.String)
	 */
	public void setKrankenkasse(String i) {
		krankenKasse.setText(i);
	}
	/* (non-Javadoc)
	 * @see Karteikarte#getSonstiges()
	 */
	public String getSonstiges() {
		return sonstiges.getText();
	}
	/* (non-Javadoc)
	 * @see Karteikarte#setSonstiges(java.lang.String)
	 */
	public void setSonstiges(String i) {
		sonstiges.setText(i);
	}
	
	/**
	 * 
	 * L�scht die Daten aller Felder
	 */
	public void leeren() {
		// setPrimaryKey(0); brauchen wir nicht zu setzen
		setGruppe((byte)1);
		setKindGeburtsDatum("");
		setEintrittsDatum("");
		setEintrittsGrund("");
		setAustrittsDatum("");
		setAustrittsGrund("");
		setKindNachname("");
		setKindVorname("");
		setGeburtsOrt("");
		setKindWohnung("");
		setReligion("");
		setStaat("");
		setKindTelefon("");
		setVaterName("");
		setVaterGeburt("");
		setVaterBeruf("");
		setMutterName("");
		setMutterGeburt("");
		setMutterBeruf("");
		setSorgePerson("");
		setArbeitsOrt1("");
		setArbeitsTelefon1("");
		setArbeitsOrt2("");
		setArbeitsTelefon2("");
		setGeschwisterGeburt("");
		setFamilienStand((byte)0);
		setElternOrt("");
		setElternTelefon("");
		setAnzahlGeschwister((byte)0);
		setImpfungen("");
		setTetanusZeit("");
		setKrankheiten((short)0);
		setWeitereKrankheiten("");
		setGesundheitshinweise("");
		setArztOrt("");
		setArztTelefon("");
		setKrankenkasse("");
		setSonstiges("");
	}

}
