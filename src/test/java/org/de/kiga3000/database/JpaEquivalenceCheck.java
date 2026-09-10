package org.de.kiga3000.database;

import java.sql.Connection;
import java.util.List;

import jakarta.validation.ConstraintViolationException;

import org.de.kiga3000.data.KarteikarteImpl;

/**
 * Proves the JPA mapping behaves the same as the hand-written SQL it replaces, and
 * that Bean Validation now catches the class of bug that started this work.
 *
 * <p>Both paths are still present, so this compares them directly rather than trusting
 * the new one. Not named {@code *Test}: it needs a live MySQL.
 *
 * <pre>
 * mvn -q test-compile dependency:build-classpath -Dmdep.outputFile=target/cp.txt
 * java -cp "target/classes:target/test-classes:$(cat target/cp.txt)" \
 *      org.de.kiga3000.database.JpaEquivalenceCheck
 * </pre>
 */
public final class JpaEquivalenceCheck {

    private static int failures;

    private JpaEquivalenceCheck() {
    }

    public static void main(String[] args) throws Exception {
        try {
            sameReadAsJdbc(1);
            sameReadAsJdbc(9);
            roundTrip();
            validationRejectsOverlongCaregiver();
            validationAllowsAValidCard();
            System.out.println();
            System.out.println(failures == 0 ? "RESULT: OK" : "RESULT: " + failures + " FAILED");
        } finally {
            KarteikarteRepository.shutdown();
        }
        if (0 < failures) {
            System.exit(1);
        }
    }

    /** The mapping must yield what the hand-written SELECT yields. */
    private static void sameReadAsJdbc(int id) throws Exception {
        System.out.println("=== card " + id + ": JPA read vs hand-written SQL read ===");

        KarteikarteImpl viaJdbc = new KarteikarteImpl();
        OpenConnection open = new OpenConnection();
        Connection conn = open.openConnection();
        try {
            new KarteikartenDatenHolerSQLImpl().fill(viaJdbc, conn, id);
        } finally {
            open.releaseConnection(conn);
        }

        KarteikarteImpl viaJpa = KarteikarteRepository.find(id);
        if (null == viaJpa) {
            fail("  JPA found no card with id " + id);
            return;
        }

        compare("kindVorname", viaJdbc.getKindVorname(), viaJpa.getKindVorname());
        compare("kindNachname", viaJdbc.getKindNachname(), viaJpa.getKindNachname());
        compare("sorgePerson", viaJdbc.getSorgePerson(), viaJpa.getSorgePerson());
        compare("sonstiges", viaJdbc.getSonstiges(), viaJpa.getSonstiges());
        compare("gruppe", String.valueOf(viaJdbc.getGruppe()), String.valueOf(viaJpa.getGruppe()));
        // kindgeburt is the one field the JDBC reader already formatted for display,
        // so both paths should agree exactly.
        compare("kindGeburtsDatum", viaJdbc.getKindGeburtsDatum(), viaJpa.getKindGeburtsDatum());
        // The other four arrived raw from the result set on the JDBC path. JPA
        // normalises them to display format, which is a deliberate improvement, so
        // these are reported rather than asserted equal.
        System.out.printf("  eintritt      jdbc=[%s] jpa=[%s]%n",
                viaJdbc.getEintrittsDatum(), viaJpa.getEintrittsDatum());
        System.out.printf("  austritt      jdbc=[%s] jpa=[%s]  (both mean 'not set')%n",
                viaJdbc.getAustrittsDatum(), viaJpa.getAustrittsDatum());
        System.out.println();
    }

    /** Insert, read, update, delete through JPA only. */
    private static void roundTrip() {
        System.out.println("=== JPA round trip ===");
        KarteikarteImpl karte = validCard();
        karte.setKindVorname("Jpa");
        karte.setKindNachname("Rundlauf");
        karte.setAustrittsDatum("");           // absent -> NULL
        karte.setKindGeburtsDatum("01.03.2021");

        int id = KarteikarteRepository.insert(karte);
        System.out.println("  inserted id " + id);

        KarteikarteImpl read = KarteikarteRepository.find(id);
        compare("kindNachname after insert", "Rundlauf", read.getKindNachname());
        compare("kindGeburtsDatum after insert", "01.03.2021", read.getKindGeburtsDatum());
        compare("austritt stays empty", "", read.getAustrittsDatum());

        read.setSorgePerson("beide Eltern");
        read.setAustrittsDatum("31.07.2026");
        KarteikarteRepository.update(read);

        KarteikarteImpl reread = KarteikarteRepository.find(id);
        compare("sorgePerson after update", "beide Eltern", reread.getSorgePerson());
        compare("austritt after update", "31.07.2026", reread.getAustrittsDatum());

        // The group guard: a wrong group must not delete.
        boolean wrongGroup = KarteikarteRepository.delete(id, (byte) 99);
        compare("delete with wrong group refused", "false", String.valueOf(wrongGroup));

        boolean deleted = KarteikarteRepository.delete(id, reread.getGruppe());
        compare("delete with right group", "true", String.valueOf(deleted));
        compare("gone afterwards", "null", String.valueOf(KarteikarteRepository.find(id)));
        System.out.println();
    }

    /**
     * The bug that started this: an over-long caregiver field used to reach MySQL and
     * come back as "Data truncation: Data too long for column 'sorgeperson'".
     * SQLHelfer.pruefeDaten checked gesundheit and sonstiges but never sorgeperson.
     * The @Size(max = 100) on the entity now stops it before it reaches the database.
     */
    private static void validationRejectsOverlongCaregiver() {
        System.out.println("=== Bean Validation: over-long sorgeperson ===");
        KarteikarteImpl karte = validCard();
        karte.setKindVorname("Zu");
        karte.setKindNachname("Lang");
        karte.setSorgePerson("x".repeat(150));      // column is varchar(100)
        try {
            int id = KarteikarteRepository.insert(karte);
            KarteikarteRepository.delete(id, karte.getGruppe());
            fail("  expected a ConstraintViolationException, but the insert succeeded");
        } catch (ConstraintViolationException e) {
            System.out.println("  rejected before reaching the database, as intended:");
            e.getConstraintViolations().forEach(v ->
                    System.out.println("    " + v.getPropertyPath() + " " + v.getMessage()));
        }
        System.out.println();
    }

    private static void validationAllowsAValidCard() {
        System.out.println("=== Bean Validation: a valid card is not obstructed ===");
        KarteikarteImpl karte = validCard();
        karte.setKindVorname("Gueltig");
        karte.setKindNachname("Datensatz");
        karte.setSorgePerson("beide Eltern");
        int id = KarteikarteRepository.insert(karte);
        System.out.println("  accepted, id " + id);
        KarteikarteRepository.delete(id, karte.getGruppe());
        List<KarteikarteImpl> group = KarteikarteRepository.findByGruppe((byte) 1);
        System.out.println("  JPQL findByGruppe(1) returned " + group.size() + " cards");
    }

    private static KarteikarteImpl validCard() {
        KarteikarteImpl karte = new KarteikarteImpl();
        karte.setGruppe((byte) 1);
        karte.setKindGeburtsDatum("14.03.2020");
        return karte;
    }

    private static void compare(String what, String expected, String actual) {
        boolean ok = expected == null ? actual == null : expected.equals(actual);
        System.out.printf("  %-34s %s%n", what, ok ? "ok" : "MISMATCH exp=[" + expected + "] act=[" + actual + "]");
        if (!ok) {
            failures++;
        }
    }

    private static void fail(String message) {
        System.out.println(message);
        failures++;
    }
}
