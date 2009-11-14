package itu.malta.drunkendroidserver.tech;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class databaseConfiguration {
	private static final String BUNDLE_NAME = "itu.malta.drunkendroidserver.util.databaseConnection"; //$NON-NLS-1$

	private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
			.getBundle(BUNDLE_NAME);

	private databaseConfiguration() {
	}

	public static String getString(String key) {
		try {
			return RESOURCE_BUNDLE.getString(key);
		} catch (MissingResourceException e) {
			return '!' + key + '!';
		}
	}
}
