// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
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
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.MultiOccurrenceManager;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.dom.client.Style.Display;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeDetail extends ContentPanel {

    private ViewBean viewBean;

    private TreeEx tree = new TreeEx();

    private TreeItem root;

    private List<Tree> columnTrees = new ArrayList<Tree>();

    private Map<String, Field<?>> fieldMap = new HashMap<String, Field<?>>();

    private ForeignKeyRender fkRender = new ForeignKeyRenderImpl();

    private ItemDetailToolBar toolBar;

    private DynamicTreeItem selectedItem;

    MultiOccurrenceManager multiManager;

    public DynamicTreeItem getSelectedItem() {
        return selectedItem;
    }

    public MultiOccurrenceManager getMultiManager() {
        return multiManager;
    }

    private ItemsDetailPanel itemsDetailPanel;

    public TreeDetail(ItemsDetailPanel itemsDetailPanel) {
        this.setHeaderVisible(false);
        // this.setAutoWidth(true);
        this.setLayout(new FlowLayout());
        this.setScrollMode(Scroll.AUTO);
        this.setBorders(false);
        this.setBodyBorder(false);
        this.itemsDetailPanel = itemsDetailPanel;
    }

    public void initTree(ViewBean viewBean, ItemBean itemBean) {
        initTree(viewBean, itemBean, null, null);
    }

    public void initTree(ViewBean viewBean, ItemBean itemBean, Map<String, String> initDataMap, final String operation) {
        this.viewBean = viewBean;
        this.multiManager = new MultiOccurrenceManager(viewBean.getBindingEntityModel().getMetaDataTypes(), this);
        if (itemBean == null) {
            buildPanel(operation, initDataMap);
        } else {
            final BrowseRecordsServiceAsync itemService = getItemService();
            itemService.getItemNodeModel(itemBean, viewBean.getBindingEntityModel(), Locale.getLanguage(),
                    new SessionAwareAsyncCallback<ItemNodeModel>() {

                        public void onSuccess(ItemNodeModel node) {
                            renderTree(node, operation);
                            if (node.isHasVisiblueRule()) {
                                itemService.executeVisibleRule(CommonUtil.toXML(node, TreeDetail.this.viewBean),
                                        new SessionAwareAsyncCallback<List<VisibleRuleResult>>() {

                                            public void onSuccess(List<VisibleRuleResult> arg0) {
                                                for (VisibleRuleResult visibleRuleResult : arg0) {
                                                    if (columnTrees.size() > 0){
                                                        for (Tree columnTree : columnTrees) {
                                                            recrusiveSetItems(visibleRuleResult, (DynamicTreeItem) columnTree.getItem(0));
                                                        }
                                                    } else {
                                                        recrusiveSetItems(visibleRuleResult, (DynamicTreeItem) root);                                                        
                                                    }
                                                }
                                            }
                                        });
                            }
                        }

                    });
        }
    }

    private void buildPanel(final String operation, Map<String, String> initDataMap) {
        getItemService().createDefaultItemNodeModel(viewBean, initDataMap, Locale.getLanguage(),
                new SessionAwareAsyncCallback<ItemNodeModel>() {
            public void onSuccess(ItemNodeModel result) {
                
                renderTree(result, operation);
                if (hasVisibleRule(viewBean.getBindingEntityModel().getMetaDataTypes().get(
                        viewBean.getBindingEntityModel().getConceptName()))) {
                            getItemService().executeVisibleRule(CommonUtil.toXML(result, viewBean),
                            new SessionAwareAsyncCallback<List<VisibleRuleResult>>() {

                                public void onSuccess(List<VisibleRuleResult> arg0) {
                                    for (VisibleRuleResult visibleRuleResult : arg0) {
                                        if (columnTrees.size() > 0){
                                            for (Tree columnTree : columnTrees){
                                                recrusiveSetItems(visibleRuleResult, (DynamicTreeItem) columnTree.getItem(0));
                                            }
                                        } else {
                                            recrusiveSetItems(visibleRuleResult, (DynamicTreeItem) root);
                                        }
                                    }
                                }
                            });
                }
            }
                });
    }

    public DynamicTreeItem buildGWTTree(final ItemNodeModel itemNode, DynamicTreeItem item, boolean withDefaultValue,
            String operation) {
        Map<TypeModel, List<ItemNodeModel>> foreighKeyMap = new LinkedHashMap<TypeModel, List<ItemNodeModel>>();
        Map<TypeModel, ItemNodeModel> foreignKeyParentMap = new LinkedHashMap<TypeModel, ItemNodeModel>();
        DynamicTreeItem reuslt = buildGWTTree(itemNode, item, withDefaultValue, operation, foreighKeyMap, foreignKeyParentMap);
        if (foreighKeyMap.size() > 0) {
            for (TypeModel model : foreighKeyMap.keySet()) {
                fkRender.RenderForeignKey(foreignKeyParentMap.get(model), foreighKeyMap.get(model), model, toolBar, viewBean,
                        this, itemsDetailPanel);
            }
        }
        return reuslt;
    }

    private boolean isFirstKey = true;

    private DynamicTreeItem buildGWTTree(final ItemNodeModel itemNode, DynamicTreeItem item, boolean withDefaultValue,
            String operation, Map<TypeModel, List<ItemNodeModel>> foreighKeyMap, Map<TypeModel, ItemNodeModel> foreignKeyParentMap) {
        if (item == null) {
            item = new DynamicTreeItem();
            item.setItemNodeModel(itemNode);
            String itemRealType = itemNode.getRealType();
            if (itemRealType != null && itemRealType.trim().length() > 0) {
                item.setState(true);
            }
            if (ItemDetailToolBar.DUPLICATE_OPERATION.equals(operation)) {
                if (itemNode.isKey()) {
                    if (isFirstKey) {
                        itemNode.setObjectValue(null);
                        isFirstKey = false;
                    }
                }
            }
            
            MultiOccurrenceChangeItem itemWidget = TreeDetailUtil.createWidget(itemNode, viewBean, fieldMap, null, operation, itemsDetailPanel);
            itemWidget.setTreeDetail(this);
            item.setWidget(itemWidget);
        }

        List<ModelData> itemNodeChildren = itemNode.getChildren();

        if (itemNodeChildren != null && itemNodeChildren.size() > 0) {
            Map<String, TypeModel> metaDataTypes = viewBean.getBindingEntityModel().getMetaDataTypes();
            for (ModelData model : itemNodeChildren) {
                ItemNodeModel node = (ItemNodeModel) model;
                Serializable nodeObjectValue = node.getObjectValue();
                String nodeBindingPath = node.getBindingPath();
                String typePath = node.getTypePath();

                TypeModel typeModel = metaDataTypes.get(typePath);
                String typeModelDefaultValue = typeModel.getDefaultValue();
                if (withDefaultValue && typeModelDefaultValue != null && (nodeObjectValue == null || nodeObjectValue.equals(""))) //$NON-NLS-1$
                    node.setObjectValue(typeModelDefaultValue);

                boolean isFKDisplayedIntoTab = isFKDisplayedIntoTab(node, typeModel, metaDataTypes);

                if (isFKDisplayedIntoTab) {
                    if (!foreighKeyMap.containsKey(typeModel)) {
                        foreighKeyMap.put(typeModel, new ArrayList<ItemNodeModel>());
                        foreignKeyParentMap.put(typeModel, itemNode);
                    }
                    foreighKeyMap.get(typeModel).add(node);
                } else {
                    TreeItem childItem = buildGWTTree(node, null, withDefaultValue, operation, foreighKeyMap, foreignKeyParentMap);
                    if (childItem != null) { //
                        item.addItem(childItem);

                        if (typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1) {
                            multiManager.addMultiOccurrenceNode((DynamicTreeItem) childItem);
                        }
                    }
                }
            }

            if (itemNodeChildren.size() > 0 && item.getChildCount() == 0)
                return null; // All went to FK Tab, in that case return null so the tree item is not added

            item.getElement().getStyle().setPaddingLeft(3.0, Unit.PX);
        }

        item.setUserObject(itemNode);
        item.setState(viewBean.getBindingEntityModel().getMetaDataTypes().get(itemNode.getTypePath()).isAutoExpand());

        return item;
    }

    static boolean isFKDisplayedIntoTab(ItemNodeModel node, TypeModel typeModel, Map<String, TypeModel> metaDataTypes) {
        String typeModelFK = typeModel.getForeignkey();
        if (typeModelFK == null)
            return false; // Not a FK

        if (!typeModel.isSeparateFk())
            return false;

        ItemNodeModel parentNode = (ItemNodeModel) node.getParent();
        if (parentNode == null) {
            return false; // It is root
        }
        if (parentNode.getParent() == null) {
            return true;
        }
        TypeModel parentType = metaDataTypes.get(parentNode.getTypePath());
        if (parentType instanceof ComplexTypeModel){
            List<TypeModel> subTypes = ((ComplexTypeModel) parentType).getSubTypes();
            return subTypes != null && subTypes.size() == 1;
        }
        return false;
    }

    public void onUpdatePolymorphism(ComplexTypeModel typeModel) {
        // DynamicTreeItem item = (DynamicTreeItem) tree.getSelectedItem();
        if (selectedItem == null) {
            return;
        }
        selectedItem.setState(true);
        final ItemNodeModel treeNode = selectedItem.getItemNodeModel();

        List<ItemNodeModel> fkContainers = ForeignKeyUtil.getAllForeignKeyModelParent(viewBean, treeNode);
        for (ItemNodeModel fkContainer : fkContainers) {
            fkRender.removeRelationFkPanel(fkContainer);
        }

        treeNode.setRealType(typeModel.getName());
        selectedItem.removeItems();

        String contextPath = CommonUtil.getRealXPath(treeNode);
        String typePath = CommonUtil.getRealTypePath(treeNode);
        typePath = typePath.replaceAll(":" + treeNode.getRealType() + "$", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        ItemNodeModel rootNode = (ItemNodeModel) root.getUserObject();
        treeNode.removeAll();
        String xml = CommonUtil.toXML(rootNode, viewBean);

        getItemService().createSubItemNodeModel(viewBean, xml, typePath, contextPath, treeNode.getRealType(), UrlUtil.getLanguage(),
                new SessionAwareAsyncCallback<ItemNodeModel>() {


            public void onSuccess(ItemNodeModel result) {
                ModelData[] children = result.getChildren().toArray(new ModelData[0]);
                for (ModelData child : children) {
                    treeNode.add(child);
                }
                buildGWTTree(treeNode, selectedItem, false, null);
            }
        });
        
    }

    public void onExecuteVisibleRule(List<VisibleRuleResult> visibleResults) {
        DynamicTreeItem rootItem = (DynamicTreeItem) tree.getItem(0);
        for (VisibleRuleResult visibleResult : visibleResults) {
            if (columnTrees.size() > 0) {
                for (Tree columnTree : columnTrees) {
                    recrusiveSetItems(visibleResult, (DynamicTreeItem) columnTree.getItem(0));
                }
            } else {
                recrusiveSetItems(visibleResult, rootItem);
            }
        }
    }

    private void renderTree(ItemNodeModel rootModel) {
        renderTree(rootModel, null);
    }

    private void renderTree(ItemNodeModel rootModel, String operation) {

        root = buildGWTTree(rootModel, null, false, operation);
        multiManager.warningAllItems();
        multiManager.handleOptIcons();
        isFirstKey = true;
        root.setState(true);
        tree = new TreeEx();
        tree.getElement().setId("TreeDetail-tree"); //$NON-NLS-1$
        if (root.getElement().getFirstChildElement() != null)
            root.getElement().getFirstChildElement().setClassName("rootNode"); //$NON-NLS-1$
        tree.addItem(root);
        root.getElement().setId("TreeDetail-root"); //$NON-NLS-1$
        
        ColumnTreeLayoutModel columnLayoutModel = viewBean.getColumnLayoutModel();
        if (columnLayoutModel != null) {// TODO if create a new PrimaryKey, tree UI should not render according to the
                                        // layout template
            FlexTable htable = new FlexTable();
            htable.setHeight("100%"); //$NON-NLS-1$
            columnTrees.clear();
            int columnNum = 0;
            for (ColumnTreeModel ctm : columnLayoutModel.getColumnTreeModels()) {
                // Tree columnTree = displayGWTTree(ctm);
                Tree columnTree = ViewUtil.transformToCustomLayout(root, ctm, viewBean);
                columnTrees.add(columnTree);
                htable.setWidget(0, columnNum, columnTree);
                ViewUtil.setStyleAttribute(htable.getCellFormatter().getElement(0, columnNum), ctm.getStyle());
                htable.getCellFormatter().getElement(0, columnNum).setAttribute("vAlign", "top"); //$NON-NLS-1$//$NON-NLS-2$
                columnNum++;
                addTreeListener(columnTree);
            }
            //            hp.setHeight("570px"); //$NON-NLS-1$
            // HorizontalPanel spacehp = new HorizontalPanel();
            //            spacehp.setHeight("10px"); //$NON-NLS-1$
            // add(spacehp);
            add(htable);

        } else {
            add(tree);
            addTreeListener(tree);
        }
        this.layout(true);
        String foreignKeyDeleteMessage = rootModel.get("foreignKeyDeleteMessage"); //$NON-NLS-1$
        if (foreignKeyDeleteMessage != null && foreignKeyDeleteMessage.trim().length() > 0)
            MessageBox.alert(MessagesFactory.getMessages().warning_title(), foreignKeyDeleteMessage, null).getDialog()
                    .setWidth(600);
    }

    // get selected item in tree
    private void addTreeListener(Tree tree) {
        tree.addSelectionHandler(new SelectionHandler<TreeItem>() {

            public void onSelection(SelectionEvent<TreeItem> event) {
                selectedItem = (DynamicTreeItem) event.getSelectedItem();
            }
        });
    }

    private void recrusiveSetItems(VisibleRuleResult visibleResult, DynamicTreeItem rootItem) {
        if (rootItem.getItemNodeModel() != null) {
            if (rootItem.getItemNodeModel().getBindingPath().equals(visibleResult.getXpath())) {
                rootItem.setVisible(visibleResult.isVisible());
            }
        }

        if (rootItem.getChildCount() == 0) {
            return;
        }

        for (int i = 0; i < rootItem.getChildCount(); i++) {
            if (rootItem.getChild(i) instanceof DynamicTreeItem) {
                DynamicTreeItem item = (DynamicTreeItem) rootItem.getChild(i);
                recrusiveSetItems(visibleResult, item);
            }
        }
    }

    public static class DynamicTreeItem extends TreeItem {

        private ItemNodeModel itemNode;

        public DynamicTreeItem() {
            super();
            if (GXT.isIE) {
                getContentElement().getStyle().setDisplay(Display.BLOCK);
            }
            this.getElement().getStyle().setPaddingTop(0D, Unit.PX);
            this.getElement().getStyle().setPaddingBottom(0D, Unit.PX);
        }

        private native Element getContentElement()/*-{
            return this.@com.google.gwt.user.client.ui.TreeItem::contentElem;
        }-*/;
        public void insertItem(DynamicTreeItem item, int beforeIndex) {
            // Detach item from existing parent.
            if ((item.getParentItem() != null) || (item.getTree() != null)) {
                item.remove();
            }

            if (_getChildren() == null) {
                _initChildren();
            }

            // Logical attach.
            item._setParentItem(this);

            _getChildren().add(beforeIndex, item);

            DOM.appendChild(_getChildSpanElem(), item.getElement());
            Element beforeEl = DOM.getChild(_getChildSpanElem(), beforeIndex);
            DOM.insertBefore(_getChildSpanElem(), item.getElement(), beforeEl);
            // Adopt.
            item._setTree(this.getTree());

            if (_getChildren().size() == 1) {
                _updateState(false, false);

            }
        }

        private native ArrayList<TreeItem> _getChildren()/*-{
            return this.@com.google.gwt.user.client.ui.TreeItem::children;
        }-*/;

        private native void _initChildren() /*-{
            this.@com.google.gwt.user.client.ui.TreeItem::initChildren()
        }-*/;

        private native void _setParentItem(TreeItem parent)/*-{
            this.@com.google.gwt.user.client.ui.TreeItem::setParentItem(Lcom/google/gwt/user/client/ui/TreeItem;)(parent);
        }-*/;

        private native void _updateState(boolean animate, boolean updateTreeSelection)/*-{
            this.@com.google.gwt.user.client.ui.TreeItem::updateState(ZZ)(animate, updateTreeSelection);
        }-*/;

        private native void _setTree(Tree tree)/*-{
            this.@com.google.gwt.user.client.ui.TreeItem::setTree(Lcom/google/gwt/user/client/ui/Tree;)(tree);
        }-*/;

        private native Element _getChildSpanElem()/*-{
            return this.@com.google.gwt.user.client.ui.TreeItem::childSpanElem;
        }-*/;

        public void removeItem(DynamicTreeItem item) {
            super.removeItem(item);
        }

        public void setItemNodeModel(ItemNodeModel treeNode) {
            itemNode = treeNode;
        }

        public ItemNodeModel getItemNodeModel() {
            return itemNode;
        }
    }

    private static BrowseRecordsServiceAsync getItemService() {

        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        return service;

    }

    public Tree getTree() {
        return tree;
    }

    public void refreshTree(final ItemBean item) {
        item.set("isRefresh", true); //$NON-NLS-1$
        getItemService().getItemNodeModel(item, viewBean.getBindingEntityModel(), Locale.getLanguage(),
                new SessionAwareAsyncCallback<ItemNodeModel>() {

                    public void onSuccess(ItemNodeModel node) {
                        TreeDetail.this.removeAll();
                        item.setLastUpdateTime(node);
                        itemsDetailPanel.clearChildrenContent();
                        renderTree(node);
                    }

                    @Override
                    protected void doOnFailure(Throwable caught) {
                        MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                                .refresh_error(), null);
                    }
                });
    }

    public class CountMapItem {

        private String xpath;

        private ItemNodeModel parentModel;

        public CountMapItem(String xpath, ItemNodeModel parentModel) {
            this.xpath = xpath;
            this.parentModel = parentModel;
        }

        public String getXpath() {
            return this.xpath;
        }

        public ItemNodeModel getParentModel() {
            return this.parentModel;
        }

        @Override
        public int hashCode() {
            return xpath.length();
        }

        @Override
        public boolean equals(Object o) {
            CountMapItem item = (CountMapItem) o;
            return item.getXpath().equals(xpath) && item.getParentModel().equals(parentModel);
        }
    }

    private boolean hasVisibleRule(TypeModel typeModel) {
        if (typeModel.isHasVisibleRule()) {
            return true;
        }

        if (!typeModel.isSimpleType()) {
            ComplexTypeModel complexModel = (ComplexTypeModel) typeModel;
            List<TypeModel> children = complexModel.getSubTypes();

            for (TypeModel model : children) {
                boolean childVisibleRule = hasVisibleRule(model);

                if (childVisibleRule) {
                    return true;
                }
            }
        }

        return false;
    }

    public void setToolBar(ItemDetailToolBar toolBar) {
        this.toolBar = toolBar;
    }

    public ItemDetailToolBar getToolBar() {
        return toolBar;
    }

    public Map<String, Field<?>> getFieldMap() {
        return fieldMap;
    }

    public void setRoot(TreeItem root) {
        this.root = root;
        if (root != null && root.getElement() != null) {
            root.getElement().setId("TreeDetail-root"); //$NON-NLS-1$
        }
    }

    public ItemNodeModel getRootModel() {
        return (ItemNodeModel) root.getUserObject();
    }

    public void setViewBean(ViewBean viewBean) {
        this.viewBean = viewBean;
    }

    public ItemsDetailPanel getItemsDetailPanel() {
        return itemsDetailPanel;
    }

}