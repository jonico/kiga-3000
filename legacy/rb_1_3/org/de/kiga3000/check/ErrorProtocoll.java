/*
 * Created on 26.01.2006
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package org.de.kiga3000.check;

import java.util.HashMap;

/**
 * provides a ErrorProtocoll for views and controllers to react
 * @author bobo_local
 * 
  */
public class ErrorProtocoll {
	private HashMap error;  //error Table
	/*  Error types as constants */
	public static final String typeError = "E"; 
	public static final String typeWarning = "W";
	public static final String typeInfo = "I";
  
	/* Constructor creates new Error table */
	public ErrorProtocoll() {
		error = new HashMap();
	}

	/**
	 * add an error description
	 * @param typeErr Constant typeError etc
	 * @param message Any String
	 * you should consider that for further decision
	 * the error message should be unique
	 */
	public void addError(String typeErr,String message){
		error.put(message,typeErr);
	}

	/**
	 * return the error table as HashMap 
	 * @return error table as HashMap
	 */
	public HashMap getErrorTab(){
		return error;
	}
	
	/**
	 *  clears the error message table
	 */
	public void clear(){
		error.clear();
	}
}
