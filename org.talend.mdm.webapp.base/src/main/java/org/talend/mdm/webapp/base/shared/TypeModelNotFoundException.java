/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.webapp.base.shared;

import org.talend.mdm.webapp.base.client.exception.ServiceException;

public class TypeModelNotFoundException extends ServiceException {

    private static final String PARAM_TYPEPATH = "PARAM_TYPEPATH"; //$NON-NLS-1$

    public TypeModelNotFoundException() {
        super();
    }

    public TypeModelNotFoundException(Throwable cause) {
        super(cause);
    }

    public TypeModelNotFoundException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public TypeModelNotFoundException(String typePath) {

        super(PARAM_TYPEPATH, typePath);

    }

    public String getTypePathParameter() {
        
        if(getParameter(PARAM_TYPEPATH)==null)
            return ""; //$NON-NLS-1$

        return getParameter(PARAM_TYPEPATH);
            
    }


}
