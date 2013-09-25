package com.amalto.core.ejb;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.schema.manage.SchemaCoreAgent;

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
	 * @throws Exception 
	 */
	public static DroppedItemPOJOPK buildUid2POJOPK(String input, Map<String, BusinessConcept> conceptMap) throws Exception {
		//TODO need regular expression to validate input
		
		int pos=input.lastIndexOf("-"); // input = "head.Product.Product.1-" or "head.RTE-RAP.Contrat.1-"
		String part1=input.substring(0, pos);
		String part2=input.substring(pos);
		
		String[] part1s=part1.split("\\.");
		if(part1s.length<3)return null;//validate
		String revision=part1s[0];
		String clusterName=part1s[1];
		String conceptName=part1s[2];
		// check xsd key's length
        String idPrefix = clusterName + "." + conceptName + ".";  //$NON-NLS-1$//$NON-NLS-2$
        String[] ids=new String[part1s.length-3];
        for (int i = 0; i < ids.length; i++) {
            ids[i]=part1s[i+3];
        }
        if (!conceptMap.containsKey(idPrefix)) {
            BusinessConcept businessConcept = SchemaCoreAgent.getInstance().getBusinessConcept(conceptName, new DataModelID(clusterName, revision));
            businessConcept.load();
            conceptMap.put(idPrefix, businessConcept);
        }
        if (conceptMap.get(idPrefix) != null && conceptMap.get(idPrefix).getKeyFieldPaths().size() == 1) {
            ids = new String[] {StringUtils.removeStart(part1, idPrefix)};
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
