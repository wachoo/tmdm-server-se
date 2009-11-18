package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.io.File;

import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.util.Misc;

/**
 * @author starkey
 */
public final class WorkflowDeployAgent extends WorkflowAgent {

	public WorkflowDeployAgent() {
		super();
	}

	public void deploy(String barFile,String processId) throws Exception {

		final File file = new File(barFile);
		if (!file.exists()) {
			throw new Exception(
					"The given file : "
							+ file
							+ " does not exist. Are you sure you have generated the bar file ?");
		}
		final ProcessDefinition process = managementAPI.deployBar(
				Misc.getAllContentFrom(file)).get(processId);
		
		

	}
	
	public void deployXpdl(String xpdlFile,String processId) throws Exception {

		final File file = new File(xpdlFile);
		if (!file.exists()) {
			throw new Exception(
					"The given file : "
							+ file
							+ " does not exist. Are you sure you have generated the bar file ?");
		}
		final ProcessDefinition process = managementAPI.deployXpdl(
				Misc.getAllContentFrom(file)).get(processId);
		
		

	}

}
