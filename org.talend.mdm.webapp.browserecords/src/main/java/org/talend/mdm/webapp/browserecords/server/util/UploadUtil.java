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
package org.talend.mdm.webapp.browserecords.server.util;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("nls")
public class UploadUtil {
    
    public static Map<String,Boolean> getVisibleMap(String headerString){
        Map<String,Boolean> visbleMap = new HashMap<String,Boolean>();
        String fields[] = headerString.split("@"); //$NON-NLS-1$
        for (int i=0;i<fields.length;i++){
            visbleMap.put(getFieldName(fields[i]), getFieldVisible(fields[i]));
        }
        return visbleMap;       
    }
    
    public static Set<String> chechMandatoryField(String mandatoryField, Set<String> fields) {

        String[] mandatoryFields = mandatoryField.split("@"); //$NON-NLS-1$
        Set<String> mandatorySet = new HashSet<String>();
        for (String field : mandatoryFields)
            mandatorySet.add(field);

        for (String fieldValue : fields) {
            String fieldName = getFieldName(fieldValue);
            if (mandatorySet.contains(fieldName))
                mandatorySet.remove(fieldName);
        }

        return mandatorySet;
    }
    
    public static String getFieldName(String fieldValue){
        return fieldValue.split(":")[0]; //$NON-NLS-1$
    }
    
    public static boolean getFieldVisible(String fieldValue){
        return Boolean.valueOf(fieldValue.split(":")[1]); //$NON-NLS-1$
    }
    
    public static String[] getDefaultHeader(String headerString){
        List<String> headers = new LinkedList<String>();  
        String fields[] = headerString.split("@"); //$NON-NLS-1$
        for (int i=0;i<fields.length;i++){
            headers.add(getFieldName(fields[i]));  
        }
        return headers.toArray(new String[headers.size()]);
    }
    
    public static String getRootCause(Throwable throwable) {
        String message = ""; //$NON-NLS-1$
        Throwable currentCause = throwable;
        while (currentCause != null) {           
            message = currentCause.getMessage();
            currentCause = currentCause.getCause();
        }
        return message;
    }
    
    public static boolean isViewableXpathValid(String viewableXpath, String concept){
        String[] xPathArr = viewableXpath.split("@"); //$NON-NLS-1$
        for(String path : xPathArr){
            String str = path.substring(0, path.indexOf("/"));
            if(!str.equalsIgnoreCase(concept))
                return false;
        }
        return true;
    }
}
