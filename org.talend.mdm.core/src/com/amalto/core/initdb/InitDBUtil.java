package com.amalto.core.initdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

/**
 * Create system init datacluster/datamodel. etc
 * Current only support head universe
 * @author achen
 *
 */
public class InitDBUtil {
	static Logger logger=Logger.getLogger(InitDBUtil.class);
	
	static HashMap<String, List<String>> initDB=new HashMap<String, List<String>>();
	static HashMap<String, List<String>> initExtensionDB=new HashMap<String, List<String>>();
	static boolean useExtension=false;
	
	static final String[] scrapResourceNames = new String[]{"Default_Admin", "Default_User", "Default_Viewer"};
	
	public static void init(){
		InputStream dbIn=null;
		InputStream edbIn=null;
		try{
			dbIn=InitDBUtil.class.getResourceAsStream("/com/amalto/core/initdb/initdb.xml");
			edbIn=InitDBUtil.class.getResourceAsStream("/com/amalto/core/initdb/initdb-extension.xml");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			parseInitMap(dbIn, builder, initDB);
			if(edbIn!=null){
				useExtension=true;
				org.apache.log4j.Logger.getLogger(InitDBUtil.class).info("Use Extension...");
				parseInitMap(edbIn, builder, initExtensionDB);
			}
			
		}catch(Exception e){
			org.apache.log4j.Logger.getLogger(InitDBUtil.class).error(e.getCause());
		}finally {
			if(dbIn!=null) try{dbIn.close();}catch(Exception e) {};
			if(edbIn!=null) try{edbIn.close();}catch(Exception e) {};
		}
	}

	private static void parseInitMap(InputStream in, DocumentBuilder builder,
			HashMap<String, List<String>> initMap) throws SAXException,
			IOException {
		Document doc = builder.parse(in);	
		
		NodeList nodelist = doc.getElementsByTagName("item");
		for(int i=0; i<nodelist.getLength(); i++){
			Node node=nodelist.item(i);
			NodeList list=node.getChildNodes();
			String name=null;
			for(int j=0; j<list.getLength(); j++){
				Node n=list.item(j);
				if(n instanceof Element){
					if("name".equals(n.getNodeName())){
						name=n.getTextContent();
						if( initMap.get(name)==null){
							initMap.put(name, new ArrayList<String>());
						}
					}
					if("list".equals(n.getNodeName())){
						if(n.getTextContent()==null || n.getTextContent().trim().length()==0) continue;
						List<String> lists=initMap.get(name);
						String[] arr=n.getTextContent().split(";");
						lists.addAll(Arrays.asList(arr));
					}						
				}
			}
		}
	}
	
	/**
	 * init db
	 * @throws Exception
	 */
	public static void initDB(){

		updateDB("/com/amalto/core/initdb/data", initDB);
		if(useExtension)updateDB("/com/amalto/core/initdb/extensiondata", initExtensionDB);
		updateUsersWithNewRoleScheme();
		deleteScrapDB();
		
		//init db extension job		
		initDBExtJob();
	}
	
	private static void initDBExtJob(){
		InitDbExtJobRepository.getInstance().execute();
	}
	private static void updateDB(String resourcePath,
			HashMap<String, List<String>> initdb)  {
		for(Entry<String, List<String>> entry: initdb.entrySet()){
			String datacluster=entry.getKey();
			//create datacluster
			//Util.getXmlServerCtrlLocal().createCluster(null, datacluster);
			try {
			ConfigurationHelper.createCluster(null, datacluster);//slow but more reliable
			}catch(Exception e) {}
			
			List<String> list=entry.getValue();
			
			//create items
			for(String item:list){
				InputStream in=InitDBUtil.class.getResourceAsStream(resourcePath+"/"+item);
				String xmlString =getString(in);
				String uniqueID=item;
				int pos= item.lastIndexOf('/');
				if(pos!=-1){
					uniqueID=item.substring(pos+1);
				}
				uniqueID=uniqueID.replaceAll("\\+", " ");
//				System.out.println("===================================");
//				System.out.println(xmlString);
				try {
				if(MDMConfiguration.getConfiguration().get("cluster_override").equals("true")){
					ConfigurationHelper.deleteDocumnet(null, datacluster, uniqueID);
				}
				ConfigurationHelper.putDomcument(datacluster, xmlString, uniqueID);
				}catch(Exception e) {}
			}
		}
	}
	
	private static void updateUsersWithNewRoleScheme()
	{		
		final String userClusterName = "PROVISIONING";
		String query = "collection(\"" + userClusterName + "\")/ii/p/User/username";
		try {
            XmlServerSLWrapperLocal server = ConfigurationHelper.getServer();
            ArrayList<String> list = server.runQuery(null, userClusterName, query, null);
			for (String user: list)
			{
				NodeList users = Util.getNodeList(Util.parse(user), "/username");
				for (int i = 0; i < users.getLength(); i++)
				{
					String entry = users.item(i).getFirstChild().getNodeValue();
					String uniqueID = userClusterName + ".User." + entry;
					String userXml = server.getDocumentAsString(null, userClusterName, uniqueID);
					String updateXml = new String(userXml);
					 for (Map.Entry<String, String> pair : ICoreConstants.rolesConvert.oldRoleToNewRoleMap.entrySet())
					 {
						 userXml = userXml.replaceAll(pair.getKey().toString(), pair.getValue().toString());
					 }
					 if(!updateXml.equals(userXml)) {
                         server.start(userClusterName);
                         server.putDocumentFromString(userXml, uniqueID, userClusterName, null);
                         server.commit(userClusterName);
					 }
				}
			}
		} catch (Exception e) {
            logger.error("Exception occurred while updating users roles", e);
		}
	}
	private static void deleteScrapDB()
	{
		final String  scrapClusterName = "amaltoOBJECTSRole";
		for (String resName : scrapResourceNames)
		{
			try {
				ConfigurationHelper.deleteDocumnet(null, scrapClusterName, resName);
			} catch (XtentisException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static String getString(InputStream in){
		String result="";
		try {
			StringBuffer sb=new StringBuffer();		
			BufferedReader reader=new BufferedReader(new InputStreamReader(in));
	
			String line=reader.readLine();
			while(line!=null){
				sb= sb.append(line).append("\n");
				line=reader.readLine();
			}
					
			result= new String(sb.toString().getBytes(),"UTF-8");
		
		}catch(Exception e) {
			
		}finally {
			if(in!=null) try {in.close();}catch(Exception e) {}
		}
		return result;
	}
	public static void main(String[] args) {
		init();
		try {
			initDB();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
