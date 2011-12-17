package com.amalto.webapp.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {
	static Properties props;

	private static final String PROP_FILE = "/version.properties";

	/**
	 * Returns <code>String</code> representation of package version
	 * information.
	 */
	public static String getVersion(Class<?> clazz) {
		loadProps(clazz);
		return "v" + props.getProperty("major") + "."
				+ props.getProperty("minor") + "." + props.getProperty("rev")
				+ "_" + props.getProperty("build.number") + " "
				+ props.getProperty("build.date") + " : "
				+ props.getProperty("description");
	}


	// load props as resource on classpath
	private static void loadProps(Class<?> clazz) {
		InputStream is;
		props = new Properties();
		is = clazz.getResourceAsStream(PROP_FILE);
		if (is == null) {
			throw new RuntimeException("Couldn't find: " + PROP_FILE
					+ " on CLASSPATH");
		}
		try {
			props.load(is);
			is.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
		}
	}

	// never instantiate this class
	private Version() {
	}

}
