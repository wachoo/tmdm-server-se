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

import com.amalto.core.history.accessor.DOMAccessor;
import org.apache.commons.lang.StringUtils;
import org.talend.mdm.commmon.util.core.EUUIDCustomType;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.FieldUpdateAction;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ContainedTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.DefaultMetadataVisitor;
import org.talend.mdm.commmon.metadata.EnumerationFieldMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;
import org.w3c.dom.Node;

/**
 * Generate actions on creation (like setting UUID and AUTO_INCREMENT fields that <b>are not</b> part of the saved
 * entity type).
 * 
 * @see ID for code that sets ID values.
 */
class CreateActions extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<PathElement> path = new Stack<PathElement>();

    private final List<Action> actions = new LinkedList<Action>();

    private final Date date;

    private final String source;

    private final String userName;

    private final String universe;

    private final SaverSource saverSource;

    private final String dataCluster;
    
    private final String dataModel;

    private final Map<String, String> idValueMap = new HashMap<String, String>();

    private final MutableDocument document;

    private boolean hasMetAutoIncrement;

    private String rootTypeName = null;

    private Map<String, String> autoIncrementFieldMap;

    private static class PathElement {

        FieldMetadata fieldMetadata;

        Integer index;

        PathElement(FieldMetadata fieldMetadata, Integer index) {
            this.fieldMetadata = fieldMetadata;
            this.index = index;
        }
    }

    CreateActions(MutableDocument document,
                  Date date,
                  String source,
                  String userName,
                  String dataCluster,
                  String dataModel,
                  String universe,
                  SaverSource saverSource,
                  Map<String, String> autoIncrementFieldMap) {
        this.document = document;
        this.date = date;
        this.source = source;
        this.userName = userName;
        this.dataCluster = dataCluster;
        this.dataModel = dataModel;
        this.universe = universe;
        this.saverSource = saverSource;
        this.autoIncrementFieldMap = autoIncrementFieldMap;
    }

    private String getRealPath() {
        if (path.isEmpty()) {
            throw new IllegalStateException();
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<PathElement> pathIterator = path.iterator();
            while (pathIterator.hasNext()) {
                PathElement item = pathIterator.next();
                builder.append(item.fieldMetadata.getName());
                if (item.index != null) {
                    builder.append('[').append(String.valueOf(item.index)).append(']');
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
        path.push(new PathElement(referenceField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        path.pop();
        if (referenceField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                path.push(new PathElement(referenceField, i));
                super.visit(referenceField);
                path.pop();
            }
        } else {
            path.push(new PathElement(referenceField, null));
            super.visit(referenceField);
            path.pop();
        }
        return actions;
    }

    @Override
    public List<Action> visit(ContainedTypeFieldMetadata containedField) {
        path.push(new PathElement(containedField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        path.pop();
        if (containedField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                path.push(new PathElement(containedField, i));
                super.visit(containedField);
                path.pop();
            }
        } else {
            path.push(new PathElement(containedField, null));
            super.visit(containedField);
            path.pop();
        }
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        path.push(new PathElement(enumField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        path.pop();
        if (enumField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                path.push(new PathElement(enumField, i));
                super.visit(enumField);
                path.pop();
            }
        } else {
            path.push(new PathElement(enumField, null));
            super.visit(enumField);
            path.pop();
        }
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        path.push(new PathElement(simpleField, null));
        String xpath = getRealPath();
        int size = document.createAccessor(xpath).size();
        path.pop();
        boolean doCreate = isDoCreate(simpleField);
        if (simpleField.isMany() && size > 1) {
            for (int i = 1; i <= size; i++) {
                path.push(new PathElement(simpleField, i));
                handleSimpleTypeFieldMetadata(simpleField, doCreate);
                path.pop();
            }
        } else {
            path.push(new PathElement(simpleField, null));
            handleSimpleTypeFieldMetadata(simpleField, doCreate);
            path.pop();
        }
        return super.visit(simpleField);
    }

    private boolean isDoCreate(SimpleTypeFieldMetadata simpleField) {
        // Under some circumstances, do not generate action(s) for UUID/AUTOINCREMENT(see TMDM-4473)
        boolean doCreate = true;
        if (!path.isEmpty()) {
            // Only apply the do-create-check for UUID/AUTO_INCREMENT field to improve the performance
            if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(simpleField.getType().getName())
                    || EUUIDCustomType.UUID.getName().equalsIgnoreCase(simpleField.getType().getName())) {
                FieldMetadata parentField = path.peek().fieldMetadata;
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
        String currentPathWithoutLastIndex = currentPath;
        if (currentPath.indexOf('[') > 0) {
            currentPathWithoutLastIndex = currentPath.substring(0, currentPath.indexOf('['));
        }
        if (EUUIDCustomType.AUTO_INCREMENT.getName().equalsIgnoreCase(simpleField.getType().getName()) && doCreate) {
            String conceptName = rootTypeName + "." + simpleField.getName().replaceAll("/", "."); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            String autoIncrementValue;
            if (autoIncrementFieldMap.containsKey(currentPath)) {
                autoIncrementValue = autoIncrementFieldMap.get(currentPath);
            } else {
                autoIncrementValue = saverSource.nextAutoIncrementId(universe, dataCluster, dataModel, conceptName);
                autoIncrementFieldMap.put(currentPath, autoIncrementValue);
            }
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
