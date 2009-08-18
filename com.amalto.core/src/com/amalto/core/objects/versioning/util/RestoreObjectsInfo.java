package com.amalto.core.objects.versioning.util;

import java.io.Serializable;

import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;

/**
 * Used to tag objects in Background Jobs
 * @author bgrieder
 *
 */
public class RestoreObjectsInfo implements Serializable {
	private VersioningSystemPOJOPK versioningSystemPOJOPK;
	private String tag;
	private String username;
	private String type;
	private String[] instances;
	
	public RestoreObjectsInfo() {
		super();
	}

	
	
	public RestoreObjectsInfo(VersioningSystemPOJOPK versioningSystemPOJOPK, String tag, String username, String type, String[] instances) {
		super();
		this.versioningSystemPOJOPK = versioningSystemPOJOPK;
		this.tag = tag;
		this.username = username;
		this.type = type;
		this.instances = instances;
	}


	public String[] getInstances() {
		return instances;
	}

	public void setInstances(String[] instances) {
		this.instances = instances;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
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