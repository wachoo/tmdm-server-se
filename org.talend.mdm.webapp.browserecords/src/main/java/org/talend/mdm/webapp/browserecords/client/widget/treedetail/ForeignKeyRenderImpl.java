package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.ui.TreeItem;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    HashMap<ItemNodeModel, TabItem> relationFk = new HashMap<ItemNodeModel, TabItem>();

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);
    
    public ForeignKeyRenderImpl() {
        
    }

    public void RenderForeignKey(final ItemNodeModel parentModel, final List<ItemNodeModel> fkNodeModelList,
            final TypeModel fkTypeModel, final ItemDetailToolBar toolBar, final ViewBean pkViewBean, final ContentPanel cp, final ItemsDetailPanel detailPanel) {
        if (fkNodeModelList != null) {
            String concept = fkTypeModel.getForeignkey().split("/")[0]; //$NON-NLS-1$
            service.getEntityModel(concept, Locale.getLanguage(), new SessionAwareAsyncCallback<EntityModel>() {

                public void onSuccess(EntityModel entityModel) {
                    Map<String, Field<?>> fieldMap;
                    TreeItem root;
                    if (cp instanceof TreeDetail) {
                        fieldMap = ((TreeDetail) cp).getFieldMap();
                        root = ((TreeDetail) cp).getTree().getItem(0);
                    } else {
                        fieldMap = ((ForeignKeyTreeDetail) cp).getFieldMap();
                        root = ((ForeignKeyTreeDetail) cp).getRoot();
                    }
                    ForeignKeyTablePanel fkPanel = new ForeignKeyTablePanel(entityModel, parentModel, fkNodeModelList,
                            fkTypeModel, fieldMap, detailPanel, pkViewBean);
                    ItemPanel itemPanel = new ItemPanel(pkViewBean, toolBar.getItemBean(), toolBar.getOperation(), fkPanel, root, detailPanel);
                    String xpathLabel = ForeignKeyUtil.transferXpathToLabel(fkTypeModel, pkViewBean);
                    TabItem tabItem = detailPanel.addTabItem(xpathLabel, itemPanel,
                            ItemsDetailPanel.MULTIPLE,
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
