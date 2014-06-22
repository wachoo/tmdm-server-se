package com.amalto.core.objects.view.ejb;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.ArrayListHolder;


/**
 * @author bgrieder
 * 
 */
public class ViewPOJO extends ObjectPOJO{
   
		
    private String name;
    private String description;
    private ArrayListHolder searchableBusinessElements;
    private ArrayListHolder viewableBusinessElements;
    private ArrayListHolder whereConditions;
    private String transformerPK;
    private boolean isTransformerActive;
    
    
    public String getTransformerPK() {
		return transformerPK;
	}
	public void setTransformerPK(String transformerPK) {
		this.transformerPK = transformerPK;
	}
	public boolean isTransformerActive() {
		return isTransformerActive;
	}
	public void setTransformerActive(boolean isTransformerActive) {
		this.isTransformerActive = isTransformerActive;
	}
	/**
     * 
     */
    public ViewPOJO() {
        this.searchableBusinessElements = new ArrayListHolder();
        this.viewableBusinessElements = new ArrayListHolder();
        this.whereConditions = new ArrayListHolder();
    }    
	public ViewPOJO(String name) {
		super();
		this.name = name;
	}
	

	/**
	 * @return Returns the Name.
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name The name to set.
	 */
	public void setName(String name) {
		this.name = name;
	}

	
	/**
	 * @return Returns the Description.
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * @param description The description to set.
	 */
	public void setDescription(String description) {
		this.description = description;
	}
	
	

	public ArrayListHolder getSearchableBusinessElements() {
		return searchableBusinessElements;
	}
	public void setSearchableBusinessElements(
			ArrayListHolder searchableBusinessElements) {
		this.searchableBusinessElements = searchableBusinessElements;
	}
	public ArrayListHolder getViewableBusinessElements() {
		return viewableBusinessElements;
	}
	public void setViewableBusinessElements(
			ArrayListHolder viewableBusinessElements) {
		this.viewableBusinessElements = viewableBusinessElements;
	}
	public ArrayListHolder getWhereConditions() {
		return whereConditions;
	}
	public void setWhereConditions(ArrayListHolder whereConditions) {
		this.whereConditions = whereConditions;
	}
	
	
	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	

}
