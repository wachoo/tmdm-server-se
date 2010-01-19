package com.amalto.core.initdb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.XtentisException;

/**
 * Create system init datacluster/datamodel. etc
 * Current only support head universe
 * @author achen
 *
 */
public class InitDBUtil {
	static HashMap<String, List<String>> initDB=new HashMap<String, List<String>>();
	static HashMap<String, List<String>> initExtensionDB=new HashMap<String, List<String>>();
	static boolean useExtension=false;
	
	public static void init(){
		
		try{
			InputStream dbIn=InitDBUtil.class.getResourceAsStream("/com/amalto/core/initdb/initdb.xml");
			InputStream edbIn=InitDBUtil.class.getResourceAsStream("/com/amalto/core/initdb/initdb-extension.xml");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			
			parseInitMap(dbIn, builder, initDB);
			if(edbIn!=null){
				useExtension=true;
				org.apache.log4j.Logger.getLogger(InitDBUtil.class).info("Use Extension...");
				parseInitMap(edbIn, builder, initExtensionDB);
			}
			
		}catch(Exception e){
			org.apache.log4j.Logger.getLogger(InitDBUtil.class).error(e.getCause());
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
	public static void initDB()throws Exception{
		updateDB("/com/amalto/core/initdb/data", initDB);
		if(useExtension)updateDB("/com/amalto/core/initdb/extensiondata", initExtensionDB);
	}

	private static void updateDB(String resourcePath,
			HashMap<String, List<String>> initdb) throws XtentisException {
		for(Entry<String, List<String>> entry: initdb.entrySet()){
			String datacluster=entry.getKey();
			//create datacluster
			//Util.getXmlServerCtrlLocal().createCluster(null, datacluster);
			ConfigurationHelper.createCluster(null, datacluster);//slow but more reliable 
			
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
				ConfigurationHelper.putDomcument(datacluster, xmlString, uniqueID);
			}
		}
	}
	
	private static String getString(InputStream in){
		StringBuffer sb=new StringBuffer();
		BufferedReader reader=new BufferedReader(new InputStreamReader(in));
		try {
			String line=reader.readLine();
			while(line!=null){
				sb=sb.append(line+"\n");
				line=reader.readLine();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		String result="";
		try {
			result= new String(sb.toString().getBytes(),"UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
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
