package org.de.kiga3000.api;

import java.util.List;

/**
 * Just enough JSON to serialise {@link CardSummary}.
 *
 * <p>No JSON library. Adding Jackson to this application would pull a dependency tree
 * larger than the application, for six fields of two types. If the API grows beyond a
 * read-only projection, that trade changes.
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
            if (0 < i) {
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
        if (null == value) {
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
                    if (0x20 > ch) {
                        sb.append("\\u%04x".formatted((int) ch));
                    } else {
                        sb.append(ch);
                    }
                }
            }
        }
        return sb.append('"').toString();
    }
}
