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

package com.amalto.core.storage.hibernate;


import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

/**
 *
 */
interface TableResolver {
    /**
     * Returns the table name for <code>type</code>. The table name is more a 'proposition', database specific mapping
     * may decide to shorten the name (or not).
     * @param type A type from a data model.
     * @return A proposed table name to store instances of type <code>type</code>.
     */
    String get(ComplexTypeMetadata type);

    /**
     * Returns the column name for <code>field</code>. The column name is more a 'proposition', database specific mapping
     * may decide to shorten the name (or not).
     * @param field A field from a type of data model.
     * @return A proposed column name to store values of field <code>field</code>.
     */
    String get(FieldMetadata field);

    /**
     * @param field A field from a type of data model.
     * @return <code>true</code> if field should be indexed for fast search, <code>false</code> in other cases.
     */
    boolean isIndexed(FieldMetadata field);

    /**
     * @return The max length for table / column names. This differs from a database to another (e.g. Oracle does not
     * like names longer than 30).
     */
    int getNameMaxLength();

}
