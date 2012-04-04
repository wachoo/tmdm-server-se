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

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;

import java.util.Iterator;

public class SchematronProcessor implements XmlSchemaAnnotationProcessor {
    public void process(MetadataRepository repository, ComplexTypeMetadata type, XmlSchemaAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            Iterator annotations = annotation.getItems().getIterator();
            while (annotations.hasNext()) {
                XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) annotations.next();
                if ("X_Schematron".equals(appInfo.getSource())) {
                    String rawTextContent = appInfo.getMarkup().item(0).getTextContent();
                    state.setSchematron("<schema>" + StringEscapeUtils.unescapeXml(rawTextContent) + "</schema>");
                }
            }
        }
    }
}
