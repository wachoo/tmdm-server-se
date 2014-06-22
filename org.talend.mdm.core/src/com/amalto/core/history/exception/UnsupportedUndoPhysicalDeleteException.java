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
package com.amalto.core.history.exception;


/**
 * created by yjli on 2013-3-22
 * Detailled comment
 *
 */
public class UnsupportedUndoPhysicalDeleteException extends RuntimeException {
    
    public UnsupportedUndoPhysicalDeleteException() {
        super();
    }

    public UnsupportedUndoPhysicalDeleteException(String message) {
        super(message);
    }

    public UnsupportedUndoPhysicalDeleteException(String message, Throwable cause) {
        super(message, cause);
    }

    public UnsupportedUndoPhysicalDeleteException(Throwable cause) {
        super(cause);
    }
}
