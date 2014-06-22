// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.creator;

import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.base.client.model.ItemBean;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;

public class CellRendererCreator {

    public static GridCellRenderer<ModelData> createRenderer(TypeModel dataType, final String xpath) {
        if (dataType.getType().equals(DataTypeConstants.URL)) {
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {

                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    String value = model.get(property);
                    if (value != null){
                        String[] url = value.split("@@");//$NON-NLS-1$
                        return "<a href='" + url[1] + "'>" + url[0] + "</a>";//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                    }
                    return "null";//$NON-NLS-1$
                }
            };
            return renderer;
        }
        if (dataType.getForeignkey() != null){
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {

                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    ItemBean itemBean = (ItemBean) model;
                    ForeignKeyBean fkBean = itemBean.getForeignkeyDesc((String)model.get(property));
                    return fkBean == null ? "" : fkBean.toString();//$NON-NLS-1$
                }
            };
            return renderer;
        }
        return null;
    }
}
