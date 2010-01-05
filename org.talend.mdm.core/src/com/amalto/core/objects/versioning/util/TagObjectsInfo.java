package com.amalto.core.objects.versioning.util;

import java.io.Serializable;

import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;

/**
 * Used to tag objects in Background Jobs
 * @author bgrieder
 *
 */
public class TagObjectsInfo implements Serializable {
	private VersioningSystemPOJOPK versioningSystemPOJOPK;
	private VersioningServiceCtrlLocalBI service;
	private String tag;
	private String comment;
	private String username;
	private String type;
	private String[] instances;
	
	public TagObjectsInfo() {
		super();
	}

	
	
	public TagObjectsInfo(VersioningSystemPOJOPK versioningSystemPOJOPK,VersioningServiceCtrlLocalBI service, String tag, String comment, String username, String type, String[] instances) {
		super();
		this.versioningSystemPOJOPK = versioningSystemPOJOPK;
		this.service = service;
		this.tag = tag;
		this.comment = comment;
		this.username = username;
		this.type = type;
		this.instances = instances;
	}



	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
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
	

	public VersioningServiceCtrlLocalBI getService() {
		return service;
	}


	public void setService(VersioningServiceCtrlLocalBI service) {
		this.service = service;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}
	
}