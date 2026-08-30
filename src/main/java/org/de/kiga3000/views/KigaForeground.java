package org.de.kiga3000.views;

import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.Window;
import java.util.logging.Logger;

/**
 * Brings the application to the foreground at startup.
 *
 * <p>The application used to open behind whatever was already in front - typically the
 * terminal it was launched from. {@code setVisible(true)} maps the window, but on
 * macOS it does not activate the <em>application</em>: the frontmost app keeps focus,
 * and the new window appears behind it. {@link Window#toFront()} does not fix that
 * either, because it reorders windows within an application that is not frontmost to
 * begin with.
 *
 * <p>Activating the application itself needs
 * {@link Desktop#requestForeground(boolean)}, which was added in <strong>Java 9</strong>
 * ({@link Desktop.Action#APP_REQUEST_FOREGROUND}). On the Java 1.4/5 this application
 * targeted there was no portable way to do it at all - the options were the
 * Apple-specific {@code com.apple.eawt} classes or a native launcher stub.
 *
 * <p>All three steps are applied, since they address different things and the useful
 * ones vary by platform:
 * <ol>
 *   <li>{@code requestForeground} activates the application (Java 9+).</li>
 *   <li>{@code toFront} raises this window above the application's other windows.</li>
 *   <li>{@code requestFocus} gives it keyboard focus, so typing goes to the card
 *       fields rather than to the window that used to be in front.</li>
 * </ol>
 */
public final class KigaForeground {

    private static final Logger LOGGER = Logger.getLogger(KigaForeground.class.getName());

    private KigaForeground() {
    }

    /**
     * Requests that the application and the given window become frontmost.
     *
     * <p>Best effort by design: a window manager is free to refuse, and refusing is
     * legitimate - stealing focus from whatever the user is typing into is exactly the
     * behaviour focus-stealing prevention exists to stop. Nothing here is allowed to
     * prevent startup.
     *
     * @param window the window to raise; may be null, in which case only the
     *               application is activated
     */
    public static void raise(Window window) {
        // Must run on the Event Dispatch Thread, and AFTER the pending window events.
        //
        // The first version of this called straight through on the calling thread,
        // which is the main thread, immediately after setVisible(true) and
        // setExtendedState(MAXIMIZED_BOTH). It had no effect: MAXIMIZED_BOTH re-maps
        // the window, so the activation request raced the window actually appearing.
        // Queueing it means it runs once those events have been processed.
        // Always deferred, even when already on the EDT: the point is to run after the
        // queued window events, not merely to be on the right thread.
        EventQueue.invokeLater(() -> doRaise(window));
    }

    private static void doRaise(Window window) {
        // Java 9+, and the only part that activates the application itself on macOS.
        try {
            if (Desktop.isDesktopSupported()) {
                Desktop desktop = Desktop.getDesktop();
                if (desktop.isSupported(Desktop.Action.APP_REQUEST_FOREGROUND)) {
                    // true, not false: false only considers the application's foremost
                    // window, and right after a MAXIMIZED_BOTH re-map there may not be
                    // a settled foremost window to act on.
                    desktop.requestForeground(true);
                }
            }
        } catch (UnsupportedOperationException | SecurityException | IllegalStateException e) {
            LOGGER.fine("could not request foreground: " + e.getMessage());
        }

        if (window == null) {
            return;
        }

        /*
         * Briefly marking the window always-on-top is the portable way to get it above
         * the windows of other applications. toFront() alone is widely ignored -
         * window managers treat it as a hint, and macOS in particular will reorder only
         * within the current application. The flag is restored immediately, so the
         * window does not actually stay pinned above everything.
         */
        boolean wasAlwaysOnTop = window.isAlwaysOnTop();
        boolean canPin = window.isAlwaysOnTopSupported();
        try {
            if (canPin) {
                window.setAlwaysOnTop(true);
            }
            window.toFront();
            window.requestFocus();
        } catch (SecurityException e) {
            LOGGER.fine("could not raise the window: " + e.getMessage());
        } finally {
            try {
                if (canPin) {
                    window.setAlwaysOnTop(wasAlwaysOnTop);
                }
            } catch (SecurityException e) {
                LOGGER.fine("could not restore always-on-top: " + e.getMessage());
            }
        }
    }
}
