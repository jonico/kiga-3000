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

    // ------------------------------------------------------------------------ writes

    /**
     * Creates a card from the writable projection.
     *
     * <p>Only the {@link CardDraft} fields are set; every other column keeps the
     * entity's default. The sensitive columns are therefore never populated over HTTP.
     *
     * @return the created card, including the id the database assigned
     */
    CardSummary create(CardDraft draft);

    /**
     * Replaces the writable fields of an existing card.
     *
     * <p>A replace in the {@link CardDraft} sense, not in the HTTP-PUT-replaces-the-whole
     * -resource sense: the 33 columns this API cannot see are left exactly as they are.
     * Overwriting a child's health record with defaults because it was absent from a
     * request body would be data loss dressed up as protocol correctness.
     *
     * @return the updated card, or empty when no card has that id
     */
    Optional<CardSummary> replace(int id, CardDraft draft);

    /**
     * Deletes a card.
     *
     * @return false when no card has that id
     */
    boolean delete(int id);
}
