package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.TabItem;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    HashMap<ItemNodeModel, TabItem> relationFk = new HashMap<ItemNodeModel, TabItem>();

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    public ForeignKeyRenderImpl() {

    }

    public void RenderForeignKey(final ItemNodeModel parentModel, final List<ItemNodeModel> fkNodeModelList,
            final TypeModel fkTypeModel, final ItemDetailToolBar toolBar, final ViewBean pkViewBean) {
        TabItem tabItem = ItemsDetailPanel.getInstance().getTabPanel().getItem(0);
        if (tabItem != null) {
            ItemPanel itemPanel = (ItemPanel) tabItem.getWidget(0);
            if (pkViewBean == BrowseRecords.getSession().get(UserSession.CURRENT_VIEW) && itemPanel instanceof ItemPanel
                    && itemPanel.getToolBar() != toolBar)
                return;
        }
        if (fkNodeModelList != null) {
            String concept = fkTypeModel.getForeignkey().split("/")[0]; //$NON-NLS-1$ //$NON-NLS-2$
            service.getEntityModel(concept, Locale.getLanguage(), new SessionAwareAsyncCallback<EntityModel>() {

                public void onSuccess(EntityModel entityModel) {
                    ForeignKeyTablePanel fkPanel = new ForeignKeyTablePanel(entityModel, parentModel, fkNodeModelList,
                            fkTypeModel,
                            toolBar);
                    ItemPanel itemPanel = new ItemPanel(pkViewBean, toolBar.getItemBean(), toolBar.getOperation(), fkPanel);
                    String xpathLabel = ForeignKeyUtil.transferXpathToLabel(fkTypeModel, pkViewBean);
                    TabItem tabItem = ItemsDetailPanel.getInstance().addTabItem(xpathLabel, itemPanel, ItemsDetailPanel.MULTIPLE,
                            fkTypeModel.getXpath());
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
