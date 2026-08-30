package org.de.kiga3000.views;


import java.util.ResourceBundle;

/**
 * @author bobohead2
 * 
 */
public class Configuration {

	private static ResourceBundle _resourceBundle = ResourceBundle
			.getBundle("org.de.kiga3000.properties.Config");

	/**
	 * Get a message from the resource bundle
	 * 
	 * @param messageKey
	 *            Key for the message to be retrieved.
	 * @return The message text.
	 */
	public String getConfig(String configKey) {
		return (_resourceBundle.getString(configKey));
	}

}
