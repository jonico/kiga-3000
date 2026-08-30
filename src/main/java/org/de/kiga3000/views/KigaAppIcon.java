package org.de.kiga3000.views;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Taskbar;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

/**
 * Gives the application its own icon - the KiGa 3000 title picture - instead of the
 * generic Java coffee cup.
 *
 * <p>Two different mechanisms are needed, and only one of them existed when this
 * application was written:
 *
 * <ul>
 *   <li>{@link Window#setIconImage} sets the window icon. It has been available since
 *       JDK 1.0 and covers Windows and most Linux desktops, but on macOS the window
 *       has no title-bar icon and the Dock ignores it entirely.</li>
 *   <li>{@link java.awt.Taskbar}, added in <strong>Java 9</strong>, sets the icon the
 *       Dock and the app switcher show. This is the part that was simply not
 *       expressible on the Java 1.4/5 the original targeted - the only way to do it
 *       then was the non-portable {@code -Xdock:icon=} launcher flag, which needs a
 *       filesystem path and therefore does not work from inside a jar.</li>
 * </ul>
 *
 * <p>Both are attempted, each guarded, so the application still starts on a platform
 * or headless configuration where either is unavailable.
 */
public final class KigaAppIcon {

    private static final Logger LOGGER = Logger.getLogger(KigaAppIcon.class.getName());

    /** The title picture, loaded from the classpath so it works from inside the jar. */
    private static final String ICON_RESOURCE = "/titlepage/KigaBild.jpg";

    private KigaAppIcon() {
    }

    /**
     * Installs the application icon on the given window and, where supported, on the
     * taskbar or Dock.
     *
     * @param window the main window; may be null, in which case only the Dock icon is set
     */
    public static void install(Window window) {
        BufferedImage icon = loadSquareIcon();
        if (icon == null) {
            return;
        }

        if (window != null) {
            window.setIconImage(icon);
        }

        // Java 9+. On macOS this is what actually changes the Dock icon.
        try {
            if (Taskbar.isTaskbarSupported()) {
                Taskbar taskbar = Taskbar.getTaskbar();
                if (taskbar.isSupported(Taskbar.Feature.ICON_IMAGE)) {
                    taskbar.setIconImage(icon);
                }
            }
        } catch (UnsupportedOperationException | SecurityException e) {
            // Not fatal: the window icon above is still set.
            LOGGER.fine("taskbar icon not available: " + e.getMessage());
        }
    }

    /**
     * Loads the title picture and letterboxes it onto a transparent square.
     *
     * <p>The source is 557x395, and Dock and taskbar icons are square. Handing over a
     * wide image gets it stretched, so it is centred on a transparent square of its
     * larger dimension instead, preserving the aspect ratio.
     */
    static BufferedImage loadSquareIcon() {
        URL url = KigaAppIcon.class.getResource(ICON_RESOURCE);
        if (url == null) {
            LOGGER.warning("application icon not found on the classpath: " + ICON_RESOURCE);
            return null;
        }
        try (InputStream in = KigaAppIcon.class.getResourceAsStream(ICON_RESOURCE)) {
            BufferedImage source = ImageIO.read(in);
            if (source == null) {
                LOGGER.warning("application icon could not be decoded: " + ICON_RESOURCE);
                return null;
            }

            int side = Math.max(source.getWidth(), source.getHeight());
            BufferedImage square = new BufferedImage(side, side, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = square.createGraphics();
            try {
                g.setRenderingHint(RenderingHints.KEY_INTERPOLATION,
                        RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g.drawImage(source,
                        (side - source.getWidth()) / 2,
                        (side - source.getHeight()) / 2,
                        null);
            } finally {
                g.dispose();
            }
            return square;
        } catch (IOException e) {
            LOGGER.warning("application icon could not be read: " + e.getMessage());
            return null;
        }
    }
}
