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

package com.amalto.core.history.accessor;

/**
 *
 */
public interface Accessor {

    /**
     * Set a value in the document. The full path of elements <b>must</b> exist (this can be ensured via a call
     * to {@link #create()} if needed.
     *
     * @param value The value to be set.
     * @see #create()
     * @see #exist()
     */
    void set(String value);

    /**
     * Get the value in the document. The full path of elements <b>must</b> exist (this can be ensured via a call
     * to {@link #create()} if needed.
     *
     * @return The value accessible via this accessor.
     * @see #create()
     * @see #exist()
     */
    String get();

    /**
     * Creates all the structure necessary to make {@link #set(String)} successful. It creates all
     * missing XML elements in the document.
     *
     * @see #exist()
     */
    void create();

    /**
     * Deletes the underlying XML element of this accessor (might be an element or an attribute). This method does not
     * remove all intermediate elements (i.e. path1/path2/path3 will only remove path3 and not path2 nor path1).
     */
    void delete();

    /**
     * @return Returns true if the field exists (i.e. the full path to the field exists). If it doesn't, a call to
     *         {@link #create()} should be able to create everything needed.
     */
    boolean exist();
}
