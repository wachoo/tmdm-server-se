package com.amalto.core.plugin.base.workflowtrigger.agent;


import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.log4j.Logger;
import org.ow2.bonita.util.SimpleCallbackHandler;

import com.amalto.core.ejb.WorkflowServiceCtrlLocalBI;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

public abstract class WorkflowAgent {
	
	private static final Logger LOG = Logger.getLogger(WorkflowAgent.class.getName());
	
	private LoginContext loginContext=null;
	
	private WorkflowServiceCtrlLocalBI workflowService=null;
	
	protected WorkflowConsole console=null;
	
	
	
	public WorkflowAgent() {
		
	}

	public void start(WorkflowConnectInfo connectInfo) throws LoginException {
		this.login(connectInfo);
		try {
			this.workflowService=Util.getWorkflowService();
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		this.console=new WorkflowConsole();
	}
	
	public void end() throws LoginException {
		logout();
	}
	
	
	private void login(WorkflowConnectInfo connectInfo) throws LoginException {
		//login TODO:jaas of workflow
		loginContext = new LoginContext("Bonita", new SimpleCallbackHandler(connectInfo.getUsername(), connectInfo.getPassword()));
		loginContext.login();

	}
	
	private void logout() throws LoginException {
		loginContext.logout();

	}
	
	
	protected WorkflowServiceCtrlLocalBI getWorkflowService() {
		return workflowService;
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
