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
package org.talend.mdm.webapp.welcomeportal.client.i18n;

import com.google.gwt.core.client.GWT;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class MessagesFactory {

    private static WelcomePortalMessages MESSAGES;

    private MessagesFactory() {
    }

    public static WelcomePortalMessages getMessages() {
        if (GWT.isClient()) {
            if (MESSAGES == null)
                MESSAGES = GWT.create(WelcomePortalMessages.class);
            return MESSAGES;
        }
        // Can't be called from server-side
        throw new IllegalStateException();
    }
}
