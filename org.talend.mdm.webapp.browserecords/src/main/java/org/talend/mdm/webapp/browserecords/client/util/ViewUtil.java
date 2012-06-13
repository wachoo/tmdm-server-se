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
package org.talend.mdm.webapp.browserecords.client.util;

import java.util.List;

import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

/**
 * DOC HSHU class global comment. Detailled comment
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
        if (LabelUtil.isDynamicLabel(label)) {
            label = typeModel.getName();
        }
        return label;

    }

	public static ItemBaseModel getDefaultSmartViewModel(List<ItemBaseModel> list, String concept) {
		String defSmartView = "Smart_view_" + concept; //$NON-NLS-1$
		String defSmartViewWithLang = defSmartView + "_" + Locale.getLanguage(); //$NON-NLS-1$
		ItemBaseModel model = null;
		for (ItemBaseModel item : list) {
			if (item.get("key").toString().toUpperCase()
					.startsWith(defSmartView.toUpperCase())) {
				if (item.get("key").toString().equalsIgnoreCase(defSmartView)) {
					return item;
				}

				if (item.get("key").toString().equalsIgnoreCase(defSmartViewWithLang)) {
					return item;
				}

				if (model == null) {
					model = item;
				}
			}
		}
		return model;
	}
}
