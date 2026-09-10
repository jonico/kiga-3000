package org.de.kiga3000.data;

import java.text.ParseException;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import org.de.kiga3000.conversion.DateConversion;

/**
 * Maps the card's {@code String} date fields onto real SQL {@code DATE} columns.
 *
 * <p>The card carries its five dates as strings, because that is what the Swing text
 * fields hold and what every layer between them passes around. The database stores
 * them as {@code DATE}. This converter is the single place that boundary is crossed.
 *
 * <p>Two conventions it upholds:
 * <ul>
 *   <li>"no date" is the empty string on the Java side and SQL {@code NULL} in the
 *       database. It used to be the literal {@code '0000-00-00'}, which required a
 *       relaxed {@code sql_mode} and cannot be represented by {@link java.sql.Date} -
 *       the reason the columns were made nullable before introducing JPA.</li>
 *   <li>Entity attributes are always in the German display format
 *       ({@code dd.MM.yyyy}). The hand-written DAOs produced a mix - {@code kindgeburt}
 *       pre-formatted for display, the rest raw from the result set - which callers had
 *       to tolerate. Reading through JPA now yields one consistent format.</li>
 * </ul>
 *
 * <p>Parsing accepts either format anyway, so a value that arrives from the user
 * interface and one that arrives from a hand-written query are both understood.
 */
@Converter
public class DateStringConverter implements AttributeConverter<String, java.sql.Date> {

    private static final DateConversion CONVERSION = new DateConversion();

    /**
     * @return the date, or null when the field is empty
     */
    @Override
    public java.sql.Date convertToDatabaseColumn(String attribute) {
        if (null == attribute || attribute.trim().isEmpty()) {
            return null;
        }
        try {
            String iso = CONVERSION.StringToMysqlDate(attribute);
            return iso.isEmpty() ? null : java.sql.Date.valueOf(iso);
        } catch (ParseException e) {
            /*
             * An unparseable date is a validation failure, not a mapping failure, and
             * throwing here would surface as a Hibernate exception from deep inside a
             * flush. Storing NULL matches what the JDBC write path used to do
             * and leaves the reporting to Bean Validation and CheckKarteiKarte.
             */
            return null;
        }
    }

    /**
     * @return the date in display format, or the empty string for SQL NULL
     */
    @Override
    public String convertToEntityAttribute(java.sql.Date dbData) {
        if (null == dbData) {
            return "";
        }
        try {
            return CONVERSION.StringToLocalDate(dbData.toString());
        } catch (ParseException e) {
            // java.sql.Date.toString() is always uuuu-MM-dd, so this cannot happen;
            // returning the raw form is still better than propagating.
            return dbData.toString();
        }
    }
}
