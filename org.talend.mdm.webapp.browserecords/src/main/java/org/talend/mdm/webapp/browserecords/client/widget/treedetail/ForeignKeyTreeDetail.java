package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

public class ForeignKeyTreeDetail extends ContentPanel {

    private ViewBean viewBean;

    private ItemDetailToolBar toolBar;

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
        TreeItem root = buildGWTTree(models.get(0));

        Tree tree = new Tree();
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

    private TreeItem buildGWTTree(ItemNodeModel itemNode) {
        TreeItem item = new TreeItem();

        item.setWidget(TreeDetailUtil.createWidget(itemNode, itemNode.getDescription(), viewBean));

        if (itemNode.getChildren() != null && itemNode.getChildren().size() > 0) {
            for (ModelData model : itemNode.getChildren()) {
                ItemNodeModel node = (ItemNodeModel) model;
                item.addItem(buildGWTTree(node));
            }
        }

        return item;
    }

}