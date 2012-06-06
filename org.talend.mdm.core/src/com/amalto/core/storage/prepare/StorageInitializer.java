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

package com.amalto.core.storage.prepare;

import com.amalto.core.storage.Storage;

/**
 *
 */
public interface StorageInitializer {

    /**
     * @param storage A {@link Storage} implementation.
     * @return <code>true</code> if {@link Storage} is already initialized, <code>false</code> otherwise.
     */
    boolean isInitialized(Storage storage);

    /**
     * Initializes a storage before any data operation can be performed.
     * @param storage A {@link Storage} implementation.
     */
    void initialize(Storage storage);
    
}
