package com.amalto.core.plugin.base.workflowtrigger.agent;

import javax.security.auth.login.LoginException;

import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.ow2.bonita.util.BonitaException;

import com.amalto.core.util.XtentisException;

public class WorkflowTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		WorkflowAgent workflowAgent=null;
		
		WorkflowConnectInfo workflowConnectInfo=new WorkflowConnectInfo(
				"org.jnp.interfaces.NamingContextFactory",
				"jnp://localhost:1099",
				"EJB2",
				"jack",
				"bpm");
		
		
		//deploy
//		workflowAgent=new WorkflowDeployAgent();
//		WorkflowDeployAgent workflowDeployAgent=(WorkflowDeployAgent)workflowAgent;
//		try {
//			workflowDeployAgent.start(workflowConnectInfo);
//			workflowDeployAgent.deploy("E:/IDE/eclipse-talend-mdm/eclipse-3.4/tom/bonitaExample/bars/approvalWorkflow.bar");
//			workflowDeployAgent.end();
//		} catch (LoginException e) {
//			e.printStackTrace();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
		
		//execute
		workflowAgent=new WorkflowExecutorAgent();
		WorkflowExecutorAgent workflowExecutorAgent =(WorkflowExecutorAgent)workflowAgent;
		
		WorkflowProcessVariableBox workflowProcessVariableBox =new WorkflowProcessVariableBox();
//		workflowProcessVariableBox.addVariable("SampleItemId", "1", "String");
//		workflowProcessVariableBox.addVariable("SampleItemName", "SampleItem", "String");
		
		WorkflowActivityVariableBoxes workflowActivityVariableBoxes=new WorkflowActivityVariableBoxes();
		workflowActivityVariableBoxes.addVariable("Approval", "isGranted", "true", "Boolean");
		
		try {
			workflowExecutorAgent.start(workflowConnectInfo);
			ProcessInstanceUUID processInstanceUUID=
			workflowExecutorAgent.execute(new WorkflowProcessPK("ApprovalWorkflow1",
					                                            "ApprovalWorkflow",
					                                            "1.0"),
					                                            workflowProcessVariableBox,                     
					                                            workflowActivityVariableBoxes
					                      );
			//workflowExecutorAgent.cleanPackage(processInstanceUUID);
			workflowExecutorAgent.end();
		} catch (LoginException e) {
			e.printStackTrace();
		} catch (BonitaException e) {
			e.printStackTrace();
		} catch (XtentisException e) {
			e.printStackTrace();
		}
		
		//print
		System.out.println(workflowAgent.getConsole().getContent());
		
	}

}
