package com.amalto.webapp.v3.reporting.bean;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;

public class Reporting {

	private String reportingName;
	private String concept;
	private String cluster;
	private String format;
	private String owner;
	private boolean shared;
	private String pivotField;
	private String pivotXpath;
	private ReportingField[] fields;
	private ReportingFilter[] filters;
	private boolean erasable = true;
	private boolean unErasableButDeleteable = false;
	
	private String storedProcedure;
	private ReportingParameter[] parameters;
	
	
	public Reporting() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getConcept() {
		return concept;
	}

	public void setConcept(String document) {
		this.concept = document;
	}
	
	public String getFormat() {
		return format;
	}

	public void setFormat(String format) {
		this.format = format;
	}
	
	public String getPivotField() {
		return pivotField;
	}

	public void setPivotField(String pivotField) {
		this.pivotField = pivotField;
	}
	
	public String getPivotXpath() {
		return pivotXpath;
	}

	public void setPivotXpath(String pivotXpath) {
		this.pivotXpath = pivotXpath;
	}
	public ReportingField[] getFields() {
		return fields;
	}

	public void setFields(ReportingField[] fields) {
		this.fields = fields;
	}

	public ReportingFilter[] getFilters() {
		return filters;
	}

	public void setFilters(ReportingFilter[] filters) {
		this.filters = filters;
	}

	public String getReportingName() {
		return reportingName;
	}

	public void setReportingName(String reportingName) {
		this.reportingName = reportingName;
	}

	public static Reporting parse(String xml) {
		Reporting reporting = new Reporting();
		Element result;
		try {
			result = Util.parse(xml).getDocumentElement();
			reporting.setReportingName(Util.getFirstTextNode(result, "//ReportingName"));	
			reporting.setConcept(Util.getFirstTextNode(result, "//Concept"));
			reporting.setCluster(Util.getFirstTextNode(result, "//Cluster"));
			reporting.setFormat(Util.getFirstTextNode(result, "//Format"));
			reporting.setOwner(Util.getFirstTextNode(result, "//Owner"));
			reporting.setShared(Boolean.parseBoolean(Util.getFirstTextNode(result, "//Shared")));
			reporting.setPivotField(Util.getFirstTextNode(result, "//Pivot/Field"));
			reporting.setPivotXpath(Util.getFirstTextNode(result, "//Pivot/XPath"));
			reporting.setStoredProcedure(Util.getFirstTextNode(result, "//StoredProcedure/Name"));
//			if (reporting.getStoredProcedure()!=null && !reporting.getStoredProcedure().equals("")){
//				reporting.setErasable(false);
//				reporting.setUnErasableButDeleteable(true);
//			}
			
			NodeList fields = Util.getNodeList(result,"/Reporting/ListOfFields/DisplayField");
			if (fields!=null){
				ReportingField[] reportingFields = new ReportingField[fields.getLength()];
				for (int i=0;i<fields.getLength();i++){
					ReportingField  reportingField = new ReportingField();
					reportingField.setField(Util.getFirstTextNode(fields.item(i),"./Field"));
					reportingField.setXpath(Util.getFirstTextNode(fields.item(i),"./XPath"));
					reportingFields[i] = reportingField;					
				}
				reporting.setFields(reportingFields);
			}				
			NodeList filters = Util.getNodeList(result,"/Reporting/ListOfFilters/Filter");
			if (filters!=null){
				ReportingFilter[] reportingFilters = new ReportingFilter[filters.getLength()];
				for (int i=0;i<filters.getLength();i++){
					ReportingFilter  reportingFilter = new ReportingFilter();
					reportingFilter.setField(Util.getFirstTextNode(filters.item(i),"./Field"));
					reportingFilter.setXpath(Util.getFirstTextNode(filters.item(i),"./XPath"));
					reportingFilter.setOperator(Util.getFirstTextNode(filters.item(i),"./Operator"));
					reportingFilter.setValue(Util.getFirstTextNode(filters.item(i),"./Value"));
					reportingFilters[i] = reportingFilter;					
				}
				reporting.setFilters(reportingFilters);
			}
			NodeList parameters = Util.getNodeList(result,"/Reporting/StoredProcedure/ListOfParameters/Parameter");
			if (parameters!=null){
				List<ReportingParameter> reportingParameters = new ArrayList<ReportingParameter>();
				for (int i=0;i<parameters.getLength();i++){
					
					if(Util.getFirstTextNode(parameters.item(i),"./Name")!=null){
						ReportingParameter  reportingParameter = new ReportingParameter();
						reportingParameter.setName(Util.getFirstTextNode(parameters.item(i),"./Name"));
						reportingParameter.setDescription(Util.getFirstTextNode(parameters.item(i),"./Description"));
						reportingParameter.setType(Util.getFirstTextNode(parameters.item(i),"./Type"));
						reportingParameters.add(reportingParameter);
					}
										
				}
				reporting.setParameters(reportingParameters.toArray(new ReportingParameter[reportingParameters.size()]));
			}	
		} catch (Exception e) {
			e.printStackTrace();
		}
		return reporting;		
	}
	
	public String serialize(){
		StringBuffer xml = new StringBuffer();
        xml.append("<Reporting>");
        xml.append("<ReportingName>"+StringEscapeUtils.escapeXml(reportingName)+"</ReportingName>");
        xml.append("<Concept>"+StringEscapeUtils.escapeXml(concept)+"</Concept>");
        xml.append("<Cluster>"+StringEscapeUtils.escapeXml(cluster)+"</Cluster>");
        xml.append("<Format>"+StringEscapeUtils.escapeXml(format)+"</Format>");
        xml.append("<Owner>"+StringEscapeUtils.escapeXml(owner)+"</Owner>");
        xml.append("<Shared>"+shared+"</Shared>");
        xml.append("<Pivot><Field>"+StringEscapeUtils.escapeXml(pivotField)+"</Field><XPath>"+StringEscapeUtils.escapeXml(pivotXpath)+"</XPath></Pivot>");
        
        if (storedProcedure!=null && !"".equals(storedProcedure)) {
            xml.append("<StoredProcedure>");
            xml.append("<Name>"+StringEscapeUtils.escapeXml(storedProcedure)+"</Name>");
            xml.append("<ListOfParameters>");
            if(parameters!=null){
            	for(int i=0;i<parameters.length;i++) {
                	xml.append("<Parameter>");
                	xml.append("<Name>"+StringEscapeUtils.escapeXml(parameters[i].getName())+"</Name>");
                	xml.append("<Description>"+StringEscapeUtils.escapeXml(parameters[i].getDescription())+"</Description>");
                	xml.append("<Type>"+StringEscapeUtils.escapeXml(parameters[i].getType())+"</Type>");
                	xml.append("</Parameter>");
                }
            }
            xml.append("</ListOfParameters>");
            xml.append("</StoredProcedure>");
        }
        
        xml.append("<ListOfFields>");
        for(int i=0;i<fields.length;i++) {
        	xml.append("<DisplayField>");
        	xml.append("<Field>"+fields[i].getField()+"</Field>");
        	xml.append("<XPath>"+fields[i].getXpath()+"</XPath>");
        	xml.append("</DisplayField>");
        }
        xml.append("</ListOfFields>");
        xml.append("<ListOfFilters>");
        for(int i=0;i<filters.length;i++) {
        	xml.append("<Filter>");
        	xml.append("<Field>"+filters[i].getField()+"</Field>");
        	xml.append("<XPath>"+filters[i].getXpath()+"</XPath>");
        	xml.append("<Operator>"+filters[i].getOperator()+"</Operator>");
        	xml.append("<Value>"+filters[i].getValue()+"</Value>");
        	xml.append("</Filter>");
        }
        xml.append("</ListOfFilters>");
        xml.append("</Reporting>");
        return xml.toString();
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public boolean isShared() {
		return shared;
	}

	public void setShared(boolean shared) {
		this.shared = shared;
	}

	public boolean isErasable() {
		return erasable;
	}

	public void setErasable(boolean erasable) {
		this.erasable = erasable;
	}
	
	public boolean isUnErasableButDeleteable() {
		return unErasableButDeleteable;
	}

	public void setUnErasableButDeleteable(boolean unErasableButDeleteable) {
		this.unErasableButDeleteable = unErasableButDeleteable;
	}

	public String getCluster() {
		return cluster;
	}

	public void setCluster(String cluster) {
		this.cluster = cluster;
	}

	public ReportingParameter[] getParameters() {
		return parameters;
	}

	public void setParameters(ReportingParameter[] parameters) {
		this.parameters = parameters;
	}

	public String getStoredProcedure() {
		return storedProcedure;
	}

	public void setStoredProcedure(String name) {
		this.storedProcedure = name;
	}




}
