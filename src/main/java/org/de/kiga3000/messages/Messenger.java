/**
 * created 22.10.2005 14:42:15
 *
 */
package org.de.kiga3000.messages;

import java.util.Locale;
import java.util.ResourceBundle;

/**
 * @author bobohead2
 * 
 */
public class Messenger {

	private static ResourceBundle _resourceBundle = ResourceBundle
			.getBundle("org.de.kiga3000.properties.Messages", Locale.UK); //Locale.getDefault());

	/**
	 * Get a message from the resource bundle
	 * 
	 * @param messageKey
	 *            Key for the message to be retrieved.
	 * @return The message text.
	 */
	public String getMessage(String messageKey) {
		return (_resourceBundle.getString(messageKey));
	}

	/**
	 * Get a parameterized text message from the resource bundle.
	 * 
	 * @param messageKey
	 *            Key for the message to be retrieved.
	 * @param replacementOne
	 *            The replacement string to insert in the raw message text.
	 * @return The message text.
	 */
	public String getMessage(String messageKey, String replacementOne) {
		String resourceKey = _resourceBundle.getString(messageKey);
		return (resourceKey.replaceAll("\\{1}", replacementOne));
	}

	/**
	 * Get a parameterized text message from the resource bundle.
	 * 
	 * @param messageKey
	 *            Key for the message to be retrieved.
	 * @param replacementOne
	 *            The first replacement string to insert in the raw message
	 *            text.
	 * @param replacementTwo
	 *            The second replacement string to insert in the raw message
	 *            text.
	 * @return The message text.
	 */
	public String getMessage(String messageKey, String replacementOne,
			String replacementTwo) {
		String resourceKey = _resourceBundle.getString(messageKey);
		resourceKey = resourceKey.replaceAll("\\{1}", replacementOne);
		return (resourceKey.replaceAll("\\{2}", replacementTwo));
	}

	/**
	 * Get a parameterized text message from the resource bundle.
	 * 
	 * @param messageKey
	 *            Key for the message to be retrieved.
	 * @param replacementOne
	 *            The first replacement string to insert in the raw message
	 *            text.
	 * @param replacementTwo
	 *            The second replacement string to insert in the raw message
	 *            text.
	 * @param replacementThree
	 *            The third replacement string to insert in the raw message
	 *            text.
	 * @return The message text.
	 */
	public String getMessage(String messageKey, String replacementOne,
			String replacementTwo, String replacementThree) {
		String resourceKey = _resourceBundle.getString(messageKey);
		resourceKey = resourceKey.replaceAll("\\{1}", replacementOne);
		resourceKey = resourceKey.replaceAll("\\{2}", replacementTwo);
		return (resourceKey.replaceAll("\\{3}", replacementThree));
	}
}
