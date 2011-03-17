package org.talend.mdm.webapp.itemsbrowser2.client.util;

public class CommonUtil {

    public static String getXpathSuffix(String xpath) {
        return xpath.substring(xpath.lastIndexOf('/') + 1);//$NON-NLS-1$
    }

    public static String getElementFromXpath(String xpath) {
        String[] arr = xpath.split("/");//$NON-NLS-1$
        for (int i = arr.length - 1; i > -1; i--) {
            if (arr[i] != "")//$NON-NLS-1$
                return arr[i];
        }
        return xpath;
    }

}
