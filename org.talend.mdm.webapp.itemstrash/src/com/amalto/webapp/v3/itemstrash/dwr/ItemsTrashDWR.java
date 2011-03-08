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
package com.amalto.webapp.v3.itemstrash.dwr;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;
import org.jboss.dom4j.DocumentException;
import org.jboss.dom4j.io.SAXReader;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.dmagent.SchemaWebAgent;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDroppedItem;
import com.amalto.webapp.util.webservices.WSDroppedItemPK;
import com.amalto.webapp.util.webservices.WSDroppedItemPKArray;
import com.amalto.webapp.util.webservices.WSFindAllDroppedItemsPKs;
import com.amalto.webapp.util.webservices.WSGetItem;
import com.amalto.webapp.util.webservices.WSItemPK;
import com.amalto.webapp.util.webservices.WSLoadDroppedItem;
import com.amalto.webapp.util.webservices.WSRecoverDroppedItem;
import com.amalto.webapp.util.webservices.WSRemoveDroppedItem;
import com.amalto.webapp.v3.itemstrash.bean.ItemsTrashItem;

public class ItemsTrashDWR {

    private Logger logger = org.apache.log4j.Logger.getLogger(this.getClass());

    public ItemsTrashDWR() {
        super();
    }

    // Date date =new Date(2009,6,4);
    // ItemsTrashInstances iti = new ItemsTrashInstances();
    public ListRange getTrashItems(int start, int limit, String sort, String dir, String regex) throws Exception {
        logger.debug("getTrashItems() regex== " + regex + "start==" + start + "limit==" + limit);
        try {
            //
            if (regex == null || regex.length() == 0) {
                regex = "";
            }
            regex = regex.replaceAll("\\*", "");
            regex = ".*" + regex + ".*";

            List<ItemsTrashItem> li = new ArrayList<ItemsTrashItem>();

            WSDroppedItemPKArray pks = Util.getPort().findAllDroppedItemsPKs(new WSFindAllDroppedItemsPKs(regex));
            WSDroppedItemPK[] items = pks.getWsDroppedItemPK();

            for (WSDroppedItemPK pk : items) {
                WSDroppedItem wsitem = Util.getPort().loadDroppedItem(new WSLoadDroppedItem(pk));
                ItemsTrashItem item = new ItemsTrashItem();
                item = item.WS2POJO(wsitem);
                li.add(item);
            }
            List<ItemsTrashItem> sublist = li;
            if (li.size() > 0) {
                start = start < li.size() ? start : li.size() - 1;
                int end = li.size() < (start + limit) ? li.size() - 1 : (start + limit - 1);
                sublist = li.subList(start, end + 1);
            }
            ListRange listRange = new ListRange();
            listRange.setData(sublist.toArray(new ItemsTrashItem[sublist.size()]));
            listRange.setTotalSize(li.size());
            return listRange;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }

    }
    public boolean isEntityPhysicalDeletable(String conceptName) throws Exception{
    	return SchemaWebAgent.getInstance().isEntityPhysicalDeletable(conceptName);
    }
    public void removeDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids)
            throws Exception {
        try {
            // WSDroppedItemPK
            String[] ids1 = ids.split("\\.");
            WSDataClusterPK wddcpk = new WSDataClusterPK(itemPk);
            WSItemPK wdipk = new WSItemPK(wddcpk, conceptName, ids1);
            WSDroppedItemPK wddipk = new WSDroppedItemPK(wdipk, partPath, revisionId);
            WSRemoveDroppedItem wsrdi = new WSRemoveDroppedItem(wddipk);
            WSDroppedItemPK wdipkd = Util.getPort().removeDroppedItem(wsrdi);

            String xml = Util.createUpdateReport(ids1, conceptName, "PHYSICAL_DELETE", null); //$NON-NLS-1$
            Util.persistentUpdateReport(xml, true);
            // return wdipkd;
        } catch (Exception e) {
            e.printStackTrace();

            throw new Exception(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }

    }

    public void recoverDroppedItem(String itemPk, String partPath, String revisionId, String conceptName, String ids)
            throws Exception {
        try {

            String[] ids1 = ids.split("\\.");
            WSDataClusterPK wddcpk = new WSDataClusterPK(itemPk);
            WSItemPK wdipk = new WSItemPK(wddcpk, conceptName, ids1);
            WSDroppedItemPK wsdipk = new WSDroppedItemPK(wdipk, partPath, revisionId);
            WSRecoverDroppedItem wsrdi = new WSRecoverDroppedItem(wsdipk);
            WSItemPK wsipk = Util.getPort().recoverDroppedItem(wsrdi);

            // put the restore into updatereport archive
            String xml = Util.createUpdateReport(ids1, conceptName, "RESTORED", null); //$NON-NLS-1$
            Util.persistentUpdateReport(xml, true);
            // return wsipk;
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getClass().getName() + ": " + e.getLocalizedMessage());
        }

    }

    public String getUri(String concept, String[] ids) {
        Configuration config;
        String uri = "";
        try {
            config = Configuration.getInstance();
            String dataClusterPK = config.getCluster();
            String content = "";
            WSItemPK wsItem = new WSItemPK(new WSDataClusterPK(dataClusterPK), concept, ids);
            if (wsItem != null)
                content = Util.getPort().getItem(new WSGetItem(wsItem)).getContent();
            for (Iterator iterator = parsXMLString(content).getRootElement().nodeIterator(); iterator.hasNext();) {
                org.jboss.dom4j.Node node = (org.jboss.dom4j.Node) iterator.next();
                if (node.getStringValue().startsWith("/imageserver")) {
                    uri = node.getStringValue();
                    break;
                }
            }
            System.out.println(uri);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return uri;
    }

    private org.jboss.dom4j.Document parsXMLString(String xmlString) {
        SAXReader saxReader = new SAXReader();
        org.jboss.dom4j.Document document = null;
        try {
            document = saxReader.read(new StringReader(xmlString));
        } catch (DocumentException e) {
            e.printStackTrace();
            return null;
        }
        return document;

    }
}
