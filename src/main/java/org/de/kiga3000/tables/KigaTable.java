package org.de.kiga3000.tables;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

public class KigaTable extends JTable{
	
	private TableColumn id;
	private TableColumn vorname;
	private TableColumn nachname;
	private TableColumn geburtsdatum;
	private TableColumn anschrift;
	
	public KigaTable(DefaultTableModel model) {
		//setRowHeight(20);
		// setPreferredScrollableViewportSize(new Dimension(200,100));
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(false);
		// setCellSelectionEnabled(false);
		setAutoCreateColumnsFromModel(true);
		setModel(model);
		setShowHorizontalLines(true);
		setShowVerticalLines(false);
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		getTableHeader().setReorderingAllowed(true);
		id=getColumnModel().getColumn(0);
		vorname=getColumnModel().getColumn(1);
		nachname=getColumnModel().getColumn(2);
		geburtsdatum=getColumnModel().getColumn(3);
		anschrift=getColumnModel().getColumn(4);
		removeColumn(id);
		// getColumnModel().getColumn(0).setMaxWidth(50);
		getColumnModel().getColumn(3).setMinWidth(300);
	}
	
	public void zeigeVorname(boolean i) {
		if (i)
			addColumn(vorname);
		else
			removeColumn(vorname);
	}
	
	public void zeigeNachname(boolean i) {
			if (i)
				addColumn(nachname);
			else
				removeColumn(nachname);
	}
	
	public void zeigeGeburtstag(boolean i) {
					if (i)
						addColumn(geburtsdatum);
					else
						removeColumn(geburtsdatum);
	}
	
	public void zeigeAnschrift(boolean i) {
					if (i)
						addColumn(anschrift);
					else
						removeColumn(anschrift);
	}

	public boolean isCellEditable(int x,int y) {
		return false;
	}

}


