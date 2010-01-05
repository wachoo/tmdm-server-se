package com.amalto.core.objects.configurationinfo.assemble;



public class StartServiceSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
    	//workflow service
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
