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

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;


public class CellRendererCreator {

    public static GridCellRenderer<ModelData> createRenderer(TypeModel dataType){
        if (dataType.getTypeName().equals(DataTypeConstants.URL)){
            GridCellRenderer<ModelData> renderer = new GridCellRenderer<ModelData>() {

                public Object render(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ModelData> store, Grid<ModelData> grid) {
                    String value = model.get(property);
                    if (value != null){
                        String[] url = value.split("@@");
                        return "<a href='" + url[1] + "'>" + url[0] + "</a>";
                    }
                    return "null";
                }
            };
            return renderer;
        }
        return null;
    }
}
