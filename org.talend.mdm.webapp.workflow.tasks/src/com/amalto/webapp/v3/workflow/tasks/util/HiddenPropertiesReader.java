package com.amalto.webapp.v3.workflow.tasks.util;



import java.io.InputStream;
import java.util.Properties;

public class HiddenPropertiesReader {
	
	private static final String configFileName="HiddenFieldConfig.properties";
	
	/** unique instance */
	private static HiddenPropertiesReader sInstance = null;
	
	private Properties configProperties=null;

	/** 
	 * Private constuctor
	 */
	private HiddenPropertiesReader() {
		super();
	}

	/** 
	 * Get the unique instance of this class.
	 */
	public static synchronized HiddenPropertiesReader getInstance() {

		if (sInstance == null) {
			sInstance = new HiddenPropertiesReader();
		}

		return sInstance;

	}

	private Properties getProperties() {
		
		if(configProperties==null){
			//reload
			configProperties = new Properties();
			try {
				InputStream is = getClass().getClassLoader().getResourceAsStream(configFileName);
				configProperties.load(is);
				if (is != null)
					is.close();
			} catch (Exception e) {
				System.out.println(e + " File " + configFileName + " not found");
				e.printStackTrace();
			}
		}else{
			
		}
		
		return configProperties;
	}
	
	public String getProperty(String key) {

		return getProperties().getProperty(key);
	}
	
	public static boolean isHidden(String varName) {
		boolean isHidden=false;
		
		if(varName.matches(HiddenPropertiesReader.getInstance().getProperty("fields.hidden.rule1"))){
			isHidden=true;
			return isHidden;
		}
		
		if(varName.matches(HiddenPropertiesReader.getInstance().getProperty("fields.hidden.rule2"))){
			isHidden=true;
			return isHidden;
		}
		
		return isHidden;
	}
	
	//test
	public static void main(String[] args) {
		
		System.out.println(isHidden("MDM_aa"));
		System.out.println(isHidden("aa_xpath"));
		
	}
	

}
