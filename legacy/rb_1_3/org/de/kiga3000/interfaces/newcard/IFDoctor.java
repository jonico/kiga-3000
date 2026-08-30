package org.de.kiga3000.interfaces.newcard;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.commun.Communication;

public interface IFDoctor extends IFAdressCommunication {
	
	public Adress getAdress();
	public void setAdress(Adress adress);
	public Communication getComu();
	public void setComu(Communication comu);
	public int getDocID();
	public void setDocID(int docID);
	public String getFirstName();
	public void setFirstName(String firstName);
	public String getName();
	public void setName(String name);
	public String getSpecialised();
	public void setSpecialised(String specialised);
	public String getTitle();
	public void setTitle(String title);


}
