package com.amalto.core.jobox;

import java.io.Serializable;

public class JobInvokeConfig implements Serializable{
	
    private String jobName=null;
	
	private String jobVersion=null;
	
	private String jobMainClass=null;

	public String getJobName() {
		return jobName;
	}

	public void setJobName(String jobName) {
		this.jobName = jobName;
	}

	public String getJobVersion() {
		return jobVersion;
	}

	public void setJobVersion(String jobVersion) {
		this.jobVersion = jobVersion;
	}

	public String getJobMainClass() {
		return jobMainClass;
	}

	public void setJobMainClass(String jobMainClass) {
		this.jobMainClass = jobMainClass;
	}


}
