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

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.dom4j.DocumentHelper;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.server.util.XmlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
                setDefaultValue(model, node, isCreate);
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

    private static void setDefaultValue(TypeModel model, ItemNodeModel node, boolean isCreate) {

        if (model.getDefaultValueExpression() != null && model.getDefaultValueExpression().trim().length() > 0) {
            if (!"".equals(model.getForeignkey()) && model.getForeignkey() != null) { //$NON-NLS-1$
                ForeignKeyBean foreignKeyBean = new ForeignKeyBean();
                foreignKeyBean.setId(model.getDefaultValue());
                foreignKeyBean.setForeignKeyPath(model.getForeignkey());
                node.setObjectValue(foreignKeyBean);
            } else {
                node.setObjectValue(model.getDefaultValue());
            }
            if (isCreate)
                node.setChangeValue(true);
        } else {
            if (model.getType().getTypeName().equals(DataTypeConstants.BOOLEAN.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.BOOLEAN.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DATETIME.getTypeName())) {
                DateFormat df = new SimpleDateFormat("yyyy-MM-dd"); //$NON-NLS-1$
                DateFormat tf = new SimpleDateFormat("HH:mm:ss"); //$NON-NLS-1$
                String dateStr = df.format((Date) DataTypeConstants.DATETIME.getDefaultValue());
                String timeStr = tf.format((Date) DataTypeConstants.DATETIME.getDefaultValue());
                node.setObjectValue(dateStr + "T" + timeStr); //$NON-NLS-1$
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DECIMAL.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.DECIMAL.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DOUBLE.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.DOUBLE.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.FLOAT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.FLOAT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.INT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.INT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.INTEGER.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.INTEGER.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.LONG.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.LONG.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.SHORT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.SHORT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.STRING.getTypeName())) {
                if (model.getForeignkey() != null && model.getForeignkey().trim().length() > 0) {
                    ForeignKeyBean foreignKeyBean = new ForeignKeyBean();
                    foreignKeyBean.setId(""); //$NON-NLS-1$
                    foreignKeyBean.setForeignKeyPath(model.getForeignkey());
                    node.setObjectValue(foreignKeyBean);
                } else {
                    node.setObjectValue((Serializable) DataTypeConstants.STRING.getDefaultValue());
                }
            } else if (model.getType().getTypeName().equals(DataTypeConstants.UUID.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.UUID.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.AUTO_INCREMENT.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.AUTO_INCREMENT.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.PICTURE.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.PICTURE.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.URL.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.URL.getDefaultValue());
            }
        }
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
            List<Element> list = _getDefaultXML(typeModel, realType, doc, language);
            Element root = list.get(0);
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

    private static List<Element> _getDefaultXML(TypeModel model, String realType, Document doc, String language) {
        List<Element> itemNodes = new ArrayList<Element>();
        if (model.getMinOccurs() > 1) {
            for (int i = 0; i < model.getMinOccurs(); i++) {
                Element el = doc.createElement(model.getName());
                applySimpleTypesDefaultValue(model, el);
                itemNodes.add(el);
            }
        } else {
            Element el = doc.createElement(model.getName());
            applySimpleTypesDefaultValue(model, el);
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
                    List<Element> els = _getDefaultXML(typeModel, realType, doc, language);
                    for (Element el : els) {
                        node.appendChild(el);
                    }
                }
            }
        }
        return itemNodes;
    }

    private static void applySimpleTypesDefaultValue(TypeModel nodeTypeModel, Element el) {

        if (nodeTypeModel != null && el != null) {

            if (nodeTypeModel.isSimpleType()) {
                if (nodeTypeModel.getType().equals(DataTypeConstants.BOOLEAN)) {
                    el.setTextContent("false"); //$NON-NLS-1$
                } else if (nodeTypeModel.getType().equals(DataTypeConstants.INT)
                        || nodeTypeModel.getType().equals(DataTypeConstants.INTEGER)
                        || nodeTypeModel.getType().equals(DataTypeConstants.SHORT)
                        || nodeTypeModel.getType().equals(DataTypeConstants.LONG)) {
                    el.setTextContent("0"); //$NON-NLS-1$
                } else if (nodeTypeModel.getType().equals(DataTypeConstants.DECIMAL)
                        || nodeTypeModel.getType().equals(DataTypeConstants.DOUBLE)
                        || nodeTypeModel.getType().equals(DataTypeConstants.FLOAT)) {
                    el.setTextContent("0.0"); //$NON-NLS-1$
                }
                // TODO is there any more?
            }

        }
    }
}
