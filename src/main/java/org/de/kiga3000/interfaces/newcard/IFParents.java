package org.de.kiga3000.interfaces.newcard;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.adults.Adult;
import org.de.kiga3000.data.commun.Communication;

public interface IFParents {
	
	public byte getAuthorizedCaregiver();
	public void setAuthorizedCaregiver(byte authorizedCaregiver);
	public int getFamilyID();
	public void setFamilyID(int familyID);
	public byte getFamilystatus();
	public void setFamilystatus(byte familystatus);
	public Adult getFather();
	public void setFather(Adult father);
	public Adult getMother();
	public void setMother(Adult mother);
	public Adult getAuthCareGiver();
	public void setAuthCareGiver(Adult authCareGiver);
	public int getParentID();
	public void setParentID(int parentID);


	public Adress getFatherAdress();
	public void setFatherAdress(Adress adress);
	
	public Communication getFatherComu();
	public void setFatherComu(Communication comu);
	
	public String getFatherFirstName();
	public void setFatherFirstName(String firstName);
	
	public String getFatherName();
	public void setFatherName(String name);
	
	public int getFatherParentID();
	public void setFatherParentID(int parentID);
	
	public String getFatherProfession();
	public void setFatherProfession(String profession);
	
	public String getFatherWorkCity();
	public void setFatherWorkCity(String workCity);
	
	public int getFatherAdultId();
	public void setFatherAdultID(int adultID);

	public String getFatherCity(); 
	public void setFatherCity(String city);
	
	public String getFatherCountry();
	public void setFatherCountry(String country);
	
	public String getFatherPobox();
	public void setFatherPobox(String pobox);
	
	public String getFatherRegion();
	public void setFatherRegion(String region);
	
	public String getFatherStreet();
	public void setFatherStreet(String street);
	
	public int getFatherCardID();
	public void setFatherCardID(int cardID);
	
	public int getFatherAdrID();
	public void setFatherAdrID(int adrID);

	public String getFatherEmail();
	public void setFatherEmail(String email);
	
	public String getFatherMobilPhone();
	public void setFatherMobilPhone(String mobilPhone);
	
	public String getFatherPager();
	public void setFatherPager(String pager);
	
	public String getFatherPrivFax();
	public void setFatherPrivFax(String privFax);
	
	public String getFatherPrivPhone();
	public void setFatherPrivPhone(String privPhone);
	
	public String getFatherWorkFax();
	public void setFatherWorkFax(String workFax);
	
	public String getFatherWorkPhone();
	public void setFatherWorkPhone(String workPhone);
	
	public int getFatherComuID();
	public void setFatherComuID(int comuID);


	public Adress getMotherAdress();
	public void setMotherAdress(Adress adress);
	
	public Communication getMotherComu();
	public void setMotherComu(Communication comu);
	
	public String getMotherFirstName();
	public void setMotherFirstName(String firstName);
	
	public String getMotherName();
	public void setMotherName(String name);
	
	public int getMotherParentID();
	public void setMotherParentID(int parentID);
	
	public String getMotherProfession();
	public void setMotherProfession(String profession);
	
	public String getMotherWorkCity();
	public void setMotherWorkCity(String workCity);
	
	public int getMotherAdultId();
	public void setMotherAdultID(int adultID);

	public String getMotherCity(); 
	public void setMotherCity(String city);
	
	public String getMotherCountry();
	public void setMotherCountry(String country);
	
	public String getMotherPobox();
	public void setMotherPobox(String pobox);
	
	public String getMotherRegion();
	public void setMotherRegion(String region);
	
	public String getMotherStreet();
	public void setMotherStreet(String street);
	
	public int getMotherCardID();
	public void setMotherCardID(int cardID);
	
	public int getMotherAdrID();
	public void setMotherAdrID(int adrID);

	public String getMotherEmail();
	public void setMotherEmail(String email);
	
	public String getMotherMobilPhone();
	public void setMotherMobilPhone(String mobilPhone);
	
	public String getMotherPager();
	public void setMotherPager(String pager);
	
	public String getMotherPrivFax();
	public void setMotherPrivFax(String privFax);
	
	public String getMotherPrivPhone();
	public void setMotherPrivPhone(String privPhone);
	
	public String getMotherWorkFax();
	public void setMotherWorkFax(String workFax);
	
	public String getMotherWorkPhone();
	public void setMotherWorkPhone(String workPhone);
	
	public int getMotherComuID();
	public void setMotherComuID(int comuID);
}
