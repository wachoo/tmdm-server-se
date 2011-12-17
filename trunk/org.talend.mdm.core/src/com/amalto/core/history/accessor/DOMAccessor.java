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
interface DOMAccessor extends Accessor {
    /**
     * Returns the {@link org.w3c.dom.Node} that corresponds to this accessor.
     *
     * @return The node that corresponds to this accessor or <code>null</code> if does not exist.
     * @see {@link #create()}
     */
    org.w3c.dom.Node getNode();
}
