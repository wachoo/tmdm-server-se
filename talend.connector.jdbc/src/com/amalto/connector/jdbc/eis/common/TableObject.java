package com.amalto.connector.jdbc.eis.common;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TableObject {
	
	private String tableName;
	
	private Map<String,TableField> fieldMap;
	
	public TableObject(String tableName) {
		this.tableName=tableName;
		this.fieldMap=new HashMap<String,TableField>();
	}

	public String getTableName() {
		return tableName;
	}

	public void addField(String fieldName,String fieldValue) {
		this.fieldMap.put(fieldName,new TableField(fieldName, fieldValue));
	}
	
	public void addField(String fieldName,String fieldValue,boolean isKeyField) {
		this.fieldMap.put(fieldName,new TableField(fieldName, fieldValue, isKeyField));
	}
	
	public void addField(String fieldName,String fieldValue,String isKeyField) {
		if(isKeyField==null||isKeyField.equals(""))isKeyField="false";
		this.fieldMap.put(fieldName,new TableField(fieldName, fieldValue, Boolean.parseBoolean(isKeyField)));
	}
	
	public String getFieldValue(String fieldName) {
		Object fieldObj = this.fieldMap.get(fieldName);
		
		if(fieldObj!=null){
			TableField tableField=(TableField)fieldObj;
			return tableField.getFieldValue();
		}
		
		return "";
	}
	
	public boolean isKeyField(String fieldName) {
		Object fieldObj = this.fieldMap.get(fieldName);
		
		if(fieldObj!=null){
			TableField tableField=(TableField)fieldObj;
			return tableField.isKey();
		}
		
		return false;
	}
	
	public Iterator<String> getFieldsIterator() {
		
		return this.fieldMap.keySet().iterator();

	}
	
    public int getFieldsCount() {
		
		return this.fieldMap.size();

	}
	
}
