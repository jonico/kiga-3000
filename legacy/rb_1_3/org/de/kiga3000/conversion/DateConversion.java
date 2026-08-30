package org.de.kiga3000.conversion;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.Locale;

/**
 * @author bobo_local
 * <br>this class provides date conversion functionalities
 * 
  */
public class DateConversion {

	// DateFormat instance for locale dates
	private DateFormat dateToLocale = DateFormat.getDateInstance(
			DateFormat.MEDIUM, Locale.getDefault());
    // DateFormat instance for database format
	private DateFormat dateToMysql = DateFormat.getDateInstance(
			DateFormat.MEDIUM, Locale.CHINA);

	/**
	 * checks if given date is valid and changes database format to locale
	 * @param datum Date
	 * @return      formatted date
	 * @throws ParseException Date is not valid
	 */
	public String StringToLocalDate(String datum) throws ParseException {
		// returns formatted date
		String retDate = "";
		// if date is empty or databse gives 0
		if (datum.equals("") || datum.equals("0000-00-00")) {
			retDate = "";
		} else {
			try {
				// first convert to Date
				Date tempDate = dateToLocale.parse(datum);
				// format date
				retDate = dateToLocale.format(tempDate);
			} catch (ParseException exc) {
				// maybe it's a database format ?
				Date tempDate = dateToMysql.parse(datum);
				retDate = dateToLocale.format(tempDate);
			}
		}
		return retDate;
	}

	public String DateToLocalDate(Date datum) throws ParseException {
		// returns formatted date
		String retDate = "";
		// if date is empty or databse gives 0
		if (datum.equals("") || datum.equals("0000-00-00")) {
			retDate = "";
		} else {
    		retDate = dateToLocale.format(datum);
		}
		return retDate;
	}
	/**
	 * formats date to database format
	 * @param datum date
	 * @return     formatted database date
	 * @throws ParseException date is not valid
	 */
	public String StringToMysqlDate(String datum) throws ParseException {
		String retDate = "";
		// no date is given 
		if (datum.equals("")) {
			retDate = "0000-00-00";
		} else {
			// first convert to date
			Date tempDate = dateToLocale.parse(datum);
			// convert to database date
			java.sql.Date temp2date = new java.sql.Date(tempDate.getTime());
			// return as String
			retDate = temp2date.toString();
		}
		return retDate;
	}

}
