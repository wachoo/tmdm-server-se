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
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.BreadCrumbModel;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;

public class BreadCrumb extends Composite {

    private HorizontalPanel pWidget = new HorizontalPanel();
    
    private ItemsDetailPanel itemsDetailPanel;

    public static String DEFAULTNAME = "Talend MDM", DEFAULTLINK = "../talendmdm/secure"; //$NON-NLS-1$ //$NON-NLS-2$    

    public BreadCrumb(List<BreadCrumbModel> list, ItemsDetailPanel itemsDetailPanel) {
        this.itemsDetailPanel = itemsDetailPanel;
        int i = 0;

        for (BreadCrumbModel bcm : list) {

            HTML h = initBreadCrumb(bcm.getConcept(), bcm.getLabel(), bcm.getIds(), bcm.getPkInfo(), bcm.isIfLink());
            pWidget.add(h);
            ++i;
            if (i != list.size())
            	pWidget.add(new HTML("&nbsp;&gt;&nbsp;"));//$NON-NLS-1$            
        }

        initWidget(pWidget);
    }

    public void appendBreadCrumb(String concept, String label, String ids, String pkInfo) {
        if (pWidget != null) {
            String title;
            if (label != null) {
                if (pkInfo != null)
                    title = label + " " + pkInfo; //$NON-NLS-1$
                else if (ids != null)
                    title = label + " " + ids; //$NON-NLS-1$
                else
                    title = label;
            } else
                title = ids;
            HTML tmph = new HTML("<a>" + title + "</a><input value=\"" + concept + "\"' type=\"hidden\">");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$    
            if (pWidget.getWidget(pWidget.getWidgetCount() - 1).getElement().getInnerHTML().equals(tmph.getHTML()))
                return;
            pWidget.add(new HTML("&nbsp;&gt;&nbsp;"));//$NON-NLS-1$      
            HTML h = initBreadCrumb(concept, label, ids, pkInfo, true);
            pWidget.add(h);
        }
    }

    private HTML initBreadCrumb(final String concept, String label, final String ids, String pkInfo, boolean ifLink) {
        HTML h = null;
        String title;
        if (label != null) {
            if (pkInfo != null)
                title = label + " " + pkInfo; //$NON-NLS-1$
            else if (ids != null)
                title = label + " " + ids; //$NON-NLS-1$
            else
                title = label;
        }
        else
            title = ids;
        if (ifLink) {
            h = new HTML("<a>" + title + "</a><input value=\"" + concept + "\"' type=\"hidden\">");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$             
            h.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    ForeignKeyUtil.displayForeignKey(false, concept, ids, itemsDetailPanel);
                    if (pWidget != null) {
                        HTML clickedHtml = (HTML) event.getSource();
                        int index = pWidget.getWidgetIndex(clickedHtml);
                        if (index > -1) {
                            while (pWidget.getWidgetCount() - 1 > index) {
                                pWidget.remove(pWidget.getWidgetCount() - 1);
                            }
                        }
                    }
                }

            });

        } else {
            h = new HTML("<font>" + title + "</font>"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        return h;
    }

}
