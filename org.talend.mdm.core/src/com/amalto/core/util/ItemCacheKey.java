package com.amalto.core.util;

import java.util.Hashtable;

public class ItemCacheKey {
	String revisionID;
	String uniqueID;
	String dataClusterID;
	public ItemCacheKey(String rID,String uniqueID,String dataClusterID){		
		this.revisionID=rID;
		
		this.uniqueID=uniqueID;
		this.dataClusterID=dataClusterID;
		this.revisionID= this.revisionID==null?"":this.revisionID;
	}
	
	public String getRevisionID() {
		return revisionID;
	}
	public void setRevisionID(String revisionID) {
		this.revisionID = revisionID;
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public String getDataClusterID() {
		return dataClusterID;
	}
	public void setDataClusterID(String dataClusterID) {
		this.dataClusterID = dataClusterID;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof ItemCacheKey){
			ItemCacheKey key=(ItemCacheKey)obj;			
			return new String(key.revisionID+key.uniqueID+key.dataClusterID).equals(revisionID+uniqueID+dataClusterID);
		}
		return false;
		
	}
	@Override
	public int hashCode() {
		return 10;
	}
	
	public static void main(String[] args) {
		Hashtable<ItemCacheKey, String> hash=new Hashtable<ItemCacheKey, String>();
		ItemCacheKey key1=new ItemCacheKey("11",null,"33");
		hash.put(key1, "1");
		key1=new ItemCacheKey("11",null,"33");
		hash.put(key1, "2");
		
		key1=new ItemCacheKey("11",null,"33");
		if(hash.containsKey(key1)){
			System.out.println("true");
		}
	}
}
