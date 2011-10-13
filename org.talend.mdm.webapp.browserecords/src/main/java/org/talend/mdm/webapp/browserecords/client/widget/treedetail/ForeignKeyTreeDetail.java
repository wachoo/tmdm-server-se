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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ColumnElement;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeModel;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ChangeEvent;
import com.extjs.gxt.ui.client.data.ChangeEventSource;
import com.extjs.gxt.ui.client.data.ChangeListener;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class ForeignKeyTreeDetail extends ContentPanel {

    private ViewBean viewBean;

    private ItemDetailToolBar toolBar;

    private ItemNodeModel model;

    private boolean isCreate;

    private ForeignKeyModel fkModel;

    private ColumnTreeLayoutModel columnLayoutModel;

    private Tree tree;

    private TreeItem root;

    private ForeignKeyRender fkRender;

    private Map<String, Field<?>> fieldMap = new HashMap<String, Field<?>>();

    private ClickHandler handler = new ClickHandler() {

        public void onClick(ClickEvent arg0) {
            DynamicTreeItem selected = (DynamicTreeItem) tree.getSelectedItem();
            DynamicTreeItem parentItem = (DynamicTreeItem) selected.getParentItem();

            if ("Add".equals(arg0.getRelativeElement().getId())) { //$NON-NLS-1$
                // clone a new item
                DynamicTreeItem clonedItem = new DynamicTreeItem();
                HTML label = new HTML();
                label.setHTML(((TreeItemWidget) selected.getWidget()).getLabel().getHTML());

                Field<?> field = new Field<Object>() {
                };
                field.setElement(DOM.clone(((TreeItemWidget) selected.getWidget()).getField().getElement(), true));
                ((TreeItemWidget) clonedItem.getWidget()).setLabel(label);
                ((TreeItemWidget) clonedItem.getWidget()).setField(field);
                ((TreeItemWidget) clonedItem.getWidget()).setHandler(handler);
                ((TreeItemWidget) clonedItem.getWidget()).setSimpleType(true);
                ((TreeItemWidget) clonedItem.getWidget()).paint();
                parentItem.insertItem(clonedItem, parentItem.getChildIndex(selected));
            } else {
                parentItem.removeTreeItem(selected);
            }
        }
    };

    public ForeignKeyTreeDetail() {
        this.setHeaderVisible(false);
        this.setHeight(Window.getClientHeight() - (60 + 4 * 20));
        this.setScrollMode(Scroll.AUTO);
        this.setFkRender(new ForeignKeyRenderImpl());
        // display ForeignKey detail information,the tabPanel need to be clear. including create link refresh.
        ItemsDetailPanel.getInstance().clearContent();
    }

    public ForeignKeyTreeDetail(ViewBean viewBean, boolean isCreate) {
        this();
        this.isCreate = isCreate;
        this.viewBean = viewBean;
        this.columnLayoutModel = viewBean.getColumnLayoutModel();
        this.toolBar = new ItemDetailToolBar(new ItemBean(viewBean.getBindingEntityModel().getConceptName(), "", ""), //$NON-NLS-1$//$NON-NLS-2$
                isCreate ? ItemDetailToolBar.CREATE_OPERATION : ItemDetailToolBar.VIEW_OPERATION, true);
        this.setTopComponent(toolBar);
        buildPanel(viewBean);
    }

    public ForeignKeyTreeDetail(ForeignKeyModel fkModel, boolean isCreate) {
        this();
        this.isCreate = isCreate;
        this.fkModel = fkModel;
        this.model = fkModel.getNodeModel();
        this.viewBean = fkModel.getViewBean();
        this.columnLayoutModel = viewBean.getColumnLayoutModel();
        this.toolBar = new ItemDetailToolBar(fkModel.getItemBean(), isCreate ? ItemDetailToolBar.CREATE_OPERATION
                : ItemDetailToolBar.VIEW_OPERATION, true);
        this.setTopComponent(toolBar);
        ItemsDetailPanel.getInstance().clearContent();
        ItemsDetailPanel.getInstance().initBanner(fkModel.getItemBean().getPkInfoList(), fkModel.getItemBean().getDescription());
        // Update breadcrumb
        ItemsDetailPanel.getInstance().appendBreadCrumb(fkModel.getItemBean().getConcept(), fkModel.getItemBean().getIds());
        buildPanel(viewBean);
    }

    public void buildPanel(final ViewBean viewBean) {
        ItemNodeModel rootModel;
        if (this.model == null && this.isCreate) {
            List<ItemNodeModel> models = CommonUtil.getDefaultTreeModel(
                    viewBean.getBindingEntityModel().getMetaDataTypes().get(viewBean.getBindingEntityModel().getConceptName()),
                    Locale.getLanguage());
            rootModel = models.get(0);
        } else
            rootModel = this.model;
        renderTree(rootModel);
    }

    private void renderTree(ItemNodeModel rootModel) {
        root = buildGWTTree(rootModel);
        tree = new Tree();
        tree.addItem(root);
        root.setState(true);
        if (this.columnLayoutModel != null) {// TODO if create a new ForeignKey, tree UI can not render according to the
                                             // layout template
            HorizontalPanel hp = new HorizontalPanel();
            for (ColumnTreeModel ctm : columnLayoutModel.getColumnTreeModels()) {
                Tree tree = displayGWTTree(ctm);
                hp.add(tree);
            }
            hp.setHeight("570px"); //$NON-NLS-1$
            HorizontalPanel spacehp = new HorizontalPanel();
            spacehp.setHeight("10px"); //$NON-NLS-1$
            add(spacehp);
            add(hp);

        } else {
            add(tree);
        }
    }

    public void refreshTree() {
        BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
        final ItemBean item = fkModel.getItemBean();
        item.set("isRefresh", true); //$NON-NLS-1$
        service.getItemNodeModel(item, viewBean.getBindingEntityModel(), Locale.getLanguage(),
                new SessionAwareAsyncCallback<ItemNodeModel>() {

                    public void onSuccess(ItemNodeModel nodeModel) {
                        fkModel.setNodeModel(nodeModel);
                        model = nodeModel;
                        ForeignKeyTreeDetail.this.getItem(0).removeFromParent();
                        item.set("time", nodeModel.get("time")); //$NON-NLS-1$ //$NON-NLS-2$
                        renderTree(nodeModel);
                        ForeignKeyTreeDetail.this.layout();
                    }

                    @Override
                    protected void doOnFailure(Throwable caught) {
                        MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().refresh_tip()
                                + " " + MessagesFactory.getMessages().message_fail(), null); //$NON-NLS-1$
                    }
                });

    }

    public ViewBean getViewBean() {
        return viewBean;
    }

    public void setViewBean(ViewBean viewBean) {
        this.viewBean = viewBean;
        buildPanel(viewBean);
    }

    private DynamicTreeItem buildGWTTree(final ItemNodeModel itemNode) {
        DynamicTreeItem item = new DynamicTreeItem();

        item.setWidget(TreeDetailUtil.createWidget(itemNode, viewBean, fieldMap, handler));
        item.setUserObject(itemNode);
        if (itemNode.getChildren() != null && itemNode.getChildren().size() > 0) {
            final Map<TypeModel, List<ItemNodeModel>> fkMap = new HashMap<TypeModel, List<ItemNodeModel>>();
            for (ModelData model : itemNode.getChildren()) {
                ItemNodeModel node = (ItemNodeModel) model;
                TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(node.getBindingPath());
                if (this.isCreate && this.model != null && node.isKey()) // duplicate
                    node.setObjectValue(null); // id
                if (typeModel.getForeignkey() != null && fkRender != null) {
                    if (!fkMap.containsKey(typeModel))
                        fkMap.put(typeModel, new ArrayList<ItemNodeModel>());
                    fkMap.get(typeModel).add(node);
                } else if (typeModel.getForeignkey() == null) {
                    item.addItem(buildGWTTree(node));
                }
            }

            if (fkMap.size() > 0) {
                DeferredCommand.addCommand(new Command() {

                    public void execute() {
                        for (TypeModel model : fkMap.keySet()) {
                            fkRender.RenderForeignKey(itemNode, fkMap.get(model), model);
                        }
                        itemNode.addChangeListener(new ChangeListener() {

                            public void modelChanged(ChangeEvent event) {
                                if (event.getType() == ChangeEventSource.Remove) {
                                    ItemNodeModel source = (ItemNodeModel) event.getItem();
                                    fkRender.removeRelationFkPanel(source);
                                }
                            }
                        });
                    }
                });
            }

            item.getElement().getStyle().setPaddingLeft(3.0, Unit.PX);
        }

        return item;
    }

    private Tree displayGWTTree(ColumnTreeModel treeModel) {
        Tree tree = new Tree();
        if (root != null && root.getChildCount() > 0) {
            for (ColumnElement ce : treeModel.getColumnElements()) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    TreeItem child = root.getChild(i);
                    ItemNodeModel node = (ItemNodeModel) child.getUserObject();
                    if (("/" + node.getBindingPath()).equals(ce.getxPath())) { //$NON-NLS-1$
                        tree.addItem(child);
                        break;
                    }
                }
            }
        }
        return tree;
    }

    public ItemNodeModel getRootModel() {
        return (ItemNodeModel) root.getUserObject();
    }

    public boolean isCreate() {
        return isCreate;
    }

    public ForeignKeyModel getFkModel() {
        return fkModel;
    }

    public void setFkModel(ForeignKeyModel fkModel) {
        this.fkModel = fkModel;
    }

    public static class DynamicTreeItem extends TreeItem {

        public DynamicTreeItem() {
            super();
            this.setWidget(widget);
        }

        private TreeItemWidget widget = new TreeItemWidget();

        private List<TreeItem> items = new ArrayList<TreeItem>();

        public void insertItem(TreeItem item, int beforeIndex) {
            int count = this.getChildCount();
            if (items.size() == 0) {
                for (int i = 0; i < count; i++) {
                    items.add(this.getChild(i));
                }
            }

            items.add(beforeIndex, item);
            super.removeItems();

            for (int j = 0; j < items.size(); j++) {
                this.addItem(items.get(j));
            }
        }

        public void removeTreeItem(TreeItem item) {
            super.removeItem(item);
            items.remove(item);
        }
    }

    public static abstract class AbstractTreeItemWidget extends HorizontalPanel {

        public AbstractTreeItemWidget() {
            super();
        }

        private boolean isSimpleType;

        public boolean isSimpleType() {
            return isSimpleType;
        }

        public void setSimpleType(boolean isSimpleType) {
            this.isSimpleType = isSimpleType;
        }

        public abstract void paint();
    }

    public static class TreeItemWidget extends AbstractTreeItemWidget {

        public TreeItemWidget() {
            super();
        }

        ClickHandler handler;

        public void setHandler(ClickHandler handler) {
            this.handler = handler;
        }

        Image add;

        Image remove;

        HTML label = new HTML();

        Field<?> field;

        public Field<?> getField() {
            return field;
        }

        public void setField(Field<?> field) {
            this.field = field;
        }

        public HTML getLabel() {
            return label;
        }

        public void setLabel(HTML label) {
            this.label = label;
        }

        public void paint() {
            this.add(label);
            this.add(field);

            add = buildAdd();
            add.addClickHandler(handler);
            remove = buildRemove();
            remove.addClickHandler(handler);
            if (isSimpleType()) {
                this.add(add);
                this.add(remove);
            }

            this.setCellWidth(label, "200px"); //$NON-NLS-1$
        }

        public Image getAdd() {
            if (add != null) {
                return add;
            }

            add = new Image();

            return add;
        }

        public Image getRemove() {
            if (remove != null) {
                return remove;
            }

            remove = new Image();

            return remove;
        }

        public static Image buildAdd() {
            Image add = new Image("/talendmdm/secure/img/genericUI/add.png"); //$NON-NLS-1$
            add.getElement().setId("Add"); //$NON-NLS-1$
            add.getElement().getStyle().setMarginLeft(5.0, Unit.PX);

            return add;
        }

        public static Image buildRemove() {
            Image remove = new Image("/talendmdm/secure/img/genericUI/delete.png"); //$NON-NLS-1$
            remove.getElement().getStyle().setMarginLeft(5.0, Unit.PX);

            return remove;
        }
    }

    public boolean validateTree() {
        boolean flag = true;
        ItemNodeModel rootNode = (ItemNodeModel) tree.getItem(0).getUserObject();
        if (rootNode != null) {
            flag = validateNode(rootNode, flag);
        }
        return flag;
    }

    public boolean validateNode(ItemNodeModel rootNode, boolean flag) {

        if (rootNode.getChildren() != null && rootNode.getChildren().size() > 0) {
            for (ModelData model : rootNode.getChildren()) {

                ItemNodeModel node = (ItemNodeModel) model;
                if (!node.isValid() && node.getChildCount() == 0) {
                    com.google.gwt.user.client.Window.alert(node.getName() + "'Value validate failure"); //$NON-NLS-1$
                    flag = false;
                }

                if (node.getChildren() != null && node.getChildren().size() > 0) {
                    flag = validateNode(node, flag);
                }

                if (!flag) {
                    break;
                }
            }
        }
        return flag;
    }

    public ForeignKeyRender getFkRender() {
        return fkRender;
    }

    public void setFkRender(ForeignKeyRender fkRender) {
        this.fkRender = fkRender;
    }
}
