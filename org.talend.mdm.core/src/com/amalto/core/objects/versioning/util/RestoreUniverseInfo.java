package com.amalto.core.objects.versioning.util;

import java.io.Serializable;

import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;

/**
 * Used to tag universe in Background Jobs
 * @author starkey
 *
 */
public class RestoreUniverseInfo implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 24228458444514441L;
	
	private VersioningServiceCtrlLocalBI service;
	private VersioningSystemPOJOPK versioningSystemPOJOPK;
	private String tag;
	private String username;
	private String[] encodedClusterNames;
	
	public RestoreUniverseInfo() {
		super();
	}

	
	
	public RestoreUniverseInfo(VersioningServiceCtrlLocalBI service,VersioningSystemPOJOPK versioningSystemPOJOPK, String tag, String username, String[] encodedClusterNames) {
		super();
		this.service = service;
		this.versioningSystemPOJOPK = versioningSystemPOJOPK;
		this.tag = tag;
		this.username = username;
		this.encodedClusterNames = encodedClusterNames;
	}

	
	public VersioningServiceCtrlLocalBI getService() {
		return service;
	}

	public void setService(VersioningServiceCtrlLocalBI service) {
		this.service = service;
	}

	

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	

	public String[] getEncodedClusterNames() {
		return encodedClusterNames;
	}



	public void setEncodedClusterNames(String[] encodedClusterNames) {
		this.encodedClusterNames = encodedClusterNames;
	}



	public VersioningSystemPOJOPK getVersioningSystemPOJOPK() {
		return versioningSystemPOJOPK;
	}

	public void setVersioningSystemPOJOPK(
			VersioningSystemPOJOPK versioningSystemPOJOPK) {
		this.versioningSystemPOJOPK = versioningSystemPOJOPK;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}
	
}