package com.amalto.core.jobox.util;

import java.io.File;
import java.util.Properties;

public class JoboxConfig {
	
	private String jobox_home;
	public static final String JOBOX_HOME_PATH="jobox_home_path";
	private static final String deploy_dir_name="deploy";
	private static final String work_dir_name="work";
	
	public JoboxConfig(Properties props) {
		super();
		jobox_home = props.getProperty(JOBOX_HOME_PATH);
	}
	
	

	public String getJoboxHome() {
		return jobox_home;
	}

	public String getDeployPath() {
		return jobox_home+File.separator+deploy_dir_name;
	}
	public String getWorkPath() {
		return jobox_home+File.separator+work_dir_name;
	}

}
