/*
 * Created on 14.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.de.kiga3000.control;

import java.io.InputStream;
import java.nio.file.Path;
import java.sql.SQLException;
import java.util.logging.LogManager;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.UIManager;

import org.de.kiga3000.database.Ressourcen;
import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.messages.Messenger;
import org.de.kiga3000.messages.SystemMessenger;
import org.de.kiga3000.views.Configuration;
import org.de.kiga3000.views.Kiga3000MainPanel;
import org.de.kiga3000.views.KigaAppIcon;
import org.de.kiga3000.views.KigaForeground;

import com.birosoft.liquid.LiquidLookAndFeel;

/**
 * @author bobo_local
 * 
 * class setup view, config logger etc. pp
 */
public class KigaMainControl {
    // logging manager
	private static LogManager manag = LogManager.getLogManager();
    // logger object
	private static Logger _logger = Logger.getLogger(KigaMainControl.class
			.getName());
    // messenger
	private static Messenger messenger = new Messenger();
    // messenger for system messages
	private static SystemMessenger sysMessenger = new SystemMessenger();
    // configuration data
	private static Configuration config = new Configuration();
    // control for view
    private KigaMainViewControl viewcont;
	// view
    private Kiga3000MainPanel mainPanel;

    /*
     * starts the main application  view
     */
    public void startApplication() {
    	// path for logging file
		String pfadLog = "";
		try {
			/*
			 * Was: new FileInputStream(config.getConfig("KiGaLoggingPath")
			 *                          + "/org/de/kiga3000/properties/KiGaLogging.properties")
			 *
			 * KiGaLoggingPath ships empty, so this looked for the file at the
			 * filesystem root and the logging configuration was never applied. The file
			 * is packaged in the jar. KigaResources honours the configured directory
			 * when one is set and otherwise reads from the classpath.
			 *
			 * The log directory is created first: java.util.logging.FileHandler does not
			 * create parent directories and fails silently at configuration time if the
			 * path does not exist.
			 */
			try {
				java.nio.file.Files.createDirectories(
						Path.of(System.getProperty("user.home"),
								".kiga3000", "logs"));
			} catch (java.io.IOException dirExc) {
				_logger.warning("could not create the log directory: " + dirExc.getMessage());
			}
			try (InputStream inp =
					org.de.kiga3000.KigaResources.open(
							org.de.kiga3000.KigaResources.LOGGING_CONFIG)) {
				if (inp == null) {
					throw new java.io.FileNotFoundException(
							org.de.kiga3000.KigaResources.LOGGING_CONFIG);
				}
				// read configuration
				manag.readConfiguration(inp);
			} catch (java.io.IOException exc) {
				// log it if not found
				_logger
						.severe(sysMessenger.getMessage(
								"KiGa.log.ErrorNoFileFound", exc.getMessage(),
								pfadLog));
			}
            // starts Ressource manager
			Ressourcen.startConnectionKiller(10000);

			try {
				// UI Manager set liquid look and feel
				UIManager
						.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
				LiquidLookAndFeel.setPanelTransparency(false);
				LiquidLookAndFeel.setLiquidDecorations(true);
			} catch (Exception e) {
				// log it if something didin't work 
				_logger.severe(sysMessenger.getMessage(
						"KiGa.log.InitializeInterface", e.getMessage()));
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.log.InitializeInterface", e.getMessage()),
						messenger.getMessage("KiGa.title"),
						JOptionPane.ERROR_MESSAGE);
				// not liquid no programm
				System.exit(1);
			}
            // define frame  
   			JFrame hauptFenster = new JFrame(messenger.getMessage("KiGa.title"));
			try {
				// deletes all data's moved to group 9 and older than 5 years
				Ressourcen.getDatenschutz().entferneAlteDaten((byte) 9);
				// instantiates control for view
				viewcont = new KigaMainViewControl();
				// creates main panel
				mainPanel = new Kiga3000MainPanel(viewcont);
				// tells view about controll
				viewcont.setView(mainPanel);
				// set mainpanel
				hauptFenster.getContentPane().add(mainPanel);
			} catch (SQLException e) {
				// log failures
				_logger.severe(sysMessenger.getMessage(
						"KiGa.log.QuestDatabase", e.getMessage()));
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.log.QuestDatabase", e.getMessage()), messenger
						.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
				// no frame no programm
				System.exit(1);
			} catch (PoolException e) {
				// log failures
				_logger.severe(sysMessenger.getMessage("KiGa.log.DatabaseConn",
						e.getMessage()));
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.log.DatabaseConn", e.getMessage()), messenger
						.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
				// no database no programm
				System.exit(1);
			} catch (KigaException e) {
				// log failures
				_logger.severe(sysMessenger.getMessage("KiGa.log.LogicalError",
						e.getMessage()));
				JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
						"KiGa.log.LogicalError", e.getMessage()), messenger
						.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
				// failure here !? no programm ;-)
				System.exit(1);
			}
            // application icon instead of the generic Java coffee cup. Set before the
			// window becomes visible so it is never shown with the default icon.
			KigaAppIcon.install(hauptFenster);
            // define usual exit and stuff
			hauptFenster.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			hauptFenster.setBounds(0, 0, 1024, 768);
			hauptFenster.setVisible(true);
			hauptFenster.setExtendedState(JFrame.MAXIMIZED_BOTH);
			// Without this the window opens behind whatever was already frontmost -
			// usually the terminal it was started from. setVisible(true) maps the
			// window but does not activate the application on macOS.
			KigaForeground.raise(hauptFenster);

			/*
			 * Close the persistence factory, and with it the HikariCP pool, when the
			 * JVM exits. EXIT_ON_CLOSE terminates without unwinding, so without this
			 * the pool's threads are simply killed and Hikari logs no orderly
			 * shutdown. Registered after the window is up so a startup failure cannot
			 * leave a hook behind.
			 */
			Runtime.getRuntime().addShutdownHook(new Thread(
					org.de.kiga3000.database.KarteikarteRepository::shutdown,
					"kiga3000-persistence-shutdown"));
			
		} catch (Exception e) {
            // log failure
			_logger.severe(sysMessenger.getMessage("KiGa.log.Runtime", e
					.getMessage()));
			JOptionPane.showMessageDialog(null, sysMessenger.getMessage(
					"KiGa.log.Runtime", e.getMessage()), messenger
					.getMessage("KiGa.title"), JOptionPane.ERROR_MESSAGE);
			// if the view is not displayable what else should we do ? 
			System.exit(1);
		}
	}

}
