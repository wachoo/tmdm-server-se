package com.amalto.core.objects.configurationinfo.ejb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;



/**
 * @author bgrieder
 * 
 */
public class ConfigurationInfoPOJO extends ObjectPOJO{
   


	
    private String name;
	private int major;
	private int minor;
	private int revision;
	private String build;
	private String releaseNote = "";
	private String date;
	private HashMap<String, String> properties = new HashMap<String, String>();
    
    
    /**
     * 
     */
    public ConfigurationInfoPOJO() {
    	super();
    }    
	public ConfigurationInfoPOJO(String name) {
		super();
		this.name = name;
	}

	
	/***************************************************************************************
	 *  Bean Stuf
	 ***************************************************************************************/
	
	/**
	 * @return Returns the Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
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

	public HashMap<String, String> getProperties() {
		return properties;
	}

	public void setProperties(HashMap<String, String> properties) {
		this.properties = properties;
	}

	public int getRevision() {
		return revision;
	}

	public void setRevision(int revision) {
		this.revision = revision;
	}
	
	public String getReleaseNote() {
		return releaseNote;
	}
	public void setReleaseNote(String releaseNote) {
		this.releaseNote = releaseNote;
	}
	
	public String getBuild() {
		return build;
	}
	public void setBuild(String build) {
		this.build = build;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	
	
	/***************************************************************************************
	 *  Helpers
	 ***************************************************************************************/
	
	public void setProperty(String key, String value) {
		properties.put(key, value);
	}
	
	public String getProperty(String key) {
		return properties.get(key);
	}

	public String removeProperty(String key) {
		return properties.remove(key);
	}
	
	public Set<String> getPropertyKeys() {
		return new HashSet<String>(properties.keySet());
	}

	public String getVersionString() {
		return major+"."+minor+"."+revision+"_"+build+" "+releaseNote;
	}
	
}
