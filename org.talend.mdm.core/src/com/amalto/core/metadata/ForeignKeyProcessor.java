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

import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.eclipse.emf.common.util.EList;
import org.eclipse.xsd.XSDAnnotation;
import org.w3c.dom.Element;

import java.util.*;

class ForeignKeyProcessor implements XmlSchemaAnnotationProcessor {

    public void process(MetadataRepository repository, ComplexTypeMetadata type, XmlSchemaAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            Iterator annotations = annotation.getItems().getIterator();
            while (annotations.hasNext()) {
                Object next = annotations.next();
                if (next instanceof XmlSchemaAppInfo) {
                    XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) next;
                    if ("X_ForeignKey".equals(appInfo.getSource())) { //$NON-NLS-1$
                        handleForeignKey(repository, state, appInfo);
                    } else if ("X_ForeignKeyInfo".equals(appInfo.getSource())) { //$NON-NLS-1$
                        handleForeignKeyInfo(repository, type, state, appInfo);
                    } else if ("X_FKIntegrity".equals(appInfo.getSource())) { //$NON-NLS-1$
                        state.setFkIntegrity(Boolean.valueOf(appInfo.getMarkup().item(0).getNodeValue()));
                    } else if ("X_FKIntegrity_Override".equals(appInfo.getSource())) { //$NON-NLS-1$
                        state.setFkIntegrityOverride(Boolean.valueOf(appInfo.getMarkup().item(0).getNodeValue()));
                    }
                }
            }
        }
    }

    @Override
    public void process(MetadataRepository repository, ComplexTypeMetadata type, XSDAnnotation annotation, XmlSchemaAnnotationProcessorState state) {
        if (annotation != null) {
            EList<Element> annotations = annotation.getApplicationInformation();
            for (Element appInfo : annotations) {
                if ("X_ForeignKey".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    handleForeignKey(repository, state, appInfo);
                } else if ("X_ForeignKeyInfo".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    handleForeignKeyInfo(repository, type, state, appInfo);
                } else if ("X_FKIntegrity".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    state.setFkIntegrity(Boolean.valueOf(appInfo.getTextContent()));
                } else if ("X_FKIntegrity_Override".equals(appInfo.getAttribute("source"))) { //$NON-NLS-1$
                    state.setFkIntegrityOverride(Boolean.valueOf(appInfo.getTextContent()));
                }
            }
        }
    }

    private void handleForeignKeyInfo(MetadataRepository repository, ComplexTypeMetadata type, XmlSchemaAnnotationProcessorState state, Element appInfo) {
        String path = appInfo.getTextContent();
        String[] typeAndFields = path.split("/"); //$NON-NLS-1$
        String typeName;
        if (typeAndFields[0].equals(".")) { //$NON-NLS-1$
            typeName = type.getName();
        } else {
            typeName = typeAndFields[0].trim();
        }

        List<String> fieldList = Arrays.asList(typeAndFields);
        FieldMetadata fieldMetadata = createFieldReference(repository, new SoftTypeRef(repository, repository.getUserNamespace(), typeName, true), fieldList);
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Path '" + path + "' is not supported.");
        }
        state.setForeignKeyInfo(fieldMetadata);
    }

    private void handleForeignKey(MetadataRepository repository, XmlSchemaAnnotationProcessorState state, Element appInfo) {
        state.markAsReference();
        String path = appInfo.getTextContent();
        String[] typeAndFields = path.split("/"); //$NON-NLS-1$
        String userNamespace = repository.getUserNamespace();
        state.setFieldType(new SoftTypeRef(repository, userNamespace, typeAndFields[0].trim(), true));
        state.setReferencedType(new SoftTypeRef(repository, userNamespace, typeAndFields[0].trim(), true)); // Only reference instantiable types.
        List<String> fieldList = Arrays.asList(typeAndFields);
        FieldMetadata fieldMetadata = createFieldReference(repository, state.getFieldType(), fieldList);
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Path '" + path + "' is not supported.");
        }
        state.setReferencedField(fieldMetadata);
    }

    private void handleForeignKeyInfo(MetadataRepository repository, ComplexTypeMetadata type, XmlSchemaAnnotationProcessorState state, XmlSchemaAppInfo appInfo) {
        String path = appInfo.getMarkup().item(0).getNodeValue();
        String[] typeAndFields = path.split("/"); //$NON-NLS-1$
        String typeName;
        if (typeAndFields[0].equals(".")) { //$NON-NLS-1$
            typeName = type.getName();
        } else {
            typeName = typeAndFields[0].trim();
        }

        List<String> fieldList = Arrays.asList(typeAndFields);
        FieldMetadata fieldMetadata = createFieldReference(repository, new SoftTypeRef(repository, repository.getUserNamespace(), typeName, true), fieldList);
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Path '" + path + "' is not supported.");
        }
        state.setForeignKeyInfo(fieldMetadata);
    }

    private void handleForeignKey(MetadataRepository repository, XmlSchemaAnnotationProcessorState state, XmlSchemaAppInfo appInfo) {
        state.markAsReference();
        String path = appInfo.getMarkup().item(0).getNodeValue();
        String[] typeAndFields = path.split("/"); //$NON-NLS-1$
        String userNamespace = repository.getUserNamespace();
        state.setFieldType(new SoftTypeRef(repository, userNamespace, typeAndFields[0].trim(), true));
        state.setReferencedType(new SoftTypeRef(repository, userNamespace, typeAndFields[0].trim(), true)); // Only reference instantiable types.

        List<String> fieldList = Arrays.asList(typeAndFields);
        FieldMetadata fieldMetadata = createFieldReference(repository, state.getFieldType(), fieldList);
        if (fieldMetadata == null) {
            throw new IllegalArgumentException("Path '" + path + "' is not supported.");
        }
        state.setReferencedField(fieldMetadata);
    }

    private static FieldMetadata createFieldReference(MetadataRepository repository, TypeMetadata rootTypeName, List<String> path) {
        Queue<String> processQueue = new LinkedList<String>(path);
        processQueue.poll(); // Remove first (first is type name and required additional processing for '.')
        SoftTypeRef currentType = new SoftTypeRef(repository, rootTypeName.getNamespace(), rootTypeName.getName(), true);
        if (processQueue.isEmpty()) {
            // handle case where referenced type "Type" only and not "Type/Id" (way to reference composite id).
            return new SoftIdFieldRef(repository, rootTypeName.getName());
        }

        SoftFieldRef fieldMetadata = null;
        while (!processQueue.isEmpty()) {
            // In case of "PersonneMorale/IdPersonneMorale[PersonneMorale/ListeRole/Role/IdRole=DEP]", skip xpath condition.
            String currentField = StringUtils.substringBefore(processQueue.poll().trim(), "["); //$NON-NLS-1$
            if (fieldMetadata == null) {
                fieldMetadata = new SoftFieldRef(repository, currentField, currentType);
            } else {
                fieldMetadata = new SoftFieldRef(repository, currentField, fieldMetadata);
            }
        }
        return fieldMetadata;
    }
}
