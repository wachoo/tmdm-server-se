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

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.rest.ExplainRestServiceHandler;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class LineagePanel extends ContentPanel {

    private static LineagePanel instance;

    private LineageListPanel lineageListPanel;

    private ContentPanel detailPanel;

    private ContentPanel explainTreePanel;

    public static LineagePanel getInstance() {
        if (instance == null) {
            instance = new LineagePanel();
        }
        return instance;
    }

    private LineagePanel() {
        setHeading(MessagesFactory.getMessages().staging_data_viewer_title());
        setHeaderVisible(false);
        setLayout(new BorderLayout());
        setBorders(false);

        ContentPanel contentPanel = new ContentPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setHeaderVisible(false);
        contentPanel.setLayout(new BorderLayout());
        contentPanel.setBorders(false);

        BorderLayoutData northData = new BorderLayoutData(LayoutRegion.NORTH, 325);
        northData.setFloatable(true);
        northData.setSplit(true);
        lineageListPanel = LineageListPanel.getInstance();
        contentPanel.add(lineageListPanel, northData);

        BorderLayoutData southData = new BorderLayoutData(LayoutRegion.CENTER);
        detailPanel = new ContentPanel();
        detailPanel.setFrame(false);
        detailPanel.setHeaderVisible(false);
        detailPanel.setLayout(new FitLayout());
        detailPanel.setBodyBorder(false);
        contentPanel.add(detailPanel, southData);

        BorderLayoutData eastData = new BorderLayoutData(LayoutRegion.EAST);
        eastData.setSplit(true);
        eastData.setCollapsible(true);
        eastData.setFloatable(false);
        eastData.setMargins(new Margins(0, 0, 0, 5));
        explainTreePanel = new ContentPanel();
        explainTreePanel.setHeading(MessagesFactory.getMessages().explain_result_title());
        explainTreePanel.setScrollMode(Scroll.AUTO);
        explainTreePanel.setLayout(new FitLayout());
        add(explainTreePanel, eastData);

        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        add(contentPanel, centerData);

    }

    public void init(String concept, String taskId) {
        lineageListPanel.initPanel(taskId);
        explainTreePanel.removeAll();
        ExplainRestServiceHandler.get().explainGroupResult(BrowseRecords.getSession().getAppHeader().getMasterDataCluster(),
                concept, taskId, new SessionAwareAsyncCallback<BaseTreeModel>() {

                    @Override
                    public void onSuccess(BaseTreeModel root) {
                        TreePanel<BaseTreeModel> tree = CommonUtil.getExplainResultPanel(root);
                        explainTreePanel.add(tree);
                        explainTreePanel.layout();
                        tree.expandAll();
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
