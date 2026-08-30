package org.de.kiga3000.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

/**
 * Round-trips every accessor pair on the card data object.
 *
 * <p>This exists because of a real defect. {@code KarteikartenDialogSwingImpl}
 * contained
 *
 * <pre>
 *   public String getSorgePerson() { return karteikarteSwi.getSonstiges(); }
 * </pre>
 *
 * a copy-paste slip in a generated stub, with a correct setter. Every card update
 * therefore wrote the {@code sonstiges} text into the {@code sorgeperson} column, and
 * because {@code sonstiges} is {@code varchar(255)} while {@code sorgeperson} is
 * {@code varchar(100)}, a long note produced "Data truncation: Data too long for
 * column 'sorgeperson'" - while a short note was silently filed in the wrong column.
 *
 * <p>A test cannot easily reach that class, which extends a JDialog and needs a
 * display. What it can do is assert the invariant on the data object every layer
 * copies through: each field must give back exactly what was put in, and no two
 * fields may share storage. Giving every field a distinct value makes any crossed
 * wiring show up immediately.
 *
 * <p>{@code tools/audit-delegating-accessors.py} covers the delegating views that this
 * test cannot instantiate.
 */
public class KarteikarteImplTest {

    /** Fields that are not simple String storage and are exercised separately. */
    private static final List<String> NON_STRING =
            List.of("PrimaryKey", "Gruppe", "FamilienStand", "AnzahlGeschwister", "Krankheiten");

    @Test
    public void everyStringFieldRoundTripsItsOwnValue() throws Exception {
        KarteikarteImpl karte = new KarteikarteImpl();

        Map<String, Method> getters = new TreeMap<>();
        Map<String, Method> setters = new TreeMap<>();
        for (Method m : KarteikarteImpl.class.getMethods()) {
            if (m.getDeclaringClass() == Object.class) {
                continue;
            }
            // Keyed case-insensitively on purpose: this class spells one field
            // getGesundheitsHinweise / setGesundheitshinweise, and a case-sensitive
            // pairing would silently drop it from the round trip.
            if (m.getName().startsWith("get") && m.getParameterCount() == 0
                    && m.getReturnType() == String.class) {
                getters.put(m.getName().substring(3).toLowerCase(), m);
            } else if (m.getName().startsWith("set") && m.getParameterCount() == 1
                    && m.getParameterTypes()[0] == String.class) {
                setters.put(m.getName().substring(3).toLowerCase(), m);
            }
        }

        List<String> paired = new ArrayList<>(getters.keySet());
        paired.retainAll(setters.keySet());
        NON_STRING.forEach(name -> paired.remove(name.toLowerCase()));

        // Guard against the reflection silently finding nothing.
        assertTrue(paired.size() >= 30,
                "expected at least 30 String field pairs, found " + paired.size());

        // A value unique per field, so a crossed wire cannot coincidentally pass.
        for (String field : paired) {
            setters.get(field).invoke(karte, "value-of-" + field);
        }
        for (String field : paired) {
            assertEquals("value-of-" + field, getters.get(field).invoke(karte),
                    "field " + field + " did not return its own value - accessors crossed?");
        }
    }

    @Test
    public void numericFieldsRoundTrip() {
        KarteikarteImpl karte = new KarteikarteImpl();

        karte.setPrimaryKey(4711);
        karte.setGruppe((byte) 3);
        karte.setFamilienStand((byte) 2);
        karte.setAnzahlGeschwister((byte) 5);
        karte.setKrankheiten((short) 1234);

        assertEquals(4711, karte.getPrimaryKey());
        assertEquals((byte) 3, karte.getGruppe());
        assertEquals((byte) 2, karte.getFamilienStand());
        assertEquals((byte) 5, karte.getAnzahlGeschwister());
        assertEquals((short) 1234, karte.getKrankheiten());
    }

    @Test
    public void germanCharactersSurviveTheRoundTrip() {
        // The sources are declared ISO-8859-1, the database is utf8mb4 and the JDBC
        // driver sits in between, so this is worth asserting somewhere cheap.
        KarteikarteImpl karte = new KarteikarteImpl();
        karte.setKindNachname("Roßberg-Übergang");
        karte.setKindVorname("Jörg");
        karte.setGeburtsOrt("Düren");

        assertEquals("Roßberg-Übergang", karte.getKindNachname());
        assertEquals("Jörg", karte.getKindVorname());
        assertEquals("Düren", karte.getGeburtsOrt());
    }

    @Test
    public void distinctFieldsDoNotShareStorage() {
        // The specific pair the 2006 bug crossed.
        KarteikarteImpl karte = new KarteikarteImpl();
        karte.setSorgePerson("Frau Muster");
        karte.setSonstiges("a much longer note about the child, of the kind that "
                + "overflowed the sorgeperson column when the two were crossed");

        assertEquals("Frau Muster", karte.getSorgePerson());
        assertTrue(karte.getSonstiges().length() > karte.getSorgePerson().length());
    }
}
