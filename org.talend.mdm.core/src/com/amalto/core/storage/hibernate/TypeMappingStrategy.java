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

package com.amalto.core.storage.hibernate;

public enum TypeMappingStrategy {
    /**
     * Simplest mapping strategy: put all elements in one type/table. This strategy can not be applied if there are
     * repeated contained elements in the metadata.
     */
    FLAT(false),
    /**
     * Most general strategy: maps each level of contained elements to a new type. The XML tree is then 'scattered'
     * across multiple types.
     */
    SCATTERED(false),
    /**
     * Same as {@link #SCATTERED} strategy with one change: all long string values will be stored using CLOB in database.
     */
    SCATTERED_CLOB(true),
    /**
     * "Best guess" strategy: MDM will choose best strategy based on metadata analysis.
     */
    AUTO(false);

    private final boolean preferClobUse;

    private TypeMappingStrategy(boolean preferClobUse) {
        this.preferClobUse = preferClobUse;
    }

    /**
     * @return <code>true</code> if strategy implies compression of long string values, <code>false</code> otherwise.
     */
    public boolean preferClobUse() {
        return preferClobUse;
    }
}
