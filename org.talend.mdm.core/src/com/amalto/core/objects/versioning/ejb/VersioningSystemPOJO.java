package com.amalto.core.objects.versioning.ejb;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;



/**
 * @author bgrieder
 * 
 */
public class VersioningSystemPOJO extends ObjectPOJO{
   
    private String name;
    private String jndi;
    private String description;
    private String url;
    private String username;
    private String password;
    private String autocommit="false";
    
    /**
     * 
     */
    public VersioningSystemPOJO() {
        super();
    }
    

	
	public VersioningSystemPOJO(String name, String jndi, String description, String url, String username, String password, String autocommit) {
		super();
		this.name = name;
		this.jndi = jndi;
		this.description = description;
		this.url = url;
		this.username = username;
		this.password = password;
		this.autocommit=autocommit;
	}



	public String getAutocommit() {
		return autocommit;
	}



	public void setAutocommit(String autocommit) {
		this.autocommit = autocommit;
	}



	@Override
	public ObjectPOJOPK getPK() {
		return new ObjectPOJOPK(new String[] {name});
	}



	public String getName() {
		return name;
	}



	public String getJndi() {
		return jndi;
	}



	public void setJndi(String jndi) {
		this.jndi = jndi;
	}



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



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}



	public String getUrl() {
		return url;
	}



	public void setUrl(String url) {
		this.url = url;
	}



	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}
	





 

}
