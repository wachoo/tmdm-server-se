package com.amalto.core.ejb;

import java.io.Serializable;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;

public class DroppedItemPOJOPK implements Serializable {
	
	private ItemPOJOPK refItemPOJOPK;
	
	private String partPath;
	
	private String revisionId;

	public DroppedItemPOJOPK(String revisionId,ItemPOJOPK refItemPOJOPK,String partPath) {
		super();
		this.revisionId = (revisionId==null?"":(revisionId));
		this.refItemPOJOPK = refItemPOJOPK;
		this.partPath = partPath;
	}


	public String getUniquePK() {
		if(revisionId==null||revisionId.equals(""))revisionId="head";
    	return revisionId+"."+refItemPOJOPK.getUniqueID()+convertItemPartPath(partPath);
	}
	
	
	public String getRevisionId() {
		if(revisionId!=null&&revisionId.toLowerCase().equals("head"))revisionId=null;
		return revisionId;
	}


	public ItemPOJOPK getRefItemPOJOPK() {
		return refItemPOJOPK;
	}
	
	public String getPartPath() {
		return partPath;
	}


	private static String convertItemPartPath(String partPath) {
		
		if(partPath!=null&&partPath.length()>0){
			partPath=partPath.replaceAll("/", "-");
		}
		
		return partPath;
		
	}
	
	
	/**
	 * @param input "."&&"-" are reserved words
	 * @return DroppedItemPOJOPK
	 */
	public static DroppedItemPOJOPK buildUid2POJOPK(String input) {
		//TODO need regular expression to validate input
		
		int pos=input.indexOf("-");
		String part1=input.substring(0, pos);
		String part2=input.substring(pos);
		
		String[] part1s=part1.split("\\.");
		if(part1s.length<3)return null;//validate
		String revision=part1s[0];
		String clusterName=part1s[1];
		String conceptName=part1s[2];
		String[] ids=new String[part1s.length-3];
		for (int i = 0; i < ids.length; i++) {
			ids[i]=part1s[i+3];
		}
		
		String partPath=part2.replaceAll("-", "/");
		
		ItemPOJOPK refItemPOJOPK = new ItemPOJOPK(new DataClusterPOJOPK(clusterName), conceptName, ids);
		DroppedItemPOJOPK droppedItemPOJOPK=new DroppedItemPOJOPK(revision,refItemPOJOPK,partPath);
		return droppedItemPOJOPK;
	}
	
	@Override
	public String toString() {
		return getUniquePK();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) return false;
	    if (! (obj instanceof DroppedItemPOJOPK)) return false;
	    DroppedItemPOJOPK other = (DroppedItemPOJOPK) obj;
	    return other.getUniquePK().equals(this.getUniquePK());
	}	
	
}
