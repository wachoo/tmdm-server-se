package com.amalto.core.plugin.base.xslt;

import com.amalto.core.objects.ItemPOJO;
import com.amalto.core.objects.ItemPOJOPK;
import com.amalto.core.objects.datacluster.DataClusterPOJOPK;
import com.amalto.core.util.Util;
import com.amalto.core.server.api.Item;

public class MdmExtension {

    public static String getItemProjection(String revision, String clusterName, String conceptName, String ids) {
        String itemProjection = "";
        if (clusterName == null || clusterName.length() == 0) {
            return itemProjection;
        }
        if (conceptName == null || conceptName.length() == 0) {
            return itemProjection;
        }
        if (ids == null) {
            return itemProjection;
        }
        if (revision != null && (revision.trim().equals("") || revision.trim().equals("null"))) {
            revision = null;
        }
        try {
            Item itemCtrl2Local = Util.getItemCtrl2Local();
            //parse ids
            String[] idArray = ids.split("\\.");
            ItemPOJOPK itemPOJOPK = new ItemPOJOPK(new DataClusterPOJOPK(clusterName), conceptName, idArray);
            ItemPOJO itemPOJO = itemCtrl2Local.getItem(itemPOJOPK);
            if (itemPOJO != null) {
                itemProjection = itemPOJO.getProjectionAsString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return itemProjection;

    }

    public static String getItemPKXmlString(String clusterName, String conceptName, String ids) {
        StringBuilder itemPKXmlString = new StringBuilder();
        if (clusterName == null || clusterName.length() == 0) {
            return itemPKXmlString.toString();
        }
        if (conceptName == null || conceptName.length() == 0) {
            return itemPKXmlString.toString();
        }
        if (ids == null) {
            return itemPKXmlString.toString();
        }
        itemPKXmlString.append("<item-pOJOPK><concept-name>")
                .append(conceptName)
                .append("</concept-name><ids>")
                .append(ids)
                .append("</ids><data-cluster-pOJOPK><ids>")
                .append(clusterName)
                .append("</ids></data-cluster-pOJOPK></item-pOJOPK>");
        return itemPKXmlString.toString();
    }

}
