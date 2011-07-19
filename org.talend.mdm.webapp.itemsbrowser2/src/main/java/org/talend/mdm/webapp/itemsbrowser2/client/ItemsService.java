package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.Restriction;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.DownloadBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.DownloadTable;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("ItemsService")
public interface ItemsService extends RemoteService {

    public static class Util {

        public static ItemsServiceAsync getInstance() {

            return GWT.create(ItemsService.class);
        }
    }

    ItemBasePageLoadResult<ItemBean> queryItemBeans(final QueryModel config);

    ViewBean getView(String viewPk, String language);

    List<ItemBaseModel> getViewsList(String language);

    boolean isExistCriteria(String dataObjectLabel, String id);

    String saveCriteria(String viewPK, String templateName, boolean isShared, String criteriaString);

    String getCriteriaByBookmark(String bookmark);

    List<ItemBaseModel> getUserCriterias(String view);

    PagingLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, PagingLoadConfig load);

    String deleteSearchTemplate(String id);

    String getCurrentDataModel() throws Exception;

    String getCurrentDataCluster() throws Exception;

    ItemResult saveItemBean(ItemBean item);

    ItemResult deleteItemBean(ItemBean item);

    List<ItemResult> deleteItemBeans(List<ItemBean> items);

    ItemResult logicalDeleteItem(ItemBean item, String path);

    List<ItemResult> logicalDeleteItems(List<ItemBean> items, String path);

    AppHeader getAppHeader() throws Exception;

    ItemBean getItem(ItemBean itemBean, EntityModel entityModel) throws Exception;

    ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(PagingLoadConfig config, TypeModel model, String dataClusterPK,
            boolean ifFKFilter, String value);

    List<ItemBaseModel> getUploadTableNames(String value);

    Map<String, List<String>> getUploadTableDescription(String tableName) throws Exception;

    List<ItemBaseModel> deleteItemsBrowserTable(String tableName) throws Exception;

    List<Restriction> getForeignKeyPolymTypeList(String xpathForeignKey, String language) throws Exception;

    ForeignKeyDrawer switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
            String fkFilter) throws Exception;

    boolean addNewTable(String concept, String[] fields, String[] keys) throws Exception;

    void deleteDocument(String concept, DownloadBaseModel model) throws Exception;

    void updateDocument(String concept, List<DownloadBaseModel> models) throws Exception;

    PagingLoadResult<DownloadBaseModel> getDownloadTableContent(String tableName, PagingLoadConfig load) throws Exception;

    DownloadTable getDownloadTable(String tableName) throws Exception;

}
