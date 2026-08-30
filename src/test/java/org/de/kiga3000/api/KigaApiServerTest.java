package org.de.kiga3000.api;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Drives the API over a real socket on an ephemeral port, with a stubbed
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

    private static final CardSource STUB = new CardSource() {
        public List<CardSummary> all() {
            return CARDS;
        }

        public Optional<CardSummary> byId(int id) {
            return CARDS.stream().filter(c -> c.id() == id).findFirst();
        }

        public List<CardSummary> byGruppe(byte gruppe) {
            return CARDS.stream().filter(c -> c.gruppe() == gruppe).toList();
        }
    };

    private KigaApiServer server;
    private HttpClient client;

    @BeforeEach
    void start() throws Exception {
        server = new KigaApiServer(0, STUB);   // 0 = ephemeral port
        server.start();
        client = HttpClient.newHttpClient();
    }

    @AfterEach
    void stop() {
        server.close();
    }

    private HttpResponse<String> get(String path) throws Exception {
        return client.send(
                HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + server.port() + path))
                        .GET().build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
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

    /** Read-only by design: writes are refused explicitly, not silently 404ed. */
    @Test
    public void writesAreRejectedWith405AndAnAllowHeader() throws Exception {
        for (String method : List.of("POST", "PUT", "DELETE", "PATCH")) {
            HttpResponse<String> r = client.send(
                    HttpRequest.newBuilder(
                            URI.create("http://127.0.0.1:" + server.port() + "/api/cards"))
                            .method(method, HttpRequest.BodyPublishers.noBody()).build(),
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(405, r.statusCode(), method + " should be refused");
            assertEquals("GET", r.headers().firstValue("allow").orElse(""),
                    method + " response should advertise Allow: GET");
            assertTrue(r.body().contains("read-only"), r.body());
        }
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
}
