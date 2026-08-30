package org.de.kiga3000.control.communicator;

import java.text.ParseException;

import org.de.kiga3000.check.ErrorProtocoll;
import org.de.kiga3000.conversion.DateConversion;
import org.de.kiga3000.data.card.KigaCard;
import org.de.kiga3000.interfaces.newcard.IFKigaCard;
import org.de.kiga3000.messages.Messenger;

public class KarteiCardCommunicator {
	/*
	 * Utility to convert input date to database date and vise versa
	 */
	private DateConversion convDate = new DateConversion();

	/*
	 * Error Protocoll object
	 */
	private ErrorProtocoll err = new ErrorProtocoll();

	/*
	 * Message Object which delivers internationalized messages
	 */
	private static Messenger messenger = new Messenger();

	public void transferDataToView(IFKigaCard view, KigaCard karte)
			throws ParseException {
		view.setAdditionalInfo(karte.getAdditionalInfo());
		view.setCardID(karte.getCardID());
		view.setChild(karte.getChild());
		view.setGroup(karte.getGroup());
		try {
			view.setEntryDate(convDate.StringToLocalDate(karte.getEntryDate()));
		} catch (ParseException excp) {
			err.addError(err.typeError, view.getEntryDate());
			throw excp;
		}
		try {
			view.setLeavingDate(convDate.StringToLocalDate(karte
					.getLeavingDate()));
		} catch (ParseException excp) {
			err.addError(err.typeError, view.getLeavingDate());
			throw excp;
		}

	}

}
