package org.de.kiga3000;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.logging.Logger;

import org.de.kiga3000.views.Configuration;

/**
 * Locates the application's own resources - the title page and the logging
 * configuration.
 *
 * <p>Why this exists. The 2006 code built filesystem paths by concatenating the
 * {@code KiGaLoggingPath} configuration key with a relative path:
 *
 * <pre>
 *   allgemeinHTML.setPage("file:///" + config.getConfig("KiGaLoggingPath")
 *                         + "/titlepage/kiga.htm");
 *   new FileInputStream(pfadLog + "/org/de/kiga3000/properties/KiGaLogging.properties");
 * </pre>
 *
 * <p>{@code KiGaLoggingPath} ships EMPTY, so out of the box those resolve to
 * {@code file:////titlepage/kiga.htm} and {@code /org/de/.../KiGaLogging.properties} -
 * absolute paths at the filesystem root. The result was that the title picture never
 * appeared (the user got a modal "no online help" error dialog instead) and the
 * logging configuration was never applied. Both resources are packaged in the jar, so
 * neither could ever be found this way; the application only worked if someone
 * unpacked it and pointed the key at the unpacked directory.
 *
 * <p>Resolution order now:
 * <ol>
 *   <li>If {@code KiGaLoggingPath} is set to a non-empty value AND the resource exists
 *     underneath it, use that. This keeps any existing installation that relies on the
 *     key working, and still allows an administrator to override a packaged resource.</li>
 *   <li>Otherwise load it from the classpath, which is where these resources actually
 *     live. This is what makes {@code java -jar Kiga3000.jar} work from any directory.</li>
 * </ol>
 */
public final class KigaResources {

    private static final Logger LOGGER = Logger.getLogger(KigaResources.class.getName());

    /** The configuration key the original code used as an installation directory. */
    private static final String INSTALL_DIR_KEY = "KiGaLoggingPath";

    public static final String TITLE_PAGE = "/titlepage/kiga.htm";
    public static final String LOGGING_CONFIG =
            "/org/de/kiga3000/properties/KiGaLogging.properties";

    private KigaResources() {
    }

    /**
     * The configured installation directory, or null when unset, blank or absent.
     */
    private static File installDir() {
        try {
            String configured = new Configuration().getConfig(INSTALL_DIR_KEY);
            if (null == configured || configured.trim().isEmpty()) {
                return null;
            }
            File dir = new File(configured.trim());
            return dir.isDirectory() ? dir : null;
        } catch (MissingResourceException e) {
            // Key removed from Config.properties entirely - fine, use the classpath.
            return null;
        }
    }

    /**
     * Resolves a resource to a URL, preferring a configured installation directory and
     * falling back to the classpath.
     *
     * @param resourcePath absolute classpath-style path, e.g. {@code /titlepage/kiga.htm}
     * @return the URL, or null if the resource cannot be found anywhere
     */
    public static URL locate(String resourcePath) {
        File dir = installDir();
        if (null != dir) {
            File candidate = new File(dir, resourcePath);
            if (candidate.isFile()) {
                try {
                    return candidate.toURI().toURL();
                } catch (MalformedURLException e) {
                    LOGGER.fine("unusable path for " + resourcePath + ": " + e.getMessage());
                }
            }
        }
        URL fromClasspath = KigaResources.class.getResource(resourcePath);
        if (null == fromClasspath) {
            LOGGER.warning("resource not found on the classpath: " + resourcePath);
        }
        return fromClasspath;
    }

    /**
     * Opens a resource as a stream, with the same resolution order as
     * {@link #locate(String)}.
     *
     * @return an open stream the caller must close, or null if not found
     */
    public static InputStream open(String resourcePath) throws IOException {
        URL url = locate(resourcePath);
        return url == null ? null : url.openStream();
    }
}
