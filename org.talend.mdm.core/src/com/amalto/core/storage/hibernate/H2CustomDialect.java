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

package com.amalto.core.storage.hibernate;

import org.hibernate.dialect.H2Dialect;

import java.sql.Types;

// Dynamically called, don't remove
public class H2CustomDialect extends H2Dialect {
    public H2CustomDialect() {
        registerColumnType(Types.BOOLEAN, "boolean"); //$NON-NLS-1$
        registerHibernateType(Types.BOOLEAN, "boolean"); //$NON-NLS-1$
    }
}
