package org.talend.mdm.workflow.client.util;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class XmlFromSimpleXpathsBuilder {

	public static void main(String[] args) {

		String testPath0 = "/customer/info/nic";
		String testPath1 = "/customer/id";
		String testPath2 = "/customer/info/name";
		String testPath3 = "/customer/age";
		String testValue0 = "v1";
		String testValue1 = "vid";
		String testValue2 = "vname";
		String testValue3 = "15";

		Map<String, Object> input = new HashMap<String, Object>();
		input.put(testPath0, testValue0);
		input.put(testPath1, testValue1);
		input.put(testPath2, testValue2);
		input.put(testPath3, testValue3);
		
		System.out.println(parse2String(input));
		
	}
	
	public static String parse2String(Map<String, Object> input)throws MutiRootNameException {
		
		String text = "";
		Document doc=parse(input);
		if(doc!=null)text=doc.asXML();
		 
		return text;
	}

	public static Document parse(Map<String, Object> input)throws MutiRootNameException {
		Document doc=null;
		if (input != null && input.size() > 0) {

			doc = DocumentHelper.createDocument();
			Element root = null;
			String rootTag = null;

			for (Iterator<String> iterator = input.keySet().iterator(); iterator
					.hasNext();) {
				
				String xpath = iterator.next();
				String lawXpath = xpath;

				// clear prefix
				if (xpath.startsWith("//")) {
					xpath = xpath.substring(2);
				} else if (xpath.startsWith("/")) {
					xpath = xpath.substring(1);
				}
				// parse root
				String thisRootTag = null;
				int cutPoint = xpath.indexOf("/");
				if (cutPoint == -1) {
					thisRootTag = xpath;
				} else {
					thisRootTag = xpath.substring(0, cutPoint);
				}
				if (rootTag != null && !rootTag.equals(thisRootTag))
					throw new MutiRootNameException();
				rootTag = thisRootTag;
				
				if(root==null){
					//first time
					root = DocumentHelper.createElement(rootTag);
					doc.setRootElement(root);
				}
				
				// builder dom
				String subPath = "";
				if (cutPoint == -1) {
				} else {
					subPath = xpath.substring(cutPoint + 1);
				}
				String[] subPathParts = subPath.split("/");

				if (root != null) {
					Element lastElement = root;
					for (int i = 0; i < subPathParts.length; i++) {
						String nodeTag = subPathParts[i];
						
                        //create
						Element thisElement = null;
						if(root.element(nodeTag)!=null){
							thisElement = root.element(nodeTag);
						}else{
							thisElement = lastElement.addElement(nodeTag);
						}
						
						//set text
						if (i == subPathParts.length - 1)thisElement.setText(input.get(lawXpath).toString());
						
						//attach for next
						if (i < subPathParts.length - 1&&root.element(nodeTag)!=null) {
							lastElement = thisElement;
						}
						
					}
				}

			}//end for

		}
		
		return doc;
	}

}
