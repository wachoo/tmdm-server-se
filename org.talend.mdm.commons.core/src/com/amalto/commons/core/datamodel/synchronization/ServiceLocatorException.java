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
package com.amalto.commons.core.datamodel.synchronization;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ServiceLocatorException extends Exception {

    /**
     * 
     */
    private static final long serialVersionUID = 9197172146403264194L;

    public ServiceLocatorException(String msg) {
        super(msg);
    }

    public ServiceLocatorException(Exception e) {
        super(e);
    }
}
