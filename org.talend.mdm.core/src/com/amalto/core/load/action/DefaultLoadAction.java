/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load.action;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.load.io.XMLStreamTokenizer;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import javax.servlet.http.HttpServletRequest;

/**
 *
 */
public class DefaultLoadAction implements LoadAction {
    private final String dataClusterName;
    private final String typeName;
    private final String dataModelName;
    private final boolean needValidate;
    private final boolean needAutoGenPK;

    public DefaultLoadAction(String dataClusterName, String typeName,
                             String dataModelName, boolean needValidate, boolean needAutoGenPK) {
        this.dataClusterName = dataClusterName;
        this.typeName = typeName;
        this.dataModelName = dataModelName;
        this.needValidate = needValidate;
        this.needAutoGenPK = needAutoGenPK;
    }

    public boolean supportValidation() {
        return true;
    }

    public boolean supportAutoGenPK() {
        return true;
    }

    public void load(HttpServletRequest request, XSDKey keyMetadata, XmlServerSLWrapperLocal server) throws Exception {
        XMLStreamTokenizer xmlStreamTokenizer = new XMLStreamTokenizer(request.getInputStream());

        while (xmlStreamTokenizer.hasMoreElements()) {
            String xmlData = xmlStreamTokenizer.nextElement();

            DataModelPOJO dataModel = Util.getDataModelCtrlLocal()
                    .getDataModel(new DataModelPOJOPK(dataModelName));
            String schemaString = dataModel.getSchema();

            if (xmlData == null || xmlData.trim().length() == 0) {
                return;
            }

            Element root = Util.parse(xmlData).getDocumentElement();

            // get key values
            // support UUID or auto-increase temporarily
            String[] ids = null;
            if (!needAutoGenPK) {
                ids = Util.getKeyValuesFromItem(root, keyMetadata);
            } else {
                if (Util.getUUIDNodes(schemaString, typeName).size() > 0) { // check
                    // uuid
                    // key
                    // exists
                    Node n = Util.processUUID(root, schemaString, dataClusterName, typeName);
                    // get key values
                    ids = Util.getKeyValuesFromItem((Element) n, keyMetadata);
                    // reset item projection
                    xmlData = Util.nodeToString(n);
                }
            }

            DataClusterPOJOPK clusterPK = new DataClusterPOJOPK(dataClusterName);
            ItemPOJO itemPOJO = new ItemPOJO(clusterPK, typeName, ids,
                    System.currentTimeMillis(), xmlData);

            // validate
            if (schemaString != null && needValidate) {
                Util.validate(itemPOJO.getProjection(), schemaString);
            }

            if (dataModelName != null && dataModelName.length() > 0) {
                itemPOJO.setDataModelName(dataModelName);
            }

            // When doing bulk load, disable cache
            itemPOJO.store(false);
        }
    }

    public void endLoad() {
        // Nothing to do
    }
}
