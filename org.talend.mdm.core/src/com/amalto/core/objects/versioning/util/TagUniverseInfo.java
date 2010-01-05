package com.amalto.core.objects.versioning.util;

import java.io.Serializable;
import java.util.Map;

import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;


public class TagUniverseInfo implements Serializable {
	private VersioningSystemPOJOPK versioningSystemPOJOPK;
	private VersioningServiceCtrlLocalBI service;
	private String tag;
	private String comment;
	private String username;
	private Map<String,String[]> typeInstances;
	
	public TagUniverseInfo() {
		super();
	}

	
	
	public TagUniverseInfo(VersioningSystemPOJOPK versioningSystemPOJOPK,VersioningServiceCtrlLocalBI service, String tag, String comment, String username,  Map<String,String[]> typeInstances) {
		super();
		this.versioningSystemPOJOPK = versioningSystemPOJOPK;
		this.service = service;
		this.tag = tag;
		this.comment = comment;
		this.username = username;
		this.typeInstances = typeInstances;
	}



	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public Map<String, String[]> getTypeInstances() {
		return typeInstances;
	}


	public void setTypeInstances(Map<String, String[]> typeInstances) {
		this.typeInstances = typeInstances;
	}

	public String getTag() {
		return tag;
	}

	public void setTag(String tag) {
		this.tag = tag;
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