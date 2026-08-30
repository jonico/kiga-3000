package org.de.kiga3000.interfaces.newcard;

import java.util.Date;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.adults.Parents;
import org.de.kiga3000.data.commun.Communication;
import org.de.kiga3000.data.health.HealthCard;

public interface IFChildAndHealth extends IFHealthCard {
	public int getChildId();
	public void setChildId(int childId);
	public int getParentsId();
	public void setParentsId(int parentsId);
	public int getCardID();
	public void setCardID(int cardID);
	public int getChildComuID();
	public void setComuID(int comuID);

	public HealthCard getHealthCard();
	public void setHealthCard(HealthCard healthCard);
	public Adress getAdress();
	public void setAdress(Adress adress);
	public Communication getComu();
	public void setComu(Communication comu);
	public String getCountryOfOrigin();
	public void setCountryOfOrigin(String countryOfOrigin);
	public String getFirstName();
	public void setFirstName(String firstName);
	public Date getGeburtsDatum();
	public void setGeburtsDatum(Date geburtsDatum);
	public String getName();
	public void setName(String name);
	public String getReligion();
	public void setReligion(String religion);
	public Parents getParents();
	public void setParents(Parents parents);

	
	public String getCity(); 
	public void setCity(String city);
	
	public String getChildCountry();
	public void setChildCountry(String country);
	
	public String getChildPobox();
	public void setChildPobox(String pobox);
	
	public String getChildRegion();
	public void setChildRegion(String region);
	
	public String getChildStreet();
	public void setChildStreet(String street);

	public String getChildEmail();
	public void setChilEmail(String email);
	
	public String getChildMobilPhone();
	public void setChildMobilPhone(String mobilPhone);
	
	public String getChildPager();
	public void setChildPager(String pager);
	
	public String getChildPrivFax();
	public void setChildPrivFax(String privFax);
	
	public String getvPrivPhone();
	public void setChildPrivPhone(String privPhone);
	
	public String getChildWorkFax();
	public void setChildWorkFax(String workFax);
	
	public String getChildWorkPhone();
	public void setChildWorkPhone(String workPhone);
	

}
