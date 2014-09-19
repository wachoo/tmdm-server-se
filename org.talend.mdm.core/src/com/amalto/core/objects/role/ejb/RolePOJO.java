package com.amalto.core.objects.role.ejb;

import java.util.HashMap;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.util.RoleSpecification;
import com.amalto.core.util.XtentisException;


/**
 * @author Bruno Grieder
 * A role with the specifications for each Object Type
 * 
 */
public class RolePOJO extends ObjectPOJO{
   
		
    private String name;
    private String description;
    private HashMap<String,RoleSpecification> specifications;
    
    
    /**
     * 
     */
    public RolePOJO() {
        this.specifications = new HashMap<String,RoleSpecification>();
    }    
	public RolePOJO(String name) {
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
	
	/**
	 * The specifications of the role<br/>
	 * The key is the Object Type
	 */
	public HashMap<String, RoleSpecification> getRoleSpecifications() {
		return specifications;
	}
	/**
	 * Sets the specifications of the role for the Object Types
	 * @param specifications
	 */
	public void setRoleSpecifications(HashMap<String,RoleSpecification> specifications) {
		this.specifications = specifications;
	}
	
	
	@Override
	public ObjectPOJOPK getPK() {
		if (getName()==null) return null;
		return new ObjectPOJOPK(new String[] {name});
	}
	
    @Override
    public ObjectPOJOPK store() throws XtentisException {
        /*if ("authenticated".equals(getName())) throw new XtentisException("The role \"authenticated\" is reserved and cannot be created");
    	if ("administration".equals(getName())) throw new XtentisException("The role \"administration\" is reserved and cannot be created");
    	if ("disabled".equals(getName())) throw new XtentisException("The role \"disabled\" is reserved and cannot be created");*/
    	return super.store();
    }
	
	

}
