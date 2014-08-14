package com.amalto.core.objects.synchronization.ejb;

public class SynchronizationRemoteInstance {
	
	String remoteSystemName;
	String revisionID;
	String xml;
	long lastLocalSynchronizationTime;
	
	
	public SynchronizationRemoteInstance() {
    }


	public SynchronizationRemoteInstance(String remoteSystemName, String revisionID, String xml, long lastLocalSynchronizationTime) {
	    super();
	    this.remoteSystemName = remoteSystemName;
	    this.revisionID = revisionID == null ? "" : revisionID;
	    this.xml = xml;
	    this.lastLocalSynchronizationTime = lastLocalSynchronizationTime;
    }

	public String getKey() {
		return getRemoteSystemName()+"/"+getRevisionID();
	}
	
	public String getRevisionID() {
    	return revisionID == null ? "" : revisionID;
    }

	public void setRevisionID(String revisionID) {
    	this.revisionID = revisionID == null ? "" : revisionID;
    }


	public String getXml() {
    	return xml;
    }

	public void setXml(String xml) {
    	this.xml = xml;
    }


	public long getLastLocalSynchronizationTime() {
    	return lastLocalSynchronizationTime;
    }

	public void setLastLocalSynchronizationTime(long lastLocalSynchronizationTime) {
    	this.lastLocalSynchronizationTime = lastLocalSynchronizationTime;
    }


	public String getRemoteSystemName() {
    	return remoteSystemName;
    }

	public void setRemoteSystemName(String remoteSystemName) {
    	this.remoteSystemName = remoteSystemName;
    }

	
	
}
