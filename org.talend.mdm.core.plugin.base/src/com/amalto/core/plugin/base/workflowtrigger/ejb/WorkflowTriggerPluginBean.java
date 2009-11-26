package com.amalto.core.plugin.base.workflowtrigger.ejb;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import javax.ejb.SessionBean;
import javax.security.auth.login.LoginException;
import javax.xml.transform.TransformerException;

import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.BonitaException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerPluginV2CtrlBean;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.plugin.base.workflowtrigger.CompiledParameters;
import com.amalto.core.plugin.base.workflowtrigger.VariableParameter;
import com.amalto.core.plugin.base.workflowtrigger.agent.WorkflowActivityVariableBoxes;
import com.amalto.core.plugin.base.workflowtrigger.agent.WorkflowConnectInfo;
import com.amalto.core.plugin.base.workflowtrigger.agent.WorkflowExecutorAgent;
import com.amalto.core.plugin.base.workflowtrigger.agent.WorkflowProcessPK;
import com.amalto.core.plugin.base.workflowtrigger.agent.WorkflowProcessVariableBox;
import com.amalto.core.plugin.base.workflowtrigger.agent.WorkflowVariableSet;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

/**
 *
 * @author Starkey Shu
 *
 * @ejb.bean name="WorkflowTriggerPluginBean"
 *           display-name="Name for WorkflowTriggerPluginBean"
 *           description="Description for WorkflowTriggerPluginBean"
 * 		     local-jndi-name = "amalto/local/transformer/plugin/workflowtrigger"
 *           type="Stateless"
 *           view-type="local"
 *           local-business-interface="com.amalto.core.objects.transformers.v2.util.TransformerPluginV2LocalInterface"
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
 */
     

public class WorkflowTriggerPluginBean extends TransformerPluginV2CtrlBean  implements SessionBean{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -7151306698676625010L;
	/**
	 * 
	 */
	//parameters
	public static final String PARAMETERS ="com.amalto.core.plugin.workflowtrigger.parameters";
	//various
	private static final String INPUT_PK = "item_primary_key";
	private static final String OUTPUT_TEXT = "execution_result";

	private static final String br ="\n";
	
	
    private transient boolean configurationLoaded = false;

	public WorkflowTriggerPluginBean() {
		super();
		try {
			getConfiguration(null);
		} catch (Exception e) {};
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getJNDIName() throws XtentisException {
		return "amalto/local/transformer/plugin/workflowtrigger";
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDescription(String twoLettersLanguageCode)
			throws XtentisException {
		String description="";
		if(twoLettersLanguageCode.toLowerCase().equals("en")){
			description="Pass an item to a workflow engine";
		}else{
			description="Unsupported language! ";
		}
		return description;
	}

	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getDocumentation(String twoLettersLanguageCode)
			throws XtentisException {
		return
		"Pass an item to a workflow engine. \n" +
		"\n" +
		"\n" +
		"Parameters\n" +
		"	initialContextFactory [mandatory]: the Initial Context Factory "+"\n"+
		"	providerURL [mandatory]: the provider URL "+"\n"+
		"	apiType [mandatory]: the API type "+"\n"+
		//"	packageId [mandatory]: the packageId of the process "+"\n"+
		"	processId [mandatory]: the processId of the process "+"\n"+
		"	processVersion [mandatory]: the processVersion of the process "+"\n"+
		//"	username [mandatory]: the username used to login workflow "+"\n"+
		//"	password [mandatory]: the password used to login workflow "+"\n"+
		"	variable [optional]: the variable which will be used in workflow "+"\n"+
		"		scope: the scope of the variable, 'process' or 'activity' "+"\n"+
		"		activityId [optional]: if the scope is equals to 'activity' "+"\n"+
		"		name: the name of the variable defined in xpdl"+"\n"+
		"		type: the type of variable,you can choose 'String','Boolean' or others " + "\n" + 
		"		fromItem: is the value came from a part of an item, 'true' or 'false' "+"\n"+
		"		xpath [optional]: if the fromItem is equals to 'true' "+"\n"+
		"		value [optional]: if the fromItem is equals to 'false' "+"\n"+
		"\n"+
		"\n"+
		"Example" +"\n"+
		"	<parameters>" +"\n"+
		"		<initialContextFactory>org.jnp.interfaces.NamingContextFactory</initialContextFactory>" +"\n"+
		"		<providerURL>jnp://localhost:1099</providerURL>" +"\n"+
		"		<apiType>EJB2</apiType>" +"\n"+
		//"		<packageId>ApprovalWorkflow</packageId>" +"\n"+
		"		<processId>ApprovalWorkflow</processId>" +"\n"+
		"		<processVersion>1.0</processVersion>" +"\n"+
		//"		<username>admin</username>" +"\n"+
		//"		<password>talend</password>" +"\n"+
		"		<variable>" +"\n"+	
		"			<scope>process</scope>" +"\n"+
		"			<name>SampleItemName</name>" +"\n"+
		"			<type>String</type>" +"\n"+
		"			<fromItem>true</fromItem>" +"\n"+
		"			<xpath>/Customer/Name</xpath>" +"\n"+
		"		</variable>" +"\n"+
		"		<variable>" +"\n"+	
		"			<scope>activity</scope>" +"\n"+
		"			<activityId>Approval</activityId>" +"\n"+
		"			<name>isGranted</name>" +"\n"+
		"			<type>Boolean</type>" +"\n"+
		"			<fromItem>false</fromItem>" +"\n"+
		"			<value>true</value>" +"\n"+
		"		</variable>" +"\n"+
		"	</parameters>"+"\n"+
		"\n"+
		"\n"+
		"Notes for Plugin Developers: " +"\n"+
		"		empty"	;
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public ArrayList<TransformerPluginVariableDescriptor> getInputVariableDescriptors(
			String twoLettersLanguageCode) throws XtentisException {
		ArrayList<TransformerPluginVariableDescriptor> inputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();


		 TransformerPluginVariableDescriptor descriptor1 = new TransformerPluginVariableDescriptor();
		 descriptor1.setVariableName(INPUT_PK);
		 descriptor1.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("(text/xml|application/xtentis.itempk)")
						})
				)
		 );
		 HashMap<String, String> descriptions1 = new HashMap<String, String>();
		 descriptions1.put("en", "The item instance primary key.");
		 descriptor1.setDescriptions(descriptions1);
		 descriptor1.setMandatory(true);
		 descriptor1.setPossibleValuesRegex(null);
		 inputDescriptors.add(descriptor1);


		 return inputDescriptors;
		
	}


	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public ArrayList<TransformerPluginVariableDescriptor> getOutputVariableDescriptors(
			String twoLettersLanguageCode) throws XtentisException {
		ArrayList<TransformerPluginVariableDescriptor> outputDescriptors = new ArrayList<TransformerPluginVariableDescriptor>();

		 //descriptor
		 TransformerPluginVariableDescriptor descriptor = new TransformerPluginVariableDescriptor();
		 descriptor.setVariableName(OUTPUT_TEXT);
		 descriptor.setContentTypesRegex(
				 new ArrayList<Pattern>(
						 Arrays.asList(new Pattern[]{
								 Pattern.compile("text/.*")
						})
				)
		 );
		 HashMap<String, String> descriptions = new HashMap<String, String>();
		 descriptions.put("en", "The execution report of workflow");
		 descriptor.setDescriptions(descriptions);
		 descriptor.setMandatory(true);
		 descriptor.setPossibleValuesRegex(null);
		 outputDescriptors.add(descriptor);

		 return outputDescriptors;
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String getParametersSchema() throws XtentisException {
		//TODO
		return null;

	}

	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public String compileParameters(String parameters) throws XtentisException {
		try {
					
			if(parameters==null||parameters.length()==0)return "";
			
    		CompiledParameters compiled = new CompiledParameters();
    		Element params = Util.parse(parameters).getDocumentElement();
    		
    		//mandatory case
			String initialContextFactory = Util.getFirstTextNode(params, "initialContextFactory");
			if (initialContextFactory==null||initialContextFactory.length()==0) {
				String err = "The initialContextFactory parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setInitialContextFactory(initialContextFactory);
    		
    		String providerURL = Util.getFirstTextNode(params, "providerURL");
			if (providerURL==null||providerURL.length()==0) {
				String err = "The providerURL parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setProviderURL(providerURL);
    		
    		String apiType = Util.getFirstTextNode(params, "apiType");
			if (apiType==null||apiType.length()==0) {
				String err = "The apiType parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setApiType(apiType);
    		
//    		String packageId = Util.getFirstTextNode(params, "packageId");
//			if (packageId==null||packageId.length()==0) {
//				String err = "The packageId parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
//				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
//				throw new XtentisException(err);
//			}
//    		compiled.setPackageId(packageId);
    		
    		String processId = Util.getFirstTextNode(params, "processId");
			if (processId==null||processId.length()==0) {
				String err = "The processId parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setProcessId(processId);
    		
    		String processVersion = Util.getFirstTextNode(params, "processVersion");
			if (processVersion==null||processVersion.length()==0) {
				String err = "The processVersion parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
				throw new XtentisException(err);
			}
    		compiled.setProcessVersion(processVersion);
    		
//    		String username = Util.getFirstTextNode(params, "username");
//			if (username==null||username.length()==0) {
//				String err = "The username parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
//				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
//				throw new XtentisException(err);
//			}
            //compiled.setUsername("jack");
    		
//    		String password = Util.getFirstTextNode(params, "password");
//			if (password==null||password.length()==0) {
//				String err = "The password parameter of the WorkflowTriggerPluginBean Transformer Plugin cannot be empty";
//				org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
//				throw new XtentisException(err);
//			}
    		//FIXME:BAD HARD CODE
    		//compiled.setPassword("bpm");
    		
    		//optional case
    		//TODO check dependency
    		List<VariableParameter> variableParameterList=new ArrayList<VariableParameter>();
			NodeList variableNodeList = Util.getNodeList(params, "//variable");
			for (int i=0; i<variableNodeList.getLength(); i++) {
				String varscope = Util.getFirstTextNode(variableNodeList.item(i), "scope");
				String varactivityId = Util.getFirstTextNode(variableNodeList.item(i), "activityId");
				String varname = Util.getFirstTextNode(variableNodeList.item(i), "name");
				String vartype = Util.getFirstTextNode(variableNodeList.item(i), "type");
				String tmpfromItem = Util.getFirstTextNode(variableNodeList.item(i), "fromItem");
				boolean varfromItem = (tmpfromItem!=null&&tmpfromItem.equals("true"));
				String varxpath = Util.getFirstTextNode(variableNodeList.item(i), "xpath");
				String varvalue = Util.getFirstTextNode(variableNodeList.item(i), "value");
				
				VariableParameter variableParameter=new VariableParameter(varscope,varactivityId,varname,vartype,varfromItem,varxpath,varvalue);
				variableParameterList.add(variableParameter);
			}
			
    		if(variableParameterList.size()>0){
    			compiled.setVariableParameters(variableParameterList.toArray(new VariableParameter[variableParameterList.size()]));
    		}
    		
    		return compiled.serialize();
    		
    	} catch (XtentisException e) {
    		throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to serialize the configuration of the WorkflowTrigger Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
	}

	

	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void init(TransformerPluginContext context, String compiledParameters)
			throws XtentisException {

		try {
			if (!configurationLoaded) loadConfiguration();

			//parse parameters
			CompiledParameters parameters=CompiledParameters.deserialize(compiledParameters);
			String userToken=context.getTransformerGlobalContext().getUserToken();
			if(userToken!=null){
				String[] tokens=userToken.split("/");  
				parameters.setUsername(tokens[0]);
	            parameters.setPassword(tokens[1]);
			}
            

			context.put(PARAMETERS, parameters);
			
		} catch (XtentisException xe) {
			throw (xe);
		} catch (Exception e) {
			String err = "Could not init the WorkflowTrigger plugin:"+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
		}
		
	}
	
	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void execute(TransformerPluginContext context)
			throws XtentisException {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() WorkflowTrigger");	
		try {
			
			CompiledParameters parameters= (CompiledParameters)context.get(PARAMETERS);
			TypedContent inputPKTC = (TypedContent)context.get(INPUT_PK);

			//Get the Key and content
			ItemPOJOPK pk =  ItemPOJOPK.unmarshal(new String(inputPKTC.getContentBytes(),"UTF8"));
			org.apache.log4j.Logger.getLogger(this.getClass()).debug("execute() PK "+pk);

			ItemPOJO item=Util.getItemCtrl2Local().getItem(pk);
			Element itemElement=item.getProjection();

			//now perform workflow trigger
			WorkflowConnectInfo workflowConnectInfo=new WorkflowConnectInfo(
					parameters.getInitialContextFactory(),
					parameters.getProviderURL(),
					parameters.getApiType(),
					parameters.getUsername(),
					parameters.getPassword());
			
			
			//execute
			WorkflowExecutorAgent workflowExecutorAgent =new WorkflowExecutorAgent();
			
			//parse parameter
			WorkflowProcessVariableBox workflowProcessVariableBox =new WorkflowProcessVariableBox();
			WorkflowActivityVariableBoxes workflowActivityVariableBoxes=new WorkflowActivityVariableBoxes();
			
			//set build-in parameters
			InetAddress addr = InetAddress.getLocalHost();
			String ip=addr.getHostAddress().toString();
			String portUrl = "http://"+ip+":8080/talend/TalendPort";
			workflowProcessVariableBox.addVariable("MDM_url", portUrl, WorkflowVariableSet.STRING_TYPE);
			
			String universeName=LocalUser.getLocalUser().getUniverse().getName();
			if(universeName.equals("[HEAD]"))universeName="";
			workflowProcessVariableBox.addVariable("MDM_universe", universeName, WorkflowVariableSet.STRING_TYPE);
			
			String dataClusterName=pk.getDataClusterPOJOPK().getUniqueId();
			workflowProcessVariableBox.addVariable("MDM_dataCluster", dataClusterName, WorkflowVariableSet.STRING_TYPE);
			
			String dataModelName=item.getDataModelName();
			workflowProcessVariableBox.addVariable("MDM_dataModel", dataModelName, WorkflowVariableSet.STRING_TYPE);
			
			//set custom parameters
			VariableParameter[] variableParameters=parameters.getVariableParameters();
			if(variableParameters!=null&&variableParameters.length>0){
				for (int i = 0; i < variableParameters.length; i++) {
					VariableParameter variableParameter=variableParameters[i];
					if(variableParameter.getScope().equals("process")){
						workflowProcessVariableBox.addVariable(variableParameter.getName(), getVariableValue(itemElement,variableParameter), variableParameter.getType());
						//set dual xpath var
						if(variableParameter.getXpath()!=null&&variableParameter.getXpath().length()>0){
							workflowProcessVariableBox.addVariable(variableParameter.getName()+"_xpath", variableParameter.getXpath(), WorkflowVariableSet.STRING_TYPE);
						}
					}else if(variableParameter.getScope().equals("activity")){
						workflowActivityVariableBoxes.addVariable(variableParameter.getActivityId(), variableParameter.getName(), getVariableValue(itemElement,variableParameter), variableParameter.getType());
						//set dual xpath var
						if(variableParameter.getXpath()!=null&&variableParameter.getXpath().length()>0){
							workflowActivityVariableBoxes.addVariable(variableParameter.getActivityId(),variableParameter.getName()+"_xpath", variableParameter.getXpath(), WorkflowVariableSet.STRING_TYPE);
						}
					}
				}
			}
			
			workflowExecutorAgent.start(workflowConnectInfo);
			ProcessInstanceUUID processInstanceUUID=
			workflowExecutorAgent.execute(new WorkflowProcessPK(parameters.getPackageId(),
					                                            parameters.getProcessId(),
					                                            parameters.getProcessVersion()),
						                                        workflowProcessVariableBox,                     
						                                        workflowActivityVariableBoxes
						                      );
			workflowExecutorAgent.end();
			

			//save result to context
			context.put(OUTPUT_TEXT, new TypedContent(workflowExecutorAgent.getConsole().getContent().getBytes(),"text/xml; charset=utf-8"));
			//call the callback content is ready
			context.getPluginCallBack().contentIsReady(context);
			

		} catch (XtentisException xe) {
			throw (xe);
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (BonitaException e) {
			e.printStackTrace();
		} catch (Exception e) {
			String err = "Could not execute the WorkflowTrigger plugin "+
				e.getClass().getName()+": "+e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(e);
		}
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("execute() WorkflowTrigger done");
	}

	private String getVariableValue(Element itemElement,
			VariableParameter variableParameter) throws TransformerException {
		String value=null;
		if(variableParameter.isFromItem()){
			String xpath=variableParameter.getXpath();
			if(!xpath.startsWith("/")&&!xpath.startsWith("//"))xpath="/"+xpath;
			value=Util.getFirstTextNode(itemElement,xpath);
		}else{
			value=variableParameter.getValue();
		}
		return value;
	}


	
	private String getDefaultConfiguration(){
    	return
    		"<configuration>"+
    		"		<initialContextFactory>org.jnp.interfaces.NamingContextFactory</initialContextFactory>" +"\n"+
    		"		<providerURL>jnp://localhost:1099</providerURL>" +"\n"+
    		"		<apiType>EJB2</apiType>" +"\n"+
    		//"		<packageId>ApprovalWorkflow</packageId>" +"\n"+
    		"		<processId>ApprovalWorkflow</processId>" +"\n"+
    		"		<processVersion>1.0</processVersion>" +"\n"+
    		//"		<username>admin</username>" +"\n"+
    		//"		<password>talend</password>" +"\n"+
    		"		<variable>" +"\n"+	
    		"			<scope>process</scope>" +"\n"+
    		"			<name>SampleItemName</name>" +"\n"+
    		"			<type>String</type>" +"\n"+
    		"			<fromItem>true</fromItem>" +"\n"+
    		"			<xpath>/Customer/Name</xpath>" +"\n"+
    		"		</variable>" +"\n"+
    		"		<variable>" +"\n"+	
    		"			<scope>activity</scope>" +"\n"+
    		"			<activityId>Approval</activityId>" +"\n"+
    		"			<name>isGranted</name>" +"\n"+
    		"			<type>Boolean</type>" +"\n"+
    		"			<fromItem>false</fromItem>" +"\n"+
    		"			<value>true</value>" +"\n"+
    		"		</variable>" +"\n"+
			"</configuration>";
    }

	/**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
    public String getConfiguration(String optionalParameters) throws XtentisException{
    	try {
    		String configuration = loadConfiguration();
    		if (configuration == null) {
    			configuration = getDefaultConfiguration();
    		}
    		configurationLoaded = true;
    		return configuration;
        } catch (XtentisException e) {
    		throw (e);
	    } catch (Exception e) {
    	    String err = "Unable to deserialize the configuration of the BatchProject Transformer Plugin"
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Category.getInstance(this.getClass()).error(err);
    	    throw new XtentisException(err);
	    }
    }

    /**
     * @throws XtentisException
     *
     * @ejb.interface-method view-type = "local"
     * @ejb.facade-method
     */
	public void putConfiguration(String configuration) throws XtentisException {
		configurationLoaded = false;
		super.putConfiguration(configuration);
	}

	


}