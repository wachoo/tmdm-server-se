package com.amalto.service.workflow.ejb;

import java.io.File;
import java.io.Serializable;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.security.auth.login.LoginContext;

import org.exolab.castor.xml.Unmarshaller;
import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition.ProcessState;
import org.ow2.bonita.facade.exception.InstanceNotFoundException;
import org.ow2.bonita.facade.exception.ProcessNotFoundException;
import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ActivityInstanceUUID;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.BusinessArchiveFactory;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.WorkflowServiceCtrlBean;
import com.amalto.core.util.XtentisException;
import com.amalto.service.workflow.bean.WorkflowConfiguration;

/**
 * @author Starkey Shu
 * 
 * @ejb.bean 	name="Workflow"
 *           	display-name="Name for Workflow"
 *           	description="Description for Workflow"
 * 		  		local-jndi-name = "amalto/local/service/workflow"
 *           	type="Stateless"
 *           	view-type="local"
 *              local-business-interface="com.amalto.core.ejb.WorkflowServiceCtrlLocalBI"
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
 * 
 * 
 */
public class WorkflowBean extends WorkflowServiceCtrlBean  implements SessionBean{


	private static final long serialVersionUID = 5379672088510852086L;
	
	private WorkflowConfiguration configuration;
	
	private LoginContext loginContext;
	

	/**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/service/workflow";
	}
	
	
	/**
     * Returns the XML schema for the configuration<br>
     * Can be null
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getConfigurationSchema() throws XtentisException{
		return
		"<xsd:schema" +
		" 		elementFormDefault='unqualified'" +
		"		xmlns:xsd='http://www.w3.org/2001/XMLSchema'" +
		">" +
		"<xsd:element name='svn-configuration'>" +
		"			<xsd:complexType >" +
		"				<xsd:all>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='initial-context-factory' type='xsd:string'/>" +	
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='provider-uRL' type='xsd:string'/>" +
		"					<xsd:element minOccurs='1' maxOccurs='1' nillable='false' name='api-type' type='xsd:string'/>" +
		"				</xsd:all>" +
		"			</xsd:complexType>" +
		"</xsd:element>"+
		"</xsd:schema>";
    }
    
    /**
     * Check Configure
     *  
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public boolean checkConfigure(String conf) throws XtentisException {
    	//TODO
		return true;
	}
    
    /**
     * get the default system configuration
     *
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public String getDefaultConfiguration() throws XtentisException{

    	StringBuffer sb=new StringBuffer();
	    sb.append("<workflow-configuration>\n"); 
	    sb.append("	<initial-context-factory>org.jnp.interfaces.NamingContextFactory</initial-context-factory>\n"); 
	    sb.append("	<provider-uRL>jnp://localhost:1099</provider-uRL>\n"); 
	    sb.append("	<api-type>EJB2</api-type>\n"); 
	    sb.append("</workflow-configuration>");
	    
	    return sb.toString();
    }
	
    /**
     * 
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
    public String getConfiguration(String optionalParameters) throws XtentisException{
       try {
    		
    		String marshalledConfiguration =loadConfiguration();
    		if (marshalledConfiguration == null) {
    			marshalledConfiguration = getDefaultConfiguration();
    		}
    		
	        configuration =  (WorkflowConfiguration)
			Unmarshaller.unmarshal(WorkflowConfiguration.class, new InputSource(new StringReader(marshalledConfiguration)));

    		return marshalledConfiguration;
        } catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the Workflow Service"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * @throws EJBException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void putConfiguration(String marshalledConfiguration) throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("putConfiguration() ");

    	//update the cached configuration
    	try {
    		if (marshalledConfiguration == null) {
    			marshalledConfiguration = getDefaultConfiguration();
    		}
    		org.apache.log4j.Logger.getLogger(this.getClass()).debug("putConfiguration() "+marshalledConfiguration);
    		
	        configuration =  (WorkflowConfiguration)
				Unmarshaller.unmarshal(WorkflowConfiguration.class, new InputSource(new StringReader(marshalledConfiguration)));
	        
	        System.setProperty(BonitaConstants.INITIAL_CONTEXT_FACTORY_PROPERTY,configuration.getInitialContextFactory());
			System.setProperty(BonitaConstants.PROVIDER_URL_PROPERTY,configuration.getProviderURL());
			System.setProperty(BonitaConstants.API_TYPE_PROPERTY, configuration.getApiType());

	    } catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the Workflow Service"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }

	    try {
	    	super.putConfiguration(marshalledConfiguration);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	throw new XtentisException(e.getLocalizedMessage());
	    }

	}

    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getDescription(String twoLetterLanguageCode) throws XtentisException {
		if ("fr".matches(twoLetterLanguageCode.toLowerCase()))
			return "This service handle the basic functionality of workflow";
		return "This service handle the basic functionality of workflow";
	}

    /**
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException{
    	return "Here are the example of parameters:\n\n" +
		"<workflow-configuration>\n"+
		"	<initial-context-factory>org.jnp.interfaces.NamingContextFactory</initial-context-factory>\n"+
		"	<provider-uRL>jnp://localhost:1099</provider-uRL>\n"+
		"	<api-type>EJB2</api-type>\n"+
		"</workflow-configuration>\n";	
    }

    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String getStatus() throws XtentisException {
		return "N/A"; 
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void start() throws XtentisException {
		getConfiguration(null);
		System.setProperty(BonitaConstants.INITIAL_CONTEXT_FACTORY_PROPERTY,configuration.getInitialContextFactory());
		System.setProperty(BonitaConstants.PROVIDER_URL_PROPERTY,configuration.getProviderURL());
		System.setProperty(BonitaConstants.API_TYPE_PROPERTY, configuration.getApiType());
		org.apache.log4j.Logger.getLogger(this.getClass()).info("Workflow service started... ");
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public void stop() throws XtentisException {
		return;
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public Serializable receiveFromOutbound(HashMap<String, Serializable> map) throws XtentisException {
		// N/A
		return null;
	}


    /**
     * @throws EJBException
     * 
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method 
     */
	public String receiveFromInbound(ItemPOJOPK itemPK, String routingOrderID, String parameters) throws XtentisException {

		String err = "The Workflow service does not send messages out at this stage.";
		org.apache.log4j.Logger.getLogger(this.getClass()).error("receiveFromInbound() "+err);
		throw new XtentisException(err);
		
	}

	/**
	 * @throws EJBException
	 *
	 * @ejb.interface-method view-type = "local"
	 * @ejb.facade-method
	 */
	 public Serializable fetchFromOutbound(String command, String parameters,String schedulePlanID) throws XtentisException {
			// TODO We can handle this functions of Workflow
			return null;
	 }
	 
	 /**
	  * Deploy Process Bar
	  * @throws XtentisException 
	  *
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public ProcessDefinitionUUID deploy(String barFilePath) throws XtentisException {

			// deploy the process based on the bar file exported from studio
		    ProcessDefinition process=null;
		    try {
				File businessArchiveFile = new File(barFilePath);
				BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(businessArchiveFile);
				process = AccessorUtil.getAPIAccessor().getManagementAPI().deploy(businessArchive);				
		    } catch (Exception e) {
				throw new XtentisException(e);
			}
		    
			return process.getUUID();

	 }

	 /**
	  * UnDeploy Process 
	  * @throws XtentisException 
	  *
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 @SuppressWarnings("deprecation")
	public void undeploy(ProcessDefinitionUUID uuid) throws XtentisException {


		    try {
		    	List<ProcessDefinitionUUID> l=new ArrayList<ProcessDefinitionUUID>();
		    	l.add(uuid);
		    	AccessorUtil.getAPIAccessor().getManagementAPI().delete(l);			
		    } catch (Exception e) {
				throw new XtentisException(e);
			}
	 }
	 
	 /**
	  * Get Process Definitions
	  * @throws XtentisException 
	  *
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public Set<ProcessDefinition> getProcessDefinitions() throws XtentisException {
		Set<ProcessDefinition> processes=null; 
		try {
			processes=AccessorUtil.getAPIAccessor().getQueryDefinitionAPI().getProcesses(ProcessState.ENABLED);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return processes;
	}
	 
	 
	 
	 /**
	  * Instantiate Process Instance
	  * @throws XtentisException 
	  *
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public ProcessInstanceUUID instantiateProcessInstance(String processDefinitionId, String processDefinitionVersion) throws XtentisException {
		 // instantiation
		ProcessInstanceUUID instanceUUID=null;
		try {
			ProcessDefinition processDefinition = null;
				
			 processDefinition = AccessorUtil.getAPIAccessor().getQueryDefinitionAPI().getProcess(processDefinitionId, processDefinitionVersion);
			 instanceUUID = AccessorUtil.getAPIAccessor().getRuntimeAPI().instantiateProcess(processDefinition.getUUID());
		} catch (ProcessNotFoundException e) {
			throw new XtentisException(e);
		}
		 
		 return instanceUUID;
	 }
	 
	 /**
	  * Get Process Instance Variables
	  * @throws XtentisException 
	  *
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public Map<String, Object> getProcessInstanceVariables(ProcessInstanceUUID instanceUUID) throws XtentisException {
		 
		Map<String, Object> instanceVariables=null;
		try {
			instanceVariables=AccessorUtil.getAPIAccessor().getQueryRuntimeAPI().getProcessInstanceVariables(instanceUUID);
		} catch (InstanceNotFoundException e) {
			throw new XtentisException(e);
		}
		return instanceVariables;

	 }
	 
	 /**
	  * Set Process Instance Variable
	  * @throws XtentisException
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public void setProcessInstanceVariable(ProcessInstanceUUID instanceUUID,String variableName,Object variableValue) throws XtentisException {
		 
		try {
			AccessorUtil.getAPIAccessor().getRuntimeAPI().setProcessInstanceVariable(instanceUUID,variableName,variableValue);
		} catch (Exception e) {
			throw new XtentisException(e);
		}

	 }
	 
	 
	 
	 /**
	  * Get Task List
	  * @throws XtentisException
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public Collection<TaskInstance> getTaskList(ProcessInstanceUUID instanceUUID,ActivityState state) throws XtentisException  {
		 
		Collection<TaskInstance> activities=null;		
		try {
			activities = AccessorUtil.getAPIAccessor().getQueryRuntimeAPI().getTaskList(instanceUUID, state);
			TaskInstance in;			
		} catch (InstanceNotFoundException e) {
			throw new XtentisException(e);
		}
		return activities;

	 }
	 /**
	  * Get Task List
	  * @throws XtentisException
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public Collection<TaskInstance> getTaskList(ProcessInstanceUUID instanceUUID) throws XtentisException  {
		
		Collection<TaskInstance> activities=null;		
		try {
			activities = AccessorUtil.getAPIAccessor().getQueryRuntimeAPI().getTasks(instanceUUID);
			TaskInstance in;			
		} catch (InstanceNotFoundException e) {
			throw new XtentisException(e);
		}
		return activities;

	 }	 
	 /**
	  * Start Task
	  * @throws XtentisException
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public void startTask(ActivityInstanceUUID taskUUID) throws XtentisException  {
		    try {
				AccessorUtil.getAPIAccessor().getRuntimeAPI().startTask(taskUUID, true);				
			} catch (Exception e) {
				throw new XtentisException(e);
			}
	 }
	
	 
	 /**
	  * Finish Task
	  * @throws XtentisException
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public void finishTask(ActivityInstanceUUID taskUUID) throws XtentisException  {
		    try {
				AccessorUtil.getAPIAccessor().getRuntimeAPI().finishTask(taskUUID, true);
			} catch (Exception e) {
				throw new XtentisException(e);
			}
	 }
	 
	 /**
	  * Set Activity Instance Variable
	  * @throws XtentisException
	  *
	  * @ejb.interface-method view-type = "both"
	  * @ejb.facade-method
	  */
	 public void setActivityInstanceVariable(ActivityInstanceUUID taskUUID,String variableName,Object variableValue) throws XtentisException {
		
		try {
			AccessorUtil.getAPIAccessor().getRuntimeAPI().setActivityInstanceVariable(taskUUID,variableName, variableValue);
		} catch (Exception e) {
			throw new XtentisException(e);
		}

	 }


	
	 
	 

	
}