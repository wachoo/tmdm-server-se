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

package com.amalto.core.metadata;

import org.apache.ws.commons.schema.XmlSchemaAnnotation;

/**
 * Enrich a {@link ComplexTypeMetadata} being built with information contained in XML Schema information.
 * @see MetadataRepository#createFieldMetadata(org.apache.ws.commons.schema.XmlSchemaElement, ComplexTypeMetadata)
 */
interface XmlSchemaAnnotationProcessor {

    /**
     * Process additional type information contained in {@link XmlSchemaAnnotation}.
     *
     * @param repository The repository that contains the <code>type</code>.
     * @param type       The {@link ComplexTypeMetadata} being enriched by the <code>annotation</code>.
     * @param annotation An XML Schema annotation.
     * @param state      A {@link XmlSchemaAnnotationProcessorState} that keeps track of information parsed by
     *                   {@link XmlSchemaAnnotationProcessor}.
     */
    void process(MetadataRepository repository, ComplexTypeMetadata type, XmlSchemaAnnotation annotation, XmlSchemaAnnotationProcessorState state);

}
