package org.de.kiga3000.printing;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;

import javax.swing.RepaintManager;

/** A simple utility class that lets you very simply print
 *  an arbitrary component. Just pass the component to the
 *  PrintUtilities.printComponent. The component you want to
 *  print doesn't need a print method and doesn't have to
 *  implement any interface or do anything special at all.
 *  <P>
 *  If you are going to be printing many times, it is marginally more 
 *  efficient to first do the following:
 *  <PRE>
 *    PrintUtilities printHelper = new PrintUtilities(theComponent);
 *  </PRE>
 *  then later do printHelper.print(). But this is a very tiny
 *  difference, so in most cases just do the simpler
 *  PrintUtilities.printComponent(componentToBePrinted).
 *
 *  7/99 Marty Hall, http://www.apl.jhu.edu/~hall/java/
 *  May be freely used or adapted.
 */

public class PrintUtilities implements Printable {
  private Component componentToBePrinted;
  private PageFormat pf;
public static void printComponent(Component c) throws PrinterException {
    new PrintUtilities(c).print();
  }
  

public PrintUtilities(Component componentToBePrinted) {
    this.componentToBePrinted = componentToBePrinted;
  }
  
  public void print() throws PrinterException {
    PrinterJob printJob = PrinterJob.getPrinterJob();
    pf=printJob.pageDialog(printJob.defaultPage());
    if (printJob.printDialog()) {
		printJob.setPrintable(this,pf);
		printJob.print();
    }
        
  }

  /**
   * Renders the component onto the printer graphics.
   *
   * <p>This used to paint the component into an intermediate
   * {@link java.awt.image.BufferedImage} with {@code printAll}, and then blit that
   * image onto the printer's {@code Graphics2D}. On macOS that blit crashes the whole
   * JVM - not a Java exception, a native abort:
   *
   * <pre>
   *   Bad JNI lookup sData
   *   libawt_lwawt.dylib  Java_sun_java2d_OSXOffScreenSurfaceData_getSurfaceData
   *   *** Terminating app due to uncaught exception NSGenericException,
   *       reason: JNI Lookup Exception
   * </pre>
   *
   * <p>Drawing an offscreen surface onto a printer surface is what reaches that native
   * code path. Rendering straight onto the printer graphics avoids it entirely, and is
   * also how Swing printing is meant to be done - the offscreen copy was a 1999-era
   * workaround inherited from the original utility class.
   *
   * <p>Double buffering is disabled for the duration, which is what this class's own
   * javadoc has always advised and what the two helper methods below exist for; the
   * calls were commented out and so never ran. Leaving it enabled makes Swing paint
   * through its own offscreen buffer, which reintroduces the same crash by another
   * route.
   */
  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (0 < pageIndex) {
      return(NO_SUCH_PAGE);
    }

    int myWidth = componentToBePrinted.getWidth();
    int myHeight = componentToBePrinted.getHeight();
    // A zero-sized component would previously have thrown IllegalArgumentException
    // from the BufferedImage constructor, inside a print job, with no useful message.
    if (0 >= myWidth || 0 >= myHeight) {
      return(NO_SUCH_PAGE);
    }

    Graphics2D g2d = (Graphics2D)g;
    g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());

    // Shrink to fit the page, never enlarge.
    double yourWidth = pageFormat.getImageableWidth();
    double yourHeight = pageFormat.getImageableHeight();
    double scalex = myWidth < yourWidth ? 1.0 : yourWidth / myWidth;
    double scaley = myHeight < yourHeight ? 1.0 : yourHeight / myHeight;
    double scale = Math.min(scalex, scaley);
    g2d.scale(scale, scale);

    RepaintManager repaintManager = RepaintManager.currentManager(componentToBePrinted);
    boolean wasDoubleBuffering = repaintManager.isDoubleBufferingEnabled();
    disableDoubleBuffering(componentToBePrinted);
    try {
      componentToBePrinted.printAll(g2d);
    } finally {
      // Restore whatever it was, rather than unconditionally enabling it.
      repaintManager.setDoubleBufferingEnabled(wasDoubleBuffering);
    }
    return(PAGE_EXISTS);
  }

  /** The speed and quality of printing suffers dramatically if
   *  any of the containers have double buffering turned on.
   *  So this turns if off globally.
   *  @see enableDoubleBuffering
   */
  public static void disableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(false);
  }

  /** Re-enables double buffering globally. */
  
  public static void enableDoubleBuffering(Component c) {
    RepaintManager currentManager = RepaintManager.currentManager(c);
    currentManager.setDoubleBufferingEnabled(true);
  }
}
