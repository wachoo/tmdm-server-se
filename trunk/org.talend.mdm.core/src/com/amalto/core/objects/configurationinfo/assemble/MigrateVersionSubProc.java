package com.amalto.core.objects.configurationinfo.assemble;

import javax.naming.InitialContext;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;

public class MigrateVersionSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
		//perform upgrades
		
		boolean autoupgrade = "true".equals(MDMConfiguration.getConfiguration().getProperty(
				"system.data.auto.upgrade", 
				"true"
			));
		if(autoupgrade){
			try {
				ConfigurationInfoCtrlLocal ctrl =  ((ConfigurationInfoCtrlLocalHome)new InitialContext().lookup(ConfigurationInfoCtrlLocalHome.JNDI_NAME)).create();
				ctrl.autoUpgrade();
			} catch (Exception e) {
				org.apache.log4j.Logger.getLogger(this.getClass()).error("Migrate error! ");
			}
		}
		
	}

}
