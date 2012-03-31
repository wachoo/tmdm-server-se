// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.client.util;

import java.util.LinkedList;
import java.util.List;

import org.talend.mdm.webapp.base.client.model.Image;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class ImageUtil {    
    
    final public static String IMAGE_SERVER_PATH = "pubcomponent/secure/pictures"; //$NON-NLS-1$
    
    final public static String IMAGE_SERVER_USERNAME = "administrator"; //$NON-NLS-1$
    
    final public static String IMAGE_SERVER_PASSWORD = "administrator"; //$NON-NLS-1$
    
    final public static String IMAGE_PATH = "imageserver/"; //$NON-NLS-1$
    
    final public static String UPLOAD_PATH = IMAGE_PATH + "upload/"; //$NON-NLS-1$
    
    public static List<Image> getImages(String xml) throws Exception{
        List<Image> images = new LinkedList<Image>();
        Document docment = XMLParser.parse(xml);
        Element element = docment.getDocumentElement();
        XMLParser.removeWhitespace(element);
        NodeList result = element.getElementsByTagName("entry") ; //$NON-NLS-1$
        for (int i=0;i<result.getLength();i++){
            Image image = new Image();
            Element node = (Element)result.item(i);             
            image.setName(getElementTextValue(node,"name")); //$NON-NLS-1$
            image.setPath(getElementTextValue(node,"uri")); //$NON-NLS-1$
            image.setCatalog(getCatalogByPath(image.getPath()));
            images.add(image);                
        }
        return images;
    }
    
    private static String getElementTextValue(Element parent, String elementTag) {
        return parent.getElementsByTagName(elementTag).item(0).getFirstChild().getNodeValue();
    }
    
    private static String getCatalogByPath(String path){
        String catalog = path.replace("/" + UPLOAD_PATH, ""); //$NON-NLS-1$ //$NON-NLS-2$
        catalog = catalog.substring(0,catalog.lastIndexOf('/'));
        return catalog;
    }
    

}
