package org.de.kiga3000.conversion;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.Date;

/**
 * @author bobo_local
 * <br>this class provides date conversion functionalities
 *
 * <p>Two formats are involved, and both are now stated explicitly:
 * <ul>
 *   <li>{@code dd.MM.uuuu} - what the German user interface shows and accepts.</li>
 *   <li>{@code uuuu-MM-dd} - the MySQL {@code DATE} wire format. Some card fields
 *       ({@code eintritt}, {@code austritt}, {@code vatergeburt}, {@code muttergeburt})
 *       are read straight out of the {@code ResultSet} with {@code getString()} and so
 *       arrive in this format, while {@code kindgeburt} arrives already formatted for
 *       display by {@link org.de.kiga3000.database.SQLHelfer}. Both therefore have to
 *       be accepted here.</li>
 * </ul>
 *
 * <p>Historical note, because this was a real bug rather than a style problem. The
 * original 2004 implementation obtained both formats from the JDK's locale data:
 *
 * <pre>
 *   dateToLocale = DateFormat.getDateInstance(MEDIUM, Locale.getDefault());
 *   dateToMysql  = DateFormat.getDateInstance(MEDIUM, Locale.CHINA);
 * </pre>
 *
 * <p>{@code Locale.CHINA} was a stand-in for the database format: under Java 8's COMPAT
 * locale data, China's MEDIUM pattern was {@code yyyy-M-d}, close enough to MySQL's
 * ISO format to parse it. JDK 9 switched the default locale data provider to CLDR,
 * where China's MEDIUM pattern became {@code y年M月d日}. From that release on, the
 * fallback could no longer parse a database date, and neither could the primary format
 * unless the machine's default locale happened to be German. Opening any card therefore
 * threw {@link ParseException} five times over - once per date field - on every JDK
 * from 9 onwards.
 *
 * <p>The bug was invisible for years because the 2004 MySQL driver could not
 * authenticate against any modern server, so the application never got far enough to
 * read a card. It surfaced the moment the driver was updated.
 *
 * <p>Fixed formats also mean the stored and displayed representation no longer depend
 * on the machine's locale, which matters for a German application that may be run on
 * an English-locale desktop.
 */
public class DateConversion {

    /** What the user sees and types, e.g. {@code 03.02.2001}. */
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);

    /** The MySQL {@code DATE} format, e.g. {@code 2001-02-03}. */
    private static final DateTimeFormatter DATABASE_FORMAT =
            DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);

    /**
     * The sentinel the application used to store for "no date".
     *
     * <p>No longer written: the date columns are nullable and SQL NULL means "no date"
     * (see db/migrate-01-nullable-dates.sql). It is still RECOGNISED on input, so that
     * a database which has not been migrated, or a value that survived somewhere, is
     * still understood rather than reported as a broken date.
     */
    private static final String LEGACY_NO_DATE = "0000-00-00";

    /**
     * checks if given date is valid and changes database format to locale
     *
     * <p>Accepts either the display format or the database format, so it can be used on
     * values coming from the user interface and on values read directly from the
     * database.
     *
     * @param datum Date
     * @return      formatted date, or the empty string when no date is set
     * @throws ParseException Date is not valid
     */
    public String StringToLocalDate(String datum) throws ParseException {
        if (isBlank(datum)) {
            return "";
        }
        return DISPLAY_FORMAT.format(parseEitherFormat(datum));
    }

    /**
     * formats a {@link Date} for display
     *
     * @param datum date, may be null
     * @return      formatted date, or the empty string when no date is set
     * @throws ParseException never thrown; retained for source compatibility
     */
    public String DateToLocalDate(Date datum) throws ParseException {
        // The original compared a Date against the strings "" and "0000-00-00", which
        // can never be true. A null check is what was actually meant.
        if (datum == null) {
            return "";
        }
        return DISPLAY_FORMAT.format(
                datum.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
    }

    /**
     * formats date to database format
     *
     * @param datum date in display format
     * @return     formatted database date, or the empty string when none is given
     * @throws ParseException date is not valid
     */
    public String StringToMysqlDate(String datum) throws ParseException {
        if (isBlank(datum)) {
            // Was NO_DATE ("0000-00-00"). The columns are nullable now, so "no date"
            // is the empty string here and SQL NULL at the binding site - see
            // DateStringConverter. Returning the old sentinel would write a date MySQL
            // rejects unless sql_mode is relaxed.
            return "";
        }
        return DATABASE_FORMAT.format(parseEitherFormat(datum));
    }

    /** Empty, whitespace-only, null, or the MySQL zero-date sentinel. */
    private static boolean isBlank(String datum) {
        return datum == null || datum.trim().isEmpty() || LEGACY_NO_DATE.equals(datum.trim());
    }

    /**
     * Parses display format first, then database format.
     *
     * @throws ParseException wrapping the failure, so that the existing callers - which
     *         all catch {@link ParseException} and degrade gracefully - keep working.
     *         {@link DateTimeParseException} is unchecked and would otherwise escape
     *         those handlers and reach the user as an unhandled exception.
     */
    private static LocalDate parseEitherFormat(String datum) throws ParseException {
        String trimmed = datum.trim();
        try {
            return LocalDate.parse(trimmed, DISPLAY_FORMAT);
        } catch (DateTimeParseException displayFailed) {
            try {
                return LocalDate.parse(trimmed, DATABASE_FORMAT);
            } catch (DateTimeParseException databaseFailed) {
                ParseException pe = new ParseException(
                        "Unparseable date: \"" + datum + "\" (expected dd.MM.yyyy or yyyy-MM-dd)",
                        0);
                pe.initCause(databaseFailed);
                throw pe;
            }
        }
    }
}
