package com.amalto.core.plugin.base.workflowtrigger.agent;


import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.ow2.bonita.facade.CommandAPI;
import org.ow2.bonita.facade.ManagementAPI;
import org.ow2.bonita.facade.QueryDefinitionAPI;
import org.ow2.bonita.facade.QueryRuntimeAPI;
import org.ow2.bonita.facade.RuntimeAPI;
import org.ow2.bonita.util.AccessorUtil;
import org.ow2.bonita.util.BonitaConstants;
import org.ow2.bonita.util.SimpleCallbackHandler;

public abstract class WorkflowAgent {
	
	private static final Logger LOG = Logger.getLogger(WorkflowAgent.class.getName());
	
	private  LoginContext loginContext=null;
	
	protected RuntimeAPI runtimeAPI = null;
	protected QueryRuntimeAPI queryRuntimeAPI = null;
	protected ManagementAPI managementAPI = null;
	protected QueryDefinitionAPI queryDefinitionAPI = null;
	protected CommandAPI commandAPI = null;
	
	protected WorkflowConsole console=null;
	
	public WorkflowAgent() {
		
	}

	public void start(WorkflowConnectInfo connectInfo) throws LoginException {
		this.initProperties(connectInfo);
		this.login(connectInfo);
		this.prepareAPI();
		
		this.console=new WorkflowConsole();
	}
	
	public void end() throws LoginException {
		logout();
	}
	
	private void initProperties(WorkflowConnectInfo connectInfo) {
		// init 
//		String jaasLoginProp = "java.security.auth.login.config";
//		if (System.getProperty(jaasLoginProp) == null) {
//			  String defaultJaasLogin = this.getClass().getResource("jaas-jboss.cfg").getPath();
//			  System.setProperty(jaasLoginProp, defaultJaasLogin);
//			  info("No jaas login property (" + jaasLoginProp + ") has been defined. Using by default : " + defaultJaasLogin + "");
//		}
		
		System.setProperty(BonitaConstants.INITIAL_CONTEXT_FACTORY_PROPERTY,connectInfo.getInitialContextFactory());
		System.setProperty(BonitaConstants.PROVIDER_URL_PROPERTY,connectInfo.getProviderURL());
		System.setProperty(BonitaConstants.API_TYPE_PROPERTY, connectInfo.getApiType());

	}
	
	private void login(WorkflowConnectInfo connectInfo) throws LoginException {
		//login TODO:jaas of workflow
		loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(connectInfo.getUsername(), connectInfo.getPassword()));
		loginContext.login();

	}
	
	private void logout() throws LoginException {
		loginContext.logout();

	}
	
	private void prepareAPI() {
		// get APIs
		runtimeAPI = AccessorUtil.getAPIAccessor().getRuntimeAPI();
		queryRuntimeAPI = AccessorUtil.getAPIAccessor().getQueryRuntimeAPI();
		managementAPI = AccessorUtil.getAPIAccessor().getManagementAPI();
		queryDefinitionAPI = AccessorUtil.getAPIAccessor().getQueryDefinitionAPI();
		commandAPI = AccessorUtil.getAPIAccessor().getCommandAPI();
	}
	
	protected void info(String msg) {
		if (LOG.isInfoEnabled()) {
			LOG.info(msg);
		}
	}

	public WorkflowConsole getConsole() {
		return console;
	}	

}
