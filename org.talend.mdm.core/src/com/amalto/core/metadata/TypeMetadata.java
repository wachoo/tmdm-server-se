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

import java.util.Collection;
import java.util.List;

/**
 *
 */
public interface TypeMetadata extends MetadataVisitable {

    Collection<TypeMetadata> getSuperTypes();

    String getName();

    String getNamespace();

    boolean isAbstract();

    Collection<FieldMetadata> getFields();

}
