/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.save;

public enum UserAction {
    /**
     * MDM will decide whether user is doing a {@link #CREATE} or {@link #UPDATE}.
     */
    AUTO,
    /**
     * User is creating a new record (record is not expected to be already stored in database).
     */
    CREATE,
    /**
     * User is replacing database content with a new version of the record (record is expected to already exist in
     * database).
     */
    REPLACE,
    /**
     * User has only sent values that should be modified in database. For many valued elements, the <b>whole</b>
     * sequence of elements is replaced.
     */
    UPDATE,
    /**
     * User has only sent values that should be modified in database. Using {@link com.amalto.core.save.DocumentSaverContext#getPartialUpdatePivot()}
     * and {@link DocumentSaverContext#getPartialUpdateKey()}, user can perform selective changes.
     */
    PARTIAL_UPDATE
}
