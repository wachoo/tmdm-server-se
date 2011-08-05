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

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface Smtp2ServiceAsync {

    void saveConfiguration(SmtpConfigurationBean config, AsyncCallback<Void> callback);

    void loadConfiguration(AsyncCallback<SmtpConfigurationBean> callback);

    void getStatus(AsyncCallback<String> callback);

    void start(AsyncCallback<String> callback);

    void stop(AsyncCallback<String> callback);

    void sendSampleEmail(String from, String to, String cc, String bcc, String subject, String body,
            AsyncCallback<String> callback);
}
