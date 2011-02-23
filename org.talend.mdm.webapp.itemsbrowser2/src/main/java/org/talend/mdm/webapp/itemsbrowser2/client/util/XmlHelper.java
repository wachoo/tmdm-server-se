// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.util;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class XmlHelper {
    
    
    /**
     * DOC HSHU Comment method "parse".
     */
    public static Document parse(String text) {
        return parse(text,false);
    }
    
    /**
     * DOC HSHU Comment method "parse".
     * @param text
     * @param removeWhitespace
     * @return
     */
    public static Document parse(String text,boolean removeWhitespace) {
        Document xmlDoc = XMLParser.parse(text);
        if(removeWhitespace)XMLParser.removeWhitespace(xmlDoc);
        return xmlDoc;
    }
    
    /**
     * DOC HSHU Comment method "getElementsByTagName".
     * @param root
     * @param tagName
     * @return
     */
    public static Element getElementsByTagName(Element root,String tagName) {
        return getElementsByTagName(root,tagName,1);
    }
    
    /**
     * DOC HSHU Comment method "getElementsByTagName".
     */
    public static Element getElementsByTagName(Element root,String tagName,int itemIndex) {
        Element elem = (Element) root.getElementsByTagName(tagName).item(itemIndex);
        return elem;
    }
    
    
    /**
     * DOC HSHU Comment method "getFirstTextValue".
     */
    public static String getFirstTextValue(Element elem) {
        
        return elem.getFirstChild().getNodeValue();

    }
    
    
    /**
     * DOC HSHU Comment method "getTextValueFromXpath".
     */
    public static String getTextValueFromXpath(Document doc, String xpath) {
        
        //FIXME
        String label="";
        if(xpath.indexOf("/")!=-1)label=xpath.substring(xpath.lastIndexOf("/")+1);
        Element elem=doc.getDocumentElement();
        NodeList nodelist=elem.getChildNodes();
        for (int i = 0; i < nodelist.getLength(); i++) {
            Node node = nodelist.item(i);
            if(node.getNodeName().endsWith(label)) 
                return getFirstTextValue((Element) node);
        }
        
        return null;

    }
   

}
