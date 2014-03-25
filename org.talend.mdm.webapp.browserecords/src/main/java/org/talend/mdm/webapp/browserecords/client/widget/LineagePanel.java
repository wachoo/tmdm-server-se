// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class LineagePanel extends ContentPanel {

    private static LineagePanel instance;

    LayoutContainer lineageLayoutContainer;

    private ContentPanel detailPanel;

    public static LineagePanel getInstance() {
        if (instance == null) {
            instance = new LineagePanel();
        }
        return instance;
    }

    private LineagePanel() {
        setHeading(MessagesFactory.getMessages().staging_data_viewer_title());
        setHeaderVisible(false);
        lineageLayoutContainer = new LayoutContainer();
        lineageLayoutContainer.setLayout(new BorderLayout());
        lineageLayoutContainer.setBorders(false);

        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 325);
        lineageLayoutContainer.add(LineageListPanel.getInstance(), northData);

        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        detailPanel = new ContentPanel();
        detailPanel.setFrame(false);
        detailPanel.setHeaderVisible(false);
        detailPanel.setLayout(new FitLayout());
        detailPanel.setBodyBorder(false);
        detailPanel.setSize(300, 360);
        lineageLayoutContainer.add(detailPanel, centerData);
        add(lineageLayoutContainer);
    }

    public void updateDetailPanel(ItemsDetailPanel itemsDetailPanel) {
        detailPanel.removeAll();
        detailPanel.add(itemsDetailPanel);
        detailPanel.layout();
    }

    public void clearDetailPanel() {
        detailPanel.removeAll();
    }
}
