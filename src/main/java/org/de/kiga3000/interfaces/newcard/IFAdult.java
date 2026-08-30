package org.de.kiga3000.interfaces.newcard;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.commun.Communication;

public interface IFAdult extends IFAdressCommunication {
	
	public Adress getAdress();
	public void setAdress(Adress adress);
	
	public Communication getComu();
	public void setComu(Communication comu);
	
	public String getFirstName();
	public void setFirstName(String firstName);
	
	public char getMoterFather();
	public void setMoterFather(char moterFather);
	
	public String getName();
	public void setName(String name);
	
	public int getParentID();
	public void setParentID(int parentID);
	
	public String getProfession();
	public void setProfession(String profession);
	
	public String getWorkCity();
	public void setWorkCity(String workCity);
	
	public int getAdultId();
	public void setAdultID(int adultID);

}
