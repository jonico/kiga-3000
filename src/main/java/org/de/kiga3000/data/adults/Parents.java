package org.de.kiga3000.data.adults;

public class Parents {
	
	private int parentID;
	private Adult mother;
	private Adult father;
	private Adult authCareGiver;
	private int familyID;
	private byte familystatus;
	private byte authorizedCaregiver;
	
	public byte getAuthorizedCaregiver() {
		return authorizedCaregiver;
	}
	public void setAuthorizedCaregiver(byte authorizedCaregiver) {
		this.authorizedCaregiver = authorizedCaregiver;
	}
	public int getFamilyID() {
		return familyID;
	}
	public void setFamilyID(int familyID) {
		this.familyID = familyID;
	}
	public byte getFamilystatus() {
		return familystatus;
	}
	public void setFamilystatus(byte familystatus) {
		this.familystatus = familystatus;
	}
	public Adult getFather() {
		return father;
	}
	public void setFather(Adult father) {
		this.father = father;
	}
	public Adult getMother() {
		return mother;
	}
	public void setMother(Adult mother) {
		this.mother = mother;
	}
	public Adult getAuthCareGiver() {
		return authCareGiver;
	}
	public void setAuthCareGiver(Adult authCareGiver) {
		this.authCareGiver = authCareGiver;
	}
	public int getParentID() {
		return parentID;
	}
	public void setParentID(int parentID) {
		this.parentID = parentID;
	}
	
	

}
