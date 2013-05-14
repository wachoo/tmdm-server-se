// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.creator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ItemCreator {

    /**
     * DOC HSHU Comment method "createDefaultItemBean".
     * 
     * @param concept
     * @param entityModel
     * @return
     */
    public static ItemBean createDefaultItemBean(String concept, EntityModel entityModel) {
        ItemBean itemBean = new ItemBean(concept, "", null);//$NON-NLS-1$

        Map<String, TypeModel> types = entityModel.getMetaDataTypes();
        Set<String> xpaths = types.keySet();
        for (String path : xpaths) {
            TypeModel typeModel = types.get(path);
            if (typeModel.isSimpleType()) {

                if (typeModel.getType().equals(DataTypeConstants.DATE)) {
                    itemBean.set(path, new Date());
                } else if (typeModel.isMultiOccurrence()) {
                    List<Serializable> list = new ArrayList<Serializable>();
                    int[] range = typeModel.getRange();
                    int min = range[0];
                    for (int i = 0; i < min; i++) {
                        list.add("");//$NON-NLS-1$
                    }
                    itemBean.set(path, list);
                } else {
                    itemBean.set(path, "");//$NON-NLS-1$
                }
            }
        }

        return itemBean;
    }

}
