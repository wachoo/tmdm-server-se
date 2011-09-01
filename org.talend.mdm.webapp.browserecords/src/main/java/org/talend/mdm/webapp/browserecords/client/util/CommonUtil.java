package org.talend.mdm.webapp.browserecords.client.util;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.DataTypeConstants;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

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

    public static List<ItemNodeModel> getDefaultTreeModel(TypeModel model) {
        List<ItemNodeModel> itemNodes = new ArrayList<ItemNodeModel>();

        if (model.getMinOccurs() > 1 && model.getMaxOccurs() > model.getMinOccurs()) {
            for (int i = 0; i < model.getMaxOccurs()-model.getMinOccurs(); i++) {
                ItemNodeModel itemNode = new ItemNodeModel();
                itemNodes.add(itemNode);
            }
        } else {
            ItemNodeModel itemNode = new ItemNodeModel();
            itemNodes.add(itemNode);
        }

        for (ItemNodeModel node : itemNodes) {
            if(model.isSimpleType()){
                if(model.getType().getTypeName().equals(DataTypeConstants.INTEGER.getTypeName()))
                    node.setValue("0"); //$NON-NLS-1$
                if (model.getType().getTypeName().equals(DataTypeConstants.DOUBLE.getTypeName()))
                    node.setValue("0.0"); //$NON-NLS-1$
                if (model.getType().getTypeName().equals(DataTypeConstants.PICTURE.getTypeName()))
                    node.setValue("http://hiphotos.baidu.com/baidu/pic/item/80dd07f45cff7f837709d75a.jpg"); //$NON-NLS-1$
                if (model.getType().getTypeName().equals(DataTypeConstants.BOOLEAN.getTypeName()))
                    node.setValue("true"); //$NON-NLS-1$
                if (model.getType().getTypeName().equals(DataTypeConstants.STRING.getTypeName()))
                    node.setValue(""); //$NON-NLS-1$
                if (model.getType().getTypeName().equals(DataTypeConstants.DOUBLE.getTypeName()))
                    node.setValue("0.0"); //$NON-NLS-1$
                if (model.getType().getTypeName().equals(DataTypeConstants.DECIMAL.getTypeName()))
                    node.setValue("0.0"); //$NON-NLS-1$
                if (model.getType().getTypeName().equals(DataTypeConstants.URL.getTypeName()))
                    node.setValue("www.baidu.com"); //$NON-NLS-1$
                // TODO other type
                
                node.setName(model.getName());

            } else {
                ComplexTypeModel complexModel = (ComplexTypeModel) model;
                node.setName(complexModel.getName());
                List<TypeModel> children = complexModel.getSubTypes();
                List<ItemNodeModel> list = new ArrayList<ItemNodeModel>();
                for (TypeModel typeModel : children) {
                    list.addAll(getDefaultTreeModel(typeModel));
                }
                node.setChildNodes(list);
            }
        }
        
        return itemNodes;
    }
}