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

import com.google.gwt.user.client.rpc.IsSerializable;

public class TypeModelNotFoundException extends RuntimeException implements IsSerializable {

    public TypeModelNotFoundException() {
        super("Failed to find the target type-model! "); //$NON-NLS-1$
    }

    public TypeModelNotFoundException(Throwable cause) {
        super("Failed to find the target type-model! ", cause); //$NON-NLS-1$
    }

    public TypeModelNotFoundException(String message) {
        super(message);
    }

    public TypeModelNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

}
