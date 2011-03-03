package org.talend.mdm.webapp.itemsbrowser2.client.util;

public class CommonUtil {

    public static String getConceptFromBrowseItemView(String viewPK) {
        String concept = viewPK.replaceAll("Browse_items_", "");
        concept = concept.replaceAll("#.*", "");
        return concept;
    }
}
