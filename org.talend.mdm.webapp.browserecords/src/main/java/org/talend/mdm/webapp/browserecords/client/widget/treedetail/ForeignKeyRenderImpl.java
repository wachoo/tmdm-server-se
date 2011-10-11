package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.List;

import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.widget.TabItem;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    HashMap<ItemNodeModel, TabItem> relationFk = new HashMap<ItemNodeModel, TabItem>();
    
    public ForeignKeyRenderImpl() {

    }

    public void RenderForeignKey(ViewBean viewBean, ItemNodeModel parentModel, List<ItemNodeModel> fkNodeModelList,
            TypeModel fkTypeModel) {
        if (fkNodeModelList != null) {

            ForeignKeyTablePanel fkPanel = new ForeignKeyTablePanel(viewBean, parentModel, fkNodeModelList, fkTypeModel);
            TabItem tabItem = ItemsDetailPanel.getInstance().addTabItem(fkTypeModel.getXpath(), fkPanel,
                    ItemsDetailPanel.MULTIPLE, fkTypeModel.getXpath());
            relationFk.put(parentModel, tabItem);
        }


    }
    
    public void removeRelationFkPanel(ItemNodeModel parentModel){
        TabItem tabItem = relationFk.get(parentModel);
        if (tabItem != null) {
            tabItem.removeFromParent();
        }
    }

}
