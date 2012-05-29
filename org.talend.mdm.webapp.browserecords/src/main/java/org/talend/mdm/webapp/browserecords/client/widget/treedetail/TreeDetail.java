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
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ColumnElement;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatTextField;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.BoxComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.extjs.gxt.ui.client.widget.layout.FlowLayout;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class TreeDetail extends ContentPanel {

    private ViewBean viewBean;

    private TreeEx tree = new TreeEx();

    private TreeItem root;

    private List<Tree> columnTrees = new ArrayList<Tree>();

    private Map<String, Field<?>> fieldMap = new HashMap<String, Field<?>>();

    private HashMap<CountMapItem, Integer> occurMap = new HashMap<CountMapItem, Integer>();

    private ForeignKeyRender fkRender = new ForeignKeyRenderImpl();

    private ItemDetailToolBar toolBar;

    private DynamicTreeItem selectedItem;

    private ItemsDetailPanel itemsDetailPanel;

    // In case of custom layout, which displays some elements and not others,
    // we store the DynamicTreeItem corresponding to the displayed elements in
    // this set.
    private HashSet<TreeItem> customLayoutDisplayedElements = new HashSet<TreeItem>();

    private ClickHandler handler = new ClickHandler() {

        public void onClick(ClickEvent arg0) {
            if (selectedItem == null)
                return;
            // final DynamicTreeItem selected = (DynamicTreeItem) tree.getSelectedItem();
            final DynamicTreeItem parentItem = (DynamicTreeItem) selectedItem.getParentItem();
            final ItemNodeModel selectedModel = selectedItem.getItemNodeModel();
            final ItemNodeModel parentModel = (ItemNodeModel) selectedModel.getParent();

            final String xpath = selectedModel.getBindingPath();
            final String typePath = selectedModel.getTypePath();
            final CountMapItem countMapItem = new CountMapItem(xpath, parentModel);
            final int count = occurMap.containsKey(countMapItem) ? occurMap.get(countMapItem) : 0;

            if ("Add".equals(arg0.getRelativeElement().getId()) || "Clone".equals(arg0.getRelativeElement().getId())) { //$NON-NLS-1$ //$NON-NLS-2$               
                if (viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath).getMaxOccurs() < 0
                        || count < viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath).getMaxOccurs()) {
                    // clone a new item
                    ItemNodeModel model = selectedModel.clone("Clone".equals(arg0.getRelativeElement().getId()) ? true : false); //$NON-NLS-1$
                    model.setDynamicLabel(LabelUtil.getNormalLabel(model.getLabel()));
                    model.setMandatory(selectedModel.isMandatory());
                    int selectModelIndex = parentModel.indexOf(selectedModel);
                    parentModel.insert(model, selectModelIndex + 1);
                    parentModel.setChangeValue(true);
                    // if it has default value
                    if (viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath).getDefaultValue() != null)
                        model.setObjectValue(viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath).getDefaultValue());
                    parentItem.insertItem(buildGWTTree(model, null, true, null), parentItem.getChildIndex(selectedItem) + 1);
                    occurMap.put(countMapItem, count + 1);
                } else
                    MessageBox.alert(MessagesFactory.getMessages().status(), MessagesFactory.getMessages()
                            .multiOccurrence_maximize(count), null);
            } else {
                MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages().delete_confirm(),
                        new Listener<MessageBoxEvent>() {

                            public void handleEvent(MessageBoxEvent be) {
                                if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {

                                    if (count > 1
                                            && count > viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath)
                                                    .getMinOccurs()) {
                                        TreeDetailGridFieldCreator.deleteField(selectedModel, fieldMap);
                                        parentItem.removeItem(selectedItem);
                                        parentModel.remove(selectedModel);
                                        parentModel.setChangeValue(true);
                                        occurMap.put(countMapItem, count - 1);
                                        if (parentModel.getChildCount() > 0) {
                                            ItemNodeModel child = (ItemNodeModel) parentModel.getChild(0);
                                            Field<?> field = fieldMap.get(child.getId().toString());
                                            if (field != null)
                                                TreeDetailGridFieldCreator.updateMandatory(field, child, fieldMap);
                                        }
                                    } else
                                        MessageBox.alert(MessagesFactory.getMessages().status(), MessagesFactory.getMessages()
                                                .multiOccurrence_minimize(count), null);
                                }
                            }
                        });
            }
        }
    };

    public TreeDetail(ItemsDetailPanel itemsDetailPanel) {
        this.setHeaderVisible(false);
        // this.setAutoWidth(true);
        this.setLayout(new FlowLayout());
        this.setScrollMode(Scroll.AUTO);
        this.setBorders(false);
        this.setBodyBorder(false);
        this.itemsDetailPanel = itemsDetailPanel;
        this.addListener(Events.Resize, new Listener<BoxComponentEvent>() {

            public void handleEvent(BoxComponentEvent be) {
                if (root == null)
                    return;
                TreeDetail td = (TreeDetail) be.getSource();
                int width = be.getWidth();
                if (td.getWidget(0) instanceof TreeEx && columnTrees.size() == 0) {
                    td.setFiledWidth(root, width, 400, 0);
                } else if (td.getWidget(0) instanceof HorizontalPanel && columnTrees.size() > 0) {
                    int columnWidth = width / columnTrees.size();
                    for(Tree columnTree : columnTrees)
                        td.setFiledWidth(columnTree.getItem(0), columnWidth, 300, 0);
                }
            }     
        });
    }

    public void initTree(ViewBean viewBean, ItemBean itemBean) {
        initTree(viewBean, itemBean, null);
    }

    public void initTree(final ViewBean viewBean, ItemBean itemBean, final String operation) {
        this.viewBean = viewBean;
        if (itemBean == null) {
            buildPanel(operation);
        } else {
            final BrowseRecordsServiceAsync itemService = getItemService();
            itemService.getItemNodeModel(itemBean, viewBean.getBindingEntityModel(), Locale.getLanguage(),
                    new SessionAwareAsyncCallback<ItemNodeModel>() {

                        public void onSuccess(ItemNodeModel node) {
                            renderTree(node, operation);
                            if (node.isHasVisiblueRule()) {
                                itemService.executeVisibleRule(viewBean, CommonUtil.toXML(node, TreeDetail.this.viewBean, true),
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

    private void buildPanel(final String operation) {
        getItemService().createDefaultItemNodeModel(viewBean, Locale.getLanguage(), new SessionAwareAsyncCallback<ItemNodeModel>() {
            public void onSuccess(ItemNodeModel result) {
                
                renderTree(result, operation);
                if (hasVisibleRule(viewBean.getBindingEntityModel().getMetaDataTypes().get(
                        viewBean.getBindingEntityModel().getConceptName()))) {
                            getItemService().executeVisibleRule(viewBean, CommonUtil.toXML(result, viewBean, true),
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

    private DynamicTreeItem buildGWTTree(final ItemNodeModel itemNode, DynamicTreeItem item, boolean withDefaultValue,String operation) {
        Map<TypeModel, List<ItemNodeModel>> foreighKeyMap = new LinkedHashMap<TypeModel, List<ItemNodeModel>>();
        Map<TypeModel, ItemNodeModel> foreignKeyParentMap = new LinkedHashMap<TypeModel, ItemNodeModel>();
        DynamicTreeItem reuslt = buildGWTTree(itemNode, item, withDefaultValue, operation, foreighKeyMap, foreignKeyParentMap);
        if (foreighKeyMap.size() > 0) {
            for (TypeModel model : foreighKeyMap.keySet()) {
                fkRender.RenderForeignKey(foreignKeyParentMap.get(model), foreighKeyMap.get(model), model, toolBar, viewBean, this, itemsDetailPanel);
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
            item.setWidget(TreeDetailUtil.createWidget(itemNode, viewBean, fieldMap, handler, operation, itemsDetailPanel));
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
                        int count = 0;
                        CountMapItem countMapItem = new CountMapItem(nodeBindingPath, itemNode);
                        if (occurMap.containsKey(countMapItem))
                            count = occurMap.get(countMapItem);
                        occurMap.put(countMapItem, count + 1);
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

        if (typeModel.isNotSeparateFk())
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

    private Tree displayGWTTree(ColumnTreeModel columnLayoutModel) {
        Tree tree = new TreeEx();
        
        DynamicTreeItem treeRootNode = new DynamicTreeItem();
        tree.addItem(treeRootNode);

        if (root != null && root.getChildCount() > 0) {
            for (ColumnElement ce : columnLayoutModel.getColumnElements()) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    TreeItem child = root.getChild(i);
                    ItemNodeModel node = (ItemNodeModel) child.getUserObject();
                    String xpath = node.getBindingPath();
                    String typePath = node.getTypePath();
                    if (("/" + xpath).equals(ce.getxPath())) { //$NON-NLS-1$
                        treeRootNode.addItem(child);

                        // Record the fact that we are displaying this element in the custom layout.
                        customLayoutDisplayedElements.add(child);

                        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath);
                        if (typeModel.getForeignkey() == null && (typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1)) {
                            i--;
                            continue;
                        } else
                            break;
                    }
                }
            }
        }
        if (treeRootNode.getElement().getFirstChildElement() != null)
            treeRootNode.getElement().getFirstChildElement().setClassName("rootNode"); //$NON-NLS-1$
        treeRootNode.setState(true);
        return tree;
    }

    private void renderTree(ItemNodeModel rootModel) {
        renderTree(rootModel, null);
    }

    private void renderTree(ItemNodeModel rootModel, String operation) {

        root = buildGWTTree(rootModel, null, false, operation);
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
            HorizontalPanel hp = new HorizontalPanel();
            int columnWidth = this.getWidth() / columnLayoutModel.getColumnTreeModels().size(); 
            columnTrees.clear();
            for (ColumnTreeModel ctm : columnLayoutModel.getColumnTreeModels()) {
                Tree columnTree = displayGWTTree(ctm);
                if(columnWidth > 500) 
                 	this.setFiledWidth(columnTree.getItem(0), columnWidth, 300, 0); 
                columnTrees.add(columnTree);
                hp.add(columnTree);
                addTreeListener(columnTree);
            }
            //            hp.setHeight("570px"); //$NON-NLS-1$
            // HorizontalPanel spacehp = new HorizontalPanel();
            //            spacehp.setHeight("10px"); //$NON-NLS-1$
            // add(spacehp);
            add(hp);

            // For those TreeItems that are not attached because of the custom layout
            // we need to set the valid flags of their ItemNodeModel's to true. This
            // is normally set by the attach listeners of the corresponding fields,
            // which are never called since these fields are not attached due to
            // custom layout. So we set the valid flags manually here. We set it to
            // true without checking since we assume whatever we got from the server
            // is valid.
            int childCount = root.getChildCount();
            for (int i = 0; i < childCount; ++i) {
                TreeItem child = root.getChild(i);
                if (!customLayoutDisplayedElements.contains(child)) {
                    if (child instanceof DynamicTreeItem) {
                        TreeDetail.setValidFlags((DynamicTreeItem) child);
                    }
                }
            }
        } else {
        	setFiledWidth(root, this.getWidth(), 400, 0);
            add(tree);
            addTreeListener(tree);
        }
        this.layout(true);
        String foreignKeyDeleteMessage = rootModel.get("foreignKeyDeleteMessage"); //$NON-NLS-1$
        if (foreignKeyDeleteMessage != null && foreignKeyDeleteMessage.trim().length() > 0)
            MessageBox.alert(MessagesFactory.getMessages().warning_title(), foreignKeyDeleteMessage, null).getDialog()
                    .setWidth(600);
    }

	private void setFiledWidth(TreeItem item, int width, int offset, int level) {
		for (int i = 0; i < item.getChildCount(); i++) {
			TreeItem subItem = item.getChild(i);
			if (subItem.getWidget() instanceof HorizontalPanel) {
				HorizontalPanel hp = (HorizontalPanel) subItem.getWidget();
				if(hp.getWidgetCount() > 1) {
					Widget field = hp.getWidget(1);
					int size = width - (offset + 19 * level);
					if (field instanceof FormatTextField)
						((FormatTextField)field).setWidth(size > 200 ? size : 200);
					else if (field instanceof SimpleComboBox)
                        ((SimpleComboBox) field).setWidth(size > 200 ? size : 200);
				}
			}
			if (item.getChildCount() > 0)
				setFiledWidth(subItem, width, offset, level + 1);
		}
	}
    /**
     * Recursively set the valid flags of the ItemNodeModel's corresponding to the dynamicTreeItem and all its children
     * dynamicTreeItem's to true. Used to set the valid flag for all those items excluded from the display because of a
     * custom layout. Because they are not displayed, their valid flags are not set by their attach handlers, which is
     * where the valid flag is normally set for fields that are displayed.
     */
    private static void setValidFlags(DynamicTreeItem dynamicTreeItem) {
        dynamicTreeItem.getItemNodeModel().setValid(true);
        int childCount = dynamicTreeItem.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            TreeItem child = dynamicTreeItem.getChild(i);
            if (child instanceof DynamicTreeItem) {
                TreeDetail.setValidFlags((DynamicTreeItem) child);
            }
        }
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
            if (CommonUtil.getRealXPath(rootItem.getItemNodeModel()).equals(visibleResult.getXpath())) {
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
        }

        private List<TreeItem> items = new ArrayList<TreeItem>();

        public void insertItem(DynamicTreeItem item, int beforeIndex) {
            int count = this.getChildCount();

            for (int i = 0; i < count; i++) {
                items.add(this.getChild(i));
            }

            items.add(beforeIndex, item);
            this.removeItems();

            for (int j = 0; j < items.size(); j++) {
                this.addItem(items.get(j));
            }
            items.clear();
        }

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

    private class CountMapItem {

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