/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.save.context;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.UUID;

import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;
import org.w3c.dom.Node;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.accessor.DOMAccessor;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.ContainedTypeFieldMetadata;
import com.amalto.core.metadata.DefaultMetadataVisitor;
import com.amalto.core.metadata.EnumerationFieldMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.SimpleTypeFieldMetadata;

/**
 * Generate actions on creation (like setting UUID and AUTO_INCREMENT fields that <b>are not</b> part of the saved
 * entity type).
 * 
 * @see ID for code that sets ID values.
 */
class CreateActions extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<FieldItem> realPath = new Stack<FieldItem>();

    private final List<Action> actions = new LinkedList<Action>();

    private final Date date;

    private final String source;

    private final String userName;

    private final String universe;

    private final SaverSource saverSource;

    private final String dataCluster;

    private final Map<String, String> idValueMap = new HashMap<String, String>();

    private final MutableDocument document;

    private boolean hasMetAutoIncrement;

    private String rootTypeName = null;

    private Map<String, String> autoIncrementFieldMap;

    class FieldItem {

        FieldMetadata fieldMetadata;

        Integer index;

        FieldItem(FieldMetadata fieldMetadata, Integer index) {
            this.fieldMetadata = fieldMetadata;
            this.index = index;
        }
    }

    CreateActions(MutableDocument document, Date date, String source, String userName, String dataCluster, String universe,
            SaverSource saverSource, Map<String, String> autoIncrementFieldMap) {
        this.document = document;
        this.date = date;
        this.source = source;
        this.userName = userName;
        this.dataCluster = dataCluster;
        this.universe = universe;
        this.saverSource = saverSource;
        this.autoIncrementFieldMap = autoIncrementFieldMap;
    }

    private String getRealPath() {
        if (realPath.isEmpty()) {
            throw new IllegalStateException();
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<FieldItem> pathIterator = realPath.iterator();
            while (pathIterator.hasNext()) {
                FieldItem item = pathIterator.next();
                builder.append(item.fieldMetadata.getName());
                if (item.index != null) {
                    builder.append('[' + String.valueOf(item.index) + ']');
                }
                if (pathIterator.hasNext()) {
                    builder.append('/');
                }
            }
            return builder.toString();
        }
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        if (rootTypeName == null) {
            rootTypeName = complexType.getName();
        }
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        realPath.push(new FieldItem(referenceField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        realPath.pop();
        if (referenceField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                realPath.push(new FieldItem(referenceField, i));
                super.visit(referenceField);
                realPath.pop();
            }
        } else {
            realPath.push(new FieldItem(referenceField, null));
            super.visit(referenceField);
            realPath.pop();
        }
        return actions;
    }

    @Override
    public List<Action> visit(ContainedTypeFieldMetadata containedField) {
        realPath.push(new FieldItem(containedField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        realPath.pop();
        if (containedField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                realPath.push(new FieldItem(containedField, i));
                super.visit(containedField);
                realPath.pop();
            }
        } else {
            realPath.push(new FieldItem(containedField, null));
            super.visit(containedField);
            realPath.pop();
        }
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        realPath.push(new FieldItem(enumField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        realPath.pop();
        if (enumField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                realPath.push(new FieldItem(enumField, i));
                super.visit(enumField);
                realPath.pop();
            }
        } else {
            realPath.push(new FieldItem(enumField, null));
            super.visit(enumField);
            realPath.pop();
        }
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        if (simpleField.isKey() && simpleField.isMany()) {
            throw new IllegalArgumentException("Key field cannot be a repeatable element"); //$NON-NLS-1$
        }
        realPath.push(new FieldItem(simpleField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        realPath.pop();
        boolean doCreate = isDoCreate(simpleField);
        if (simpleField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                realPath.push(new FieldItem(simpleField, i));
                handleSimpleTypeFieldMetadata(simpleField, doCreate);
                realPath.pop();
            }
        } else {
            realPath.push(new FieldItem(simpleField, null));
            handleSimpleTypeFieldMetadata(simpleField, doCreate);
            realPath.pop();
        }
        return super.visit(simpleField);
    }

    private boolean isDoCreate(SimpleTypeFieldMetadata simpleField) {
        // Under some circumstances, do not generate action(s) for UUID/AUTOINCREMENT(see TMDM-4473)
        boolean doCreate = true;
        if (!realPath.isEmpty()) {
            // Only apply the do-create-check for UUID/AUTO_INCREMENT field to improve the performance
            if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(simpleField.getType().getName())
                    || EUUIDCustomType.UUID.getName().equalsIgnoreCase(simpleField.getType().getName())) {
                FieldMetadata parentField = realPath.peek().fieldMetadata;
                if (parentField != null) {

                    boolean isParentOptional = !parentField.isMandatory();

                    boolean isEmpty = false;
                    Accessor accessor = document.createAccessor(getRealPath());
                    Node parentNode = null;
                    if (accessor instanceof DOMAccessor) {
                        parentNode = ((DOMAccessor) accessor).getNode();
                    }
                    if (parentNode == null) {
                        isEmpty = true;
                    } else if (parentNode.getTextContent() == null || parentNode.getTextContent().isEmpty()) {
                        isEmpty = true;
                    }

                    if (isParentOptional && isEmpty) {
                        doCreate = false;
                    }
                }
            }
        }
        return doCreate;
    }

    private void handleSimpleTypeFieldMetadata(SimpleTypeFieldMetadata simpleField, boolean doCreate) {

        // Handle UUID and AutoIncrement elements (this code also ensures any previous value is overwritten, see
        // TMDM-3900).
        // Note #2: This code generate values even for non-mandatory fields (but this is expected behavior).
        String currentPath = getRealPath();
        String currentPathWithoutLastIndex = currentPath.replaceAll("\\[\\d+\\]$", ""); //$NON-NLS-1$//$NON-NLS-2$
        if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(simpleField.getType().getName()) && doCreate) {
            String conceptName = rootTypeName + "." + simpleField.getName().replaceAll("/", "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            String autoIncrementValue = saverSource.nextAutoIncrementId(universe, dataCluster, conceptName);
            actions.add(new FieldUpdateAction(date, source, userName, currentPath, StringUtils.EMPTY, autoIncrementValue,
                    simpleField));
            if (simpleField.isKey()) {
                idValueMap.put(currentPathWithoutLastIndex, autoIncrementValue);
            }
            hasMetAutoIncrement = true; // Remembers we've just met an auto increment value
        } else if (EUUIDCustomType.UUID.getName().equalsIgnoreCase(simpleField.getType().getName()) && doCreate) {
            String uuidValue = UUID.randomUUID().toString();
            if (simpleField.isKey()) {
                idValueMap.put(currentPathWithoutLastIndex, uuidValue);
            }
            actions.add(new FieldUpdateAction(date, source, userName, currentPath, StringUtils.EMPTY, uuidValue, simpleField));
        } else {
            if (simpleField.isKey()) {
                Accessor accessor = document.createAccessor(currentPath);
                if (!accessor.exist()) {
                    throw new IllegalStateException("Document was expected to contain id information at '" + currentPath + "'");
                }
                idValueMap.put(currentPathWithoutLastIndex, accessor.get());
            }
        }
    }

    public boolean hasMetAutoIncrement() {
        return hasMetAutoIncrement;
    }

    public Map<String, String> getIdValueMap() {
        return idValueMap;
    }
}
