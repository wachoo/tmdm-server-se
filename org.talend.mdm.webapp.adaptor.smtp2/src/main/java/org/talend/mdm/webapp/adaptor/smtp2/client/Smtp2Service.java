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

package org.talend.mdm.webapp.adaptor.smtp2.client;

import org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("Smtp2Service")
public interface Smtp2Service extends RemoteService {

    void saveConfiguration(SmtpConfigurationBean config) throws Exception;

    SmtpConfigurationBean loadConfiguration() throws Exception;

    String getStatus() throws Exception;

    String start() throws Exception;

    String stop() throws Exception;

    String sendSampleEmail(String from, String to, String cc, String bcc, String subject, String body) throws Exception;
}
