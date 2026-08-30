package org.de.kiga3000.api;

import java.util.List;
import java.util.Optional;

/**
 * Where the API gets its data.
 *
 * <p>This seam exists so the HTTP layer can be tested without a database. The real
 * implementation is {@link RepositoryCardSource}, over the JPA repository; tests supply
 * a fixed list. Without it, every API test would need a live MySQL, which is exactly
 * the trap the original codebase fell into - almost nothing in it could be tested in
 * isolation.
 */
public interface CardSource {

    /** Every card, in the order the repository returns them. */
    List<CardSummary> all();

    /** One card, or empty when no card has that id. */
    Optional<CardSummary> byId(int id);

    /** Cards in one Kindergarten group. */
    List<CardSummary> byGruppe(byte gruppe);
}
