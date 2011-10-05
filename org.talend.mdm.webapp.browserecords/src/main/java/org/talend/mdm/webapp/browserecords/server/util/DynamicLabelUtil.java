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
package org.talend.mdm.webapp.browserecords.server.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.Element;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DynamicLabelUtil {

    /**
     * 
     * @return
     */
    public static void getDynamicLabel(Document parsedDocument, ItemNodeModel itemModel, Map<String, TypeModel> metaDataTypes,
            String language) {
        try {
            String xpath = itemModel.getBindingPath();
            TypeModel typeModel = metaDataTypes.get(xpath);
            String label = typeModel.getLabel(language);
            if (org.talend.mdm.webapp.base.server.util.DynamicLabelUtil.isDynamicLabel(label)) {
                label = replaceForeignPath(itemModel.getBindingPath(), label, parsedDocument);
                String stylesheet = org.talend.mdm.webapp.base.server.util.DynamicLabelUtil.genStyle(itemModel.getBindingPath(),
                        label);
                String dynamicLB = org.talend.mdm.webapp.base.server.util.DynamicLabelUtil
                        .getParsedLabel(org.talend.mdm.webapp.base.server.util.XmlUtil.styleDocument(parsedDocument,
                                stylesheet));
                // @temp yguo, set the properties to itemmodel
                itemModel.setDynamicLabel(dynamicLB);
            }

            if (itemModel.getChildCount() == 0) {
                return;
            } else {
                for (int i = 0; i < itemModel.getChildCount(); i++) {
                    getDynamicLabel(parsedDocument, (ItemNodeModel) itemModel.getChild(i), metaDataTypes, language);
                }
            }
        } catch (Exception ex) {
        }
    }

    private static String replaceForeignPath(String basePath, String dynamicLabel, org.dom4j.Document doc) throws Exception {
        Pattern pattern = Pattern.compile("\\{.*?\\}");//$NON-NLS-1$
        Matcher matcher = pattern.matcher(dynamicLabel);
        List<String> dynamicPathes = new ArrayList<String>();
        while (matcher.find()) {
            dynamicPathes.add(matcher.group().replaceAll("^\\{", "").replaceAll("\\}$", ""));//$NON-NLS-1$ //$NON-NLS-2$
        }

        // Configuration config = Configuration.getInstance();
        // String dataModelPK = config.getModel();
        // String xsd = Util.getPort().getDataModel(new WSGetDataModel(new WSDataModelPK(dataModelPK))).getXsdSchema();
        // Map<String, XSElementDecl> map = com.amalto.core.util.Util.getConceptMap(xsd);
        // Map<String, XSType> typeMap = com.amalto.core.util.Util.getConceptTypeMap(xsd);
        basePath = basePath.startsWith("/") ? basePath.substring(1) : basePath; //$NON-NLS-1$
        //        XSElementDecl xsed = map.get(basePath.split("/")[0]); //$NON-NLS-1$

        for (String dyPath : dynamicPathes) {
            Element baseEl = (Element) doc.selectSingleNode(basePath);
            try {
                List els = (List) baseEl.selectNodes(dyPath);//$NON-NLS-1$
                if (els == null)
                    continue;
                String multiValue = "";
                if (els.size() > 0) {

                    for (int i = 0; i < els.size(); i++) {
                        List<org.dom4j.Element> pathNodes = getPathNode((org.dom4j.Element) els.get(i));
                        String key = ((org.dom4j.Element) els.get(i)).getStringValue();
                        // Object[] fkObj = getForeign(xsed, pathNodes, 0, typeMap);
                        // if (fkObj != null && ((List<String>)fkObj[1]).size() > 0) {
                        // String foreignkey = (String) fkObj[0];
                        // List<String> fkInfos = (List<String>) fkObj[1];

                        // String fkInfoStr = getFKInfo(key, foreignkey, fkInfos);
                        // multiValue += fkInfoStr == null ? "" : fkInfoStr;

                        // } else {
                        multiValue += key == null ? "" : key;
                        // }
                    }

                    dynamicLabel = dynamicLabel.replace("{" + dyPath + "}", multiValue == null ? "" : multiValue); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

                }
            } catch (Exception e) {
                continue;
            }
        }

        return dynamicLabel;
    }

    private static List<org.dom4j.Element> getPathNode(org.dom4j.Element el) {
        List<org.dom4j.Element> pathEls = new ArrayList<org.dom4j.Element>();
        org.dom4j.Element currentEl = el;
        while (currentEl != null) {
            pathEls.add(0, currentEl);
            currentEl = currentEl.getParent();
        }
        return pathEls;
    }
}
