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

import org.talend.mdm.webapp.general.model.ComboBoxModel;
import org.talend.mdm.webapp.general.model.MenuBean;
import org.talend.mdm.webapp.general.model.UserBean;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GeneralServiceAsync {

    void getMenus(String language, AsyncCallback<List<MenuBean>> callback);

    void getMsg(AsyncCallback<String> callback);

    void getClusters(AsyncCallback<List<ComboBoxModel>> callback);

    void getModels(AsyncCallback<List<ComboBoxModel>> callback);

    void setClusterAndModel(String cluster, String model, AsyncCallback<String> callback);

    void getUsernameAndUniverse(AsyncCallback<UserBean> callback);
}
