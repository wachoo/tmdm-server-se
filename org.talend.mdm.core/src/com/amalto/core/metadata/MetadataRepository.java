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

import com.amalto.core.metadata.xsd.XmlSchemaVisitor;
import com.amalto.core.metadata.xsd.XmlSchemaWalker;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.ws.commons.schema.*;
import org.talend.mdm.commmon.util.core.ICoreConstants;

import javax.xml.namespace.QName;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 *
 */
public class MetadataRepository implements MetadataVisitable, XmlSchemaVisitor {

    private final static List<XmlSchemaAnnotationProcessor> XML_ANNOTATIONS_PROCESSORS = Arrays.asList(new ForeignKeyProcessor(), new UserAccessProcessor(), new SchematronProcessor());

    private final static String USER_NAMESPACE = StringUtils.EMPTY;

    private final Map<String, Map<String, TypeMetadata>> allTypes = new HashMap<String, Map<String, TypeMetadata>>();

    private final Stack<Set<String>> typeMetadataKeyStack = new Stack<Set<String>>();

    private final Stack<ComplexTypeMetadata> currentTypeStack = new Stack<ComplexTypeMetadata>();

    private final Map<String, ComplexTypeMetadata> nonInstantiableTypes = new HashMap<String, ComplexTypeMetadata>();

    private String targetNamespace;

    public TypeMetadata getType(String name) {
        return getType(USER_NAMESPACE, name);
    }

    public String getUserNamespace() {
        return USER_NAMESPACE;
    }

    public ComplexTypeMetadata getComplexType(String typeName) {
        try {
            return (ComplexTypeMetadata) getType(USER_NAMESPACE, typeName);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Type named '" + typeName + "' is not a complex type.");
        }
    }

    public TypeMetadata getNonInstantiableType(String name) {
        return nonInstantiableTypes.get(name);
    }

    public TypeMetadata getType(String nameSpace, String name) {
        Map<String, TypeMetadata> nameSpaceTypes = allTypes.get(nameSpace);
        if (nameSpaceTypes == null) {
            return null;
        }
        return nameSpaceTypes.get(name);
    }

    /**
     * @return Returns only {@link ComplexTypeMetadata} types defined in the data model by the MDM user (no types
     *         potentially defined in other name spaces such as the XML schema's one).
     */
    public Collection<ComplexTypeMetadata> getUserComplexTypes() {
        List<ComplexTypeMetadata> complexTypes = new LinkedList<ComplexTypeMetadata>();
        // User types are all located in the default (empty) name space.
        Collection<TypeMetadata> namespaceTypes = allTypes.get(USER_NAMESPACE).values();
        for (TypeMetadata namespaceType : namespaceTypes) {
            if (namespaceType instanceof ComplexTypeMetadata) {
                complexTypes.add((ComplexTypeMetadata) namespaceType);
            }
        }
        return complexTypes;
    }

    public Collection<TypeMetadata> getTypes() {
        List<TypeMetadata> allTypes = new LinkedList<TypeMetadata>();
        Collection<Map<String, TypeMetadata>> nameSpaces = this.allTypes.values();
        for (Map<String, TypeMetadata> nameSpace : nameSpaces) {
            allTypes.addAll(nameSpace.values());
        }

        return allTypes;
    }

    public Collection<TypeMetadata> getTypes(String namespace) {
        return this.allTypes.get(namespace).values();
    }

    public void load(InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream can not be null.");
        }

        XmlSchemaCollection collection = new XmlSchemaCollection();
        collection.read(new InputStreamReader(inputStream), new ValidationEventHandler());

        XmlSchemaWalker.walk(collection, this);

        // "Freeze" all types (a consequence of this will be validation of all reference fields).
        for (ComplexTypeMetadata type : getUserComplexTypes()) {
            type.freeze();
        }
        for (ComplexTypeMetadata type : nonInstantiableTypes.values()) {
            type.freeze();
        }
    }

    public <T> T accept(MetadataVisitor<T> visitor) {
        return visitor.visit(this);
    }

    /*
     * XML Schema parse.
     */
    public void visitSchema(XmlSchema xmlSchema) {
        targetNamespace = xmlSchema.getTargetNamespace() == null ? USER_NAMESPACE : xmlSchema.getTargetNamespace();
        XmlSchemaWalker.walk(xmlSchema, this);

        if (!currentTypeStack.isEmpty()) {
            // At the end of data model parsing, we expect all entity types to be processed.
            throw new IllegalStateException(currentTypeStack.size() + " types have not been correctly parsed.");
        }
    }

    public void visitSimpleType(XmlSchemaSimpleType type) {
        String typeName = type.getName();
        List<TypeMetadata> superTypes = new LinkedList<TypeMetadata>();
        if (typeName == null) {
            // Anonymous simple type (expects this is a restriction of a simple type or fails).
            XmlSchemaSimpleTypeContent content = type.getContent();
            if (content != null) {
                if (content instanceof XmlSchemaSimpleTypeRestriction) {
                    QName baseTypeName = ((XmlSchemaSimpleTypeRestriction) content).getBaseTypeName();
                    typeName = baseTypeName.getLocalPart();
                } else {
                    throw new NotImplementedException("Support for " + content);
                }
            } else {
                throw new NotImplementedException("Support for " + type);
            }
        } else {
            // Simple type might inherit from other simple types (i.e. UUID from string).
            XmlSchemaSimpleTypeContent content = type.getContent();
            if (content != null) {
                if (content instanceof XmlSchemaSimpleTypeRestriction) {
                    QName baseTypeName = ((XmlSchemaSimpleTypeRestriction) content).getBaseTypeName();
                    superTypes.add(new SoftTypeRef(this, baseTypeName.getNamespaceURI(), baseTypeName.getLocalPart()));
                }
            }
        }

        TypeMetadata typeMetadata = getType(targetNamespace, typeName);
        if (typeMetadata == null) {
            typeMetadata = new SimpleTypeMetadata(targetNamespace, typeName);
            for (TypeMetadata superType : superTypes) {
                typeMetadata.addSuperType(superType, this);
            }
            addTypeMetadata(typeMetadata);
        }
    }

    public void visitElement(XmlSchemaElement element) {
        if (currentTypeStack.isEmpty()) { // "top level" elements means new MDM entity type
            String typeName = element.getName();
            if (allTypes.get(getUserNamespace()) != null) {
                if (allTypes.get(getUserNamespace()).containsKey(typeName)) {
                    // Ignore another definition (already processed).
                    return;
                }
            }

            // Id fields
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

            typeMetadataKeyStack.push(idFields);
            {
                ComplexTypeMetadata type = getComplexType(typeName);
                if (type == null) { // Take type from repository if already built
                    XmlSchemaAnnotationProcessorState state;
                    try {
                        XmlSchemaAnnotation annotation = element.getAnnotation();
                        state = new XmlSchemaAnnotationProcessorState();
                        for (XmlSchemaAnnotationProcessor processor : XML_ANNOTATIONS_PROCESSORS) {
                            processor.process(this, type, annotation, state);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException("Annotation processing exception while parsing info for type '" + typeName + "'.", e);
                    }

                    // If write is not allowed for everyone, at least add "administration".
                    if (state.getAllowWrite().isEmpty()) {
                        state.getAllowWrite().add(ICoreConstants.ADMIN_PERMISSION);
                    }

                    type = new ComplexTypeMetadataImpl(targetNamespace,
                            typeName,
                            state.getAllowWrite(),
                            state.getDenyCreate(),
                            state.getHide(),
                            state.getDenyPhysicalDelete(),
                            state.getDenyLogicalDelete(),
                            state.getSchematron());
                    addTypeMetadata(type);
                }
                currentTypeStack.push(type);
                // Walk the fields
                {
                    XmlSchemaWalker.walk(element.getSchemaType(), this);
                }
                currentTypeStack.pop();

                // If type's keys are defined in super type (but not defined as key in super type), register as keys
                // references to super type's fields.
                Set<String> unresolvedIds = typeMetadataKeyStack.peek();
                if (!unresolvedIds.isEmpty()) {
                    for (String unresolvedId : unresolvedIds) {
                        type.registerKey(new SoftIdFieldRef(this, type.getName(), unresolvedId));
                    }
                }
            }
            typeMetadataKeyStack.pop();
        } else { // Non "top level" elements means fields for the MDM entity type being parsed
            FieldMetadata fieldMetadata = createFieldMetadata(element, currentTypeStack.peek());
            currentTypeStack.peek().addField(fieldMetadata);
        }
    }

    public void visitComplexType(XmlSchemaComplexType type) {
        String typeName = type.getName();
        boolean isNonInstantiableType = currentTypeStack.isEmpty();
        if (isNonInstantiableType) {
            if (allTypes.get(getUserNamespace()) != null) {
                if (allTypes.get(getUserNamespace()).containsKey(typeName)) {
                    // Ignore another definition of type (already processed).
                    return;
                }
            }
            // There's no current 'entity' type being parsed, this is a complex type not to be used for entity but
            // might be referenced by others entities (for fields, inheritance...).
            ComplexTypeMetadata nonInstantiableType = new ComplexTypeMetadataImpl(targetNamespace, typeName);
            nonInstantiableTypes.put(typeName, nonInstantiableType);
            currentTypeStack.push(nonInstantiableType);
            typeMetadataKeyStack.push(Collections.<String>emptySet());
        }

        XmlSchemaParticle contentTypeParticle = type.getParticle();
        if (contentTypeParticle != null && contentTypeParticle instanceof XmlSchemaGroupBase) {
            XmlSchemaObjectCollection items = ((XmlSchemaGroupBase) contentTypeParticle).getItems();
            Iterator itemsIterator = items.getIterator();
            while (itemsIterator.hasNext()) {
                XmlSchemaObject schemaObject = (XmlSchemaObject) itemsIterator.next();
                XmlSchemaWalker.walk(schemaObject, this);
            }
        } else if (contentTypeParticle != null) {
            throw new IllegalArgumentException("Not supported XML Schema particle: " + contentTypeParticle.getClass().getName());
        }

        // Adds the type information about super types.
        XmlSchemaContentModel contentModel = type.getContentModel();
        if (contentModel != null) {
            XmlSchemaContent content = contentModel.getContent();
            if (content != null) {
                if (content instanceof XmlSchemaComplexContentExtension) {
                    QName baseTypeName = ((XmlSchemaComplexContentExtension) content).getBaseTypeName();
                    // Check if base type has already been parsed (means a complex type has been visited). If no
                    // complex type was visited, this is a direct reference to an element.
                    String fieldTypeName = baseTypeName.getLocalPart();
                    currentTypeStack.peek().addSuperType(new SoftTypeRef(this, targetNamespace, fieldTypeName), this);

                    XmlSchemaParticle particle = ((XmlSchemaComplexContentExtension) content).getParticle();
                    if (particle != null) {
                        if (particle instanceof XmlSchemaGroupBase) {
                            XmlSchemaObjectCollection items = ((XmlSchemaGroupBase) particle).getItems();
                            Iterator itemsIterator = items.getIterator();
                            while (itemsIterator.hasNext()) {
                                XmlSchemaWalker.walk(((XmlSchemaElement) itemsIterator.next()), this);
                            }
                        } else {
                            throw new IllegalStateException("Not supported: " + particle.getClass().getName());
                        }
                    }
                }
            }
        }

        if (isNonInstantiableType) {
            typeMetadataKeyStack.pop();
            currentTypeStack.pop();
        }
    }

    // TODO To refactor once test coverage is good.
    private FieldMetadata createFieldMetadata(XmlSchemaElement element, ComplexTypeMetadata containingType) {
        String fieldName = element.getName();
        boolean isMany = element.getMaxOccurs() > 1;
        boolean isKey = typeMetadataKeyStack.peek().remove(fieldName);

        XmlSchemaAnnotationProcessorState state;
        try {
            XmlSchemaAnnotation annotation = element.getAnnotation();
            state = new XmlSchemaAnnotationProcessorState();
            for (XmlSchemaAnnotationProcessor processor : XML_ANNOTATIONS_PROCESSORS) {
                processor.process(this, containingType, annotation, state);
            }
        } catch (Exception e) {
            throw new RuntimeException("Annotation processing exception while parsing info for field '" + fieldName + "' in type '" + containingType.getName() + "'", e);
        }

        boolean isMandatory = element.getMinOccurs() > 0;
        boolean isContained = false;
        boolean isReference = state.isReference();
        boolean fkIntegrity = state.isFkIntegrity();
        boolean fkIntegrityOverride = state.isFkIntegrityOverride();
        FieldMetadata foreignKeyInfo = state.getForeignKeyInfo();
        TypeMetadata fieldType = state.getFieldType();
        FieldMetadata referencedField = state.getReferencedField();
        TypeMetadata referencedType = state.getReferencedType();
        List<String> hideUsers = state.getHide();
        List<String> allowWriteUsers = state.getAllowWrite();

        // TODO If allowWriteUsers is empty, put ICoreConstants.admin???

        if (foreignKeyInfo != null && fieldType == null) {
            throw new IllegalArgumentException("Invalid foreign key definition for field '" + fieldName + "' in type '" + containingType.getName() + "'.");
        }

        XmlSchemaType schemaType = element.getSchemaType();
        if (schemaType instanceof XmlSchemaSimpleType) {
            XmlSchemaSimpleType simpleSchemaType = (XmlSchemaSimpleType) schemaType;
            XmlSchemaSimpleTypeContent content = simpleSchemaType.getContent();
            if (schemaType.getQName() != null) { // Null QNames may happen for anonymous types extending other types.
                fieldType = new SoftTypeRef(this, schemaType.getQName().getNamespaceURI(), schemaType.getQName().getLocalPart());
            }
            if (content != null) {
                XmlSchemaSimpleTypeRestriction typeRestriction = (XmlSchemaSimpleTypeRestriction) content;
                if (fieldType == null) {
                    QName baseTypeName = typeRestriction.getBaseTypeName();
                    fieldType = new SoftTypeRef(this, baseTypeName.getNamespaceURI(), baseTypeName.getLocalPart());
                }
                if (typeRestriction.getFacets().getCount() > 0) {
                    boolean isEnumeration = false;
                    for (int i = 0; i < typeRestriction.getFacets().getCount(); i++) {
                        XmlSchemaObject item = typeRestriction.getFacets().getItem(i);
                        if (item instanceof XmlSchemaEnumerationFacet) {
                            isEnumeration = true;
                        }
                    }
                    if (isEnumeration) {
                        return new EnumerationFieldMetadata(containingType, isKey, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                    } else {
                        return new SimpleTypeFieldMetadata(containingType, isKey, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                    }
                } else {
                    return new SimpleTypeFieldMetadata(containingType, isKey, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                }
            }
        }

        if (fieldType == null) {
            QName qName = element.getSchemaTypeName();
            if (qName != null) {
                TypeMetadata metadata = getType(qName.getNamespaceURI(), qName.getLocalPart());
                if (metadata != null) {
                    fieldType = new SoftTypeRef(this, metadata.getNamespace(), metadata.getName());
                } else {
                    if (schemaType instanceof XmlSchemaComplexType) {
                        referencedType = new ContainedComplexTypeRef(currentTypeStack.peek(), targetNamespace, element.getName(), new SoftTypeRef(this, targetNamespace, schemaType.getName()));
                        isContained = true;
                    } else if (schemaType instanceof XmlSchemaSimpleType) {
                        fieldType = new SoftTypeRef(this, schemaType.getSourceURI(), schemaType.getName());
                        XmlSchemaWalker.walk(schemaType, this);
                    } else {
                        throw new NotImplementedException("Support for '" + schemaType.getClass() + "'.");
                    }
                }
            } else { // Ref & anonymous complex type
                isReference = false;
                QName refName = element.getRefName();
                if (schemaType != null) {
                    referencedType = new ContainedComplexTypeMetadata(currentTypeStack.peek(), targetNamespace, element.getName());
                    fieldType = referencedType;
                    isContained = true;
                    currentTypeStack.push((ComplexTypeMetadata) referencedType);
                    typeMetadataKeyStack.push(Collections.<String>emptySet());
                    {
                        XmlSchemaWalker.walk(schemaType, this);
                    }
                    typeMetadataKeyStack.pop();
                    currentTypeStack.pop();
                } else if (refName != null) {
                    fieldType = new SoftTypeRef(this, refName.getNamespaceURI(), refName.getLocalPart());
                } else {
                    throw new NotImplementedException();
                }
            }
        }

        if (isReference) {
            return new ReferenceFieldMetadata(containingType, isKey, isMany, isMandatory, fieldName, (ComplexTypeMetadata) referencedType, referencedField, foreignKeyInfo, fkIntegrity, fkIntegrityOverride, allowWriteUsers, hideUsers);
        } else if (isContained) {
            return new ContainedTypeFieldMetadata(containingType, isMany, isMandatory, fieldName, (ContainedComplexTypeMetadata) referencedType, allowWriteUsers, hideUsers);
        } else {
            return new SimpleTypeFieldMetadata(containingType, isKey, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
        }
    }

    public void addTypeMetadata(TypeMetadata typeMetadata) {
        String namespace = typeMetadata.getNamespace();
        Map<String, TypeMetadata> nameSpace = allTypes.get(namespace);
        if (nameSpace == null) {
            nameSpace = new HashMap<String, TypeMetadata>();
            allTypes.put(namespace, nameSpace);
        }

        allTypes.get(namespace).put(typeMetadata.getName(), typeMetadata);
    }


    public void close() {
        allTypes.clear();
        nonInstantiableTypes.clear();
    }

    public Collection<ComplexTypeMetadata> getNonInstantiableTypes() {
        return nonInstantiableTypes.values();
    }
}