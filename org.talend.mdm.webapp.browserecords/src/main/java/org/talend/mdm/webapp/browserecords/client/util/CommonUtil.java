package org.talend.mdm.webapp.browserecords.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

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

    public static String toXML(ItemNodeModel nodeModel) {
        Document doc = XMLParser.createDocument();
        Element root = _toXML(doc, nodeModel);
        doc.appendChild(root);
        return doc.toString();
    }

    @SuppressWarnings("unchecked")
    private static Element _toXML(Document doc, ItemNodeModel nodeModel) {
        Element root = doc.createElement(nodeModel.getName());
        Serializable value = nodeModel.getObjectValue();
        if (value != null && nodeModel.getParent() != null) {
            if (value instanceof ForeignKeyBean)
                root.appendChild(doc.createTextNode(((ForeignKeyBean) value).getId()));
            else if (value instanceof List<?>) {// FK list
                StringBuffer sb = new StringBuffer();
                for (ForeignKeyBean fkBean : (List<ForeignKeyBean>) value) {
                    sb.append(fkBean.getId());
                }
                root.appendChild(doc.createTextNode(sb.toString()));
            } else {
                root.appendChild(doc.createTextNode(value.toString()));
            }

        }

        root.setNodeValue(nodeModel.getValue());
        List<ModelData> children = nodeModel.getChildren();
        if (children != null) {
            for (ModelData child : children) {
                Element el = _toXML(doc, (ItemNodeModel) child);
                root.appendChild(el);
            }
        }

        return root;
    }

    public static List<ItemNodeModel> getDefaultTreeModel(TypeModel model) {
        List<ItemNodeModel> itemNodes = new ArrayList<ItemNodeModel>();

        if (model.getMinOccurs() > 1 && model.getMaxOccurs() > model.getMinOccurs()) {
            for (int i = 0; i < model.getMaxOccurs() - model.getMinOccurs(); i++) {
                ItemNodeModel itemNode = new ItemNodeModel();
                itemNodes.add(itemNode);
            }
        } else {
            ItemNodeModel itemNode = new ItemNodeModel();
            itemNodes.add(itemNode);
        }

        for (ItemNodeModel node : itemNodes) {
            if (model.isSimpleType()) {
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



            } else {
                ComplexTypeModel complexModel = (ComplexTypeModel) model;
                List<TypeModel> children = complexModel.getSubTypes();
                List<ItemNodeModel> list = new ArrayList<ItemNodeModel>();
                for (TypeModel typeModel : children) {
                    list.addAll(getDefaultTreeModel(typeModel));
                }
                node.setChildNodes(list);
            }
            node.setName(model.getName());
            node.setBindingPath(model.getXpath());
            node.setDescription(model.getLabel(Locale.getLanguage()));
        }

        return itemNodes;
    }
}
