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

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TreeItem;

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

    public static void copyStyleToTreeItem(TreeItem source, TreeItem target) {
        setStyleAttribute(target.getElement(), getStyleAttribute(source.getElement()));
        if (source.getWidget() instanceof HorizontalPanel) {
            final HorizontalPanel sourceHp = (HorizontalPanel) source.getWidget();
            final HorizontalPanel targetHp = (HorizontalPanel) target.getWidget();
            HTML sourceLabel = (HTML) sourceHp.getWidget(0);
            HTML targetLabel = (HTML) targetHp.getWidget(0);
            setStyleAttribute(targetLabel.getElement(), getStyleAttribute(sourceLabel.getElement()));
            if (sourceHp.getWidgetCount() >= 2 && sourceHp.getWidget(1) instanceof Field<?>) {
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        El sourceInputEl = getInputEl((Field<?>) sourceHp.getWidget(1));
                        El targetInputEl = getInputEl((Field<?>) targetHp.getWidget(1));
                        setStyleAttribute(targetInputEl.dom, getStyleAttribute(sourceInputEl.dom));
                    }
                });
            }
        }
    }

    public static void setStyleAttribute(Element el, String styleText) {
        if (GXT.isIE) {
            el.getStyle().setProperty("cssText", styleText); //$NON-NLS-1$
        } else {
            el.setAttribute("style", styleText); //$NON-NLS-1$
        }
    }
    public static String getStyleAttribute(Element el) {
        if (GXT.isIE) {
            return el.getStyle().getProperty("cssText"); //$NON-NLS-1$
        } else {
            return el.getAttribute("style"); //$NON-NLS-1$
        }
    }

    private static native El getInputEl(Field<?> field)/*-{
        return field.@com.extjs.gxt.ui.client.widget.form.Field::getInputEl()();
    }-*/;

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
