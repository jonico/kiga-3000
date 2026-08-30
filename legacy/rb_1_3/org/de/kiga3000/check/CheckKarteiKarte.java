/*
 * Created on 26.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.de.kiga3000.check;

//import com.ibm.icu.text.DateFormat;
import org.de.kiga3000.data.KarteikarteImpl;
/**
 * checks are not implemented yet 
 * @author bobo_local
 *
 * TODO implement Error Interface for interaction with view
 * check sammelt fehler und gibt sie an control zurück, der wiederum
 * diese Infos an den View weitergibt. der arbeitet das Protokoll
 * ab. Layout Protokoll : karteifeldname, fehlertype.
 */
/**
 * @author bobo_local
 *

 */
public class CheckKarteiKarte {

	private ErrorProtocoll error;
//	private DateFormat dateform = new DateFormat();
	/**
	 *  constructor for check object
	 *  instantiates and refresh error Protocoll
	 */
	public CheckKarteiKarte(){
		error = new ErrorProtocoll();
	}
	
	/**
	 * will check different field for consistency and
	 * validity provides oObject ErrorProtocoll
	 * @param kartei {@link org.de.kiga3000.data.KarteikarteImpl} data container
	 * @return {@link org.de.kiga3000.check.ErrorProtocoll} error protocoll
	 */
	public ErrorProtocoll checkKarteiKarte(KarteikarteImpl kartei){
		// checks die gemacht werden :
		// 1. Datums Format
		// 2. Telefon angaben
		// 3. Postleitzahlen
//		dateform.
		return error;
	}
}
