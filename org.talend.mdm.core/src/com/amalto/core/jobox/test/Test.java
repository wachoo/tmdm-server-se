package com.amalto.core.jobox.test;

import java.util.Properties;

import com.amalto.core.jobox.JobContainer;
import com.amalto.core.jobox.util.JoboxConfig;

public class Test {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//init
		JobContainer jobContainer=JobContainer.getUniqueInstance();
		Properties props=new Properties();
		props.put(JoboxConfig.JOBOX_HOME_PATH, "E:/base/jobox");
		jobContainer.init(props);
		
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			e.printStackTrace();
//		}
		//invoke
		JobContainer.getUniqueInstance().getJobInvoke().call("hello", "0.1", "mdmtest.hello_0_1.hello");
		
		
		
//		// Avoid program exit
//	    while (!false) ;

	}

}
