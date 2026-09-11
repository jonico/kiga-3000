package org.de.kiga3000.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Drives the API over a real socket on an ephemeral port, against an in-memory
 * {@link CardSource} so no database is needed.
 *
 * <p>That seam is the point. Almost nothing in the original codebase could be tested
 * without a live MySQL and a display, which is how it shipped with two assertion-free
 * tests.
 */
public class KigaApiServerTest {

    /** Fixed data, including the characters that break naive JSON writers. */
    private static final List<CardSummary> CARDS = List.of(
            new CardSummary(1, 1, "Lena", "Müller", "12.04.2019", "01.09.2022"),
            new CardSummary(2, 1, "Elias", "Roßberg", "08.02.2019", "01.09.2022"),
            new CardSummary(3, 2, "Yusuf", "Özdemir", "17.06.2020", "01.09.2023"),
            // a name containing a quote and a backslash, to prove escaping
            new CardSummary(4, 2, "Quote\"Test", "Back\\slash", "", ""));

    /**
     * A mutable stand-in for the repository.
     *
     * <p>Deliberately keeps only the six summary fields, which is the whole point of the
     * projection: there is no place in this stub to put a health note, so a test cannot
     * accidentally prove that writing one works.
     */
    private static final class InMemoryCards implements CardSource {

        private final List<CardSummary> cards = new ArrayList<>(CARDS);
        private int nextId = 100;

        @Override
        public List<CardSummary> all() {
            return List.copyOf(cards);
        }

        @Override
        public Optional<CardSummary> byId(int id) {
            return cards.stream().filter(c -> c.id() == id).findFirst();
        }

        @Override
        public List<CardSummary> byGruppe(byte gruppe) {
            return cards.stream().filter(c -> c.gruppe() == gruppe).toList();
        }

        @Override
        public CardSummary create(CardDraft draft) {
            CardSummary created = new CardSummary(nextId++, draft.gruppe(),
                    draft.vorname(), draft.nachname(),
                    draft.geburtsdatum(), draft.eintritt());
            cards.add(created);
            return created;
        }

        @Override
        public Optional<CardSummary> replace(int id, CardDraft draft) {
            return byId(id).map(existing -> {
                CardSummary updated = new CardSummary(id, draft.gruppe(),
                        draft.vorname(), draft.nachname(),
                        draft.geburtsdatum(), draft.eintritt());
                cards.set(cards.indexOf(existing), updated);
                return updated;
            });
        }

        @Override
        public boolean delete(int id) {
            return cards.removeIf(c -> c.id() == id);
        }
    }

    private KigaApiServer server;
    private HttpClient client;

    @BeforeEach
    void start() throws Exception {
        server = new KigaApiServer(0, new InMemoryCards());   // 0 = ephemeral port
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void stop() {
        server.close();
    }

    // ---------------------------------------------------------------------- helpers

    private URI uri(String path) {
        return URI.create("http://127.0.0.1:" + server.port() + path);
    }

    private HttpResponse<String> send(HttpRequest request) throws Exception {
        return client.send(request,
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
    }

    private HttpResponse<String> get(String path) throws Exception {
        return send(HttpRequest.newBuilder(uri(path)).GET().build());
    }

    private HttpResponse<String> body(String method, String path, String json)
            throws Exception {
        return send(HttpRequest.newBuilder(uri(path))
                .header("Content-Type", "application/json")
                .method(method, HttpRequest.BodyPublishers.ofString(json,
                        StandardCharsets.UTF_8))
                .build());
    }

    private HttpResponse<String> delete(String path) throws Exception {
        return send(HttpRequest.newBuilder(uri(path)).DELETE().build());
    }

    // ------------------------------------------------------------------- happy path

    @Test
    public void healthReportsOk() throws Exception {
        HttpResponse<String> r = get("/api/health");
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("\"status\":\"ok\""), r.body());
        assertEquals("application/json; charset=utf-8",
                r.headers().firstValue("content-type").orElse(""));
    }

    @Test
    public void listsAllCards() throws Exception {
        HttpResponse<String> r = get("/api/cards");
        assertEquals(200, r.statusCode());
        assertTrue(r.body().startsWith("["), r.body());
        for (CardSummary c : CARDS) {
            assertTrue(r.body().contains("\"id\":" + c.id()), "missing id " + c.id());
        }
    }

    @Test
    public void fetchesOneCard() throws Exception {
        HttpResponse<String> r = get("/api/cards/2");
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("\"nachname\":\"Roßberg\""), r.body());
        assertTrue(r.body().startsWith("{"), "a single card is an object, not an array");
    }

    @Test
    public void filtersByGroup() throws Exception {
        HttpResponse<String> r = get("/api/groups/2/cards");
        assertEquals(200, r.statusCode());
        assertTrue(r.body().contains("Özdemir"), r.body());
        assertFalse(r.body().contains("Müller"), "group 1 must not appear in group 2");
    }

    /** German characters must survive the round trip as UTF-8. */
    @Test
    public void umlautsSurvive() throws Exception {
        String body = get("/api/cards").body();
        for (String expected : List.of("Müller", "Roßberg", "Özdemir")) {
            assertTrue(body.contains(expected), "lost " + expected + " in: " + body);
        }
    }

    /** A quote or backslash in a name would otherwise produce malformed JSON. */
    @Test
    public void quotesAndBackslashesAreEscaped() throws Exception {
        String body = get("/api/cards/4").body();
        assertTrue(body.contains("Quote\\\"Test"), body);
        assertTrue(body.contains("Back\\\\slash"), body);
    }

    // ------------------------------------------------------------------- failures

    @Test
    public void unknownCardIs404() throws Exception {
        HttpResponse<String> r = get("/api/cards/9999");
        assertEquals(404, r.statusCode());
        assertTrue(r.body().contains("\"status\":404"), r.body());
    }

    @Test
    public void unknownPathIs404() throws Exception {
        assertEquals(404, get("/api/nope").statusCode());
        assertEquals(404, get("/api/cards/1/extra/bits").statusCode());
    }

    @Test
    public void nonNumericIdIs400() throws Exception {
        HttpResponse<String> r = get("/api/cards/abc");
        assertEquals(400, r.statusCode());
    }

    // ----------------------------------------------------------------------- writes

    @Test
    public void createsACardAndReportsWhereItWent() throws Exception {
        HttpResponse<String> r = body("POST", "/api/cards", """
                {"gruppe":3,"vorname":"Mira","nachname":"Bergmann",
                 "geburtsdatum":"14.03.2020","eintritt":"01.09.2023"}""");

        assertEquals(201, r.statusCode(), r.body());
        String location = r.headers().firstValue("location").orElse("");
        assertTrue(location.startsWith("/api/cards/"), "no Location header: " + location);
        assertTrue(r.body().contains("\"nachname\":\"Bergmann\""), r.body());

        // The Location header has to actually resolve.
        assertEquals(200, get(location).statusCode());
    }

    @Test
    public void replacesTheWritableFields() throws Exception {
        HttpResponse<String> r = body("PUT", "/api/cards/1", """
                {"gruppe":4,"vorname":"Lena","nachname":"Müller-Roßberg",
                 "geburtsdatum":"12.04.2019","eintritt":"01.09.2022"}""");

        assertEquals(200, r.statusCode(), r.body());
        assertTrue(r.body().contains("Müller-Roßberg"), r.body());
        assertTrue(get("/api/cards/1").body().contains("\"gruppe\":4"),
                "the change should be visible on a subsequent read");
    }

    @Test
    public void deletesACard() throws Exception {
        HttpResponse<String> r = delete("/api/cards/3");
        assertEquals(204, r.statusCode());
        assertTrue(r.body().isEmpty(), "204 must not carry a body");
        assertEquals(404, get("/api/cards/3").statusCode(), "should be gone");
    }

    @Test
    public void writingAnUnknownCardIs404() throws Exception {
        assertEquals(404, body("PUT", "/api/cards/9999", """
                {"gruppe":1,"vorname":"Nobody","nachname":"Here"}""").statusCode());
        assertEquals(404, delete("/api/cards/9999").statusCode());
    }

    /** Umlauts have to survive being written, not only being read. */
    @Test
    public void writtenUmlautsComeBackIntact() throws Exception {
        HttpResponse<String> created = body("POST", "/api/cards", """
                {"gruppe":2,"vorname":"Jörg","nachname":"Vogtländer",
                 "geburtsdatum":"06.05.2018","eintritt":"01.09.2021"}""");
        assertEquals(201, created.statusCode(), created.body());

        String location = created.headers().firstValue("location").orElseThrow();
        String reread = get(location).body();
        assertTrue(reread.contains("Jörg"), reread);
        assertTrue(reread.contains("Vogtländer"), reread);
    }

    /** A quote and a backslash have to survive the parser as well as the writer. */
    @Test
    public void writtenQuotesAndBackslashesRoundTrip() throws Exception {
        HttpResponse<String> created = body("POST", "/api/cards",
                "{\"gruppe\":1,\"vorname\":\"Quote\\\"Me\","
                        + "\"nachname\":\"Back\\\\slash\"}");
        assertEquals(201, created.statusCode(), created.body());

        String location = created.headers().firstValue("location").orElseThrow();
        String reread = get(location).body();
        assertTrue(reread.contains("Quote\\\"Me"), reread);
        assertTrue(reread.contains("Back\\\\slash"), reread);
    }

    // -------------------------------------------------------------- write validation

    /**
     * The guardrail that replaces "the API is read-only".
     *
     * <p>Writes exist now, so the protection can no longer be "no writes". It is that
     * the writable field set equals the readable field set. A caller naming a
     * special-category column must be refused loudly - silently dropping it would leave
     * them believing a child's health note had been stored.
     */
    @Test
    public void sensitiveFieldsCannotBeWritten() throws Exception {
        for (String field : List.of("religion", "gesundheit", "impfung", "krankheiten",
                "arztort", "krankenkasse", "staat", "sonstiges", "sorgeperson",
                "vatername", "muttername", "kindwohnung", "kindtelefon")) {
            String json = """
                    {"gruppe":1,"vorname":"Test","nachname":"Kind","%s":"x"}"""
                    .formatted(field);

            HttpResponse<String> r = body("POST", "/api/cards", json);
            assertEquals(400, r.statusCode(),
                    field + " must be refused, not accepted: " + r.body());
            assertTrue(r.body().contains("not writable"),
                    "the refusal should say why, for " + field + ": " + r.body());
        }
    }

    @Test
    public void idInTheBodyIsRefused() throws Exception {
        HttpResponse<String> r = body("POST", "/api/cards", """
                {"id":77,"gruppe":1,"vorname":"Test","nachname":"Kind"}""");
        assertEquals(400, r.statusCode(), r.body());
        assertTrue(r.body().contains("id"), r.body());
    }

    /**
     * An impossible date must be refused here, because DateStringConverter maps an
     * unparseable date to SQL NULL rather than failing - so without this check a typo
     * would be accepted and silently stored as "no date".
     */
    @Test
    public void impossibleDatesAreRefused() throws Exception {
        for (String bad : List.of("30.02.2020", "11.3.2020", "2020-3-1", "nonsense",
                "12.04.19")) {
            HttpResponse<String> r = body("POST", "/api/cards", """
                    {"gruppe":1,"vorname":"Test","nachname":"Kind","geburtsdatum":"%s"}"""
                    .formatted(bad));
            assertEquals(400, r.statusCode(), bad + " should be refused: " + r.body());
        }
    }

    /**
     * An ISO date is accepted but stored in display format.
     *
     * <p>{@link org.de.kiga3000.conversion.DateConversion} parses both forms, so
     * refusing ISO would be gratuitous - but the entity's contract is that its date
     * attributes are always {@code dd.MM.yyyy}, and passing the ISO string through
     * verbatim would quietly break it.
     */
    @Test
    public void isoDatesAreNormalisedToDisplayFormat() throws Exception {
        HttpResponse<String> r = body("POST", "/api/cards", """
                {"gruppe":1,"vorname":"Test","nachname":"Kind",
                 "geburtsdatum":"2020-03-11","eintritt":"01.09.2023"}""");
        assertEquals(201, r.statusCode(), r.body());
        assertTrue(r.body().contains("\"geburtsdatum\":\"11.03.2020\""),
                "ISO input should come back as dd.MM.yyyy: " + r.body());
    }

    @Test
    public void validDatesAreAccepted() throws Exception {
        HttpResponse<String> r = body("POST", "/api/cards", """
                {"gruppe":1,"vorname":"Test","nachname":"Kind",
                 "geburtsdatum":"29.02.2020","eintritt":""}""");
        assertEquals(201, r.statusCode(), "29.02.2020 is a real date: " + r.body());
    }

    @Test
    public void groupOutsideTheUsedRangeIsRefused() throws Exception {
        for (String gruppe : List.of("0", "10", "255")) {
            HttpResponse<String> r = body("POST", "/api/cards", """
                    {"gruppe":%s,"vorname":"Test","nachname":"Kind"}"""
                    .formatted(gruppe));
            assertEquals(400, r.statusCode(), "gruppe " + gruppe + ": " + r.body());
        }
    }

    @Test
    public void namesAreRequiredAndBounded() throws Exception {
        assertEquals(400, body("POST", "/api/cards", """
                {"gruppe":1,"nachname":"Kind"}""").statusCode(), "vorname missing");
        assertEquals(400, body("POST", "/api/cards", """
                {"gruppe":1,"vorname":"  ","nachname":"Kind"}""").statusCode(),
                "blank vorname");

        // 51 characters, against a varchar(50): a 400 here rather than a driver-level
        // data-truncation error is the whole point.
        HttpResponse<String> tooLong = body("POST", "/api/cards", """
                {"gruppe":1,"vorname":"%s","nachname":"Kind"}"""
                .formatted("a".repeat(51)));
        assertEquals(400, tooLong.statusCode(), tooLong.body());
    }

    @Test
    public void malformedBodiesAreRefused() throws Exception {
        for (String bad : List.of("", "not json", "{", "{\"gruppe\":1,",
                // nesting is refused outright: a card is flat
                "{\"gruppe\":1,\"vorname\":{\"a\":1},\"nachname\":\"K\"}",
                "[]",
                // a duplicate field would make the stored value depend on ordering
                "{\"gruppe\":1,\"gruppe\":2,\"vorname\":\"A\",\"nachname\":\"B\"}")) {
            HttpResponse<String> r = body("POST", "/api/cards", bad);
            assertEquals(400, r.statusCode(), "should refuse: <" + bad + "> " + r.body());
        }
    }

    @Test
    public void gruppeMustBeANumberNotAString() throws Exception {
        HttpResponse<String> r = body("POST", "/api/cards", """
                {"gruppe":"1","vorname":"Test","nachname":"Kind"}""");
        assertEquals(400, r.statusCode(), r.body());
    }

    @Test
    public void writesToTheWrongPathAre404() throws Exception {
        assertEquals(404, body("POST", "/api/health", "{}").statusCode());
        assertEquals(404, body("POST", "/api/cards/1", "{}").statusCode());
        assertEquals(404, body("PUT", "/api/cards", "{}").statusCode());
        assertEquals(404, delete("/api/cards").statusCode());
    }

    // ------------------------------------------------------------- method handling

    /**
     * The supported set is advertised rather than guessed at. PATCH is genuinely not
     * supported: a partial update of a five-field projection buys nothing, and the
     * ambiguity over whether an absent field means "unchanged" or "clear it" is exactly
     * the kind of thing that quietly erases a date of birth.
     */
    @Test
    public void unsupportedMethodsAreRejectedWith405AndAnAllowHeader() throws Exception {
        for (String method : List.of("PATCH", "TRACE")) {
            HttpResponse<String> r = send(HttpRequest.newBuilder(uri("/api/cards"))
                    .method(method, HttpRequest.BodyPublishers.noBody()).build());

            assertEquals(405, r.statusCode(), method + " should be refused");
            String allow = r.headers().firstValue("allow").orElse("");
            for (String supported : List.of("GET", "POST", "PUT", "DELETE")) {
                assertTrue(allow.contains(supported),
                        method + " response should advertise " + supported
                                + ", was: " + allow);
            }
        }
    }

    @Test
    public void optionsAdvertisesTheSupportedMethods() throws Exception {
        HttpResponse<String> r = send(HttpRequest.newBuilder(uri("/api/cards"))
                .method("OPTIONS", HttpRequest.BodyPublishers.noBody()).build());
        assertEquals(204, r.statusCode());
        assertTrue(r.headers().firstValue("allow").orElse("").contains("PUT"),
                r.headers().map().toString());
    }

    // ------------------------------------------------------------- web interface

    @Test
    public void servesTheWebInterface() throws Exception {
        HttpResponse<String> r = get("/");
        assertEquals(200, r.statusCode());
        assertTrue(r.headers().firstValue("content-type").orElse("").startsWith("text/html"),
                r.headers().map().toString());
        assertTrue(r.body().contains("KiGa 3000"), "not the index page");
        assertTrue(r.body().contains("app.js"), "index should load the client");
    }

    @Test
    public void servesTheWebAssets() throws Exception {
        assertEquals(200, get("/app.js").statusCode());
        assertEquals(200, get("/style.css").statusCode());
        assertTrue(get("/style.css").headers().firstValue("content-type")
                .orElse("").startsWith("text/css"));
    }

    /**
     * The web interface has to say what it does not show. Someone looking at a form
     * with five fields would otherwise assume the rest of the card was merely missing
     * rather than deliberately withheld.
     */
    @Test
    public void theWebInterfaceDeclaresWhatItWithholds() throws Exception {
        String html = get("/").body();
        assertTrue(html.contains("DSGVO"), "the GDPR note should be in the page");
        assertTrue(html.contains("127.0.0.1"), "the loopback limit should be stated");
    }

    @Test
    public void unknownAssetsAre404() throws Exception {
        assertEquals(404, get("/nope.js").statusCode());
        assertEquals(404, get("/deeper/nope.css").statusCode());
    }

    /** Path traversal must not escape the packaged /web directory. */
    @Test
    public void assetTraversalCannotEscape() throws Exception {
        // Sent raw so the client does not normalise the dot segments away.
        HttpResponse<String> r = send(HttpRequest.newBuilder(
                URI.create("http://127.0.0.1:" + server.port()
                        + "/..%2f..%2fpom.xml")).GET().build());
        assertTrue(r.statusCode() == 400 || r.statusCode() == 404,
                "traversal should not succeed, was " + r.statusCode());
        assertFalse(r.body().contains("<artifactId>"), "leaked the pom");
    }

    @Test
    public void theWebInterfaceIsGetOnly() throws Exception {
        HttpResponse<String> r = body("POST", "/index.html", "{}");
        assertEquals(405, r.statusCode());
        assertEquals("GET", r.headers().firstValue("allow").orElse(""));
    }

    // ------------------------------------------------------ the Java 21 part

    /**
     * Every request is served on its own virtual thread, so many concurrent requests
     * blocking in a handler cost almost nothing. This fires 200 at once, which would
     * need a carefully sized platform-thread pool on the original stack.
     */
    @Test
    public void servesManyConcurrentRequests() throws Exception {
        int requests = 200;
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            List<java.util.concurrent.Future<Integer>> futures =
                    java.util.stream.IntStream.range(0, requests)
                            .mapToObj(i -> executor.submit(() -> get("/api/cards").statusCode()))
                            .toList();
            for (var f : futures) {
                assertEquals(200, f.get().intValue());
            }
        }
    }

    /**
     * The projection is the protection while there is no authentication. If a field is
     * ever added to CardSummary, this test should be the thing that makes someone stop
     * and think.
     */
    @Test
    public void sensitiveFieldsAreNotExposed() throws Exception {
        String body = get("/api/cards").body();
        for (String field : List.of("religion", "gesundheit", "impfung", "krankheit",
                "arzt", "krankenkasse", "staat", "sonstiges", "sorgeperson",
                "vatername", "muttername", "kindwohnung", "telefon")) {
            assertFalse(body.toLowerCase().contains(field),
                    "the summary projection must not expose " + field
                            + " - it is special-category personal data about a child,"
                            + " and this API has no authentication yet");
        }
    }

    /** The record that decides what leaves the building must stay six fields wide. */
    @Test
    public void theWritableProjectionMatchesTheReadableOne() {
        assertNotNull(CardDraft.class.getRecordComponents());
        List<String> readable = java.util.Arrays.stream(
                        CardSummary.class.getRecordComponents())
                .map(java.lang.reflect.RecordComponent::getName)
                .filter(name -> !"id".equals(name))
                .toList();
        List<String> writable = java.util.Arrays.stream(
                        CardDraft.class.getRecordComponents())
                .map(java.lang.reflect.RecordComponent::getName)
                .toList();
        assertEquals(readable, writable,
                "the writable and readable field sets must stay identical; widening one"
                        + " without the other is how an unauthenticated caller ends up"
                        + " able to write a field it cannot read");
    }
}
