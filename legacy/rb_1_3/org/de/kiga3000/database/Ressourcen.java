package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Timer;

import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.Datenschutz;
import org.de.kiga3000.interfaces.Gruppenverschieber;
import org.de.kiga3000.interfaces.KarteikartenDatenAuffrischer;
import org.de.kiga3000.interfaces.KarteikartenDatenEntferner;
import org.de.kiga3000.interfaces.KarteikartenDatenErzeuger;
import org.de.kiga3000.interfaces.KarteikartenDatenholer;
import org.de.kiga3000.interfaces.SuchDatumsholer;
import org.de.kiga3000.interfaces.SuchGruppenholer;
import org.de.kiga3000.interfaces.SuchNamenholer;
import org.de.kiga3000.views.KarteikartenDialog;
import org.de.kiga3000.views.KarteikartenDialogSwingImpl;

/*
 * Created on 25.08.2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */

/**
 * @author maestro
 * Diese Klasse stellt ein paar statische Methoden zur Verfügung, um Ressourcen zu verteilen
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public final class Ressourcen {
	
	private static final ConnectionPool pool=new ConnectionPool("jdbc:mysql://localhost/Kindergarten?prepStmtCacheSqlLimit=2048&cachePrepStmts=true&autoReconnect=true","KiGa","Kiga3000",20,"com.mysql.jdbc.Driver");
	
	static public Gruppenverschieber getGruppenVerschieber() throws PoolException, SQLException {
		return new GruppenVerschieberSQLImpl();
	}
	
//	static public KarteikartenDatenholer getKarteikartenDatenholer() throws PoolException, SQLException {
//		return new KarteikartenDatenHolerSQLImpl();
//	}
	
//	static public KarteikartenDialog getKarteikartenDialog() throws PoolException, SQLException {
//		return new KarteikartenDialogSwingImpl();
//	}
	
//	static public KarteikartenDatenAuffrischer getKarteikartenDatenAuffrischer() throws PoolException, SQLException {
// 		return new KarteikartenDatenAuffrischerSQLImpl();
//	}
//	static public KarteikartenDatenErzeuger getKarteikartenDatenErzeuger() throws PoolException, SQLException {
//			return new KarteikartenDatenErzeugerSQLImpl();
//		}
		
//		static public KarteikartenDatenEntferner getKarteikartenDatenEntferner() throws PoolException, SQLException {
//			return new KarteiKartenDatenEntfernerSQLImpl();
//		}
		
		static public SuchDatumsholer getSuchDatumsHoler() throws PoolException, SQLException {
			return new SuchDatumsholerSQLImpl();
		}
		
		static public SuchGruppenholer getSuchGruppenHoler() throws PoolException, SQLException {
				return new SuchGruppenholerSQLImpl();
			}
			
		static public SuchNamenholer getSuchNamenHoler() throws PoolException, SQLException {
				return new SuchNamenholerSQLImpl();
			}
			
			
		static public Datenschutz getDatenschutz() {
			return new DatenschutzSQLImpl();
		}
		
		static public Connection getConnection(long i) throws PoolException {
			return pool.acquireConnection(i);	
		}
		
		/**
		 * Wichtig: Durch das Auto Connect müssen die Connections nicht unbedingt zurückgegeben werden
		 * @param i zurückzugebende Verbindung
		 */
		static public void releaseConnection(Connection i) {
			pool.releaseConnection(i);
		}
		
		/**
		 * Löscht connections, die im Pool sind
		 * Wichtig: Durch autoReconnect müssen die Komponenten ihre Verbindungen nicht unbedingt zurückgeben
		 * @param timeout Wann sollen connections zurückgegeben werden
		 */
		static public void startConnectionKiller(int timeout) {
			Timer timer=new Timer();
			timer.schedule(new ConnectionKiller(pool),timeout*1000,timeout*1000);
			timer.schedule(new ConnectionKiller(pool),timeout*1000,timeout*1000);

		}
			
}
