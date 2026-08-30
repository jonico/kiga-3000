package org.de.kiga3000.interfaces.newcard;


import org.de.kiga3000.data.child.Child;

public interface IFKigaCard {

	public String getAdditionalInfo();
	public void setAdditionalInfo(String additionalInfo);
	public int getCardID();
	public void setCardID(int cardID);
	public Child getChild();
	public void setChild(Child child);
	public String getEntryDate();
	public void setEntryDate(String entryDate);
	public int getGroup();
	public void setGroup(int group);
	public String getLeavingDate();
	public void setLeavingDate(String leavingDate);
				
}
