package talend.ext.service.dao;

import java.util.ArrayList;

import com.amalto.core.util.XtentisException;

import talend.ext.service.entity.SrvSchedulePlan;

public interface ServiceSchedulPlanDAO {
	
	public boolean savePlan(SrvSchedulePlan srvSchedulePlan);
	
	public SrvSchedulePlan loadPlan(String serviceSchedulePlanId);
	
	public boolean updatePlanStatus(String srvSchedulePlanID, String schedulePlanStatus);
	
	public boolean updatePlanStatus(SrvSchedulePlan srvSchedulePlan,String schedulePlanStatus);
	
	public ArrayList findAllSchedulingPlans() throws XtentisException;
	
	public ArrayList findAllPlans() throws XtentisException;
	
	public boolean deletePlan(String srvSchedulePlanID);

}
