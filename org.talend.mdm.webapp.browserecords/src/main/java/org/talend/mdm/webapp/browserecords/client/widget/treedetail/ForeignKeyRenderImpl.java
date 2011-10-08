package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;

import com.extjs.gxt.ui.client.data.ModelData;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    public ForeignKeyRenderImpl() {

    }

    public void RenderForeignKey(List<ItemNodeModel> fkNodeModelList, TypeModel fkTypeModel) {
        if (fkNodeModelList != null) {
            List<ItemNodeModel> fkModels = new ArrayList<ItemNodeModel>();
            for (ModelData child : fkNodeModelList) {
                ItemNodeModel item = (ItemNodeModel) child;
                if (item.getBindingPath().equals(fkTypeModel.getXpath())) {
                    fkModels.add(item);
                }
            }
            ForeignKeyPanel fkPanel = new ForeignKeyPanel(fkModels);
            ItemsDetailPanel.getInstance().addTabItem(fkTypeModel.getDescriptionMap().get(UrlUtil.getLanguage()), fkPanel,
                    ItemsDetailPanel.MULTIPLE, fkTypeModel.getXpath());
        }


    }

}
