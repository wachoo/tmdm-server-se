package com.amalto.webapp.v3.hierarchical.bean;

import java.util.ArrayList;
import java.util.List;

import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.v3.hierarchical.util.HierarchicalUtil;

public class HierarchicalTreeCriterion {
	
	private String dataObjectName;
	private String pivotPath;
	private String titleFieldPath;
	private FilterItem[] filters;
	
	private String[] pivotDirections=null;
	private String[] indexDirections=null;
	private int limit=Integer.MAX_VALUE;
	
	private String clusterName;
	private String modelName;
	
	public HierarchicalTreeCriterion(String dataObjectName, String pivotPath,
			String titleFieldPath, FilterItem[] filters) {
		super();
		this.dataObjectName = dataObjectName;
		this.pivotPath = pivotPath;
		this.titleFieldPath = titleFieldPath;
		this.filters = filters;
	}
	
	public HierarchicalTreeCriterion(String clusterName,String modelName, String dataObjectName,
			String pivotPath, String titleFieldPath, FilterItem[] filters) {
		super();
		
		this.clusterName = clusterName;
		this.modelName = modelName;
		this.dataObjectName = dataObjectName;
		this.pivotPath = pivotPath;
		this.titleFieldPath = titleFieldPath;
		this.filters = filters;
	}
	
	
	public HierarchicalTreeCriterion(String clusterName,String modelName, String dataObjectName,
			String pivotPath, String titleFieldPath, FilterItem[] filters, String[] pivotDirections,String[] indexDirections,int limit) {
		super();
		
		this.clusterName = clusterName;
		this.modelName = modelName;
		this.dataObjectName = dataObjectName;
		this.pivotPath = pivotPath;
		this.titleFieldPath = titleFieldPath;
		this.filters = filters;
		
		this.pivotDirections = pivotDirections;
		this.indexDirections = indexDirections;
		this.limit = limit;
	}



	public String getDataObjectName() {
		return dataObjectName;
	}

	public void setDataObjectName(String dataObjectName) {
		this.dataObjectName = dataObjectName;
	}

	public String getPivotPath() {
		return pivotPath;
	}

	public void setPivotPath(String pivotPath) {
		this.pivotPath = pivotPath;
	}

	public String getTitleFieldPath() {
		return titleFieldPath;
	}

	public void setTitleFieldPath(String titleFieldPath) {
		this.titleFieldPath = titleFieldPath;
	}

	public FilterItem[] getFilters() {
		return filters;
	}

	public void setFilters(FilterItem[] filters) {
		this.filters = filters;
	}
	
	
	public String getClusterName() {
		return clusterName;
	}

	public void setClusterName(String clusterName) {
		this.clusterName = clusterName;
	}
	
	
	public String getModelName() {
		return modelName;
	}

	public void setModelName(String modelName) {
		this.modelName = modelName;
	}

	public String[] getPivotDirections() {
		return pivotDirections;
	}


	public void setPivotDirections(String[] pivotDirections) {
		this.pivotDirections = pivotDirections;
	}
	
	public String obtainPivotDirectionsExpr() {
		String expr="";
		
		if(pivotDirections!=null){
			for (int i = pivotDirections.length-1; i >-1 ; i--) {
				if(pivotDirections[i].equals("ascending")){
					expr+="ASC";
				}else if(pivotDirections[i].equals("descending")){
					expr+="DESC";
				}else{
					expr+=pivotDirections[i];
				}
				if(i>0)expr+="-";
			}
		}
		
       return expr;
	}


	public String[] getIndexDirections() {
		return indexDirections;
	}
	
	public String obtainIndexDirectionsExpr() {
		String expr="";
		
		if(indexDirections!=null){
			for (int i = 0; i < indexDirections.length; i++) {
				
				if(indexDirections[i].equals("ascending")){
					expr+="ASC";
				}else if(indexDirections[i].equals("descending")){
					expr+="DESC";
				}else{
					expr+=indexDirections[i];
				}
				if(i<indexDirections.length-1)expr+="-";
			}
		}
		
       return expr;
	}


	public void setIndexDirections(String[] indexDirections) {
		this.indexDirections = indexDirections;
	}


	public int getLimit() {
		return limit;
	}


	public void setLimit(int limit) {
		this.limit = limit;
	}


	public String parsePivotMainPath() {
		String pivotPathPart="";
		try {
			String[] pivotPathParts=pivotPath.split("->");
			pivotPathPart=pivotPathParts[0];
			pivotPathPart=HierarchicalUtil.cleanPivotPath(pivotPathPart);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return pivotPathPart;
	}


	public List parsePivotTagOrderList() {
		List rtn=new ArrayList();
		String[] pivotPathParts=pivotPath.split("->");
		for (int i = 0; i < pivotPathParts.length; i++) {
			String pivotPathPart=pivotPathParts[i];
			pivotPathPart=HierarchicalUtil.cleanPivotPath(pivotPathPart);
			String tagName=HierarchicalUtil.parseLastPartFromXpath(pivotPathPart);
			rtn.add(tagName);
		}
		return rtn;
	}
	
	public List parseTitleTag() {
		List rtn=new ArrayList();
		String tagName=HierarchicalUtil.parseLastPartFromXpath(titleFieldPath);	
		rtn.add(tagName);
		return rtn;
	}
	
	public List parseKeysTagList() {
		List rtn=new ArrayList();
		
		try {
			String mainPivot =HierarchicalUtil.cleanPivotPath(this.pivotPath.split("->")[0]);
			String mainConceptName = HierarchicalUtil
					.parseDataConceptFromPivotPath(mainPivot);

			String[] pks = CommonDWR.getBusinessConceptKeyPaths(
					modelName, mainConceptName);

			for (int i = 0; i < pks.length; i++) {
				String tagName = HierarchicalUtil.parseLastPartFromXpath(pks[i]);
				rtn.add(tagName);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return rtn;
	}

}
