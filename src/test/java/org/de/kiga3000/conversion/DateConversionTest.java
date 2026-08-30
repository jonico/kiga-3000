package org.de.kiga3000.conversion;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.text.ParseException;
import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * The original version of this class had no assertions at all: both methods printed to
 * stdout and caught {@link ParseException} by printing "Mist". That is why the date
 * handling could be completely broken from JDK 9 onwards while the test suite stayed
 * green.
 *
 * <p>These tests assert instead, and deliberately include the two cases that matter:
 * a database-format date (which the old {@code Locale.CHINA} fallback was meant to
 * handle) and explicit locale switching (which is what actually broke).
 */
public class DateConversionTest {

    private final DateConversion dateconv = new DateConversion();
    private final Locale original = Locale.getDefault();

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(original);
    }

    // ---------------------------------------------------------------- to display

    @Test
    public void databaseFormatIsConvertedForDisplay() throws ParseException {
        // eintritt, austritt, vatergeburt and muttergeburt reach this method in the
        // MySQL wire format, straight from ResultSet.getString().
        assertEquals("03.02.2001", dateconv.StringToLocalDate("2001-02-03"));
    }

    @Test
    public void displayFormatIsAccepted() throws ParseException {
        // kindgeburt arrives already formatted for display, via SQLHelfer.
        assertEquals("03.02.2001", dateconv.StringToLocalDate("03.02.2001"));
    }

    @Test
    public void zeroDateBecomesEmpty() throws ParseException {
        assertEquals("", dateconv.StringToLocalDate("0000-00-00"));
    }

    @Test
    public void emptyStaysEmpty() throws ParseException {
        assertEquals("", dateconv.StringToLocalDate(""));
    }

    // --------------------------------------------------------------- to database

    @Test
    public void displayFormatIsConvertedForTheDatabase() throws ParseException {
        assertEquals("2005-02-28", dateconv.StringToMysqlDate("28.02.2005"));
    }

    @Test
    public void noDateBecomesEmptyNotTheOldSentinel() throws ParseException {
        // The date columns are nullable now, so "no date" is the empty string here and
        // SQL NULL at the mapping boundary (DateStringConverter). It used to be the literal
        // "0000-00-00", which the server only accepted under a relaxed sql_mode.
        assertEquals("", dateconv.StringToMysqlDate(""));
    }

    @Test
    public void theOldSentinelIsStillUnderstoodOnInput() throws ParseException {
        // A database that has not been migrated may still contain it.
        assertEquals("", dateconv.StringToLocalDate("0000-00-00"));
        assertEquals("", dateconv.StringToMysqlDate("0000-00-00"));
    }

    @Test
    public void roundTripIsStable() throws ParseException {
        String display = "29.02.2004"; // a real leap day
        assertEquals("2004-02-29", dateconv.StringToMysqlDate(display));
        assertEquals(display, dateconv.StringToLocalDate("2004-02-29"));
    }

    // ------------------------------------------------------------------ rejection

    @Test
    public void impossibleDateIsRejected() {
        // 2005 was not a leap year. The old implementation was lenient and would have
        // silently produced 02.03.2005 for a child's date of birth.
        assertThrows(ParseException.class, () -> dateconv.StringToLocalDate("30.02.2005"));
    }

    @Test
    public void garbageIsRejected() {
        assertThrows(ParseException.class, () -> dateconv.StringToLocalDate("not a date"));
    }

    @Test
    public void nullDateFormatsAsEmpty() throws ParseException {
        assertEquals("", dateconv.DateToLocalDate(null));
    }

    // ------------------------------------------------------- the actual regression

    /**
     * The bug this class was fixed for. The old implementation derived both formats
     * from locale data - {@code Locale.getDefault()} for display and
     * {@code Locale.CHINA} as a stand-in for the database format - so conversion
     * results depended on the machine's locale, and stopped working entirely once
     * JDK 9 moved from COMPAT to CLDR locale data.
     */
    @Test
    public void conversionDoesNotDependOnTheDefaultLocale() throws ParseException {
        for (Locale locale : new Locale[] {
                Locale.US, Locale.GERMANY, Locale.CHINA, Locale.JAPAN, Locale.ROOT }) {
            Locale.setDefault(locale);
            assertEquals("03.02.2001", dateconv.StringToLocalDate("2001-02-03"),
                    "database format must parse under locale " + locale);
            assertEquals("03.02.2001", dateconv.StringToLocalDate("03.02.2001"),
                    "display format must parse under locale " + locale);
            assertEquals("2001-02-03", dateconv.StringToMysqlDate("03.02.2001"),
                    "display format must convert under locale " + locale);
        }
    }
}
