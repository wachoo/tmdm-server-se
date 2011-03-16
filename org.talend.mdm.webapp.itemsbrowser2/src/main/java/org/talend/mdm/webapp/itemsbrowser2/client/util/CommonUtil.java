package org.talend.mdm.webapp.itemsbrowser2.client.util;

public class CommonUtil {

    public static String getConceptFromBrowseItemView(String viewPK) {
        String concept = viewPK.replaceAll("Browse_items_", "");
        concept = concept.replaceAll("#.*", "");
        return concept;
    }

    public static String getXpathSuffix(String xpath) {
        return xpath.substring(xpath.lastIndexOf('/') + 1);
    }

    public static String getElementFromXpath(String xpath) {
        String[] arr = xpath.split("/");
        for (int i = arr.length - 1; i > -1; i--) {
            if (arr[i] != "")
                return arr[i];
        }
        return xpath;
    }

}
