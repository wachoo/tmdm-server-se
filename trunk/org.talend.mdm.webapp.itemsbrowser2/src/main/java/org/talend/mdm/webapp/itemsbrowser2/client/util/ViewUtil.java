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
package org.talend.mdm.webapp.itemsbrowser2.client.util;

import org.talend.mdm.webapp.base.shared.TypeModel;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class ViewUtil {
    
    public static String getConceptFromBrowseItemView(String viewPK) {
        String concept = viewPK.replaceAll("Browse_items_", "");//$NON-NLS-1$ //$NON-NLS-2$
        concept = concept.replaceAll("#.*", "");//$NON-NLS-1$ //$NON-NLS-2$
        return concept;
    }
    
    
    /**
     * DOC HSHU Comment method "getSearchableLabel".
     */
    public static String getViewableLabel(String language, TypeModel typeModel) {
        
        String label = typeModel.getLabel(language);
        if(LabelUtil.isDynamicLabel(label)) {
            label = typeModel.getName();
        }
        return label;

    }

}
