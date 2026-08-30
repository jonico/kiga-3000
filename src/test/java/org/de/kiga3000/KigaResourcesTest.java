package org.de.kiga3000;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.InputStream;
import java.net.URL;

import org.junit.jupiter.api.Test;

/**
 * Covers resource resolution. These are the two resources whose loading was broken
 * from the start: both were looked up as filesystem paths built by concatenating the
 * empty {@code KiGaLoggingPath} config key with a relative path, so neither was ever
 * found, and both are packaged inside the jar where a filesystem path cannot reach
 * them.
 */
public class KigaResourcesTest {

    @Test
    public void titlePageIsResolvable() {
        URL url = KigaResources.locate(KigaResources.TITLE_PAGE);
        assertNotNull(url, "the title page must be resolvable, or the Allgemein tab "
                + "shows an error dialog instead of the picture");
    }

    @Test
    public void titlePageReferencesItsPicture() throws Exception {
        URL url = KigaResources.locate(KigaResources.TITLE_PAGE);
        try (InputStream in = url.openStream()) {
            String html = new String(in.readAllBytes(), "ISO-8859-1");
            assertTrue(html.contains("KigaBild.jpg"),
                    "the page must reference the title picture");
            assertTrue(html.contains("canvas_blue.jpg"),
                    "the page must reference its background");
        }
    }

    /**
     * Swing resolves a relative {@code <IMG SRC>} against the document's own URL, so
     * the images have to sit next to the page wherever it is loaded from - including
     * inside a jar.
     */
    @Test
    public void picturesResolveRelativeToTheTitlePage() throws Exception {
        URL page = KigaResources.locate(KigaResources.TITLE_PAGE);
        for (String image : new String[] {"KigaBild.jpg", "canvas_blue.jpg"}) {
            URL resolved = page.toURI().resolve(image).toURL();
            try (InputStream in = resolved.openStream()) {
                assertTrue(in.readAllBytes().length > 0, image + " should be readable");
            }
        }
    }

    @Test
    public void loggingConfigurationIsResolvable() throws Exception {
        try (InputStream in = KigaResources.open(KigaResources.LOGGING_CONFIG)) {
            assertNotNull(in, "the logging configuration must be resolvable");
            String text = new String(in.readAllBytes(), "ISO-8859-1");
            assertTrue(text.contains("java.util.logging.FileHandler"),
                    "expected a FileHandler to be configured");
        }
    }

    /**
     * Guards the two specific values that were broken: a formatter class that does not
     * exist anywhere, and a log path relative to the working directory.
     */
    @Test
    public void loggingConfigurationUsesAnExistingFormatterAndAnAbsolutePath()
            throws Exception {
        String text;
        try (InputStream in = KigaResources.open(KigaResources.LOGGING_CONFIG)) {
            text = new String(in.readAllBytes(), "ISO-8859-1");
        }

        assertTrue(text.contains("formatter = java.util.logging.XMLFormatter"),
                "the formatter must be a class that exists; formatter.LoggingFormatter "
                        + "is not present in this project or in the original CVS tree");
        assertTrue(text.contains("pattern = %h/"),
                "the log pattern must be anchored at the user home directory, not "
                        + "relative to whatever directory the app was started from");

        // Class must actually be loadable, not merely spelled plausibly.
        assertNotNull(Class.forName("java.util.logging.XMLFormatter"));
    }

    @Test
    public void unknownResourceResolvesToNullRatherThanThrowing() {
        assertEquals(null, KigaResources.locate("/does/not/exist.txt"));
    }
}
