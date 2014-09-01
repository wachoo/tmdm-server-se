/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.server.api;

import org.w3c.dom.Node;

import org.talend.mdm.storage.datasource.DataSource;

/**
 * A datasource extension that can be implemented to provide additional
 * {@link org.talend.mdm.storage.datasource.DataSource} implementation that are not part of the original MDM server.
 */
public interface DataSourceExtension {

    /**
     * @param type The value as present in "type" element of the datasource configuration file.
     * @return <code>true</code> if the <code>type</code> is supported by this datasource extension.
     */
    boolean accept(String type);

    /**
     * Parse the datasource type specific part and returns a {@link org.talend.mdm.storage.datasource.DataSource
     * datasource}.
     * 
     *
     * @param datasourceNode The XML node in the datasource configuration file to parse.
     * @return A {@link org.talend.mdm.storage.datasource.DataSource datasource} implementation.
     */
    DataSource create(Node datasourceNode);
}
