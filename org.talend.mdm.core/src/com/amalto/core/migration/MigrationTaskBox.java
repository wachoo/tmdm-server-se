package com.amalto.core.migration;

import java.io.Serializable;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import com.amalto.core.objects.marshalling.MarshallingFactory;

public class MigrationTaskBox implements  Serializable{
	
	private static final long serialVersionUID = 5658917963695194606L;

	public MigrationTaskBox() {
	
	}
	

	public MigrationTaskBox(Map<String, Boolean> handlerMap) {
		super();
		this.handlerMap = handlerMap;
	}



	private Map<String, Boolean> handlerMap = new HashMap<String, Boolean>();

	public Map<String, Boolean> getHandlerMap() {
		return handlerMap;
	}

	public void setHandlerMap(Map<String, Boolean> handlerMap) {
		this.handlerMap = handlerMap;
	}
	
	 
	public String toString() {
		try {
			StringWriter sw = new StringWriter();
			MarshallingFactory.getInstance().getMarshaller(this.getClass()).marshal(this, sw);
			return sw.toString();
	    } catch (Exception e) {
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(e);
		    return null;
	    } 
	}
}
