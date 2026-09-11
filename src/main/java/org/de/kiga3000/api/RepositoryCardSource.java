package org.de.kiga3000.api;

import java.util.List;
import java.util.Optional;

import org.de.kiga3000.data.KarteikarteImpl;
import org.de.kiga3000.database.KarteikarteRepository;

/**
 * {@link CardSource} over the JPA repository.
 *
 * <p>Maps the entity down to the summary projection. That mapping is the only place the
 * API touches the entity, so the set of fields that can leave the process is confined
 * to one method.
 */
public final class RepositoryCardSource implements CardSource {

    @Override
    public List<CardSummary> all() {
        // Groups 1..9; the schema stores gruppe as an unsigned tinyint and the
        // application uses 9 as the retention group.
        return java.util.stream.IntStream.rangeClosed(1, 9)
                .mapToObj(g -> KarteikarteRepository.findByGruppe((byte) g))
                .flatMap(List::stream)
                .map(RepositoryCardSource::toSummary)
                .toList();
    }

    @Override
    public Optional<CardSummary> byId(int id) {
        return Optional.ofNullable(KarteikarteRepository.find(id))
                .map(RepositoryCardSource::toSummary);
    }

    @Override
    public List<CardSummary> byGruppe(byte gruppe) {
        return KarteikarteRepository.findByGruppe(gruppe).stream()
                .map(RepositoryCardSource::toSummary)
                .toList();
    }

    // ------------------------------------------------------------------------ writes

    @Override
    public CardSummary create(CardDraft draft) {
        KarteikarteImpl karte = new KarteikarteImpl();
        apply(draft, karte);
        int id = KarteikarteRepository.insert(karte);
        // Read back rather than trusting the in-memory instance: the entity's date
        // converter and the column widths both get a say in what was actually stored.
        return byId(id).orElseThrow(
                () -> new IllegalStateException("card " + id + " vanished after insert"));
    }

    @Override
    public Optional<CardSummary> replace(int id, CardDraft draft) {
        KarteikarteImpl karte = KarteikarteRepository.find(id);
        if (karte == null) {
            return Optional.empty();
        }
        // Mutating the loaded entity is what keeps the 33 unexposed columns intact; a
        // fresh instance would merge defaults over a child's health record.
        apply(draft, karte);
        KarteikarteRepository.update(karte);
        return byId(id);
    }

    @Override
    public boolean delete(int id) {
        KarteikarteImpl karte = KarteikarteRepository.find(id);
        if (karte == null) {
            return false;
        }
        // The repository takes the group as a second argument as a guard against
        // deleting a card that was moved in the meantime, so pass the one we just read.
        return KarteikarteRepository.delete(id, karte.getGruppe());
    }

    /**
     * The only place API input is written onto the entity.
     *
     * <p>The counterpart of {@link #toSummary}: together they are the whole of the
     * API's contact with the 39-column card, which is what makes the exposed and
     * writable field sets reviewable in one place.
     */
    private static void apply(CardDraft draft, KarteikarteImpl karte) {
        karte.setGruppe((byte) draft.gruppe());
        karte.setKindVorname(draft.vorname());
        karte.setKindNachname(draft.nachname());
        karte.setKindGeburtsDatum(draft.geburtsdatum());
        karte.setEintrittsDatum(draft.eintritt());
    }

    /** The only place entity fields are chosen for exposure. */
    private static CardSummary toSummary(KarteikarteImpl karte) {
        return new CardSummary(
                karte.getPrimaryKey(),
                karte.getGruppe(),
                karte.getKindVorname(),
                karte.getKindNachname(),
                karte.getKindGeburtsDatum(),
                karte.getEintrittsDatum());
    }
}
