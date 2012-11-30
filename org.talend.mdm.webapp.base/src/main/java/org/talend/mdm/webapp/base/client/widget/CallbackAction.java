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
package org.talend.mdm.webapp.base.client.widget;

import java.util.HashMap;
import java.util.Map;

public class CallbackAction {
    
    private static CallbackAction callback;
    
    final public static String HIERARCHY_SAVEITEM_CALLBACK = "hierarchySaveItem"; //$NON-NLS-1$
    
    final public static String HIERARCHY_DELETEITEM_CALLBACK = "hierarchyDeleteItem"; //$NON-NLS-1$
    
    private Map<String, Callback> actions = new HashMap<String, Callback>();
    
    public static synchronized CallbackAction getInstance(){
        if (callback == null){
            callback = new CallbackAction();
        }
        return callback;
    }
    
    public void putAction(String name,Callback action){
        actions.put(name, action);
    }
    
    public void doAction(String name,Object value,String concept,Boolean isClose){ 
        if (actions.get(name) != null){
            actions.get(name).doAction(value,concept,isClose); 
        }
    }
}
