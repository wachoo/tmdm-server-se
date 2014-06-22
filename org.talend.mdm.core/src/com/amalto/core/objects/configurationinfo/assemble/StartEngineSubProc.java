package com.amalto.core.objects.configurationinfo.assemble;

import com.amalto.core.server.RoutingEngine;
import com.amalto.core.util.Util;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;

public class StartEngineSubProc extends AssembleSubProc{

    private final static Logger LOGGER = Logger.getLogger(StartEngineSubProc.class);

	@Override
	public void run() throws Exception {
		//autoUpgrade completed - start the routing engine
    	//Start Routing Engine
		boolean autostart = "true".equals(MDMConfiguration.getConfiguration().getProperty(
			"subscription.engine.autostart", 
			"true"
		));
		if (autostart) {
            RoutingEngineV2CtrlLocal routingEngine;
			try {
				routingEngine = Util.getRoutingEngineV2CtrlLocal();
			} catch (Exception e) {
				String err = "Auto Configuration in the background completed but Unable to start the routing Engine";
				LOGGER.error(err,e);
				throw new RuntimeException(err, e);
			}
        	routingEngine.start();
        	LOGGER.info("Routing Engine has been started! ");
		}
	}

}
