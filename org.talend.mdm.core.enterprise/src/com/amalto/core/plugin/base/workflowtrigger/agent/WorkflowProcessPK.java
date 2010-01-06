package com.amalto.core.plugin.base.workflowtrigger.agent;

public class WorkflowProcessPK {
	
	private String packageId;
	private String processId;
	private String processVersion;
	
	public WorkflowProcessPK() {
		
	}
	
	
	public WorkflowProcessPK(String packageId, String processId,
			String processVersion) {
		super();
		this.packageId = packageId;
		this.processId = processId;
		this.processVersion = processVersion;
	}

	public String getPackageId() {
		return packageId;
	}
	public void setPackageId(String packageId) {
		this.packageId = packageId;
	}
	public String getProcessId() {
		return processId;
	}
	public void setProcessId(String processId) {
		this.processId = processId;
	}
	public String getProcessVersion() {
		return processVersion;
	}
	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}
	
	

}
