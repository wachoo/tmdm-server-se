package com.amalto.core.objects.menu;

import java.util.ArrayList;

import com.amalto.core.objects.ObjectPOJO;
import com.amalto.core.objects.ObjectPOJOPK;

/**
 * @author Bruno Grieder
 * 
 */
public class MenuPOJO extends ObjectPOJO{
   
	
    private String name;
    private String description;
    private ArrayList<MenuEntryPOJO> menuEntries =new ArrayList<MenuEntryPOJO>();
    

    public MenuPOJO() {}
    
	public MenuPOJO(String name) {
		super();
		this.name = name;
	}


	public MenuPOJO(String name, String description, ArrayList<MenuEntryPOJO> menuEntries) {
		super();
		this.name = name;
		this.description = description;
		this.menuEntries = menuEntries;
	}
	
	
	

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ArrayList<MenuEntryPOJO> getMenuEntries() {
		return menuEntries;
	}

	public void setMenuEntries(ArrayList<MenuEntryPOJO> menuEntries) {
		this.menuEntries = menuEntries;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	

}
