package com.amalto.core.objects.configurationinfo.assemble;

import com.amalto.core.util.Util;
import org.apache.log4j.Logger;


public class StartServiceSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
    	//workflow service
		
		if (Util.isEnterprise()) {
			
			Object workflowService= 
				com.amalto.core.util.Util.retrieveComponent(
                        "amalto/local/service/workflow"
				);

            try {
                Util.getMethod(workflowService, "start").invoke(
                        workflowService,
                        new Object[] {}
                    );
            } catch (Exception e) {
                Logger.getLogger(StartServiceSubProc.class).error("Could not start workflow service.", e);
            }
        }
		
	}
	

}
