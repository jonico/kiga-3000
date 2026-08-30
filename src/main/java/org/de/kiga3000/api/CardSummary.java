package org.de.kiga3000.api;

/**
 * What the API exposes about a card.
 *
 * <p>Deliberately a SUMMARY, not the whole record. The table has 39 columns including
 * religion, nationality, vaccination history, illnesses, doctor, health insurer and
 * free-text health notes - special-category personal data about children under GDPR
 * Article 9. None of that is exposed here.
 *
 * <p>There is no authentication on this endpoint yet, so the projection is the
 * protection: the fields below are the ones a Kindergarten group list would print on
 * paper anyway. Widening it is a decision that has to come after an authentication and
 * authorisation story, not before.
 *
 * <p>A record rather than a class: it is a value, it needs no identity, and the compact
 * form makes the exposed surface obvious at a glance - which is exactly the property
 * you want in the type that decides what leaves the building. Records arrived in
 * Java 16, so this could not have been written on the original stack.
 */
public record CardSummary(
        int id,
        int gruppe,
        String vorname,
        String nachname,
        String geburtsdatum,
        String eintritt) {
}
