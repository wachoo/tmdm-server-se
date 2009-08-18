package com.amalto.workbench.utils;

public interface IConstants {
	/*Conditions operator*/
	static final String[] VIEW_CONDITION_OPERATORS={"Contains","Contains Text Of","Starts With","Strict Contains","=","!=",">",">=","<","<="};
	static final String[] ROUTE_CONDITION_OPERATORS={"Contains","Matches","Starts With","Is Null","Is Not Null","=","!=",">",">=","<","<="};
	
	static final String[] PREDICATES={"","Or","And","Strict And","Exactly","Not"};
	/**
	 * The default universe name
	 */
	static final String HEAD="HEAD";
	
	static final String TALEND="Talend Open MDM";
	
	//static final String TALEND_MDM="Talend MDM";
	/**
	 * Used for Update Report
	 */
	public final static String SOURCE_GENERICUI="genericUI";

	public final static String SOURCE_ADMINWORKBENCH="adminWorkbench";
	
	public final static String SOURCE_DATASYNCHRONIZATION="dataSynchronization";
	
	public final static String OPERATIONTYPE_CREATE="CREATE";
	
	public final static String OPERATIONTYPE_UPDATEE="UPDATE";
	
	public final static String OPERATIONTYPE_DELETE="DELETE";
	
	//public final static String OPERATIONTYPE_SYNCHRONIZE="SYNCHRONIZE";
	
	/**
	 * Used for exist admin user/passwd
	 */
	public final static String EXIST_ADMIN="admin";
	public final static String EXIST_ADMIN_PASSWD="1bc29b36f623ba82aaf6724fd3b16718";
	public final static String EXIST_PORT="8080";
	
}
