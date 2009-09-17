package org.talend.mdmd.migration;

import java.io.Serializable;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;

import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;

public  class AbstractMigrationTask implements  Serializable{
    private Map<String, Boolean> handlerMap = new HashMap<String, Boolean>();
    private static final String CLUSTER_MIGRATION = "MIGRATION";
    private static final String UNIQUE_MIGRATION = "MIGRATION";
    
	public AbstractMigrationTask() {
	}
	
	public void setHandlerMap(Map<String, Boolean> map) {
		handlerMap = map;
	}
	
	public Map<String, Boolean> getHandlerMap() {
		return handlerMap;
	}
	
	 
	protected  boolean isDone(){
		Boolean res = false;
		try {
			String content = Util.getXmlServerCtrlLocal().getDocumentAsString(null, CLUSTER_MIGRATION, UNIQUE_MIGRATION);
			if (content == null) return false;
			AbstractMigrationTask cpy = unmarshal(content);
			handlerMap = cpy.getHandlerMap();
			res = handlerMap.get(this.getClass().getName());
			if (res == null) return false;
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getMessage());
			return res;
		}
		return res;
	}
	
	public void start()
	{	
		if (isDone())
		{
			org.apache.log4j.Logger.getLogger(this.getClass()).info(
					this.getClass().getName()
							+ "return a successful result with no alter");
			return;
		}
		
		Boolean result = execute();

		if (result)
		{
			org.apache.log4j.Logger.getLogger(this.getClass()).info(
					this.getClass().getName()
							+ "return a successful result");
			handlerMap.put(this.getClass().getName(), true);
		}
		else if (!result)
		{
			org.apache.log4j.Logger.getLogger(this.getClass()).info(
					this.getClass().getName() + "failed");
			handlerMap.put(this.getClass().getName(), false);
		}
		
		try {
			Util.getXmlServerCtrlLocal().putDocumentFromString(this.toString(), UNIQUE_MIGRATION,
					CLUSTER_MIGRATION, null);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getMessage());
		}
	}
	
	protected  Boolean execute(){
		return false;
	}
	

	 public String  marshal() throws XtentisException {	
	    try {
			StringWriter sw = new StringWriter();
			Marshaller.marshal(this, sw);
			return sw.toString();
	    } catch (Exception e) {
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(e);
		    throw new XtentisException(e.getMessage());
	    } 
	 }
	 
	public String toString() {
        try {
	        return marshal();
        } catch (XtentisException e) {
	        return null;
        }
	}
	
	 public static AbstractMigrationTask  unmarshal(String marshalledRevision) throws XtentisException {	
        try {
    		return (AbstractMigrationTask) Unmarshaller.unmarshal(AbstractMigrationTask.class, new StringReader(marshalledRevision));
	    } catch (Exception e) {
    	    org.apache.log4j.Logger.getLogger(AbstractMigrationTask.class).error(e);
    	    throw new XtentisException(e.getMessage());
	    } 
	 }

}
