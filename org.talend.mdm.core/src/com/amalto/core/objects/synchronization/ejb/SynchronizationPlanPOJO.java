package com.amalto.core.objects.synchronization.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.metadata.LongString;
import com.amalto.core.util.ArrayListHolder;

/**
 * @author Bruno Grieder
 * 
 * A synchronization plan defines how this system is synchronized with a remote system.
 * 
 */
public class SynchronizationPlanPOJO extends ObjectPOJO{
   
	public static final int ACTION_START_FULL = 1;
	public static final int ACTION_START_DIFFERENTIAL = 2;
	public static final int ACTION_STOP = 3;
	public static final int ACTION_GET_STATUS = 4;
	public static final int ACTION_RESET = 5;

	public static final String STATUS_DOES_NOT_EXIST = "DOES NOT EXIST";
	public static final String STATUS_COMPLETED = "COMPLETED";
	public static final String STATUS_FAILED = "FAILED";
	public static final String STATUS_RUNNING = "RUNNING";
	public static final String STATUS_SCHEDULED = "SCHEDULED";
	public static final String STATUS_STOPPING = "STOPPING";
	
    private String name;
    private String description;
    private String remoteSystemName;
    private String remoteSystemURL;
    private String remoteSystemUsername;
    private String remoteSystemPassword;
    private String tisURL;
    private String tisUsername;
    private String tisPassword;
    private String tisParameters;
    
    private HashMap<String, ArrayListHolder<SynchronizationPlanObjectLine>> xtentisObjectsSynchronizations = new HashMap<String, ArrayListHolder<SynchronizationPlanObjectLine>>();
    private ArrayList<SynchronizationPlanItemLine> itemsSynchronizations = new ArrayList<SynchronizationPlanItemLine>();
    private String defaultItemRevisionID;
    private long lastRunStarted;
    private long lastRunStopped;
    private String currentStatusCode;
    private String currentStatusMessage;
    
    public static final String LOCAL_WINS="Local Wins";
    public static final String REMOTE_WINS="Remote Wins";
    public static final String MANUAL="Manual";
    public static final String TIS_CALL="TIS Call";
    /** For marshaling */
    public SynchronizationPlanPOJO() {
	    super();
	    this.currentStatusCode = STATUS_FAILED;
	    this.currentStatusMessage = "Never Run";
    }
    
    
    
    
	public SynchronizationPlanPOJO(String name, String description, String remoteSystemName, String remoteSystemURL, String remoteSystemUsername, String remoteSystemPassword, String tisURL, String tisUsername, String tisPassword, String tisParameters, HashMap<String, ArrayListHolder<SynchronizationPlanObjectLine>> xtentisObjectsSynchronizations, ArrayList<SynchronizationPlanItemLine> itemsSynchronizations, String defaultItemRevisionID, long lastRunStarted, long lastRunStopped) {
	    super();
	    this.name = name;
	    this.description = description;
	    this.remoteSystemName = remoteSystemName;
	    this.remoteSystemURL = remoteSystemURL;
	    this.remoteSystemUsername = remoteSystemUsername;
	    this.remoteSystemPassword = remoteSystemPassword;
	    this.tisURL = tisURL;
	    this.tisUsername = tisUsername;
	    this.tisPassword = tisPassword;
	    this.tisParameters = tisParameters;
	    this.xtentisObjectsSynchronizations = xtentisObjectsSynchronizations;
	    this.itemsSynchronizations = itemsSynchronizations;
	    this.defaultItemRevisionID = defaultItemRevisionID;
	    this.lastRunStarted = lastRunStarted;
	    this.lastRunStopped = lastRunStopped;
	    this.currentStatusCode = STATUS_FAILED;
	    this.currentStatusMessage = "Never Run";
    }



	public String getName() {
    	return name;
    }

	public void setName(String name) {
    	this.name = name;
    }

	
	public String getDescription() {
    	return description;
    }

	public void setDescription(String description) {
    	this.description = description;
    }

	
	public String getRemoteSystemName() {
    	return remoteSystemName;
    }

	public void setRemoteSystemName(String remoteSystemName) {
    	this.remoteSystemName = remoteSystemName;
    }

	
	public String getRemoteSystemURL() {
    	return remoteSystemURL;
    }

	public void setRemoteSystemURL(String remoteSystemURL) {
    	this.remoteSystemURL = remoteSystemURL;
    }


	public String getRemoteSystemUsername() {
    	return remoteSystemUsername;
    }

	public void setRemoteSystemUsername(String remoteSystemUsername) {
    	this.remoteSystemUsername = remoteSystemUsername;
    }


	public String getRemoteSystemPassword() {
    	return remoteSystemPassword;
    }

	public void setRemoteSystemPassword(String remoteSystemPassword) {
    	this.remoteSystemPassword = remoteSystemPassword;
    }

	public String getTisURL() {
    	return tisURL;
    }

	public void setTisURL(String tisURL) {
    	this.tisURL = tisURL;
    }

	public String getTisUsername() {
    	return tisUsername;
    }

	public void setTisUsername(String tisUsername) {
    	this.tisUsername = tisUsername;
    }

	public String getTisPassword() {
    	return tisPassword;
    }

	public void setTisPassword(String tisPassword) {
    	this.tisPassword = tisPassword;
    }

	public String getTisParameters() {
    	return tisParameters;
    }

	public void setTisParameters(String tisParameters) {
    	this.tisParameters = tisParameters;
    }


	public HashMap<String, ArrayListHolder<SynchronizationPlanObjectLine>> getXtentisObjectsSynchronizations() {
    	return xtentisObjectsSynchronizations;
    }

	public void setXtentisObjectsSynchronizations(HashMap<String, ArrayListHolder<SynchronizationPlanObjectLine>> xtentisObjectsSynchronizations) {
    	this.xtentisObjectsSynchronizations = xtentisObjectsSynchronizations;
    }


	public ArrayList<SynchronizationPlanItemLine> getItemsSynchronizations() {
    	return itemsSynchronizations;
    }

	public void setItemsSynchronizations(ArrayList<SynchronizationPlanItemLine> itemsSyncrhonizations) {
    	this.itemsSynchronizations = itemsSyncrhonizations;
    }


	public String getDefaultItemRevisionID() {
    	return defaultItemRevisionID;
    }

	public void setDefaultItemRevisionID(String defaultItemRevisionID) {
    	this.defaultItemRevisionID = defaultItemRevisionID;
    }
	
    
    public long getLastRunStarted() {
    	return lastRunStarted;
    }

	public void setLastRunStarted(long lastRunStarted) {
    	this.lastRunStarted = lastRunStarted;
    }


	public long getLastRunStopped() {
    	return lastRunStopped;
    }

	public void setLastRunStopped(long lastRunStopped) {
    	this.lastRunStopped = lastRunStopped;
    }


	public String getCurrentStatusCode() {
    	return currentStatusCode;
    }

	public void setCurrentStatusCode(String currentStatusCode) {
    	this.currentStatusCode = currentStatusCode;
    }


	@LongString
	public String getCurrentStatusMessage() {
    	return currentStatusMessage;
    }

	public void setCurrentStatusMessage(String currentStatusMessage) {
    	this.currentStatusMessage = currentStatusMessage;
    }

	
	/**
	 * The list of Xtentis Object Names to use a a key to 
	 * the Xtentis Objects revision IDs {@link HashMap}
	 * @return
	 * 		An ordered list of Xtentis Object Names
	 */
	public static TreeSet<String> getXtentisObjectNames() {
		TreeSet<String> list =  new TreeSet<String>(ObjectPOJO.getObjectsNames2ClassesMap().keySet());
		//remove the Configuration Info which is not linked to the user
		list.remove("Configuration Info");
		//remove the synchronization plan
		list.remove("Synchronization Plan");
		//return the list
		return list;
	}
	
	/**
	 * The list of Algorithms names that can be used for synchronization of Xtentis Objects 
	 * @return
	 * 		An ordered list of Algorithms Names
	 */
	public static TreeSet<String> getObjectsAlgorithmNames() {
		TreeSet<String> list =  new TreeSet<String>();
		list.add(LOCAL_WINS);
		list.add(REMOTE_WINS);
		return list;
	}
	
	/**
	 * The list of Algorithms names that can be used for synchronization of Items
	 * @return
	 * 		An ordered list of Algorithms Names
	 */
	public static TreeSet<String> getItemsAlgorithmNames() {
		TreeSet<String> list =  new TreeSet<String>();
		list.add(MANUAL);
		list.add(LOCAL_WINS);
		list.add(REMOTE_WINS);
		//list.add(TIS_CALL);
		return list;
	}

	@Override
    public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
    }
    
}
