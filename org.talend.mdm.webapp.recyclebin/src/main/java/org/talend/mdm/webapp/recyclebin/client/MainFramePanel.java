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
package org.talend.mdm.webapp.recyclebin.client;


import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.recyclebin.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.recyclebin.client.resources.icon.Icons;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class MainFramePanel extends ContentPanel {

    private static MainFramePanel instance;

    private MainFramePanel() {
        initTopBar();
    }

    private void initGrid() {
        List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();
        int subWidth = this.getWidth() / 9;
        ColumnConfig colPK = new ColumnConfig("itemPK", MessagesFactory.getMessages().dataClusterName(), subWidth);
        ccList.add(colPK);
        ColumnConfig colRevisionID = new ColumnConfig("revisionID", MessagesFactory.getMessages().revisionID(), subWidth);
        ccList.add(colRevisionID);
        ColumnConfig colConceptName = new ColumnConfig("conceptName", MessagesFactory.getMessages().conceptName(), subWidth);
        ccList.add(colConceptName);
        ColumnConfig colIds = new ColumnConfig("ids", MessagesFactory.getMessages().Ids(), subWidth);
        ccList.add(colIds);
        ColumnConfig colPartPath = new ColumnConfig("partPath", MessagesFactory.getMessages().partPath(), subWidth);
        ccList.add(colPartPath);
        ColumnConfig colUserName = new ColumnConfig("insertionUserName", MessagesFactory.getMessages().UserName(), subWidth);
        ccList.add(colUserName);
        ColumnConfig colDate = new ColumnConfig("insertionTime", MessagesFactory.getMessages().Date(), subWidth);
        ccList.add(colDate);
        ColumnConfig colDelete = new ColumnConfig("delete", MessagesFactory.getMessages().delete(), subWidth);
        colDelete.setRenderer(new GridCellRenderer<BaseModelData>() {

            public Object render(BaseModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<BaseModelData> store, Grid<BaseModelData> grid) {
                Image image = new Image();
                image.setResource(Icons.INSTANCE.delete());
                image.addClickListener(new ClickListener() {

                    public void onClick(Widget arg0) {
                        MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages()
                                .delete_confirm(), new Listener<MessageBoxEvent>() {

                            public void handleEvent(MessageBoxEvent be) {
                                if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                                    // service.deleteDocument(tableName, model, new AsyncCallback<Void>() {
                                    //
                                    // public void onFailure(Throwable caught) {
                                    // Dispatcher.forwardEvent(CrossreferenceEvents.Error, caught);
                                    // }
                                    //
                                    // public void onSuccess(Void arg0) {
                                    // pagetoolBar.refresh();
                                    // store.remove(model);
                                    // }
                                    //
                                    // });
                                }
                            }

                        });

                    }

                });
                return image;
            }
            
        });
        ccList.add(colDelete);

        ColumnModel cm = new ColumnModel(ccList);
        // Grid grid = new Grid<BaseModelData>(store, cm);
    }

    private void initTopBar(){
        ToolBar bar = new ToolBar();
        TextField text = new TextField();
        text.setId("trash-criteria");
        text.setEmptyText("*");
        //TODO add special key event
        bar.add(text);
        Button btn = new Button("Search");
        bar.add(btn);
        this.setTopComponent(bar);
    }

    public static MainFramePanel getInstance() {
        if (instance == null)
            instance = new MainFramePanel();
        return instance;
    }
}
