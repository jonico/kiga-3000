import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.RepaintManager;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.de.kiga3000.printing.PrintUtilities;

/**
 * Manual check for the macOS printing crash, without having to navigate the whole
 * application.
 *
 * <p>Printing in KiGa 3000 used to abort the JVM natively on macOS:
 *
 * <pre>
 *   Bad JNI lookup sData
 *   libawt_lwawt.dylib  Java_sun_java2d_OSXOffScreenSurfaceData_getSurfaceData
 *   *** Terminating app due to uncaught exception NSGenericException,
 *       reason: JNI Lookup Exception
 * </pre>
 *
 * <p>The cause was rendering the component into an offscreen BufferedImage and then
 * blitting that onto the printer's Graphics2D. This harness drives a real PrinterJob
 * through the native macOS print dialogs, which is the only way to reach that code
 * path - a PostScript StreamPrintService is pure Java and never touches it, so an
 * automated test cannot cover this.
 *
 * <p>Modes:
 * <ul>
 *   <li>{@code fixed} (default) - calls the repository's real
 *       {@link PrintUtilities}, i.e. what ships today.</li>
 *   <li>{@code legacy} - reproduces the 2006 code path inline. Expected to crash the
 *       JVM on macOS. Use it to confirm the harness really does exercise the bug.</li>
 * </ul>
 *
 * <p>Both modes open the page setup dialog and then the print dialog. Choosing
 * "Save as PDF" as the destination is enough; no paper is needed.
 */
public final class PrintCheck {

    private static boolean legacy;
    private static Component target;

    private PrintCheck() {
    }

    public static void main(String[] args) throws Exception {
        legacy = args.length > 0 && args[0].equalsIgnoreCase("legacy");

        try {
            UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");
        } catch (Exception e) {
            System.out.println("Liquid L&F unavailable, continuing with the default: "
                    + e.getMessage());
        }
        System.out.println("mode          : " + (legacy ? "LEGACY (expected to crash)" : "FIXED"));
        System.out.println("look and feel : " + UIManager.getLookAndFeel().getName());
        System.out.println("java          : " + System.getProperty("java.version"));
        System.out.println();

        SwingUtilities.invokeAndWait(PrintCheck::buildAndPrint);
    }

    private static void buildAndPrint() {
        // Something shaped like the application's search-results table.
        String[] cols = {"id", "Gruppe", "Vorname", "Nachname", "Geburtstag", "Wohnung"};
        Object[][] rows = new Object[20][6];
        for (int i = 0; i < rows.length; i++) {
            rows[i] = new Object[] {
                    i + 1, 1 + i % 3, "Vorname" + i, "Nachnameß" + i,
                    String.format("%02d.02.201%d", 1 + i % 28, i % 10),
                    "Musterweg " + i + ", Musterstadt"};
        }
        JTable table = new JTable(rows, cols);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel("KiGa 3000 print check - " + (legacy ? "legacy" : "fixed") + " path"),
                BorderLayout.NORTH);
        panel.add(new JScrollPane(table), BorderLayout.CENTER);

        // The component must be realized and sized, or there is nothing to print.
        JFrame frame = new JFrame("KiGa 3000 print check");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().add(panel);
        frame.setSize(900, 650);
        frame.setVisible(true);

        target = table;

        try {
            if (legacy) {
                printTheOldWay(target);
            } else {
                PrintUtilities.printComponent(target);
            }
            System.out.println();
            System.out.println("RESULT: printing returned normally - no JVM crash.");
            System.out.println("Close the window to exit.");
        } catch (PrinterException pe) {
            System.out.println();
            System.out.println("RESULT: PrinterException (a normal Java error, not the crash): "
                    + pe.getMessage());
        }
    }

    /** The 2006 implementation, verbatim, for comparison. */
    private static void printTheOldWay(Component component) throws PrinterException {
        PrinterJob job = PrinterJob.getPrinterJob();
        PageFormat pf = job.pageDialog(job.defaultPage());
        if (!job.printDialog()) {
            System.out.println("cancelled");
            return;
        }
        job.setPrintable(new Printable() {
            public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
                if (pageIndex > 0) {
                    return NO_SUCH_PAGE;
                }
                Graphics2D g2d = (Graphics2D) g;
                g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
                int myWidth = component.getWidth();
                int myHeight = component.getHeight();
                double yourWidth = pageFormat.getImageableWidth();
                double yourHeight = pageFormat.getImageableHeight();
                double scalex = myWidth < yourWidth ? 1.0 : yourWidth / myWidth;
                double scaley = myHeight < yourHeight ? 1.0 : yourHeight / myHeight;
                g2d.scale(Math.min(scalex, scaley), Math.min(scalex, scaley));

                // the offscreen surface, and the blit onto the printer surface,
                // that OSXOffScreenSurfaceData chokes on. Double buffering is left
                // enabled here exactly as the original left it.
                BufferedImage buffer =
                        new BufferedImage(myWidth, myHeight, BufferedImage.TYPE_INT_RGB);
                Graphics2D bg = buffer.createGraphics();
                component.printAll(bg);
                g2d.drawImage(buffer, 0, 0, Color.WHITE, component);
                return PAGE_EXISTS;
            }
        }, pf);
        job.print();
        // referenced so the import is not flagged as unused in legacy mode
        RepaintManager.currentManager(component);
    }
}
