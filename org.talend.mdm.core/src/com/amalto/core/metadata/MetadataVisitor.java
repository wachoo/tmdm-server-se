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

package com.amalto.core.metadata;

/**
 *
 */
public interface MetadataVisitor<T> {

    T visit(MetadataRepository repository);

    T visit(SimpleTypeMetadata typeMetadata);

    T visit(ComplexTypeMetadata metadata);

    T visit(FieldMetadata metadata);

    T visit(ReferenceUnaryFieldMetadata metadata);

    T visit(ReferenceCollectionFieldMetadata metadata);

    T visit(SimpleTypeFieldMetadata metadata);

    T visit(SimpleTypeCollectionFieldMetadata metadata);

    T visit(EnumerationFieldMetadata metadata);

}
