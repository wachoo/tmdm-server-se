// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

/**
 * DOC talend2 class global comment. Detailled comment
 */
public class BeforeSavingErrorException extends RuntimeException {

    public BeforeSavingErrorException() {
        super();
    }

    public BeforeSavingErrorException(String message) {
        super(message);
    }

    public BeforeSavingErrorException(String message, Throwable cause) {
        super(message, cause);
    }

    public BeforeSavingErrorException(Throwable cause) {
        super(cause);
    }

}
