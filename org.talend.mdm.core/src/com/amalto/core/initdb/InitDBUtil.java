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

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.util.Util;

/**
 * Create system init datacluster/datamodel. etc
 * @author achen
 *
 */
public class InitDBUtil {
	static HashMap<String, List<String>> initdb=new HashMap<String, List<String>>();
	
	public static void init(){
		try{
			InputStream in=InitDBUtil.class.getResourceAsStream("/com/amalto/core/initdb/initdb.xml");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
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
							if( initdb.get(name)==null){
								initdb.put(name, new ArrayList<String>());
							}
						}
						if("list".equals(n.getNodeName())){
							if(n.getTextContent()==null || n.getTextContent().trim().length()==0) continue;
							List<String> lists=initdb.get(name);
							String[] arr=n.getTextContent().split(";");
							lists.addAll(Arrays.asList(arr));
						}						
					}
				}
			}
			System.out.println(initdb.size());
		}catch(Exception e){
			org.apache.log4j.Logger.getLogger(InitDBUtil.class).error(e.getCause());
		}
	}
	
	/**
	 * init db
	 * @throws Exception
	 */
	public static void initDB()throws Exception{
		for(Entry<String, List<String>> entry: initdb.entrySet()){
			String datacluster=entry.getKey();
			//create datacluster
			Util.getXmlServerCtrlLocal().createCluster(null, datacluster);
			
			List<String> list=entry.getValue();
			
			//create items
			for(String item:list){
				InputStream in=InitDBUtil.class.getResourceAsStream("/com/amalto/core/initdb/data/"+item);
				String xmlString =getString(in);
				String uniqueID=item;
				int pos= item.lastIndexOf('/');
				if(pos!=-1){
					uniqueID=item.substring(pos+1);
				}
				uniqueID=uniqueID.replaceAll("\\+", " ");
//				System.out.println("===================================");
//				System.out.println(xmlString);
				Util.getXmlServerCtrlLocal().putDocumentFromString(xmlString, uniqueID, datacluster, null);
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
		return sb.toString();
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
