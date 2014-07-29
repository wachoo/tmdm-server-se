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

import java.util.LinkedList;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.StagingConstant;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGridCellRenderer;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;

public class ExplainTablePanel extends ContentPanel {

    private TreeGrid<ModelData> tree;

    private ColumnModel columnModel;

    public ExplainTablePanel() {
        setHeaderVisible(false);
        setScrollMode(Scroll.AUTO);
        setLayout(new FitLayout());
    }

    public void buildTree(BaseTreeModel root) {
        buildColumn((List<String>) root.get(StagingConstant.MATCH_FIELD_LIST));
        TreeStore<ModelData> store = new TreeStore<ModelData>();
        store.add(root.getChildren(), true);
        tree = new TreeGrid<ModelData>(store, columnModel);
        tree.setAutoExpandColumn(StagingConstant.MATCH_GROUP_NAME);
        tree.setBorders(true);
        tree.getView().setForceFit(true);
        tree.getView().setAutoFill(true);
        addSelectedItemListener(tree);
        add(tree);
        tree.expandAll();
        layout();
    }

    public void buildColumn(List<String> matchFieldList) {
        List<ColumnConfig> columnList = new LinkedList<ColumnConfig>();
        ColumnConfig groupColumn = new ColumnConfig(StagingConstant.MATCH_GROUP_NAME, MessagesFactory.getMessages()
                .explainResult_group_header(), 50);
        groupColumn.setRenderer(new TreeGridCellRenderer<ModelData>());
        columnList.add(groupColumn);
        ColumnConfig idColumn = new ColumnConfig(StagingConstant.MATCH_GROUP_ID, MessagesFactory.getMessages()
                .explainResult_id_header(), 50);
        columnList.add(idColumn);
        if (matchFieldList != null) {
            for (int i = 0; i < matchFieldList.size(); i++) {
                String fieldName = matchFieldList.get(i);
                ColumnConfig matchFieldColumn = new ColumnConfig(fieldName, fieldName, 100);
                columnList.add(matchFieldColumn);
            }
        }
        ColumnConfig groupIdColumn = new ColumnConfig(StagingConstant.MATCH_GROUP_GID, MessagesFactory.getMessages()
                .explainResult_gid_header(), 50);
        columnList.add(groupIdColumn);
        ColumnConfig groupSizeColumn = new ColumnConfig(StagingConstant.MATCH_GROUP_SZIE, MessagesFactory.getMessages()
                .explainResult_gsize_header(), 100);
        columnList.add(groupSizeColumn);
        ColumnConfig confidenceColumn = new ColumnConfig(StagingConstant.MATCH_GROUP_CONFIDENCE, MessagesFactory.getMessages()
                .explainResult_confidence_header(), 100);
        columnList.add(confidenceColumn);
        ColumnConfig scoreColumn = new ColumnConfig(StagingConstant.MATCH_SCORE, MessagesFactory.getMessages()
                .explainResult_attrscore_header(), 50);
        columnList.add(scoreColumn);
        ColumnConfig detailsColumn = new ColumnConfig(StagingConstant.MATCH_DETAILS, MessagesFactory.getMessages()
                .explainResult_details_header(), 100);
        detailsColumn.setRenderer(new GridCellRenderer<ModelData>() {

            @Override
            public Object render(final ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid) {
                if (model.get(StagingConstant.MATCH_IS_GROUP)) {
                    Button detailButton = new Button("..."); //$NON-NLS-1$
                    detailButton.setSize(40, 10);
                    detailButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

                        @Override
                        public void componentSelected(ButtonEvent ce) {
                            BaseTreeModel root = model.get(StagingConstant.MATCH_DATA);
                            Window explainWindow = new Window();
                            explainWindow.setHeading(MessagesFactory.getMessages().compare_result_title());
                            explainWindow.setSize(800, 600);
                            explainWindow.setLayout(new FitLayout());
                            explainWindow.setScrollMode(Scroll.NONE);
                            TreeStore<BaseTreeModel> detailStore = new TreeStore<BaseTreeModel>();
                            detailStore.add(root, true);
                            TreePanel<BaseTreeModel> detailTree = new TreePanel<BaseTreeModel>(detailStore);
                            detailTree.setDisplayProperty(StagingConstant.DISPLAY_NAME);
                            ContentPanel contentPanel = new ContentPanel();
                            contentPanel.setHeaderVisible(false);
                            contentPanel.setScrollMode(Scroll.AUTO);
                            contentPanel.setLayout(new FitLayout());
                            contentPanel.add(detailTree);
                            explainWindow.add(contentPanel);
                            explainWindow.show();
                            detailTree.expandAll();
                        }
                    });
                    return detailButton;
                } else {
                    return ""; //$NON-NLS-1$
                }
            }
        });
        columnList.add(detailsColumn);
        columnModel = new ColumnModel(columnList);
    }

    public void addSelectedItemListener(TreeGrid<ModelData> target) {
        target.getSelectionModel().addSelectionChangedListener(new SelectionChangedListener<ModelData>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ModelData> event) {
                String id = event.getSelectedItem().get(StagingConstant.MATCH_GROUP_ID);
                boolean isGroup = event.getSelectedItem().get(StagingConstant.MATCH_IS_GROUP);
                if (id != null && !id.isEmpty() && !isGroup) {
                    ItemBean item = new ItemBean();
                    item.setConcept(BrowseRecords.getSession().getCurrentEntityModel().getConceptName());
                    item.setIds(id);
                    AppEvent appEvent = new AppEvent(BrowseRecordsEvents.ViewLineageItem, item);
                    Dispatcher.forwardEvent(appEvent);
                }
            }
        });
    }
}
