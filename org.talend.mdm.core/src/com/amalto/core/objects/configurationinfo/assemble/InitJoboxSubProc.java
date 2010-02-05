package com.amalto.core.objects.configurationinfo.assemble;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.util.JoboxConfig;




public class InitJoboxSubProc extends AssembleSubProc{
	

	@Override
	public void run() throws Exception {
		
		//FIXME: THIS WAY TO GET DEFAULT JOBOX HOME MAYBE NOT GOOD
		URL url=this.getClass().getResource("/");
		String jbossHome=new File(url.toURI()).getParentFile().getParentFile().getParentFile().getAbsolutePath();
		//init
		JobContainer jobContainer=JobContainer.getUniqueInstance();
		Properties props=new Properties();
		props.put(JoboxConfig.JOBOX_HOME_PATH, jbossHome+File.separator+"jobox");
		jobContainer.init(props);	
		
	}
	

}
