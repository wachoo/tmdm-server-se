package com.amalto.webapp.core.util;

import java.util.HashMap;
import java.util.Map;

public enum XSystemObjects {

	// Data Clusters
	DC_JCAADAPTERS(XObjectType.DATA_CLUSTER, "JCAAdapters"), 
	DC_INBOX(XObjectType.DATA_CLUSTER, "Inbox"),
	//DC_BUG_TRACKING(XObjectType.DATA_CLUSTER, "Bug Tracking"), 
	DC_CONF(XObjectType.DATA_CLUSTER, "CONF"), 
	//DC_MDMCONF(XObjectType.DATA_CLUSTER, "MDMCONF"), 
	DC_PROVISIONING(XObjectType.DATA_CLUSTER, "PROVISIONING"), 
	DC_UPDATE_PREPORT(XObjectType.DATA_CLUSTER, "UpdateReport"), 
	//DC_XTENTIS_COMMON_CONF(XObjectType.DATA_CLUSTER, "Xtentis Common Conf"), 
	DC_XTENTIS_COMMON_REPORTING(XObjectType.DATA_CLUSTER, "Reporting"),
	DC_MDMITEMSTRASH(XObjectType.DATA_CLUSTER,"MDMItemsTrash"),

	// Data Models
	//DM_BUG_TRACKING(XObjectType.DATA_MODEL, "Bug Tracking"), 
	DM_CONF(XObjectType.DATA_MODEL, "CONF"), 
	DM_PROVISIONING(XObjectType.DATA_MODEL, "PROVISIONING"), 
	//DM_REPORTING(XObjectType.DATA_MODEL, "REPORTING"), 
	DM_UPDATEREPORT(XObjectType.DATA_MODEL, "UpdateReport"), 
	//DM_XTENTIS_COMMON_CONF(XObjectType.DATA_MODEL, "Xtentis Common Conf"), 
	DM_XTENTIS_COMMON_REPORTING(XObjectType.DATA_MODEL, "Reporting"),

	// Menus

	M_BROWSE_ITEMS(XObjectType.MENU, "Browse items"), 
	M_BROWSE_VIEWS(XObjectType.MENU, "Browse views"), 
	M_MANAGER_USERS(XObjectType.MENU, "Manage users"), 
	M_REPORTING(XObjectType.MENU,"Reporting"), 
	M_SYNCHRONIZATIONACTION(XObjectType.MENU,"SynchronizationAction"), 
	M_SYNCHRONIZATIONPLAN(XObjectType.MENU,"SynchronizationItem"),
	M_DATA_CHANGES(XObjectType.MENU,"Data changes"),
	M_UPDATE_REPORT(XObjectType.MENU,"UpdateReport"),
	M_SERVICE_SCHEDULE(XObjectType.MENU,"Service Schedule"),

	// roles
	R_BOLLORE_CONTRIBUTOR(XObjectType.ROLE, "BOLLORE_Contributor");

	// Views

	//V_MDM_REPORTING_VIEW(XObjectType.VIEW, "MDM Reporting View"), 
	//V_XTENTIS_MDM_REPORTING(XObjectType.VIEW, "Xtentis MDM Reporting"), 
	//V_XTENTIS_PORTAL_ROLE_VIEW(XObjectType.VIEW, "XTENTIS PORTAL ROLE VIEW");

	XSystemObjects(int type, String name) {
		this.name = name;
		this.type = type;
	}

	XSystemObjects() {
	};

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	private String name;

	private int type;

	// key is the type
	/**
	 * get all the elements
	 * 
	 * @return map
	 */
	public static Map<String, XSystemObjects> getXSystemObjects() {

		Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
		for (int i = 0; i < values().length; i++) {
			map.put(values()[i].getType()+"_"+String.valueOf(values()[i].getName()), values()[i]);
		}
		return map;
	}

	/**
	 * get the "type" elements
	 * 
	 * @param type
	 * @return map
	 */
	public static Map<String, XSystemObjects> getXSystemObjects(int type) {

		Map<String, XSystemObjects> map = new HashMap<String, XSystemObjects>();
		for (int i = 0; i < values().length; i++) {
			if (type == values()[i].getType())
				map.put(String.valueOf(values()[i].getName()), values()[i]);
		}
		return map;
	}
	
	/**
	 * @param type
	 * @param objectPK
	 * @return
	 */
	public static boolean isXSystemObject(int type,String objectPK) {

		Map<String, XSystemObjects> map = getXSystemObjects(type);
		
		return isXSystemObject(map,type,objectPK);
	}
	
	/**
	 * @param map
	 * @param type
	 * @param objectPK
	 * @return
	 */
	public static boolean isXSystemObject(Map<String, XSystemObjects> map,int type,String objectPK) {

		if(map.get(objectPK)!=null){
			return true;
		}
		return false;
	}

	/**
	 * chenk if the element is exist
	 * 
	 * @param type
	 * @param name
	 * @return boolean
	 */
	public static boolean isExist(int type, String name) {
		boolean is = false;
		for (int i = 0; i < values().length; i++) {
			if (type == values()[i].getType() && name.equals(values()[i].name)) {
				is = true;
				break;
			}
		}
		return is;
	}
	
}
