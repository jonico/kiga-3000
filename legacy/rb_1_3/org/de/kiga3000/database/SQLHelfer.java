package org.de.kiga3000.database;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

import org.de.kiga3000.exception.KigaException;
import org.de.kiga3000.interfaces.Karteikarte;
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

/* added internationalization by bdiemer 2005-11-30 */

public class SQLHelfer {
	private static DateFormat df=DateFormat.getDateInstance(DateFormat.MEDIUM,Locale.GERMAN);
	private static Messenger messenger = new Messenger();
	public static  java.sql.Date  pruefeDaten (Karteikarte i) throws KigaException{
		if (i.getKindGeburtsDatum().length()<6)
			throw new KigaException(messenger.getMessage("KiGa.DateOfBirthChildToShort"));
		if (i.getGesundheitsHinweise().length()>255)
			throw new KigaException(messenger.getMessage("KiGa.HealthInfoTooLong"));
		if (i.getSonstiges().length()>255)
			throw new KigaException(messenger.getMessage("KiGa.InfoTooLong"));
		if (i.getKindVorname().length()<1)
			throw new KigaException(messenger.getMessage("KiGa.SurnameChildTooShort"));
		if (i.getKindNachname().length()<1)
			throw new KigaException(messenger.getMessage("KiGa.NameOfChildTooShort"));
		return getDatumausString(i.getKindGeburtsDatum());
	}
	
	public static String getStringausDatum(java.sql.Date i) {
		Date dummy=new Date(i.getTime());
		return df.format(dummy);
	}
	
	public static java.sql.Date getDatumausString(String i) throws KigaException {
		df.setLenient(true);
		Date dummy;
		try {
				dummy=df.parse(i);
		} catch (ParseException e) {
			throw new KigaException(messenger.getMessage("KiGa.DateOfBirthChildWrongFormat"));
		}
		return new java.sql.Date(dummy.getTime());
	}
}	
