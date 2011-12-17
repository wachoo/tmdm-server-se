package com.amalto.connector.util;

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
	public static String getVersion(Class<? extends Object> clazz) {
		if (props == null) loadProps(clazz);
		return 
			"v"
			+props.getProperty("major")+"."+props.getProperty("minor")+"."+props.getProperty("rev")+"_"+props.getProperty("build.number")
			+" "+props.getProperty("build.date")
			+" : "+props.getProperty("description");
	}

	// load props as resource on classpath
	private static void loadProps(Class<? extends Object> clazz) {
		InputStream is;
		props = new Properties();
		is = clazz.getResourceAsStream(PROP_FILE);
		if (is == null) {
			throw new RuntimeException("Couldn't find: " + PROP_FILE + " on CLASSPATH");
		}
		try {
			props.load(is);
			is.close();
		} catch (IOException ioe) {
			org.apache.log4j.Logger.getLogger(Version.class).info("ERROR SYSTRACE: "+ioe.getClass().getName()+": "+ioe.getLocalizedMessage(),ioe);
		}
	}

	// never instantiate this class
	private Version() {
	}

}
