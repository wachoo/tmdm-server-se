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
package com.amalto.core.migration.tasks;

import com.amalto.core.ejb.UpdateReportPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

import com.amalto.core.migration.AbstractDataModelMigrationTask;
import com.amalto.core.objects.configurationinfo.localutil.ConfigurationHelper;
import com.amalto.core.util.Util;

/**
 * DOC fliu class global comment. Detailled comment
 */
public class UpdateActionTypeWithNewTypeStrategy extends AbstractDataModelMigrationTask {

    /*
     * (non-Jsdoc)
     * 
     * @see com.amalto.core.migration.AbstractDataModelMigrationTask#getDataModel()
     */
    protected String getDataModel() {
        return "UpdateReport";//$NON-NLS-1$
    }

    /*
     * (non-Jsdoc)
     * 
     * @see com.amalto.core.migration.AbstractDataModelMigrationTask#updateSchema(org.w3c.dom.Document)
     */
    protected void updateSchema(Document doc) throws Exception {

        NodeList nodeList = Util.getNodeList(doc, "./schema/text()"); //$NON-NLS-1$
        Document schemaRoot = null;
        if (nodeList.getLength() > 0) {
            Object obj = nodeList.item(0);
            if (obj instanceof Text) {
                String wholeSchema = ((Text) obj).getWholeText();
                schemaRoot = Util.parseXSD(wholeSchema);
            }
        }
        if (schemaRoot != null) {
            Element rootNS = Util.getRootElement("nsholder", schemaRoot.getDocumentElement().getNamespaceURI(), "xsd"); //$NON-NLS-1$
            NodeList enumList = Util.getNodeList(schemaRoot,
                    "//xsd:complexType[@name='Update']//xsd:restriction[@base='xsd:string']/xsd:enumeration[@value='DELETE']", //$NON-NLS-1$
                    rootNS.getNamespaceURI(), "xsd"); //$NON-NLS-1$
            if (enumList.getLength() > 0) {
                Node enumNode = enumList.item(0);
                Node parentNode = enumNode.getParentNode();
                Node actionTypeNode = enumNode.getAttributes().getNamedItem("value"); //$NON-NLS-1$
                actionTypeNode.setNodeValue(UpdateReportPOJO.OPERATION_TYPE_LOGICAL_DELETE);
                Element newOne = schemaRoot.createElement("xsd:enumeration"); //$NON-NLS-1$
                newOne.setAttribute("value", UpdateReportPOJO.OPERATION_TYPE_PHYSICAL_DELETE); //$NON-NLS-1$
                parentNode.appendChild(newOne);

                Element antOne = schemaRoot.createElement("xsd:enumeration"); //$NON-NLS-1$
                antOne.setAttribute("value", UpdateReportPOJO.OPERATION_TYPE_RESTORED); //$NON-NLS-1$
                parentNode.appendChild(antOne);

                String newSchema = "<schema>" + StringEscapeUtils.escapeXml(Util.nodeToString(schemaRoot)) + "</schema>"; //$NON-NLS-1$
                Node oldChild = Util.getNodeList(doc, "./schema").item(0); //$NON-NLS-1$
                Node elem = doc.importNode(Util.parse(newSchema).getDocumentElement(), true);
                doc.getDocumentElement().replaceChild(elem, oldChild);

                XmlServerSLWrapperLocal server = ConfigurationHelper.getServer();
                server.start(cluster);
                server.putDocumentFromString(Util.nodeToString(doc), getDataModel(), cluster, null);
                server.commit(cluster);
            }

        }

    }
}
