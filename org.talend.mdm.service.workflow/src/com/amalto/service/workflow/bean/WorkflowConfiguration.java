package com.amalto.service.workflow.bean;

import java.io.Serializable;

public class WorkflowConfiguration implements Serializable {
	
	private String initialContextFactory;
	
	private String providerURL;
	
	private String apiType;
	
	public WorkflowConfiguration() {
	
	}
	
//	public WorkflowConfiguration( 
//			String initialContextFactory,
//			String providerURL, 
//			String apiType) {
//		super();
//		this.apiType = apiType;
//		this.initialContextFactory = initialContextFactory;
//		this.providerURL = providerURL;
//	}


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


	
}
