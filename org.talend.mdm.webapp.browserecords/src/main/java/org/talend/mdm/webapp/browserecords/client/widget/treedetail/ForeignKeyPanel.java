package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;

public class ForeignKeyPanel extends ContentPanel {

    Grid<ModelData> grid;

    ListStore<ModelData> store = new ListStore<ModelData>();

    List<ItemNodeModel> fkModels;

    public ForeignKeyPanel(List<ItemNodeModel> fkModels) {
        this.fkModels = fkModels;
        List<ColumnConfig> configs = new ArrayList<ColumnConfig>();

        ColumnConfig columnId = new ColumnConfig();
        columnId.setId("id"); //$NON-NLS-1$
        configs.add(columnId);

        ColumnConfig columnInfo = new ColumnConfig();
        columnInfo.setId("displayInfo"); //$NON-NLS-1$
        configs.add(columnInfo);

        ColumnModel cm = new ColumnModel(configs);
        grid = new Grid<ModelData>(store, cm);
        fillData();
        this.add(grid);
    }

    private void fillData() {
        store.removeAll();
        for (ItemNodeModel fkModel : fkModels) {
            ForeignKeyBean fkBean = (ForeignKeyBean) fkModel.getObjectValue();
            store.add(fkBean);
        }
    }
}
