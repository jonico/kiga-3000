package org.de.kiga3000.interfaces.newcard;

import java.util.Date;

import org.de.kiga3000.data.adress.Adress;
import org.de.kiga3000.data.adults.Parents;
import org.de.kiga3000.data.commun.Communication;
import org.de.kiga3000.data.health.HealthCard;

public interface IFChild extends IFAdressCommunication {

	public int getCardId();
	public void setCardId(int cardId);
	public int getParentsId();
	public void setParentsId(int parentsId);
	public HealthCard getHealthCard();
	public void setHealthCard(HealthCard healthCard);
	public Adress getAdress();
	public void setAdress(Adress adress);
	public int getChildId();
	public void setChildId(int childId);
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

}
