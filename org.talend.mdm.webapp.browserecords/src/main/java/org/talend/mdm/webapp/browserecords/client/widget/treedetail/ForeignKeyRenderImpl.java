package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.TabItem;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    HashMap<ItemNodeModel, TabItem> relationFk = new HashMap<ItemNodeModel, TabItem>();

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    public ForeignKeyRenderImpl() {

    }

    public void RenderForeignKey(final ItemNodeModel parentModel, final List<ItemNodeModel> fkNodeModelList,
            final TypeModel fkTypeModel, final ItemDetailToolBar toolBar) {
        if (fkNodeModelList != null) {
            String viewFkName = "Browse_items_" + fkTypeModel.getForeignkey().split("/")[0]; //$NON-NLS-1$ //$NON-NLS-2$
            service.getView(viewFkName, Locale.getLanguage(), new SessionAwareAsyncCallback<ViewBean>() {
                public void onSuccess(ViewBean viewBean) {
                    ForeignKeyTablePanel fkPanel = new ForeignKeyTablePanel(viewBean, parentModel, fkNodeModelList, fkTypeModel,
                            toolBar);
                    String xp = fkTypeModel.getXpath();
                    StringBuffer sb = new StringBuffer();
                    // a/b/c/d
                    Stack<String> stack = new Stack<String>();
                    do{

                        TypeModel tm = viewBean.getBindingEntityModel().getMetaDataTypes().get(xp);
                        if (tm != null)
                            stack.push(tm.getLabel(Locale.getLanguage()));
                        xp = xp.substring(0, xp.lastIndexOf("/")); //$NON-NLS-1$

                    } while (xp.indexOf("/") != -1); //$NON-NLS-1$
                    boolean flag = true;

                    while (!stack.isEmpty()) {
                        if(flag)
                           flag = false;
                        else
                            sb.append("/"); //$NON-NLS-1$
                        sb.append(stack.pop());
                    }

                    TabItem tabItem = ItemsDetailPanel.getInstance().addTabItem(sb.toString(), fkPanel,
                            ItemsDetailPanel.MULTIPLE, fkTypeModel.getXpath());
                    relationFk.put(parentModel, tabItem);
                }
            });
        }
    }
    
    public void removeRelationFkPanel(ItemNodeModel parentModel){
        TabItem tabItem = relationFk.get(parentModel);
        if (tabItem != null) {
            tabItem.removeFromParent();
        }
    }

}
