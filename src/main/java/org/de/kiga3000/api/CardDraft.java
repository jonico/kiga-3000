package org.de.kiga3000.api;

/**
 * What the API accepts when a card is created or replaced.
 *
 * <p>The mirror image of {@link CardSummary}, and narrow for the same reason. The table
 * has 39 columns, including religion, nationality, vaccination history, illnesses,
 * doctor, health insurer and free-text health notes - special-category personal data
 * about children under GDPR Article 9. None of them can be <em>read</em> through this
 * API, and this record is what stops them being <em>written</em> through it either.
 *
 * <p>That symmetry is deliberate. A write API whose input type was the whole entity
 * would let an unauthenticated caller fill in a child's health record even though it
 * could never read one back, which is worse rather than better. Because the writable
 * surface is exactly the readable surface, the projection remains the protection while
 * there is no authentication story.
 *
 * <p>No {@code id}: it is assigned by the database on create, and taken from the URL on
 * replace. Accepting one in the body would invite a caller to disagree with the path.
 *
 * <p>Fields a create leaves untouched keep the entity's own defaults (empty string for
 * text, 0 for the numeric ones); a replace leaves whatever is already stored alone. So
 * the sensitive columns are only ever written through the desktop client, by someone
 * sitting in the Kindergarten.
 */
public record CardDraft(
        int gruppe,
        String vorname,
        String nachname,
        String geburtsdatum,
        String eintritt) {
}
