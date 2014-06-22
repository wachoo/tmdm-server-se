// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.webapp.core.util;

import java.io.StringReader;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.apache.commons.lang.StringEscapeUtils;
import org.dom4j.Document;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class XmlUtil {
    
    /**
     * DOC HSHU Comment method "parseDocument".
     * @param doc
     * @return
     * @throws Exception
     */
    public static Document parseDocument(org.w3c.dom.Document doc) throws Exception {
        if (doc == null) {
            return (null);
        }
        org.dom4j.io.DOMReader xmlReader = new org.dom4j.io.DOMReader();
        return (xmlReader.read(doc));
    }
    
    public static Document styleDocument(org.w3c.dom.Document document, String stylesheet) throws Exception  {
        
        Document parsedDocument=parseDocument(document);
        return styleDocument(parsedDocument,stylesheet);
        
    }
    
    /**
     * DOC HSHU Comment method "styleDocument".
     * @param document
     * @param stylesheet
     * @return
     * @throws Exception
     */
    public static Document styleDocument(Document document, String stylesheet) throws Exception {
        
        TransformerFactory factory = TransformerFactory.newInstance(
                "net.sf.saxon.TransformerFactoryImpl", Thread.currentThread().getContextClassLoader()); //$NON-NLS-1$
        Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(stylesheet)));
        // now lets style the given document
        DocumentSource source = new DocumentSource(document);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);
        // return the transformed document
        Document transformedDoc = result.getDocument();
        return transformedDoc;
    }
    
    /**
     * DOC HSHU Comment method "normalizeXpath".
     * @param xpath
     */
    public static String normalizeXpath(String xpath) {
        if (xpath.startsWith("/")) //$NON-NLS-1$
            xpath = xpath.substring(1);
        return xpath;
    }
    
    public static String toXml(Document document) {

        String text = document.asXML();

        return text;
    }
    
    public static void print(Document document) {

        String text = toXml(document);

        System.out.println(text);
    }
    
    public static String escapeXml(String value) {
        if (value == null)
            return null;
        boolean isEscaped=false;
        if (value.indexOf("&quot;") != -1 || //$NON-NLS-1$
                value.indexOf("&amp;") != -1 || //$NON-NLS-1$
                value.indexOf("&lt;") != -1 || //$NON-NLS-1$
                value.indexOf("&gt;") != -1)isEscaped = true; //$NON-NLS-1$
        
        if(!isEscaped)value=StringEscapeUtils.escapeXml(value);
        return value;
    }

}
