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
package com.amalto.core.migration.tasks;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.amalto.core.migration.AbstractDataModelMigrationTask;
import com.amalto.core.util.Util;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class UpdateSearchTemplateModel extends AbstractDataModelMigrationTask {

    /*
     * (non-Jsdoc)
     * 
     * @see com.amalto.core.migration.AbstractDataModelMigrationTask#getDataModel()
     */
    protected String getDataModel() {
        return "SearchTemplate"; //$NON-NLS-1$
    }

    /*
     * (non-Jsdoc)
     * 
     * @see com.amalto.core.migration.AbstractDataModelMigrationTask#updateSchema(org.w3c.dom.Document,
     * org.w3c.dom.Document)
     */
    protected void updateSchema(Document doc) throws Exception {
        
        if (Util.getNodeList(doc, "//xsd:element[@name='BrowseItem']//xsd:element[@name='SearchCriteria']").getLength() == 0) {//$NON-NLS-1$
            NodeList checkList = Util.getNodeList(doc, "//xsd:element[@name='BrowseItem']//xsd:element[@name='WhereCriteria']");//$NON-NLS-1$
            if (checkList.getLength() > 0) {

                Node whereCriteriaNode = checkList.item(0);
                Element searchCriteriaElem = doc.createElement("xsd:element");//$NON-NLS-1$
                searchCriteriaElem.setAttribute("name", "SearchCriteria");//$NON-NLS-1$ //$NON-NLS-2$
                searchCriteriaElem.setAttribute("maxOccurs", "1");//$NON-NLS-1$ //$NON-NLS-2$
                searchCriteriaElem.setAttribute("minOccurs", "0");//$NON-NLS-1$ //$NON-NLS-2$
                searchCriteriaElem.setAttribute("type", "xsd:string");//$NON-NLS-1$ //$NON-NLS-2$
                searchCriteriaElem.setAttribute("nillable", "false");//$NON-NLS-1$ //$NON-NLS-2$
                whereCriteriaNode.getParentNode().appendChild(searchCriteriaElem);

            }
        }

    }

}
