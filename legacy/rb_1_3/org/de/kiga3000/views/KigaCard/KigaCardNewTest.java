package org.de.kiga3000.views.KigaCard;

import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.de.kiga3000.control.KigaMainViewControl;
import org.de.kiga3000.database.Ressourcen;
import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.views.KigaCard.KigaCardNew;

import com.birosoft.liquid.LiquidLookAndFeel;

import junit.framework.TestCase;

public class KigaCardNewTest extends TestCase {

	/*
	 * Test method for 'org.de.kiga3000.views.KigaCard.KigaCardNew.KigaCardNew()'
	 */
	public void testKigaCardNew() {

		try {
			// UI Manager set liquid look and feel
			UIManager
					.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
			LiquidLookAndFeel.setPanelTransparency(false);
			LiquidLookAndFeel.setLiquidDecorations(true);
		} catch (Exception e) {
			// not liquid no programm
			System.exit(1);
		}
        // define frame  
			JFrame hauptFenster = new JFrame("KiGa.title");
			KigaCardNew main = new KigaCardNew();
			hauptFenster.getContentPane().add(main);
        // define usual exit and stuff
		hauptFenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		hauptFenster.setBounds(0, 0, 1024, 768);
		hauptFenster.show();
		hauptFenster.setExtendedState(JFrame.MAXIMIZED_BOTH);
		

	}

}
