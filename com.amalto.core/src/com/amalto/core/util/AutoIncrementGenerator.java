package com.amalto.core.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Properties;

/**
 * 
 * AutoIncrement to generate a num
 * the autoincrement num is saved in auto_increment.conf file
 * @author achen
 *
 */
public class AutoIncrementGenerator {
	//static volatile long  num=-1;	

	static File file = new File("auto_increment.conf");
	
	private static Properties CONFIGURATION = null;
	static{
		//first try Current path
		CONFIGURATION = new Properties();
		if (file.exists()) {
			try {
				CONFIGURATION.load(new FileInputStream(file));				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} 	
	}
	/**
	 * this is not a good alogrithm, need to find a better way
	 * @return
	 */
	public synchronized static long  generateNum(String universe,String dataCluster,String conceptName){
		String key=universe+"."+dataCluster+"."+conceptName;
		long num=0;		
		String n=CONFIGURATION.getProperty(key);
		if(n==null){
			num=0;				
		}else{
			num=Long.valueOf(n).longValue();
		}
		num++;
			
		CONFIGURATION.setProperty(key, String.valueOf(num));				
		return num;
	}
	/**
	 * save configure file
	 */
	public static void saveToFile(){
		FileOutputStream out;
		try {
			out = new FileOutputStream(file);
			CONFIGURATION.store(out, "AUTO GENERATED FILE DON'T DELETE auto_increment configure file");
		} catch (Exception e) {
			String err = "AUTOINCREMENT Configuration: unable to save the configuration in '"+file.getAbsolutePath()+"' :"+e.getMessage(); 
			org.apache.log4j.Logger.getLogger(AutoIncrementGenerator.class).error(err,e);
		}		
	}	
}
