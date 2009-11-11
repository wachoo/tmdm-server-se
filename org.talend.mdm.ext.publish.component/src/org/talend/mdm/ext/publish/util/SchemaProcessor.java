package org.talend.mdm.ext.publish.util;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

public class SchemaProcessor{
	
	private static final String elementID = "xsd:element";
	
	public static String transform2types(String infoXML) {
		String transformedXml=null;
		try {
			Document doc = DocumentHelper.parseText(infoXML);
			
			//first level only
			List<Element> elements=doc.getRootElement().elements();
			for (Iterator<Element> iterator = elements.iterator(); iterator.hasNext();) {
				Element element = (Element) iterator.next();
				
				//remove concept element
				if(element.getQualifiedName().equals(elementID)){
					Element parentElement =element.getParent();
					if(parentElement!=null)parentElement.remove(element);
				}
			}
			
			transformedXml=doc.asXML();
		} catch (DocumentException e) {
			e.printStackTrace();
			return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

		return transformedXml;
	}

}
