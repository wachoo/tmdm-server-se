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
package org.talend.mdm.webapp.itemsbrowser2.server.exception;

import javax.servlet.ServletException;

public class SessionTimeOutException extends ServletException {
	public SessionTimeOutException() {
		super();
	}
	
	public SessionTimeOutException(String msg) {
		super(msg);
	}
	
	public SessionTimeOutException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
