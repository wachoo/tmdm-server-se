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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.util.MultilanguageMessageParser;
import org.talend.mdm.webapp.base.server.util.XmlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.webapp.core.util.Util;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class CommonUtil {

    private static final Logger LOG = Logger.getLogger(CommonUtil.class);

    public static List<ItemNodeModel> getDefaultTreeModel(TypeModel model, boolean isCreate, String language) {
        List<ItemNodeModel> itemNodes = new ArrayList<ItemNodeModel>();

        if (model.getMinOccurs() > 1) {
            for (int i = 0; i < model.getMinOccurs(); i++) {
                ItemNodeModel itemNode = new ItemNodeModel();
                itemNodes.add(itemNode);
                if (model.getForeignkey() != null)
                    break;
            }
        } else {
            ItemNodeModel itemNode = new ItemNodeModel();
            itemNodes.add(itemNode);
        }

        for (ItemNodeModel node : itemNodes) {
            if (model.getMinOccurs() > 0) {
                node.setMandatory(true);
            }
            if (model.isSimpleType()) {
                if (isCreate)
                    node.setChangeValue(true);
            } else {
                ComplexTypeModel complexModel = (ComplexTypeModel) model;
                List<TypeModel> children = complexModel.getSubTypes();
                List<ItemNodeModel> list = new ArrayList<ItemNodeModel>();
                for (TypeModel typeModel : children) {
                    list.addAll(getDefaultTreeModel(typeModel, isCreate, language));
                }
                node.setChildNodes(list);
            }
            node.setName(model.getName());
            node.setBindingPath(model.getXpath());
            node.setTypePath(model.getTypePath());
            node.setDescription(model.getDescriptionMap().get(language));
            node.setLabel(model.getLabel(language));
        }
        return itemNodes;
    }

    public static int getFKFormatType(String str){
        if(str == null)
            return 0;
        
        if(str.trim().equalsIgnoreCase("")) //$NON-NLS-1$
            return 0;
        
        Pattern p = Pattern.compile("^\\[.+\\]$"); //$NON-NLS-1$
        Matcher m = p.matcher(str);
        if(m.matches())
            return 1;
        
        p = Pattern.compile("^\\[.+\\]-.+"); //$NON-NLS-1$
        m = p.matcher(str);
        if(m.matches())
            return 2;
        
        return 0;
    }
    
    public static String getForeignKeyId(String str, int type){
        if(type == 1)
            return str;
        if(type == 2){
            int index = str.indexOf("-"); //$NON-NLS-1$
            if(index > 0)
                return str.substring(0, index);
        }
        return null;            
    }

    public static Document getSubXML(TypeModel typeModel, String realType, String language) throws ServiceException {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try {
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.newDocument();
            List<Element> list = _getDefaultXML(typeModel, null, realType, doc, language);
            Element root = list.get(0);
            root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$//$NON-NLS-2$
            doc.appendChild(root);
            return doc;
        } catch (ParserConfigurationException e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getMessage());
        }
    }

    public static Document getSubDoc(org.dom4j.Document mainDoc, String contextPath) throws ServiceException {
        org.dom4j.Element el = (org.dom4j.Element) mainDoc.selectSingleNode(contextPath);
        if (el.getParent() != null) {
            el.getParent().remove(el);
        }
        org.dom4j.Document doc = DocumentHelper.createDocument(el);
        return XmlUtil.parseDocument(doc);
    }

    private static List<Element> _getDefaultXML(TypeModel model, TypeModel parentModel, String realType, Document doc,
            String language) {
        List<Element> itemNodes = new ArrayList<Element>();
        if (model.getMinOccurs() > 1) {
            for (int i = 0; i < model.getMinOccurs(); i++) {
                Element el = doc.createElement(model.getName());
                applySimpleTypesDefaultValue(model, parentModel, el);
                itemNodes.add(el);
            }
        } else {
            Element el = doc.createElement(model.getName());
            applySimpleTypesDefaultValue(model, parentModel, el);
            itemNodes.add(el);
        }
        if (!model.isSimpleType()) {
            ComplexTypeModel complexModel = (ComplexTypeModel) model;
            ComplexTypeModel realTypeModel = complexModel.getRealType(realType);
            List<TypeModel> children;
            if (realTypeModel != null) {
                children = realTypeModel.getSubTypes();
            } else {
                children = complexModel.getSubTypes();
            }
            for (Element node : itemNodes) {
                if (realTypeModel != null) {
                    node.setAttributeNS("http://www.w3.org/2001/XMLSchema-instance", "xsi:type", realType); //$NON-NLS-1$ //$NON-NLS-2$
                }
                for (TypeModel typeModel : children) {
                    List<Element> els = _getDefaultXML(typeModel, model, realType, doc, language);
                    for (Element el : els) {
                        node.appendChild(el);
                    }
                }
            }
        }
        return itemNodes;
    }

    private static void applySimpleTypesDefaultValue(TypeModel nodeTypeModel, TypeModel parentModel, Element el) {
         
        // if parent node is not root node, also is non-mandatory, don't apply system default value
        if (parentModel != null && parentModel.getParentTypeModel()!=null
        		&& parentModel instanceof ComplexTypeModel && parentModel.getMinOccurs() == 0)
            return;

        if (nodeTypeModel != null && el != null) {
            // only assist system default value for mandatory node
            if (nodeTypeModel.isSimpleType() && nodeTypeModel.getMinOccurs() > 0) {
                if (nodeTypeModel.getType().equals(DataTypeConstants.BOOLEAN)) {
                    el.setTextContent("false"); //$NON-NLS-1$
                }
                // TODO is there any more?
            }

        }
    }
    
    public static Map<String, String> handleProcessMessage(String outputMessage, String language) throws Exception {
    	Map<String, String> messageMap = new HashMap<String, String>();
    	messageMap.put("typeCode", null); //$NON-NLS-1$
    	messageMap.put("message", null); //$NON-NLS-1$
    	
    	Document doc = Util.parse(outputMessage);
    	String xpath = "//report/message"; //$NON-NLS-1$
    	NodeList checkList = Util.getNodeList(doc, xpath);
    	if (checkList != null && checkList.getLength() > 0) {
    		Node messageNode = checkList.item(0);
    		if (messageNode instanceof Element) {
    			Element messageElement = (Element) messageNode;
    			messageMap.put("typeCode", messageElement.getAttribute("type")); //$NON-NLS-1$ //$NON-NLS-2$
    			NodeList childList = messageElement.getChildNodes();
    			if(childList.getLength() == 1) {
    				Node contentNode = childList.item(0);
    				if(contentNode.getNodeType() == Node.TEXT_NODE) 
    					messageMap.put("message", MultilanguageMessageParser.pickOutISOMessage(contentNode.getTextContent(), language)); //$NON-NLS-1$
    				else if(contentNode.getNodeType() == Node.ELEMENT_NODE)
    					if(contentNode.getChildNodes().getLength() == 1 && contentNode.getChildNodes().item(0).getNodeType() == Node.TEXT_NODE)
    						messageMap.put("message", MultilanguageMessageParser.pickOutISOMessage(contentNode.getTextContent(), language)); //$NON-NLS-1$
    			}
    		}
    	}
        return messageMap;
    }
}
