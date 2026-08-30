package org.de.kiga3000.data.child;

import java.util.Date;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.adults.Parents;
import org.de.kiga3000.data.commun.Communication;
import org.de.kiga3000.data.health.HealthCard;

public class Child {
	
	private int cardId;
	private int childId;
	private int parentsId;
	private Adress adress;
	private Communication comu;
	private String name;
	private String firstName;
	private Date geburtsDatum;
	private String religion;
	private String CountryOfOrigin;
	private HealthCard healthCard;
	private Parents parents;
	
	public HealthCard getHealthCard() {
		return healthCard;
	}
	public void setHealthCard(HealthCard healthCard) {
		this.healthCard = healthCard;
	}
	public Adress getAdress() {
		return adress;
	}
	public void setAdress(Adress adress) {
		this.adress = adress;
	}
	public int getChildId() {
		return childId;
	}
	public void setChildId(int childId) {
		this.childId = childId;
	}
	public Communication getComu() {
		return comu;
	}
	public void setComu(Communication comu) {
		this.comu = comu;
	}
	public String getCountryOfOrigin() {
		return CountryOfOrigin;
	}
	public void setCountryOfOrigin(String countryOfOrigin) {
		CountryOfOrigin = countryOfOrigin;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public Date getGeburtsDatum() {
		return geburtsDatum;
	}
	public void setGeburtsDatum(Date geburtsDatum) {
		this.geburtsDatum = geburtsDatum;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getReligion() {
		return religion;
	}
	public void setReligion(String religion) {
		this.religion = religion;
	}
	public int getCardId() {
		return cardId;
	}
	public void setCardId(int cardId) {
		this.cardId = cardId;
	}
	public int getParentsId() {
		return parentsId;
	}
	public void setParentsId(int parentsId) {
		this.parentsId = parentsId;
	}
	
	public Parents getParents() {
		return parents;
	}
	public void setParents(Parents parents) {
		this.parents = parents;
	}
	
	

}
