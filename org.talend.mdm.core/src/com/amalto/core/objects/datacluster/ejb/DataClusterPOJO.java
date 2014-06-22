package com.amalto.core.objects.datacluster.ejb;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.metadata.LongString;

/**
 * @author Bruno Grieder
 * 
 */
public class DataClusterPOJO extends ObjectPOJO{

    private String name;
    private String description;
    private String vocabulary;

    /**
     * 
     */
    public DataClusterPOJO() {
    }

	public DataClusterPOJO(String revisionID, String name) {
		super();
		this.name = name;
	}

	public DataClusterPOJO(String name, String description, String vocabulary) {
		super();
		this.name = name;
		this.description = description;
		this.vocabulary = vocabulary;
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
	
	/**
	 * 
	 * @return the xsd Vocabulary
	 */
    @LongString
	public String getVocabulary() {
		return vocabulary;
	}
	
	public void setVocabulary(String vocabulary) {
		this.vocabulary = vocabulary;
	}

	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
}
