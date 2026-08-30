/**
 * kiga3000 org.de.kiga3000.sorting KarteiKarteComparator.java
 * 02.12.2005
 */
package org.de.kiga3000.sorting;

import java.text.ParseException;
import java.util.Comparator;

import org.de.kiga3000.conversion.DateConversion;
import org.de.kiga3000.interfaces.Karteikarte;

/**
 * Orders cards for the search-results table: surname, then forename, then date of
 * birth, then address.
 *
 * <p>The 2006 implementation did not do that. Its conditions were inverted - each
 * branch returned the comparison of a field it had just established to be EQUAL:
 *
 * <pre>
 *   if (surname differs) {
 *       if (forename differs) {
 *           if (birth differs) {
 *               if (birth differs) { return compare(address); }
 *               else               { return compare(birth); }   // always 0
 *           } else                 { return compare(birth); }   // always 0
 *       } else                     { return compare(forename); } // always 0
 *   } else                         { return compare(surname); }  // always 0
 * </pre>
 *
 * <p>The consequences were: equal surnames returned 0 immediately, so the forename
 * tie-break was unreachable; differing surnames fell through to comparing the ADDRESS,
 * so the table was ordered by {@code kindwohnung} rather than by name; and the result
 * was non-zero only when surname, forename and date of birth all differed AND the
 * addresses differed. For every other input it was 0, so sorting was a no-op and rows
 * kept their arrival order. The duplicated inner test also made one branch dead code.
 *
 * <p>That ordering was additionally not transitive, which breaks the
 * {@link Comparator} contract: {@code List.sort} is entitled to throw
 * "Comparison method violates its general contract!" on a large enough result set, and
 * would have done so eventually.
 *
 * <p>Two further corrections in this rewrite:
 *
 * <ul>
 *   <li><b>Dates are compared as dates.</b> They were compared as {@code dd.MM.yyyy}
 *       strings, which orders by day of month - 01.12.2019 sorted before 05.01.2019.
 *       They are now normalised to {@code uuuu-MM-dd} via {@link DateConversion},
 *       which is both lexicographically and chronologically ordered. An unparseable or
 *       absent date sorts last rather than throwing.</li>
 *   <li><b>Null-safe.</b> Every accessor was dereferenced directly. Card fields default
 *       to "" now, but a card assembled by hand can still carry nulls, and a comparator
 *       throwing NullPointerException inside a sort is a poor failure mode.</li>
 * </ul>
 */
public class KarteiKarteComparator implements Comparator {

	private static final DateConversion CONVERSION = new DateConversion();

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		Karteikarte left = (Karteikarte) arg0;
		Karteikarte right = (Karteikarte) arg1;

		int result = text(left.getKindNachname(), right.getKindNachname());
		if (result != 0) {
			return result;
		}
		result = text(left.getKindVorname(), right.getKindVorname());
		if (result != 0) {
			return result;
		}
		result = date(left.getKindGeburtsDatum(), right.getKindGeburtsDatum());
		if (result != 0) {
			return result;
		}
		return text(left.getKindWohnung(), right.getKindWohnung());
	}

	/** Case-insensitive, as the original was, and null-safe. */
	private static int text(String left, String right) {
		String l = left == null ? "" : left;
		String r = right == null ? "" : right;
		return l.compareToIgnoreCase(r);
	}

	/**
	 * Compares two date strings chronologically.
	 *
	 * <p>Cards carry dates as strings, in either the German display format or the
	 * database format. Both are normalised to {@code uuuu-MM-dd} before comparing, so
	 * ordering is chronological rather than by day of month.
	 *
	 * <p>A card with no date, or with a date that cannot be parsed, sorts after one
	 * that has a usable date. Two such cards compare equal, which keeps the ordering
	 * transitive.
	 */
	private static int date(String left, String right) {
		String l = normalise(left);
		String r = normalise(right);
		if (l.isEmpty() || r.isEmpty()) {
			// "" sorts last: empty vs non-empty -> non-empty first.
			return l.isEmpty() ? (r.isEmpty() ? 0 : 1) : -1;
		}
		return l.compareTo(r);
	}

	/** @return the date as uuuu-MM-dd, or "" if absent or unparseable */
	private static String normalise(String datum) {
		if (datum == null || datum.trim().isEmpty()) {
			return "";
		}
		try {
			return CONVERSION.StringToMysqlDate(datum);
		} catch (ParseException e) {
			return "";
		}
	}
}
