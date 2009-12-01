package com.amalto.core.ejb;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;

import com.amalto.core.util.XtentisException;


/**
 * @author hshu
 * 
 * @ejb.bean name="WorkflowServiceCtrl" 
 * 		  local-jndi-name = "amalto/local/core/servicectrl"
 *           type="Stateless"
 *           view-type="local"
 *           generate = "false"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 * 
 * @ejb.ejb-ref 
 * 		ejb-name = "WorkflowServiceBean" 
 * 		ref-name = "ejb/WorkflowServiceBean" 
 * 		view-type = "local"
 * 
 */
public abstract class WorkflowServiceCtrlBean extends ServiceCtrlBean implements WorkflowServiceCtrlLocalBI {
  
    
    /**
     * WorkflowServiceCtrlBean.java
     * Constructor
     * 
     */
    public WorkflowServiceCtrlBean() {
        super();
    }
    
    /**
     * deploy
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract ProcessDefinitionUUID deploy(String barFilePath) throws XtentisException;
    /**
     * Get Process Definitions
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract Set<ProcessDefinition> getProcessDefinitions() throws XtentisException;
	
	/**
     * instantiate Process Instance
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public abstract ProcessInstanceUUID instantiateProcessInstance(String processDefinitionId, String processDefinitionVersion) throws XtentisException;
	
	/**
     * Get Process Instance Variables
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID instanceUUID) throws XtentisException;
	/**
	  * Set Process Instance Variable
	  */
	public abstract void setProcessInstanceVariable(ProcessInstanceUUID instanceUUID,String variableName,Object variableValue) throws XtentisException;
	
	/**
	 * Get Task List
	 * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract Collection<TaskInstance> getTaskList(ProcessInstanceUUID instanceUUID,ActivityState state) throws XtentisException;
	
	/**
	 * Start Task
	 * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract void startTask(ActivityInstanceUUID taskUUID) throws XtentisException;
	/**
	 * Finish Task
	 * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract void finishTask(ActivityInstanceUUID taskUUID) throws XtentisException;
	
	/**
	 * Set Activity Instance Variable
	 * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract void setActivityInstanceVariable(ActivityInstanceUUID taskUUID,String variableName,Object variableValue) throws XtentisException;
    
    
}