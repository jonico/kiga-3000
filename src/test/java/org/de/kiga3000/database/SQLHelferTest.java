package org.de.kiga3000.database;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Locale;

import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.exception.KigaException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * Covers the date helpers and the field-length validation, neither of which needs a
 * database connection.
 *
 * <p>{@code SQLHelfer} formats the string that {@code DateConversion} then has to
 * parse, so the two must agree on a format. That contract used to be implicit - both
 * derived it from JDK locale data - and JDK 9's move from COMPAT to CLDR locale data
 * broke it. These tests pin the format down.
 */
public class SQLHelferTest {

    private final Locale original = Locale.getDefault();

    @AfterEach
    void restoreLocale() {
        Locale.setDefault(original);
    }

    @Test
    public void sqlDateIsFormattedForDisplay() {
        java.sql.Date date = java.sql.Date.valueOf("2001-02-03");
        assertEquals("03.02.2001", SQLHelfer.getStringausDatum(date));
    }

    @Test
    public void displayStringIsParsedBackToSqlDate() throws KigaException {
        java.sql.Date parsed = SQLHelfer.getDatumausString("03.02.2001");
        assertEquals("2001-02-03", parsed.toString());
    }

    @Test
    public void dateHelpersRoundTrip() throws KigaException {
        java.sql.Date original = java.sql.Date.valueOf("2019-11-05");
        String displayed = SQLHelfer.getStringausDatum(original);
        assertEquals("05.11.2019", displayed);
        assertEquals(original.toString(), SQLHelfer.getDatumausString(displayed).toString());
    }

    /**
     * The regression guard. This class used to take its pattern from
     * {@code DateFormat.getDateInstance(MEDIUM, Locale.GERMAN)}, i.e. from locale data.
     * The format the rest of the application depends on must not vary with the
     * machine's locale.
     */
    @Test
    public void formatDoesNotDependOnTheDefaultLocale() throws KigaException {
        java.sql.Date date = java.sql.Date.valueOf("2001-02-03");
        for (Locale locale : new Locale[] {
                Locale.US, Locale.GERMANY, Locale.CHINA, Locale.JAPAN, Locale.ROOT }) {
            Locale.setDefault(locale);
            assertEquals("03.02.2001", SQLHelfer.getStringausDatum(date),
                    "formatting must not depend on locale " + locale);
            assertEquals("2001-02-03", SQLHelfer.getDatumausString("03.02.2001").toString(),
                    "parsing must not depend on locale " + locale);
        }
    }

    @Test
    public void nullDateColumnFormatsAsEmpty() {
        // ResultSet.getDate returns null for a card with no leaving date now that the
        // columns are nullable. The old body dereferenced it and threw NPE.
        assertEquals("", SQLHelfer.getStringausDatum(null));
    }

    @Test
    public void blankStringParsesToNullDate() throws KigaException {
        assertNull(SQLHelfer.getDatumausString(""));
        assertNull(SQLHelfer.getDatumausString("   "));
        assertNull(SQLHelfer.getDatumausString(null));
    }

    @Test
    public void unparseableDateIsReportedAsKigaException() {
        assertThrows(KigaException.class, () -> SQLHelfer.getDatumausString("not a date"));
    }

    // ------------------------------------------------------------ pruefeDaten rules

    private static KarteikarteImpl validCard() {
        KarteikarteImpl karte = new KarteikarteImpl();
        karte.setKindVorname("Mira");
        karte.setKindNachname("Bergmann");
        karte.setKindGeburtsDatum("14.03.2020");
        karte.setGesundheitshinweise("keine");
        karte.setSonstiges("keine");
        return karte;
    }

    @Test
    public void aValidCardPassesValidation() throws KigaException {
        assertNotNull(SQLHelfer.pruefeDaten(validCard()));
    }

    @Test
    public void tooShortDateOfBirthIsRejected() {
        KarteikarteImpl karte = validCard();
        karte.setKindGeburtsDatum("1.1.1"); // fewer than 6 characters
        assertThrows(KigaException.class, () -> SQLHelfer.pruefeDaten(karte));
    }

    @Test
    public void missingForenameIsRejected() {
        KarteikarteImpl karte = validCard();
        karte.setKindVorname("");
        assertThrows(KigaException.class, () -> SQLHelfer.pruefeDaten(karte));
    }

    @Test
    public void missingSurnameIsRejected() {
        KarteikarteImpl karte = validCard();
        karte.setKindNachname("");
        assertThrows(KigaException.class, () -> SQLHelfer.pruefeDaten(karte));
    }

    @Test
    public void overlongHealthNoteIsRejected() {
        KarteikarteImpl karte = validCard();
        karte.setGesundheitshinweise("x".repeat(256)); // column is varchar(255)
        assertThrows(KigaException.class, () -> SQLHelfer.pruefeDaten(karte));
    }

    @Test
    public void overlongAdditionalNoteIsRejected() {
        KarteikarteImpl karte = validCard();
        karte.setSonstiges("x".repeat(256));
        assertThrows(KigaException.class, () -> SQLHelfer.pruefeDaten(karte));
    }

    /**
     * Documents a gap rather than asserting desired behaviour: pruefeDaten checks
     * `gesundheit` and `sonstiges` against their column widths but says nothing about
     * `sorgeperson`, which is varchar(100). That is why the crossed-accessor bug
     * surfaced as a raw MySQL "Data too long" error reaching the user instead of a
     * friendly validation message.
     */
    @Test
    public void overlongCaregiverFieldIsNotValidated() throws KigaException {
        KarteikarteImpl karte = validCard();
        karte.setSorgePerson("x".repeat(150)); // column is varchar(100)
        assertNotNull(SQLHelfer.pruefeDaten(karte),
                "pruefeDaten currently accepts an over-long sorgeperson; if this test "
                        + "starts failing, validation was added and this test should "
                        + "become an assertThrows");
    }
}
