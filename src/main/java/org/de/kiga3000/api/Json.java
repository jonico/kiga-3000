package org.de.kiga3000.api;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Just enough JSON to serialise {@link CardSummary} and to read a {@link CardDraft}.
 *
 * <p>No JSON library. Adding Jackson to this application would pull a dependency tree
 * larger than the application, for six fields of two types.
 *
 * <p>The reader added for the write endpoints is deliberately stricter than a general
 * JSON parser: it accepts a <em>flat</em> object of strings, numbers, booleans and
 * nulls, and rejects nested objects and arrays outright. That is not a shortcut, it is
 * the point. {@link CardDraft} has no nested structure, so anything nested in a request
 * body is either a mistake or an attempt to reach a field this API does not expose, and
 * a parser that cannot represent it cannot be talked into accepting it. Writing a
 * general parser here would add surface without adding capability.
 */
final class Json {

    private Json() {
    }

    static String card(CardSummary c) {
        return """
                {"id":%d,"gruppe":%d,"vorname":%s,"nachname":%s,"geburtsdatum":%s,"eintritt":%s}"""
                .formatted(c.id(), c.gruppe(),
                        string(c.vorname()), string(c.nachname()),
                        string(c.geburtsdatum()), string(c.eintritt()));
    }

    static String cards(List<CardSummary> cards) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < cards.size(); i++) {
            if (i > 0) {
                sb.append(',');
            }
            sb.append(card(cards.get(i)));
        }
        return sb.append(']').toString();
    }

    static String error(int status, String message) {
        return """
                {"status":%d,"error":%s}""".formatted(status, string(message));
    }

    /**
     * A JSON string literal, escaped.
     *
     * <p>This matters here rather than being boilerplate: the data is German and full of
     * umlauts, and the free-text fields are user-entered. A quote or backslash in a name
     * would otherwise produce malformed JSON. Control characters are escaped as \\uXXXX;
     * everything else is emitted as-is and the response is sent as UTF-8.
     */
    private static String string(String value) {
        if (value == null) {
            return "null";
        }
        StringBuilder sb = new StringBuilder(value.length() + 2).append('"');
        for (int i = 0; i < value.length(); i++) {
            char ch = value.charAt(i);
            switch (ch) {
                case '"'  -> sb.append("\\\"");
                case '\\' -> sb.append("\\\\");
                case '\n' -> sb.append("\\n");
                case '\r' -> sb.append("\\r");
                case '\t' -> sb.append("\\t");
                default -> {
                    if (ch < 0x20) {
                        sb.append("\\u%04x".formatted((int) ch));
                    } else {
                        sb.append(ch);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }

    // ---------------------------------------------------------------------- reading

    /** Thrown when a request body is not a flat JSON object this API can accept. */
    static final class Malformed extends RuntimeException {
        Malformed(String message) {
            super(message);
        }
    }

    /**
     * Parses a flat JSON object.
     *
     * @return field name to value, where a value is a {@link String}, {@link Long},
     *         {@link Double}, {@link Boolean} or null. Key order is preserved so that
     *         error messages read in the order the caller wrote the body.
     * @throws Malformed on anything else, including a nested object or array
     */
    static Map<String, Object> object(String json) {
        Reader reader = new Reader(json);
        Map<String, Object> fields = reader.object();
        reader.skipWhitespace();
        if (!reader.done()) {
            throw new Malformed("unexpected content after the JSON object");
        }
        return fields;
    }

    /**
     * A hand-written cursor over the request body.
     *
     * <p>Small enough to audit in one sitting, which matters more here than generality:
     * this is the only code in the application that turns bytes from a socket into a
     * value that reaches the database.
     */
    private static final class Reader {

        private final String src;
        private int pos;

        Reader(String src) {
            this.src = src == null ? "" : src;
        }

        Map<String, Object> object() {
            skipWhitespace();
            expect('{');
            Map<String, Object> fields = new LinkedHashMap<>();
            skipWhitespace();
            if (peek() == '}') {
                pos++;
                return fields;
            }
            while (true) {
                skipWhitespace();
                String key = string();
                skipWhitespace();
                expect(':');
                Object value = value();
                if (fields.put(key, value) != null) {
                    // Last-one-wins would make the accepted value depend on ordering.
                    throw new Malformed("duplicate field \"" + key + "\"");
                }
                skipWhitespace();
                char ch = next();
                if (ch == '}') {
                    return fields;
                }
                if (ch != ',') {
                    throw new Malformed("expected ',' or '}' but found '" + ch + "'");
                }
            }
        }

        private Object value() {
            skipWhitespace();
            char ch = peek();
            return switch (ch) {
                case '"' -> string();
                case '{', '[' -> throw new Malformed(
                        "nested objects and arrays are not accepted; a card is flat");
                case 't' -> literal("true", Boolean.TRUE);
                case 'f' -> literal("false", Boolean.FALSE);
                case 'n' -> literal("null", null);
                default -> number();
            };
        }

        private String string() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char ch = next();
                if (ch == '"') {
                    return sb.toString();
                }
                if (ch != '\\') {
                    if (ch < 0x20) {
                        throw new Malformed("unescaped control character in a string");
                    }
                    sb.append(ch);
                    continue;
                }
                char escape = next();
                switch (escape) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'n' -> sb.append('\n');
                    case 'r' -> sb.append('\r');
                    case 't' -> sb.append('\t');
                    case 'u' -> sb.append(unicode());
                    default -> throw new Malformed("unknown escape \\" + escape);
                }
            }
        }

        private char unicode() {
            if (pos + 4 > src.length()) {
                throw new Malformed("truncated \\u escape");
            }
            String hex = src.substring(pos, pos + 4);
            pos += 4;
            try {
                return (char) Integer.parseInt(hex, 16);
            } catch (NumberFormatException e) {
                throw new Malformed("invalid \\u escape: \\u" + hex);
            }
        }

        private Object number() {
            int start = pos;
            if (peek() == '-') {
                pos++;
            }
            while (!done() && (Character.isDigit(peek()) || "+-.eE".indexOf(peek()) >= 0)) {
                pos++;
            }
            String raw = src.substring(start, pos);
            if (raw.isEmpty()) {
                throw new Malformed("expected a value");
            }
            try {
                // A long first, so an integral field does not arrive as 1.0.
                return Long.valueOf(raw);
            } catch (NumberFormatException notALong) {
                try {
                    return Double.valueOf(raw);
                } catch (NumberFormatException e) {
                    throw new Malformed("not a number: " + raw);
                }
            }
        }

        private Object literal(String word, Object value) {
            if (!src.startsWith(word, pos)) {
                throw new Malformed("expected " + word);
            }
            pos += word.length();
            return value;
        }

        void skipWhitespace() {
            while (!done() && Character.isWhitespace(src.charAt(pos))) {
                pos++;
            }
        }

        boolean done() {
            return pos >= src.length();
        }

        private char peek() {
            if (done()) {
                throw new Malformed("unexpected end of body");
            }
            return src.charAt(pos);
        }

        private char next() {
            char ch = peek();
            pos++;
            return ch;
        }

        private void expect(char expected) {
            char ch = next();
            if (ch != expected) {
                throw new Malformed("expected '" + expected + "' but found '" + ch + "'");
            }
        }
    }
}
