package com.amalto.core.objects.universe.ejb;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeSet;

import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.sun.org.apache.xalan.internal.xsltc.compiler.Pattern;

/**
 * @author Bruno Grieder
 * 
 * an Universe defines the revision IDs to use for the Xtentis objects
 * and items in the system
 * 
 */
public class UniversePOJO extends ObjectPOJO{
   
	
    private String name="[HEAD]";
    private String description="Default Universe where all the objects and items are taken from the head";
    private HashMap<String, String> xtentisObjectsRevisionIDs = new HashMap<String, String>();
    private LinkedHashMap<String, String> itemsRevisionIDs = new LinkedHashMap<String, String>();
    private String defaultItemRevisionID;
    
    // metadata
    private String birthTimeStamp, modifyTimeStamp;
    private String creator;
    private List<String> modifierList = new ArrayList<String>();
    /**
     * for marshaling purposes only
     */
    public UniversePOJO() {
    }
    

	public UniversePOJO(String name, String description, HashMap<String, String> xtentisObjectsRevisionIDs, LinkedHashMap<String, String> itemsRevisionIDs) {
	    super();
	    this.name = name;
	    this.description = description;
	    this.xtentisObjectsRevisionIDs = xtentisObjectsRevisionIDs;
	    this.itemsRevisionIDs = itemsRevisionIDs;
    }
	

	/**
	 * The unique name of this universe
	 * @return
	 * 		The unique name
	 */
	public String getName() {
    	return name;
    }

	/**
	 * Sets the unique name of this universe
	 * @param name
	 * 		The unique name
	 */
	public void setName(String name) {
    	this.name = name;
    }

	/**
	 * The optional description of this universe
	 * @return
	 * 		The description of the universe
	 * 
	 */
	public String getDescription() {
    	return description;
    }

	/**
	 * Sets the optional description of this Universe
	 * @param description
	 * 		The description
	 */
	public void setDescription(String description) {
    	this.description = description;
    }

	/**
	 * The birth date of the universe
	 * @return
	 *      the birth date of the universe
	 */
	public String getBirthTimeStamp(){
		return birthTimeStamp;
	}
	
	/**
	 * Set the birth date of the Universe
	 * @param timeStamp
	 *       the birth date
	 */
	
	public void setBirthTimeStamp(String timeStamp){
		birthTimeStamp = timeStamp;
	}
	
	/**
	 *  The originator of this universe
	 * @return
	 *     The originator of this universe
	 */
	public String getCreator() {
		return creator;
	}
    
	/**
	 * set the originator of this universe
	 * @param ct
	 *     originator of this universe
	 */
	public void setCreator(String ct) {
		creator = ct;
	}
	
	/**
	 * the modify timestamp of the universe
	 * @return
	 * the modify timestamp of the universe
	 */
	public String getModifyTimeStamp() {
		return modifyTimeStamp;
	}
	
	/**
	 * set the modify timestamp of the universe 
	 * @param timeStamp
	 * the modify timestamp of the universe
	 */
	public void setModifyTimeStamp(String timeStamp) {
		modifyTimeStamp = timeStamp;
	}
	
	/**
	 * all modifier of the universe
	 * @return
	 * all modifier of the universe
	 */
	public List<String> getModifierList() {
		return modifierList;
	}

	/**
	 * set all modifier of the universe
	 * @param lst
	 * all modifier of the universe
	 */
	public void setModifierList(List<String> lst) {
		modifierList = lst;
	}
	
	/**
	 * A Map of the Xtentis objects as key and revision IDs as value
	 * @return
	 * 		The {@link HashMap} of Xtentis objects revision ids
	 */
	public HashMap<String, String> getXtentisObjectsRevisionIDs() {
    	return xtentisObjectsRevisionIDs;
    }

	/**
	 * Sets the map of Xtentis objects revision ids<br/>
	 * The keys are the Xtentis object names obtained using {@link #getXtentisObjectName()}
	 * @param xtentisObjectsRevisionIDs
	 * 		A {@link HashMap} of Xtentis objects revision ids
	 */
	public void setXtentisObjectsRevisionIDs(HashMap<String, String> xtentisObjectsRevisionIDs) {
    	this.xtentisObjectsRevisionIDs = xtentisObjectsRevisionIDs;
    }

	/**
	 * Gets the revision IDs for the items<br/>
	 * The key is a {@link Pattern} that is matching the {@link ItemPOJOPK#getConceptName()}<br/>
	 * The first pattern that matches is selected and the revision ID is selected
	 * @return
	 * 		A {@link LinkedHashMap} of revision IDs for the items 
	 */
	public LinkedHashMap<String, String> getItemsRevisionIDs() {
    	return itemsRevisionIDs;
    }

	/**
	 * Sets the revision IDs for the items<br/>
	 * The key is a {@link Pattern} that is matching the {@link ItemPOJOPK#getConceptName()}<br/>
	 * The first pattern that matches is selected and the revision ID is selected
	 * @param itemsRevisionIDs
	 * 		A {@link LinkedHashMap} of revision IDs for the items 
	 */
	public void setItemsRevisionIDs(LinkedHashMap<String, String> itemsRevisionIDs) {
    	this.itemsRevisionIDs = itemsRevisionIDs;
    }

	
	
	/**
	 * The default Item revision ID when no pattern matches
	 * @see #getItemsRevisionIDs()
	 * @return
	 * 		The default Item Revision ID
	 */
	public String getDefaultItemRevisionID() {
    	return defaultItemRevisionID;
    }

	/**
	 * Sets the default ItemID to use when no pattern matches
	 * @see #setDefaultItemRevisionID(String)
	 * @param defaultItemRevisionID The default revision ID
	 */
	public void setDefaultItemRevisionID(String defaultItemRevisionID) {
    	this.defaultItemRevisionID = defaultItemRevisionID;
    }


	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	
	/**
	 * The list of Xtentis Object Names to use a a key to 
	 * the Xtentis Objects revision IDs {@link HashMap}
	 * @see #getXtentisObjectsRevisionIDs() 
	 * @return
	 * 		An ordered list of Xtentis Object Names
	 */
	public static TreeSet<String> getXtentisObjectName() {
		return new TreeSet<String>(
    		Arrays.asList(new String []{
    			"Data Model", 
    			"Role", 
    			"Stored Procedure", 
    			"Transformer V2", 
    			"View",
    			"Menu",
    			"Synchronization Plan",
    			"Routing Rule"
    		})
    	);
	}
	
	/**
	 * Determines the proper revision ID for a concept
	 * by scanning in order the patterns in {@link #getItemsRevisionIDs()}.<br/>
	 * If no pattern matches the value in {@link #getDefaultItemRevisionID()} is returned
	 * @param conceptName
	 * @return the revision ID to use for the concept
	 */
	public String getConceptRevisionID(String conceptName) {
		org.apache.log4j.Logger.getLogger(this.getClass()).trace("getConceptRevisionID() Universe '"+getName()+"' - concept '"+conceptName+"'");
		ArrayList<String> patterns = new ArrayList<String>(itemsRevisionIDs.keySet());
		for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
			String pattern = iterator.next();
			if (conceptName.matches(pattern)) return itemsRevisionIDs.get(pattern);
		}
		return getDefaultItemRevisionID();
	}
}
