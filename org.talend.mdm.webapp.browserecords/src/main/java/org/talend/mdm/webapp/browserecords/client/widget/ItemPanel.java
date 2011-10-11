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
package org.talend.mdm.webapp.browserecords.client.widget;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Frame;

public class ItemPanel extends ContentPanel {

    private final TreeDetail tree = new TreeDetail();

    private ContentPanel smartPanel = new ContentPanel() {

        @Override
        public void onAttach() {
            Window.enableScrolling(true);
            setSize(Window.getClientWidth(), Window.getClientHeight());
            super.onAttach();
        }
    };

    private ItemDetailToolBar toolBar;

    private ViewBean viewBean;

    private ItemBean item;

    private String operation;

    public ItemPanel() {

    }

    public void onAttach() {
        Window.enableScrolling(true);
        super.onAttach();
    }

    public ItemPanel(ViewBean viewBean, ItemBean item, String operation) {
        this.viewBean = viewBean;
        this.item = item;
        this.toolBar = new ItemDetailToolBar(item, operation);
        this.operation = operation;
        this.initUI();
    }

    private void initUI() {
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setTopComponent(toolBar);
        this.setLayout(new FitLayout());
        if (ItemDetailToolBar.CREATE_OPERATION.equals(operation)) {
            tree.initTree(viewBean, null);
        } else if (ItemDetailToolBar.VIEW_OPERATION.equals(operation)) {
            tree.initTree(viewBean, item);
        } else if (ItemDetailToolBar.DUPLICATE_OPERATION.equals(operation)) {
            tree.initTree(viewBean, item);
        } else if (ItemDetailToolBar.PERSONALEVIEW_OPERATION.equals(operation)
                || ItemDetailToolBar.SMARTVIEW_OPERATION.equals(operation)) {
            tree.initTree(viewBean, item);
        } else {
            tree.initTree(viewBean, null);
        }

        tree.expand();
        this.add(tree);
        // smartPanel.setLayout(new FitLayout());
        smartPanel.setVisible(false);
        smartPanel.setHeaderVisible(false);
        this.add(smartPanel);
        // this.setBottomComponent(new PagingToolBarEx(50));
    }

    public void onUpdatePolymorphism(ComplexTypeModel typeModel) {
        tree.onUpdatePolymorphism(typeModel);
    }

    public void onExecuteVisibleRule(List<VisibleRuleResult> visibleResults) {
        tree.onExecuteVisibleRule(visibleResults);
    }

    public ItemBean getItem() {
        return item;
    }

    public TreeDetail getTree() {
        return tree;
    }

    public String getOperation() {
        return operation;
    }

    public void refreshTree() {
        tree.refreshTree(item);
        if (smartPanel.getWidget(0) != null && smartPanel.getWidget(0) instanceof Frame) {
            String url = ((Frame) smartPanel.getWidget(0)).getUrl();
            ((Frame) smartPanel.getWidget(0)).setUrl(url + "&" + Math.random()); //$NON-NLS-1$
            smartPanel.layout(true);
        }
    }

    public ContentPanel getSmartPanel() {
        return smartPanel;
    }

}
