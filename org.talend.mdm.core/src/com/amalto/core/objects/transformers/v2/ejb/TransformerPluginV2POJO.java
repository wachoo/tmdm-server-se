package com.amalto.core.objects.transformers.v2.ejb;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;



/**
 * @author bgrieder
 * 
 */
public class TransformerPluginV2POJO extends ObjectPOJO{
   
		
    private String name;
    private String configuration;
    private String serviceData;
   
    
    /**
     * 
     */
    public TransformerPluginV2POJO() {
        super();
    }
    
	public TransformerPluginV2POJO(String name, String configuration, String serviceData) {
		super();
		this.name = name;
		this.configuration = configuration;
		this.serviceData = serviceData;
	}

	
	@Override
	public ObjectPOJOPK getPK() {
		return new ObjectPOJOPK(new String[] {name});
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
    
    public String getConfiguration() {
		return configuration;
	}

	public void setConfiguration(String configuration) {
		this.configuration = configuration;
	}

	public String getServiceData() {
		return serviceData;
	}

	public void setServiceData(String serviceData) {
		this.serviceData = serviceData;
	}


 

}
