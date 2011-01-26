// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;


/**
 * DOC Starkey  class global comment. Detailled comment
 */
public class UserSession implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -4121455619510069503L;

    
    public static final String CURRENT_VIEW = "currentView"; //$NON-NLS-1$

    
    private Map<String,Object> sessionMap = null;
    
    public UserSession() {
        super();
        this.sessionMap =new HashMap<String,Object>();
    }
    
    
    /**
     * DOC Starkey Comment method "put".
     */
    public void put(String key,Object value) {
        this.sessionMap.put(key, value);
    }
    
    
    /**
     * DOC Starkey Comment method "get".
     */
    public Object get(String key) {
        return this.sessionMap.get(key);
    }


}
