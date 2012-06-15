package org.talend.mdm.webapp.browserecords.client.util;

import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;

import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;
import com.google.gwt.xml.client.XMLParser;

public class CommonUtilTestData {

    public static EntityModel getEntityModel(String modelXml) {
        Element root = XMLParser.parse(modelXml).getDocumentElement();
        EntityModel entity = new EntityModel();
        entity.setConceptName(root.getAttribute("concept")); //$NON-NLS-1$

        NodeList childNodes = root.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childEl = (Element) child;
                    boolean isSimple = Boolean.parseBoolean(childEl.getAttribute("isSimple")); //$NON-NLS-1$
                    boolean isFk = Boolean.parseBoolean(childEl.getAttribute("isFk")); //$NON-NLS-1$
                    String typePath = childEl.getAttribute("typePath"); //$NON-NLS-1$
                    TypeModel tm = null;
                    if (isSimple){
                        SimpleTypeModel stm = new SimpleTypeModel();
                        stm.setForeignkey(isFk ? "fk/fk" : null); //$NON-NLS-1$
                        stm.setXpath(typePath.replaceAll(":\\w+", ""));  //$NON-NLS-1$//$NON-NLS-2$
                        tm = stm;
                    } else {
                        ComplexTypeModel ctm = new ComplexTypeModel();
                        ctm.setXpath(typePath.replaceAll(":\\w+", ""));  //$NON-NLS-1$//$NON-NLS-2$
                        tm = ctm;
                    }
                    tm.setTypePath(typePath);
                    entity.getMetaDataTypes().put(typePath, tm);
                }
            }
        }


        return entity;
    }

    public static ItemNodeModel getItemNodeModel(String recordXml, EntityModel entityModel) {
        Element root = XMLParser.parse(recordXml).getDocumentElement();
        return builderItemNodeModel(root, entityModel);
    }

    private static ItemNodeModel builderItemNodeModel(Element el, EntityModel entityModel) {
        ItemNodeModel node = new ItemNodeModel(el.getNodeName());
        node.setTypePath(getElTypePath(el));
        TypeModel tm = entityModel.getMetaDataTypes().get(node.getTypePath());
        if (tm.isSimpleType()) {
            if (tm.getForeignkey() != null && tm.getForeignkey().trim().length() > 0) {
                if (el.getNodeValue() != null && el.getNodeValue().trim().length() > 0) {
                    ForeignKeyBean fkBean = new ForeignKeyBean();
                    fkBean.setId(el.getNodeValue());
                    node.setObjectValue(fkBean);
                } else {
                    node.setObjectValue(null);
                }
            } else {
                if (el.getFirstChild() != null) {
                    node.setObjectValue(el.getFirstChild().getNodeValue());
                } else {
                    node.setObjectValue(null);
                }
            }
        }

        NodeList childNodes = el.getChildNodes();
        if (childNodes != null) {
            for (int i = 0; i < childNodes.getLength(); i++) {
                Node child = childNodes.item(i);
                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    Element childEl = (Element) child;
                    ItemNodeModel childNode = builderItemNodeModel(childEl, entityModel);
                    node.add(childNode);
                }
            }
        }
        return node;
    }

    private static String getElTypePath(Element el) {
        String typePath = ""; //$NON-NLS-1$
        Node current = el;
        boolean isFirst = true;
        while (current != null && current instanceof Element) {
            if (isFirst) {
                typePath = getElTypeName((Element) current) + typePath;
                isFirst = false;
            } else {
                typePath = getElTypeName((Element) current) + "/" + typePath; //$NON-NLS-1$    
            }
            current = current.getParentNode();
        }
        return typePath;
    }

    private static String getElTypeName(Element el) {
        String type = el.getAttribute("xsi:type"); //$NON-NLS-1$
        if (type != null && type.trim().length() > 0) {
            return el.getNodeName() + ":" + type; //$NON-NLS-1$
        }
        return el.getNodeName();
    }

}
