
import org.de.kiga3000.control.KigaMainControl;

/*   starts first the main controller which has control 
 *   about choosing the view and creates needed controllers
 *  
 * @author bobo_local
 *
 */
public class KiGa3000Main {

	
	public static void main(String[] arg) {
		/* starts main controller */
		KigaMainControl maincontrol = new KigaMainControl();
		maincontrol.startApplication();
	}
}
