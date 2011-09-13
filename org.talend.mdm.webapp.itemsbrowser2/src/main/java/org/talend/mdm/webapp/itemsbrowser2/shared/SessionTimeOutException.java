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
package org.talend.mdm.webapp.itemsbrowser2.shared;

import java.io.IOException;

import com.google.gwt.user.client.rpc.IsSerializable;

public class SessionTimeOutException extends IOException implements IsSerializable {

    private static final long serialVersionUID = 1L;

    public SessionTimeOutException() {
        super();
    }
}
