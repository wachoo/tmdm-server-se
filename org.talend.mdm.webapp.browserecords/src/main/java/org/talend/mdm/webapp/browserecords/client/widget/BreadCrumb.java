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
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.model.BreadCrumbModel;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyUtil;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class BreadCrumb extends Composite {

    private BreadCrumbBar pWidget = new BreadCrumbBar();

    private ItemsDetailPanel itemsDetailPanel;

    public static String DEFAULTNAME = "Talend MDM", DEFAULTLINK = "../talendmdm/secure"; //$NON-NLS-1$ //$NON-NLS-2$    

    public BreadCrumb(List<BreadCrumbModel> list, ItemsDetailPanel itemsDetailPanel) {
        this.itemsDetailPanel = itemsDetailPanel;

        boolean isFirst = true;
        for (BreadCrumbModel bcm : list) {

            HTML h = initBreadCrumb(bcm.getConcept(), bcm.getLabel(), bcm.getIds(), bcm.getPkInfo(), bcm.isIfLink(), isFirst);
            pWidget.add(h);
            isFirst = false;
        }

        initWidget(pWidget);
    }

    public void appendBreadCrumb(String concept, String label, String ids, String pkInfo) {
        if (pWidget != null) {
            String title;
            if (label != null) {
                if (ids != null)
                    title = label + " " + ids; //$NON-NLS-1$
                else
                    title = label;
            } else
                title = ids;
            HTML tmph = new HTML("&nbsp;&gt;&nbsp;<a>" + title + "</a><input value=\"" + concept + "\"' type=\"hidden\">");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$    
            if (pWidget.getWidget(pWidget.getWidgetCount() - 1).getElement().getInnerHTML().equals(tmph.getHTML()))
                return;
            HTML h = initBreadCrumb(concept, label, ids, pkInfo, true, false);
            pWidget.add(h);
        }
    }

    private HTML initBreadCrumb(final String concept, final String label, final String ids, String pkInfo, boolean ifLink,
            boolean isFirst) {
        HTML h = null;
        String title;
        if (label != null) {
            if (ids != null)
                title = label + " " + ids; //$NON-NLS-1$
            else
                title = label;
        } else
            title = ids;
        if (ifLink) {
            h = new HTML("&nbsp;&gt;&nbsp;<a>" + title + "</a><input value=\"" + concept + "\"' type=\"hidden\">");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$             
            h.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    if (concept != null && concept.trim().length() > 0) {
                        ForeignKeyUtil.displayForeignKey(false, concept, ids, itemsDetailPanel);
                    } else {
                        displayCreatedEntity(label);
                    }
                    if (pWidget != null) {
                        HTML clickedHtml = (HTML) event.getSource();
                        pWidget.removeNeedless(clickedHtml);
                    }
                }

            });

        } else {
            h = new HTML((isFirst ? "" : "&nbsp;&gt;&nbsp;") + "<font>" + title + "</font>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        }
        h.getElement().setAttribute("titleText", title); //$NON-NLS-1$
        h.setWordWrap(false);
        return h;
    }

    private void displayCreatedEntity(String label) {
        itemsDetailPanel.clearContent();
        itemsDetailPanel.clearBanner();

        // Init Banner and BreadCrumb
        List<String> pkInfoList = new ArrayList<String>();
        pkInfoList.add(label);
        itemsDetailPanel.initBanner(pkInfoList, null);
        List<BreadCrumbModel> breads = new ArrayList<BreadCrumbModel>();
        breads.add(new BreadCrumbModel("", BreadCrumb.DEFAULTNAME, null, null, false)); //$NON-NLS-1$
        breads.add(new BreadCrumbModel("", label, null, null, true)); //$NON-NLS-1$
        itemsDetailPanel.initBreadCrumb(new BreadCrumb(breads, itemsDetailPanel));
        // Display UI
        ItemPanel panel = BrowseRecords.getSession().getCurrentCreatedEntity();
        if (panel != null)
            itemsDetailPanel.addTabItem(label, panel, ItemsDetailPanel.SINGLETON, label);
    }

    public void adjust() {
        pWidget.adjust();
    }
}
