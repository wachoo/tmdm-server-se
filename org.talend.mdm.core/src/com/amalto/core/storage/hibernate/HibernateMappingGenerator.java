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
import com.amalto.core.storage.hibernate.enhancement.HibernateClassCreator;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

// TODO Refactor
public class HibernateMappingGenerator extends DefaultMetadataVisitor<Element> {

    private static final Logger LOGGER = Logger.getLogger(HibernateMappingGenerator.class);

    private final Document document;

    private final TableResolver resolver;

    private boolean compositeId;

    private Element parentElement;

    private boolean isDoingColumns;

    private boolean isColumnMandatory;

    private String compositeKeyPrefix;

    private boolean generateConstrains;

    public HibernateMappingGenerator(Document document, TableResolver resolver) {
        this(document, resolver, true);
    }

    public HibernateMappingGenerator(Document document, TableResolver resolver, boolean generateConstrains) {
        this.document = document;
        this.resolver = resolver;
        this.generateConstrains = generateConstrains;
    }

    private static String shortString(String s, int threshold) {
        if (s.length() < threshold) {
            return s;
        }
        char[] chars = s.toCharArray();
        if (!s.contains("_")) { //$NON-NLS-1$
            chars = ArrayUtils.add(chars, threshold / 2, '_');
        }

        StringBuilder prefix = new StringBuilder();
        BitSet bitSet = null;
        int index = 0;
        int reservedSpace = 1;
        boolean isPrefix = true;
        for (char currentChar : chars) {
            switch (currentChar) {
                case '_':
                    if (isPrefix) {
                        int rest = (int) Math.pow(10, threshold - prefix.length() - 1);
                        reservedSpace = 0;
                        while (rest > 2) {
                            rest = rest >> 1;
                            reservedSpace++;
                        }
                        bitSet = new BitSet(reservedSpace);
                        isPrefix = false;
                        if (prefix.length() > threshold - 1) {
                            throw new IllegalArgumentException("Prefix is already too long to shorten the name.");
                        }
                    }
                    break;
                default:
                    if (isPrefix) {
                        prefix.append(currentChar);
                    } else {
                        bitSet.flip((index++ * currentChar) % reservedSpace);
                    }
                    break;
            }
        }

        if (bitSet == null) {
            throw new IllegalStateException("Expected bit set to be initialized.");
        }

        String shortenString = prefix.toString() + '_' + String.valueOf(bitSet.hashCode());
        if (shortenString.length() > threshold) {
            throw new IllegalStateException("Expected string to be shorter than " + threshold + ".");
        }
        return shortenString;
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

        String table = resolver.get(complexType);
        String generatedClassName = HibernateClassCreator.PACKAGE_PREFIX + complexType.getName();

        Element classElement = document.createElement("class");
        Attr className = document.createAttribute("name");
        className.setValue(generatedClassName);
        classElement.getAttributes().setNamedItem(className);
        Attr classTable = document.createAttribute("table");
        classTable.setValue(shortString(table, 30));
        classElement.getAttributes().setNamedItem(classTable);

        // <cache usage="read-write" include="non-lazy"/>
        Element cacheElement = document.createElement("cache");
        Attr usageAttribute = document.createAttribute("usage");
        usageAttribute.setValue("read-write");
        cacheElement.getAttributes().setNamedItem(usageAttribute);
        Attr includeAttribute = document.createAttribute("include");
        includeAttribute.setValue("non-lazy");
        cacheElement.getAttributes().setNamedItem(includeAttribute);
        Attr regionAttribute = document.createAttribute("region");
        regionAttribute.setValue("region");
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
            idParentElement = document.createElement("composite-id");
            classElement.appendChild(idParentElement);

            Attr classAttribute = document.createAttribute("class");
            classAttribute.setValue(generatedClassName + "_ID");
            idParentElement.getAttributes().setNamedItem(classAttribute);

            Attr mappedAttribute = document.createAttribute("mapped");
            mappedAttribute.setValue("true");
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
                Element propertyElement = document.createElement("list");
                Attr name = document.createAttribute("name");
                name.setValue(fieldName);
                propertyElement.getAttributes().setNamedItem(name);

                Attr lazy = document.createAttribute("lazy");
                lazy.setValue("false");
                propertyElement.getAttributes().setNamedItem(lazy);

                Attr joinAttribute = document.createAttribute("fetch");
                joinAttribute.setValue("select"); // Keep it "select" (Hibernate tends to duplicate results when using "fetch")
                propertyElement.getAttributes().setNamedItem(joinAttribute);

                Attr tableName = document.createAttribute("table");
                tableName.setValue(shortString((referenceField.getContainingType().getName() + '_' + fieldName + '_' + referencedType.getName()).toUpperCase(), 30));
                propertyElement.getAttributes().setNamedItem(tableName);
                {
                    // <key column="foo_id"/>
                    Element key = document.createElement("key");
                    Attr elementColumn = document.createAttribute("column");
                    elementColumn.setValue(shortString(referenceField.getName(), 30));
                    key.getAttributes().setNamedItem(elementColumn);
                    propertyElement.appendChild(key);

                    // <index column="idx" />
                    Element index = document.createElement("index");
                    Attr indexColumn = document.createAttribute("column");
                    indexColumn.setValue("pos");
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
        Element propertyElement = document.createElement("many-to-one");
        Attr propertyName = document.createAttribute("name");
        propertyName.setValue(fieldName);
        Attr className = document.createAttribute("class");
        className.setValue(HibernateClassCreator.PACKAGE_PREFIX + referencedField.getReferencedType().getName());

        // fetch="join" lazy="false"
        Attr lazy = document.createAttribute("lazy");
        lazy.setValue("false");
        propertyElement.getAttributes().setNamedItem(lazy);

        Attr joinAttribute = document.createAttribute("fetch");
        joinAttribute.setValue("join");
        propertyElement.getAttributes().setNamedItem(joinAttribute);

        // Not null
        if (referencedField.isMandatory()) {
            Attr notNull = document.createAttribute("not-null");
            notNull.setValue("true");
            propertyElement.getAttributes().setNamedItem(notNull);
        } else {
            Attr notNull = document.createAttribute("not-null");
            notNull.setValue("false");
            propertyElement.getAttributes().setNamedItem(notNull);
        }

        // If data model authorizes fk integrity override, don't enforce database FK integrity.
        if (enforceDataBaseIntegrity) {
            // Ensure default settings for Hibernate are set (in case they change).
            Attr notFound = document.createAttribute("not-found");
            notFound.setValue("exception");
            propertyElement.getAttributes().setNamedItem(notFound);
        } else {
            // Disables all warning/errors from Hibernate.
            Attr integrity = document.createAttribute("unique");
            integrity.setValue("false");
            propertyElement.getAttributes().setNamedItem(integrity);

            Attr foreignKey = document.createAttribute("foreign-key");
            foreignKey.setValue("none");  // Disables foreign key generation for DDL.
            propertyElement.getAttributes().setNamedItem(foreignKey);

            Attr notFound = document.createAttribute("not-found");
            notFound.setValue("ignore");
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
        Element manyToMany = document.createElement("many-to-many");

        // If data model authorizes fk integrity override, don't enforce database FK integrity.
        if (enforceDataBaseIntegrity) {
            // Ensure default settings for Hibernate are set (in case they change).
            Attr notFound = document.createAttribute("not-found");
            notFound.setValue("exception");
            manyToMany.getAttributes().setNamedItem(notFound);
        } else {
            // Disables all warning/errors from Hibernate.
            Attr integrity = document.createAttribute("unique");
            integrity.setValue("false");
            manyToMany.getAttributes().setNamedItem(integrity);

            Attr foreignKey = document.createAttribute("foreign-key");
            foreignKey.setValue("none");  // Disables foreign key generation for DDL.
            manyToMany.getAttributes().setNamedItem(foreignKey);

            Attr notFound = document.createAttribute("not-found");
            notFound.setValue("ignore");
            manyToMany.getAttributes().setNamedItem(notFound);
        }

        Attr className = document.createAttribute("class");
        className.setValue(HibernateClassCreator.PACKAGE_PREFIX + referencedField.getReferencedType().getName());
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
            Element column = document.createElement("column");
            Attr columnName = document.createAttribute("name");
            columnName.setValue(shortString(compositeKeyPrefix + "_" + fieldName, 30));
            column.getAttributes().setNamedItem(columnName);

            Attr notNull = document.createAttribute("not-null");
            notNull.setValue(String.valueOf(isColumnMandatory));
            column.getAttributes().setNamedItem(notNull);

            parentElement.appendChild(column);

            return column;
        }

        if (field.isKey()) {
            Element idElement;
            if (!compositeId) {
                idElement = document.createElement("id");
            } else {
                idElement = document.createElement("key-property");
            }
            Attr idName = document.createAttribute("name");
            idName.setValue(fieldName);
            Attr columnName = document.createAttribute("column");
            columnName.setValue(shortString(fieldName, 30));

            idElement.getAttributes().setNamedItem(idName);
            idElement.getAttributes().setNamedItem(columnName);
            return idElement;
        } else {
            if (!field.isMany()) {
                Element propertyElement = document.createElement("property");
                Attr propertyName = document.createAttribute("name");
                propertyName.setValue(fieldName);
                Attr columnName = document.createAttribute("column");
                columnName.setValue(shortString(fieldName, 30));
                Attr elementType = document.createAttribute("type");
                elementType.setValue(getFieldType(field));

                // Not null
                if (generateConstrains) {
                    if (field.isMandatory()) {
                        Attr notNull = document.createAttribute("not-null");
                        notNull.setValue("true");
                        propertyElement.getAttributes().setNamedItem(notNull);
                    } else {
                        Attr notNull = document.createAttribute("not-null");
                        notNull.setValue("false");
                        propertyElement.getAttributes().setNamedItem(notNull);
                    }
                } else {
                    Attr notNull = document.createAttribute("not-null");
                    notNull.setValue("false");
                    propertyElement.getAttributes().setNamedItem(notNull);
                }

                propertyElement.getAttributes().setNamedItem(propertyName);
                propertyElement.getAttributes().setNamedItem(columnName);
                return propertyElement;
            } else {
                Element listElement = document.createElement("list");
                Attr name = document.createAttribute("name");
                name.setValue(fieldName);
                Attr tableName = document.createAttribute("table");
                tableName.setValue(shortString((field.getContainingType().getName() + '_' + fieldName).toUpperCase(), 30));

                // lazy="false"
                Attr lazyAttribute = document.createAttribute("lazy");
                lazyAttribute.setValue("false");
                listElement.getAttributes().setNamedItem(lazyAttribute);

                // inverse="true"
                Attr inverseAttribute = document.createAttribute("inverse");
                inverseAttribute.setValue("true");
                listElement.getAttributes().setNamedItem(inverseAttribute);

                // inverse="true"
                Attr fetchAttribute = document.createAttribute("fetch");
                fetchAttribute.setValue("join");
                listElement.getAttributes().setNamedItem(fetchAttribute);

                Element key = document.createElement("key");
                Attr column = document.createAttribute("column");
                column.setValue("id");
                key.getAttributes().setNamedItem(column);

                // <element column="name" type="string"/>
                Element element = document.createElement("element");
                Attr elementColumn = document.createAttribute("column");
                elementColumn.setValue("value");
                element.getAttributes().setNamedItem(elementColumn);
                Attr elementType = document.createAttribute("type");
                elementType.setValue(getFieldType(field));
                element.getAttributes().setNamedItem(elementType);

                // Not null
                if (field.isMandatory()) {
                    LOGGER.warn("Field '" + field.getName() + "' is mandatory and a collection. Constraint can not be expressed in database schema.");
                }

                // <index column="idx" />
                Element index = document.createElement("index");
                Attr indexColumn = document.createAttribute("column");
                indexColumn.setValue("pos");
                index.getAttributes().setNamedItem(indexColumn);

                listElement.getAttributes().setNamedItem(name);
                listElement.appendChild(key);
                listElement.appendChild(index);
                listElement.appendChild(element);
                return listElement;
            }
        }
    }

    protected String getFieldType(FieldMetadata field) {
        return MetadataUtils.getJavaType(field.getType());
    }
}