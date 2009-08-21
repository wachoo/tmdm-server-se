package talend.ext.service.handle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import talend.ext.service.dao.ServiceSchedulPlanDAO;
import talend.ext.service.dao.ServiceSchedulePlanDAOImpl;
import talend.ext.service.entity.SrvSchedulePlan;
import talend.ext.service.entity.SrvSchedulePlanStatus;
import talend.ext.service.schedule.SrvScheduleException;
import talend.ext.service.schedule.SrvScheduleManager;
import talend.ext.service.template.ITemplateHandler;
import talend.ext.service.util.AvailableMethodList;
import talend.ext.service.util.CommonUtil;
import talend.ext.service.view.ComboItemBean;

import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.core.bean.ListRange;

public class SrvScheduleDWR {
	
	private static Logger logger = Logger.getLogger(SrvScheduleDWR.class);
	
	public String invokeService(String serviceName, String methodName,String parameters) throws XtentisException {

		parameters=parameters.trim();
		String rtn = SrvScheduleManager.invoke(serviceName, methodName, parameters);

		return rtn;

	}
	
	public ListRange getRuntimeServiceNames() {
		
		ListRange listRange = new ListRange();
		
		List<ComboItemBean> serviceNames=new ArrayList<ComboItemBean>();
		List<String> getServiceNames=Util.getRuntimeServiceJndiList(false);
		for (Iterator<String> iterator = getServiceNames.iterator(); iterator.hasNext();) {
			String serviceName = iterator.next();
			serviceNames.add(new ComboItemBean(serviceName,serviceName));
		}
		
		listRange.setData(serviceNames.toArray());
		listRange.setTotalSize(serviceNames.size());

		return listRange;
	}
	
    public ListRange getAvailableMethodNames() {
		
		ListRange listRange = new ListRange();
		
		List<ComboItemBean> methodNames=new ArrayList<ComboItemBean>();
		List<String> getMethodNames=AvailableMethodList.methods;
		for (Iterator<String> iterator = getMethodNames.iterator(); iterator.hasNext();) {
			String methodName = iterator.next();
			methodNames.add(new ComboItemBean(methodName,methodName));
		}
		
		listRange.setData(methodNames.toArray());
		listRange.setTotalSize(methodNames.size());

		return listRange;
	}
    
    public String loadParameterTemplate(String serviceName,String methodName) throws Exception {
		
    	ITemplateHandler iTemplateHandler = CommonUtil.getMethodTemplateHandler(methodName);
    	return iTemplateHandler.getContent(serviceName);
    	
	}
    
    
    public String getServiceSchedulerStatus() {
    	
    	return SrvScheduleManager.getSrvSchedulerStatus();

	}
    
    public void startServiceScheduler() {
		
    	SrvScheduleManager.startDefaultScheduler();

	}
    
    public void standbyServiceScheduler() {
		
    	SrvScheduleManager.standbyDefaultScheduler();

	}
    
    public void shutdownServiceScheduler() {
		
    	SrvScheduleManager.shutdownDefaultScheduler();

	}
    
    private String[] saveServiceSchedulePlan(SrvSchedulePlan srvSchedulePlan) {
    	
    	String[] rtn={"","",""};
    	
    	ServiceSchedulPlanDAO serviceSchedulPlanDAO=new ServiceSchedulePlanDAOImpl();
    	boolean isSaveOK=serviceSchedulPlanDAO.savePlan(srvSchedulePlan);
    	if(isSaveOK){
    		rtn[0]=srvSchedulePlan.getSchedulePlanId();
    		rtn[1]=srvSchedulePlan.getSchedulePlanStatus();
    		rtn[2]=srvSchedulePlan.getParameters();
    	}
    	
    	return rtn;

	}
    
   public String[] saveServiceSchedulePlan(String schedulePlanId,String schedulePlanStatus,String schedulePlanDesc,String serviceName,String methodName,String parameters,String mode) {
	    
	    SrvSchedulePlan srvSchedulePlan=new SrvSchedulePlan(schedulePlanId,schedulePlanStatus,schedulePlanDesc,serviceName,methodName,parameters,mode);
    
    	return saveServiceSchedulePlan(srvSchedulePlan);

	}
   
   public boolean scheduleServiceSchedulePlan(String serviceSchedulePlanId) throws SrvScheduleException{
	   ServiceSchedulPlanDAO serviceSchedulPlanDAO=new ServiceSchedulePlanDAOImpl();
	   SrvSchedulePlan srvSchedulePlan=serviceSchedulPlanDAO.loadPlan(serviceSchedulePlanId);
	   if(srvSchedulePlan==null)return false;
	   
	   boolean scheduleFlag=SrvScheduleManager.scheduleJob(serviceSchedulePlanId, srvSchedulePlan.getServiceName(), srvSchedulePlan.getMethodName(), srvSchedulePlan.getParameters(), srvSchedulePlan.getMode());
	   if(!scheduleFlag)return false;
	   
	   //UPDATE STATUS IN DB
	   boolean updateFlag=serviceSchedulPlanDAO.updatePlanStatus(srvSchedulePlan, SrvSchedulePlanStatus.SCHEDULING);
	   if(!updateFlag){
		   //roll back
		   SrvScheduleManager.removeJob(serviceSchedulePlanId);
		   return false;
	   }
	   
	   return true;
   
   }
   
   public boolean unScheduleServiceSchedulePlan(String serviceSchedulePlanId) {
	   
	   ServiceSchedulPlanDAO serviceSchedulPlanDAO=new ServiceSchedulePlanDAOImpl();
	   SrvSchedulePlan srvSchedulePlan=serviceSchedulPlanDAO.loadPlan(serviceSchedulePlanId);
	   if(srvSchedulePlan==null)return false;
	   
	   boolean unscheduleFlag=SrvScheduleManager.removeJob(serviceSchedulePlanId);
	   if(!unscheduleFlag)return false;
	   
	   //UPDATE STATUS IN DB
	   boolean updateFlag=serviceSchedulPlanDAO.updatePlanStatus(srvSchedulePlan, SrvSchedulePlanStatus.UNSCHEDULE);
	   if(!updateFlag){
     	   //roll back
		   SrvScheduleManager.scheduleJob(serviceSchedulePlanId,srvSchedulePlan.getServiceName(),srvSchedulePlan.getMethodName(),srvSchedulePlan.getParameters(),srvSchedulePlan.getMode());
		   return false;
	   }
	   
	   return true; 
	   
   }
   
   public ListRange getSchedulePlanList() {
	   
	    ListRange listRange =new ListRange();
	    try {
	      ServiceSchedulPlanDAO serviceSchedulPlanDAO=new ServiceSchedulePlanDAOImpl();
	      
		  List allPlans=serviceSchedulPlanDAO.findAllPlans();
		  Object [] data=new Object[allPlans.size()];
		  for (int i = 0; i < allPlans.size(); i++) {
			  
			  String planXml = (String) allPlans.get(i);
			  SrvSchedulePlan srvSchedulePlan=CommonUtil.convertPlanStrToPOJO(planXml);
			  data[i]=srvSchedulePlan;
		  }
		  
		  listRange.setData(data);
		  listRange.setTotalSize(data.length);
		  
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		return listRange;

   }
   
   public boolean deleteSchedulePlan(String serviceSchedulePlanId){
	   
	   ServiceSchedulPlanDAO serviceSchedulPlanDAO=new ServiceSchedulePlanDAOImpl();
	   return serviceSchedulPlanDAO.deletePlan(serviceSchedulePlanId);
	   
   }
	

}
