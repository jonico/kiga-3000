package org.de.kiga3000.printing;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
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

  public int print(Graphics g, PageFormat pageFormat, int pageIndex) {
    if (pageIndex > 0) {
      return(NO_SUCH_PAGE);
    } else {
      Graphics2D g2d = (Graphics2D)g;
      g2d.translate(pageFormat.getImageableX(), pageFormat.getImageableY());
      // TODO hier muss noch etwas geändert werden
      // g2d.scale(0.7,0.7);
	// disableDoubleBuffering(componentToBePrinted);
	// componentToBePrinted.setSize (getDrawingSize ().getDimension ());
	int myWidth=componentToBePrinted.getWidth();
	int myHeight=componentToBePrinted.getHeight();
	// int myHeight=componentToBePrinted.getWidth();
	// int myWidth=componentToBePrinted.getHeight();
	double yourWidth=pageFormat.getImageableWidth();
	double yourHeight=pageFormat.getImageableHeight();
	// System.out.println("myWidth "+ myWidth+ " myHeight " + myHeight+ " yourWidth "+yourWidth+" yourHeight "+ yourHeight);
	double scalex=myWidth<yourWidth?1.0:yourWidth/myWidth;
	double scaley=myHeight<yourHeight?1.0:yourHeight/myHeight;
	double scale=scaley<scalex?scaley:scalex;
	// System.out.println("Scale: "+ scale);
	g2d.scale(scale,scale);
	
	// BufferedImage buffer = new BufferedImage (myWidth,myHeight, BufferedImage.TYPE_INT_RGB);
	// componentToBePrinted.setSize (yourWidth,yourHeight);
	BufferedImage buffer = new BufferedImage (myWidth,myHeight, BufferedImage.TYPE_INT_RGB);
	Graphics2D buffergraphic=buffer.createGraphics();
	//buffergraphic.rotate(Math.PI/2);
	componentToBePrinted.printAll(buffergraphic);
	
	//g2d.drawImage (buffer,0,0,yourWidth<myWidth?yourWidth:myWidth,yourHeight<myHeight?yourHeight:myHeight,componentToBePrinted);
	g2d.drawImage (buffer,0,0,Color.WHITE,componentToBePrinted);
      //componentToBePrinted.paint(g2d);
      //enableDoubleBuffering(componentToBePrinted);
      return(PAGE_EXISTS);
    }
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
