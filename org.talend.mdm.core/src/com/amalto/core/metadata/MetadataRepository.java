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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

import javax.xml.namespace.QName;

import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.schema.ValidationEventHandler;
import org.apache.ws.commons.schema.XmlSchema;
import org.apache.ws.commons.schema.XmlSchemaAnnotation;
import org.apache.ws.commons.schema.XmlSchemaAppInfo;
import org.apache.ws.commons.schema.XmlSchemaCollection;
import org.apache.ws.commons.schema.XmlSchemaComplexType;
import org.apache.ws.commons.schema.XmlSchemaElement;
import org.apache.ws.commons.schema.XmlSchemaGroupBase;
import org.apache.ws.commons.schema.XmlSchemaObject;
import org.apache.ws.commons.schema.XmlSchemaObjectCollection;
import org.apache.ws.commons.schema.XmlSchemaParticle;
import org.apache.ws.commons.schema.XmlSchemaSimpleType;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeContent;
import org.apache.ws.commons.schema.XmlSchemaSimpleTypeRestriction;
import org.apache.ws.commons.schema.XmlSchemaType;
import org.apache.ws.commons.schema.XmlSchemaUnique;
import org.apache.ws.commons.schema.XmlSchemaXPath;
import com.amalto.core.metadata.xsd.XmlSchemaVisitor;
import com.amalto.core.metadata.xsd.XmlSchemaWalker;

/**
 *
 */
public class MetadataRepository implements MetadataVisitable, XmlSchemaVisitor<Void> {

    private final Map<String, Map<String, TypeMetadata>> allTypes = new HashMap<String, Map<String, TypeMetadata>>();

    private final Stack<ComplexTypeMetadata> typeMetadataStack = new Stack<ComplexTypeMetadata>();

    private final Stack<Set<String>> typeMetadataKeyStack = new Stack<Set<String>>();

    private final Map<String, ComplexTypeMetadata> complexTypeMetadataCache = new HashMap<String, ComplexTypeMetadata>();

    private String targetNamespace;

    public TypeMetadata getType(String name) {
        return getType(StringUtils.EMPTY, name);
    }

    public TypeMetadata getType(String nameSpace, String name) {
        Map<String, TypeMetadata> nameSpaceTypes = allTypes.get(nameSpace);
        if (nameSpaceTypes == null) {
            return null;
        }
        return nameSpaceTypes.get(name);
    }

    public ComplexTypeMetadata getComplexType(String name) {
        return complexTypeMetadataCache.get(name);
    }

    public Collection<TypeMetadata> getTypes() {
        List<TypeMetadata> allTypes = new ArrayList<TypeMetadata>();
        Collection<Map<String, TypeMetadata>> nameSpaces = this.allTypes.values();
        for (Map<String, TypeMetadata> nameSpace : nameSpaces) {
            allTypes.addAll(nameSpace.values());
        }

        return allTypes;
    }

    public void load(InputStream inputStream) {
        XmlSchemaCollection collection = new XmlSchemaCollection();
        collection.read(new InputStreamReader(inputStream), new ValidationEventHandler());

        XmlSchemaWalker.walk(collection, this);
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    public Void visit(XmlSchema xmlSchema) {
        targetNamespace = xmlSchema.getTargetNamespace() == null ? StringUtils.EMPTY : xmlSchema.getTargetNamespace();
        return XmlSchemaWalker.walk(xmlSchema, this);
    }

    public Void visit(XmlSchemaSimpleType type) {
        TypeMetadata typeMetadata = new SimpleTypeMetadata(targetNamespace, type.getName());
        addTypeMetadata(typeMetadata);
        return null;
    }

    public Void visit(XmlSchemaComplexType type) {
        XmlSchemaParticle contentTypeParticle = type.getParticle();
        if (contentTypeParticle instanceof XmlSchemaGroupBase) {
            XmlSchemaObjectCollection items = ((XmlSchemaGroupBase) contentTypeParticle).getItems();
            Iterator itemsIterator = items.getIterator();
            while (itemsIterator.hasNext()) {
                XmlSchemaObject schemaObject = (XmlSchemaObject) itemsIterator.next();
                XmlSchemaWalker.walk(schemaObject, this);
            }
        } else {
            throw new IllegalArgumentException("Not supported XML Schema particle: " + contentTypeParticle.getClass().getName());
        }

        return null;
    }

    public Void visit(XmlSchemaElement element) {
        if (!typeMetadataStack.isEmpty()) {
            FieldMetadata fieldMetadata = createFieldMetadata(element);
            typeMetadataStack.peek().addField(fieldMetadata);
        }

        if (element.getSchemaTypeName() == null) {
            if (getType(element.getSourceURI(), element.getName()) == null) {
                createTypeMetadata(element);
            }
        } else if (element.getSchemaType() instanceof XmlSchemaComplexType) {
            createTypeMetadata(element);
        }

        return null;
    }

    private void createTypeMetadata(XmlSchemaElement element) {
        String typeName = element.getName();
        XmlSchemaType schemaType = element.getSchemaType();

        ComplexTypeMetadata typeMetadata = (ComplexTypeMetadata) getType(targetNamespace, typeName);
        if (typeMetadata == null) {
            // TODO Super types
            typeMetadata = new ComplexTypeMetadata(targetNamespace, typeName, Collections.<TypeMetadata>emptySet());
            addTypeMetadata(typeMetadata);
        }

        // Find key fields for the new type
        Set<String> idFields = new HashSet<String>();
        XmlSchemaObjectCollection constraints = element.getConstraints();
        Iterator constraintsIterator = constraints.getIterator();
        while (constraintsIterator.hasNext()) {
            Object nextConstraint = constraintsIterator.next();
            if (nextConstraint instanceof XmlSchemaUnique) {
                XmlSchemaUnique xmlSchemaUnique = (XmlSchemaUnique) nextConstraint;
                XmlSchemaObjectCollection fields = xmlSchemaUnique.getFields();
                Iterator uniqueIterator = fields.getIterator();
                while (uniqueIterator.hasNext()) {
                    XmlSchemaXPath idPath = (XmlSchemaXPath) uniqueIterator.next();
                    idFields.add(idPath.getXPath());
                }
            } else {
                throw new IllegalArgumentException("Constraint of type '" + nextConstraint.getClass().getName() + "' not supported.");
            }
        }


        typeMetadataStack.push(typeMetadata);
        {
            typeMetadataKeyStack.push(idFields);
            {
                XmlSchemaWalker.walk(schemaType, this);
            }
            typeMetadataKeyStack.pop();
        }
        typeMetadataStack.pop();
        complexTypeMetadataCache.put(typeName, typeMetadata);
    }

    private FieldMetadata createFieldMetadata(XmlSchemaElement element) {
        String elementName = element.getName();
        boolean isCollection = element.getMaxOccurs() > 1;
        boolean isKey = typeMetadataKeyStack.peek().contains(elementName);
        boolean isReference = false;
        String fieldTypeName = null;
        ComplexTypeMetadata currentContainingType = typeMetadataStack.peek();

        TypeRef referencedType = NotResolvedTypeRef.INSTANCE;

        XmlSchemaAnnotation annotation = element.getAnnotation();
        String foreignKeyInfo = null;
        boolean fkIntegrity = true; // Default is to enforce FK integrity
        boolean fkIntegrityOverride = false; // Default is to disable FK integrity check
        if (annotation != null) {
            Iterator annotations = annotation.getItems().getIterator();
            while (annotations.hasNext()) {
                XmlSchemaAppInfo appInfo = (XmlSchemaAppInfo) annotations.next();
                if ("X_ForeignKey".equals(appInfo.getSource())) { //$NON-NLS-1$
                    isReference = true;
                    String[] foreignKeyDefinition = appInfo.getMarkup().item(0).getTextContent().split("/");
                    fieldTypeName = foreignKeyDefinition[0];
                    referencedType = new SoftTypeRef(this, fieldTypeName);
                } else if ("X_ForeignKeyInfo".equals(appInfo.getSource())) { //$NON-NLS-1$
                    foreignKeyInfo = appInfo.getMarkup().item(0).getTextContent().split("/")[1];
                } else if ("X_FKIntegrity".equals(appInfo.getSource())) { //$NON-NLS-1$
                    fkIntegrity = Boolean.valueOf(appInfo.getMarkup().item(0).getTextContent());
                } else if ("X_FKIntegrity_Override".equals(appInfo.getSource())) { //$NON-NLS-1$
                    fkIntegrityOverride = Boolean.valueOf(appInfo.getMarkup().item(0).getTextContent());
                }
            }
        }

        XmlSchemaType schemaType = element.getSchemaType();
        if (fieldTypeName == null) {
            QName qName = element.getSchemaTypeName();
            if (qName != null) {
                TypeMetadata metadata = getType(qName.getNamespaceURI(), qName.getLocalPart());
                if (metadata != null) {
                    fieldTypeName = metadata.getName();
                } else {
                    if (schemaType instanceof XmlSchemaComplexType) {
                        isReference = true;
                        referencedType = new SoftTypeRef(this, elementName);
                    }
                }
            } else {
                isReference = true;
                referencedType = new SoftTypeRef(this, elementName);
            }
        }

        if (schemaType instanceof XmlSchemaSimpleType) {
            XmlSchemaSimpleType simpleSchemaType = (XmlSchemaSimpleType) schemaType;
            XmlSchemaSimpleTypeContent content = simpleSchemaType.getContent();
            if (content != null) {
                XmlSchemaSimpleTypeRestriction typeRestriction = (XmlSchemaSimpleTypeRestriction) content;
                if (typeRestriction.getFacets().getCount() > 0) {
                    fieldTypeName = typeRestriction.getBaseTypeName().getLocalPart();
                    return new EnumerationFieldMetadata(currentContainingType, isKey, elementName, fieldTypeName);
                }
            }
        }


        FieldMetadata metadata = isReference ? new ReferenceUnaryFieldMetadata(currentContainingType, isKey, elementName, fieldTypeName, referencedType, foreignKeyInfo, fkIntegrity, fkIntegrityOverride) : new SimpleTypeFieldMetadata(currentContainingType, isKey, elementName, fieldTypeName);
        if (isCollection) {
            if (isReference) {
                metadata = new ReferenceCollectionFieldMetadata(currentContainingType, elementName, isKey, (ReferenceUnaryFieldMetadata) metadata, foreignKeyInfo, fkIntegrity, fkIntegrityOverride);
            } else {
                metadata = new SimpleTypeCollectionFieldMetadata(currentContainingType, elementName, isKey, (SimpleTypeFieldMetadata) metadata);
            }
        }

        return metadata;
    }

    private void addTypeMetadata(TypeMetadata typeMetadata) {
        Map<String, TypeMetadata> nameSpace = allTypes.get(targetNamespace);
        if (nameSpace == null) {
            nameSpace = new HashMap<String, TypeMetadata>();
            allTypes.put(targetNamespace, nameSpace);
        }

        allTypes.get(targetNamespace).put(typeMetadata.getName(), typeMetadata);
    }

}
