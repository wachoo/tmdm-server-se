package talend.ext.service.entity;

import java.io.Serializable;

import org.apache.commons.lang.StringEscapeUtils;

public class SrvSchedulePlan implements Serializable{
	
	private String schedulePlanId;
	
	private String schedulePlanStatus;
	
	private String schedulePlanDesc;
	
	private String serviceName;
	
	private String methodName;
	
	private String parameters;
	
	private String mode;
	
	public SrvSchedulePlan() {
		super();
	}
	
	public SrvSchedulePlan(String schedulePlanId, String schedulePlanStatus,
			String schedulePlanDesc, String serviceName, String methodName,
			String parameters, String mode) {
		super();
		this.setSchedulePlanId(schedulePlanId);
		this.setSchedulePlanStatus(schedulePlanStatus);
		this.setSchedulePlanDesc(schedulePlanDesc);
		this.setServiceName(serviceName);
		this.setMethodName(methodName);
		this.setParameters(parameters);
		this.setMode(mode);
	}

	public SrvSchedulePlanPK obtainSrvSchedulePlanPk() {
		
		return new SrvSchedulePlanPK(serviceName,methodName);

	}

	public String getSchedulePlanId() {
		return schedulePlanId;
	}

	public void setSchedulePlanId(String schedulePlanId) {
		this.schedulePlanId = schedulePlanId;
	}

	public String getSchedulePlanStatus() {
		return schedulePlanStatus;
	}

	public void setSchedulePlanStatus(String schedulePlanStatus) {
		this.schedulePlanStatus = schedulePlanStatus;
	}

	public String getSchedulePlanDesc() {
		return schedulePlanDesc;
	}

	public void setSchedulePlanDesc(String schedulePlanDesc) {
		this.schedulePlanDesc = schedulePlanDesc;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getMethodName() {
		return methodName;
	}

	public void setMethodName(String methodName) {
		this.methodName = methodName;
	}

	public String getParameters() {
		return StringEscapeUtils.unescapeXml(parameters);
	}

	public void setParameters(String parameters) {
		this.parameters = StringEscapeUtils.escapeXml(parameters);
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

}
