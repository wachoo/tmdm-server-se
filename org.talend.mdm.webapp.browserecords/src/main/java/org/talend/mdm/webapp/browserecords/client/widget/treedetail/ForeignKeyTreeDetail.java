package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.Arrays;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.user.client.ui.Widget;

public class ForeignKeyTreeDetail extends ContentPanel {

    ItemNodeModel model = null;

    Grid<ModelData> fkTable = null;

    String pk, fk, fkViewName;

    ViewBean viewBean;

    public ForeignKeyTreeDetail(String pk, String fk, String fkViewName) {
        this.pk = pk;
        this.fk = fk;
        this.fkViewName = fkViewName;
    }

    public ForeignKeyTreeDetail() {

    }
    public ForeignKeyTreeDetail(ViewBean viewBean) {
        this.viewBean = viewBean;
    }

    public void buildPanel(final ViewBean viewBean) {
        List<ItemNodeModel> models = CommonUtil.getDefaultTreeModel(viewBean.getBindingEntityModel().getMetaDataTypes()
                .get(viewBean.getBindingEntityModel().getConceptName()));
        TreeStore<ModelData> store = new TreeStore<ModelData>();
        store.add(models.get(0).getChildren(), true);

        ColumnConfig name = new ColumnConfig("name", "Name", 1000); //$NON-NLS-1$ //$NON-NLS-2$

        name.setRenderer(new WidgetTreeGridCellRenderer<ItemNodeModel>() {

            @Override
            public Widget getWidget(ItemNodeModel model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ItemNodeModel> store, Grid<ItemNodeModel> grid) {
                return TreeDetailUtil.createWidget(model, property, viewBean);
            }

        });

        ColumnModel cm = new ColumnModel(Arrays.asList(name));
        cm.setColumnWidth(0, 1000);
        ContentPanel cp = new ContentPanel();
        cp.setHeaderVisible(false);
        cp.setLayout(new FitLayout());
        cp.setBodyBorder(false);
        cp.setButtonAlign(HorizontalAlignment.CENTER);
        cp.setFrame(true);

        TreeGrid<ModelData> tree = new TreeGrid<ModelData>(store, cm);
        tree.setBorders(true);
        tree.setAutoExpandColumn("name"); //$NON-NLS-1$
        tree.setView(new TreeDetailGridView());
        tree.getTreeView().setRowHeight(26);
        tree.setHideHeaders(false);
        tree.setAutoWidth(true);
        tree.setSize(800, 600);
        cp.add(tree);

        add(cp);
        this.layout(true);
    }

    public ViewBean getViewBean() {
        return viewBean;
    }

    public void setViewBean(ViewBean viewBean) {
        this.viewBean = viewBean;
        buildPanel(viewBean);
    }

}