package com.amalto.core.delegator;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.util.Util;

public class BeanDelegatorConfigReader {
	
	private static Map<String,String> beanImplNamesMap=new HashMap<String,String>();
	
	public static void init(){
		
		try{
			
			InputStream in=BeanDelegatorConfigReader.class.getResourceAsStream("/com/amalto/core/delegator/delegator.xml");
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(in);	
			

			NodeList nodelist = Util.getNodeList(doc.getDocumentElement(), "/bean/delegator/name");
			for(int i=0; i<nodelist.getLength(); i++){
				Node node=nodelist.item(i);
				String textContent=node.getTextContent();
				String[] tmp=textContent.split("#");
				beanImplNamesMap.put(tmp[0],tmp[1]);
			}
			
		}catch(Exception e){
			org.apache.log4j.Logger.getLogger(BeanDelegatorConfigReader.class).error(e.getCause());
		}
	}
	
	public static Map<String, String> getBeanImplNamesMap() {
		return beanImplNamesMap;
	}

	//test
	public static void main(String[] args) {
		init();
		Map<String, String> beanImplNamesMap=BeanDelegatorConfigReader.getBeanImplNamesMap();
		System.out.println(beanImplNamesMap);
	}

}
