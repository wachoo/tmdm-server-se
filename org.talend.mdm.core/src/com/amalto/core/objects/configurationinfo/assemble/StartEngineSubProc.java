package com.amalto.core.objects.configurationinfo.assemble;

import javax.naming.InitialContext;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocalHome;

public class StartEngineSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
		//autoUpgrade completed - start the routing engine
    	//Start Routing Engine
		boolean autostart = "true".equals(MDMConfiguration.getConfiguration().getProperty(
			"subscription.engine.autostart", 
			"true"
		));
		
		if (autostart) {
			
        	RoutingEngineV2CtrlLocal enginectrl = null;
			try {
				enginectrl = ((RoutingEngineV2CtrlLocalHome)new InitialContext().lookup(RoutingEngineV2CtrlLocalHome.JNDI_NAME)).create();
			} catch (Exception e) {
				String err = "Auto Configuration in the background completed but Unable to start the routing Engine";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
				throw new RuntimeException(err);					
			}
        	enginectrl.start();
        	org.apache.log4j.Logger.getLogger(this.getClass()).info("Routing Engine has been started! ");
        
		}
		
	}

}
