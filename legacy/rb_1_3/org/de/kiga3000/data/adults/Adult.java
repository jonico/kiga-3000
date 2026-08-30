package org.de.kiga3000.data.adults;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.commun.Communication;

public class Adult {

	private int adultID;
	private int parentID;
	private Adress adress;
	private Communication comu;
	private char moterFather;
	private String name;
	private String firstName;
	private String workCity;
	private String profession;
	
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
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public char getMoterFather() {
		return moterFather;
	}
	public void setMoterFather(char moterFather) {
		this.moterFather = moterFather;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getParentID() {
		return parentID;
	}
	public void setParentID(int parentID) {
		this.parentID = parentID;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}
	public String getWorkCity() {
		return workCity;
	}
	public void setWorkCity(String workCity) {
		this.workCity = workCity;
	}
	
	public int getAdultId(){
		return adultID;
	}
	
	public void setAdultID(int adultID){
		this.adultID = adultID;
	}
	
}
