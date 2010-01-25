package com.amalto.core.objects.datamodel.ejb;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.schema.manage.SchemaManager;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 */
public class DataModelPOJO extends ObjectPOJO{
   
		
    private String name;
    private String description;
    private String schema;
    
    
    /**
     * 
     */
    public DataModelPOJO() {
    }    
	public DataModelPOJO(String name) {
		super();
		this.name = name;
	}
	public DataModelPOJO(String name, String desc,String schema){
		this.name=name;
		this.description=desc;
		this.schema=schema;
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
	 * @return the xsd Schema
	 */
	public String getSchema() {
		return schema;
	}
	
	public void setSchema(String schema) {
		this.schema = schema;
	}
	
	
	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	

	@Override
	public ObjectPOJOPK store() throws XtentisException {
		ObjectPOJOPK pk=super.store();
		//update the cache
		if (getName()!=null)SchemaManager.resetParsedDatamodelCache(getName());
		return pk;
	}
	

}
