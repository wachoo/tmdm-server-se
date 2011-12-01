package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel.ItemDetailTabPanelContentHandle;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.TreeItem;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    HashMap<ItemNodeModel, ItemDetailTabPanelContentHandle> relationFk = new HashMap<ItemNodeModel, ItemDetailTabPanelContentHandle>();

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    public ForeignKeyRenderImpl() {

    }

    public void RenderForeignKey(final ItemNodeModel parentModel, final List<ItemNodeModel> fkNodeModelList,
            final TypeModel fkTypeModel, final ItemDetailToolBar toolBar, final ViewBean pkViewBean, final ContentPanel cp,
            final ItemsDetailPanel detailPanel) {
        if (fkNodeModelList != null) {
            String concept = fkTypeModel.getForeignkey().split("/")[0]; //$NON-NLS-1$

            final Map<String, Field<?>> fieldMap;
            TreeItem root;
            if (cp instanceof TreeDetail) {
                TreeDetail treeDetail = (TreeDetail) cp;
                fieldMap = treeDetail.getFieldMap();
                root = treeDetail.getTree().getItem(0);
            } else {
                ForeignKeyTreeDetail fkTreeDetail = (ForeignKeyTreeDetail) cp;
                fieldMap = fkTreeDetail.getFieldMap();
                root = fkTreeDetail.getRoot();
            }
            final ForeignKeyTablePanel fkPanel = new ForeignKeyTablePanel();

            ItemPanel itemPanel = new ItemPanel(pkViewBean, toolBar.getItemBean(), toolBar.getOperation(), fkPanel, root,
                    detailPanel);
            itemPanel.getToolBar().setOutMost(toolBar.isOutMost());
            String xpathLabel = ForeignKeyUtil.transferXpathToLabel(parentModel) + fkTypeModel.getLabel(UrlUtil.getLanguage());
            xpathLabel = xpathLabel.substring(xpathLabel.indexOf('/') + 1);
            ItemDetailTabPanelContentHandle handle = detailPanel.addTabItem(xpathLabel, itemPanel, ItemsDetailPanel.MULTIPLE,
                    GWT.getModuleName() + DOM.createUniqueId());
            relationFk.put(parentModel, handle);
            service.getEntityModel(concept, Locale.getLanguage(), new SessionAwareAsyncCallback<EntityModel>() {

                public void onSuccess(EntityModel entityModel) {
                    fkPanel.initContent(entityModel, parentModel, fkNodeModelList, fkTypeModel, fieldMap, detailPanel, pkViewBean);
                    fkPanel.layout(true);
                }
            });
        }
    }

    public void removeRelationFkPanel(ItemNodeModel parentModel) {
        ItemDetailTabPanelContentHandle tabItem = relationFk.get(parentModel);
        if (tabItem != null) {
            tabItem.deleteContent();
            relationFk.remove(parentModel);
        }
    }
}
