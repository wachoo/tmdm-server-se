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
package org.talend.mdm.webapp.welcomeportal.client;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("WelcomePortalService")
public interface WelcomePortalService extends RemoteService {

    public boolean isHiddenLicense() throws Exception;

    public boolean isHiddenTask() throws Exception;

    public String getAlertMsg(String language) throws Exception;

    public int getTaskMsg() throws Exception;

    public List<String> getStandaloneProcess(String language) throws Exception;

    public String runProcess(String transformerPK) throws Exception;

    public boolean isExpired() throws Exception;
}
