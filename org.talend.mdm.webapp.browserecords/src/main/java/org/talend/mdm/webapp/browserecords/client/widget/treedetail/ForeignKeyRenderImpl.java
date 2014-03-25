// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.i18n.BrowseRecordsMessages;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyTabModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ItemPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel.ForeignKeyHandler;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel.ItemDetailTabPanelContentHandle;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.DynamicTreeItem;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;

public class ForeignKeyRenderImpl implements ForeignKeyRender {

    HashMap<ItemNodeModel, ItemDetailTabPanelContentHandle> relationFk = new HashMap<ItemNodeModel, ItemDetailTabPanelContentHandle>();

    BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    public ForeignKeyRenderImpl() {

    }

    public void RenderForeignKey(final ItemNodeModel parentModel, final List<ItemNodeModel> fkNodeModelList,
            final TypeModel fkTypeModel, final ItemDetailToolBar toolBar, final ViewBean pkViewBean, final ContentPanel cp,
            final ItemsDetailPanel detailPanel) {
        if (fkNodeModelList != null) {
            final String concept = fkTypeModel.getForeignkey().split("/")[0]; //$NON-NLS-1$

            final Map<String, Field<?>> fieldMap;
            DynamicTreeItem root;
            if (cp instanceof TreeDetail) {
                TreeDetail treeDetail = (TreeDetail) cp;
                fieldMap = treeDetail.getFieldMap();
                root = (DynamicTreeItem) treeDetail.getRoot();
            } else {
                ForeignKeyTreeDetail fkTreeDetail = (ForeignKeyTreeDetail) cp;
                fieldMap = fkTreeDetail.getFieldMap();
                root = (DynamicTreeItem) fkTreeDetail.getRoot();
            }
            final ForeignKeyTablePanel fkPanel = new ForeignKeyTablePanel(concept + "_ForeignKeyTablePanel"); //$NON-NLS-1$

            final ItemPanel itemPanel = new ItemPanel(pkViewBean, toolBar.getItemBean(), toolBar.getOperation(), fkPanel, root,
                    detailPanel, toolBar.isOpenTab());
            itemPanel.getToolBar().setOutMost(toolBar.isOutMost());
            itemPanel.getToolBar().setHierarchyCall(toolBar.isHierarchyCall());
            // TMDM-3380. FK Tab Title should not support the dynamic label. If FK exist dynamic label, it should remove
            // '{***}' section to display title. for instance: 'Agency:{position()}' is an English dynamic label, Agency
            // FK Tab Title should be 'Agency', but not 'Agency:{position()}'. Only when Agency as an item to be
            // displayed in tree detail UI, its dynamic label can work.
            String xpathLabel = ForeignKeyUtil.transferXpathToLabel(parentModel)
                    + LabelUtil.getFKTabLabel(fkTypeModel.getLabel(UrlUtil.getLanguage()));
            xpathLabel = xpathLabel.substring(xpathLabel.indexOf('/') + 1);
            if(cp != detailPanel.getTreeDetail())
                return;
            ItemDetailTabPanelContentHandle handle = detailPanel.addTabItem(xpathLabel, itemPanel, ItemsDetailPanel.MULTIPLE,
                    GWT.getModuleName() + DOM.createUniqueId());
            relationFk.put(parentModel, handle);
            // lazy render FK
            ForeignKeyHandler handler = new ForeignKeyHandler() {
                
                public void onSelect() {
                    BrowseRecordsMessages msg = MessagesFactory.getMessages();
                    final MessageBox renderFkProgress = MessageBox.wait(msg.rendering_title(), msg.render_message(), msg.rendering_progress());
                    service.getEntityModel(concept, Locale.getLanguage(), new SessionAwareAsyncCallback<EntityModel>() {

                        public void onSuccess(EntityModel entityModel) {
                            fkPanel.initContent(entityModel, parentModel, fkNodeModelList, fkTypeModel, fieldMap, detailPanel, pkViewBean);
                            itemPanel.layout(true);
                            renderFkProgress.close();
                        }
                        
                        protected void doOnFailure(Throwable caught) {
                            renderFkProgress.close();
                            super.doOnFailure(caught);
                        }
                    });
                }
            };
            detailPanel.addFkHandler(itemPanel, handler);
            HashMap<String, ItemPanel> cachedEntityMap = BrowseRecords.getSession().getCurrentCachedEntity();
            String ids = toolBar.getItemBean().getIds() != null ? toolBar.getItemBean().getIds() : ""; //$NON-NLS-1$
            String key = toolBar.getItemBean().getConcept() + ids + detailPanel.isOutMost();
            if(cachedEntityMap != null && cachedEntityMap.containsKey(key)){
                ForeignKeyTabModel fkTabModel = new ForeignKeyTabModel(parentModel, xpathLabel, itemPanel, handler);
                HashMap<String, LinkedHashMap<String, ForeignKeyTabModel>> cachedFkPanels = BrowseRecords.getSession()
                        .getCurrentCachedFKTabs();
                if (cachedFkPanels == null)
                    cachedFkPanels = new HashMap<String, LinkedHashMap<String, ForeignKeyTabModel>>();
                if (!cachedFkPanels.containsKey(key)) {
                    cachedFkPanels.put(key, new LinkedHashMap<String, ForeignKeyTabModel>());
                }
                cachedFkPanels.get(key).put(xpathLabel, fkTabModel);
                BrowseRecords.getSession().put(UserSession.CURRENT_CACHED_FKTABS, cachedFkPanels);
            }
            
        }
    }

    public void removeRelationFkPanel(ItemNodeModel parentModel) {
        ItemDetailTabPanelContentHandle tabItem = relationFk.get(parentModel);
        if (tabItem != null) {
            tabItem.deleteContent();
            relationFk.remove(parentModel);
        }
    }

    public void setRelationFk(ItemNodeModel parentModel, ItemDetailTabPanelContentHandle handle) {
        relationFk.put(parentModel, handle);
    }
}
