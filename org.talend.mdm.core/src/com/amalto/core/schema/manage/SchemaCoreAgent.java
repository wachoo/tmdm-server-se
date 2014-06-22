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
package com.amalto.core.schema.manage;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.commmon.util.datamodel.management.DataModelBean;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.commmon.util.datamodel.management.SchemaManager;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.util.EntityNotFoundException;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;

public class SchemaCoreAgent extends SchemaManager {

    private static SchemaCoreAgent agent;
    
    private static Map<String, Node> hiddenNode = new HashMap<String, Node>();

    private SchemaCoreAgent() {
    }

    public static SchemaCoreAgent getInstance() {
        if (agent == null) {
            agent = new SchemaCoreAgent();
        }
        return agent;
    }

    protected void addToPool(DataModelID dataModelID, DataModelBean dataModelBean) {
        DataModelCorePool.getUniqueInstance().put(dataModelID, dataModelBean);
    }

    protected boolean existInPool(DataModelID dataModelID) {
        DataModelBean dataModelBean = DataModelCorePool.getUniqueInstance().get(dataModelID);
        return !(dataModelBean == null);
    }

    protected void removeFromPool(DataModelID dataModelID) {
        DataModelCorePool.getUniqueInstance().remove(dataModelID);
    }

    protected DataModelBean getFromPool(DataModelID dataModelID) throws Exception {
        DataModelBean dataModelBean = DataModelCorePool.getUniqueInstance().get(dataModelID);
        if (dataModelBean == null) {
            // reload it
            try {
                DataModelPOJO dataModelPOJO = ObjectPOJO.load(dataModelID.getRevisionID(), DataModelPOJO.class, new ObjectPOJOPK(
                        dataModelID.getUniqueID()));
                if (dataModelPOJO == null) {
                    throw new EntityNotFoundException(
                            "Unable to get the DataModel '" + dataModelID.getUniqueID() + "' in revision '" + dataModelID.getRevisionID() + "'."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
                }
                String dataModelSchema = dataModelPOJO.getSchema();
                dataModelBean = updateToDatamodelPool(dataModelID.getRevisionID(), dataModelID.getUniqueID(), dataModelSchema);
            } catch (Exception e) {
                throw e;
            }
        }
        return dataModelBean;
    }

    /**
     * Get business concept for current user".
     */
    public BusinessConcept getBusinessConceptForCurrentUser(String conceptName) throws Exception {
        return super.getBusinessConcept(conceptName, getUserDatamodelID());
    }

    private DataModelID getUserDatamodelID() throws Exception {
        String dataModel = null;
        // load it from db directly
        Element userProvision = Util.getLoginProvisioningFromDB();
        if (userProvision != null) {
            dataModel = Util.getUserDataModel(userProvision);
        }
        // if still fail, return null (keep silence)
        if (dataModel == null) {
            return null;
        }
        String revision = LocalUser.getLocalUser().getUniverse().getName();
        if (revision != null && (revision.equals("[HEAD]") || revision.equals("HEAD") || revision.equals(""))) { //$NON-NLS-1$//$NON-NLS-2$ //$NON-NLS-3$
            revision = null;
        }
        return new DataModelID(dataModel, revision);
    }

    public void analyzeAccessRights(DataModelID dataModelID, String conceptName, AppinfoSourceHolder appinfoSourceHolder)
            throws Exception {
        AccessRightAnalyzer accessRightAnalyzer = new AccessRightAnalyzer(getBusinessConcept(conceptName, dataModelID),
                appinfoSourceHolder);
        accessRightAnalyzer.calculate();
    }

    public Document executeHideCheck(String itemContent, HashSet<String> roles, AppinfoSourceHolder appinfoSourceHolder, boolean fill)
            throws Exception {
        if (itemContent == null || itemContent.length() == 0) {
            return null;
        }
        Document itemDocument = Util.parse(itemContent);
        for (String role : roles) {
            if (fill) {
                executeFillCheck(itemDocument, role, appinfoSourceHolder);
            } else {
                executeHideCheck(itemDocument, role, appinfoSourceHolder);
            }
        }
        return itemDocument;
    }
    
    public void executeHideCheck(Document itemDocument, String role, AppinfoSourceHolder appinfoSourceHolder) throws Exception {
        List<String> result = appinfoSourceHolder.getResult(BusinessConcept.APPINFO_X_HIDE, role);
        for (String xpath : result) {
            NodeList nodeList = Util.getNodeList(itemDocument, xpath);
            if (nodeList.getLength() > 0) {
                hiddenNode.put(xpath, nodeList.item(0));
            }
            Util.removeXpathFromDocument(itemDocument, xpath, true);
        }
    }

    public void executeFillCheck(Document itemDocument, String role, AppinfoSourceHolder appinfoSourceHolder) throws Exception {
    	List<String> result = appinfoSourceHolder.getResult(BusinessConcept.APPINFO_X_HIDE, role);
        for (String xpath : result) {
            Node node = hiddenNode.get(xpath);
            if (node != null) {
                String parentXpath = xpath.substring(0, xpath.lastIndexOf("/")); //$NON-NLS-1$
                NodeList parentNodeList = Util.getNodeList(itemDocument, parentXpath);
                if (parentNodeList != null && parentNodeList.item(0) != null &&
                        Util.getNodeList(itemDocument, xpath).getLength() == 0) {
                    Node nd = itemDocument.importNode(node, true);
                    parentNodeList.item(0).appendChild(nd);
                }
            }
        }
    }
}
