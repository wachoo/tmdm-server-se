package com.amalto.webapp.v3.itemsbrowser.util;

import org.apache.commons.lang.StringUtils;

public class XpathUtil {

    // get the source xpath if it is multi-occurs
    public static String getMainXpath(String xpath) {
        return xpath.replaceAll("\\[\\d+\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public static String getFieldXpath(String xpath) {
        return xpath.replaceAll("\\[\\d+\\]$", ""); //$NON-NLS-1$  //$NON-NLS-2$
    }

    public static String unifyXPath(String xpath) {
        String[] xpathSnippets = xpath.split("/"); //$NON-NLS-1$
        for (int i = 0; i < xpathSnippets.length; i++) {
            String xpathSnippet = xpathSnippets[i];
            if (xpathSnippet.isEmpty())
                continue;
            if (!xpathSnippet.matches("(.*?)\\[.*?\\]")) { //$NON-NLS-1$
                xpathSnippets[i] += "[1]"; //$NON-NLS-1$
            }
        }
        String modifiedXpath = StringUtils.join(xpathSnippets, "/");//$NON-NLS-1$
        return modifiedXpath;
    }

    public static boolean checkVisibleByXpath(String xpath, String xpathInRule) {
        return (xpathInRule.indexOf("[") > -1 && unifyXPath(xpath).startsWith(getFieldXpath(xpathInRule))) //$NON-NLS-1$ 
                || (xpathInRule.indexOf("[") == -1 && getMainXpath(xpath).startsWith(xpathInRule)); //$NON-NLS-1$
    }

    public static boolean checkDefalutByXpath(String xpath, String xpathInRule) {
        return (xpathInRule.indexOf("[") > -1 && getFieldXpath(unifyXPath(xpath)).equals(getFieldXpath(xpathInRule))) //$NON-NLS-1$ 
                || (xpathInRule.indexOf("[") == -1 && getMainXpath(xpath).equals(xpathInRule)); //$NON-NLS-1$
    }

}
