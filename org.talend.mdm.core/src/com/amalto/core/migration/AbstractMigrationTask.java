package com.amalto.core.migration;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.exolab.castor.xml.Unmarshaller;

import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


public abstract class AbstractMigrationTask {
    
	private Map<String, Boolean> handlerMap = null;
    public static final String CLUSTER_MIGRATION = "MDMMigration";
    private static final String UNIQUE_MIGRATION = "MIGRATION.completed.record";
    
	public AbstractMigrationTask() {
	}
	
	 
	protected  boolean isDone(){
		Boolean res = false;
		try {
			String content = Util.getXmlServerCtrlLocal().getDocumentAsString(null, CLUSTER_MIGRATION, UNIQUE_MIGRATION);
			if (content == null){
				if(handlerMap==null)handlerMap=new HashMap<String, Boolean>();
				return false;
			}
			MigrationTaskBox box = unmarshal(content);
			
			handlerMap = box.getHandlerMap();
			if(handlerMap==null)handlerMap=new HashMap<String, Boolean>();
			
			res = handlerMap.get(this.getClass().getName());
			if (res == null) return false;
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getMessage());
			e.printStackTrace();
			return res;
		}
		return res;
	}
	
	public void start()
	{	
		if (isDone())
		{
			return;
		}
		
		Boolean result = execute();
		if(result==null)result=false;
		

		if (result)
		{
			handlerMap.put(this.getClass().getName(), true);
		}
		else if (!result)
		{
			handlerMap.put(this.getClass().getName(), false);
		}
		
		try {
			MigrationTaskBox newBox=new MigrationTaskBox(handlerMap);
			Util.getXmlServerCtrlLocal().putDocumentFromString(newBox.toString(), UNIQUE_MIGRATION, CLUSTER_MIGRATION, null);
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(this.getClass()).error(e.getMessage());
		}
		
		org.apache.log4j.Logger.getLogger(this.getClass()).info(this.getClass().getName()+" has been done. ");
	}
	
   protected abstract Boolean execute();
		
   public static MigrationTaskBox  unmarshal(String marshalledRevision) throws XtentisException {	
        try {
    		return (MigrationTaskBox) Unmarshaller.unmarshal(MigrationTaskBox.class, new StringReader(marshalledRevision));
	    } catch (Exception e) {
    	    org.apache.log4j.Logger.getLogger(AbstractMigrationTask.class).error(e);
    	    throw new XtentisException(e.getMessage());
	    } 
	 }

}
