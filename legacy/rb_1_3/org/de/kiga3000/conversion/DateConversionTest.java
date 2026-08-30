package org.de.kiga3000.conversion;

import java.text.ParseException;

import junit.framework.TestCase;

public class DateConversionTest extends TestCase {

	private DateConversion dateconv;

	public DateConversionTest(String name) {
		super(name);
		dateconv = new DateConversion();
	}

	/*
	 * Test method for 'org.de.kiga3000.conversion.DateConversion.StringToLocalDate(String)'
	 */
	public void testStringToLocalDate() {
		try{
          String datum = dateconv.StringToLocalDate("30.02.2005");
          System.out.println(datum);
		}catch (ParseException excp){
			System.out.println("Mist");
		}
	}

	/*
	 * Test method for 'org.de.kiga3000.conversion.DateConversion.StringToMysqlDate(String)'
	 */
	public void testStringToMysqlDate() {
		try{
	          String datum = dateconv.StringToMysqlDate("28.02.2005");
	          System.out.println("MYSQL " + datum);
			}catch (ParseException excp){
				System.out.println("Mist");
			}

	}

}
