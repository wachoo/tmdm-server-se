package org.talend.mdm.webapp.browserecords.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.data.ModelData;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class CommonUtil {

    public static String getXpathSuffix(String xpath) {
        return xpath.substring(xpath.lastIndexOf('/') + 1);
    }

    public static String getElementFromXpath(String xpath) {
        String[] arr = xpath.split("/");//$NON-NLS-1$
        for (int i = arr.length - 1; i > -1; i--) {
            if (arr[i] != "")//$NON-NLS-1$
                return arr[i];
        }
        return xpath;
    }

    public static String toXML(ItemNodeModel nodeModel, ViewBean viewBean) {
        Document doc = XMLParser.createDocument();
        Element root = _toXML(doc, nodeModel, viewBean);
        root.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance"); //$NON-NLS-1$//$NON-NLS-2$
        doc.appendChild(root);
        return doc.toString();
    }

    private static Element _toXML(Document doc, ItemNodeModel nodeModel, ViewBean viewBean) {
        Element root = doc.createElement(nodeModel.getName());
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(nodeModel.getBindingPath());
        Serializable value = nodeModel.getObjectValue();
        if (typeModel.isSimpleType() && value != null && nodeModel.getParent() != null) {
            if (value instanceof ForeignKeyBean)
                root.appendChild(doc.createTextNode(((ForeignKeyBean) value).getId()));
            else
                root.appendChild(doc.createTextNode(value.toString()));
        }

        if (nodeModel.getRealType() != null) {
            root.setAttribute("xsi:type", nodeModel.getRealType()); //$NON-NLS-1$
        }

        List<ModelData> children = nodeModel.getChildren();
        if (children != null) {
            for (ModelData child : children) {
                Element el = _toXML(doc, (ItemNodeModel) child, viewBean);
                root.appendChild(el);
            }
        }
        return root;
    }

    public static List<ItemNodeModel> getDefaultTreeModel(TypeModel model, String language) {
        List<ItemNodeModel> itemNodes = new ArrayList<ItemNodeModel>();

        if (model.getMinOccurs() > 1 && model.getMaxOccurs() > model.getMinOccurs()) {
            for (int i = 0; i < model.getMaxOccurs() - model.getMinOccurs(); i++) {
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
            if (model.isSimpleType()) {
                setDefaultValue(model, node);
            } else {
                ComplexTypeModel complexModel = (ComplexTypeModel) model;
                List<TypeModel> children = complexModel.getSubTypes();
                List<ItemNodeModel> list = new ArrayList<ItemNodeModel>();
                for (TypeModel typeModel : children) {
                    list.addAll(getDefaultTreeModel(typeModel, language));
                }
                node.setChildNodes(list);
            }
            node.setName(model.getName());
            node.setBindingPath(model.getXpath());
            node.setDescription(model.getDescriptionMap().get(language));
            node.setLabel(model.getLabel(language));
        }
        return itemNodes;
    }

    private static void setDefaultValue(TypeModel model, ItemNodeModel node) {

        if (model.getDefaultValueExpression() != null && model.getDefaultValueExpression().trim().length() > 0) {
            if (!"".equals(model.getForeignkey()) && model.getForeignkey() != null) { //$NON-NLS-1$
                ForeignKeyBean foreignKeyBean = new ForeignKeyBean();
                foreignKeyBean.setId(model.getDefaultValue());
                foreignKeyBean.setForeignKeyPath(model.getForeignkey());
                node.setObjectValue(foreignKeyBean);
            } else {
                node.setObjectValue(model.getDefaultValue());
            }
        } else {
            if (model.getType().getTypeName().equals(DataTypeConstants.BOOLEAN.getTypeName())) {
                node.setObjectValue((Serializable) DataTypeConstants.BOOLEAN.getDefaultValue());
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DATE.getTypeName())) {
                String dateStr = DateUtil.getDate((Date) DataTypeConstants.DATE.getDefaultValue());
                node.setObjectValue(dateStr);
            } else if (model.getType().getTypeName().equals(DataTypeConstants.DATETIME.getTypeName())) {
                String dateStr = DateUtil.getDateTime((Date) DataTypeConstants.DATETIME.getDefaultValue());
                node.setObjectValue(dateStr);
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
    
    public static ItemNodeModel recrusiveRoot(ItemNodeModel node) {
    	ItemNodeModel parent = (ItemNodeModel) node.getParent();
    	ItemNodeModel root = null;
    	
    	if(parent.getParent() != null) {
    		root = recrusiveRoot(parent);
    	}
    	else {
    		root = parent;
    	}
    	
    	return root;
    }

    public static String pickOutISOMessage(String message) {
        String identy = "[" + Locale.getLanguage()//$NON-NLS-1$
                .toUpperCase() + ":";//$NON-NLS-1$
        int mask = message.indexOf(identy);
        if (mask != -1) {
            String snippet = message.substring(mask + identy.length());
            if (!snippet.isEmpty()) {
                String pickOver = "";//$NON-NLS-1$
                boolean enclosed = false;
                for (int j = 0; j < snippet.trim().length(); j++) {
                    String c = snippet.trim().charAt(j) + "";//$NON-NLS-1$
                    if ("]".equals(c)) {//$NON-NLS-1$
                        if (!pickOver.isEmpty()) {
                            enclosed = true;
                            break;
                        }
                    } else {
                        pickOver += c;
                    }
                }

                if (enclosed)
                    return pickOver;
            }
        }
        return message;
    }
}
