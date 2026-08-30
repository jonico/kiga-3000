package org.de.kiga3000.views.KigaCard;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.de.kiga3000.data.child.Child;
import org.de.kiga3000.interfaces.newcard.IFKigaCard;
import org.de.kiga3000.messages.Messenger;

public class KigaCardNew extends JPanel implements IFKigaCard {

	private static Messenger messenger = new Messenger();

	private JTextField CardId;
	private JTextField entryDate;
	private JTextField leavingDate;
	private JTextField group;
	private JTextField sonstiges;
	
	public KigaCardNew() {
		/* Transfer needed control objects */

		/*--------------------------------------------------------
		 *   Construct view
		 */
		super();
//		setMinimumSize(new Dimension(950, 650));
		setLayout(new GridBagLayout());
        //  Primary Key
		final JLabel CardIdText = new JLabel();
		final GridBagConstraints gridBagCardIdText = new GridBagConstraints();
		gridBagCardIdText.weightx = 1.0;
		gridBagCardIdText.gridx = 1;
		gridBagCardIdText.gridy = 1;
		add(CardIdText, gridBagCardIdText);
		CardIdText.setText(messenger.getMessage("GUI.KiGa.ID"));

		CardId = new JTextField();
		CardId.setColumns(4);
		final GridBagConstraints gridBagCardId = new GridBagConstraints();
		gridBagCardId.weightx = 1.0;
		gridBagCardId.gridx = 2;
		gridBagCardId.gridy = 1;
		add(CardId, gridBagCardId);
        // Goup 
		final JLabel GroupText = new JLabel();
		final GridBagConstraints gridBagGrtoupText = new GridBagConstraints();
		gridBagGrtoupText.weightx = 1.0;
		gridBagGrtoupText.gridx = 3;
		gridBagGrtoupText.gridy = 1;
		add(GroupText, gridBagGrtoupText);
		CardIdText.setText(messenger.getMessage("GUI.KiGa.Group"));

		group = new JTextField();
		group.setColumns(2);
		final GridBagConstraints gridBagGroup = new GridBagConstraints();
		gridBagGroup.weightx = 1.0;
		gridBagGroup.gridx = 4;
		gridBagGroup.gridy = 1;
		add(group, gridBagGroup);

		
		final JLabel EntryDate = new JLabel();
		final GridBagConstraints gridBagCardEntryDateText = new GridBagConstraints();
		gridBagCardEntryDateText.weightx = 1.0;
		gridBagCardEntryDateText.gridx = 3;
		gridBagCardEntryDateText.gridy = 2;
		add(EntryDate, gridBagCardEntryDateText);
		EntryDate.setText(messenger.getMessage("GUI.KiGa.EntryDate"));

		entryDate = new JTextField();
		entryDate.setColumns(10);
		final GridBagConstraints gridBagEntryDate = new GridBagConstraints();
		gridBagEntryDate.weightx = 1.0;
		gridBagEntryDate.gridx = 4;
		gridBagEntryDate.gridy = 2;
		add(entryDate, gridBagEntryDate);

		final JLabel LeaveDateText = new JLabel();
		final GridBagConstraints gridBagCardLeaveDateText = new GridBagConstraints();
		gridBagCardLeaveDateText.weightx = 1.0;
		gridBagCardLeaveDateText.gridx = 3;
		gridBagCardLeaveDateText.gridy = 2;
		add(LeaveDateText, gridBagCardLeaveDateText);
		LeaveDateText.setText(messenger.getMessage("GUI.KiGa.LeaveDate"));

		leavingDate = new JTextField();
		leavingDate.setColumns(10);
		final GridBagConstraints gridBagLeaveDate = new GridBagConstraints();
		gridBagLeaveDate.weightx = 1.0;
		gridBagLeaveDate.gridx = 4;
		gridBagLeaveDate.gridy = 2;
		add(leavingDate, gridBagLeaveDate);

		final JLabel SonstigesText = new JLabel();
		final GridBagConstraints gridBagSonstText = new GridBagConstraints();
		gridBagSonstText.weightx = 1.0;
		gridBagSonstText.gridx = 3;
		gridBagSonstText.gridy = 3;
		add(SonstigesText, gridBagSonstText);
		LeaveDateText.setText(messenger.getMessage("GUI.KiGa.MiscNotes"));

		sonstiges = new JTextField();
	    sonstiges.setColumns(255);
		final GridBagConstraints gridBagSonstiges = new GridBagConstraints();
		gridBagSonstiges.weightx = 1.0;
		gridBagSonstiges.gridx = 4;
		gridBagSonstiges.gridy = 2;
		add(sonstiges, gridBagSonstiges);
		
	}

	/*---------------------------------------------------------
	 *  Action control part
	 */

	/*---------------------------------------------------------
	 *  Communication Part
	 * 
	 */
	public String getAdditionalInfo() {
			return sonstiges.getText();
	}

	public int getCardID() {
		return Integer.parseInt(CardId.getText());
	}

	public Child getChild() {
		return null;
	}

	public String getEntryDate() {
		return entryDate.getText();
	}

	public int getGroup() {
		return Integer.parseInt(group.getText());
	}

	public String getLeavingDate() {
		return leavingDate.getText();
	}

	public void setAdditionalInfo(String additionalInfo) {
        sonstiges.setText(additionalInfo);
	}

	public void setCardID(int cardID) {
         CardId.setText(Integer.toString(cardID));
	}

	public void setChild(Child child) {
        
	}

	public void setEntryDate(String entryDate) {
		this.entryDate.setText(entryDate); 
	}

	public void setGroup(int group) {
       this.group.setText(Integer.toString(group));
	}

	public void setLeavingDate(String leavingDate) {
       this.leavingDate.setText(leavingDate); 
	}

}
