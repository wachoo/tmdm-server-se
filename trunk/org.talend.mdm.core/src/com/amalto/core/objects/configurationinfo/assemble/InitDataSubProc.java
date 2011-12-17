package com.amalto.core.objects.configurationinfo.assemble;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.initdb.InitDBUtil;

public class InitDataSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
		//perform initial 
		boolean autoinit = "true".equals(MDMConfiguration.getConfiguration().getProperty(
				"system.data.auto.init", 
				"false"
			));
		if(autoinit){
			InitDBUtil.init();
	    	try {
				InitDBUtil.initDB();
			} catch (Exception e) {
				org.apache.log4j.Logger.getLogger(this.getClass()).error("Init db error! ");
				e.printStackTrace();
			}
		}
		
	}



}
