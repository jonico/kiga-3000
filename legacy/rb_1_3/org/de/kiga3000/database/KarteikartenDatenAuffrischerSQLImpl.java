package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.interfaces.KarteikartenDatenAuffrischer;

/*
 * Created on 05.09.2004
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
public class KarteikartenDatenAuffrischerSQLImpl {
//	implements KarteikartenDatenAuffrischer {
		
//	private Connection conn;
	private PreparedStatement p;

//		public KarteikartenDatenAuffrischerSQLImpl() throws PoolException, SQLException {
//				conn=Ressourcen.getConnection(5000);
//				conn.setAutoCommit(true);
//				p=conn.prepareStatement("UPDATE `Karteikarte` SET `gruppe` = ?, `lastaccess` =  CURDATE(), `kindgeburt` = ?, `eintritt` = ?, `eintrittsgrund` = ?, `austritt` = ?, `austrittsgrund` = ?, `kindnachname` = ?, `kindvorname` = ?, `geburtsort` = ?, `kindwohnung` = ?, `religion` = ?, `staat` = ?, `kindtelefon` = ?, `vatername` = ?, `vatergeburt` = ?, `vaterberuf` = ?, `muttername` = ?, `muttergeburt` = ?, `mutterberuf` = ?, `sorgeperson` = ?, `arbeitsort1` = ?, `arbeittel1` = ?, `arbeitsort2` = ?, `arbeittel2` = ?, `geschwgeburt` = ?, `famstand` = ?, `elternort` = ?, `elterntel` = ?, `anzahlgeschw` = ?, `impfung` = ?, `tetanuszeit` = ?, `krankheiten` = ?, `wkrankheit` = ?, `gesundheit` = ?, `arztort` = ?, `arzttel` = ?, `krankenkasse` = ?, `sonstiges` = ? WHERE `id` = ?");
//			}

	/* (non-Javadoc)
	 * @see KarteikartenDatenAuffrischer#insertKarteikarte(Karteikarte)
	 */
	public void updateKarteikarte(Karteikarte i, Connection conn)
		throws SQLException, KigaException {
			
//			Date kindGeburt=SQLHelfer.pruefeDaten(i);
			p=conn.prepareStatement("UPDATE `Karteikarte` SET `gruppe` = ?, `lastaccess` =  CURDATE(), `kindgeburt` = ?, `eintritt` = ?, `eintrittsgrund` = ?, `austritt` = ?, `austrittsgrund` = ?, `kindnachname` = ?, `kindvorname` = ?, `geburtsort` = ?, `kindwohnung` = ?, `religion` = ?, `staat` = ?, `kindtelefon` = ?, `vatername` = ?, `vatergeburt` = ?, `vaterberuf` = ?, `muttername` = ?, `muttergeburt` = ?, `mutterberuf` = ?, `sorgeperson` = ?, `arbeitsort1` = ?, `arbeittel1` = ?, `arbeitsort2` = ?, `arbeittel2` = ?, `geschwgeburt` = ?, `famstand` = ?, `elternort` = ?, `elterntel` = ?, `anzahlgeschw` = ?, `impfung` = ?, `tetanuszeit` = ?, `krankheiten` = ?, `wkrankheit` = ?, `gesundheit` = ?, `arztort` = ?, `arzttel` = ?, `krankenkasse` = ?, `sonstiges` = ? WHERE `id` = ?");			
			p.setByte(1,i.getGruppe());
//			p.setDate(2,kindGeburt);
			p.setString(2,i.getKindGeburtsDatum());
			p.setString(3,i.getEintrittsDatum());
			p.setString(4,i.getEintrittsGrund());
			p.setString(5,i.getAustrittsDatum());
			p.setString(6,i.getAustrittsGrund());
			p.setString(7,i.getKindNachname());
			p.setString(8,i.getKindVorname());
			p.setString(9,i.getGeburtsOrt());
			p.setString(10,i.getKindWohnung());
			p.setString(11,i.getReligion());
			p.setString(12,i.getStaat());
			p.setString(13,i.getKindTelefon());
			p.setString(14,i.getVaterName());
			p.setString(15,i.getVaterGeburt());
			p.setString(16,i.getVaterBeruf());
			p.setString(17,i.getMutterName());
			p.setString(18,i.getMutterGeburt());
			p.setString(19,i.getMutterBeruf());
			p.setString(20,i.getSorgePerson());
			p.setString(21,i.getArbeitsOrt1());
			p.setString(22,i.getArbeitsTelefon1());
			p.setString(23,i.getArbeitsOrt2());
			p.setString(24,i.getArbeitsTelefon2());
			p.setString(25,i.getGeschwisterGeburt());
			p.setByte(26,i.getFamilienStand());
			p.setString(27,i.getElternOrt());
			p.setString(28,i.getElternTelefon());
			p.setByte(29,i.getAnzahlGeschwister());
			p.setString(30,i.getImpfungen());
			p.setString(31,i.getTetanusZeit());
			p.setShort(32,i.getKrankheiten());
			p.setString(33,i.getWeitereKrankheiten());
			p.setString(34,i.getGesundheitsHinweise());
			p.setString(35,i.getArztOrt());
			p.setString(36,i.getArztTelefon());
			p.setString(37,i.getKrankenkasse());
			p.setString(38,i.getSonstiges());
			p.setInt(39,i.getPrimaryKey());
			try {
				p.executeUpdate();
			}
			catch (Exception e) {
				p.executeUpdate();
			}
	}

}

