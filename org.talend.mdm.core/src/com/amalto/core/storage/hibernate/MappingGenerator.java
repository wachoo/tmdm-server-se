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

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

// TODO Refactor (+ NON-NLS)
class MappingGenerator extends DefaultMetadataVisitor<Element> {

    private static final Logger LOGGER = Logger.getLogger(MappingGenerator.class);

    private final Document document;

    private final TableResolver resolver;

    private boolean compositeId;

    private Element parentElement;

    private boolean isDoingColumns;

    private boolean isColumnMandatory;

    private String compositeKeyPrefix;

    private final boolean generateConstrains;

    public MappingGenerator(Document document, TableResolver resolver) {
        this(document, resolver, true);
    }

    public MappingGenerator(Document document, TableResolver resolver, boolean generateConstrains) {
        this.document = document;
        this.resolver = resolver;
        this.generateConstrains = generateConstrains;
    }

    private static String shortString(String s) {
        if (s.length() < 40) {
            return s;
        }
        char[] chars = s.toCharArray();
        return __shortString(chars, 40);
    }

    private static String __shortString(char[] chars, int threshold) {
        if (chars.length < threshold) {
            return new String(chars).replace('-', '_');
        } else {
            String s = new String(ArrayUtils.subarray(chars, 0, threshold / 2)) + new String(ArrayUtils.subarray(chars, threshold / 2, chars.length)).hashCode();
            return __shortString(s.toCharArray(), threshold);
        }
    }


    @Override
    public Element visit(MetadataRepository repository) {
        // To disallow wrong usage of this class, disables visiting the whole repository
        throw new NotImplementedException("Repository visit is disabled in this visitor.");
    }

    @Override
    public Element visit(ComplexTypeMetadata complexType) {
        if (complexType.getKeyFields().isEmpty()) {
            throw new IllegalArgumentException("Type '" + complexType.getName() + "' has no key.");
        }
        if (!complexType.getSuperTypes().isEmpty()) {
            return null;
        }

        String table = resolver.get(complexType);
        String generatedClassName = ClassCreator.PACKAGE_PREFIX + complexType.getName();

        Element classElement = document.createElement("class"); //$NON-NLS-1$
        Attr className = document.createAttribute("name");  //$NON-NLS-1$
        className.setValue(generatedClassName);
        classElement.getAttributes().setNamedItem(className);
        Attr classTable = document.createAttribute("table"); //$NON-NLS-1$
        classTable.setValue(shortString(table));
        classElement.getAttributes().setNamedItem(classTable);

        // <cache usage="read-write" include="non-lazy"/>
        Element cacheElement = document.createElement("cache"); //$NON-NLS-1$
        Attr usageAttribute = document.createAttribute("usage"); //$NON-NLS-1$
        usageAttribute.setValue("read-write"); //$NON-NLS-1$
        cacheElement.getAttributes().setNamedItem(usageAttribute);
        Attr includeAttribute = document.createAttribute("include"); //$NON-NLS-1$
        includeAttribute.setValue("non-lazy"); //$NON-NLS-1$
        cacheElement.getAttributes().setNamedItem(includeAttribute);
        Attr regionAttribute = document.createAttribute("region"); //$NON-NLS-1$
        regionAttribute.setValue("region"); //$NON-NLS-1$
        cacheElement.getAttributes().setNamedItem(regionAttribute);
        classElement.appendChild(cacheElement);

        List<FieldMetadata> keyFields = complexType.getKeyFields();
        List<FieldMetadata> allFields = new ArrayList<FieldMetadata>(complexType.getFields());

        // Process key fields first (Hibernate DTD enforces IDs to be declared first in <class/> element).
        Element idParentElement = classElement;
        if (keyFields.size() > 1) {
            /*
            <composite-id>
                        <key-property column="x_enterprise" name="x_enterprise"/>
                        <key-property column="x_id" name="x_id"/>
                    </composite-id>
             */
            compositeId = true;
            idParentElement = document.createElement("composite-id"); //$NON-NLS-1$
            classElement.appendChild(idParentElement);

            Attr classAttribute = document.createAttribute("class"); //$NON-NLS-1$
            classAttribute.setValue(generatedClassName + "_ID"); //$NON-NLS-1$
            idParentElement.getAttributes().setNamedItem(classAttribute);

            Attr mappedAttribute = document.createAttribute("mapped"); //$NON-NLS-1$
            mappedAttribute.setValue("true"); //$NON-NLS-1$
            idParentElement.getAttributes().setNamedItem(mappedAttribute);
        }

        for (FieldMetadata keyField : keyFields) {
            idParentElement.appendChild(keyField.accept(this));
            allFields.remove(keyField);
        }
        compositeId = false;
        for (FieldMetadata currentField : allFields) {
            Element child = currentField.accept(this);
            if (child == null) {
                throw new IllegalArgumentException("Field type " + currentField.getClass().getName() + " is not supported.");
            }
            classElement.appendChild(child);
        }

        // Sub types
        if (!complexType.getSubTypes().isEmpty()) {
            /*
            <union-subclass name="CreditCardPayment" table="CREDIT_PAYMENT">
                   <property name="creditCardType" column=""/>
                   ...
               </union-subclass>
            */
            for (ComplexTypeMetadata subType : complexType.getSubTypes()) {
                Element unionSubclass = document.createElement("union-subclass"); //$NON-NLS-1$
                Attr name = document.createAttribute("name"); //$NON-NLS-1$
                name.setValue(ClassCreator.PACKAGE_PREFIX + subType.getName());
                unionSubclass.setAttributeNode(name);

                Attr tableName = document.createAttribute("table"); //$NON-NLS-1$
                tableName.setValue(shortString(resolver.get(subType)));
                unionSubclass.setAttributeNode(tableName);

                List<FieldMetadata> subTypeFields = subType.getFields();
                for (FieldMetadata subTypeField : subTypeFields) {
                    if (!complexType.hasField(subTypeField.getName()) && !subTypeField.isKey()) {
                        unionSubclass.appendChild(subTypeField.accept(this));
                    }
                }
                classElement.appendChild(unionSubclass);
            }
        }

        return classElement;
    }

    @Override
    public Element visit(ContainedTypeFieldMetadata containedField) {
        throw new IllegalArgumentException("Type should have been flatten before calling this method.");
    }

    @Override
    public Element visit(ContainedComplexTypeMetadata containedType) {
        throw new IllegalArgumentException("Type should have been flatten before calling this method.");
    }

    @Override
    public Element visit(EnumerationFieldMetadata enumField) {
        // TODO Not the best solution to implement a enumeration (a FK that points to constant values?).
        // handle enum fields just like simple fields
        return handleSimpleField(enumField);
    }

    @Override
    public Element visit(ReferenceFieldMetadata referenceField) {
        if (referenceField.isKey()) {
            throw new UnsupportedOperationException("FK field '" + referenceField.getName() + "' cannot be key in type '" + referenceField.getDeclaringType().getName() + "'"); // Don't support FK as key
        } else {
            String fieldName = resolver.get(referenceField);

            boolean enforceDataBaseIntegrity = generateConstrains && (!referenceField.allowFKIntegrityOverride() && referenceField.isFKIntegrity());
            TypeMetadata referencedType = referenceField.getReferencedType();
            if (!referenceField.isMany()) {
                return newManyToOneElement(fieldName, enforceDataBaseIntegrity, referenceField);
            } else {
                /*
                <list name="bars" table="foo_bar">
                     <key column="foo_id"/>
                     <many-to-many column="bar_id" class="Bar"/>
                  </list>
                 */
                Element propertyElement = document.createElement("list"); //$NON-NLS-1$
                Attr name = document.createAttribute("name"); //$NON-NLS-1$
                name.setValue(fieldName);
                propertyElement.getAttributes().setNamedItem(name);

                Attr lazy = document.createAttribute("lazy"); //$NON-NLS-1$
                lazy.setValue("false"); //$NON-NLS-1$
                propertyElement.getAttributes().setNamedItem(lazy);

                Attr joinAttribute = document.createAttribute("fetch"); //$NON-NLS-1$
                joinAttribute.setValue("select"); // Keep it "select" (Hibernate tends to duplicate results when using "fetch")
                propertyElement.getAttributes().setNamedItem(joinAttribute);

                // cascade="true"
                if (Boolean.parseBoolean(referenceField.<String>getData("SQL_DELETE_CASCADE"))) { //$NON-NLS-1$
                    Attr cascade = document.createAttribute("cascade"); //$NON-NLS-1$
                    cascade.setValue("save-update, delete"); //$NON-NLS-1$
                    propertyElement.getAttributes().setNamedItem(cascade);
                }

                Attr tableName = document.createAttribute("table"); //$NON-NLS-1$
                tableName.setValue(shortString((referenceField.getContainingType().getName() + '_' + fieldName + '_' + referencedType.getName()).toUpperCase()));
                propertyElement.getAttributes().setNamedItem(tableName);
                {
                    // <key column="foo_id"/>
                    Element key = document.createElement("key"); //$NON-NLS-1$
                    propertyElement.appendChild(key);
                    for (FieldMetadata keyField : referenceField.getContainingType().getKeyFields()) {
                        Element elementColumn = document.createElement("column"); //$NON-NLS-1$
                        Attr columnName = document.createAttribute("name"); //$NON-NLS-1$
                        columnName.setValue(keyField.getName());
                        elementColumn.getAttributes().setNamedItem(columnName);
                        key.appendChild(elementColumn);
                    }


                    // <index column="idx" />
                    Element index = document.createElement("index"); //$NON-NLS-1$
                    Attr indexColumn = document.createAttribute("column"); //$NON-NLS-1$
                    indexColumn.setValue("pos"); //$NON-NLS-1$
                    index.getAttributes().setNamedItem(indexColumn);
                    propertyElement.appendChild(index);

                    // many to many element
                    Element manyToMany = newManyToManyElement(enforceDataBaseIntegrity, referenceField);

                    propertyElement.appendChild(manyToMany);
                }


                return propertyElement;
            }
        }
    }

    private Element newManyToOneElement(String fieldName, boolean enforceDataBaseIntegrity, ReferenceFieldMetadata referencedField) {
        Element propertyElement = document.createElement("many-to-one"); //$NON-NLS-1$
        Attr propertyName = document.createAttribute("name"); //$NON-NLS-1$
        propertyName.setValue(fieldName);
        Attr className = document.createAttribute("class"); //$NON-NLS-1$
        className.setValue(ClassCreator.PACKAGE_PREFIX + referencedField.getReferencedType().getName());

        // fetch="join" lazy="false"
        Attr lazy = document.createAttribute("lazy"); //$NON-NLS-1$
        lazy.setValue("false"); //$NON-NLS-1$
        propertyElement.getAttributes().setNamedItem(lazy);

        Attr joinAttribute = document.createAttribute("fetch"); //$NON-NLS-1$
        joinAttribute.setValue("join"); //$NON-NLS-1$
        propertyElement.getAttributes().setNamedItem(joinAttribute);

        // Not null
        if (referencedField.isMandatory()) {
            Attr notNull = document.createAttribute("not-null"); //$NON-NLS-1$
            notNull.setValue("true"); //$NON-NLS-1$
            propertyElement.getAttributes().setNamedItem(notNull);
        } else {
            Attr notNull = document.createAttribute("not-null"); //$NON-NLS-1$
            notNull.setValue("false"); //$NON-NLS-1$
            propertyElement.getAttributes().setNamedItem(notNull);
        }

        // If data model authorizes fk integrity override, don't enforce database FK integrity.
        if (enforceDataBaseIntegrity) {
            // Ensure default settings for Hibernate are set (in case they change).
            Attr notFound = document.createAttribute("not-found"); //$NON-NLS-1$
            notFound.setValue("exception"); //$NON-NLS-1$
            propertyElement.getAttributes().setNamedItem(notFound);
        } else {
            // Disables all warning/errors from Hibernate.
            Attr integrity = document.createAttribute("unique"); //$NON-NLS-1$
            integrity.setValue("false"); //$NON-NLS-1$
            propertyElement.getAttributes().setNamedItem(integrity);

            Attr foreignKey = document.createAttribute("foreign-key"); //$NON-NLS-1$*
            // Disables foreign key generation for DDL.
            foreignKey.setValue("none");  //$NON-NLS-1$
            propertyElement.getAttributes().setNamedItem(foreignKey);

            Attr notFound = document.createAttribute("not-found"); //$NON-NLS-1$
            notFound.setValue("ignore"); //$NON-NLS-1$
            propertyElement.getAttributes().setNamedItem(notFound);
        }

        propertyElement.getAttributes().setNamedItem(propertyName);
        propertyElement.getAttributes().setNamedItem(className);

        isDoingColumns = true;
        isColumnMandatory = referencedField.isMandatory();
        this.parentElement = propertyElement;
        compositeKeyPrefix = referencedField.getName();
        {
            referencedField.getReferencedField().accept(this);
        }
        isDoingColumns = false;

        return propertyElement;
    }

    private Element newManyToManyElement(boolean enforceDataBaseIntegrity, ReferenceFieldMetadata referencedField) {
        // <many-to-many column="bar_id" class="Bar"/>
        Element manyToMany = document.createElement("many-to-many"); //$NON-NLS-1$

        // If data model authorizes fk integrity override, don't enforce database FK integrity.
        if (enforceDataBaseIntegrity) {
            // Ensure default settings for Hibernate are set (in case they change).
            Attr notFound = document.createAttribute("not-found"); //$NON-NLS-1$
            notFound.setValue("exception"); //$NON-NLS-1$
            manyToMany.getAttributes().setNamedItem(notFound);
        } else {
            // Disables all warning/errors from Hibernate.
            Attr integrity = document.createAttribute("unique"); //$NON-NLS-1$
            integrity.setValue("false"); //$NON-NLS-1$
            manyToMany.getAttributes().setNamedItem(integrity);

            Attr foreignKey = document.createAttribute("foreign-key"); //$NON-NLS-1$
            // Disables foreign key generation for DDL.
            foreignKey.setValue("none"); //$NON-NLS-1$
            manyToMany.getAttributes().setNamedItem(foreignKey);

            Attr notFound = document.createAttribute("not-found"); //$NON-NLS-1$
            notFound.setValue("ignore"); //$NON-NLS-1$
            manyToMany.getAttributes().setNamedItem(notFound);
        }

        Attr className = document.createAttribute("class"); //$NON-NLS-1$
        className.setValue(ClassCreator.PACKAGE_PREFIX + referencedField.getReferencedType().getName());
        manyToMany.getAttributes().setNamedItem(className);

        isDoingColumns = true;
        this.parentElement = manyToMany;
        isColumnMandatory = referencedField.isMandatory();
        compositeKeyPrefix = referencedField.getName();
        {
            referencedField.getReferencedField().accept(this);
        }
        isDoingColumns = false;

        return manyToMany;
    }

    @Override
    public Element visit(SimpleTypeFieldMetadata simpleField) {
        return handleSimpleField(simpleField);
    }

    private Element handleSimpleField(FieldMetadata field) {
        String fieldName = resolver.get(field);

        if (isDoingColumns) {
            Element column = document.createElement("column"); //$NON-NLS-1$
            Attr columnName = document.createAttribute("name"); //$NON-NLS-1$
            columnName.setValue(shortString(compositeKeyPrefix + "_" + fieldName)); //$NON-NLS-1$
            column.getAttributes().setNamedItem(columnName);

            Attr notNull = document.createAttribute("not-null"); //$NON-NLS-1$
            notNull.setValue(String.valueOf(isColumnMandatory));
            column.getAttributes().setNamedItem(notNull);

            parentElement.appendChild(column);

            return column;
        }

        if (field.isKey()) {
            Element idElement;
            if (!compositeId) {
                idElement = document.createElement("id"); //$NON-NLS-1$
                if ("UUID".equals(field.getType().getName()) && ScatteredMappingCreator.GENERATED_ID.equals(fieldName)) {  //$NON-NLS-1$
                    // <generator class="uuid.hex"/>
                    Element generator = document.createElement("generator"); //$NON-NLS-1$
                    Attr generatorClass = document.createAttribute("class"); //$NON-NLS-1$
                    generatorClass.setValue("uuid.hex"); //$NON-NLS-1$
                    generator.getAttributes().setNamedItem(generatorClass);
                    idElement.appendChild(generator);
                }
            } else {
                idElement = document.createElement("key-property"); //$NON-NLS-1$
            }
            Attr idName = document.createAttribute("name"); //$NON-NLS-1$
            idName.setValue(fieldName);
            Attr columnName = document.createAttribute("column"); //$NON-NLS-1$
            columnName.setValue(shortString(fieldName));

            idElement.getAttributes().setNamedItem(idName);
            idElement.getAttributes().setNamedItem(columnName);
            return idElement;
        } else {
            if (!field.isMany()) {
                Element propertyElement = document.createElement("property"); //$NON-NLS-1$
                Attr propertyName = document.createAttribute("name"); //$NON-NLS-1$
                propertyName.setValue(fieldName);
                Attr columnName = document.createAttribute("column"); //$NON-NLS-1$
                columnName.setValue(shortString(fieldName));
                if (resolver.isIndexed(field)) { // Create indexes for fields that should be indexed.
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Creating index for field '" + field.getName() + "'.");
                    }
                    Attr indexName = document.createAttribute("index"); //$NON-NLS-1$
                    indexName.setValue(shortString(fieldName) + "_index"); //$NON-NLS-1$
                    propertyElement.getAttributes().setNamedItem(indexName);
                } else {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("*Not* creating index for field '" + field.getName() + "'.");
                    }
                }
                // Not null
                if (generateConstrains) {
                    if (field.isMandatory()) {
                        Attr notNull = document.createAttribute("not-null"); //$NON-NLS-1$
                        notNull.setValue("true"); //$NON-NLS-1$
                        propertyElement.getAttributes().setNamedItem(notNull);
                    } else {
                        Attr notNull = document.createAttribute("not-null"); //$NON-NLS-1$
                        notNull.setValue("false"); //$NON-NLS-1$
                        propertyElement.getAttributes().setNamedItem(notNull);
                    }
                } else {
                    Attr notNull = document.createAttribute("not-null"); //$NON-NLS-1$
                    notNull.setValue("false"); //$NON-NLS-1$
                    propertyElement.getAttributes().setNamedItem(notNull);
                }

                if ("base64Binary".equals(field.getType().getName())) { //$NON-NLS-1$
                    Attr elementType = document.createAttribute("type"); //$NON-NLS-1$
                    elementType.setValue("text"); //$NON-NLS-1$
                    propertyElement.getAttributes().setNamedItem(elementType);
                } else if(field instanceof SimpleTypeFieldMetadata) {
                    Object sqlType = ((SimpleTypeFieldMetadata) field).getData("SQL_TYPE"); //$NON-NLS-1$
                    if(sqlType != null) {
                        Attr elementType = document.createAttribute("type"); //$NON-NLS-1$
                        elementType.setValue(String.valueOf(sqlType));
                        propertyElement.getAttributes().setNamedItem(elementType);
                    }
                }

                propertyElement.getAttributes().setNamedItem(propertyName);
                propertyElement.getAttributes().setNamedItem(columnName);
                return propertyElement;
            } else {
                Element listElement = document.createElement("list"); //$NON-NLS-1$
                Attr name = document.createAttribute("name"); //$NON-NLS-1$
                name.setValue(fieldName);
                Attr tableName = document.createAttribute("table"); //$NON-NLS-1$
                tableName.setValue(shortString((field.getContainingType().getName() + '_' + fieldName).toUpperCase()));
                listElement.getAttributes().setNamedItem(tableName);

                // lazy="false"
                Attr lazyAttribute = document.createAttribute("lazy"); //$NON-NLS-1$
                lazyAttribute.setValue("false"); //$NON-NLS-1$
                listElement.getAttributes().setNamedItem(lazyAttribute);

                // fetch="join"
                Attr fetchAttribute = document.createAttribute("fetch"); //$NON-NLS-1$
                fetchAttribute.setValue("select"); //$NON-NLS-1$
                listElement.getAttributes().setNamedItem(fetchAttribute);

                // cascade="delete"
                Attr cascade = document.createAttribute("cascade"); //$NON-NLS-1$
                cascade.setValue("delete"); //$NON-NLS-1$
                listElement.getAttributes().setNamedItem(cascade);

                Element key = document.createElement("key"); //$NON-NLS-1$
                Attr column = document.createAttribute("column"); //$NON-NLS-1$
                column.setValue("id"); //$NON-NLS-1$
                key.getAttributes().setNamedItem(column);

                // <element column="name" type="string"/>
                Element element = document.createElement("element"); //$NON-NLS-1$
                Attr elementColumn = document.createAttribute("column"); //$NON-NLS-1$
                elementColumn.setValue("value"); //$NON-NLS-1$
                element.getAttributes().setNamedItem(elementColumn);
                Attr elementType = document.createAttribute("type"); //$NON-NLS-1$
                if ("base64Binary".equals(field.getType().getName())) {
                    elementType.setValue("text"); //$NON-NLS-1$
                } else {
                    elementType.setValue(getFieldType(field));
                }
                element.getAttributes().setNamedItem(elementType);

                // Not null
                if (field.isMandatory()) {
                    LOGGER.warn("Field '" + field.getName() + "' is mandatory and a collection. Constraint can not be expressed in database schema.");
                }

                // <index column="idx" />
                Element index = document.createElement("index"); //$NON-NLS-1$
                Attr indexColumn = document.createAttribute("column"); //$NON-NLS-1$
                indexColumn.setValue("pos"); //$NON-NLS-1$
                index.getAttributes().setNamedItem(indexColumn);

                listElement.getAttributes().setNamedItem(name);
                listElement.appendChild(key);
                listElement.appendChild(index);
                listElement.appendChild(element);
                return listElement;
            }
        }
    }

    String getFieldType(FieldMetadata field) {
        return MetadataUtils.getJavaType(field.getType());
    }
}