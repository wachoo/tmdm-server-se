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
package com.amalto.webapp.core.util;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class WebappForbiddenLoginException extends Exception {
    
    /**
     * DOC HSHU WebappLoginException constructor comment.
     */
    public WebappForbiddenLoginException() {
        super();
    }
    /**
     * @param message
     */
    public WebappForbiddenLoginException(String message) {
        super(message);
    }
    /**
     * @param message
     * @param cause
     */
    public WebappForbiddenLoginException(String message, Throwable cause) {
        super(message, cause);
    }
    /**
     * @param cause
     */
    public WebappForbiddenLoginException(Throwable cause) {
        super(cause);
    }

}
