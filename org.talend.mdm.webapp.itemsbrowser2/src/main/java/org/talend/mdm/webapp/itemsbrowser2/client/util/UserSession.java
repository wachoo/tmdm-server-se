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
package org.talend.mdm.webapp.itemsbrowser2.client.util;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;
import org.talend.mdm.webapp.itemsbrowser2.shared.EntityModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;


/**
 * DOC Starkey  class global comment. Detailled comment
 */
public class UserSession implements Serializable{

    /**
     * 
     */
    private static final long serialVersionUID = -4121455619510069503L;

    
    public static final String CURRENT_VIEW = "currentView"; //$NON-NLS-1$
    public static final String CURRENT_ENTITY_MODEL = "currentEntityModel"; //$NON-NLS-1$
    public static final String APP_HEADER = "appHeader"; //$NON-NLS-1$
    public static final String ENTITY_MODEL_LIST = "entitymodellist"; //$NON-NLS-1$
    
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


    
    /**
     * Getter for appHeader.
     * @return the AppHeader
     */
    public AppHeader getAppHeader() {
        return (AppHeader) get(APP_HEADER);
    }
    
    /**
     * DOC HSHU Comment method "getCurrentView".
     * @return
     */
    public ViewBean getCurrentView() {
        return (ViewBean) get(CURRENT_VIEW);
    }
    
    /**
     * DOC HSHU Comment method "getCurrentEntityModel".
     * @return
     */
    public EntityModel getCurrentEntityModel() {
        return (EntityModel) get(CURRENT_ENTITY_MODEL);
    }

    @SuppressWarnings("unchecked")
    public List<ItemBaseModel> getEntitiyModelList() {
        return (List<ItemBaseModel>) get(ENTITY_MODEL_LIST);
    }
}
