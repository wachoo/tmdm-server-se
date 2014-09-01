/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package org.talend.mdm.server;

import org.talend.mdm.storage.Storage;
import org.talend.mdm.storage.StorageType;
import org.talend.mdm.storage.datasource.DataSourceDefinition;

/**
 * A storage extension that can be implemented to provide additional {@link org.talend.mdm.storage.Storage}
 * implementation that are not part of the original MDM server.
 */
public interface StorageExtension {

    /**
     * Test if this extension can accept some initialization parameters.
     * 
     * @param definition A {@link org.talend.mdm.storage.datasource.DataSourceDefinition datasource} to check with this
     * storage extension.
     * @param storageType The {@link org.talend.mdm.storage.StorageType type} of the storage that is checked.
     * @return <code>true</code> if a {@link org.talend.mdm.storage.Storage storage} can be created with the supplied
     * parameters.
     * @see #create(String, org.talend.mdm.storage.StorageType)
     */
    boolean accept(DataSourceDefinition definition, StorageType storageType);

    /**
     * Returns an implementation of {@link org.talend.mdm.storage.Storage storage}. Implementation is <b>not</b>
     * expected to be initialized (through {@link Storage#init(org.talend.mdm.storage.datasource.DataSourceDefinition)}
     * .
     * 
     * @param storageName The storage name.
     * @param storageType The storage {@link org.talend.mdm.storage.StorageType type}.
     * @return A {@link org.talend.mdm.storage.Storage storage} ready to be initialized.
     */
    Storage create(String storageName, StorageType storageType);
}
