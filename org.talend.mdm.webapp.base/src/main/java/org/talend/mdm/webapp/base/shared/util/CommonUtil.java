// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.shared.util;

import java.util.ArrayList;
import java.util.List;

/**
 * created by yjli on 2013-8-1
 * Detailled comment
 *
 */
public class CommonUtil {
    
    public static String escape(String src) {
        int i;
        char j;
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length() * 6);

        for (i = 0; i < src.length(); i++) {

            j = src.charAt(i);

            if (Character.isDigit(j) || Character.isLowerCase(j)
                    || Character.isUpperCase(j))
                tmp.append(j);
            else if (j < 256) {
                tmp.append("%"); //$NON-NLS-1$
                if (j < 16)
                    tmp.append("0"); //$NON-NLS-1$
                tmp.append(Integer.toString(j, 16));
            } else {
                tmp.append("%u"); //$NON-NLS-1$
                tmp.append(Integer.toString(j, 16));
            }
        }
        return tmp.toString();
    }

    public static String unescape(String src) {
        StringBuffer tmp = new StringBuffer();
        tmp.ensureCapacity(src.length());
        int lastPos = 0, pos = 0;
        char ch;
        while (lastPos < src.length()) {
            pos = src.indexOf("%", lastPos); //$NON-NLS-1$
            if (pos == lastPos) {
                if (src.charAt(pos + 1) == 'u') {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 2, pos + 6), 16);
                    tmp.append(ch);
                    lastPos = pos + 6;
                } else {
                    ch = (char) Integer.parseInt(src
                            .substring(pos + 1, pos + 3), 16);
                    tmp.append(ch);
                    lastPos = pos + 3;
                }
            } else {
                if (pos == -1) {
                    tmp.append(src.substring(lastPos));
                    lastPos = src.length();
                } else {
                    tmp.append(src.substring(lastPos, pos));
                    lastPos = pos;
                }
            }
        }
        return tmp.toString();
    }
    
    public static String convertListToString(List<String> itemList,String separator) {
        if (itemList == null) {
            return null;
        }
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < itemList.size(); i++) {
            result.append((i > 0) ? separator : ""); //$NON-NLS-1$ 
            result.append(escape(itemList.get(i)));
        }
        return result.toString();
    }
    
    public static List<String> convertStrigToList(String valueString,String separator) {
        if (valueString == null || valueString.isEmpty()) {
            return null;
        }
        List<String> valueList = new ArrayList<String>();
        String[] valueArray = valueString.split(separator);
        for (String value : valueArray) {
            valueList.add(unescape(value));
        }
        return valueList;
    }    
}
