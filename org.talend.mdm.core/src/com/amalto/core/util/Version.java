package com.amalto.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Version {
	static Properties props;

	private static final String PROP_FILE = "/product_version.properties";
	
	
	/**
	 * Returns a <code>Version</code> object holding the package version implementation
	 */
	public static Version getVersion(Class clazz) {
		if (props == null) loadProps(clazz);
		return 
			new Version(
					Integer.parseInt(props.getProperty("major")),
					Integer.parseInt(props.getProperty("minor")),
					Integer.parseInt(props.getProperty("rev")),
					Integer.parseInt(props.getProperty("build.number")),
					props.getProperty("description"),
					props.getProperty("build.date")
			);
	}

	/**
	 * Returns <code>String</code> representation of package version
	 * information.
	 */
	public static String getVersionAsString(Class clazz) {
		if (props == null) loadProps(clazz);
		return 
			"v"
			+props.getProperty("major")+"."+props.getProperty("minor")+"."+props.getProperty("rev")+"_"+props.getProperty("build.number")
			+" "+props.getProperty("build.date")
			+" : "+props.getProperty("description");
	}
	
	public static String getSimpleVersionAsString(Class clazz) {
		if (props == null) loadProps(clazz);
		return 
			"v"
			+props.getProperty("major")+"."+props.getProperty("minor")+"."+props.getProperty("rev")+"_"+props.getProperty("build.number");
	}

	// load props as resource on classpath
	private static void loadProps(Class clazz) {
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
			ioe.printStackTrace();
		}
	}

	@Override
	/**
	 * Returns a Version String with Release Notes
	 * @return the Version String
	 */
	public String toString() {
		return 
		"v"
		+props.getProperty("major")+"."+props.getProperty("minor")+"."+props.getProperty("rev")+"_"+props.getProperty("build.number")
		+" "+props.getProperty("build.date")
		+" : "+((props.getProperty("description")==null||props.getProperty("description").length()==0)?"[no description]":props.getProperty("description"));
	}



	/*********************************************************************************
	 * Bean Implementation
	 *********************************************************************************/
	
	private int major;
	private int minor;
	private int revision;
	private int build;
	private String description;
	private String date;
	
	private Version() {
	}
	
	public Version(int major, int minor, int revision, int build, String description, String date) {
		super();
		this.major = major;
		this.minor = minor;
		this.revision = revision;
		this.build = build;
		this.description = description;
		this.date = date;
	}

	public int getBuild() {
		return build;
	}

	public void setBuild(int build) {
		this.build = build;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getMajor() {
		return major;
	}

	public void setMajor(int major) {
		this.major = major;
	}

	public int getMinor() {
		return minor;
	}

	public void setMinor(int minor) {
		this.minor = minor;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}

}
