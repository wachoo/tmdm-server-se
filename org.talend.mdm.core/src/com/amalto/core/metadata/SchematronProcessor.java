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

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;
import java.util.Iterator;

public class SchematronProcessor implements XmlSchemaAnnotationProcessor {

    private static final TransformerFactory transformerFactory = TransformerFactory.newInstance();

    public void process(MetadataRepository repository, ComplexTypeMetadata type, XmlSchemaAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            Iterator annotations = annotation.getItems().getIterator();
            while (annotations.hasNext()) {
                XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) annotations.next();
                if ("X_Schematron".equals(appInfo.getSource())) { //$NON-NLS-1$
                    try {
                        // TODO This is not really efficient but doing it nicely would require to rewrite a StringEscapeUtils.unescapeXml()
                        StringWriter sw = new StringWriter();
                        Transformer transformer = transformerFactory.newTransformer();
                        transformer.setOutputProperty("omit-xml-declaration", "yes"); //$NON-NLS-1$ //$NON-NLS-2$
                        transformer.transform(new DOMSource(appInfo.getMarkup().item(0)), new StreamResult(sw));
                        state.setSchematron("<schema>" + StringEscapeUtils.unescapeXml(sw.toString()) + "</schema>"); //$NON-NLS-1$ //$NON-NLS-2$
                    } catch (TransformerException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }
}
