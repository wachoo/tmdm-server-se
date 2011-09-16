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
package org.talend.mdm.webapp.browserecords.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

import com.amalto.webapp.core.util.XmlUtil;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DynamicLabelUtil extends XmlUtil{
    
    private static final Pattern MULTI_OCCURRENCE_PATTERN = Pattern.compile("(.+)\\[(\\d+)\\]$");//$NON-NLS-1$

    private static final String RESERVED_WORD_START_FLAG = "{";//$NON-NLS-1$

    private static final String RESERVED_WORD_END_FLAG = "}";//$NON-NLS-1$


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
        if (currentXpath.startsWith("/"))//$NON-NLS-1$
            currentXpath = currentXpath.substring(1);
        // Parse dynamic label
        dynamicLabel = parseDynamicLabel(dynamicLabel);

        StringBuffer sb = new StringBuffer();
        sb.append("<xsl:stylesheet xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\" version=\"1.0\">");//$NON-NLS-1$
        sb.append("<xsl:output method=\"xml\" indent=\"yes\" omit-xml-declaration=\"yes\"/>");//$NON-NLS-1$
        sb.append("<xsl:template match=\"/\">");//$NON-NLS-1$
        sb.append("<result-lable>");//$NON-NLS-1$
        genForEachLoop(currentXpath, dynamicLabel, sb);
        sb.append("</result-lable>");//$NON-NLS-1$
        sb.append("</xsl:template>");//$NON-NLS-1$
        sb.append("</xsl:stylesheet>");//$NON-NLS-1$

        return sb.toString();

    }

    /**
     * DOC HSHU Comment method "parseDynamicLabel".
     * @param dynamicLabel
     * @return
     */
    private static String parseDynamicLabel(String dynamicLabel) {
        
        if(dynamicLabel == null)return "";//$NON-NLS-1$
        //dynamicLabel = dynamicLabel.replace(RESERVED_WORD_START_FLAG, "<xsl:value-of select=\"");
        //dynamicLabel = dynamicLabel.replace(RESERVED_WORD_END_FLAG, "\"/>");

        while (dynamicLabel.indexOf(RESERVED_WORD_START_FLAG)!=-1) {
            
            int pos=dynamicLabel.indexOf(RESERVED_WORD_START_FLAG);
            String firstPart=dynamicLabel.substring(0,pos);
            
            String leftPart=dynamicLabel.substring(pos+1);
            int pos2=leftPart.indexOf(RESERVED_WORD_END_FLAG);
            if(pos2==-1)break;//incomplete
            String middlePart=leftPart.substring(0,pos2);
            middlePart=middlePart.replace("\"", "'");//filter inner content //$NON-NLS-1$ //$NON-NLS-2$
            
            String lastPart=leftPart.substring(pos2+1);
            
            dynamicLabel=firstPart+"<xsl:value-of select=\""+middlePart+"\"/>"+lastPart; //$NON-NLS-1$ //$NON-NLS-2$
            
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
        
        String pathSnatch="";//$NON-NLS-1$
        String[] paths=currentXpath.split("/");//$NON-NLS-1$
        
        boolean reachTheEnd=false;
        boolean writeForEachLine=false;
        boolean isMultiOccurrence=false;
        
        for (int i = 0; i < paths.length; i++) {
            
            String path=paths[i];
            String occNum="";//$NON-NLS-1$
            
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
            
            if(pathSnatch.length()>0)pathSnatch+="/"+path;//$NON-NLS-1$
            else pathSnatch+=path;
                
            if(isMultiOccurrence||reachTheEnd)writeForEachLine=true;
            else writeForEachLine=false;
            
            if(writeForEachLine) {
                
                startTagList.add("<xsl:for-each select=\""+pathSnatch+"\">");//$NON-NLS-1$ //$NON-NLS-2$
                endTagList.add("</xsl:for-each>");//$NON-NLS-1$ 
                
                if(isMultiOccurrence&&occNum.length()>0) {
                    //writeIfLine
                    startTagList.add("<xsl:if test=\"position()="+occNum+"\">");//$NON-NLS-1$ //$NON-NLS-2$
                    endTagList.add("</xsl:if>");//$NON-NLS-1$
                }
                
                //reset pathSnatch
                pathSnatch="";//$NON-NLS-1$
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
        Node node = transformedDoc.selectSingleNode("/result-lable");//$NON-NLS-1$
        return node == null ? null : node.getText();
    }

    /**
     * 
     * @return
     */
    public static void getDynamicLabel(Document parsedDocument, ItemNodeModel itemModel, 
    		Map<String, TypeModel> metaDataTypes, String language) 
    {
        try {
        	String xpath = itemModel.getBindingPath();
        	TypeModel typeModel = metaDataTypes.get(xpath);
        	String label = typeModel.getLabel(language);
        	if (DynamicLabelUtil.isDynamicLabel(label)) {
        		label = replaceForeignPath(itemModel.getBindingPath(), label, parsedDocument);
        		String stylesheet = genStyle(itemModel.getBindingPath(), label);
        		String dynamicLB = getParsedLabel(DynamicLabelUtil.styleDocument(parsedDocument, stylesheet));
        		//@temp yguo, set the properties to itemmodel
        		itemModel.setDynamicLabel(dynamicLB);
        	}
        	
        	if(itemModel.getChildCount() == 0) {
        		return;
        	}
        	else {
	    		for(int i = 0; i < itemModel.getChildCount(); i++) {
	    			getDynamicLabel(parsedDocument, (ItemNodeModel) itemModel.getChild(i), metaDataTypes, language);
	    		}
        	}
        }
        catch(Exception ex) {
        }
    }
    
    private static String replaceForeignPath(String basePath, String dynamicLabel, org.dom4j.Document doc) throws Exception {
        Pattern pattern = Pattern.compile("\\{.*?\\}");//$NON-NLS-1$
        Matcher matcher = pattern.matcher(dynamicLabel);
        List<String> dynamicPathes = new ArrayList<String>();
        while (matcher.find()) {
            dynamicPathes.add(matcher.group().replaceAll("^\\{", "").replaceAll("\\}$", ""));//$NON-NLS-1$ //$NON-NLS-2$
        }

//        Configuration config = Configuration.getInstance();
//        String dataModelPK = config.getModel();
//        String xsd = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelPK))).getXsdSchema();
//        Map<String, XSElementDecl> map = com.amalto.core.util.Util.getConceptMap(xsd);
//        Map<String, XSType> typeMap = com.amalto.core.util.Util.getConceptTypeMap(xsd);
        basePath = basePath.startsWith("/") ? basePath.substring(1) : basePath; //$NON-NLS-1$
//        XSElementDecl xsed = map.get(basePath.split("/")[0]); //$NON-NLS-1$

        for (String dyPath : dynamicPathes) {
            Element baseEl = (Element) doc.selectSingleNode(basePath);
            try {
                List els = (List) baseEl.selectNodes(dyPath);//$NON-NLS-1$
                if (els == null)
                    continue;
                String multiValue = "";
                if (els.size() > 0){

	                for (int i = 0; i < els.size();i++){
		                List<org.dom4j.Element> pathNodes = getPathNode((org.dom4j.Element) els.get(i));
		                String key = ((org.dom4j.Element)els.get(i)).getStringValue();
//		                Object[] fkObj = getForeign(xsed, pathNodes, 0, typeMap);
//		                if (fkObj != null && ((List<String>)fkObj[1]).size() > 0) {
//		                    String foreignkey = (String) fkObj[0];
//		                    List<String> fkInfos = (List<String>) fkObj[1];

//		                    String fkInfoStr = getFKInfo(key, foreignkey, fkInfos);
//		                    multiValue += fkInfoStr == null ? "" : fkInfoStr;

//		                } else {
		                	multiValue += key == null ? "" : key;
//		                }
	                }
	                
	                dynamicLabel = dynamicLabel.replace("{" + dyPath + "}", multiValue == null ? "" : multiValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                }
            } catch (Exception e) {
                continue;
            }
        }
        
        return dynamicLabel;
    }
    
    private static List<org.dom4j.Element> getPathNode(org.dom4j.Element el) {
        List<org.dom4j.Element> pathEls = new ArrayList<org.dom4j.Element>();
        org.dom4j.Element currentEl = el;
        while (currentEl != null) {
            pathEls.add(0, currentEl);
            currentEl = currentEl.getParent();
        }
        return pathEls;
    }
}
