package org.de.kiga3000.data.adress;

public class Adress {
	
	private int cardID;
	private int adrID;
	private String street;
	private String pobox;
	private String city;
	private String region;
	private String country;
	
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPobox() {
		return pobox;
	}
	public void setPobox(String pobox) {
		this.pobox = pobox;
	}
	public String getRegion() {
		return region;
	}
	public void setRegion(String region) {
		this.region = region;
	}
	public String getStreet() {
		return street;
	}
	public void setStreet(String street) {
		this.street = street;
	}
	public int getCardID() {
		return cardID;
	}
	public void setCardID(int cardID) {
		this.cardID = cardID;
	}
	public int getAdrID() {
		return adrID;
	}
	public void setAdrID(int adrID) {
		this.adrID = adrID;
	}
	
	

}
