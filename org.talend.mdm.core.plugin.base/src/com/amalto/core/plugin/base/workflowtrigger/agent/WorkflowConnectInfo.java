package com.amalto.core.plugin.base.workflowtrigger.agent;

public class WorkflowConnectInfo {
	
	private String initialContextFactory;
	
	private String providerURL;
	
	private String apiType;
	
	private String username;
	
	private String password;
	
	public WorkflowConnectInfo() {
		
	}
	
	
	public WorkflowConnectInfo(String initialContextFactory,
			String providerURL, String apiType) {
		super();
		this.initialContextFactory = initialContextFactory;
		this.providerURL = providerURL;
		this.apiType = apiType;
	}
	
	
	public WorkflowConnectInfo(String initialContextFactory,
			String providerURL, String apiType, String username, String password) {
		super();
		this.initialContextFactory = initialContextFactory;
		this.providerURL = providerURL;
		this.apiType = apiType;
		this.username = username;
		this.password = password;
	}


	public String getInitialContextFactory() {
		return initialContextFactory;
	}

	public void setInitialContextFactory(String initialContextFactory) {
		this.initialContextFactory = initialContextFactory;
	}

	public String getProviderURL() {
		return providerURL;
	}

	public void setProviderURL(String providerURL) {
		this.providerURL = providerURL;
	}

	public String getApiType() {
		return apiType;
	}

	public void setApiType(String apiType) {
		this.apiType = apiType;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}
	
	

}
