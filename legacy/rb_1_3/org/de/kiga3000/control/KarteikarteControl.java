/**
 * 
 */
package org.de.kiga3000.control;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.database.GruppenVerschieberSQLImpl;
import org.de.kiga3000.database.KarteiKartenDatenEntfernerSQLImpl;
import org.de.kiga3000.database.KarteikartenDatenAuffrischerSQLImpl;
import org.de.kiga3000.database.KarteikartenDatenErzeugerSQLImpl;
import org.de.kiga3000.database.KarteikartenDatenHolerSQLImpl;
import org.de.kiga3000.database.OpenConnection;
import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.messages.Messenger;
import org.de.kiga3000.messages.SystemMessenger;

/**
 * @author bobohead
 * this object controls all action reffering to data
 * printing and saving
 */
public class KarteikarteControl {

	//  message object
	private static Messenger messenger = new Messenger();
    // system messages like database connection and stuff
	private SystemMessenger sysMessenger = new SystemMessenger();
    // logging for later support
	private static Logger _logger = Logger.getLogger(KarteikarteControl.class
			.getName());
    // data container
	private KarteikarteImpl karteikarte;
    // database update data
	private KarteikartenDatenAuffrischerSQLImpl updateKartei = new KarteikartenDatenAuffrischerSQLImpl();
//	 database group change
	private GruppenVerschieberSQLImpl gruppeVerschieb = new GruppenVerschieberSQLImpl();
//	 database flag as not relevant
	private KarteiKartenDatenEntfernerSQLImpl deleteKartei = new KarteiKartenDatenEntfernerSQLImpl();
//	 database insert data
	private KarteikartenDatenErzeugerSQLImpl insertKartei = new KarteikartenDatenErzeugerSQLImpl();
//	 database get data
	private KarteikartenDatenHolerSQLImpl leseKartei = new KarteikartenDatenHolerSQLImpl();
// database connection
	private OpenConnection connect = new OpenConnection();
// hold connection object
	private Connection conn;

	/**
	 * loads data into data container
	 * @param karteikarte {@link org.de.kiga3000.data.KarteikarteImpl} data container
	 * @param karteiId primary key 
	 */
	public void ladeKarteiEinzel(KarteikarteImpl karteikarte, int karteiId) {
		boolean ok = true;
		ok = this.ladeKartei(karteikarte, karteiId);
		if (ok != true) {
			_logger.warning(messenger.getMessage("Kiga.DataChild"));
			JOptionPane.showMessageDialog(null, messenger
					.getMessage("Kiga.DataChild"), messenger
					.getMessage("KiGa.title"), JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * insert a new child
	 * 
	 * @param karteikarte
	 *            {@link org.de.kiga3000.data.KarteikarteImpl KarteikarteImpl}
	 */
	public void insertKarteiEinzeln(KarteikarteImpl karteikarte) {
		boolean ok = true;
		// / TODO hier noch die Checks rein
		ok = this.insertKartei(karteikarte);
		if (ok) {
			_logger.warning(messenger.getMessage("KiGa.ChildAdded"));
			JOptionPane.showMessageDialog(null, messenger
					.getMessage("KiGa.ChildAdded"), messenger
					.getMessage("KiGa.title"), JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * modifys child
	 * 
	 * @param karteikarte
	 *            {@link org.de.kiga3000.data.KarteikarteImpl KarteikarteImpl}
	 */
	public void updateKarteiEinzeln(KarteikarteImpl karteikarte) {
		// TODO hier noch die checks rein und Formatierungen rein
		boolean ok = this.updateKartei(karteikarte);
		if (ok) {
			_logger.finest(messenger.getMessage("KiGa.DataModif"));
			JOptionPane.showMessageDialog(null, messenger
					.getMessage("KiGa.DataModif"), messenger
					.getMessage("KiGa.title"), JOptionPane.INFORMATION_MESSAGE);
		}
	}

	/**
	 * deletes child by setting group number 9
	 * 
	 * @param karteikarte
	 *            {@link org.de.kiga3000.data.KarteikarteImpl KarteikarteImpl}
	 */
	public void deleteKarteEinzeln(KarteikarteImpl karteikarte) {
		boolean ok = this.deleteKartei(karteikarte);
		if (ok) {
			_logger.finest(messenger.getMessage("KiGa.ChildRemove"));
			JOptionPane.showMessageDialog(null, messenger
					.getMessage("KiGa.ChildRemove"), messenger
					.getMessage("KiGa.title"), JOptionPane.INFORMATION_MESSAGE);
		}

	}

	public void verschiebeGruppe(byte i, byte j, byte k) {
		try {
			Connection conn = connect.openConnection();
			gruppeVerschieb.swapGroups(i, j, k, conn);
			connect.releaseConnection(conn);
		} catch (PoolException pe) {
			// e.printStackTrace();
			_logger.severe(sysMessenger.getMessage("KiGa.log.DatabaseConn", pe
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.DatabaseConn", pe.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
		} catch (SQLException e1) {
			_logger.severe(sysMessenger.getMessage("KiGa.ChangeGroupDBErr", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.ChangeGroupDBErr", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			return;
		} catch (KigaException e1) {
			_logger.severe(sysMessenger.getMessage("KiGa.log.LogicalError", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.LogicalError", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.WARNING_MESSAGE);
			return;
		}

	}

	/**
	 * Sub routienen
	 */
	private boolean ladeKartei(KarteikarteImpl karteikarte, int karteiId) {
		boolean ok = true;
		try {
			conn = connect.openConnection();
			try {
				leseKartei.fill(karteikarte, conn, karteiId);
				connect.releaseConnection(conn);
			} catch (SQLException e1) {
				_logger.severe(sysMessenger.getMessage("KiGa.InsertNewChild",
						e1.getMessage()));
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.InsertNewChild", e1.getMessage()), messenger
						.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
				ok = false;
			} catch (KigaException e1) {
				_logger.warning(sysMessenger.getMessage("KiGa.WrongData", e1
						.getMessage()));
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.WrongData", e1.getMessage()), messenger
						.getMessage("KiGa.title"), JOptionPane.WARNING_MESSAGE);
				ok = false;
			}
		} catch (SQLException e1) {
			_logger.severe(sysMessenger.getMessage("KiGa.InsertNewChild", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.InsertNewChild", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;
			connect.releaseConnection(conn);
		} catch (PoolException pe) {
			// e.printStackTrace();
			_logger.severe(sysMessenger.getMessage("KiGa.log.DatabaseConn", pe
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.DatabaseConn", pe.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;
			connect.releaseConnection(conn);
		}

		return ok;
	}

	/**
	 * INSERT karteikarte
	 */

	private boolean insertKartei(KarteikarteImpl karteikarte) {
		boolean ok = true;
		try {
			Connection conn = connect.openConnection();
			insertKartei.insertKarteikarte(karteikarte, conn);
			connect.releaseConnection(conn);
		} catch (PoolException pe) {
			// e.printStackTrace();
			_logger.severe(sysMessenger.getMessage("KiGa.log.DatabaseConn", pe
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.DatabaseConn", pe.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;
		} catch (SQLException e1) {
			_logger.severe(sysMessenger.getMessage("KiGa.InsertNewChild", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.InsertNewChild", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;
		} catch (KigaException e1) {
			_logger.warning(sysMessenger.getMessage("KiGa.WrongData", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.WrongData", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.WARNING_MESSAGE);
			ok = false;
			connect.releaseConnection(conn);
		}
		return ok;
	}

	/**
	 * UPDATE karteikarte
	 */

	private boolean updateKartei(KarteikarteImpl karteikarte) {
		boolean ok = true;
		try {
			Connection conn = connect.openConnection();
			updateKartei.updateKarteikarte(karteikarte, conn);
			connect.releaseConnection(conn);
		} catch (PoolException pe) {
			// e.printStackTrace();
			_logger.severe(sysMessenger.getMessage("KiGa.log.DatabaseConn", pe
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.DatabaseConn", pe.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;
		} catch (SQLException sqe) {
			// e.printStackTrace();
			_logger.severe(sysMessenger.getMessage("KiGa.log.DatabaseConn", sqe
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.DatabaseConn", sqe.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;
		} catch (KigaException ke) {
			_logger.warning(sysMessenger.getMessage("KiGa.WrongData", ke
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.WrongData", ke.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.WARNING_MESSAGE);
			ok = false;
		}
		return ok;
	}

	/**
	 * DELETE karteikarte
	 */

	private boolean deleteKartei(KarteikarteImpl karteikarte) {
		boolean ok = true;
		try {
			Connection conn = connect.openConnection();
			byte delGrup = 9;
			deleteKartei.deleteKarteikarte(karteikarte.getPrimaryKey(),
					delGrup, conn);
			connect.releaseConnection(conn);
		} catch (PoolException pe) {
			// e.printStackTrace();
			_logger.severe(sysMessenger.getMessage("KiGa.log.DatabaseConn", pe
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.DatabaseConn", pe.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;

		} catch (SQLException e1) {
			_logger.severe(sysMessenger.getMessage("KiGa.LogoffErr", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.LogoffErr", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			ok = false;
		} catch (KigaException e1) {
			_logger.severe(sysMessenger.getMessage("KiGa.log.LogicalError", e1
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.LogicalError", e1.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.WARNING_MESSAGE);
			ok = false;
		}
		return ok;
	}
}
