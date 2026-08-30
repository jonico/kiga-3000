package org.de.kiga3000.data.card;

import java.util.Date;

import org.de.kiga3000.data.child.Child;

public class KigaCard {
	
	private int cardID;
	private Child child;
	private String entryDate;
	private String leavingDate;
	private int group;
	private String additionalInfo;
	
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}
	public int getCardID() {
		return cardID;
	}
	public void setCardID(int cardID) {
		this.cardID = cardID;
	}
	public Child getChild() {
		return child;
	}
	public void setChild(Child child) {
		this.child = child;
	}
	public String getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(String entryDate) {
		this.entryDate = entryDate;
	}
	public int getGroup() {
		return group;
	}
	public void setGroup(int group) {
		this.group = group;
	}
	public String getLeavingDate() {
		return leavingDate;
	}
	public void setLeavingDate(String leavingDate) {
		this.leavingDate = leavingDate;
	}
	

}
