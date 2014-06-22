// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.MetadataRepository;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.base.client.model.BasePagingLoadConfigImpl;
import org.talend.mdm.webapp.base.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.recyclebin.client.RecycleBinService;
import org.talend.mdm.webapp.recyclebin.shared.ItemsTrashItem;
import org.talend.mdm.webapp.recyclebin.shared.NoPermissionException;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Messages;
import com.amalto.core.util.MessagesFactory;
import com.amalto.core.util.SynchronizedNow;
import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.util.DataModelAccessor;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.util.webservices.WSConceptKey;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSDroppedItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSDroppedItemPKArray;
import com.amalto.webapp.util.webservices.WSExistsItem;
import com.amalto.webapp.util.webservices.WSFindAllDroppedItemsPKs;
import com.amalto.webapp.util.webservices.WSGetBusinessConceptKey;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSLoadDroppedItem;
import com.amalto.webapp.util.webservices.WSRecoverDroppedItem;
import com.amalto.webapp.util.webservices.WSRemoveDroppedItem;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class RecycleBinAction implements RecycleBinService {

    private static final Logger LOG = Logger.getLogger(RecycleBinAction.class);

    private static final Messages MESSAGES = MessagesFactory.getMessages(
            "org.talend.mdm.webapp.recyclebin.client.i18n.RecycleBinMessages", RecycleBinAction.class.getClassLoader()); //$NON-NLS-1$

    private final static SynchronizedNow now = new SynchronizedNow();

    @Override
    public ItemBasePageLoadResult<ItemsTrashItem> getTrashItems(String regex, BasePagingLoadConfigImpl load)
            throws ServiceException {
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
            Map<String, MetadataRepository> repositoryMap = new HashMap<String, MetadataRepository>();

            for (WSDroppedItemPK pk : items) {
                WSDroppedItem wsitem = Util.getPort().loadDroppedItem(new WSLoadDroppedItem(pk));

                String conceptName = wsitem.getConceptName();
                String conceptXML = wsitem.getProjection();
                String modelName = getModelNameFromConceptXML(conceptXML);

                if (modelName != null) {
                    // For enterprise version we check the user roles first, if one user don't have read permission on a
                    // DataModel Object, then ignore it
                    if (Webapp.INSTANCE.isEnterpriseVersion()
                            && !LocalUser.getLocalUser().userCanRead(DataModelPOJO.class, modelName)) {
                        continue;
                    }

                    String modelXSD = DataModelAccessor.getInstance().getDataModelXSD(modelName);
                    if (modelXSD != null && modelXSD.trim().length() > 0) {
                        if (!repositoryMap.containsKey(modelName)) {
                            MetadataRepository repository = new MetadataRepository();
                            InputStream is = new ByteArrayInputStream(modelXSD.getBytes("UTF-8")); //$NON-NLS-1$
                            repository.load(is);
                            repositoryMap.put(modelName, repository);
                        }
                    }

                    if (!Webapp.INSTANCE.isEnterpriseVersion()
                            || (DataModelAccessor.getInstance().checkReadAccess(modelName, conceptName))) {
                        ItemsTrashItem item = new ItemsTrashItem();
                        item = WS2POJO(wsitem, repositoryMap.get(modelName), (String) load.get("language")); //$NON-NLS-1$
                        li.add(item);

                    }
                }
            }
            List<ItemsTrashItem> sublist = new ArrayList<ItemsTrashItem>();
            int start = load.getOffset(), limit = load.getLimit();
            if (li.size() > 0) {
                start = start < li.size() ? start : li.size() - 1;
                int end = li.size() < (start + limit) ? li.size() - 1 : (start + limit - 1);
                for (int i = start; i < end + 1; i++) {
                    sublist.add(li.get(i));
                }
            }
            return new ItemBasePageLoadResult<ItemsTrashItem>(sublist, load.getOffset(), li.size());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }

    }

    private static String getModelNameFromConceptXML(String conceptXML) {
        String result = null;

        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(conceptXML));

            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("dmn"); //$NON-NLS-1$
            if (nodes.getLength() > 0) {
                Element element = (Element) nodes.item(0);
                Node child = element.getFirstChild();
                if (child instanceof CharacterData) {
                    CharacterData cd = (CharacterData) child;
                    result = cd.getData();
                }
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }

        return result;
    }

    private ItemsTrashItem WS2POJO(WSDroppedItem item, MetadataRepository repository, String language) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");//$NON-NLS-1$
        String projection = item.getProjection();
        String[] values = org.talend.mdm.webapp.recyclebin.server.actions.Util.getItemNameByProjection(item.getConceptName(),
                projection, repository, language);
        ItemsTrashItem pojo = new ItemsTrashItem(item.getConceptName(), values[1],
                Util.joinStrings(item.getIds(), "."), values[0] != null ? values[0] : "", df.format(new Date(//$NON-NLS-1$ //$NON-NLS-2$
                        item.getInsertionTime())), item.getInsertionUserName(), item.getWsDataClusterPK().getPk(),
                item.getPartPath(), item.getProjection(), item.getRevisionID(), item.getUniqueId());
        return pojo;
    }

    @Override
    public boolean isEntityPhysicalDeletable(String conceptName) throws ServiceException {
        try {
            boolean isDeletable = !SchemaWebAgent.getInstance().isEntityDenyPhysicalDeletable(conceptName);
            if (!isDeletable) {
                throw new NoPermissionException();
            }
            return isDeletable;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    // FIXME Code duplication
    @Override
    public String removeDroppedItem(String clusterName, String modelName, String partPath, String revisionId, String conceptName,
            String ids, String language) throws ServiceException {
        try {
            Locale locale = new Locale(language);
            // WSDroppedItemPK
            WSConceptKey key = CommonUtil.getPort().getBusinessConceptKey(
                    new WSGetBusinessConceptKey(new WSDataModelPK(modelName), conceptName));
            String[] ids1 = CommonUtil.extractIdWithDots(key.getFields(), ids);
            String outputErrorMessage = com.amalto.core.util.Util.beforeDeleting(clusterName, conceptName, ids1);

            String message = null;
            String errorCode = null;
            if (outputErrorMessage != null) {
                Document doc = Util.parse(outputErrorMessage);
                String xpath = "//report/message"; //$NON-NLS-1$
                Node errorNode = Util.getNodeList(doc, xpath).item(0);
                if (errorNode instanceof Element) {
                    Element errorElement = (Element) errorNode;
                    errorCode = errorElement.getAttribute("type"); //$NON-NLS-1$                    
                    message = errorElement.getTextContent();
                }
            }

            if (outputErrorMessage != null && !"info".equals(errorCode)) { //$NON-NLS-1$
                if (message == null || message.isEmpty()) {
                    if ("error".equals(errorCode)) { //$NON-NLS-1$
                        message = MESSAGES.getMessage(locale, "delete_process_validation_failure"); //$NON-NLS-1$
                    } else {
                        message = MESSAGES.getMessage(locale, "delete_record_failure"); //$NON-NLS-1$
                    }
                }
                throw new ServiceException(message);
            } else {
                WSDataClusterPK wddcpk = new WSDataClusterPK(clusterName);
                WSItemPK wdipk = new WSItemPK(wddcpk, conceptName, ids1);
                WSDroppedItemPK wddipk = new WSDroppedItemPK(wdipk, partPath, revisionId);
                WSRemoveDroppedItem wsrdi = new WSRemoveDroppedItem(wddipk);
                Util.getPort().removeDroppedItem(wsrdi);

                String xml = createUpdateReport(clusterName, modelName, ids1, conceptName,
                        UpdateReportPOJO.OPERATION_TYPE_PHYSICAL_DELETE);
                Util.persistentUpdateReport(xml, true);

                if (message == null || message.isEmpty()) {
                    message = MESSAGES.getMessage(locale, "delete_process_validation_success"); //$NON-NLS-1$
                }

                return "info".equals(errorCode) ? message : null; //$NON-NLS-1$
            }
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public boolean checkConflict(String clusterName, String conceptName, String id) throws ServiceException {
        try {
            String ids[] = { id };
            WSDataClusterPK wddcpk = new WSDataClusterPK(clusterName);
            return Util.getPort().existsItem(new WSExistsItem(new WSItemPK(wddcpk, conceptName, ids))).is_true();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    @Override
    public void recoverDroppedItem(String clusterName, String modelName, String partPath, String revisionId, String conceptName,
            String ids) throws ServiceException {
        try {
            if (Webapp.INSTANCE.isEnterpriseVersion()
                    && !DataModelAccessor.getInstance().checkRestoreAccess(modelName, conceptName)) {
                throw new NoPermissionException();
            }
            WSConceptKey key = CommonUtil.getPort().getBusinessConceptKey(
                    new WSGetBusinessConceptKey(new WSDataModelPK(modelName), conceptName));
            String[] ids1 = CommonUtil.extractIdWithDots(key.getFields(), ids);
            WSDataClusterPK wddcpk = new WSDataClusterPK(clusterName);
            WSItemPK wdipk = new WSItemPK(wddcpk, conceptName, ids1);
            WSDroppedItemPK wsdipk = new WSDroppedItemPK(wdipk, partPath, revisionId);
            WSRecoverDroppedItem wsrdi = new WSRecoverDroppedItem(wsdipk);
            Util.getPort().recoverDroppedItem(wsrdi);

            // put the restore into updatereport archive
            String xml = createUpdateReport(clusterName, modelName, ids1, conceptName, UpdateReportPOJO.OPERATION_TYPE_RESTORED);
            Util.persistentUpdateReport(xml, true);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    private String createUpdateReport(String dataClusterPK, String dataModelPK, String[] ids, String concept, String operationType)
            throws Exception {

        String revisionId = null;
        String username = com.amalto.webapp.core.util.Util.getLoginUserName();
        String universename = com.amalto.webapp.core.util.Util.getLoginUniverse();
        if (universename != null && universename.length() > 0) {
            revisionId = com.amalto.webapp.core.util.Util.getRevisionIdFromUniverse(universename, concept);
        }

        UpdateReportPOJO updateReportPOJO = new UpdateReportPOJO(concept, Util.joinStrings(ids, "."), operationType, //$NON-NLS-1$
                "genericUI", now.getTime(), dataClusterPK, dataModelPK, username, revisionId, null); ////$NON-NLS-1$
        return updateReportPOJO.serialize();
    }
}
