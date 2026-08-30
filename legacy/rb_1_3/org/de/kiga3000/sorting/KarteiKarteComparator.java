/**
 * kiga3000 org.de.kiga3000.sorting KarteiKarteComparator.java
 * 02.12.2005
 */
package org.de.kiga3000.sorting;

import java.util.Comparator;

import org.de.kiga3000.interfaces.Karteikarte;

/**
 * @author bobohead2
 *
 */
public class KarteiKarteComparator implements Comparator {

	/* (non-Javadoc)
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	public int compare(Object arg0, Object arg1) {
		Karteikarte karteikarte = (Karteikarte)arg0;
		Karteikarte unknown = (Karteikarte)arg1;
		if(karteikarte.getKindNachname().compareToIgnoreCase(unknown.getKindNachname()) != 0){
			if(karteikarte.getKindVorname().compareToIgnoreCase(unknown.getKindVorname()) != 0){
				if(karteikarte.getKindGeburtsDatum().compareToIgnoreCase(unknown.getKindGeburtsDatum()) != 0){
					if(karteikarte.getKindGeburtsDatum().compareToIgnoreCase(unknown.getKindGeburtsDatum()) != 0){
						return karteikarte.getKindWohnung().compareToIgnoreCase(unknown.getKindWohnung());
					}else{
						return karteikarte.getKindGeburtsDatum().compareToIgnoreCase(unknown.getKindGeburtsDatum());
					}
				}else{
					return karteikarte.getKindGeburtsDatum().compareToIgnoreCase(unknown.getKindGeburtsDatum());
				}
			}else{
				return karteikarte.getKindVorname().compareToIgnoreCase(unknown.getKindVorname());
			}
		}else{
			return karteikarte.getKindNachname().compareToIgnoreCase(unknown.getKindNachname());
		}
	}

}
