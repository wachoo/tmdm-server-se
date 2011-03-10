package org.talend.mdm.webapp.itemsbrowser2.client.util;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

public class CommonUtil {

    public static String getConceptFromBrowseItemView(String viewPK) {
        String concept = viewPK.replaceAll("Browse_items_", "");
        concept = concept.replaceAll("#.*", "");
        return concept;
    }
    
    public static String getXpathSuffix(String xpath){
        return xpath.substring(xpath.lastIndexOf('/') + 1);
    }
    
    public static ItemBean createDefaultItemBean(String concept, ViewBean viewBean){
        ItemBean itemBean = new ItemBean(concept, "", null);
        
        Map<String, TypeModel> types = viewBean.getMetaDataTypes();
        Set<String> xpaths = types.keySet();
        for (String path : xpaths) {
            TypeModel typeModel = types.get(path);
            if (typeModel.isSimpleType()) {

                if (typeModel.getTypeName().equals(DataTypeConstants.DATE)) {
                    itemBean.set(path, new Date());
                } else if (typeModel.isMultiple()){
                    List<Serializable> list = new ArrayList<Serializable>();
                    int[] range = typeModel.getRange();
                    int min = range[0];
                    for (int i = 0;i < min;i++){
                        list.add("");
                    }
                    itemBean.set(path, list);
                } else {
                    itemBean.set(path, "");
                }
            }
        }
        
        return itemBean;
    }
}
