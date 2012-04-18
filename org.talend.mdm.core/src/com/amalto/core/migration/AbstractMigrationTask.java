package com.amalto.core.migration;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import org.exolab.castor.xml.Unmarshaller;

import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;


public abstract class AbstractMigrationTask {
    
	private Map<String, Boolean> handlerMap = null;
    public static final String CLUSTER_MIGRATION = "MDMMigration";
    private static final String UNIQUE_MIGRATION = "MIGRATION.completed.record";
    boolean forceExe=false;
	public AbstractMigrationTask() {
	}
	
	 
	public boolean isForceExe() {
		return forceExe;
	}


	public void setForceExe(boolean forceExe) {
		this.forceExe = forceExe;
	}


	protected  boolean isDone(){
		Boolean res = false;
		
		String content = null;
		try{
			Util.getXmlServerCtrlLocal().createCluster(null, CLUSTER_MIGRATION);
			content=Util.getXmlServerCtrlLocal().getDocumentAsString(null, CLUSTER_MIGRATION, UNIQUE_MIGRATION);				
		}catch(Exception e){}
		if (content == null){
			if(handlerMap==null)handlerMap=new HashMap<String, Boolean>();
			return false;
		}
		try {			
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
		if (isDone() && !forceExe)
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
            XmlServerSLWrapperLocal xmlServerCtrlLocal = Util.getXmlServerCtrlLocal();
            xmlServerCtrlLocal.start(CLUSTER_MIGRATION);
            xmlServerCtrlLocal.putDocumentFromString(newBox.toString(), UNIQUE_MIGRATION, CLUSTER_MIGRATION, null);
            xmlServerCtrlLocal.commit(CLUSTER_MIGRATION);
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
