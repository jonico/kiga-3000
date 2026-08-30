package org.de.kiga3000.api;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

/**
 * A small read-only HTTP API over the card index.
 *
 * <p>Why this exists: the application is a Swing desktop client, and Swing does not run
 * on mobile. Any path to a phone client means the data has to be reachable over HTTP
 * first, with the desktop client and the phone talking to the same service. This is
 * that first step, kept deliberately small.
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
 *   <li><b>Records (Java 16)</b> for the response projection, which makes the exposed
 *       field set obvious at a glance.</li>
 *   <li><b>Pattern matching for switch (Java 21)</b> for routing.</li>
 *   <li><b>Text blocks (Java 15)</b> for the JSON templates.</li>
 * </ul>
 *
 * <h2>Deliberate limits</h2>
 *
 * <ul>
 *   <li><b>Read-only.</b> No POST, PUT, PATCH or DELETE. Anything else returns 405.
 *       The data is about children; a write API needs an authorisation model first.</li>
 *   <li><b>Loopback only.</b> Bound to 127.0.0.1, never 0.0.0.0, so it is not reachable
 *       from the network even by accident.</li>
 *   <li><b>No authentication yet</b> - which is precisely why the response projection is
 *       narrow. See {@link CardSummary}: religion, nationality, vaccination history,
 *       illnesses, doctor, insurer and health notes are all withheld. They are
 *       special-category personal data and must not be exposed before there is an
 *       authentication story.</li>
 *   <li><b>Not started by the desktop application.</b> It has its own {@code main}, so
 *       running the Swing client does not open a socket.</li>
 * </ul>
 *
 * <h2>Endpoints</h2>
 *
 * <pre>
 * GET /api/health              {"status":"ok",...}
 * GET /api/cards               [ CardSummary, ... ]
 * GET /api/cards/{id}          CardSummary, or 404
 * GET /api/groups/{n}/cards    [ CardSummary, ... ]
 * </pre>
 */
public final class KigaApiServer implements AutoCloseable {

    private static final Logger LOGGER = Logger.getLogger(KigaApiServer.class.getName());
    private static final String JSON = "application/json; charset=utf-8";

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
    }

    public void start() {
        http.start();
        LOGGER.info(() -> "KiGa 3000 read-only API listening on http://127.0.0.1:" + port()
                + "/api (loopback only)");
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
            if (!"GET".equals(exchange.getRequestMethod())) {
                // Read-only by design, so say so rather than 404.
                exchange.getResponseHeaders().add("Allow", "GET");
                respond(exchange, 405, Json.error(405, "This API is read-only"));
                return;
            }

            String path = exchange.getRequestURI().getPath();
            // Pattern matching for switch, Java 21. Segment-based rather than regex so
            // the shape of each route is visible.
            String[] seg = path.replaceAll("^/api/?", "").split("/");
            String body = switch (seg.length) {
                case 1 -> switch (seg[0]) {
                    case "health" -> """
                            {"status":"ok","service":"kiga3000","api":"read-only"}""";
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
        } catch (NumberFormatException e) {
            respond(exchange, 400, Json.error(400, "Not a number: " + e.getMessage()));
        } catch (NotFound e) {
            respond(exchange, 404, Json.error(404, e.getMessage()));
        } catch (RuntimeException e) {
            // Never let a stack trace reach the client.
            LOGGER.warning(() -> "request failed: " + e);
            respond(exchange, 500, Json.error(500, "Internal error"));
        }
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

    private static void respond(HttpExchange exchange, int status, String body)
            throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().set("Content-Type", JSON);
        exchange.sendResponseHeaders(status, bytes.length);
        try (OutputStream out = exchange.getResponseBody()) {
            out.write(bytes);
        }
    }

    /** Signals 404 from inside a handler without an early return at every level. */
    private static final class NotFound extends RuntimeException {
        NotFound(String message) {
            super(message);
        }
    }

    // ---------------------------------------------------------------- standalone

    /**
     * Runs the API on its own. Not called by the desktop application, so starting the
     * Swing client never opens a socket.
     *
     * @param args optional port, default 8080
     */
    public static void main(String[] args) throws Exception {
        int port = args.length > 0 ? Integer.parseInt(args[0]) : 8080;
        KigaApiServer server = new KigaApiServer(port, new RepositoryCardSource());
        server.start();
        System.out.println("GET http://127.0.0.1:" + server.port() + "/api/health");
        System.out.println("GET http://127.0.0.1:" + server.port() + "/api/cards");
        System.out.println("GET http://127.0.0.1:" + server.port() + "/api/cards/1");
        System.out.println("GET http://127.0.0.1:" + server.port() + "/api/groups/1/cards");
        System.out.println("Ctrl-C to stop.");
        Thread.currentThread().join();
    }
}
