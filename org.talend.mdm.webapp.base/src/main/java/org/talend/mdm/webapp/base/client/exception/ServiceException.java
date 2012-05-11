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
package org.talend.mdm.webapp.base.client.exception;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.user.client.rpc.IsSerializable;

public class ServiceException extends Exception implements IsSerializable {

    private static final long serialVersionUID = 1L;

    private Map<String, Object> parameterMap;

    public ServiceException() {
        super();
    }

    public ServiceException(String message) {
        super(message);
    }

    public ServiceException(Throwable cause) {
        super(cause);
    }

    public ServiceException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ServiceException(String parameterKey, Object parameterValue) {
        super();
        setParameter(parameterKey, parameterValue);
    }


    private void setParameter(String key, Object parameter) {
        if (parameterMap == null)
            parameterMap = new HashMap<String, Object>();
        parameterMap.put(key, parameter);
    }

    protected <X> X getParameter(String key) {
        if (parameterMap == null)
            return null;
        return (X) parameterMap.get(key);
    }
}
