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
package org.talend.mdm.webapp.general.client;

import java.util.List;

import org.talend.mdm.webapp.general.model.ActionBean;
import org.talend.mdm.webapp.general.model.ItemBean;
import org.talend.mdm.webapp.general.model.MenuBean;
import org.talend.mdm.webapp.general.model.UserBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("GeneralService")
public interface GeneralService extends RemoteService {

    List<MenuBean> getMenus(String language) throws Exception;
    
    ActionBean getAction();

    String setClusterAndModel(String cluster, String model);
    
    public UserBean getUsernameAndUniverse() throws Exception;
    
    public List<ItemBean> getLanguages();

    public String logout();
}
