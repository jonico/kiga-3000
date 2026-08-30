package org.de.kiga3000.interfaces.newcard;

import org.de.kiga3000.data.health.Doctor;

public interface IFHealthCard extends IFDoctor{

	public String getAdditionalIllnesses();
	public void setAdditionalIllnesses(String additionalIllnesses);
	public int getHealthChildID();
	public void setHealthChildID(int childID);
	public String getHealthInfo();
	public void setHealthInfo(String healthInfo);
	public String getHealthInsurance();
	public void setHealthInsurance(String healthInsurance);
	public int getHealtID();
	public void setHealtID(int healtID);
	public byte getIllnesses();
	public void setIllnesses(byte illnesses);
	public String getJaundice();
	public void setJaundice(String jaundice);
	public String getLastTetanusShot();
	public void setLastTetanusShot(String lastTetanusShot);
	public Doctor getDoctor();
	public void setDoctor(Doctor doctor);


}
