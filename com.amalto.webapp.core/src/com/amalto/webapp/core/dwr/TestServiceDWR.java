package com.amalto.webapp.core.dwr;

import com.amalto.webapp.core.bean.FileSystemConfiguration;

public class TestServiceDWR extends ServiceDWR {

	final static String JNDI_NAME = "test";
	final static String serviceName = "test";
	final static Class configurationClass = FileSystemConfiguration.class;
	static FileSystemConfiguration config = new FileSystemConfiguration();
	
	public TestServiceDWR() {
		super(JNDI_NAME, serviceName);
	}

	public FileSystemConfiguration getConfiguration() throws Exception {
		// TODO Auto-generated method stub
		return (FileSystemConfiguration)super.getConfiguration(FileSystemConfiguration.class);
	}
	
	
	


}


