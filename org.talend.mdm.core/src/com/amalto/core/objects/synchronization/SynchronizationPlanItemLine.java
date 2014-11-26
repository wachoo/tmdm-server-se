package com.amalto.core.objects.synchronization;

import com.amalto.core.objects.datacluster.DataClusterPOJOPK;

public class SynchronizationPlanItemLine {
	
	private String conceptName;
	private String idsPattern;
	private DataClusterPOJOPK localClusterPOJOPK;
	private String localRevisionID;
	private DataClusterPOJOPK remoteClusterPOJOPK;
	private String remoteRevisionID;
	private String algorithm;
	
	/** for marshaling */
	public SynchronizationPlanItemLine() {
    }

	public SynchronizationPlanItemLine(String conceptName, String idsPattern, DataClusterPOJOPK localClusterPOJOPK, String localRevisionID, DataClusterPOJOPK remoteClusterPOJOPK, String remoteRevisionID, String algorithm) {
	    super();
	    this.conceptName = conceptName;
	    this.idsPattern = idsPattern;
	    this.localClusterPOJOPK = localClusterPOJOPK;
	    this.localRevisionID = localRevisionID;
	    this.remoteClusterPOJOPK = remoteClusterPOJOPK;
	    this.remoteRevisionID = remoteRevisionID;
	    this.algorithm = algorithm;
    }

	public String getConceptName() {
    	return conceptName;
    }

	public void setConceptName(String conceptPattern) {
    	this.conceptName = conceptPattern;
    }

	public String getIdsPattern() {
    	return idsPattern;
    }

	public void setIdsPattern(String idsPattern) {
    	this.idsPattern = idsPattern;
    }

	public String getLocalRevisionID() {
		if(localRevisionID!=null){
			if(localRevisionID.trim().toUpperCase().equals("HEAD")||localRevisionID.trim().toUpperCase().equals("[HEAD]")){
				localRevisionID="";
			}
		}
    	return localRevisionID;
    }

	public void setLocalRevisionID(String sourceRevisionID) {
    	this.localRevisionID = sourceRevisionID;
    }

	public String getRemoteRevisionID() {
		if(remoteRevisionID!=null){
			if(remoteRevisionID.trim().toUpperCase().equals("HEAD")||remoteRevisionID.trim().toUpperCase().equals("[HEAD]")){
				remoteRevisionID="";
			}
		}
    	return remoteRevisionID;
    }

	public void setRemoteRevisionID(String destinationRevisionID) {
    	this.remoteRevisionID = destinationRevisionID;
    }

	public String getAlgorithm() {
    	return algorithm;
    }

	public void setAlgorithm(String algorithm) {
    	this.algorithm = algorithm;
    }

	public DataClusterPOJOPK getLocalClusterPOJOPK() {
    	return localClusterPOJOPK;
    }

	public void setLocalClusterPOJOPK(DataClusterPOJOPK localClusterPOJOPK) {
    	this.localClusterPOJOPK = localClusterPOJOPK;
    }

	public DataClusterPOJOPK getRemoteClusterPOJOPK() {
    	return remoteClusterPOJOPK;
    }

	public void setRemoteClusterPOJOPK(DataClusterPOJOPK remoteClusterPOJOPK) {
    	this.remoteClusterPOJOPK = remoteClusterPOJOPK;
    }
	
	

}
