package com.amalto.core.objects.synchronization.ejb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.metadata.LongString;

/**
 * @author Bruno Grieder
 * 
 * A synchronization plan defines how this system is synchronized with a remote system.
 * 
 */
public class SynchronizationObjectPOJO extends ObjectPOJO{
   
	/** The synchronization was started but is not resolved yet */
	public final static int STATUS_PENDING = 1;
	/** The synchronization failed - manual resolution required (for items only) */
	public final static int STATUS_MANUAL = 2;
	/** The synchronization was resolved but not yet executed */
	public final static int STATUS_RESOLVED = 3;
	/** The synchronization was executed */
	public final static int STATUS_EXECUTED = 4;
	
	
	
	private String localRevisionID = null;
    private String objectName = null;
    private String objectUniqueID = null;
    private String lastRunPlan = null;
    private int status = STATUS_PENDING;
    private String resolvedXML = null;
    private HashMap<String, SynchronizationRemoteInstance> remoteIntances = new HashMap<String, SynchronizationRemoteInstance>();
    
    
    /** For marshaling */
    public SynchronizationObjectPOJO() {
    }
    

	public SynchronizationObjectPOJO(String lastRunPlan, String thisSystemRevisionID, String objectName, String objectUniqueID, int status, String thisSystemXML, HashMap<String, SynchronizationRemoteInstance> remoteIntances) {
	    super();
	    this.localRevisionID = (thisSystemRevisionID == null ? "" : thisSystemRevisionID);
	    this.objectName = objectName;
	    this.objectUniqueID = objectUniqueID;
	    this.status = status;
	    this.resolvedXML = thisSystemXML;
	    this.remoteIntances = remoteIntances;
	    this.lastRunPlan = lastRunPlan;
    }



	public String getLocalRevisionID() {
    	return localRevisionID;
    }


	public void setLocalRevisionID(String thisSystemRevisionID) {
    	this.localRevisionID = (thisSystemRevisionID == null ? "" : thisSystemRevisionID);
    }


	public String getObjectName() {
    	return objectName;
    }


	public void setObjectName(String objectName) {
    	this.objectName = objectName;
    }


	public String getObjectUniqueID() {
    	return objectUniqueID;
    }


	public void setObjectUniqueID(String objectUniqueID) {
    	this.objectUniqueID = objectUniqueID;
    }


	public int getStatus() {
    	return status;
    }


	public void setStatus(int status) {
    	this.status = status;
    }

	@LongString
	public String getResolvedXML() {
    	return resolvedXML;
    }


	public void setResolvedXML(String thisSystemXML) {
    	this.resolvedXML = thisSystemXML;
    }


	public HashMap<String, SynchronizationRemoteInstance> getRemoteIntances() {
    	return remoteIntances;
    }


	public void setRemoteIntances(HashMap<String, SynchronizationRemoteInstance> remoteIntances) {
    	this.remoteIntances = remoteIntances;
    }


	public String getLastRunPlan() {
    	return lastRunPlan;
    }

	public void setLastRunPlan(String lastRunPlan) {
    	this.lastRunPlan = lastRunPlan;
    }


	@Override
    public ObjectPOJOPK getPK() {
		if (getObjectName()==null) return null;
		
		ArrayList<String> ids = new ArrayList<String>();
		ids.add(localRevisionID == null ? "" : localRevisionID);
		ids.add(objectName);
		ids.addAll(Arrays.asList(objectUniqueID));		
		return new ObjectPOJOPK(ids.toArray(new String[ids.size()]));
    }
    
}
