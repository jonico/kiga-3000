package org.de.kiga3000.messages;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

/**
 * Covers the resource bundles behind the user-facing text. Cheap to test and easy to
 * break: a missing key raises {@link MissingResourceException} at the moment the
 * message is shown, which in a Swing application means an unhandled exception in an
 * event handler.
 */
public class MessengerTest {

    private final Messenger messenger = new Messenger();
    private final SystemMessenger systemMessenger = new SystemMessenger();

    @Test
    public void resolvesAPlainMessage() {
        assertEquals("KiGA300 Version 1.2", messenger.getMessage("KiGa.title"));
    }

    @Test
    public void substitutesASinglePlaceholder() {
        // The bundle text is "Data error: {1}"
        String msg = messenger.getMessage("KiGa.DataErr", "disk on fire");
        assertTrue(msg.contains("disk on fire"), "actual: " + msg);
        assertFalse(msg.contains("{1}"), "placeholder left unsubstituted: " + msg);
    }

    @Test
    public void unknownKeyRaisesMissingResourceException() {
        assertThrows(MissingResourceException.class,
                () -> messenger.getMessage("KiGa.ThisKeyDoesNotExist"));
    }

    @Test
    public void systemMessengerResolvesItsOwnBundle() {
        // SystemMessages is a separate bundle from Messages; this asserts the split
        // still works rather than pinning any particular wording.
        String msg = systemMessenger.getMessage("KiGa.log.ErrorNoFileFound", "a", "b");
        assertFalse(msg.isEmpty());
    }

    /**
     * The German and English bundles must define the same keys, or switching locale
     * turns a translated string into a MissingResourceException at runtime.
     */
    @Test
    public void germanAndEnglishBundlesDefineTheSameKeys() throws IOException {
        for (String bundle : new String[] {"Messages", "SystemMessages"}) {
            Properties en = load("/org/de/kiga3000/properties/" + bundle + ".properties");
            Properties de = load("/org/de/kiga3000/properties/" + bundle + "_de_DE.properties");

            TreeSet<String> onlyEn = new TreeSet<>(en.stringPropertyNames());
            onlyEn.removeAll(de.stringPropertyNames());
            TreeSet<String> onlyDe = new TreeSet<>(de.stringPropertyNames());
            onlyDe.removeAll(en.stringPropertyNames());

            assertTrue(onlyEn.isEmpty(), bundle + ": keys missing from the German bundle: " + onlyEn);
            assertTrue(onlyDe.isEmpty(), bundle + ": keys missing from the English bundle: " + onlyDe);
        }
    }

    private static Properties load(String resource) throws IOException {
        Properties p = new Properties();
        try (InputStream in = MessengerTest.class.getResourceAsStream(resource)) {
            assertTrue(in != null, "resource not on the classpath: " + resource);
            // The bundles are ISO-8859-1, which is what Properties.load assumes.
            p.load(in);
        }
        return p;
    }
}
