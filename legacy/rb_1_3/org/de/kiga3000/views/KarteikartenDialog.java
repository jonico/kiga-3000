package org.de.kiga3000.views;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.HeadlessException;
import java.sql.SQLException;

import javax.swing.JDialog;

import org.de.kiga3000.exception.KigaException;


/**
 * @author maestro
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public abstract class KarteikartenDialog extends JDialog {

	/**
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog() throws HeadlessException {
		super();
	}

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Dialog owner) throws HeadlessException {
		super(owner);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Dialog owner, boolean modal)
		throws HeadlessException {
		super(owner, modal);
	}

	/**
	 * @param owner
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Frame owner) throws HeadlessException {
		super(owner);
	}

	/**
	 * @param owner
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Frame owner, boolean modal)
		throws HeadlessException {
		super(owner, modal);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Dialog owner, String title)
		throws HeadlessException {
		super(owner, title);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Dialog owner, String title, boolean modal)
		throws HeadlessException {
		super(owner, title, modal);
	}

	/**
	 * @param owner
	 * @param title
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Frame owner, String title)
		throws HeadlessException {
		super(owner, title);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(Frame owner, String title, boolean modal)
		throws HeadlessException {
		super(owner, title, modal);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 * @throws java.awt.HeadlessException
	 */
	public KarteikartenDialog(
		Dialog owner,
		String title,
		boolean modal,
		GraphicsConfiguration gc)
		throws HeadlessException {
		super(owner, title, modal, gc);
	}

	/**
	 * @param owner
	 * @param title
	 * @param modal
	 * @param gc
	 */
	public KarteikartenDialog(
		Frame owner,
		String title,
		boolean modal,
		GraphicsConfiguration gc) {
		super(owner, title, modal, gc);
	}
	
	/**
	 * 
	 * @param primaryKey Identifier um Daten des Kindes zu laden
	 * @return true, falls Laden geklappt hat, sonst false
	 */
//	public abstract boolean ladeKind(int primaryKey) throws SQLException, KigaException;

}
