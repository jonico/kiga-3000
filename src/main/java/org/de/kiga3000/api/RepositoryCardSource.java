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
