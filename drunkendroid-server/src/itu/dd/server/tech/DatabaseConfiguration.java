package itu.dd.server.tech;

import java.util.MissingResourceException;
import java.util.ResourceBundle;
/**
 * CLass used for getting the externalised connection string.
 */
public class DatabaseConfiguration {
	private static final String BUNDLE_NAME = "itu.malta.drunkendroidserver.util.databaseConnection"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private DatabaseConfiguration() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
