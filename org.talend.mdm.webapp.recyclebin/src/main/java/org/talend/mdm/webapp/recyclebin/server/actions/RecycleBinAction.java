// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.recyclebin.server.actions;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.recyclebin.client.RecycleBinService;
import org.talend.mdm.webapp.recyclebin.shared.ItemsTrashItem;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.UpdateReportItem;
import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDroppedItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSDroppedItemPKArray;
import com.amalto.webapp.util.webservices.WSFindAllDroppedItemsPKs;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSLoadDroppedItem;
import com.amalto.webapp.util.webservices.WSRecoverDroppedItem;
import com.amalto.webapp.util.webservices.WSRemoveDroppedItem;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class RecycleBinAction implements RecycleBinService {

    private static final Logger LOG = Logger.getLogger(RecycleBinAction.class);

    public PagingLoadResult<ItemsTrashItem> getTrashItems(String regex, PagingLoadConfig load) throws Exception {
        try {
            //
            if (regex == null || regex.length() == 0) {
                regex = ""; //$NON-NLS-1$
            }
            regex = regex.replaceAll("\\*", "");//$NON-NLS-1$//$NON-NLS-2$
            regex = ".*" + regex + ".*";//$NON-NLS-1$//$NON-NLS-2$

            List<ItemsTrashItem> li = new ArrayList<ItemsTrashItem>();

            WSDroppedItemPKArray pks = Util.getPort().findAllDroppedItemsPKs(new WSFindAllDroppedItemsPKs(regex));
            WSDroppedItemPK[] items = pks.getWsDroppedItemPK();

            for (WSDroppedItemPK pk : items) {
                WSDroppedItem wsitem = Util.getPort().loadDroppedItem(new WSLoadDroppedItem(pk));
                ItemsTrashItem item = new ItemsTrashItem();
                item = WS2POJO(wsitem);
                li.add(item);
            }
            List<ItemsTrashItem> sublist = new ArrayList<ItemsTrashItem>();
            int start = load.getOffset(), limit = load.getLimit();
            if (li.size() > 0) {
                start = start < li.size() ? start : li.size() - 1;
                int end = li.size() < (start + limit) ? li.size() - 1 : (start + limit - 1);
                for (int i = start; i < end + 1; i++)
                    sublist.add(li.get(i));
            }
            return new BasePagingLoadResult<ItemsTrashItem>(sublist, load.getOffset(), li.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new Exception(e.getClass().getName() + ": " + e.getMessage()); //$NON-NLS-1$
        }

    }

    private ItemsTrashItem WS2POJO(WSDroppedItem item) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//$NON-NLS-1$
        ItemsTrashItem pojo = new ItemsTrashItem(item.getConceptName(), Util.joinStrings(item.getIds(), "."), df.format(new Date(//$NON-NLS-1$
                item.getInsertionTime())), item.getInsertionUserName(), item.getWsDataClusterPK().getPk(), item.getPartPath(),
                item.getProjection(), item.getRevisionID(), item.getUniqueId());
        return pojo;
    }

    public boolean isEntityPhysicalDeletable(String conceptName) throws Exception {
        return !SchemaWebAgent.getInstance().isEntityDenyPhysicalDeletable(conceptName);
    }

    public void removeDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids)
            throws Exception {
        try {
            // WSDroppedItemPK
            String[] ids1 = ids.split("\\.");//$NON-NLS-1$
            WSDataClusterPK wddcpk = new WSDataClusterPK(itemPk);
            WSItemPK wdipk = new WSItemPK(wddcpk, conceptName, ids1);
            WSDroppedItemPK wddipk = new WSDroppedItemPK(wdipk, partPath, revisionId);
            WSRemoveDroppedItem wsrdi = new WSRemoveDroppedItem(wddipk);
            Util.getPort().removeDroppedItem(wsrdi);

            String xml = createUpdateReport(ids1, conceptName, "PHYSICAL_DELETE", null); //$NON-NLS-1$
            Util.persistentUpdateReport(xml, true);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new Exception(e.getClass().getName() + ": " + e.getMessage()); //$NON-NLS-1$
        }

    }

    public void recoverDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids)
            throws Exception {
        try {

            String[] ids1 = ids.split("\\.");//$NON-NLS-1$
            WSDataClusterPK wddcpk = new WSDataClusterPK(itemPk);
            WSItemPK wdipk = new WSItemPK(wddcpk, conceptName, ids1);
            WSDroppedItemPK wsdipk = new WSDroppedItemPK(wdipk, partPath, revisionId);
            WSRecoverDroppedItem wsrdi = new WSRecoverDroppedItem(wsdipk);
            Util.getPort().recoverDroppedItem(wsrdi);

            // put the restore into updatereport archive
            String xml = createUpdateReport(ids1, conceptName, "RESTORED", null); //$NON-NLS-1$
            Util.persistentUpdateReport(xml, true);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new Exception(e.getClass().getName() + ": " + e.getMessage()); //$NON-NLS-1$
        }

    }

    // TODO use session instead
    public String getCurrentDataModel() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getModel();
    }

    public String getCurrentDataCluster() throws Exception {
        Configuration config = Configuration.getConfiguration();
        return config.getCluster();
    }

    // TODO this is used in many places, refactor it in core project
    public String createUpdateReport(String[] ids, String concept, String operationType,
            HashMap<String, UpdateReportItem> updatedPath) throws Exception {

        String revisionId = null;
        String dataModelPK = getCurrentDataModel() == null ? "" : getCurrentDataModel();//$NON-NLS-1$ 
        String dataClusterPK = getCurrentDataCluster() == null ? "" : getCurrentDataCluster();//$NON-NLS-1$ 

        String username = com.amalto.webapp.core.util.Util.getLoginUserName();
        String universename = com.amalto.webapp.core.util.Util.getLoginUniverse();
        if (universename != null && universename.length() > 0)
            revisionId = com.amalto.webapp.core.util.Util.getRevisionIdFromUniverse(universename, concept);

        StringBuilder keyBuilder = new StringBuilder();
        if (ids != null) {
            for (int i = 0; i < ids.length; i++) {
                keyBuilder.append(ids[i]);
                if (i != ids.length - 1)
                    keyBuilder.append("."); //$NON-NLS-1$
            }
        }
        String key = keyBuilder.length() == 0 ? "null" : keyBuilder.toString(); //$NON-NLS-1$

        StringBuilder sb = new StringBuilder();
        // TODO what is StringEscapeUtils.escapeXml used for
        sb.append("<Update><UserName>").append(username).append("</UserName><Source>genericUI</Source><TimeInMillis>") //$NON-NLS-1$ //$NON-NLS-2$ 
                .append(System.currentTimeMillis()).append("</TimeInMillis><OperationType>") //$NON-NLS-1$
                .append(operationType).append("</OperationType><RevisionID>").append(revisionId) //$NON-NLS-1$
                .append("</RevisionID><DataCluster>").append(dataClusterPK).append("</DataCluster><DataModel>") //$NON-NLS-1$ //$NON-NLS-2$ 
                .append(dataModelPK).append("</DataModel><Concept>").append(concept) //$NON-NLS-1$
                .append("</Concept><Key>").append(key).append("</Key>"); //$NON-NLS-1$ //$NON-NLS-2$ 

        if ("UPDATE".equals(operationType)) { //$NON-NLS-1$
            Collection<UpdateReportItem> list = updatedPath.values();
            boolean isUpdate = false;
            for (UpdateReportItem item : list) {
                String oldValue = item.getOldValue() == null ? "" : item.getOldValue();//$NON-NLS-1$
                String newValue = item.getNewValue() == null ? "" : item.getNewValue();//$NON-NLS-1$
                if (newValue.equals(oldValue))
                    continue;
                sb.append("<Item>   <path>").append(item.getPath()).append("</path>   <oldValue>")//$NON-NLS-1$ //$NON-NLS-2$
                        .append(oldValue).append("</oldValue>   <newValue>")//$NON-NLS-1$
                        .append(newValue).append("</newValue></Item>");//$NON-NLS-1$
                isUpdate = true;
            }
            if (!isUpdate)
                return null;
        }
        sb.append("</Update>");//$NON-NLS-1$
        return sb.toString();
    }
}
