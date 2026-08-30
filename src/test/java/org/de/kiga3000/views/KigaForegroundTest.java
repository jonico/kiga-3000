package org.de.kiga3000.views;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * There is not much to assert here without a real desktop: whether a window actually
 * comes to the front is decided by the window manager, and focus-stealing prevention
 * may legitimately refuse.
 *
 * <p>What can be asserted, and matters, is that none of it can prevent the application
 * from starting. The call sits immediately after the main window is shown, so an
 * exception escaping it would be caught by the outer handler in
 * {@code KigaMainControl.startApplication} and turn a cosmetic problem into
 * {@code System.exit(1)}.
 */
public class KigaForegroundTest {

    @Test
    public void raiseToleratesANullWindow() {
        assertDoesNotThrow(() -> KigaForeground.raise(null));
    }

    @Test
    public void raiseIsSafeToCallRepeatedly() {
        assertDoesNotThrow(() -> {
            KigaForeground.raise(null);
            KigaForeground.raise(null);
        });
    }
}
