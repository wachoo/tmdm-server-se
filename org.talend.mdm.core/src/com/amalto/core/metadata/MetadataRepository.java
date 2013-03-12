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

import com.amalto.core.metadata.xsd.XSDVisitor;
import com.amalto.core.metadata.xsd.XmlSchemaVisitor;
import com.amalto.core.metadata.xsd.XmlSchemaWalker;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.ws.commons.schema.*;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.xsd.*;
import org.eclipse.xsd.util.XSDParser;
import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.w3c.dom.Element;
import org.xml.sax.helpers.LocatorImpl;

import javax.xml.XMLConstants;
import javax.xml.namespace.QName;
import java.io.*;
import java.util.*;

/**
 *
 */
public class MetadataRepository implements MetadataVisitable, XmlSchemaVisitor, XSDVisitor {

    public static final String COMPLEX_TYPE_NAME = "metadata.complex.type.name"; //$NON-NLS-1$

    public static final String DATA_MAX_LENGTH = "metadata.data.length"; //$NON-NLS-1$

    public static final String XSD_LINE_NUMBER = "metadata.xsd.line"; //$NON-NLS-1$

    public static final String XSD_COLUMN_NUMBER = "metadata.xsd.column"; //$NON-NLS-1$

    private static final String XSD_DOM_ELEMENT = "metadata.xsd.dom.element"; //$NON-NLS-1$

    private static final String ANONYMOUS_PREFIX = "X_ANONYMOUS";

    private static final Logger LOGGER = Logger.getLogger(MetadataRepository.class);

    private final static List<XmlSchemaAnnotationProcessor> XML_ANNOTATIONS_PROCESSORS = Arrays.asList(new ForeignKeyProcessor(), new UserAccessProcessor(), new SchematronProcessor());

    private final static String USER_NAMESPACE = StringUtils.EMPTY;

    public static final boolean USE_APACHE_LIBRARY = true; // TODO Temp property

    private final Map<String, Map<String, TypeMetadata>> entityTypes = new TreeMap<String, Map<String, TypeMetadata>>();

    private final Map<String, Map<String, TypeMetadata>> nonInstantiableTypes = new TreeMap<String, Map<String, TypeMetadata>>();
    private final Stack<ComplexTypeMetadata> currentTypeStack = new Stack<ComplexTypeMetadata>();

    private String targetNamespace;

    private int anonymousCounter = 0;

    public MetadataRepository() {
        // TMDM-4444: Adds standard Talend types such as UUID.
        try {
            XmlSchemaCollection collection = new XmlSchemaCollection();
            InputStream internalTypes = MetadataRepository.class.getResourceAsStream("talend_types.xsd"); //$NON-NLS-1$
            if (internalTypes == null) {
                throw new IllegalStateException("Could not find internal type data model.");
            }
            collection.read(new InputStreamReader(internalTypes, "UTF-8"), new ValidationEventHandler()); //$NON-NLS-1$
            XmlSchemaWalker.walk(collection, this);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Could not parse internal data model.", e); //$NON-NLS-1$
        }
    }

    public TypeMetadata getType(String name) {
        return getType(USER_NAMESPACE, name);
    }

    public String getUserNamespace() {
        return USER_NAMESPACE;
    }

    public ComplexTypeMetadata getComplexType(String typeName) {
        try {
            return (ComplexTypeMetadata) getType(USER_NAMESPACE, typeName.trim());
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Type named '" + typeName + "' is not a complex type.");
        }
    }

    public TypeMetadata getType(String nameSpace, String name) {
        if (nameSpace == null) {
            nameSpace = StringUtils.EMPTY;
        }
        Map<String, TypeMetadata> nameSpaceTypes = entityTypes.get(nameSpace);
        if (nameSpaceTypes == null) {
            return null;
        }
        return nameSpaceTypes.get(name.trim());
    }

    /**
     * @return Returns only {@link ComplexTypeMetadata} types defined in the data model by the MDM user (no types
     *         potentially defined in other name spaces such as the XML schema's one).
     */
    public Collection<ComplexTypeMetadata> getUserComplexTypes() {
        List<ComplexTypeMetadata> complexTypes = new LinkedList<ComplexTypeMetadata>();
        // User types are all located in the default (empty) name space.
        Map<String, TypeMetadata> userNamespace = entityTypes.get(USER_NAMESPACE);
        if (userNamespace == null) {
            return Collections.emptyList();
        }
        Collection<TypeMetadata> namespaceTypes = userNamespace.values();
        for (TypeMetadata namespaceType : namespaceTypes) {
            if (namespaceType instanceof ComplexTypeMetadata) {
                complexTypes.add((ComplexTypeMetadata) namespaceType);
            }
        }
        return complexTypes;
    }

    public Collection<TypeMetadata> getTypes() {
        List<TypeMetadata> allTypes = new LinkedList<TypeMetadata>();
        Collection<Map<String, TypeMetadata>> nameSpaces = entityTypes.values();
        for (Map<String, TypeMetadata> nameSpace : nameSpaces) {
            allTypes.addAll(nameSpace.values());
        }
        nameSpaces = nonInstantiableTypes.values();
        for (Map<String, TypeMetadata> nameSpace : nameSpaces) {
            allTypes.addAll(nameSpace.values());
        }
        return allTypes;
    }

    public TypeMetadata getNonInstantiableType(String namespace, String typeName) {
        if (namespace == null) {
            namespace = StringUtils.EMPTY;
        }
        Map<String, TypeMetadata> map = nonInstantiableTypes.get(namespace);
        if (map != null) {
            return map.get(typeName.trim());
        }
        return null;
    }

    public List<ComplexTypeMetadata> getNonInstantiableTypes() {
        Map<String, TypeMetadata> map = nonInstantiableTypes.get(USER_NAMESPACE);
        List<ComplexTypeMetadata> nonInstantiableTypes = new LinkedList<ComplexTypeMetadata>();
        if (map != null) {
            for (TypeMetadata typeMetadata : map.values()) {
                if (typeMetadata instanceof ComplexTypeMetadata) {
                    nonInstantiableTypes.add((ComplexTypeMetadata) typeMetadata);
                }
            }
        }
        return nonInstantiableTypes;
    }

    public void load(InputStream inputStream) {
        load(inputStream, DefaultValidationHandler.INSTANCE);
    }

    public void load(InputStream inputStream, ValidationHandler handler) {
        if (inputStream == null) {
            throw new IllegalArgumentException("Input stream can not be null.");
        }
        // Validates data model using shared studio / server classes
        // Load user defined data model now
        if (USE_APACHE_LIBRARY) {
            try {
                XmlSchemaCollection collection = new XmlSchemaCollection();
                collection.read(new InputStreamReader(inputStream, "UTF-8"), new ValidationEventHandler()); //$NON-NLS-1$
                XmlSchemaWalker.walk(collection, this);
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException("Could not parse data model.", e); //$NON-NLS-1$
            }
        } else {
            Map<String, Object> options = new HashMap<String, Object>();
            options.put(Resource.OPTION_SAVE_ONLY_IF_CHANGED, Resource.OPTION_SAVE_ONLY_IF_CHANGED_MEMORY_BUFFER);
            XSDParser parse = new XSDParser(options);
            parse.setDocumentLocator(new LocatorImpl());
            parse.parse(inputStream);
            XSDSchema schema = parse.getSchema();
            schema.validate();
            EList<XSDDiagnostic> diagnostics = schema.getDiagnostics();
            for (XSDDiagnostic diagnostic : diagnostics) {
                XSDDiagnosticSeverity severity = diagnostic.getSeverity();
                if (severity.equals(XSDDiagnosticSeverity.ERROR_LITERAL)) {
                    handler.error(null, "XSD validation error: " + diagnostic.getMessage(), -1, -1);
                } else if (severity.equals(XSDDiagnosticSeverity.WARNING_LITERAL)) {
                    handler.error(null, "XSD validation warning: " + diagnostic.getMessage(), -1, -1);
                }
            }
            XmlSchemaWalker.walk(schema, this);
        }
        // TMDM-4876 Additional processing for entity inheritance
        resolveAdditionalSuperTypes(this, handler);
        // "Freeze" all types (a consequence of this will be validation of all fields).
        for (TypeMetadata type : getTypes()) {
            type.freeze(handler);
        }
    }

    private static void resolveAdditionalSuperTypes(MetadataRepository repository, ValidationHandler handler) {
        Collection<ComplexTypeMetadata> types = repository.getUserComplexTypes();
        for (TypeMetadata current : types) {
            String complexTypeName = current.getData(COMPLEX_TYPE_NAME);
            if (complexTypeName != null) {
                TypeMetadata nonInstantiableType = repository.getNonInstantiableType(USER_NAMESPACE, complexTypeName);
                if (!nonInstantiableType.getSuperTypes().isEmpty()) {
                    if (nonInstantiableType.getSuperTypes().size() > 1) {
                        handler.error(nonInstantiableType,
                                "Multiple inheritance is not supported.",
                                nonInstantiableType.<Integer>getData(MetadataRepository.XSD_LINE_NUMBER),
                                nonInstantiableType.<Integer>getData(MetadataRepository.XSD_COLUMN_NUMBER));
                    }
                    TypeMetadata superType = nonInstantiableType.getSuperTypes().iterator().next();
                    ComplexTypeMetadata entitySuperType = null;
                    for (TypeMetadata entity : types) {
                        if (superType.getName().equals(entity.getData(COMPLEX_TYPE_NAME))) {
                            entitySuperType = (ComplexTypeMetadata) entity;
                            break;
                        }
                    }
                    if (entitySuperType != null) {
                        current.addSuperType(entitySuperType, repository);
                    }
                }
            }
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
            if (LOGGER.isDebugEnabled()) {
                StringBuilder builder = new StringBuilder();
                for (ComplexTypeMetadata unprocessedType : currentTypeStack) {
                    builder.append(unprocessedType.getName()).append(" "); //$NON-NLS-1$
                }
                LOGGER.debug("Unprocessed types: " + builder);
            }
            // At the end of data model parsing, we expect all entity types to be processed.
            throw new IllegalStateException(currentTypeStack.size() + " types have not been correctly parsed.");
        }
    }

    public void visitSimpleType(XmlSchemaSimpleType type) {
        String typeName = type.getName();
        TypeMetadata typeMetadata = getNonInstantiableType(targetNamespace, typeName);
        if (typeMetadata == null) {
            typeMetadata = new SimpleTypeMetadata(targetNamespace, typeName);
        }
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
                    XmlSchemaSimpleTypeRestriction simpleTypeRestriction = (XmlSchemaSimpleTypeRestriction) content;
                    QName baseTypeName = simpleTypeRestriction.getBaseTypeName();
                    superTypes.add(new SoftTypeRef(this, baseTypeName.getNamespaceURI(), baseTypeName.getLocalPart(), false));
                    XmlSchemaObjectCollection facets = simpleTypeRestriction.getFacets();
                    Iterator facetsIterator = facets.getIterator();
                    while(facetsIterator.hasNext()) {
                        Object currentFacet = facetsIterator.next();
                        if (currentFacet instanceof XmlSchemaMaxLengthFacet) {
                            typeMetadata.setData(MetadataRepository.DATA_MAX_LENGTH, String.valueOf(((XmlSchemaMaxLengthFacet) currentFacet).getValue()));
                        } else if (LOGGER.isDebugEnabled()) {
                            LOGGER.debug("Ignore simple type facet on type '" + typeName + "': " + currentFacet);
                        }
                    }
                }
            }
        }
        if (getNonInstantiableType(targetNamespace, typeName) == null) {
            for (TypeMetadata superType : superTypes) {
                typeMetadata.addSuperType(superType, this);
            }
            addTypeMetadata(typeMetadata);
        }
    }

    public void visitElement(XmlSchemaElement element) {
        if (currentTypeStack.isEmpty()) { // "top level" elements means new MDM entity type
            String typeName = element.getName();
            if (entityTypes.get(getUserNamespace()) != null) {
                if (entityTypes.get(getUserNamespace()).containsKey(typeName)) {
                    // Ignore another definition (already processed).
                    return;
                }
            }
            // Id fields
            List<String> idFields = new LinkedList<String>();
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
            ComplexTypeMetadata type = getComplexType(typeName); // Take type from repository if already built
            if (type == null) {
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
                        state.getSchematron(),
                        true);
                addTypeMetadata(type);
            }
            // Walk the fields
            currentTypeStack.push(type);
            {
                XmlSchemaWalker.walk(element.getSchemaType(), this);
            }
            currentTypeStack.pop();
            // Super types
            QName substitutionGroup = element.getSubstitutionGroup();
            if (substitutionGroup != null) {
                type.addSuperType(new SoftTypeRef(this, substitutionGroup.getNamespaceURI(), substitutionGroup.getLocalPart(), true), this);
            }
            // Register keys (TMDM-4470).
            for (String unresolvedId : idFields) {
                type.registerKey(new SoftIdFieldRef(this, type.getName(), unresolvedId));
            }
        } else { // Non "top level" elements means fields for the MDM entity type being parsed
            FieldMetadata fieldMetadata = createFieldMetadata(element, currentTypeStack.peek());
            currentTypeStack.peek().addField(fieldMetadata);
        }
    }

    public void visitComplexType(XmlSchemaComplexType type) {
        String typeName = type.getName();
        boolean isNonInstantiableType = currentTypeStack.isEmpty();
        if (isNonInstantiableType) {
            if (nonInstantiableTypes.get(getUserNamespace()) != null) {
                if (nonInstantiableTypes.get(getUserNamespace()).containsKey(typeName)) {
                    // Ignore another definition of type (already processed).
                    return;
                }
            }
            // There's no current 'entity' type being parsed, this is a complex type not to be used for entity but
            // might be referenced by others entities (for fields, inheritance...).
            ComplexTypeMetadata nonInstantiableType = new ComplexTypeMetadataImpl(targetNamespace, typeName, false);
            addTypeMetadata(nonInstantiableType);
            currentTypeStack.push(nonInstantiableType);
        } else {
            // Keep track of the complex type used for entity type (especially for inheritance).
            if (typeName != null) {
                currentTypeStack.peek().setData(MetadataRepository.COMPLEX_TYPE_NAME, typeName);
            }
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
                    // Check if base type has already been parsed (means a complex type has been visited). If no
                    // complex type was visited, this is a direct reference to an element.
                    QName baseTypeName = ((XmlSchemaComplexContentExtension) content).getBaseTypeName();
                    String fieldTypeName = baseTypeName.getLocalPart();
                    currentTypeStack.peek().addSuperType(new SoftTypeRef(this, targetNamespace, fieldTypeName, false), this);

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
            currentTypeStack.pop();
        }
    }

    // TODO To refactor once test coverage is good.
    private FieldMetadata createFieldMetadata(XmlSchemaElement element, ComplexTypeMetadata containingType) {
        String fieldName = element.getName();
        boolean isMany = element.getMaxOccurs() > 1;
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
                fieldType = new SoftTypeRef(this, schemaType.getQName().getNamespaceURI(), schemaType.getQName().getLocalPart(), false);
            }
            if (content != null) {
                XmlSchemaSimpleTypeRestriction typeRestriction = (XmlSchemaSimpleTypeRestriction) content;
                if (fieldType == null) {
                    QName baseTypeName = typeRestriction.getBaseTypeName();
                    fieldType = new SoftTypeRef(this, baseTypeName.getNamespaceURI(), baseTypeName.getLocalPart(), false);
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
                        return new EnumerationFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                    } else {
                        return new SimpleTypeFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                    }
                } else {
                    return new SimpleTypeFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                }
            }
        }
        if (fieldType == null) {
            QName qName = element.getSchemaTypeName();
            if (qName != null) {
                TypeMetadata metadata = getType(qName.getNamespaceURI(), qName.getLocalPart());
                if (metadata != null) {
                    referencedType = new ContainedComplexTypeRef(currentTypeStack.peek(), targetNamespace, element.getName(), new SoftTypeRef(this, targetNamespace, schemaType.getName(), false));
                    isContained = true;
                } else {
                    if (schemaType == null) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' from type '" + containingType.getName() + "' has an invalid type.");
                    }
                    if (schemaType instanceof XmlSchemaComplexType) {
                        referencedType = new ContainedComplexTypeRef(currentTypeStack.peek(), targetNamespace, element.getName(), new SoftTypeRef(this, targetNamespace, schemaType.getName(), false));
                        isContained = true;
                    } else if (schemaType instanceof XmlSchemaSimpleType) {
                        fieldType = new SoftTypeRef(this, schemaType.getSourceURI(), schemaType.getName(), false);
                        XmlSchemaWalker.walk(schemaType, this);
                    } else {
                        throw new NotImplementedException("Support for '" + schemaType.getClass() + "'.");
                    }
                }
            } else { // Ref & anonymous complex type
                isReference = false;
                isContained = true;
                QName refName = element.getRefName();
                if (schemaType != null) {
                    referencedType = new ContainedComplexTypeMetadata(currentTypeStack.peek(), targetNamespace, ANONYMOUS_PREFIX + String.valueOf(anonymousCounter++));
                    fieldType = referencedType;
                    isContained = true;
                    currentTypeStack.push((ComplexTypeMetadata) referencedType);
                    XmlSchemaWalker.walk(schemaType, this);
                    currentTypeStack.pop();
                } else if (refName != null) {
                    // Reference being an element, consider references as references to entity type.
                    SoftTypeRef reference = new SoftTypeRef(this, refName.getNamespaceURI(), refName.getLocalPart(), true);
                    referencedType = new ContainedComplexTypeRef(currentTypeStack.peek(), targetNamespace, element.getName(), reference);
                    fieldType = referencedType;
                } else {
                    throw new NotImplementedException();
                }
            }
        }
        if (isReference) {
            return new ReferenceFieldMetadata(containingType,
                    false,
                    isMany,
                    isMandatory,
                    fieldName,
                    (ComplexTypeMetadata) referencedType,
                    referencedField,
                    foreignKeyInfo,
                    fkIntegrity,
                    fkIntegrityOverride,
                    fieldType,
                    allowWriteUsers,
                    hideUsers);
        } else if (isContained) {
            return new ContainedTypeFieldMetadata(containingType, isMany, isMandatory, fieldName, (ContainedComplexTypeMetadata) referencedType, allowWriteUsers, hideUsers);
        } else {
            return new SimpleTypeFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
        }
    }

    public void addTypeMetadata(TypeMetadata typeMetadata) {
        String namespace = typeMetadata.getNamespace();
        if (typeMetadata.isInstantiable()) {
            registerType(typeMetadata, namespace, entityTypes);
        } else {
            registerType(typeMetadata, namespace, nonInstantiableTypes);
        }
    }

    private static void registerType(TypeMetadata typeMetadata, String namespace, Map<String, Map<String, TypeMetadata>> typeMap) {
        Map<String, TypeMetadata> nameSpace = typeMap.get(namespace);
        if (nameSpace == null) {
            nameSpace = new HashMap<String, TypeMetadata>();
            typeMap.put(namespace, nameSpace);
        }
        typeMap.get(namespace).put(typeMetadata.getName(), typeMetadata);
    }


    public void close() {
        entityTypes.clear();
        nonInstantiableTypes.clear();
    }

    public Collection<TypeMetadata> getInstantiableTypes() {
        return entityTypes.get(USER_NAMESPACE).values();
    }

    @Override
    public void visitSchema(XSDSchema xmlSchema) {
        targetNamespace = xmlSchema.getTargetNamespace() == null ? USER_NAMESPACE : xmlSchema.getTargetNamespace();
        XmlSchemaWalker.walk(xmlSchema, this);
        if (!currentTypeStack.isEmpty()) {
            if (LOGGER.isDebugEnabled()) {
                StringBuilder builder = new StringBuilder();
                for (ComplexTypeMetadata unprocessedType : currentTypeStack) {
                    builder.append(unprocessedType.getName()).append(" "); //$NON-NLS-1$
                }
                LOGGER.debug("Unprocessed types: " + builder);
            }
            // At the end of data model parsing, we expect all entity types to be processed.
            throw new IllegalStateException(currentTypeStack.size() + " types have not been correctly parsed.");
        }
    }

    @Override
    public void visitSimpleType(XSDSimpleTypeDefinition type) {
        String typeName = type.getName();
        TypeMetadata typeMetadata = getNonInstantiableType(targetNamespace, typeName);
        if (typeMetadata == null) {
            typeMetadata = new SimpleTypeMetadata(targetNamespace, typeName);
        }
        List<TypeMetadata> superTypes = new LinkedList<TypeMetadata>();
        if (typeName == null) {
            // Anonymous simple type (expects this is a restriction of a simple type or fails).
            XSDSimpleTypeDefinition baseTypeDefinition = type.getBaseTypeDefinition();
            if (baseTypeDefinition != null) {
                typeName = baseTypeDefinition.getName();
            } else {
                throw new NotImplementedException("Support for " + type);
            }
        } else {
            // Simple type might inherit from other simple types (i.e. UUID from string).
            XSDSimpleTypeDefinition content = type.getBaseTypeDefinition();
            if (content != null) {
                superTypes.add(new SoftTypeRef(this, content.getTargetNamespace(), content.getName(), false));
                EList<XSDConstrainingFacet> facets = type.getFacetContents();
                for (XSDConstrainingFacet currentFacet : facets) {
                    if (currentFacet instanceof XSDMaxLengthFacet) {
                        typeMetadata.setData(MetadataRepository.DATA_MAX_LENGTH, String.valueOf(((XSDMaxLengthFacet) currentFacet).getValue()));
                    } else if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Ignore simple type facet on type '" + typeName + "': " + currentFacet);
                    }
                }
            }
        }
        if (getNonInstantiableType(targetNamespace, typeName) == null) {
            for (TypeMetadata superType : superTypes) {
                typeMetadata.addSuperType(superType, this);
            }
            addTypeMetadata(typeMetadata);
        }
    }

    @Override
    public void visitComplexType(XSDComplexTypeDefinition type) {
        String typeName = type.getName();
        boolean isNonInstantiableType = currentTypeStack.isEmpty();
        if (isNonInstantiableType) {
            if (nonInstantiableTypes.get(getUserNamespace()) != null) {
                if (nonInstantiableTypes.get(getUserNamespace()).containsKey(typeName)) {
                    // Ignore another definition of type (already processed).
                    return;
                }
            }
            // There's no current 'entity' type being parsed, this is a complex type not to be used for entity but
            // might be referenced by others entities (for fields, inheritance...).
            ComplexTypeMetadata nonInstantiableType = new ComplexTypeMetadataImpl(targetNamespace, typeName, false);
            // Keep line and column of definition
            nonInstantiableType.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(type.getElement()));
            nonInstantiableType.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(type.getElement()));
            nonInstantiableType.setData(XSD_DOM_ELEMENT, type.getElement());
            addTypeMetadata(nonInstantiableType);
            currentTypeStack.push(nonInstantiableType);
        } else {
            // Keep track of the complex type used for entity type (especially for inheritance).
            if (typeName != null) {
                currentTypeStack.peek().setData(MetadataRepository.COMPLEX_TYPE_NAME, typeName);
            }
        }
        XSDComplexTypeContent particle = type.getContent();
        if (particle instanceof XSDParticle) {
            XSDParticle currentParticle = (XSDParticle) particle;
            if (currentParticle.getTerm() instanceof XSDModelGroup) {
                XSDModelGroup group = (XSDModelGroup) currentParticle.getTerm();
                EList<XSDParticle> particles = group.getContents();
                for (XSDParticle p : particles) {
                    XSDParticleContent particleContent = p.getContent();
                    XmlSchemaWalker.walk(particleContent, this);
                }
            }
        } else if (particle != null) {
            throw new IllegalArgumentException("Not supported XML Schema particle: " + particle.getClass().getName());
        }
        // Adds the type information about super types.
        XSDTypeDefinition contentModel = type.getBaseTypeDefinition();
        if (contentModel != null) {
            if (!XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(contentModel.getTargetNamespace())
                    && !"anyType".equals(contentModel.getName())) {
                SoftTypeRef superType = new SoftTypeRef(this,
                        contentModel.getTargetNamespace(),
                        contentModel.getName(),
                        false);
                currentTypeStack.peek().addSuperType(superType, this);
                particle = type.getContent();
                if (particle instanceof XSDParticle) {
                    XSDParticle currentParticle = (XSDParticle) particle;
                    if (currentParticle.getTerm() instanceof XSDModelGroup) {
                        XSDModelGroup group = (XSDModelGroup) currentParticle.getTerm();
                        EList<XSDParticle> particles = group.getContents();
                        for (XSDParticle p : particles) {
                            XSDParticleContent particleContent = p.getContent();
                            XmlSchemaWalker.walk(particleContent, this);
                        }
                    }
                } else if (particle != null) {
                    throw new IllegalArgumentException("Not supported XML Schema particle: " + particle.getClass().getName());
                }
            }
        }
        if (isNonInstantiableType) {
            currentTypeStack.pop();
        }
    }

    @Override
    public void visitElement(XSDElementDeclaration element) {
        if (currentTypeStack.isEmpty()) { // "top level" elements means new MDM entity type
            String typeName = element.getName();
            if (entityTypes.get(getUserNamespace()) != null) {
                if (entityTypes.get(getUserNamespace()).containsKey(typeName)) {
                    // Ignore another definition (already processed).
                    return;
                }
            }
            // Id fields
            List<String> idFields = new LinkedList<String>();
            EList<XSDIdentityConstraintDefinition> constraints = element.getIdentityConstraintDefinitions();
            for (XSDIdentityConstraintDefinition constraint : constraints) {
                EList<XSDXPathDefinition> fields = constraint.getFields();
                for (XSDXPathDefinition field : fields) {
                    idFields.add(field.getValue());
                }
            }
            ComplexTypeMetadata type = getComplexType(typeName); // Take type from repository if already built
            if (type == null) {
                XmlSchemaAnnotationProcessorState state;
                try {
                    XSDAnnotation annotation = element.getAnnotation();
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
                        state.getSchematron(),
                        true);
                // Keep line and column of definition
                type.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
                type.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
                type.setData(XSD_DOM_ELEMENT, element.getElement());
                addTypeMetadata(type);
            }
            // Walk the fields
            currentTypeStack.push(type);
            {
                XmlSchemaWalker.walk(element.getType(), this);
            }
            currentTypeStack.pop();
            // Super types
            XSDElementDeclaration substitutionGroup = element.getSubstitutionGroupAffiliation();
            if (substitutionGroup != null
                    && !XMLConstants.W3C_XML_SCHEMA_NS_URI.equals(substitutionGroup.getTargetNamespace())
                    && !"anyType".equals(substitutionGroup.getName())) {
                if (!substitutionGroup.getResolvedElementDeclaration().equals(element)) {
                    SoftTypeRef superType = new SoftTypeRef(this,
                            substitutionGroup.getTargetNamespace(),
                            substitutionGroup.getName(),
                            true);
                    type.addSuperType(superType, this);
                }
            }
            // Register keys (TMDM-4470).
            for (String unresolvedId : idFields) {
                type.registerKey(new SoftIdFieldRef(this, type.getName(), unresolvedId));
            }
        } else { // Non "top level" elements means fields for the MDM entity type being parsed
            FieldMetadata fieldMetadata = createFieldMetadata(element, currentTypeStack.peek());
            currentTypeStack.peek().addField(fieldMetadata);
        }
    }

    // TODO To refactor once test coverage is good.
    private FieldMetadata createFieldMetadata(XSDElementDeclaration element, ComplexTypeMetadata containingType) {
        String fieldName = element.getName();
        boolean isMany = ((XSDParticle) element.getContainer()).getMaxOccurs() == -1 || ((XSDParticle) element.getContainer()).getMaxOccurs() > 1;
        XmlSchemaAnnotationProcessorState state;
        try {
            XSDAnnotation annotation = element.getAnnotation();
            state = new XmlSchemaAnnotationProcessorState();
            for (XmlSchemaAnnotationProcessor processor : XML_ANNOTATIONS_PROCESSORS) {
                processor.process(this, containingType, annotation, state);
            }
        } catch (Exception e) {
            throw new RuntimeException("Annotation processing exception while parsing info for field '" + fieldName + "' in type '" + containingType.getName() + "'", e);
        }
        boolean isMandatory = ((XSDParticle) element.getContainer()).getMinOccurs() > 0;
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
        XSDTypeDefinition schemaType = element.getType();
        if (schemaType instanceof XSDSimpleTypeDefinition) {
            XSDSimpleTypeDefinition simpleSchemaType = (XSDSimpleTypeDefinition) schemaType;
            XSDSimpleTypeDefinition content = simpleSchemaType.getBaseTypeDefinition();
            if (schemaType.getQName() != null) { // Null QNames may happen for anonymous types extending other types.
                fieldType = new SoftTypeRef(this, schemaType.getTargetNamespace(), schemaType.getName(), false);
            }
            if (isReference) {
                ReferenceFieldMetadata referenceField = new ReferenceFieldMetadata(containingType,
                        false,
                        isMany,
                        isMandatory,
                        fieldName,
                        (ComplexTypeMetadata) referencedType,
                        referencedField,
                        foreignKeyInfo,
                        fkIntegrity,
                        fkIntegrityOverride,
                        fieldType,
                        allowWriteUsers,
                        hideUsers);
                referencedField.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
                referencedField.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
                referencedField.setData(XSD_DOM_ELEMENT, element.getElement());
                return referenceField;
            }
            if (content != null) {
                if (fieldType == null) {
                    fieldType = new SoftTypeRef(this, content.getTargetNamespace(), content.getName(), false);
                }
                if (content.getFacets().size() > 0) {
                    boolean isEnumeration = false;
                    for (int i = 0; i < content.getFacets().size(); i++) {
                        XSDConstrainingFacet item = content.getFacets().get(i);
                        if (item instanceof XSDEnumerationFacet) {
                            isEnumeration = true;
                        }
                    }
                    if (isEnumeration) {
                        EnumerationFieldMetadata enumField = new EnumerationFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                        enumField.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
                        enumField.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
                        enumField.setData(XSD_DOM_ELEMENT, element.getElement());
                        return enumField;
                    } else {
                        FieldMetadata field = new SimpleTypeFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                        field.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
                        field.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
                        field.setData(XSD_DOM_ELEMENT, element.getElement());
                        return field;
                    }
                } else {
                    FieldMetadata field = new SimpleTypeFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
                    field.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
                    field.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
                    field.setData(XSD_DOM_ELEMENT, element.getElement());
                    return field;
                }
            }
        }
        if (fieldType == null) {
            String qName = element.getType() == null ? null : element.getType().getQName();
            if (qName != null) {
                TypeMetadata metadata = getType(element.getType().getTargetNamespace(), element.getType().getName());
                if (metadata != null) {
                    referencedType = new ContainedComplexTypeRef(currentTypeStack.peek(), targetNamespace, element.getName(), new SoftTypeRef(this, targetNamespace, schemaType.getName(), false));
                    isContained = true;
                } else {
                    if (schemaType == null) {
                        throw new IllegalArgumentException("Field '" + fieldName + "' from type '" + containingType.getName() + "' has an invalid type.");
                    }
                    if (schemaType instanceof XSDComplexTypeDefinition) {
                        referencedType = new ContainedComplexTypeRef(currentTypeStack.peek(), targetNamespace, element.getName(), new SoftTypeRef(this, targetNamespace, schemaType.getName(), false));
                        isContained = true;
                    } else if (schemaType instanceof XmlSchemaSimpleType) {
                        fieldType = new SoftTypeRef(this, schemaType.getTargetNamespace(), schemaType.getName(), false);
                        XmlSchemaWalker.walk(schemaType, this);
                    } else {
                        throw new NotImplementedException("Support for '" + schemaType.getClass() + "'.");
                    }
                }
            } else { // Ref & anonymous complex type
                isContained = true;
                XSDElementDeclaration refName = element.getResolvedElementDeclaration();
                if (schemaType != null) {
                    referencedType = new ContainedComplexTypeMetadata(currentTypeStack.peek(), targetNamespace, ANONYMOUS_PREFIX + String.valueOf(anonymousCounter++));
                    fieldType = referencedType;
                    isContained = true;
                    currentTypeStack.push((ComplexTypeMetadata) referencedType);
                    XmlSchemaWalker.walk(schemaType, this);
                    currentTypeStack.pop();
                } else if (refName != null) {
                    // Reference being an element, consider references as references to entity type.
                    SoftTypeRef reference = new SoftTypeRef(this, refName.getTargetNamespace(), refName.getName(), true);
                    referencedType = new ContainedComplexTypeRef(currentTypeStack.peek(), targetNamespace, element.getName(), reference);
                    fieldType = referencedType;
                } else {
                    throw new NotImplementedException();
                }
            }
        }
        if (isContained) {
            ContainedTypeFieldMetadata containedField = new ContainedTypeFieldMetadata(containingType,
                    isMany,
                    isMandatory,
                    fieldName,
                    (ContainedComplexTypeMetadata) referencedType,
                    allowWriteUsers,
                    hideUsers);
            containedField.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
            containedField.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
            containedField.setData(XSD_DOM_ELEMENT, element.getElement());
            return containedField;
        } else {
            FieldMetadata field = new SimpleTypeFieldMetadata(containingType, false, isMany, isMandatory, fieldName, fieldType, allowWriteUsers, hideUsers);
            field.setData(XSD_LINE_NUMBER, XSDParser.getStartLine(element.getElement()));
            field.setData(XSD_COLUMN_NUMBER, XSDParser.getStartColumn(element.getElement()));
            field.setData(XSD_DOM_ELEMENT, element.getElement());
            return field;
        }
    }
}