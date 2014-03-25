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
package com.amalto.webapp.v3.reporting.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.w3c.dom.NodeList;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;

/**
 * DOC achen  class global comment. Detailled comment
 */
public class ForeignKeyDescHelper {

    private static final Pattern extractIdPattern = Pattern.compile("\\[.*?\\]"); //$NON-NLS-1$

    public static String getFkInfoValue(String xpath, ForeignKeyDesc fkDesc, String ids) {
        Boolean isRetrive = fkDesc.getRetrieveFKinfos().get(xpath);
        if (!(isRetrive != null && isRetrive))
            return ids;
        ItemPOJOPK pk = new ItemPOJOPK();
        String[] itemId = extractIdWithBrackets(ids);
        if (itemId == null)
            return null;
        pk.setIds(itemId);
        String fk = fkDesc.getForeignKeys().get(xpath);
        if (fk == null)
            return ids;
        pk.setConceptName(fk.split("/")[0]); //$NON-NLS-1$
        pk.setDataClusterPOJOPK(new DataClusterPOJOPK(fkDesc.getDataCluster()));
        ItemPOJO item;
        try {
            item = com.amalto.core.util.Util.getItemCtrl2Local().getItem(pk);
            if (item != null) {
                org.w3c.dom.Document document = item.getProjection().getOwnerDocument();
                List<String> foreignKeyInfo = fkDesc.getFkInfoLists().get(xpath);
                String formattedId = ""; // Id formatted using foreign key info //$NON-NLS-1$
                for (String foreignKeyPath : foreignKeyInfo) {
                    NodeList nodes = com.amalto.core.util.Util.getNodeList(document,
                            StringUtils.substringAfter(foreignKeyPath, "/")); //$NON-NLS-1$
                    if (nodes.getLength() == 1) {
                        if (formattedId.equals("")) //$NON-NLS-1$
                            formattedId += nodes.item(0).getTextContent();
                        else
                            formattedId += "-" + nodes.item(0).getTextContent(); //$NON-NLS-1$
                    }
                }
                return formattedId;
            }
            return ids; //$NON-NLS-1$
        } catch (Exception e) {
            return ids; //$NON-NLS-1$
        }
    }

    /**
     * @param ids Expect a id like "[value0][value1][value2]"
     * @return Returns an array with ["value0", "value1", "value2"]
     */
    private static String[] extractIdWithBrackets(String ids) {
        List<String> idList = new ArrayList<String>();
        Matcher matcher = extractIdPattern.matcher(ids);
        boolean hasMatchedOnce = false;
        while (matcher.find()) {
            String id = matcher.group();
            id = id.replaceAll("\\[", "").replaceAll("\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
            idList.add(id);
            hasMatchedOnce = true;
        }

            if (!hasMatchedOnce) {
            return null;
        }

        return idList.toArray(new String[idList.size()]);
    }
}
