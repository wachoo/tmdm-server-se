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
package org.talend.mdm.webapp.welcomeportal.client;

import java.util.List;

import org.talend.mdm.webapp.base.client.exception.ServiceException;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("WelcomePortalService")
public interface WelcomePortalService extends RemoteService {

    public boolean isHiddenLicense() throws ServiceException;

    public boolean isHiddenTask() throws ServiceException;

    public String getAlertMsg(String language) throws ServiceException;

    public int getTaskMsg() throws ServiceException;

    public List<String> getStandaloneProcess(String language) throws ServiceException;

    public String runProcess(String transformerPK) throws ServiceException;

    public boolean isExpired(String language) throws ServiceException;

    boolean isEnterpriseVersion() throws ServiceException;

    public String getMenuLabel(String language, String id) throws Exception;

    public String getLicenseWarning(String language) throws ServiceException;
}
