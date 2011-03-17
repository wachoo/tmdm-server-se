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
package org.talend.mdm.webapp.itemsbrowser2.client.creator;

import org.talend.mdm.webapp.itemsbrowser2.client.model.DataType;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeConstants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.DataTypeCustomized;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class DataTypeCreator {
    
    public static DataType getDataType(String typeName, String baseTypeName) {
        
        if(typeName==null||typeName.trim().length()==0)return DataTypeConstants.UNKNOW;
        
        DataTypeConstants[] values = DataTypeConstants.values();
        for (DataTypeConstants value : values) {
            if (value.getTypeName().equals(typeName)) {
                return value;
            }
        }
        
        return new DataTypeCustomized(typeName,baseTypeName);
    }
    
    
    /**
     * DOC HSHU Comment method "checkBaseType".
     */
    public static DataType checkBaseType(DataTypeCustomized dataTypeCustomized) {
        
        if(dataTypeCustomized==null)return null;
        
        String baseTypeName=dataTypeCustomized.getBaseTypeName();
        if(baseTypeName==null)return dataTypeCustomized;
        
        DataTypeConstants[] values = DataTypeConstants.values();
        for (DataTypeConstants value : values) {
            if (value.getTypeName().equals(baseTypeName)) {
                return value;
            }
        }
        
        return dataTypeCustomized;

    }

}
