package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;

public interface ForeignKeyRender {

    public void RenderForeignKey(ItemNodeModel parent, TypeModel fkTypeModel);

}
