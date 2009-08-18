package com.amalto.core.ejb;




/**
 * @author Bruno Grieder
 * @deprecated - use TransformerV2 package
 * 
 */
public class TransformerPluginPOJO extends ObjectPOJO{
   
	
    private String name;
    private String configuration;
    private String serviceData;

    
    
    /**
     * 
     */
    public TransformerPluginPOJO() {
        super();
    }
    
	public TransformerPluginPOJO(String name, String configuration, String serviceData) {
		super();
		this.name = name;
		this.configuration = configuration;
		this.serviceData = serviceData;
	}

	
	@Override
	public ObjectPOJOPK getPK() {
		return new ObjectPOJOPK(new String[] {name});
	}

	public String getName() {
		return name;
	}
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
