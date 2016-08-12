// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.rest.ExplainRestServiceHandler;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class BulkUpdatePanel extends ContentPanel {
	private static BulkUpdatePanel instance;

    private ContentPanel detailPanel;

    public static BulkUpdatePanel getInstance() {
        if (instance == null) {
            instance = new BulkUpdatePanel();
        }
        return instance;
    }

    private BulkUpdatePanel() {
        setHeading(MessagesFactory.getMessages().staging_data_viewer_title());
        setHeaderVisible(false);
        setLayout(new BorderLayout());
        setBorders(false);

//        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 200);
//        northData.setFloatable(true);
//        northData.setSplit(true);
//        add(LineageTabPanel.getInstance(), northData);
//
//        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
//        detailPanel = new ContentPanel();
//        detailPanel.setFrame(false);
//        detailPanel.setHeaderVisible(false);
//        detailPanel.setLayout(new FitLayout());
//        detailPanel.setBodyBorder(false);
//        add(detailPanel, centerData);
        
//        setLayout(new BorderLayout());
//        setBorders(false);

//        ContentPanel topPanel = new ContentPanel() {
//
//            @Override
//            protected void onResize(int width, int height) {
//                super.onResize(width, height);
//                ItemsToolBar.getInstance().setWidth(width);
//                ItemsToolBar.getInstance().getAdvancedPanel().setWidth(width);
//                this.layout(true);
//            }
//        };

//        topPanel.setHeaderVisible(false);

//        topPanel.add(ItemsToolBar.getInstance());
//        topPanel.add(ItemsToolBar.getInstance().getAdvancedPanel());
//        northData = new BorderLayoutData(LayoutRegion.NORTH);
//        northData.setSize(34);
//        add(topPanel, northData);

        BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 470);
        westData.setSplit(true);
        westData.setMargins(new Margins(0, 5, 0, 0));
        westData.setFloatable(true);
        westData.setMinSize(0);
        westData.setMaxSize(7000);
        add(BulkUpdateListPanel.getInstance(), westData);

        add(new ContentPanel(), new BorderLayoutData(LayoutRegion.CENTER));

    }

    public void init(String concept, String taskId) {
        final LineageTabPanel lineageTabPanel = LineageTabPanel.getInstance();
        lineageTabPanel.init();
        lineageTabPanel.getLineageListPanel().initPanel(taskId);
        lineageTabPanel.getLineageListPanel().layout();
        ExplainRestServiceHandler.get().explainGroupResult(BrowseRecords.getSession().getAppHeader().getMasterDataCluster(),
                concept, taskId, new SessionAwareAsyncCallback<BaseTreeModel>() {

                    @Override
                    public void onSuccess(BaseTreeModel rootNode) {
                        lineageTabPanel.getExplainTablePanel().buildTree(rootNode);
                    }
                });
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
