package org.de.kiga3000.data.health;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.commun.Communication;

public class Doctor {
	
	private int docID;
	private Adress adress;
	private Communication comu;
	private String name;
	private String firstName;
	private String title;
	private String specialised;
	
	public Adress getAdress() {
		return adress;
	}
	public void setAdress(Adress adress) {
		this.adress = adress;
	}
	public Communication getComu() {
		return comu;
	}
	public void setComu(Communication comu) {
		this.comu = comu;
	}
	public int getDocID() {
		return docID;
	}
	public void setDocID(int docID) {
		this.docID = docID;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSpecialised() {
		return specialised;
	}
	public void setSpecialised(String specialised) {
		this.specialised = specialised;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	
	
	

}
