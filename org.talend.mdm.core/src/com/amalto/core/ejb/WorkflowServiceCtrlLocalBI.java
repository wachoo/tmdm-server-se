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

import com.amalto.core.util.ServiceCtrlLocalBI;
import com.amalto.core.util.XtentisException;


public interface WorkflowServiceCtrlLocalBI extends ServiceCtrlLocalBI{
	
	public static final long serialVersionUID = 1L;
	
	   /**
	    * Returns the XML schema for the configuration<br>
	    * Can be null
	    *  
	    */
	   public String getConfigurationSchema() throws XtentisException;


	   /**
	    * checkup the svn configuration
	    * @throws XtentisException
	    *
	    */
	    public boolean checkConfigure(String conf)throws com.amalto.core.util.XtentisException;

	    /**
	     * @author achen
	     * @throws XtentisException
	     */
	    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException;
	    	
	  
	    /**
	     * return default the configuration<br>
	     * Can be null
	     */
	    public String getDefaultConfiguration() throws XtentisException;    	
	
	/**
     * deploy
     * 
     */
	public abstract ProcessDefinitionUUID deploy(String barFilePath) throws XtentisException;
  
	/**
     * undeploy
     * 
     */
	public abstract void undeploy(ProcessDefinitionUUID uuid) throws XtentisException;	
	/**
     * Get Process Definitions
     * 
     */
	public abstract Set<ProcessDefinition> getProcessDefinitions() throws XtentisException;
	
    /**
     * instantiate Process Instance
     * 
     */
	public abstract ProcessInstanceUUID instantiateProcessInstance(String processDefinitionId, String processDefinitionVersion) throws XtentisException;
	
	/**
     * Get Process Instance Variables
     * 
     */
	public abstract Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID instanceUUID) throws XtentisException;
	/**
	  * Set Process Instance Variable
	  */
	public abstract void setProcessInstanceVariable(ProcessInstanceUUID instanceUUID,String variableName,Object variableValue) throws XtentisException;
	
	/**
	  * Get Task List
	  */
	public abstract Collection<TaskInstance> getTaskList(ProcessInstanceUUID instanceUUID,ActivityState state) throws XtentisException;

	/**
	 * Get Task List
	 * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
	public abstract Collection<TaskInstance> getTaskList(ProcessInstanceUUID instanceUUID) throws XtentisException;		
	/**
	  * Start Task
	  */
	public abstract void startTask(ActivityInstanceUUID taskUUID) throws XtentisException;
	/**
	  * Finish Task
	  */
	public abstract void finishTask(ActivityInstanceUUID taskUUID) throws XtentisException;
	
	/**
	  * Set Activity Instance Variable
	  */
	public abstract void setActivityInstanceVariable(ActivityInstanceUUID taskUUID,String variableName,Object variableValue) throws XtentisException;
}