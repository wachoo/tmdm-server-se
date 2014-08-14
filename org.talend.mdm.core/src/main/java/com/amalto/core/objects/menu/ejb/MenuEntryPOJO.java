package com.amalto.core.objects.menu.ejb;

import java.util.ArrayList;
import java.util.HashMap;



/**
 * @author bgrieder
 * 
 */
public class MenuEntryPOJO {
   
	
	
    private String id;
    private HashMap<String,String> descriptions = new HashMap<String, String>();
    private String context;
    private String application;
    private String icon;
    private ArrayList<MenuEntryPOJO> subMenus = new ArrayList<MenuEntryPOJO>();
    

    public MenuEntryPOJO() {}
    
	public MenuEntryPOJO(String id) {
		super();
		this.id = id;
	}


	public MenuEntryPOJO(String id, HashMap<String, String> descriptions, String context, String application, ArrayList<MenuEntryPOJO> subMenus) {
		super();
		this.id = id;
		this.descriptions = descriptions;
		this.context = context;
		this.application = application;
		this.subMenus = subMenus;
	}
	
	public MenuEntryPOJO(String id, HashMap<String, String> descriptions, String context, String application, ArrayList<MenuEntryPOJO> subMenus,String icon) {
		super();
		this.id = id;
		this.descriptions = descriptions;
		this.context = context;
		this.application = application;
		this.subMenus = subMenus;
		this.icon=icon;
	}	


	public String getApplication() {
		return application;
	}

	public void setApplication(String application) {
		this.application = application;
	}

	public String getContext() {
		return context;
	}

	public void setContext(String context) {
		this.context = context;
	}

	public HashMap<String, String> getDescriptions() {
		return descriptions;
	}

	public void setDescriptions(HashMap<String, String> descriptions) {
		this.descriptions = descriptions;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public ArrayList<MenuEntryPOJO> getSubMenus() {
		return subMenus;
	}

	public void setSubMenus(ArrayList<MenuEntryPOJO> subMenus) {
		this.subMenus = subMenus;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	

}
