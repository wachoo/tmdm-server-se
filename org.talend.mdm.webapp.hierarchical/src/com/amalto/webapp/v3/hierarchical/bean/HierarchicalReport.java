package com.amalto.webapp.v3.hierarchical.bean;

import java.io.StringReader;
import java.io.StringWriter;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.Unmarshaller;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.InputSource;


public class HierarchicalReport {
    //report info
	private String reportName;
	private String owner;
	private Boolean shared;
	
	//context
	private String dataCluster;
	private String dataModel;
	
	//main info
	private String dataObjectName;
	private String pivotPath;
	private String titleFieldPath;
	private Filters filters;
	
	//addition info
	private String pivotDirectionsExpr;
	private String indexDirectionsExpr;
	private Integer limit;

		
	public HierarchicalReport() {
		super();
	}


	public String getReportName() {
		return reportName;
	}



	public void setReportName(String reportName) {
		this.reportName = reportName;
	}


	public String getOwner() {
		return owner;
	}


	public void setOwner(String owner) {
		this.owner = owner;
	}


	public Boolean getShared() {
		return shared;
	}


	public void setShared(Boolean shared) {
		this.shared = shared;
	}


	public String getDataCluster() {
		return dataCluster;
	}


	public void setDataCluster(String dataCluster) {
		this.dataCluster = dataCluster;
	}


	public String getDataModel() {
		return dataModel;
	}


	public void setDataModel(String dataModel) {
		this.dataModel = dataModel;
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


	public Filters getFilters() {
		return filters;
	}


	public void setFilters(Filters filters) {
		this.filters = filters;
	}


	public String getPivotDirectionsExpr() {
		return pivotDirectionsExpr;
	}


	public void setPivotDirectionsExpr(String pivotDirectionsExpr) {
		this.pivotDirectionsExpr = pivotDirectionsExpr;
	}


	public String getIndexDirectionsExpr() {
		return indexDirectionsExpr;
	}


	public void setIndexDirectionsExpr(String indexDirectionsExpr) {
		this.indexDirectionsExpr = indexDirectionsExpr;
	}


	public Integer getLimit() {
		return limit;
	}


	public void setLimit(Integer limit) {
		this.limit = limit;
	}
	
	public String marshal2String() {
		
		//marshal
		StringWriter sw = new StringWriter();
		try {
			Marshaller.marshal(this, sw);
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		String marshaledItem = sw.toString().replaceAll("<\\?xml.*?\\?>","");	
		return marshaledItem;

	}
	
	public static HierarchicalReport unmarshal2POJO(String marshaledItem) {
		
		//unmarshal
		HierarchicalReport hierarchicalReport2 = null;
		try {
			hierarchicalReport2 = (HierarchicalReport) Unmarshaller.unmarshal(HierarchicalReport.class,new InputSource(new StringReader(marshaledItem)));
		} catch (MarshalException e) {
			e.printStackTrace();
		} catch (ValidationException e) {
			e.printStackTrace();
		}
		
		return hierarchicalReport2;

	}

}
