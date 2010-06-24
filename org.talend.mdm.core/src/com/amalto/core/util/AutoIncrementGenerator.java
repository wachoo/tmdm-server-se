package com.amalto.core.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Properties;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;

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
	public static String AUTO_INCREMENT="Auto_Increment";
	private static Properties CONFIGURATION = null;
	static DataClusterPOJOPK DC=new DataClusterPOJOPK(XSystemObjects.DC_CONF.getName());
	static String[] IDS=new String[] {"AutoIncrement"};
	static String CONCEPT="AutoIncrement";
	static{
		init();
	}
	public static void init(){
		//first try Current path
		CONFIGURATION = new Properties();
		//load from db
		try {
//			String xml=Util.getXmlServerCtrlLocal().getDocumentAsString(null, XSystemObjects.DC_CONF.getName(), AUTO_INCREMENT);
			ItemPOJOPK pk=new ItemPOJOPK(DC, CONCEPT, IDS);
			ItemPOJO itempojo=ItemPOJO.load(pk);
			String xml=itempojo.getProjectionAsString();
			if(xml!=null && xml.trim().length()>0) {
				String doctype="<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">";
				xml=xml.replaceFirst("(<\\?xml.*\\?>)", doctype);
				if(!xml.startsWith(doctype)){
					xml=doctype+xml;
				}
				byte[] buf= xml.getBytes();	
				ByteArrayInputStream bio=new ByteArrayInputStream(buf);

				CONFIGURATION.loadFromXML(bio);
				bio.close();
			}
		} catch (Exception e) {
			org.apache.log4j.Logger.getLogger(AutoIncrementGenerator.class).error(e.getLocalizedMessage(),e);
		}	
	}
	/**
	 * this is not a good algrithom, need to find a better way
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
		saveToDB();
		return num;
	}
	
	public static  void saveToDB() {

		try {
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			CONFIGURATION.storeToXML(bos, "","UTF-8");
			String xmlString=bos.toString("UTF-8");
			xmlString=xmlString.replace("<!DOCTYPE properties SYSTEM \"http://java.sun.com/dtd/properties.dtd\">", "");
			//Util.getXmlServerCtrlLocal().putDocumentFromString(xmlString, AUTO_INCREMENT, XSystemObjects.DC_CONF.getName(), null);
			ItemPOJO pojo = new ItemPOJO(					
					DC,	//cluster
					CONCEPT,								//concept name
					IDS,
					System.currentTimeMillis(),			//insertion time
					xmlString												//actual data
			);
			pojo.store(null);
			//read from xml file
			bos.close();
		}catch(Exception e) {
			
		}		
	}
}
