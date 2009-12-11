package com.amalto.webapp.v3.workflow.tasks.bean;

import org.ow2.bonita.facade.runtime.TaskInstance;

import com.amalto.webapp.v3.workflow.tasks.util.DateUtil;

public class TaskVO implements Comparable{
	
	private String processName;
	
	private String processVersion;
	
	private long processInstanceNb;
	
	private String processInstanceUUIDValue;//hidden
	
	private String processDefineUUIDValue;//hidden
	
	private String processTag;//show
	
	private String activityName;//show
	
	private String activityState;//show
	
	private String activityReadyDate;//show
	
	private String activityUUIDValue;//hidden
	
	public TaskVO() {
		
	}
	
	public String getProcessName() {
		return processName;
	}

	public void setProcessName(String processName) {
		this.processName = processName;
	}

	public String getProcessVersion() {
		return processVersion;
	}

	public void setProcessVersion(String processVersion) {
		this.processVersion = processVersion;
	}

	public long getProcessInstanceNb() {
		return processInstanceNb;
	}

	public void setProcessInstanceNb(long processInstanceNb) {
		this.processInstanceNb = processInstanceNb;
	}
	
	public String getProcessInstanceUUIDValue() {
		return processInstanceUUIDValue;
	}

	public void setProcessInstanceUUIDValue(String processInstanceUUIDValue) {
		this.processInstanceUUIDValue = processInstanceUUIDValue;
	}
	
	public String getProcessDefineUUIDValue() {
		return processDefineUUIDValue;
	}

	public void setProcessDefineUUIDValue(String processDefineUUIDValue) {
		this.processDefineUUIDValue = processDefineUUIDValue;
	}
	
	public String getProcessTag() {
		StringBuffer sb=new StringBuffer();
		sb.append(processName==null?"":processName.substring(processName.lastIndexOf("_")+1))
		  .append("-")
		  .append(processVersion==null?"":processVersion)
		  .append("-")
		  .append("#"+processInstanceNb);
		processTag=sb.toString();
		return processTag;
	}

	public void setProcessTag(String processTag) {
		this.processTag = processTag;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}

	public String getActivityState() {
		return activityState;
	}

	public void setActivityState(String activityState) {
		this.activityState = activityState;
	}

	public String getActivityReadyDate() {
		return activityReadyDate;
	}

	public void setActivityReadyDate(String activityReadyDate) {
		this.activityReadyDate = activityReadyDate;
	}

	public String getActivityUUIDValue() {
		return activityUUIDValue;
	}

	public void setActivityUUIDValue(String activityUUIDValue) {
		this.activityUUIDValue = activityUUIDValue;
	}

	public void load(TaskInstance task) {
		
		this.setProcessName(task.getProcessInstanceUUID().getProcessDefinitionUUID().getProcessName());
		this.setProcessVersion(task.getProcessInstanceUUID().getProcessDefinitionUUID().getProcessVersion());
		this.setProcessInstanceNb(task.getProcessInstanceUUID().getInstanceNb());
		this.setProcessInstanceUUIDValue(task.getProcessInstanceUUID().getValue());
		this.setProcessDefineUUIDValue(task.getProcessDefinitionUUID().getValue());
		this.setActivityName(task.getActivityName());
		this.setActivityState(task.getState().name());
		this.setActivityReadyDate(DateUtil.convertDateToString(task.getReadyDate()));
		this.setActivityUUIDValue(task.getUUID().getValue());
		
	}


	public int compareTo(Object o) {
		
		 if (o == null) {
			return 0;
		 }
		 
		 if (!o.getClass().equals(this.getClass())) {
			return 0;
		 }
		 
		 TaskVO other=(TaskVO)o;
		 
		 if (this.activityState==null||other.activityState==null) {
				return 0;
		 }
		 
		 if (this.activityReadyDate==null||other.activityReadyDate==null) {
				return 0;
		 }
		 
		 if(!this.activityState.equals(other.activityState)){
		    return other.activityState.compareTo(this.activityState);
		 }else{
			return (other.activityReadyDate+"_"+other.activityUUIDValue).compareTo((this.activityReadyDate+"_"+this.activityUUIDValue));    
		 }
	}
	
}
