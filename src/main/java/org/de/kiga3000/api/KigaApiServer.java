package org.de.kiga3000.api;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import org.de.kiga3000.conversion.DateConversion;

/**
 * A small HTTP API over the card index, plus the web interface that drives it.
 *
 * <p>Why this exists: the application is a Swing desktop client, and Swing does not run
 * on mobile. Any path to a phone client means the data has to be reachable over HTTP
 * first, with the desktop client and the phone talking to the same service. This is
 * that step, kept deliberately small.
 *
 * <h2>What makes this only possible on the new stack</h2>
 *
 * <ul>
 *   <li><b>Virtual threads (Java 21).</b> The server uses
 *       {@code Executors.newVirtualThreadPerTaskExecutor()}, so every request gets its
 *       own thread and blocking JDBC calls inside a handler cost almost nothing. On
 *       Java 8 a thread-per-request server over a blocking database meant a bounded
 *       platform-thread pool, and the correct design would have been an asynchronous
 *       rewrite - far more than this feature is worth.</li>
 *   <li><b>Records (Java 16)</b> for the request and response projections, which makes
 *       the exposed and writable field sets obvious at a glance.</li>
 *   <li><b>Pattern matching for switch (Java 21)</b> for routing.</li>
 *   <li><b>Text blocks (Java 15)</b> for the JSON templates.</li>
 * </ul>
 *
 * <h2>Deliberate limits</h2>
 *
 * <ul>
 *   <li><b>Writes are confined to the summary projection.</b> The API reads and writes
 *       the same six fields and no others. Religion, nationality, vaccination history,
 *       illnesses, doctor, insurer, contacts, addresses and the free-text notes can
 *       neither be read nor written here - see {@link CardSummary} and
 *       {@link CardDraft}. They are special-category personal data about children under
 *       GDPR Article 9, and the desktop client, used by someone sitting in the
 *       Kindergarten, remains the only way to touch them.</li>
 *   <li><b>Unknown fields are refused, not ignored.</b> A body naming a column outside
 *       the writable projection gets a 400 that says so. Silently dropping it would
 *       leave the caller believing a health note had been stored.</li>
 *   <li><b>Loopback only.</b> Bound to 127.0.0.1, never 0.0.0.0, so neither the API nor
 *       the web interface is reachable from the network even by accident. This is what
 *       stands in for the authentication story that does not exist yet, and it is the
 *       reason the write endpoints are defensible at all.</li>
 *   <li><b>Dates are validated here.</b> {@code DateStringConverter} maps an
 *       unparseable date to SQL NULL rather than failing a flush, so without a check at
 *       this boundary a typo in a date of birth would be accepted and silently stored
 *       as "no date".</li>
 *   <li><b>Not started by the desktop application.</b> It has its own {@code main}, so
 *       running the Swing client does not open a socket.</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 *
 * <pre>
 * GET    /api/health              {"status":"ok",...}
 * GET    /api/cards               [ CardSummary, ... ]
 * GET    /api/cards/{id}          CardSummary, or 404
 * GET    /api/groups/{n}/cards    [ CardSummary, ... ]
 * POST   /api/cards               CardDraft -&gt; 201 + Location, CardSummary
 * PUT    /api/cards/{id}          CardDraft -&gt; 200 CardSummary, or 404
 * DELETE /api/cards/{id}          204, or 404
 * GET    /                        the web interface
 * </pre>
 */
public final class KigaApiServer implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(KigaApiServer.class.getName());
    private static final String JSON = "application/json; charset=utf-8";

    /** Advertised on a 405 and answered to OPTIONS. */
    private static final String ALLOWED = "GET, POST, PUT, DELETE, OPTIONS";

    /**
     * A card body is six short fields. Anything larger is a mistake or an attempt to
     * make the server allocate, and reading it into memory first would be the bug.
     */
    private static final int MAX_BODY_BYTES = 64 * 1024;

    /** Groups the application uses; 9 is the data-retention group. */
    private static final int MIN_GRUPPE = 1;
    private static final int MAX_GRUPPE = 9;

    /** The column width both name fields declare. */
    private static final int MAX_NAME = 50;

    private static final DateConversion DATES = new DateConversion();

    private final HttpServer http;
    private final CardSource source;

    /**
     * @param port 0 picks a free port, which is what the tests use
     */
    public KigaApiServer(int port, CardSource source) throws IOException {
        this.source = source;
        // Loopback explicitly: HttpServer.create(new InetSocketAddress(port), ...) would
        // bind every interface.
        this.http = HttpServer.create(
                new InetSocketAddress(InetAddress.getLoopbackAddress(), port), 0);
        // One virtual thread per request. This is the Java 21 part: the handlers below
        // block on JDBC, and with platform threads that would need a bounded pool and
        // careful sizing.
        this.http.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        this.http.createContext("/api", this::route);
        // Longest-prefix wins, so /api never reaches the asset handler.
        this.http.createContext("/", this::serveWeb);
    }

    public void start() {
        http.start();
        LOGGER.info(() -> "KiGa 3000 API and web interface on http://127.0.0.1:" + port()
                + " (loopback only)");
    }

    public int port() {
        return http.getAddress().getPort();
    }

    @Override
    public void close() {
        http.stop(0);
    }

    // ------------------------------------------------------------------- routing

    private void route(HttpExchange exchange) throws IOException {
        try {
            String path = exchange.getRequestURI().getPath();
            // Segment-based rather than regex so the shape of each route is visible.
            String[] seg = path.replaceAll("^/api/?", "").split("/");

            switch (exchange.getRequestMethod()) {
                case "GET" -> get(exchange, path, seg);
                case "POST" -> post(exchange, path, seg);
                case "PUT" -> put(exchange, path, seg);
                case "DELETE" -> delete(exchange, path, seg);
                case "OPTIONS" -> {
                    exchange.getResponseHeaders().add("Allow", ALLOWED);
                    respondEmpty(exchange, 204);
                }
                default -> {
                    exchange.getResponseHeaders().add("Allow", ALLOWED);
                    respond(exchange, 405, Json.error(405,
                            exchange.getRequestMethod() + " is not supported"));
                }
            }
        } catch (NumberFormatException e) {
            respond(exchange, 400, Json.error(400, "Not a number: " + e.getMessage()));
        } catch (Json.Malformed e) {
            respond(exchange, 400, Json.error(400, "Malformed JSON: " + e.getMessage()));
        } catch (BadRequest e) {
            respond(exchange, 400, Json.error(400, e.getMessage()));
        } catch (NotFound e) {
            respond(exchange, 404, Json.error(404, e.getMessage()));
        } catch (RuntimeException e) {
            // Never let a stack trace reach the client.
            LOGGER.warning(() -> "request failed: " + e);
            respond(exchange, 500, Json.error(500, "Internal error"));
        }
    }

    private void get(HttpExchange exchange, String path, String[] seg) throws IOException {
        // Pattern matching for switch, Java 21.
        String body = switch (seg.length) {
            case 1 -> switch (seg[0]) {
                case "health" -> """
                        {"status":"ok","service":"kiga3000","api":"read-write"}""";
                case "cards" -> Json.cards(source.all());
                default -> null;
            };
            case 2 -> "cards".equals(seg[0]) ? cardById(seg[1]) : null;
            case 3 -> "groups".equals(seg[0]) && "cards".equals(seg[2])
                    ? cardsByGruppe(seg[1]) : null;
            default -> null;
        };

        if (body == null) {
            respond(exchange, 404, Json.error(404, "No such resource: " + path));
        } else {
            respond(exchange, 200, body);
        }
    }

    private void post(HttpExchange exchange, String path, String[] seg) throws IOException {
        if (seg.length != 1 || !"cards".equals(seg[0])) {
            respond(exchange, 404, Json.error(404, "Cannot create at " + path));
            return;
        }
        CardSummary created = source.create(draft(readBody(exchange)));
        exchange.getResponseHeaders().add("Location", "/api/cards/" + created.id());
        respond(exchange, 201, Json.card(created));
    }

    private void put(HttpExchange exchange, String path, String[] seg) throws IOException {
        if (seg.length != 2 || !"cards".equals(seg[0])) {
            respond(exchange, 404, Json.error(404, "Cannot replace at " + path));
            return;
        }
        int id = Integer.parseInt(seg[1]);
        Optional<CardSummary> updated = source.replace(id, draft(readBody(exchange)));
        if (updated.isEmpty()) {
            throw new NotFound("No card with id " + id);
        }
        respond(exchange, 200, Json.card(updated.get()));
    }

    private void delete(HttpExchange exchange, String path, String[] seg) throws IOException {
        if (seg.length != 2 || !"cards".equals(seg[0])) {
            respond(exchange, 404, Json.error(404, "Cannot delete at " + path));
            return;
        }
        int id = Integer.parseInt(seg[1]);
        if (!source.delete(id)) {
            throw new NotFound("No card with id " + id);
        }
        respondEmpty(exchange, 204);
    }

    private String cardById(String raw) {
        Optional<CardSummary> found = source.byId(Integer.parseInt(raw));
        return Json.card(found.orElseThrow(() -> new NotFound("No card with id " + raw)));
    }

    private String cardsByGruppe(String raw) {
        int gruppe = Integer.parseInt(raw);
        if (gruppe < 0 || gruppe > 255) {
            throw new NotFound("No such group: " + raw);
        }
        List<CardSummary> cards = source.byGruppe((byte) gruppe);
        return Json.cards(cards);
    }

    // ---------------------------------------------------------------- request bodies

    private static String readBody(HttpExchange exchange) throws IOException {
        try (InputStream in = exchange.getRequestBody()) {
            byte[] bytes = in.readNBytes(MAX_BODY_BYTES + 1);
            if (bytes.length > MAX_BODY_BYTES) {
                throw new BadRequest("Request body larger than " + MAX_BODY_BYTES + " bytes");
            }
            return new String(bytes, StandardCharsets.UTF_8);
        }
    }

    /**
     * Turns a request body into a validated {@link CardDraft}.
     *
     * <p>Unknown field names are refused rather than dropped. That is the guardrail
     * that matters: a caller who sends {@code "religion"} must be told the field is not
     * writable here, because the alternative is that they believe it was stored.
     */
    private static CardDraft draft(String body) {
        Map<String, Object> fields = Json.object(body);

        for (String name : fields.keySet()) {
            switch (name) {
                case "gruppe", "vorname", "nachname", "geburtsdatum", "eintritt" -> {
                    // writable
                }
                case "id" -> throw new BadRequest(
                        "id is assigned by the server on create and taken from the URL"
                                + " on replace; remove it from the body");
                default -> throw new BadRequest("Field not writable through this API: "
                        + name + ". Only gruppe, vorname, nachname, geburtsdatum and"
                        + " eintritt can be written; the rest of the card is"
                        + " special-category personal data and is desktop-only");
            }
        }

        int gruppe = requiredInt(fields, "gruppe");
        if (gruppe < MIN_GRUPPE || gruppe > MAX_GRUPPE) {
            throw new BadRequest("gruppe must be between " + MIN_GRUPPE + " and "
                    + MAX_GRUPPE + ", was " + gruppe);
        }

        return new CardDraft(
                gruppe,
                requiredName(fields, "vorname"),
                requiredName(fields, "nachname"),
                optionalDate(fields, "geburtsdatum"),
                optionalDate(fields, "eintritt"));
    }

    private static int requiredInt(Map<String, Object> fields, String name) {
        Object value = fields.get(name);
        if (value == null) {
            throw new BadRequest(name + " is required");
        }
        if (!(value instanceof Long number)) {
            throw new BadRequest(name + " must be a whole number, was: " + value);
        }
        return number.intValue();
    }

    private static String requiredName(Map<String, Object> fields, String name) {
        Object value = fields.get(name);
        if (!(value instanceof String text) || text.isBlank()) {
            throw new BadRequest(name + " is required and must be a non-empty string");
        }
        String trimmed = text.trim();
        if (trimmed.length() > MAX_NAME) {
            // Checked here so the caller gets a 400 rather than a data-truncation error
            // from the driver, which is how the sorgeperson bug used to surface.
            throw new BadRequest(name + " must be at most " + MAX_NAME
                    + " characters, was " + trimmed.length());
        }
        return trimmed;
    }

    /**
     * A date normalised to German display format, or the empty string for "no date".
     *
     * <p>Validated rather than passed through, because the entity's converter maps an
     * unparseable date to SQL NULL instead of failing a flush: an unchecked typo in a
     * date of birth would be accepted and silently stored as no date at all.
     *
     * <p><b>Normalised</b> rather than merely checked, which is the less obvious half.
     * {@link DateConversion} accepts both {@code dd.MM.yyyy} and the MySQL
     * {@code uuuu-MM-dd} form, so an ISO date from a caller parses happily - but the
     * entity's contract is that its date attributes are always in display format, and
     * storing the ISO string verbatim would quietly break it. Converting here means
     * either input is accepted and exactly one form is ever stored.
     */
    private static String optionalDate(Map<String, Object> fields, String name) {
        Object value = fields.get(name);
        if (value == null) {
            return "";
        }
        if (!(value instanceof String text)) {
            throw new BadRequest(name + " must be a string in dd.MM.yyyy format");
        }
        String trimmed = text.trim();
        if (trimmed.isEmpty()) {
            return "";
        }
        try {
            return DATES.StringToLocalDate(trimmed);
        } catch (ParseException e) {
            throw new BadRequest(name + " must be a real date in dd.MM.yyyy format,"
                    + " was: " + trimmed);
        }
    }

    // ------------------------------------------------------------- web interface

    /**
     * Serves the web interface from the classpath.
     *
     * <p>Packaged under {@code /web} in the jar, for the same reason the title page had
     * to move there: a filesystem path cannot reach a resource inside a jar, and the
     * 2006 code's habit of concatenating a configuration key with a relative path is
     * exactly how the title picture stayed invisible for two decades.
     */
    private void serveWeb(HttpExchange exchange) throws IOException {
        if (!"GET".equals(exchange.getRequestMethod())) {
            exchange.getResponseHeaders().add("Allow", "GET");
            respond(exchange, 405, Json.error(405, "The web interface is served over GET"));
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String asset = "/".equals(path) ? "/index.html" : path;

        // No traversal out of /web. The check is on the request, before it becomes a
        // resource name, so there is nothing to normalise away afterwards.
        if (asset.contains("..") || asset.contains("//")) {
            respond(exchange, 400, Json.error(400, "Bad asset path"));
            return;
        }

        byte[] bytes;
        try (InputStream in = KigaApiServer.class.getResourceAsStream("/web" + asset)) {
            if (in == null) {
                respond(exchange, 404, Json.error(404, "No such asset: " + path));
                return;
            }
            bytes = in.readAllBytes();
        }

        exchange.getResponseHeaders().set("Content-Type", contentType(asset));
        exchange.sendResponseHeaders(200, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    private static String contentType(String asset) {
        int dot = asset.lastIndexOf('.');
        String extension = dot < 0 ? "" : asset.substring(dot + 1);
        return switch (extension) {
            case "html" -> "text/html; charset=utf-8";
            case "css" -> "text/css; charset=utf-8";
            case "js" -> "text/javascript; charset=utf-8";
            case "svg" -> "image/svg+xml";
            case "json" -> JSON;
            default -> "application/octet-stream";
        };
    }

    // ------------------------------------------------------------------- responses

    private static void respond(HttpExchange exchange, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", JSON);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    /** A response with no body at all, which 204 requires. */
    private static void respondEmpty(HttpExchange exchange, int status) throws IOException {
        exchange.sendResponseHeaders(status, -1);
        exchange.close();
    }

    /** Signals 404 from inside a handler without an early return at every level. */
    private static final class NotFound extends RuntimeException {
        NotFound(String message) {
            super(message);
        }
    }

    /** Signals 400 for input this API will not accept. */
    private static final class BadRequest extends RuntimeException {
        BadRequest(String message) {
            super(message);
        }
    }

    // ---------------------------------------------------------------- standalone

    /**
     * Runs the API and the web interface on their own. Not called by the desktop
     * application, so starting the Swing client never opens a socket.
     *
     * @param args optional port, default 8080
     */
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        KigaApiServer server = new KigaApiServer(port, new RepositoryCardSource());
        server.start();
        String base = "http://127.0.0.1:" + server.port();
        System.out.println("Web interface : " + base + "/");
        System.out.println("GET    " + base + "/api/health");
        System.out.println("GET    " + base + "/api/cards");
        System.out.println("GET    " + base + "/api/cards/1");
        System.out.println("GET    " + base + "/api/groups/1/cards");
        System.out.println("POST   " + base + "/api/cards");
        System.out.println("PUT    " + base + "/api/cards/1");
        System.out.println("DELETE " + base + "/api/cards/1");
        System.out.println("Ctrl-C to stop.");
        Thread.currentThread().join();
    }
}
