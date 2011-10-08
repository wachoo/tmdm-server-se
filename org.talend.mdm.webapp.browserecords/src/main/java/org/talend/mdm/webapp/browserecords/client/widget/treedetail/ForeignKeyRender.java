package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.List;

import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;

public interface ForeignKeyRender {

    public void RenderForeignKey(List<ItemNodeModel> fkNodeModelList, TypeModel fkTypeModel);

}
