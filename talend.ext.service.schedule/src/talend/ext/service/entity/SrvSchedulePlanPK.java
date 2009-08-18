package talend.ext.service.entity;

import java.io.Serializable;

public class SrvSchedulePlanPK  implements Serializable{
	
	private String serviceName;
	
	private String methodName;

	public SrvSchedulePlanPK(String serviceName,String methodName) {
		super();
		this.methodName = methodName;
		this.serviceName = serviceName;
	}
	
	public String getUniqueId() {
		
		return "Job."+serviceName+"."+methodName+"."+System.currentTimeMillis();

	}
    
}
