package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ColumnElement;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class TreeDetail extends ContentPanel {

    private final ViewBean viewBean = (ViewBean) BrowseRecords.getSession().get(UserSession.CURRENT_VIEW);

    private Tree tree = new Tree();

    private TreeItem root;

    private HashMap<String, Integer> occurMap = new HashMap<String, Integer>();

    private ClickHandler handler = new ClickHandler() {

        public void onClick(ClickEvent arg0) {
            DynamicTreeItem selected = (DynamicTreeItem) tree.getSelectedItem();
            DynamicTreeItem parentItem = (DynamicTreeItem) selected.getParentItem();

            String xpath = selected.getItemNodeModel().getBindingPath();
            int count = 0;
            if (occurMap.containsKey(xpath)) {
                count = occurMap.get(xpath);
            }
            if ("Add".equals(arg0.getRelativeElement().getId()) || "Clone".equals(arg0.getRelativeElement().getId())) { //$NON-NLS-1$ //$NON-NLS-2$               
                if (viewBean.getBindingEntityModel().getMetaDataTypes().get(xpath).getMaxOccurs() < 0
                        || count < viewBean.getBindingEntityModel().getMetaDataTypes().get(xpath).getMaxOccurs()) {
                    // clone a new item
                    ItemNodeModel model = selected.getItemNodeModel().clone(
                            "Clone".equals(arg0.getRelativeElement().getId()) ? true : false); //$NON-NLS-1$
                    // TODO if it has default value
                    // model.setObjectValue(null);
                    parentItem.insertItem(buildGWTTree(model, null), parentItem.getChildIndex(selected) + 1);
                    occurMap.put(xpath, count + 1);
                } else
                    MessageBox.alert(MessagesFactory.getMessages().status(), MessagesFactory.getMessages()
                            .multiOccurrence_maximize(count), null);
            } else {
                if (count > 1 && count > viewBean.getBindingEntityModel().getMetaDataTypes().get(xpath).getMinOccurs()) {
                    parentItem.removeItem(selected);
                    occurMap.put(xpath, count - 1);
                } else
                    MessageBox.alert(MessagesFactory.getMessages().status(), MessagesFactory.getMessages()
                            .multiOccurrence_minimize(count), null);
            }
        }
    };

    public TreeDetail() {
        this.setHeaderVisible(false);
        this.setHeight(1000);
        this.setScrollMode(Scroll.AUTO);
    }

    public void initTree(ItemBean itemBean) {        
        if (itemBean == null) {
            buildPanel(viewBean);
        } else {
            getItemService().getItemNodeModel(itemBean, viewBean.getBindingEntityModel(),
            		Locale.getLanguage(), new AsyncCallback<ItemNodeModel>() {

                public void onSuccess(ItemNodeModel node) {
                    renderTree(node);
                    //maybe need to refactor for performance
                            getItemService().executeVisibleRule(CommonUtil.toXML(node, viewBean),
                                    new AsyncCallback<List<VisibleRuleResult>>() {
						public void onFailure(Throwable arg0) {
						}

						public void onSuccess(List<VisibleRuleResult> arg0) {
							for(VisibleRuleResult visibleRuleResult : arg0) {
								recrusiveSetItems(visibleRuleResult, (DynamicTreeItem) root);
							}
						}
					});
                }

                public void onFailure(Throwable arg0) {
                    arg0.printStackTrace();
                }
            });
        }
    }

    public void buildPanel(final ViewBean viewBean) {

        List<ItemNodeModel> models = CommonUtil.getDefaultTreeModel(viewBean.getBindingEntityModel().getMetaDataTypes()
                .get(viewBean.getBindingEntityModel().getConceptName()));
        renderTree(models.get(0));
    }

    private DynamicTreeItem buildGWTTree(ItemNodeModel itemNode, DynamicTreeItem item) {
        if (item == null) {
            item = new DynamicTreeItem();
            item.setItemNodeModel(itemNode);
            item.setWidget(TreeDetailUtil.createWidget(itemNode, viewBean, handler));

            int count = 0;
            if (occurMap.containsKey(itemNode.getBindingPath()))
                count = occurMap.get(itemNode.getBindingPath());
            occurMap.put(itemNode.getBindingPath(), count + 1);
        }
        if (itemNode.getChildren() != null && itemNode.getChildren().size() > 0) {
            for (ModelData model : itemNode.getChildren()) {
                ItemNodeModel node = (ItemNodeModel) model;
                item.addItem(buildGWTTree(node, null));
            }
        }

        item.setUserObject(itemNode);

        return item;
    }

    public void onUpdatePolymorphism(ComplexTypeModel typeModel) {
        DynamicTreeItem item = (DynamicTreeItem) tree.getSelectedItem();
        ItemNodeModel treeNode = item.getItemNodeModel();
        List<ComplexTypeModel> reusableTypes = typeModel.getReusableComplexTypes();
        if (reusableTypes != null) {
            for (ComplexTypeModel model : reusableTypes) {
                if (model.getName().equals(typeModel.getRealType())) {
                    String xPath = treeNode.getBindingPath();
                    viewBean.getBindingEntityModel().getMetaDataTypes().get(xPath).setRealType(model);
                    if (item.getItemNodeModel() == treeNode) {
                        item.removeItems();
                        List<ItemNodeModel> items = CommonUtil.getDefaultTreeModel(model);
                        if (items != null && items.size() > 0) {
                            List<ItemNodeModel> childrenItems = new ArrayList<ItemNodeModel>();
                            for (ModelData modelData : items.get(0).getChildren()) {
                                childrenItems.add((ItemNodeModel) modelData);
                            }
                            treeNode.setChildNodes(childrenItems);
                        }
                        buildGWTTree(treeNode, item);
                        break;
                    }

                }
            }
        }
    }

    public void onExecuteVisibleRule(List<VisibleRuleResult> visibleResults) {
        DynamicTreeItem rootItem = (DynamicTreeItem) tree.getItem(0);
        for (VisibleRuleResult visibleResult : visibleResults) {
            recrusiveSetItems(visibleResult, rootItem);
        }
    }

    private Tree displayGWTTree(ColumnTreeModel columnLayoutModel) {
        Tree tree = new Tree();
        if (root != null && root.getChildCount() > 0) {
            
            for (ColumnElement ce : columnLayoutModel.getColumnElements()) {
                for (int i = 0; i < root.getChildCount(); i++) {
                    TreeItem child = root.getChild(i);
                    ItemNodeModel node = (ItemNodeModel) child.getUserObject();
                    if (node.getBindingPath().equals(ce.getxPath())) {
                        tree.addItem(child);
                        if (child.getChildCount() > 0)
                            child.getElement().getStyle().setPaddingLeft(3.0, Unit.PX);
                        break;
                    }
                }
            }
        }

        return tree;
    }

    private void renderTree(ItemNodeModel rootModel) {
        root = buildGWTTree(rootModel, null);
        root.setState(true);
        tree = new Tree();
        tree.addItem(root);

        ColumnTreeLayoutModel columnLayoutModel = viewBean.getColumnLayoutModel();
        if (columnLayoutModel != null) {// TODO if create a new PrimaryKey, tree UI should not render according to the
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
        this.layout();
    }

    private void recrusiveSetItems(VisibleRuleResult visibleResult, DynamicTreeItem rootItem) {
    	if(rootItem.getItemNodeModel().getBindingPath().equals(visibleResult.getXpath())) {
    		rootItem.setVisible(visibleResult.isVisible());
    	}
    	
    	if(rootItem.getChildCount() == 0) {
    		return;
    	}
    	
    	for(int i = 0; i < rootItem.getChildCount(); i++) {
    		DynamicTreeItem item = (DynamicTreeItem) rootItem.getChild(i);
    		recrusiveSetItems(visibleResult, item);
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
            itemNode.getChildren().add(beforeIndex, item.getItemNodeModel());
            this.removeItems();

            for (int j = 0; j < items.size(); j++) {
                this.addItem(items.get(j));
            }
            items.clear();
        }

        public void removeItem(DynamicTreeItem item) {
            super.removeItem(item);
            itemNode.getChildren().remove(item.getItemNodeModel());
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

    public void refreshTree(ItemBean item) {
        getItemService().getItemNodeModel(item, viewBean.getBindingEntityModel(),
                Locale.getLanguage(), new AsyncCallback<ItemNodeModel>() {

                    public void onSuccess(ItemNodeModel node) {
                        TreeDetail.this.removeAll();
                        renderTree(node);
                    }

                    public void onFailure(Throwable caught) {
                        MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().refresh_tip()
                                + " " + MessagesFactory.getMessages().message_fail(), null); //$NON-NLS-1$
                    }
                });
    }
}
