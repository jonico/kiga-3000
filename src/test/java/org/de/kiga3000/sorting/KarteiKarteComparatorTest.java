package org.de.kiga3000.sorting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.de.kiga3000.data.KarteikarteImpl;
import org.junit.jupiter.api.Test;

/**
 * Ordering for the search-results table: surname, then forename, then date of birth,
 * then address.
 *
 * <p>These replace characterisation tests that pinned the previous, inverted behaviour.
 * The old comparator returned the comparison of whichever field it had just found to be
 * EQUAL, so equal surnames returned 0 and the forename tie-break was unreachable, while
 * differing surnames fell through to comparing the address. The practical effect was
 * that the table was unsorted. See the class javadoc on
 * {@link KarteiKarteComparator} for the full description.
 */
public class KarteiKarteComparatorTest {

    private static KarteikarteImpl card(String surname, String forename,
                                       String birth, String address) {
        KarteikarteImpl karte = new KarteikarteImpl();
        karte.setKindNachname(surname);
        karte.setKindVorname(forename);
        karte.setKindGeburtsDatum(birth);
        karte.setKindWohnung(address);
        return karte;
    }

    private final KarteiKarteComparator comparator = new KarteiKarteComparator();

    // ------------------------------------------------------------------- precedence

    @Test
    public void ordersBySurname() {
        assertTrue(comparator.compare(
                card("Achterberg", "Bennet", "31.10.2018", "zzz"),
                card("Bergmann", "Mira", "14.03.2020", "aaa")) < 0,
                "surname must win over every later criterion, including the address");
    }

    @Test
    public void fallsBackToForenameWhenSurnamesMatch() {
        // The Krautheim siblings, which the old comparator could not order at all.
        assertTrue(comparator.compare(
                card("Krautheim", "Leonard", "14.02.2018", "Testallee 7"),
                card("Krautheim", "Sophie", "23.11.2020", "Testallee 7")) < 0);
    }

    @Test
    public void fallsBackToDateOfBirthWhenNamesMatch() {
        assertTrue(comparator.compare(
                card("Muster", "Kind", "01.01.2019", "same"),
                card("Muster", "Kind", "02.01.2019", "same")) < 0);
    }

    @Test
    public void fallsBackToAddressWhenEverythingElseMatches() {
        // This tie-break was unreachable dead code before.
        assertTrue(comparator.compare(
                card("Muster", "Kind", "01.01.2019", "Aaa-Strasse"),
                card("Muster", "Kind", "01.01.2019", "Zzz-Strasse")) < 0);
    }

    @Test
    public void identicalCardsCompareEqual() {
        assertEquals(0, comparator.compare(
                card("Özdemir", "Yusuf", "17.06.2020", "Fiktivstraße 3"),
                card("Özdemir", "Yusuf", "17.06.2020", "Fiktivstraße 3")));
    }

    @Test
    public void comparisonIsCaseInsensitive() {
        assertEquals(0, comparator.compare(
                card("bergmann", "mira", "14.03.2020", "a"),
                card("BERGMANN", "MIRA", "14.03.2020", "a")));
    }

    // ------------------------------------------------------------------------ dates

    /**
     * Dates were compared as {@code dd.MM.yyyy} strings, i.e. by day of month, so
     * 01.12.2019 sorted before 05.01.2019 despite being the later date.
     */
    @Test
    public void datesAreOrderedChronologicallyNotAsStrings() {
        assertTrue(comparator.compare(
                card("Muster", "Kind", "05.01.2019", "x"),
                card("Muster", "Kind", "01.12.2019", "x")) < 0,
                "January must sort before December of the same year");
    }

    @Test
    public void databaseFormatDatesAreAlsoUnderstood() {
        // Cards can carry either format depending on how they were loaded.
        assertTrue(comparator.compare(
                card("Muster", "Kind", "2019-01-05", "x"),
                card("Muster", "Kind", "01.12.2019", "x")) < 0);
    }

    @Test
    public void cardsWithoutADateSortAfterCardsWithOne() {
        assertTrue(comparator.compare(
                card("Muster", "Kind", "01.01.2019", "x"),
                card("Muster", "Kind", "", "x")) < 0);
        assertTrue(comparator.compare(
                card("Muster", "Kind", "", "x"),
                card("Muster", "Kind", "01.01.2019", "x")) > 0);
    }

    @Test
    public void twoCardsWithoutADateCompareEqualOnThatCriterion() {
        // Keeps the ordering transitive, which the old comparator was not.
        assertEquals(0, comparator.compare(
                card("Muster", "Kind", "", "same"),
                card("Muster", "Kind", "", "same")));
    }

    @Test
    public void unparseableDatesAreTreatedAsAbsentRatherThanThrowing() {
        assertEquals(0, comparator.compare(
                card("Muster", "Kind", "not a date", "same"),
                card("Muster", "Kind", "30.02.2005", "same")),
                "an impossible date and a garbage date both count as absent");
    }

    // -------------------------------------------------------------------- contract

    @Test
    public void sortingProducesNameOrder() {
        List<KarteikarteImpl> cards = new ArrayList<>(Arrays.asList(
                card("Winkelmann", "Tobias", "02.07.2019", "shared"),
                card("Achterberg", "Bennet", "31.10.2018", "shared"),
                card("Krautheim", "Sophie", "23.11.2020", "shared"),
                card("Krautheim", "Leonard", "14.02.2018", "shared"),
                card("Bergmann", "Mira", "14.03.2020", "shared")));

        cards.sort(comparator);

        assertEquals(
                Arrays.asList("Achterberg", "Bergmann", "Krautheim", "Krautheim", "Winkelmann"),
                cards.stream().map(KarteikarteImpl::getKindNachname).toList());
        // and the two Krautheims by forename
        assertEquals(Arrays.asList("Leonard", "Sophie"),
                cards.stream()
                        .filter(c -> c.getKindNachname().equals("Krautheim"))
                        .map(KarteikarteImpl::getKindVorname)
                        .toList());
    }

    @Test
    public void isAntisymmetric() {
        KarteikarteImpl a = card("Achterberg", "Bennet", "31.10.2018", "x");
        KarteikarteImpl b = card("Bergmann", "Mira", "14.03.2020", "y");
        assertTrue(comparator.compare(a, b) < 0);
        assertTrue(comparator.compare(b, a) > 0);
    }

    /**
     * The old ordering was not transitive, so {@code List.sort} was entitled to throw
     * "Comparison method violates its general contract!" on a large enough input. This
     * sorts a set built to exercise every tie-break, and a violation would surface as
     * an IllegalArgumentException from the sort itself.
     */
    @Test
    public void satisfiesTheComparatorContractOnAMixedSet() {
        List<KarteikarteImpl> cards = new ArrayList<>();
        String[] surnames = {"Groß", "Müller", "Özdemir", "Weiß"};
        String[] forenames = {"Anna", "Bruno", "Clara"};
        String[] dates = {"01.01.2019", "15.06.2020", "", "2018-03-09"};
        String[] addresses = {"Aaa 1", "Bbb 2"};
        for (String s : surnames) {
            for (String f : forenames) {
                for (String d : dates) {
                    for (String a : addresses) {
                        cards.add(card(s, f, d, a));
                    }
                }
            }
        }
        Collections.shuffle(cards, new java.util.Random(42));
        cards.sort(comparator); // throws if the contract is violated
        assertEquals(surnames.length * forenames.length * dates.length * addresses.length,
                cards.size());
        // spot-check that the result really is ordered
        for (int i = 1; i < cards.size(); i++) {
            assertTrue(comparator.compare(cards.get(i - 1), cards.get(i)) <= 0,
                    "list not ordered at index " + i);
        }
    }
}
