package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class ForeignKeyTreeDetail extends ContentPanel {

    private ViewBean viewBean;

    private ItemDetailToolBar toolBar;
    
    Tree tree;
    
    private ClickHandler handler = new ClickHandler() {
		
		public void onClick(ClickEvent arg0) {
			DynamicTreeItem selected = (DynamicTreeItem) tree.getSelectedItem();
			DynamicTreeItem parentItem = (DynamicTreeItem) selected.getParentItem();
			
			if("Add".equals(arg0.getRelativeElement().getId())) {
				//clone a new item
				Element clonedElement = DOM.clone(selected.getElement(), true);
				DynamicTreeItem clonedItem = new DynamicTreeItem();
				clonedItem.getElement().setInnerHTML(clonedElement.getInnerHTML());
				parentItem.insertItem(clonedItem, parentItem.getChildIndex(selected));
			}
			else {
				parentItem.removeItem(selected);
			}
		}
	};

    public ForeignKeyTreeDetail() {
        this.setHeaderVisible(false);
        this.setHeight(1000);
        this.setScrollMode(Scroll.AUTO);
    }
    public ForeignKeyTreeDetail(ViewBean viewBean) {
        this();
        this.viewBean = viewBean;
        this.toolBar = new ItemDetailToolBar(null, ItemDetailToolBar.VIEW_OPERATION);
        this.setTopComponent(toolBar);
        buildPanel(viewBean);
    }

    public void buildPanel(final ViewBean viewBean) {

        List<ItemNodeModel> models = CommonUtil.getDefaultTreeModel(viewBean.getBindingEntityModel().getMetaDataTypes()
                .get(viewBean.getBindingEntityModel().getConceptName()));
        DynamicTreeItem root = buildGWTTree(models.get(0));

        tree = new Tree();
        tree.addItem(root);
        root.setState(true);

        add(tree);


    }

    public ViewBean getViewBean() {
        return viewBean;
    }

    public void setViewBean(ViewBean viewBean) {
        this.viewBean = viewBean;
        buildPanel(viewBean);
    }

    private DynamicTreeItem buildGWTTree(ItemNodeModel itemNode) {
    	DynamicTreeItem item = new DynamicTreeItem();

        item.setWidget(TreeDetailUtil.createWidget(itemNode, itemNode.getDescription(), viewBean, handler));

        if (itemNode.getChildren() != null && itemNode.getChildren().size() > 0) {
            for (ModelData model : itemNode.getChildren()) {
                ItemNodeModel node = (ItemNodeModel) model;
                item.addItem(buildGWTTree(node));
            }
        }

        return item;
    }
    
    public static class DynamicTreeItem extends TreeItem {
    	public DynamicTreeItem() {
			super();
		}
    	
    	private List<TreeItem> items = new ArrayList<TreeItem>();
    	
    	public void insertItem(TreeItem item, int beforeIndex) {
    		int count = this.getChildCount();
    		
    		for(int i = 0; i < count; i++) {
    			items.add(this.getChild(i));
    		}
    		
    		items.add(beforeIndex, item);
    		this.removeItems();
    		
    		for(int j = 0; j < items.size(); j++) {
    			this.addItem(items.get(j));
    		}
    	}
    }
}