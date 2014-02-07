/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.server;

import com.amalto.core.storage.Storage;

/**
 * A hook that can be implemented to intercept server components creation.
 */
public interface Hook {
    /**
     * Called when a storage is about to be created.
     *
     * @param storage The newly created storage instance.
     * @return The {@link com.amalto.core.storage.Storage} instance the server should use. Implementations are only
     * required to return a non-null storage.
     */
    Storage onStorageCreate(Storage storage);
}
