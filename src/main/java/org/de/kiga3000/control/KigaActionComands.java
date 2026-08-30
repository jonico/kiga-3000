/*
 * Created on 21.01.2006
 *
  */
package org.de.kiga3000.control;

/**
 * @author bobo_local
 *
 * contains constants for the known action commands
 */
public class KigaActionComands {

	// save data to disk
	public static final String actionSaveToFileKartei = "karteiToFile";
    // load data
	public static final String actionLoadKartei = "LoadKartei";
    // create data
	public static final String actionAnlegKartei = "AnlegKartei";
    // update data
	public static final String actionUpdateKartei = "UpdateKartei";
	// delete data
	public static final String actionDeleteKartei = "DeleteKartei";
    // sign out kid
	public static final String actionAbmeldKartei = "AbmeldKartei";
    // print table content
	public static final String actionDruckErgTab = "DruckeTab";
    // print data
	public static final String actionDruckKartei = "DruckeKartei";	
	// print data direct from ViewImpl
	public static final String actionDruckKartei2 = "DruckeKartei2";	
	// search data
	public static final String actionSucheKartei = "SucheKartei";
	// change group
	public static final String actionTauschKartei = "TauschKartei";

}
