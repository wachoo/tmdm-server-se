// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
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
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.Node;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DynamicLabelUtil {
    
    private static final Pattern MULTI_OCCURRENCE_PATTERN = Pattern.compile("(.+)\\[(\\d+)\\]$");

    private static final String RESERVED_WORD_START_FLAG = "{";

    private static final String RESERVED_WORD_END_FLAG = "}";

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

    /**
     * DOC HSHU Comment method "isDynamicLabel".
     */
    public static boolean isDynamicLabel(String label) {
        if (label == null)
            return false;
        if (label.indexOf(RESERVED_WORD_START_FLAG) != -1)
            return true;
        return false;
    }

    /**
     * DOC HSHU Comment method "genStyle".
     */
    public static String genStyle(String currentXpath, String dynamicLabel) {
        // parse currentXpath
        if (currentXpath.startsWith("/"))
            currentXpath = currentXpath.substring(1);
        // Parse dynamic label
        dynamicLabel = parseDynamicLabel(dynamicLabel);

        StringBuffer sb = new StringBuffer();
        sb.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">");
        sb.append("<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>");
        sb.append("<xsl:template match=\"/\">");
        sb.append("<result-lable>");
        genForEachLoop(currentXpath, dynamicLabel, sb);
        sb.append("</result-lable>");
        sb.append("</xsl:template>");
        sb.append("</xsl:stylesheet>");

        return sb.toString();

    }

    /**
     * DOC HSHU Comment method "parseDynamicLabel".
     * @param dynamicLabel
     * @return
     */
    private static String parseDynamicLabel(String dynamicLabel) {
        
        if(dynamicLabel == null)return "";
        //dynamicLabel = dynamicLabel.replace(RESERVED_WORD_START_FLAG, "<xsl:value-of select=\"");
        //dynamicLabel = dynamicLabel.replace(RESERVED_WORD_END_FLAG, "\"/>");

        while (dynamicLabel.indexOf(RESERVED_WORD_START_FLAG)!=-1) {
            
            int pos=dynamicLabel.indexOf(RESERVED_WORD_START_FLAG);
            String firstPart=dynamicLabel.substring(0,pos);
            
            String leftPart=dynamicLabel.substring(pos+1);
            int pos2=leftPart.indexOf(RESERVED_WORD_END_FLAG);
            if(pos2==-1)break;//incomplete
            String middlePart=leftPart.substring(0,pos2);
            middlePart=middlePart.replace("\"", "'");//filter inner content
            
            String lastPart=leftPart.substring(pos2+1);
            
            dynamicLabel=firstPart+"<xsl:value-of select=\""+middlePart+"\"/>"+lastPart;
            
        }
        
        return dynamicLabel;
        
    }

    /**
     * DOC HSHU Comment method "genForEachLoop".
     * @param currentXpath
     * @param dynamicLabel
     * @param sb
     */
    private static void genForEachLoop(String currentXpath, String dynamicLabel, StringBuffer sb) {
        
        List<String> startTagList=new ArrayList<String>();
        List<String> endTagList=new ArrayList<String>();
        
        String pathSnatch="";
        String[] paths=currentXpath.split("/");
        
        boolean reachTheEnd=false;
        boolean writeForEachLine=false;
        boolean isMultiOccurrence=false;
        
        for (int i = 0; i < paths.length; i++) {
            
            String path=paths[i];
            String occNum="";
            
            if(i==(paths.length-1))reachTheEnd=true;
            else reachTheEnd=false;
            
            Matcher matcher = MULTI_OCCURRENCE_PATTERN.matcher(paths[i]);
            boolean matches=false;
            while (matcher.find()) {
                path=matcher.group(1);
                occNum=matcher.group(2);
                matches=true;
            }
            if(matches) {
                //is multi-occurrence
                isMultiOccurrence=true;
            }else {
                isMultiOccurrence=false;
            }
            
            if(pathSnatch.length()>0)pathSnatch+="/"+path;
            else pathSnatch+=path;
                
            if(isMultiOccurrence||reachTheEnd)writeForEachLine=true;
            else writeForEachLine=false;
            
            if(writeForEachLine) {
                
                startTagList.add("<xsl:for-each select=\""+pathSnatch+"\">");
                endTagList.add("</xsl:for-each>");
                
                if(isMultiOccurrence&&occNum.length()>0) {
                    //writeIfLine
                    startTagList.add("<xsl:if test=\"position()="+occNum+"\">");
                    endTagList.add("</xsl:if>");  
                }
                
                //reset pathSnatch
                pathSnatch="";
            }
            
        }
        
        //print to sb
        for (int i = 0; i < startTagList.size(); i++) {
            sb.append(startTagList.get(i));
        }
        sb.append(dynamicLabel);
        for (int i = endTagList.size()-1; i >-1; i--) {
            sb.append(endTagList.get(i));
        }
    }
    
    /**
     * DOC HSHU Comment method "getParsedLabel".
     */
    public static String getParsedLabel(Document transformedDoc) {
        if(transformedDoc==null)return null;
        Node node = transformedDoc.selectSingleNode("/result-lable");
        return node == null ? null : node.getText();
    }

    /**
     * DOC HSHU Comment method "styleDocument".
     * @param document
     * @param stylesheet
     * @return
     * @throws Exception
     */
    public static Document styleDocument(Document document, String stylesheet) throws Exception {

        // load the transformer using JAXP
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer(new StreamSource(new StringReader(stylesheet)));

        // now lets style the given document
        DocumentSource source = new DocumentSource(document);
        DocumentResult result = new DocumentResult();
        transformer.transform(source, result);

        // return the transformed document
        Document transformedDoc = result.getDocument();

        return transformedDoc;
    }

}
