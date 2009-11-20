package com.amalto.core.plugin.base.workflowtrigger.agent;

import java.io.File;

import org.ow2.bonita.facade.def.element.BusinessArchive;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.util.BusinessArchiveFactory;

/**
 * @author starkey
 */
public final class WorkflowDeployAgent extends WorkflowAgent {

	public WorkflowDeployAgent() {
		super();
	}

	public void deploy(String barFilePath) throws Exception {

		// deploy the process based on the bar file exported from studio
		File businessArchiveFile = new File(barFilePath);
		BusinessArchive businessArchive = BusinessArchiveFactory.getBusinessArchive(businessArchiveFile);
		ProcessDefinition process = managementAPI.deploy(businessArchive);
		
		console.writeln("Process "+process.getUUID()+" has been deployed! ");

	}

}
