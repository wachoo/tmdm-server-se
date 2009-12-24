package com.amalto.webapp.v3.workflow.tasks.dwr;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;
import org.ow2.bonita.facade.def.majorElement.DataFieldDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.SimpleCallbackHandler;

import com.amalto.core.ejb.WorkflowServiceCtrlLocalBI;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.v3.workflow.tasks.bean.DataFieldDefinitionVO;
import com.amalto.webapp.v3.workflow.tasks.bean.DataFieldValueVO;
import com.amalto.webapp.v3.workflow.tasks.bean.NameValueCouple;
import com.amalto.webapp.v3.workflow.tasks.bean.TaskVO;
import com.amalto.webapp.v3.workflow.tasks.schema.BPMSchemaManager;
import com.amalto.webapp.v3.workflow.tasks.schema.VAnnotationInfo;
import com.amalto.webapp.v3.workflow.tasks.util.DateUtil;


public class WorkflowTasksDWR {
	
	private static WorkflowServiceCtrlLocalBI workflowService=null;
	
	private static Logger logger = Logger.getLogger(WorkflowTasksDWR.class);
	
	private static final String BONITA_LOGIN_CONTEXT="Bonita_Login_Context";

	public WorkflowTasksDWR() {

	}

	private WorkflowServiceCtrlLocalBI getWorkflowService() {
		if(workflowService==null){
			try {
				workflowService=Util.getWorkflowService();
			} catch (XtentisException e) {
				e.printStackTrace();
			}
		}
		return workflowService;
	}
	
	/**
	 * @throws LoginException
	 * 
	 * login
	 */
	public void doubleLogin() throws LoginException {
		
		String userToken=Util.getUsernameAndPasswordToken();
		String[] userTokenParts=userToken.split("/");
		String username=userTokenParts[0];
		String password=userTokenParts.length>1?userTokenParts[1]:"";
		
		LoginContext loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(username, password));
		loginContext.login();
		
		WebContext ctx = WebContextFactory.get();
		ctx.getSession().setAttribute(BONITA_LOGIN_CONTEXT,loginContext);
		logger.info(username+" logged in Bonita Domain successfully! ");
		
	}
	
	public void logout() throws LoginException {
		
		WebContext ctx = WebContextFactory.get();
		if(ctx.getSession().getAttribute(BONITA_LOGIN_CONTEXT)!=null){
			String username=getLoggedUser();
			LoginContext loginContext = (LoginContext) ctx.getSession().getAttribute(BONITA_LOGIN_CONTEXT);
			loginContext.logout();
			
			logger.info(username+" logged out from Bonita Domain successfully! ");
		}

	}
	
	public String getLoggedUser() {
		String loggedUser = AccessorUtil.getAPIAccessor().getManagementAPI().getLoggedUser();
		return loggedUser;
	}
	
    /**
     * @param start
     * @param limit
     * @param sort
     * @param dir
     * @param regex
     * @return
     * @throws Exception
     */
    public ListRange getTasksPrintList(int start, int limit,String sort,String dir,String regex)throws Exception{
		
		ListRange listRange = new ListRange();
				
		List<TaskVO> tasksList=getTasksList();
		List<TaskVO> tasksSubList=new ArrayList<TaskVO>();
		
		//sublist
		if(tasksList.size()>0){
			int toIndex=Math.min(start+limit, tasksList.size());
			tasksSubList=tasksList.subList(start, toIndex);
		}
		
		listRange.setData(tasksSubList.toArray(new TaskVO[tasksSubList.size()]));
		listRange.setTotalSize(tasksList.size());
		
		return listRange;
	}

	/**
	 * @return
	 * 
	 * Get Tasks List
	 */
	public List<TaskVO> getTasksList() {
		
		List<TaskVO> taskVOs=new ArrayList<TaskVO>();
		Collection<TaskInstance> toDoTasks = getToDoTasks();
		Collection<TaskInstance> doneTasks = getDoneTasks();
		
		if (toDoTasks!=null&&!toDoTasks.isEmpty()) {
			for (TaskInstance task : toDoTasks) {
				TaskVO taskVO=new TaskVO();
				taskVO.load(task);
				taskVOs.add(taskVO);
			}
		}
		
		if (doneTasks!=null&&!doneTasks.isEmpty()) {
			for (TaskInstance task : doneTasks) {
				TaskVO taskVO=new TaskVO();
				taskVO.load(task);
				taskVOs.add(taskVO);
			}
		}
		
		//sort
		Collections.sort(taskVOs);
		
		return taskVOs;
    }

	private Collection<TaskInstance> getToDoTasks() {
		Collection<TaskInstance> tasks = null;
		try {
			tasks = getWorkflowService().getTaskList(getLoggedUser(), ActivityState.READY);
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		return tasks;
	}
	
	private Collection<TaskInstance> getDoneTasks() {
		Collection<TaskInstance> tasks = null;
		try {
			tasks = getWorkflowService().getTaskList(getLoggedUser(), ActivityState.FINISHED);
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		return tasks;
	}
	
	/**
	 * Get Data Fields
	 * 
	 * @param activityUUIDValue
	 * @return
	 */
	public Set<DataFieldDefinitionVO> getDataFields(String processDefineUUIDValue,String processInstanceUUIDValue,String activityUUIDValue,String language) {
		
         Set<DataFieldDefinitionVO> dataFields=new TreeSet<DataFieldDefinitionVO>();
       
		 try {
			 
			Set<DataFieldDefinition> processDataFields=getWorkflowService().getProcessDataFields(new ProcessDefinitionUUID(processDefineUUIDValue));
			Set<DataFieldDefinition> activityDataFields=getWorkflowService().getActivityDataFields(new ActivityInstanceUUID(activityUUIDValue));
			
			if(processDataFields!=null&&!processDataFields.isEmpty()){
				for (Iterator<DataFieldDefinition> iterator = processDataFields.iterator(); iterator.hasNext();) {
					DataFieldDefinition dataFieldDefinition = iterator.next();
					DataFieldDefinitionVO dataFieldDefinitionVO=new DataFieldDefinitionVO();
					dataFieldDefinitionVO.load(dataFieldDefinition, false, processInstanceUUIDValue, activityUUIDValue);
					dataFields.add(dataFieldDefinitionVO);
				}
			}
			if(activityDataFields!=null&&!activityDataFields.isEmpty()){
				for (Iterator<DataFieldDefinition> iterator = activityDataFields.iterator(); iterator.hasNext();) {
					DataFieldDefinition dataFieldDefinition = iterator.next();
					DataFieldDefinitionVO dataFieldDefinitionVO=new DataFieldDefinitionVO();
					dataFieldDefinitionVO.load(dataFieldDefinition, true, processInstanceUUIDValue, activityUUIDValue);
					dataFields.add(dataFieldDefinitionVO);
				}
			}
			
			
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		
		//analyzing schema
		analyzeSchema4GettingDataFields(processDefineUUIDValue,processInstanceUUIDValue, dataFields,language);
		
		return dataFields;

	}

	private void analyzeSchema4GettingDataFields(
			String processDefineUUIDValue,
			String processInstanceUUIDValue,
			Set<DataFieldDefinitionVO> dataFields,
			String language) {
		try {
			
			
			Map<String, Object> processDataFieldsValues = getWorkflowService().getProcessInstanceVariables(new ProcessInstanceUUID(processInstanceUUIDValue));
			
			String dataModelPK=(String) processDataFieldsValues.get("MDM_dataModel");
			Map<String,String> xpathVariablesMap=new HashMap<String, String>();
			
			for (Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
				DataFieldDefinitionVO dataFieldDefinitionVO = (DataFieldDefinitionVO) iterator.next();
				if(dataFieldDefinitionVO.getName().matches(".*_xpath$")){
					xpathVariablesMap.put(dataFieldDefinitionVO.getName(), (String) processDataFieldsValues.get(dataFieldDefinitionVO.getName()));
				}
			}
			
			Map<String, VAnnotationInfo> annotationMap = BPMSchemaManager.parseAnnotationMapOnAConcept(dataModelPK, xpathVariablesMap);
			
			for (Iterator iterator = dataFields.iterator(); iterator.hasNext();) {
				DataFieldDefinitionVO dataFieldDefinitionVO = (DataFieldDefinitionVO) iterator.next();
				dataFieldDefinitionVO.setLabel(dataFieldDefinitionVO.getName());
				if(annotationMap.containsKey(dataFieldDefinitionVO.getName())){
					VAnnotationInfo vAnnotationInfo=annotationMap.get(dataFieldDefinitionVO.getName());
					if(vAnnotationInfo!=null){
						String accessRightToken=vAnnotationInfo.getAccessRightToken();
						Map<String,String> labelMap=vAnnotationInfo.getLabelMap();
						if(language==null||language.length()==0)language="en";
						language=language.toUpperCase();
						if(labelMap!=null&&labelMap.get(language)!=null&&labelMap.get(language).length()>0)dataFieldDefinitionVO.setLabel(labelMap.get(language));
						
						//accessRightToken
						if(accessRightToken!=null&&accessRightToken.length()>0){
							String[] parts=accessRightToken.split("#");
							String definedRoleName=parts[0];
							String definedProcessID=parts[1];
							String definedRight=parts[2];
							
							ProcessDefinitionUUID processDefinitionUUID=new ProcessDefinitionUUID(processDefineUUIDValue);
							String thisProcessID=processDefinitionUUID.getProcessName()+"_"+processDefinitionUUID.getProcessVersion();
							HashSet<String> thisRoles=LocalUser.getLocalUser().getRoles();
							
							if(thisProcessID.equals(definedProcessID)&&thisRoles.contains(definedRoleName)){
								if(definedRight.equals("Read-only")){
									dataFieldDefinitionVO.setReadonly(true);
								}else if(definedRight.equals("Hidden")){
									dataFieldDefinitionVO.setHidden(true);
								}
							}
						}
						
					}
				}
				
			}	
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
    /**
     * Get Data Fields Values
     * 
     * @param processInstanceUUIDValue
     * @param activityUUIDValue
     * @return
     */
    public Set<NameValueCouple> getDataFieldsValues(String processInstanceUUIDValue,String activityUUIDValue) {
    	
    	Map<String, Object> dataFieldsValues=new HashMap<String, Object>();
    	try {
	    	Map<String, Object> processDataFieldsValues = getWorkflowService().getProcessInstanceVariables(new ProcessInstanceUUID(processInstanceUUIDValue));	
	    	Map<String, Object> activityDataFieldsValues=getWorkflowService().getActivityInstanceVariables(new ActivityInstanceUUID(activityUUIDValue));
	    	if(processDataFieldsValues!=null&&!processDataFieldsValues.isEmpty())dataFieldsValues.putAll(processDataFieldsValues);
	    	if(activityDataFieldsValues!=null&&!activityDataFieldsValues.isEmpty())dataFieldsValues.putAll(activityDataFieldsValues);
    	} catch (XtentisException e) {
			e.printStackTrace();
		}
    	
    	//2 set
    	Set<NameValueCouple> fieldValues=new HashSet<NameValueCouple>();
    	for (Iterator<String> iterator = dataFieldsValues.keySet().iterator(); iterator.hasNext();) {
    		String varName = iterator.next();
    		Object varValue=dataFieldsValues.get(varName);
			String svarValue=obj2String(varValue);//convert
			fieldValues.add(new NameValueCouple(varName,svarValue));
		}

		return fieldValues;

	}
    
    private String obj2String(Object input) {
		String output="";
    	if(input!=null){
    		if(input instanceof Date){
    			output=DateUtil.convertDateToString((Date) input);
    		}else {
    			output=input.toString();
    		}
    	}
    	return output;
	}
    
 
    private void updateDataFieldsValues(String processInstanceUUIDValue,String activityUUIDValue,Map<String,DataFieldValueVO> dataFieldsValues) throws XtentisException {
    	
			for (Iterator<String> iterator = dataFieldsValues.keySet().iterator(); iterator.hasNext();) {
				String dataFieldName = iterator.next();
				DataFieldValueVO dataFieldValueVO = dataFieldsValues.get(dataFieldName);
				if(dataFieldValueVO.getIsActivitVariable()!=null&&dataFieldValueVO.getIsActivitVariable().equals("true")){
					getWorkflowService().setActivityInstanceVariable(new ActivityInstanceUUID(activityUUIDValue), 
                            dataFieldValueVO.getName(), 
                            parseType(dataFieldValueVO.getDataTypeClassName(),dataFieldValueVO.getValue()));
				}else{
					getWorkflowService().setProcessInstanceVariable(new ProcessInstanceUUID(processInstanceUUIDValue), 
                            dataFieldValueVO.getName(), 
                            parseType(dataFieldValueVO.getDataTypeClassName(),dataFieldValueVO.getValue()));
				}
			}
			

	}
    
    private Object parseType(String variableType,String variableValue) {
		Object toSetVariableValue=null;
		
		//TODO support date here
		if(variableType.equals(String.class.getName())){
			toSetVariableValue=variableValue;
		}else if(variableType.equals(Long.class.getName())){
			if(variableValue!=null)toSetVariableValue=Long.valueOf(variableValue);
		}else if(variableType.equals(Double.class.getName())){
			if(variableValue!=null)toSetVariableValue=Double.valueOf(variableValue);
		}else if(variableType.equals(Boolean.class.getName())){
			if(variableValue!=null)toSetVariableValue=Boolean.valueOf(variableValue);
		}else if(variableType.equals(Date.class.getName())){
			if(variableValue!=null){
				try {
					toSetVariableValue=DateUtil.convertStringToDate(variableValue);
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}else{//default
			toSetVariableValue=variableValue;
		}
		
		return toSetVariableValue;
	}
    
   
    /**
     * submit a task
     * 
     * @param processInstanceUUIDValue
     * @param activityUUIDValue
     * @param dataFieldsValues
     * @throws XtentisException
     */
    public void submit(String processInstanceUUIDValue,String activityUUIDValue,Map<String,DataFieldValueVO> dataFieldsValues) throws XtentisException {
    	//getWorkflowService().startTask(new ActivityInstanceUUID(activityUUIDValue));
    	updateDataFieldsValues(processInstanceUUIDValue,activityUUIDValue,dataFieldsValues);
    	getWorkflowService().executeTask(new ActivityInstanceUUID(activityUUIDValue));
    	logger.info("Task "+activityUUIDValue+" finished! ");
	}
    

}
