// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyModel;
import org.talend.mdm.webapp.browserecords.client.model.FormatModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.Restriction;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;
import org.talend.mdm.webapp.browserecords.shared.VisibleRuleResult;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Mock Grid refresh() method is called<br>
 * After executing save successfully, when ItemDetailToolBar's isFkToolBar=false and isOutMost = false and
 * isHierarchyCall = false, gridRefresh() is called, otherwise gridRefresh() is not called.
 */
@SuppressWarnings("nls")
public class MockGridRefreshGWTTest extends GWTTestCase {

    boolean isGridRefresh;

    MockBrowseRecordsServiceAsync service = new MockBrowseRecordsServiceAsync();

    public void testGridRefresh() {

        AppEvent event = new AppEvent(BrowseRecordsEvents.SaveItem);
        ItemDetailToolBar detailToolBar = new ItemDetailToolBar(null);
        event.setData("itemDetailToolBar", detailToolBar);

        // 1. fkToolBar = false, isOutMost = false, isHierarchyCall = false
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(false);
        detailToolBar.setHierarchyCall(false);
        onSaveItem(event);
        assertEquals(true, isGridRefresh);

        isGridRefresh = false;

        // 2. fkToolBar = true, isOutMost = false, isHierarchyCall = false
        detailToolBar.setFkToolBar(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 3. fkToolBar = true, isOutMost = true, isHierarchyCall = false
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 4. fkToolBar = true, isOutMost = true, isHierarchyCall = true
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 5. fkToolBar = true, isOutMost = false, isHierarchyCall = true
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(false);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 6. fkToolBar = false, isOutMost = false, isHierarchyCall = true
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(false);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 7. fkToolBar = false, isOutMost = true, isHierarchyCall = true
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(true);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);

        // 8. fkToolBar = false, isOutMost = true, isHierarchyCall = false
        detailToolBar.setFkToolBar(false);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(false);
        onSaveItem(event);
        assertEquals(false, isGridRefresh);
        
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(false);
        onDeleteItem();
        assertEquals(true, isGridRefresh);
        
        detailToolBar.setFkToolBar(true);
        detailToolBar.setOutMost(true);
        detailToolBar.setHierarchyCall(false);
        onDeleteItemBeans();
        assertEquals(true, isGridRefresh);
    }

    private void onSaveItem(AppEvent event) {

        final ItemDetailToolBar detailToolBar = event.getData("itemDetailToolBar");

        service.saveItem(new ViewBean(), "", "", false, "en", new SessionAwareAsyncCallback<ItemResult>() {

            public void onSuccess(ItemResult result) {
                assertEquals(ItemResult.SUCCESS, result.getStatus());
                // Grid will call refresh() when only fkToolBar = false, isOutMost = false, isHierarchyCall = false
                if (!detailToolBar.isFkToolBar() && !detailToolBar.isOutMost() && !detailToolBar.isHierarchyCall()) {
                    gridRefresh();
                }
            }
            
        });
    }
    
    private void onDeleteItem(){
        service.deleteItemBean(null, false, "", new SessionAwareAsyncCallback<String>(){

            public void onSuccess(String result) {
                assertEquals("true", result);
                gridRefresh();
            }        
        });
    }
    
    private void onDeleteItemBeans(){
        service.deleteItemBeans(null, true, "en", new SessionAwareAsyncCallback<List<String>>() {
            
            public void onSuccess(List<String> result) {
                assertNotNull(result);
                gridRefresh();
            }
        });
    }
    
    private void gridRefresh() {
        isGridRefresh = true;
    }

    class MockBrowseRecordsServiceAsync implements BrowseRecordsServiceAsync {

        public void saveItem(ViewBean viewBean, String ids, String xml, boolean isCreate, String language,
                AsyncCallback<ItemResult> callback) {
            callback.onSuccess(new ItemResult(ItemResult.SUCCESS));
        }

        public void getForeignKeyList(PagingLoadConfig config, TypeModel model, String dataClusterPK, boolean ifFKFilter,
                String value, AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>> callback) {
        }

        public void getForeignKeyPolymTypeList(String xpathForeignKey, String language, AsyncCallback<List<Restriction>> callback) {
        }

        public void switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
                String fkFilter, AsyncCallback<ForeignKeyDrawer> callback) {
        }

        public void queryItemBeans(QueryModel config, AsyncCallback<ItemBasePageLoadResult<ItemBean>> callback) {
        }

        public void saveItemBean(ItemBean item, String language, AsyncCallback<String> callback) {
        }

        public void getItem(ItemBean itemBean, String viewPK, EntityModel entityModel, String language,
                AsyncCallback<ItemBean> callback) {
        }

        public void getView(String viewPk, String language, AsyncCallback<ViewBean> callback) {
        }

        public void deleteItemBean(ItemBean item, boolean override, String language, AsyncCallback<String> callback) {
            callback.onSuccess("true");
        }

        public void deleteItemBeans(List<ItemBean> items, boolean override, String language, AsyncCallback<List<String>> callback) {
            callback.onSuccess(new ArrayList<String>());
        }

        public void checkFKIntegrity(List<ItemBean> selectedItems, AsyncCallback<Map<ItemBean, FKIntegrityResult>> asyncCallback) {
        }

        public void logicalDeleteItem(ItemBean item, String path, boolean override, AsyncCallback<Void> callback) {
           
        }

        public void logicalDeleteItems(List<ItemBean> items, String path, boolean override, AsyncCallback<Void> callback) {
        }

        public void getViewsList(String language, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void getCriteriaByBookmark(String bookmark, AsyncCallback<String> callback) {
        }

        public void getUserCriterias(String view, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void getAppHeader(AsyncCallback<AppHeader> callback) {
        }

        public void getCurrentDataModel(AsyncCallback<String> callback) {
        }

        public void getCurrentDataCluster(AsyncCallback<String> callback) {
        }

        public void querySearchTemplates(String view, boolean isShared, PagingLoadConfig load,
                AsyncCallback<PagingLoadResult<ItemBaseModel>> callback) {
        }

        public void deleteSearchTemplate(String id, AsyncCallback<Void> callback) {
        }

        public void isExistCriteria(String dataObjectLabel, String id, AsyncCallback<Boolean> callback) {
        }

        public void saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString,
                AsyncCallback<Void> callback) {
        }

        public void getItemNodeModel(ItemBean item, EntityModel entity, String language, AsyncCallback<ItemNodeModel> callback) {
        }

        public void getMandatoryFieldList(String tableName, AsyncCallback<List<String>> callback) {
        }

        public void saveItem(String concept, String ids, String xml, boolean isCreate, String language,
                AsyncCallback<ItemResult> callback) {
        }

        public void getColumnTreeLayout(String concept, AsyncCallback<ColumnTreeLayoutModel> callback) {
        }

        public void getForeignKeyModel(String concept, String ids, String language, AsyncCallback<ForeignKeyModel> callback) {
        }

        public void getRunnableProcessList(String concept, String language, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void processItem(String concept, String[] ids, String transformerPK, AsyncCallback<String> callback) {
        }

        public void getLineageEntity(String concept, AsyncCallback<List<String>> callback) {
        }

        public void getSmartViewList(String regex, AsyncCallback<List<ItemBaseModel>> callback) {
        }

        public void getItemBeanById(String concept, String[] ids, String language, AsyncCallback<ItemBean> callback) {

        }

        public void executeVisibleRule(String xml, AsyncCallback<List<VisibleRuleResult>> asyncCallback) {

        }

        public void isItemModifiedByOthers(ItemBean itemBean, AsyncCallback<Boolean> callback) {

        }

        public void updateItem(String concept, String ids, Map<String, String> changedNodes, String language,
                AsyncCallback<String> callback) {

        }

        public void formatValue(FormatModel model, AsyncCallback<String> callback) {

        }

        public void getEntityModel(String concept, String language, AsyncCallback<EntityModel> callback) {

        }

        public void createDefaultItemNodeModel(ViewBean viewBean, Map<String, String> initDataMap, String language, AsyncCallback<ItemNodeModel> callback) {

        }

        public void createSubItemNodeModel(ViewBean viewBean, String xml, String typePath, String contextPath, String realType,
                String language, AsyncCallback<ItemNodeModel> callback) {
        }

    }

    @Override
    public String getModuleName() {
        // GWTTestCase Required
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }
}
