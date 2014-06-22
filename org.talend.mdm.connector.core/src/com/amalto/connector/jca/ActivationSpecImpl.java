package com.amalto.connector.jca;

import javax.resource.ResourceException;
import javax.resource.spi.ActivationSpec;
import javax.resource.spi.InvalidPropertyException;
import javax.resource.spi.ResourceAdapter;

public class ActivationSpecImpl implements ActivationSpec {

	private ResourceAdapter ra;
	
	private String testProperty;
	
	public void validate() throws InvalidPropertyException {
		// TODO Auto-generated method stub

	}

	public ResourceAdapter getResourceAdapter() {
//		org.apache.log4j.Category.getInstance(this.getClass()).debug("getResourceAdapter() ");
		return ra;
	}

	public void setResourceAdapter(ResourceAdapter ra) throws ResourceException {
//		org.apache.log4j.Category.getInstance(this.getClass()).debug("setResourceAdapter() ");
		this.ra = ra;
	}

	public synchronized String getTestProperty() {
//		org.apache.log4j.Category.getInstance(this.getClass()).debug("getTestProperty() ");
		return testProperty;
	}

	public synchronized void setTestProperty(String testProperty) {
//		org.apache.log4j.Category.getInstance(this.getClass()).debug("setTestProperty() VALUE:"+testProperty);
		this.testProperty = testProperty;
	}

}
