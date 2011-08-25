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
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.util.Util;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class SchemaCoreAgent extends SchemaManager {

    private static SchemaCoreAgent agent;
    
    private static Map<String, Node> hiddenNode = new HashMap<String, Node>();

    /**
     * DOC HSHU SchemaCoreAgent constructor comment.
     */
    private SchemaCoreAgent() {

    }

    public static SchemaCoreAgent getInstance() {

        if (agent == null)

            agent = new SchemaCoreAgent();

        return agent;

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#addToPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID, org.talend.mdm.commmon.util.datamodel.management.DataModelBean)
     */
    @Override
    protected void addToPool(DataModelID dataModelID, DataModelBean dataModelBean) {
        DataModelCorePool.getUniqueInstance().put(dataModelID, dataModelBean);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#existInPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected boolean existInPool(DataModelID dataModelID) {
        DataModelBean dataModelBean = DataModelCorePool.getUniqueInstance().get(dataModelID);
        return !(dataModelBean == null);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#removeFromPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected void removeFromPool(DataModelID dataModelID) {
        DataModelBean dataModelBean = DataModelCorePool.getUniqueInstance().remove(dataModelID);
        dataModelBean = null;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.talend.mdm.commmon.util.datamodel.management.SchemaManager#getFromPool(org.talend.mdm.commmon.util.datamodel
     * .management.DataModelID)
     */
    @Override
    protected DataModelBean getFromPool(DataModelID dataModelID) throws Exception {
        DataModelBean dataModelBean = DataModelCorePool.getUniqueInstance().get(dataModelID);
        if (dataModelBean == null) {

            // reload it
            try {
                DataModelPOJO dataModelPOJO = ObjectPOJO.load(dataModelID.getRevisionID(), DataModelPOJO.class, new ObjectPOJOPK(
                        dataModelID.getUniqueID()));
                String dataModelSchema = dataModelPOJO.getSchema();

                dataModelBean = updateToDatamodelPool(dataModelID.getRevisionID(), dataModelID.getUniqueID(), dataModelSchema);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        return dataModelBean;
    }

    /**
     * DOC HSHU Comment method "analyzeAccessRights".
     * 
     * @throws Exception
     */
    public void analyzeAccessRights(DataModelID dataModelID, String conceptName, AppinfoSourceHolder appinfoSourceHolder)
            throws Exception {

        AccessRightAnalyzer accessRightAnalyzer = new AccessRightAnalyzer(getBusinessConcept(conceptName, dataModelID),
                appinfoSourceHolder);

        accessRightAnalyzer.calculate();

    }

    /**
     * DOC HSHU Comment method "executeHideCheck".
     * 
     * @param itemContent
     * @param roles
     * @param appinfoSourceHolder
     * @return
     * @throws Exception
     */
    public Document executeHideCheck(String itemContent, HashSet<String> roles, AppinfoSourceHolder appinfoSourceHolder, boolean fill)
            throws Exception {
        if (itemContent == null || itemContent.length() == 0)
            return null;

        Document itemDocument = Util.parse(itemContent);
        for (Iterator<String> iterator = roles.iterator(); iterator.hasNext();) {
            String role = iterator.next();
            if(fill) {
            	executeFillCheck(itemDocument, role, appinfoSourceHolder);
            } else {
            	executeHideCheck(itemDocument, role, appinfoSourceHolder);
            }
        }
        return itemDocument;
    }
    
    /**
     * DOC HSHU Comment method "executeHideCheck".
     * 
     * @param itemDocument
     * @param role
     * @param appinfoSourceHolder
     * @throws Exception
     */
    public void executeHideCheck(Document itemDocument, String role, AppinfoSourceHolder appinfoSourceHolder) throws Exception {
        List<String> result = appinfoSourceHolder.getResult(BusinessConcept.APPINFO_X_HIDE, role);

        for (Iterator<String> iterator = result.iterator(); iterator.hasNext();) {
            String xpath = iterator.next();
            NodeList nodeList = Util.getNodeList(itemDocument, xpath);
            if(nodeList.getLength() > 0) {
            	hiddenNode.put(xpath, nodeList.item(0));
            }
            Util.removeXpathFromDocument(itemDocument, xpath, true);
        }

    }

    public void executeFillCheck(Document itemDocument, String role, AppinfoSourceHolder appinfoSourceHolder) throws Exception {
    	List<String> result = appinfoSourceHolder.getResult(BusinessConcept.APPINFO_X_HIDE, role);

        for (Iterator<String> iterator = result.iterator(); iterator.hasNext();) {
            String xpath = iterator.next();
            Node node = hiddenNode.get(xpath);
            
            if(node != null) {
            	String parentXpath = xpath.substring(0, xpath.lastIndexOf("/"));
            	NodeList parentNodeList = Util.getNodeList(itemDocument, parentXpath);
            	
            	if(parentNodeList != null && parentNodeList.item(0) != null && 
            		Util.getNodeList(itemDocument, xpath).getLength() == 0) 
            	{
            		Node nd = itemDocument.importNode(node, true);
            		parentNodeList.item(0).appendChild(nd);
            	}
            }
        }
    }
}
