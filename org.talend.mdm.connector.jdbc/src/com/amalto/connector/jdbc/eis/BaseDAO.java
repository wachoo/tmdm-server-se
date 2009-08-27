package com.amalto.connector.jdbc.eis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.connector.jca.XtentisConnectorException;
import com.amalto.connector.jdbc.eis.common.BasicPrintStreamed;
import com.amalto.connector.jdbc.eis.common.TableObject;
import com.amalto.connector.mail.Util;

public class BaseDAO extends SqlAgent{
	
	private static Logger logger = Logger.getLogger(BaseDAO.class);
	
	//TODO add select function
	private static final String OPERATIONTYPE_SELECT = "SELECT";
	
	private static final String OPERATIONTYPE_INSERT = "INSERT";
	
	private static final String OPERATIONTYPE_UPDATE = "UPDATE";
	
	private static final String OPERATIONTYPE_DELETE = "DELETE"; 
	
	public BaseDAO(String driverClassName, String url) {
		super(driverClassName,url);
	}
	
	public BaseDAO(String driverClassName,Properties properties, String url) {
		super(driverClassName,properties,url);
	}
	
	
	public BaseDAO(String driverClassName,String url, String username,String password) {
		super(driverClassName,url,username,password);
	}
	
	public String showTables() throws XtentisConnectorException {
		String result="";
		logger.debug("Show tables ");
		
        //do desc
		super.exeShowTablesCommand();
		//get result
		HashMap printStreamed=super.getPrintStreamed();
		BasicPrintStreamed basicPrintStreamed=(BasicPrintStreamed) printStreamed.get(SqlAgent.SHOWTABLE_PS);
		result=basicPrintStreamed.getLocalBuffer().toString();
		basicPrintStreamed.flushLocalBuffer();
		
		return result;
	}
	
	public String describeTable(String tableName) throws XtentisConnectorException {
		String result="";
		logger.debug("Describe "+tableName);
		
        //do desc
		super.exeDescribeCommand(tableName);
		//get result
		HashMap printStreamed=super.getPrintStreamed();
		BasicPrintStreamed basicPrintStreamed=(BasicPrintStreamed) printStreamed.get(SqlAgent.DESCRIBE_PS);
		result=basicPrintStreamed.getLocalBuffer().toString();
		basicPrintStreamed.flushLocalBuffer();
		
		return result;
	}
	
	public String select(String selectSql) throws XtentisConnectorException {
		String result="";
		logger.debug("Select: "+selectSql);
		
        //do select
		super.exeSelectCommand(selectSql);
		//get result
		HashMap printStreamed=super.getPrintStreamed();
		BasicPrintStreamed basicPrintStreamed=(BasicPrintStreamed) printStreamed.get(SqlAgent.SELECT_PS);
		result=basicPrintStreamed.getLocalBuffer().toString();
		basicPrintStreamed.flushLocalBuffer();
		
		return result;
	}
	
	public void update(String todoXml) throws XtentisConnectorException {
		if(todoXml==null||todoXml.length()==0)return;
		Document actionDoc = Util.parse(todoXml);
        String operationType = Util.getFirstTextNode(actionDoc, "//operation"); 
		//parse operation type
		if(operationType==null||operationType.length()==0)throw new XtentisConnectorException("Parse update action failed! ");
		if(operationType.equalsIgnoreCase(OPERATIONTYPE_INSERT)){
			String content = Util.getFirstTextNode(actionDoc, "//part/content"); 
			doInsert(content);
		}else if(operationType.equalsIgnoreCase(OPERATIONTYPE_UPDATE)){
			String content = Util.getFirstTextNode(actionDoc, "//part/content"); 
			doUpdate(content);
			
		}else if(operationType.equalsIgnoreCase(OPERATIONTYPE_DELETE)){
			String content = Util.getFirstTextNode(actionDoc, "//part/content"); 
			doDelete(content);
	
		}

	}
	
	private void doInsert(String insertXmlContent) throws XtentisConnectorException {
		if(insertXmlContent==null||insertXmlContent.length()==0)return;
		String actionContent=StringEscapeUtils.unescapeXml(insertXmlContent);
		//build xml 2 sql And parse mapping rule
		List<TableObject> tableObjects = buildTableObjects(actionContent);
		
		for (int i = 0; i < tableObjects.size(); i++) {
			TableObject tableObject=tableObjects.get(i);
			
			String insertSql=" insert into ";
			
			insertSql+=tableObject.getTableName();
			
			String fields=" (";
			String values=" values (";
			Iterator<String> fieldsIterator=tableObject.getFieldsIterator();
			for (int j=0;fieldsIterator.hasNext();j++) {
				String fieldName = (String) fieldsIterator.next();
				String fieldValue= tableObject.getFieldValue(fieldName);
				
				if(j<tableObject.getFieldsCount()-1){
					fields+=fieldName+",";
					values+=fieldValue+",";
				}else{
					fields+=fieldName;
					values+=fieldValue;
				}
				
			}			
			fields+=") ";
			values+=") ";
			
			insertSql=insertSql+fields+values;
			logger.debug(insertSql);
            //do insert
			super.exeUpdateCommand(insertSql);
		}

	}
	
	private void doUpdate(String insertXmlContent) throws XtentisConnectorException {
		if(insertXmlContent==null||insertXmlContent.length()==0)return;
		String actionContent=StringEscapeUtils.unescapeXml(insertXmlContent);
		
		//build xml 2 sql And parse mapping rule
		List<TableObject> tableObjects = buildTableObjects(actionContent);
		
		for (int i = 0; i < tableObjects.size(); i++) {
			TableObject tableObject=tableObjects.get(i);
			
			String updateSql=" update ";
			
			updateSql+=tableObject.getTableName();
			
			String set=" set ";
			String where=" where 1=1 and ";
			Iterator<String> fieldsIterator=tableObject.getFieldsIterator();
			for (;fieldsIterator.hasNext();) {
				String fieldName = (String) fieldsIterator.next();
				String fieldValue = tableObject.getFieldValue(fieldName);
				boolean isKeyField = tableObject.isKeyField(fieldName);
				
				if(!isKeyField)set+=fieldName+"="+fieldValue+",";
				if(isKeyField)where+=fieldName+"="+fieldValue+" and ";
				
			}
			if(set.lastIndexOf(",")!=-1)set=set.substring(0, set.lastIndexOf(","));
			if(where.lastIndexOf(" and ")!=-1)where=where.substring(0, where.lastIndexOf(" and "));
			
			updateSql=updateSql+set+where;
			logger.debug(updateSql);
            //do update
			super.exeUpdateCommand(updateSql);
		}

	}
	
	private void doDelete(String insertXmlContent) throws XtentisConnectorException {
		if(insertXmlContent==null||insertXmlContent.length()==0)return;
		String actionContent=StringEscapeUtils.unescapeXml(insertXmlContent);
		
		//build xml 2 sql And parse mapping rule
		List<TableObject> tableObjects = buildTableObjects(actionContent);
		
		for (int i = 0; i < tableObjects.size(); i++) {
			TableObject tableObject=tableObjects.get(i);
			
			String deleteSql=" delete from ";
			
			deleteSql+=tableObject.getTableName();
			
			String where=" where 1=1 and ";
			Iterator<String> fieldsIterator=tableObject.getFieldsIterator();
			for (;fieldsIterator.hasNext();) {
				String fieldName = (String) fieldsIterator.next();
				String fieldValue = tableObject.getFieldValue(fieldName);
				
				where+=fieldName+"="+fieldValue+" and ";
				
			}
			if(where.lastIndexOf(" and ")!=-1)where=where.substring(0, where.lastIndexOf(" and "));
			
			deleteSql=deleteSql+where;
			logger.debug(deleteSql);
            //do update
			super.exeUpdateCommand(deleteSql);
		}

	}

	private List<TableObject> buildTableObjects(String actionContent)
			throws XtentisConnectorException {
		List<TableObject> tableObjects=new ArrayList<TableObject>(); 
		            
		Document actionContentDoc=Util.parse(actionContent);
		NodeList conceptList=Util.getNodeList(actionContentDoc, "//*[@mapping-table]");
		for (int i = 0; i < conceptList.getLength(); i++) {
			Node concept=conceptList.item(i);
			String tableName=Util.getFirstTextNode(concept, "./@mapping-table");
			
			TableObject tableObject=new TableObject(tableName);
			
			NodeList elementList=Util.getNodeList(concept, "./*[@mapping-field]");
			for (int j = 0; j < elementList.getLength(); j++) {
				
				Node elem=elementList.item(j);
				String fieldName=Util.getFirstTextNode(elem, "./@mapping-field");
				String fieldValue=elem.getTextContent();
				String isKeyField=Util.getFirstTextNode(elem, "./@key-field");
				tableObject.addField(fieldName, fieldValue, isKeyField);
				
			}
			
			tableObjects.add(tableObject);
		}
		return tableObjects;
	}



}
