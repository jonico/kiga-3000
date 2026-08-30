package org.de.kiga3000.database;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.exception.PoolException;
import org.de.kiga3000.interfaces.Karteikarte;
import org.de.kiga3000.interfaces.KarteikartenDatenholer;
import org.de.kiga3000.messages.Messenger;

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
//public class KarteikartenDatenHolerSQLImpl implements KarteikartenDatenholer {
	public class KarteikartenDatenHolerSQLImpl {


	private Connection conn;
	private PreparedStatement p;
	private ResultSet r;
	private boolean dataavailable=false;
	private static Messenger messenger = new Messenger();

	public KarteikartenDatenHolerSQLImpl() {
//				conn=Ressourcen.getConnection(5000);
//				conn.setAutoCommit(true);
//				p=conn.prepareStatement("SELECT `id`, `gruppe` , `kindgeburt` , `eintritt` , `eintrittsgrund` , `austritt` , `austrittsgrund` , `kindnachname` , `kindvorname` , `geburtsort` , `kindwohnung` , `religion` , `staat` , `kindtelefon` , `vatername` , `vatergeburt` , `vaterberuf` , `muttername` , `muttergeburt` , `mutterberuf` , `sorgeperson` , `arbeitsort1` , `arbeittel1` , `arbeitsort2` , `arbeittel2` , `geschwgeburt` , `famstand` , `elternort` , `elterntel` , `anzahlgeschw` , `impfung` , `tetanuszeit` , `krankheiten` , `wkrankheit` , `gesundheit` , `arztort` , `arzttel` , `krankenkasse` , `sonstiges` FROM Karteikarte WHERE `id` = ?");
	}
	/* (non-Javadoc)
	 * @see KarteikartenDatenholer#holeDaten(int)
	 */
/*	public boolean holeDaten(int i) throws SQLException, KigaException {
		p.setInt(1,i);
		try {
			r=p.executeQuery();
		}
		catch (Exception e) {
			r=p.executeQuery();
		}
		dataavailable= r.next();
		return dataavailable;
	}
*/
	/* (non-Javadoc)
	 * @see KarteikartenDatenholer#fill(Karteikarte)
	 */
	public void fill(Karteikarte i,Connection conn,int karteiId) throws SQLException, KigaException {
		conn.setAutoCommit(true);
		p=conn.prepareStatement("SELECT `id`, `gruppe` , `kindgeburt` , `eintritt` , `eintrittsgrund` , `austritt` , `austrittsgrund` " +
				", `kindnachname` , `kindvorname` , `geburtsort` , `kindwohnung` , `religion` , `staat` , `kindtelefon` , `vatername` ," +
				" `vatergeburt` , `vaterberuf` , `muttername` , `muttergeburt` , `mutterberuf` , `sorgeperson` , `arbeitsort1` ," +
				" `arbeittel1` , `arbeitsort2` , `arbeittel2` , `geschwgeburt` , `famstand` , `elternort` , `elterntel` , `anzahlgeschw` ," +
				" `impfung` , `tetanuszeit` , `krankheiten` , `wkrankheit` , `gesundheit` , `arztort` , `arzttel` , `krankenkasse` ," +
				"                         `sonstiges` FROM Karteikarte WHERE `id` = ?");
		p.setInt(1,karteiId);
		try {
			r=p.executeQuery();
		}
		catch (Exception e) {
			r=p.executeQuery();
		}
		dataavailable= r.next();

		if (!dataavailable)
			throw new KigaException(messenger.getMessage("Kiga.DataChild"));
			
		dataavailable=false;
		
		i.setPrimaryKey(r.getInt(1));
		i.setGruppe(r.getByte(2));
		i.setKindGeburtsDatum(SQLHelfer.getStringausDatum(r.getDate(3)));
		i.setEintrittsDatum(SQLHelfer.nullToEmpty(r.getString(4)));
		i.setEintrittsGrund(r.getString(5));
		i.setAustrittsDatum(SQLHelfer.nullToEmpty(r.getString(6)));
		i.setAustrittsGrund(r.getString(7));
		i.setKindNachname(r.getString(8));
		i.setKindVorname(r.getString(9));
		i.setGeburtsOrt(r.getString(10));
		i.setKindWohnung(r.getString(11));
		i.setReligion(r.getString(12));
		i.setStaat(r.getString(13));
		i.setKindTelefon(r.getString(14));
		i.setVaterName(r.getString(15));
		i.setVaterGeburt(SQLHelfer.nullToEmpty(r.getString(16)));
		i.setVaterBeruf(r.getString(17));
		i.setMutterName(r.getString(18));
		i.setMutterGeburt(SQLHelfer.nullToEmpty(r.getString(19)));
		i.setMutterBeruf(r.getString(20));
		i.setSorgePerson(r.getString(21));
		i.setArbeitsOrt1(r.getString(22));
		i.setArbeitsTelefon1(r.getString(23));
		i.setArbeitsOrt2(r.getString(24));
		i.setArbeitsTelefon2(r.getString(25));
		i.setGeschwisterGeburt(r.getString(26));
		i.setFamilienStand(r.getByte(27));
		i.setElternOrt(r.getString(28));
		i.setElternTelefon(r.getString(29));
		i.setAnzahlGeschwister(r.getByte(30));
		i.setImpfungen(r.getString(31));
		i.setTetanusZeit(r.getString(32));
		i.setKrankheiten(r.getShort(33));
		i.setWeitereKrankheiten(r.getString(34));
		i.setGesundheitshinweise(r.getString(35));
		i.setArztOrt(r.getString(36));
		i.setArztTelefon(r.getString(37));
		i.setKrankenkasse(r.getString(38));
		i.setSonstiges(r.getString(39));
		
		r.close();
	}

}
