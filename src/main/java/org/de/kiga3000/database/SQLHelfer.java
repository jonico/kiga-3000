package org.de.kiga3000.database;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
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
	/*
	 * Explicit pattern rather than DateFormat.getDateInstance(MEDIUM, Locale.GERMAN).
	 * That call currently yields "dd.MM.y" and so produces the same output, but it
	 * reads the pattern from the JDK's locale data, which is exactly how
	 * DateConversion broke: JDK 9 replaced COMPAT locale data with CLDR and silently
	 * changed the patterns underneath the application. Stating the format here means
	 * the string this class hands to DateConversion cannot drift again.
	 *
	 * Leniency is left as the original code set it (see getDatumausString); tightening
	 * it would change which inputs are accepted, which is a separate decision.
	 */
	private static DateFormat df=new SimpleDateFormat("dd.MM.yyyy",Locale.ROOT);
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
	
	/**
	 * Formats a date column for display.
	 *
	 * <p>Returns the empty string for NULL. The date columns became nullable when the
	 * '0000-00-00' sentinel was retired, so {@code ResultSet.getDate} now returns null
	 * for a card with no leaving date - previously it returned a rounded zero date.
	 * Without this guard the old body dereferenced null and threw
	 * NullPointerException while loading perfectly ordinary cards.
	 */
	public static String getStringausDatum(java.sql.Date i) {
		if (i == null) {
			return "";
		}
		Date dummy=new Date(i.getTime());
		return df.format(dummy);
	}

	/**
	 * Parses a display-format date.
	 *
	 * @return null for blank input, which the caller binds as SQL NULL
	 */
	public static java.sql.Date getDatumausString(String i) throws KigaException {
		if (i == null || i.trim().isEmpty()) {
			return null;
		}
		df.setLenient(true);
		Date dummy;
		try {
				dummy=df.parse(i.trim());
		} catch (ParseException e) {
			throw new KigaException(messenger.getMessage("KiGa.DateOfBirthChildWrongFormat"));
		}
		return new java.sql.Date(dummy.getTime());
	}

	/**
	 * Coerces SQL NULL to the empty string.
	 *
	 * <p>The five date columns are nullable since the 0000-00-00 sentinel was
	 * retired, so {@code ResultSet.getString} returns null for them. Every field on
	 * {@link org.de.kiga3000.data.KarteikarteImpl} otherwise defaults to "" and no
	 * consumer expects null, so the reader normalises here rather than leaving each
	 * caller to remember.
	 */
	public static String nullToEmpty(String i) {
		return i == null ? "" : i;
	}

}	
