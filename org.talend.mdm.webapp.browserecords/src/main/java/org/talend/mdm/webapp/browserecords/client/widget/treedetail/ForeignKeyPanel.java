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
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.grid.RowNumberer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

/**
 * ForeignKey Panel : Display ForeignKey information
 */
public class ForeignKeyPanel extends ContentPanel {

    Grid<ItemNodeModel> grid;

    ToolBar toolBar = new ToolBar();

    Button createFkButton = new Button(MessagesFactory.getMessages().create_btn(), AbstractImagePrototype.create(Icons.INSTANCE
            .Create()));
    ListStore<ItemNodeModel> store = new ListStore<ItemNodeModel>();

    TypeModel fkTypeModel;
    List<ItemNodeModel> fkModels;

    public ForeignKeyPanel(List<ItemNodeModel> fkModels, final TypeModel fkTypeModel) {
        this.setHeaderVisible(false);
        this.setLayout(new FitLayout());
        this.setSize(800, 300);
        this.fkTypeModel = fkTypeModel;
        this.fkModels = fkModels;
        createFkButton.setText(fkTypeModel.getLabel(UrlUtil.getLanguage()));
        toolBar.add(createFkButton);
        this.setTopComponent(toolBar);

        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        RowNumberer columnNo = new RowNumberer();
        configs.add(columnNo);

        ColumnConfig columnInfo = new ColumnConfig();
        columnInfo.setWidth(400);
        columnInfo.setHeader(MessagesFactory.getMessages().fk_info());
        columnInfo.setRenderer(fkRender);
        configs.add(columnInfo);

        ColumnConfig columnOpt = new ColumnConfig();
        columnOpt.setFixed(true);
        columnOpt.setWidth(60);
        columnOpt.setRenderer(optRender);
        configs.add(columnOpt);

        ColumnModel cm = new ColumnModel(configs);
        grid = new Grid<ItemNodeModel>(store, cm);
        grid.addPlugin(columnNo);
        grid.getView().setForceFit(true);
        fillData();

        this.add(grid);
    }

    private void fillData() {
        store.removeAll();
        for (ItemNodeModel fkModel : fkModels) {
            store.add(fkModel);
        }
    }

    private void addFk(ItemNodeModel currentFkModel) {
        ItemNodeModel parent = (ItemNodeModel) currentFkModel.getParent();
        ItemNodeModel newFkModel = currentFkModel.clone(false);
        newFkModel.setParent(parent);
        int index = parent.indexOf(currentFkModel);
        parent.insert(newFkModel, index + 1);
        int row = store.indexOf(currentFkModel);
        store.insert(newFkModel, row + 1);
    }

    private void delFk(ItemNodeModel currentFkModel) {
        if (store.getCount() > 1) {
            store.remove(currentFkModel);
            TreeModel parent = currentFkModel.getParent();
            parent.remove(currentFkModel);
        }
    }

    GridCellRenderer<ItemNodeModel> fkRender = new GridCellRenderer<ItemNodeModel>() {

        public Object render(final ItemNodeModel model, String property, ColumnData config, int rowIndex, int colIndex,
                ListStore<ItemNodeModel> store, Grid<ItemNodeModel> grid) {
            final ForeignKeyCellField fkField = new ForeignKeyCellField(fkTypeModel.getForeignkey(), fkTypeModel
                    .getForeignKeyInfo());
            fkField.setValue((ForeignKeyBean) model.getObjectValue());
            fkField.addListener(Events.Change, new Listener<FieldEvent>() {

                public void handleEvent(FieldEvent fe) {
                    model.setObjectValue((Serializable) fe.getValue());
                    model.setChangeValue(true);
                    model.setValid(fkField.isValid());
                }
            });

            fkField.addListener(Events.Attach, new Listener<FieldEvent>() {

                public void handleEvent(FieldEvent fe) {
                    model.setValid(fkField.isValid());
                }
            });

            return fkField;
        }
    };
    
    GridCellRenderer<ItemNodeModel> optRender = new GridCellRenderer<ItemNodeModel>() {

        public Object render(final ItemNodeModel model, String property, ColumnData config, final int rowIndex, int colIndex,
                final ListStore<ItemNodeModel> store, Grid<ItemNodeModel> grid) {
            Image add = new Image(Icons.INSTANCE.add());
            add.getElement().getStyle().setCursor(Cursor.POINTER);
            add.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    addFk(model);
                }
            });

            Image del = new Image(Icons.INSTANCE.Delete());
            del.getElement().getStyle().setCursor(Cursor.POINTER);
            del.getElement().getStyle().setMarginLeft(3D, Unit.PX);
            del.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    delFk(model);
                }
            });

            com.google.gwt.user.client.ui.Grid optGrid = new com.google.gwt.user.client.ui.Grid(1, 2);
            optGrid.setCellPadding(0);
            optGrid.setCellSpacing(0);
            optGrid.setWidget(0, 0, add);
            optGrid.setWidget(0, 1, del);
            return optGrid;
        }
    };
}
