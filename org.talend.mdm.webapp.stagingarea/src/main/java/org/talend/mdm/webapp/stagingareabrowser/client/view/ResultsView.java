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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.model.UserContextModel;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.base.client.widget.ColumnAlignGrid;
import org.talend.mdm.webapp.stagingareabrowser.client.controller.ResultsController;
import org.talend.mdm.webapp.stagingareabrowser.client.model.ResultItem;
import org.talend.mdm.webapp.stagingareabrowser.client.resources.Resources;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;

public class ResultsView extends AbstractView {

    private Grid<ResultItem> resultGrid;

    private PagingToolBar pagingBar;

    public static final int PAGE_SIZE = 20;

    private ColumnModel columnModel;

    UserContextModel ucx;
    
    private Map<Integer, String> errorTitles;

    private void initErrorTitles() {
        errorTitles = new HashMap<Integer, String>();
        errorTitles.put(000, messages.status_000());
        errorTitles.put(201, messages.status_201(ucx.getDataContainer()));
        errorTitles.put(202, messages.status_202(ucx.getDataContainer()));
        errorTitles.put(203, messages.status_203());
        errorTitles.put(401, messages.status_401(ucx.getDataContainer()));
        errorTitles.put(402, messages.status_402(ucx.getDataContainer()));
        errorTitles.put(403, messages.status_403());
        errorTitles.put(404, messages.status_404());
    }

    @Override
    protected void initComponents() {
        ucx = UserContextUtil.getUserContext();;
        initErrorTitles();
        initColumnModel();
        resultGrid = new ColumnAlignGrid<ResultItem>(ResultsController.getClearStore(), columnModel);
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
        ColumnConfig dateTimeColumn = new ColumnConfig("dateTime", messages.date_time(), 200); //$NON-NLS-1$
        if (ucx.getDateTimeFormat() != null && !"".equals(ucx.getDateTimeFormat())) { //$NON-NLS-1$
            dateTimeColumn.setDateTimeFormat(DateTimeFormat.getFormat(ucx.getDateTimeFormat()));
        }
        columns.add(dateTimeColumn);
        columns.add(new ColumnConfig("source", messages.source(), 100)); //$NON-NLS-1$
        columns.add(new ColumnConfig("group", messages.group(), 100)); //$NON-NLS-1$
        ColumnConfig statusColumn = new ColumnConfig("status", messages.status(), 100); //$NON-NLS-1$
        statusColumn.setRenderer(new GridCellRenderer<ResultItem>() {

            public Object render(ResultItem model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ResultItem> store, Grid<ResultItem> grid) {
                String color;
                com.google.gwt.user.client.ui.Grid g = new com.google.gwt.user.client.ui.Grid(1, 2);
                Image icon;
                if (model.getStatus() >= 400) {
                    color = "red"; //$NON-NLS-1$
                    icon = new Image(Resources.ICONS.failed());
                } else {
                    icon = new Image(Resources.ICONS.successful());
                    color = "green"; //$NON-NLS-1$
                }
                g.getElement().getStyle().setColor(color);
                g.setWidget(0, 0, icon);
                g.setText(0, 1, Integer.toString(model.getStatus()));
                g.setTitle(model.getStatus() + ": " + errorTitles.get(model.getStatus())); //$NON-NLS-1$
                return g;
            }
        });
        columns.add(statusColumn);
        columns.add(new ColumnConfig("error", messages.error(), 200)); //$NON-NLS-1$
        columnModel = new ColumnModel(columns);
    }

    @Override
    protected void registerEvent() {
        resultGrid.addListener(Events.RowDoubleClick, new Listener<GridEvent<ResultItem>>() {

            public void handleEvent(GridEvent<ResultItem> ge) {
                ResultItem item = ge.getGrid().getStore().getAt(ge.getRowIndex());
                StringBuffer title = new StringBuffer();
                title.append(item.getEntity() + " "); //$NON-NLS-1$
                title.append(item.getKey() + " "); //$NON-NLS-1$

                if (ucx.getDateTimeFormat() != null && !"".equals(ucx.getDateTimeFormat())) { //$NON-NLS-1$
                    title.append(DateTimeFormat.getFormat(ucx.getDateTimeFormat()).format(item.getDateTime()));
                }
                StringBuffer message = new StringBuffer();
                String color = "green"; //$NON-NLS-1$
                if (item.getStatus() >= 400) {
                    color = "red"; //$NON-NLS-1$
                }
                message.append("<div style='width: 400px; margin-left: 50px;'>"); //$NON-NLS-1$
                message.append("<b style='color: " + color + ";'>" + item.getStatus() + ": " + errorTitles.get(item.getStatus()) + "</b>"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
                message.append("<div style='margin-top: 5px; width: 380px; height: 50px; overflow: auto;'>"); //$NON-NLS-1$
                Element el = DOM.createDiv();
                el.setInnerText(item.getError());
                message.append(el.getInnerHTML());
                message.append("</div>"); //$NON-NLS-1$
                message.append("</div>"); //$NON-NLS-1$
                MessageBox.alert(title.toString(), message.toString(), null).getDialog().setWidth(500);
            }
        });
    }
}
