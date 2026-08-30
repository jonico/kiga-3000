package org.de.kiga3000.conversion;

import java.sql.Connection;

import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.database.KarteikartenDatenHolerSQLImpl;
import org.de.kiga3000.database.OpenConnection;
import org.de.kiga3000.interfaces.Karteikarte;

/**
 * Reproduces, against a live database, the exact sequence that failed when a user
 * clicked a card in the search results to edit it.
 *
 * <p>The path is: {@code Ressourcen} / {@code ConnectionPool} for a connection,
 * {@code KarteikartenDatenHolerSQLImpl.fill()} to load the row, then the five
 * {@code DateConversion.StringToLocalDate} calls that
 * {@code KarteiKarteCommunicate} makes when it populates the card view. Before the
 * fix, every one of those five threw {@link java.text.ParseException} on any JDK from
 * 9 onwards, because the conversion formats were taken from locale data.
 *
 * <p>Note the deliberately mixed input formats this exercises, which is why the bug
 * needed two accepted formats rather than one: {@code kindgeburt} is pre-formatted for
 * display by {@code SQLHelfer} ({@code dd.MM.yyyy}), while {@code eintritt},
 * {@code austritt}, {@code vatergeburt} and {@code muttergeburt} come straight from
 * {@code ResultSet.getString()} in MySQL wire format ({@code yyyy-MM-dd}) or as the
 * {@code 0000-00-00} sentinel.
 *
 * <p>Not named {@code *Test}, so surefire never picks it up: it needs a running MySQL.
 * Run it by hand:
 *
 * <pre>
 * mvn -q test-compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
 * java -cp "target/classes:target/test-classes:$(cat target/cp.txt)" \
 *      org.de.kiga3000.conversion.DateConversionEndToEndCheck
 * </pre>
 */
public final class DateConversionEndToEndCheck {

    private DateConversionEndToEndCheck() {
    }

    public static void main(String[] args) throws Exception {
        int karteiId = args.length > 0 ? Integer.parseInt(args[0]) : 1;

        OpenConnection open = new OpenConnection();
        Connection conn = open.openConnection();
        try {
            Karteikarte karte = new KarteikarteImpl();
            new KarteikartenDatenHolerSQLImpl().fill(karte, conn, karteiId);

            System.out.println("card id " + karteiId + ": "
                    + karte.getKindVorname() + " " + karte.getKindNachname());
            System.out.println();
            System.out.printf("%-16s %-14s -> %s%n", "field", "as stored", "as displayed");

            DateConversion conv = new DateConversion();
            // exactly the five conversions KarteiKarteCommunicate performs
            show(conv, "kindgeburt", karte.getKindGeburtsDatum());
            show(conv, "eintritt", karte.getEintrittsDatum());
            show(conv, "austritt", karte.getAustrittsDatum());
            show(conv, "vatergeburt", karte.getVaterGeburt());
            show(conv, "muttergeburt", karte.getMutterGeburt());

            System.out.println();
            System.out.println("RESULT: OK - all five date fields converted without throwing");
        } finally {
            open.releaseConnection(conn);
        }
    }

    private static void show(DateConversion conv, String field, String stored)
            throws Exception {
        String displayed = conv.StringToLocalDate(stored);
        System.out.printf("%-16s %-14s -> %s%n",
                field,
                "[" + stored + "]",
                displayed.isEmpty() ? "(empty)" : displayed);
    }
}
