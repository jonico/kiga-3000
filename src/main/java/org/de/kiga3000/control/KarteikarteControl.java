/**
 * 
 */
package org.de.kiga3000.control;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.TreeMap;
import java.util.logging.Logger;

import jakarta.persistence.PersistenceException;
import javax.swing.JOptionPane;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.database.GruppenVerschieberSQLImpl;
import org.de.kiga3000.database.KarteikarteRepository;
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
//	 database group change
	private GruppenVerschieberSQLImpl gruppeVerschieb = new GruppenVerschieberSQLImpl();
//	 database flag as not relevant
//	 database insert data
//	 database get data
// database connection
	private OpenConnection connect = new OpenConnection();

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
	 *
	 * <p>These four went through hand-written DAOs whose INSERT and UPDATE each bound
	 * 39 positional parameters; they now go through {@link KarteikarteRepository}, i.e.
	 * JPA. What that changes here:
	 *
	 * <ul>
	 *   <li>No connection plumbing. The repository owns its EntityManager and its
	 *       transaction, so the open/release dance - and the bug where a release only
	 *       happened on some exception paths - is gone.</li>
	 *   <li>Constraint violations are reported separately from database failures. An
	 *       over-long field used to travel all the way to MySQL and come back as
	 *       "Data truncation: Data too long for column 'sorgeperson'"; Bean Validation
	 *       now stops it first and this reports which field and why.</li>
	 *   <li>PoolException and SQLException are no longer thrown on these paths, so the
	 *       handlers for them went with the DAOs. GruppenVerschieber still uses JDBC and
	 *       keeps its own.</li>
	 * </ul>
	 */
	private boolean ladeKartei(KarteikarteImpl karteikarte, int karteiId) {
		try {
			KarteikarteRepository.fill(karteikarte, karteiId);
			return true;
		} catch (PersistenceException pe) {
			reportDatabaseProblem("KiGa.InsertNewChild", pe);
			return false;
		}
	}

	/**
	 * INSERT karteikarte
	 */
	private boolean insertKartei(KarteikarteImpl karteikarte) {
		try {
			KarteikarteRepository.insert(karteikarte);
			return true;
		} catch (ConstraintViolationException cve) {
			reportInvalidData(cve);
			return false;
		} catch (PersistenceException pe) {
			reportDatabaseProblem("KiGa.InsertNewChild", pe);
			return false;
		}
	}

	/**
	 * UPDATE karteikarte
	 */
	private boolean updateKartei(KarteikarteImpl karteikarte) {
		try {
			KarteikarteRepository.update(karteikarte);
			return true;
		} catch (ConstraintViolationException cve) {
			reportInvalidData(cve);
			return false;
		} catch (PersistenceException pe) {
			reportDatabaseProblem("KiGa.log.DatabaseConn", pe);
			return false;
		}
	}

	/**
	 * DELETE karteikarte
	 *
	 * <p>Group 9 is the retention group: cards are moved there rather than deleted
	 * outright, and only cards already in it may be removed. The repository keeps that
	 * guard, so a card moved elsewhere in the meantime is not deleted.
	 */
	private boolean deleteKartei(KarteikarteImpl karteikarte) {
		try {
			byte delGrup = 9;
			if (!KarteikarteRepository.delete(karteikarte.getPrimaryKey(), delGrup)) {
				_logger.warning(sysMessenger.getMessage("KiGa.LogoffErr",
						"card " + karteikarte.getPrimaryKey()
								+ " is not in group " + delGrup));
				return false;
			}
			return true;
		} catch (PersistenceException pe) {
			reportDatabaseProblem("KiGa.LogoffErr", pe);
			return false;
		}
	}

	/** Reports a Bean Validation failure, naming each offending field. */
	private void reportInvalidData(ConstraintViolationException cve) {
		StringBuilder detail = new StringBuilder();
		for (ConstraintViolation<?> violation : cve.getConstraintViolations()) {
			if (detail.length() > 0) {
				detail.append('\n');
			}
			detail.append(violation.getPropertyPath()).append(": ")
					.append(violation.getMessage());
		}
		_logger.warning(sysMessenger.getMessage("KiGa.WrongData", detail.toString()));
		JOptionPane.showMessageDialog(null,
				sysMessenger.getMessage("KiGa.WrongData", detail.toString()),
				messenger.getMessage("KiGa.title"), JOptionPane.WARNING_MESSAGE);
	}

	/** Reports a persistence failure with the message key the old handler used. */
	private void reportDatabaseProblem(String messageKey, RuntimeException e) {
		String cause = e.getMessage() == null ? e.toString() : e.getMessage();
		_logger.severe(sysMessenger.getMessage(messageKey, cause));
		JOptionPane.showMessageDialog(null, sysMessenger.getMessage(messageKey, cause),
				messenger.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
	}
}
