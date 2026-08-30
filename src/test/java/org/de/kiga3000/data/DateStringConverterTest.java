package org.de.kiga3000.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Locale;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * The String-to-DATE boundary, tested without a database.
 *
 * <p>This converter carries the convention that replaced the {@code '0000-00-00'}
 * sentinel: "no date" is the empty string in Java and SQL {@code NULL} in the
 * database.
 */
public class DateStringConverterTest {

    private final DateStringConverter converter = new DateStringConverter();
    private final Locale original = Locale.getDefault();

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(original);
    }

    // ------------------------------------------------------------ entity -> column

    @Test
    public void displayFormatBecomesADate() {
        assertEquals(java.sql.Date.valueOf("2001-02-03"),
                converter.convertToDatabaseColumn("03.02.2001"));
    }

    @Test
    public void databaseFormatIsAlsoAccepted() {
        // Values can arrive from a hand-written query as well as from the UI.
        assertEquals(java.sql.Date.valueOf("2001-02-03"),
                converter.convertToDatabaseColumn("2001-02-03"));
    }

    @Test
    public void emptyBecomesNull() {
        assertNull(converter.convertToDatabaseColumn(""));
        assertNull(converter.convertToDatabaseColumn("   "));
        assertNull(converter.convertToDatabaseColumn(null));
    }

    @Test
    public void theRetiredSentinelBecomesNull() {
        // A database that has not run migrate-01 may still contain it.
        assertNull(converter.convertToDatabaseColumn("0000-00-00"));
    }

    @Test
    public void unparseableBecomesNullRatherThanThrowing() {
        // Throwing here would surface as a Hibernate exception from inside a flush.
        // Reporting invalid input is Bean Validation's job, not the mapper's.
        assertNull(converter.convertToDatabaseColumn("not a date"));
        assertNull(converter.convertToDatabaseColumn("30.02.2005"));
    }

    // ------------------------------------------------------------ column -> entity

    @Test
    public void aDateBecomesDisplayFormat() {
        assertEquals("03.02.2001",
                converter.convertToEntityAttribute(java.sql.Date.valueOf("2001-02-03")));
    }

    @Test
    public void nullBecomesEmpty() {
        assertEquals("", converter.convertToEntityAttribute(null));
    }

    // ------------------------------------------------------------------ round trip

    @Test
    public void roundTripIsStable() {
        String display = "29.02.2004"; // leap day
        java.sql.Date stored = converter.convertToDatabaseColumn(display);
        assertEquals("2004-02-29", stored.toString());
        assertEquals(display, converter.convertToEntityAttribute(stored));
    }

    /**
     * The mapping must not depend on the machine's locale - the defect that broke
     * DateConversion for every JDK from 9 onwards.
     */
    @Test
    public void conversionDoesNotDependOnTheDefaultLocale() {
        for (Locale locale : new Locale[] {
                Locale.US, Locale.GERMANY, Locale.CHINA, Locale.JAPAN, Locale.ROOT }) {
            Locale.setDefault(locale);
            assertEquals(java.sql.Date.valueOf("2001-02-03"),
                    converter.convertToDatabaseColumn("03.02.2001"),
                    "entity -> column must not depend on locale " + locale);
            assertEquals("03.02.2001",
                    converter.convertToEntityAttribute(java.sql.Date.valueOf("2001-02-03")),
                    "column -> entity must not depend on locale " + locale);
        }
    }
}
