package org.talend.mdm.webapp.itemsbrowser2.client.model;

import com.extjs.gxt.ui.client.data.BaseModel;

public class MapBean extends BaseModel {

	public MapBean(){}
	
	public MapBean(String name, String address){
		set("name", name);
		set("address", address);
	}
	
	public String getName(){
		return get("name");
	}
	
	public void setName(String name){
		set("name", name);
	}
	
	public String getAddress(){
		return get("address");
	}
	
	public void setAddress(String address){
		set("address", address);
	}
}
