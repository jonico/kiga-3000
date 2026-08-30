package org.de.kiga3000.views;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.awt.image.BufferedImage;

import org.junit.jupiter.api.Test;

/**
 * Covers loading and squaring the application icon. Needs no display: it is ImageIO
 * plus a BufferedImage, so it runs headless.
 *
 * <p>Installing the icon on a window or the Dock is not covered - that needs a real
 * window and, on macOS, a real Dock.
 */
public class KigaAppIconTest {

    @Test
    public void iconResourceIsOnTheClasspath() {
        // The picture lives in src/main/resources/titlepage and must be packaged into
        // the jar, otherwise the icon silently falls back to the Java default.
        assertNotNull(KigaAppIcon.class.getResource("/titlepage/KigaBild.jpg"),
                "/titlepage/KigaBild.jpg must be packaged as a classpath resource");
    }

    @Test
    public void iconIsLoadedAndMadeSquare() {
        BufferedImage icon = KigaAppIcon.loadSquareIcon();
        assertNotNull(icon, "the icon should decode");
        assertEquals(icon.getWidth(), icon.getHeight(),
                "Dock and taskbar icons are square; a wide image would be stretched");
    }

    @Test
    public void squareIconIsAtLeastAsLargeAsTheSourcePicture() {
        BufferedImage icon = KigaAppIcon.loadSquareIcon();
        // The source is 557x395, so the square side should be the larger dimension.
        assertEquals(557, icon.getWidth());
        assertEquals(557, icon.getHeight());
    }

    @Test
    public void squareIconHasAnAlphaChannelForTheLetterboxing() {
        BufferedImage icon = KigaAppIcon.loadSquareIcon();
        assertTrue(icon.getColorModel().hasAlpha(),
                "the padding above and below the photo must be transparent, not black");

        // Top-left corner is padding, so it should be fully transparent.
        int topLeftAlpha = (icon.getRGB(0, 0) >>> 24) & 0xFF;
        assertEquals(0, topLeftAlpha, "letterbox padding should be transparent");

        // The centre is the photo itself, so it should be opaque.
        int centreAlpha = (icon.getRGB(icon.getWidth() / 2, icon.getHeight() / 2) >>> 24) & 0xFF;
        assertEquals(255, centreAlpha, "the picture itself should be opaque");
    }

    @Test
    public void installToleratesANullWindow() {
        // Callers on a headless or unsupported platform must not get an exception.
        KigaAppIcon.install(null);
    }
}
