package org.talend.mdm.webapp.itemsbrowser2.client;

import java.util.List;

import org.talend.mdm.webapp.base.client.model.BasePagingLoadConfigImpl;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.client.model.ItemBean;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.QueryModel;
import org.talend.mdm.webapp.itemsbrowser2.client.model.Restriction;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

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

    ItemBasePageLoadResult<ItemBaseModel> querySearchTemplates(String view, boolean isShared, BasePagingLoadConfigImpl load);

    String deleteSearchTemplate(String id);

    String getCurrentDataModel() throws Exception;

    String getCurrentDataCluster() throws Exception;

    ItemResult saveItemBean(ItemBean item, String language);

    ItemResult deleteItemBean(ItemBean item, String language);

    List<ItemResult> deleteItemBeans(List<ItemBean> items, String language);

    ItemResult logicalDeleteItem(ItemBean item, String path);

    List<ItemResult> logicalDeleteItems(List<ItemBean> items, String path);

    AppHeader getAppHeader() throws Exception;

    ItemBean getItem(ItemBean itemBean, EntityModel entityModel) throws Exception;

    ItemBasePageLoadResult<ForeignKeyBean> getForeignKeyList(BasePagingLoadConfigImpl config, TypeModel model,
            String dataClusterPK, boolean ifFKFilter, String value);

    List<Restriction> getForeignKeyPolymTypeList(String xpathForeignKey, String language) throws Exception;

    ForeignKeyDrawer switchForeignKeyType(String targetEntityType, String xpathForeignKey, String xpathInfoForeignKey,
            String fkFilter) throws Exception;

    List<String> getMandatoryFieldList(String tableName) throws Exception;

}
