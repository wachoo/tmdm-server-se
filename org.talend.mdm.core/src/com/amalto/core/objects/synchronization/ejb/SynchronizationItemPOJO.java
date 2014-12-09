package com.amalto.core.objects.synchronization.ejb;

import java.util.HashMap;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;

/**
 * @author Bruno Grieder
 * 
 * A synchronization plan defines how this system is synchronized with a remote system.
 * 
 */
public class SynchronizationItemPOJO extends ObjectPOJO{
   
	/** The synchronization was started but is not resolved yet */
	public final static int STATUS_PENDING = 1;
	/** The synchronization failed - manual resolution required (for items only) */
	public final static int STATUS_MANUAL = 2;
	/** The synchronization was resolved but not yet executed */
	public final static int STATUS_RESOLVED = 3;
	/** The synchronization was executed */
	public final static int STATUS_EXECUTED = 4;
	
	
	private ItemPOJOPK itemPOJOPK = null; 
	private String localRevisionID = null;
    private String lastRunPlan = null;
    private int status = STATUS_PENDING;
    private String resolvedProjection = null;
    private HashMap<String, SynchronizationRemoteInstance> remoteIntances = new HashMap<String, SynchronizationRemoteInstance>();
    
    
    /** For marshaling */
    public SynchronizationItemPOJO() {}
    
	public SynchronizationItemPOJO(ItemPOJOPK itemPOJOPK, String localRevisionID, String lastRunPlan, int status, String resolvedProjection, HashMap<String, SynchronizationRemoteInstance> remoteIntances) {
	    super();
	    this.itemPOJOPK = itemPOJOPK;
	    this.localRevisionID = localRevisionID == null ? "" : localRevisionID;
	    this.lastRunPlan = lastRunPlan;
	    this.status = status;
	    this.resolvedProjection = resolvedProjection;
	    this.remoteIntances = remoteIntances;
    }


	public String getLocalRevisionID() {
    	return localRevisionID == null ? "" : localRevisionID;
    }


	public void setLocalRevisionID(String localRevisionID) {
    	this.localRevisionID = (localRevisionID == null ? "" : localRevisionID);
    }

	public ItemPOJOPK getItemPOJOPK() {
    	return itemPOJOPK;
    }

	public void setItemPOJOPK(ItemPOJOPK itemPOJOPK) {
    	this.itemPOJOPK = itemPOJOPK;
    }

	public int getStatus() {
    	return status;
    }

	public void setStatus(int status) {
    	this.status = status;
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

	public String getResolvedProjection() {
    	return resolvedProjection;
    }

	public void setResolvedProjection(String resolvedProjection) {
    	this.resolvedProjection = resolvedProjection;
    }

	@Override
    public ObjectPOJOPK getPK() {
		if (getItemPOJOPK()==null) return null;
		return new ObjectPOJOPK(getItemPOJOPK().getIds());
    }
	
	public SynchronizationItemPOJOPK getSynchronizationItemPOJOPK() {
		if (getItemPOJOPK()==null) return null;
		return new SynchronizationItemPOJOPK(getLocalRevisionID(),getItemPOJOPK());
	}
    
}
