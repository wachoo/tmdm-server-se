package com.amalto.core.objects.configurationinfo.assemble;

import org.talend.mdm.server.api.RoutingEngine;
import com.amalto.core.util.Util;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

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
            RoutingEngine routingEngine;
            try {
                routingEngine = Util.getRoutingEngineV2CtrlLocal();
                routingEngine.start();
                LOGGER.info("Routing engine started.");
            } catch (Exception e) {
                String err = "Auto Configuration in the background completed but unable to start the routing engine.";
                LOGGER.error(err);
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug(err, e);
                }
            }
		}
	}

}
