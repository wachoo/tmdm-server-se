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
package org.talend.mdm.webapp.stagingareabrowser.client.view;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.stagingareabrowser.client.controller.ResultsController;
import org.talend.mdm.webapp.stagingareabrowser.client.model.ResultItem;

import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;

public class ResultsView extends AbstractView {

    private Grid<ResultItem> resultGrid;

    private PagingToolBar pagingBar;

    public static final int PAGE_SIZE = 20;

    private ColumnModel columnModel;

    @Override
    protected void initComponents() {
        initColumnModel();
        resultGrid = new Grid<ResultItem>(ResultsController.getClearStore(), columnModel);
        pagingBar = new PagingToolBar(PAGE_SIZE);
        pagingBar.bind(ResultsController.getLoader());
    }

    @Override
    protected void initLayout() {
        mainPanel.setLayout(new FitLayout());
        mainPanel.add(resultGrid);
        mainPanel.setBottomComponent(pagingBar);
    }

    private void initColumnModel() {
        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        columns.add(new ColumnConfig("entity", messages.entity(), 100)); //$NON-NLS-1$
        columns.add(new ColumnConfig("key", messages.key(), 100)); //$NON-NLS-1$
        columns.add(new ColumnConfig("dateTime", messages.date_time(), 200)); //$NON-NLS-1$
        columns.add(new ColumnConfig("source", messages.source(), 100)); //$NON-NLS-1$
        columns.add(new ColumnConfig("group", messages.group(), 100)); //$NON-NLS-1$
        columns.add(new ColumnConfig("status", messages.status(), 100)); //$NON-NLS-1$
        columns.add(new ColumnConfig("error", messages.error(), 100)); //$NON-NLS-1$
        columnModel = new ColumnModel(columns);
    }
}
