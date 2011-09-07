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
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
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
			
            if ("Add".equals(arg0.getRelativeElement().getId())) { //$NON-NLS-1$
				//clone a new item
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
			}
			else {
				parentItem.removeTreeItem(selected);
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

        item.setWidget(TreeDetailUtil.createWidget(itemNode, viewBean, handler));

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
			this.setWidget(widget);
		}
    	
    	private TreeItemWidget widget = new TreeItemWidget(); 
    	
    	private List<TreeItem> items = new ArrayList<TreeItem>();
    	
		public void insertItem(TreeItem item, int beforeIndex) {
    		int count = this.getChildCount();
    		if(items.size() == 0) {
	    		for(int i = 0; i < count; i++) {
	    			items.add(this.getChild(i));
	    		}
    		}
    	
    		items.add(beforeIndex, item);
    		super.removeItems();
    		
    		for(int j = 0; j < items.size(); j++) {
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
			if(isSimpleType()) {
				this.add(add);
            	this.add(remove);
			}

			this.setCellWidth(label, "200px"); //$NON-NLS-1$
		}
		
		public Image getAdd() {
    		if(add != null) {
    			return add;
    		}
    		
    		add = new Image();
    		
    		return add;
    	}
    	
    	public Image getRemove() {
    		if(remove != null) {
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

    // public ItemNodeModel getBindingModel() {
    // return ValueBinding.getModelByFieldValue(tree.getItem(0));
    // }
}