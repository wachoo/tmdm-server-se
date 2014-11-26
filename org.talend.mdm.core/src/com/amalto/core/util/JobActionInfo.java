package com.amalto.core.util;

import java.io.Serializable;

import com.amalto.core.objects.universe.UniversePOJO;

/**
 * @author bgrieder
 *
 */
public class JobActionInfo  implements Serializable {
	String jobId="";
	String action="";
	Serializable info;
	UniversePOJO universe;
	String userToken;
	
	public JobActionInfo() {
		super();
	}
	
	public JobActionInfo(String jobId, UniversePOJO universe, String action, Serializable info) {
		super();
		this.jobId = jobId;
		this.universe = universe;
		this.action = action;
		this.info = info;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public UniversePOJO getUniverse() {
    	return universe;
    }
	public void setUniverse(UniversePOJO universe) {
    	this.universe = universe;
    }
	public String getJobId() {
		return jobId;
	}
	public void setJobId(String jobId) {
		this.jobId = jobId;
	}
	public Serializable getInfo() {
		return info;
	}
	public void setInfo(Serializable info) {
		this.info = info;
	}

	public String getUserToken() {
		return userToken;
	}

	public void setUserToken(String userToken) {
		this.userToken = userToken;
	}
	
}
