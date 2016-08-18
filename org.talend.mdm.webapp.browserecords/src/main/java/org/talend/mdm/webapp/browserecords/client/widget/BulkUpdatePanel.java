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

import java.util.List;

import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class BulkUpdatePanel extends ContentPanel {

    private static BulkUpdatePanel instance;

    private BulkUpdateListPanel bulkUpdateListPanel;

    private ContentPanel detailPanel;

    private List<String> idsList;

    public static BulkUpdatePanel getInstance() {
        if (instance == null) {
            instance = new BulkUpdatePanel();
        }
        return instance;
    }

    private BulkUpdatePanel() {
        setHeading(MessagesFactory.getMessages().bulkUpdate_title());
        setHeaderVisible(false);
        setLayout(new BorderLayout());
        setBorders(false);

        // BorderLayoutData westData = new BorderLayoutData(LayoutRegion.WEST, 470);
        // westData.setSplit(true);
        // westData.setMargins(new Margins(0, 5, 0, 0));
        // westData.setFloatable(true);
        // westData.setMinSize(0);
        // westData.setMaxSize(7000);
        // bulkUpdateListPanel = BulkUpdateListPanel.getInstance();
        // add(BulkUpdateListPanel.getInstance(), westData);

        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        detailPanel = new ContentPanel();
        detailPanel.setFrame(false);
        detailPanel.setHeaderVisible(false);
        detailPanel.setLayout(new FitLayout());
        detailPanel.setBodyBorder(false);
        add(detailPanel, centerData);
    }

    // public void init(String concept, String taskId) {
    // final LineageTabPanel lineageTabPanel = LineageTabPanel.getInstance();
    // lineageTabPanel.init();
    // lineageTabPanel.getLineageListPanel().initPanel(taskId);
    // lineageTabPanel.getLineageListPanel().layout();
    // ExplainRestServiceHandler.get().explainGroupResult(BrowseRecords.getSession().getAppHeader().getMasterDataCluster(),
    // concept, taskId, new SessionAwareAsyncCallback<BaseTreeModel>() {
    //
    // @Override
    // public void onSuccess(BaseTreeModel rootNode) {
    // lineageTabPanel.getExplainTablePanel().buildTree(rootNode);
    // }
    // });
    // }

    public void initDetailPanel(EntityModel entityModel, ViewBean viewBean, List<String> idsList) {
        this.idsList = idsList;
        AppEvent event = new AppEvent(BrowseRecordsEvents.ViewBulkUpdateItem);
        event.setData(BrowseRecords.ENTITY_MODEL, entityModel);
        event.setData(BrowseRecords.VIEW_BEAN, viewBean);
        Dispatcher.forwardEvent(event);
    }

    public void updateDetailPanel(ItemsDetailPanel itemsDetailPanel) {
        detailPanel.removeAll();
        detailPanel.add(itemsDetailPanel);
        detailPanel.layout();
    }

    public void clearDetailPanel() {
        detailPanel.removeAll();
    }

    public BulkUpdateListPanel getBulkUpdateListPanel() {
        return bulkUpdateListPanel;
    }

    public List<String> getIdsList() {
        return idsList;
    }

    public native void closeBulkUpdatePanel()/*-{
		var tabPanel = $wnd.amalto.core.getTabPanel();
		var panel = tabPanel.getItem("bulkUpdatePanel");
		if (panel != undefined) {
			tabPanel.remove(panel);
		}
    }-*/;
}
