package org.de.kiga3000.data.health;

public class HealthCard {

	private int childID;
	private int healtID;
	private String healthInfo;
	private String jaundice;
	private String healthInsurance;
	private String lastTetanusShot;
	private byte Illnesses;
	private String additionalIllnesses;
	private Doctor doctor;
	
	public Doctor getDoctor() {
		return doctor;
	}
	public void setDoctor(Doctor doctor) {
		this.doctor = doctor;
	}
	public String getAdditionalIllnesses() {
		return additionalIllnesses;
	}
	public void setAdditionalIllnesses(String additionalIllnesses) {
		this.additionalIllnesses = additionalIllnesses;
	}
	public int getChildID() {
		return childID;
	}
	public void setChildID(int childID) {
		this.childID = childID;
	}
	public String getHealthInfo() {
		return healthInfo;
	}
	public void setHealthInfo(String healthInfo) {
		this.healthInfo = healthInfo;
	}
	public String getHealthInsurance() {
		return healthInsurance;
	}
	public void setHealthInsurance(String healthInsurance) {
		this.healthInsurance = healthInsurance;
	}
	public int getHealtID() {
		return healtID;
	}
	public void setHealtID(int healtID) {
		this.healtID = healtID;
	}
	public byte getIllnesses() {
		return Illnesses;
	}
	public void setIllnesses(byte illnesses) {
		Illnesses = illnesses;
	}
	public String getJaundice() {
		return jaundice;
	}
	public void setJaundice(String jaundice) {
		this.jaundice = jaundice;
	}
	public String getLastTetanusShot() {
		return lastTetanusShot;
	}
	public void setLastTetanusShot(String lastTetanusShot) {
		this.lastTetanusShot = lastTetanusShot;
	}
	
	
}
