package org.talend.mdm.ext.publish.filter;



import java.io.InputStream;
import java.util.Properties;

public class AccessControlPropertiesReader {
	
	private static final String configFileName="AccessControllerConfig.properties";
	
	/** unique instance */
	private static AccessControlPropertiesReader sInstance = null;
	
	private Properties configProperties=null;

	/** 
	 * Private constuctor
	 */
	private AccessControlPropertiesReader() {
		super();
	}

	/** 
	 * Get the unique instance of this class.
	 */
	public static synchronized AccessControlPropertiesReader getInstance() {

		if (sInstance == null) {
			sInstance = new AccessControlPropertiesReader();
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
	
	//test
	public static void main(String[] args) {
		
		System.out.println(AccessControlPropertiesReader.getInstance().getProperty("datamodel.name.ban.pattern"));
		
	}
	

}
