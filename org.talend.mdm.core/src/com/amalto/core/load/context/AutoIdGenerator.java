/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.load.context;

/**
 *
 */
public interface AutoIdGenerator {
    /**
     * <p>
     * Generate an automatic id for the <code>conceptName</code> (a.k.a. type name) in the <code>dataCluster</code>
     * identified by <code>dataClusterName</code>.
     * </p>
     * <p>
     * Implementations of this interface may not use the parameters to generate ids.
     * </p>
     *
     * @param dataClusterName A data cluster name.
     * @param conceptName     A concept name (type name).
     * @return A automatically generated id valid for the <code>conceptName</code> in <code>dataClusterName</code>
     */
    String generateAutoId(String dataClusterName, String conceptName);
}
