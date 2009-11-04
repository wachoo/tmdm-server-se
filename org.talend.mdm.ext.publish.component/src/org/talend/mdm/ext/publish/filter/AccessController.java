package org.talend.mdm.ext.publish.filter;

import java.util.List;

import org.apache.log4j.Logger;



public abstract class AccessController {
	
	private static final Logger logger=Logger.getLogger(AccessController.class);
	
	protected Logger getLocalLogger() {
		return logger;
	}

	public abstract boolean validate(List<String> resourceInstances,AccessControlPropertiesReader propertiesReader);
	
}
