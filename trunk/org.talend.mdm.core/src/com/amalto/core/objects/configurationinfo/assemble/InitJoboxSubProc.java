package com.amalto.core.objects.configurationinfo.assemble;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Properties;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.util.JoboxConfig;




public class InitJoboxSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
		
		String jbossHomePath=com.amalto.core.util.Util.getAppServerDeployDir();
		if(!new File(jbossHomePath).exists())throw new FileNotFoundException();
		String jbossHome=new File(jbossHomePath).getAbsolutePath();
		//init
		JobContainer jobContainer=JobContainer.getUniqueInstance();
		Properties props=new Properties();
		props.put(JoboxConfig.JOBOX_HOME_PATH, jbossHome+File.separator+"jobox");
		jobContainer.init(props);	
		
	}
	

}
