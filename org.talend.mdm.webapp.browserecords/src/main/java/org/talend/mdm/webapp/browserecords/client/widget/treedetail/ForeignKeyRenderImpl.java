package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.List;

import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    public ForeignKeyRenderImpl() {

    }

    public void RenderForeignKey(List<ItemNodeModel> fkNodeModelList, TypeModel fkTypeModel) {
        if (fkNodeModelList != null) {
            ForeignKeyPanel fkPanel = new ForeignKeyPanel(fkNodeModelList, fkTypeModel);
            ItemsDetailPanel.getInstance().addTabItem(fkTypeModel.getLabel(UrlUtil.getLanguage()), fkPanel,
                    ItemsDetailPanel.MULTIPLE, fkTypeModel.getXpath());
        }


    }

}
