package com.amalto.core.objects.configurationinfo.assemble;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.util.Util;



public class StartServiceSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
    	//workflow service
		
		if (Util.isEnterprise()) {
			
			Object workflowService= 
				com.amalto.core.util.Util.retrieveComponent(
					null, 
					"amalto/local/service/workflow"
				);
			
			com.amalto.core.util.Util.getMethod(workflowService, "start").invoke(
					workflowService,
					new Object[] {}
				);
			
		}
		
	}
	

}
