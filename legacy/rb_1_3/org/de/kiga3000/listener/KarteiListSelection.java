/**
 * kiga3000 org.de.kiga3000.listener KarteiListSelection.java
 * 05.12.2005
 * All Rights reserved by Bernhard Diemer
 * Programmed by : bobohead
 * Copyright by : B4IT
 *                Am Hochrainacker 55
 *                85435 Erding
 *                Germany
 */
package org.de.kiga3000.listener;

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.de.kiga3000.views.Kiga3000MainPanel;

/**
 * @author bobohead2
 *
 */
public class KarteiListSelection implements ListSelectionListener {

	private JTable table;
    private Kiga3000MainPanel mainPanel;
    
	public KarteiListSelection(JTable table,Kiga3000MainPanel mainPanel){
		this.table = table;	
		this.mainPanel = mainPanel;
	}
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	public void valueChanged(ListSelectionEvent e) {

		int i=table.getSelectedRow();
		if (i==-1) {
			mainPanel.ergebnisAbmeldeKnopf.setEnabled(false);
			mainPanel.ergebnisAnzeigeKnopf.setEnabled(false);
		}
		else {
			mainPanel.ergebnisAbmeldeKnopf.setEnabled(true);
			mainPanel.ergebnisAnzeigeKnopf.setEnabled(true);
		}

	}

}
