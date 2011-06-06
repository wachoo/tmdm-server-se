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

package com.amalto.core.history;

/**
 * A {@link Document} that is able to change.
 */
public interface MutableDocument extends Document {

    /**
     * <p>
     * Change value of a field in the document.
     * </p>
     * @param field XPath to the field in the document.
     * @param newValue New value to be set.
     * @return A mutable document ready to be used (might be the same instance).
     */
    MutableDocument setField(String field, String newValue);

    /**
     * <p>
     * Turns on or off the <pre>isCreated</pre> flag for the document.
     * </p>
     * <p>
     * <b>Note:</b>This should turn off the deleted status.
     * </p>
     *
     * @param isCreated true if the document should be marked as created, false otherwise.
     * @return A mutable document ready to be used (might be the same instance).
     */
    MutableDocument setCreated(boolean isCreated);

    /**
     * <p>
     * Turns on or off the <pre>isDeleted</pre> flag for the document.
     * </p>
     * <p>
     * <b>Note:</b>This should turn off the created status.
     * </p>
     * @param isDeleted true if the document should be marked as deleted, false otherwise.
     * @return A mutable document ready to be used (might be the same instance).
     */
    MutableDocument setDeleted(boolean isDeleted);

    /**
     * @return Returns a unmodifiable document that might be result of action optimizations.
     */
    Document applyChanges();

}
