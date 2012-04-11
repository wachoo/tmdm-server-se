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

package com.amalto.core.save.context;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.history.accessor.Accessor;
import com.amalto.core.history.action.FieldUpdateAction;
import com.amalto.core.metadata.*;
import org.apache.commons.lang.StringUtils;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import java.util.*;

class UpdateActionCreator extends DefaultMetadataVisitor<List<Action>> {

    private final Stack<String> path = new Stack<String>();

    private final List<Action> actions = new LinkedList<Action>();

    private final Date date;

    private final String source;

    private final String userName;

    private final MutableDocument originalDocument;

    private final MutableDocument newDocument;

    private final MetadataRepository repository;

    private final Closure compareClosure;

    public UpdateActionCreator(MutableDocument originalDocument, MutableDocument newDocument, String source, String userName, MetadataRepository repository) {
        this.originalDocument = originalDocument;
        this.newDocument = newDocument;
        this.repository = repository;
        date = new Date(System.currentTimeMillis());
        this.source = source;
        this.userName = userName;
        compareClosure = new Closure() {
            public void execute(FieldMetadata field) {
                compare(field);
            }
        };
    }

    @Override
    public List<Action> visit(ComplexTypeMetadata complexType) {
        super.visit(complexType);
        return actions;
    }

    @Override
    public List<Action> visit(final ContainedTypeFieldMetadata containedField) {
        handleField(containedField, new Closure() {
            public void execute(FieldMetadata field) {
                TypeMetadata type = containedField.getContainedType();

                // Check if type of element isn't a subclass of declared type (use of xsi:type).
                String currentPath = getPath();
                Accessor leftAccessor = originalDocument.createAccessor(currentPath);
                Accessor rightAccessor = newDocument.createAccessor(currentPath);
                String typeToCheck = StringUtils.EMPTY;
                String newType = StringUtils.EMPTY;
                if (rightAccessor.exist()) {
                    typeToCheck = rightAccessor.getActualType();
                    newType = typeToCheck;
                }
                if (leftAccessor.exist()) {
                    if (!typeToCheck.equals(leftAccessor.getActualType())) {
                        typeToCheck = leftAccessor.getActualType();
                    }
                }

                if (!typeToCheck.isEmpty()) {
                    ComplexTypeMetadata subClassType = (ComplexTypeMetadata) repository.getNonInstantiableType(typeToCheck);
                    // Perform some checks about the xsi:type value (valid or not?).
                    if (subClassType == null) {
                        throw new IllegalArgumentException("Type '" + typeToCheck + "' was not found.");
                    }
                    if (!field.getType().isAssignableFrom(subClassType)) {
                        throw new IllegalArgumentException("Type '" + subClassType.getName() + "' is not assignable from type '" + subClassType.getName() + "'");
                    }
                    type = subClassType;
                }

                if (!typeToCheck.isEmpty()) {
                    actions.add(new OverrideSubclassAction(date, source, userName, currentPath, newType));
                }
                type.accept(UpdateActionCreator.this);
            }
        });
        return actions;
    }

    @Override
    public List<Action> visit(ReferenceFieldMetadata referenceField) {
        handleField(referenceField, compareClosure);
        return actions;
    }

    @Override
    public List<Action> visit(SimpleTypeFieldMetadata simpleField) {
        handleField(simpleField, compareClosure);
        return actions;
    }

    @Override
    public List<Action> visit(EnumerationFieldMetadata enumField) {
        handleField(enumField, compareClosure);
        return actions;
    }

    /**
     * Interface to encapsulate action to execute on fields
     */
    interface Closure {
        void execute(FieldMetadata field);
    }

    private String getPath() {
        if (path.isEmpty()) {
            throw new IllegalStateException();
        } else {
            StringBuilder builder = new StringBuilder();
            Iterator<String> pathIterator = path.iterator();
            while (pathIterator.hasNext()) {
                builder.append(pathIterator.next());
                if (pathIterator.hasNext()) {
                    builder.append('/');
                }
            }
            return builder.toString();
        }
    }

    private void handleField(FieldMetadata field, Closure closure) {
        path.add(field.getName());
        if (field.isMany()) {
            String currentPath = getPath();
            Accessor leftAccessor;
            Accessor rightAccessor;
            try {
                leftAccessor = originalDocument.createAccessor(currentPath);
                rightAccessor = newDocument.createAccessor(currentPath);
                if (!rightAccessor.exist()) {
                    // TODO If new list does not exist, it means element was omitted in new version (legacy behavior).
                    return;
                }
            } finally {
                path.pop();
            }

            // Proceed in "reverse" order (highest index to lowest) so there won't be issues when deleting elements in
            // a sequence (if element #2 is deleted before element #3, element #3 becomes #2...).
            for (int i = Math.max(leftAccessor.size(), rightAccessor.size()); i > 0; i--) {
                // XPath indexes are 1-based (not 0-based).
                path.add(field.getName() + "[" + i + "]"); //$NON-NLS-1$ //$NON-NLS-2$
                closure.execute(field);
                path.pop();
            }
        } else {
            closure.execute(field);
            path.pop();
        }
    }

    private void compare(FieldMetadata comparedField) {
        String path = getPath();
        Accessor originalAccessor = originalDocument.createAccessor(path);
        Accessor newAccessor = newDocument.createAccessor(path);

        if (!originalAccessor.exist()) {
            if (!newAccessor.exist()) {
                // No op
            } else { // new accessor exist
                if (!newAccessor.get().isEmpty()) { // TODO Empty accessor means no op to ensure legacy behavior
                    actions.add(new FieldUpdateAction(date, source, userName, path, StringUtils.EMPTY, newAccessor.get(), comparedField));
                }
            }
        } else { // original accessor exist
            if (!newAccessor.exist()) {
                if (comparedField.isMany()) {
                    actions.add(new FieldUpdateAction(date, source, userName, path, originalAccessor.get(), null, comparedField));
                }
            } else { // new accessor exist
                if (!originalAccessor.get().equals(newAccessor.get())) {
                    actions.add(new FieldUpdateAction(date, source, userName, path, originalAccessor.get(), newAccessor.get(), comparedField));
                }
            }
        }
    }

    private static class OverrideSubclassAction implements Action {

        private final Date date;

        private final String source;

        private final String userName;

        private final String path;

        private final String typeName;

        public OverrideSubclassAction(Date date, String source, String userName, String path, String typeName) {
            this.date = date;
            this.source = source;
            this.userName = userName;
            this.path = path;
            this.typeName = typeName;
        }

        public MutableDocument perform(MutableDocument document) {
            Document domDocument = document.asDOM();
            String xsi = domDocument.lookupNamespaceURI("xsi"); //$NON-NLS-1$
            if (xsi == null) {
                domDocument.getDocumentElement().setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);  //$NON-NLS-1$
            }

            Accessor typeAccessor = document.createAccessor(path + "/@xsi:type"); //$NON-NLS-1$
            if (typeAccessor.exist() && !typeName.equals(typeAccessor.get())) {
                Accessor accessor = document.createAccessor(path);
                accessor.deleteContent();
            }
            typeAccessor.createAndSet(typeName);
            return document;
        }

        public MutableDocument undo(MutableDocument document) {
            Accessor accessor = document.createAccessor(path + "/@xsi:type"); //$NON-NLS-1$
            accessor.delete();
            return document;
        }

        public MutableDocument addModificationMark(MutableDocument document) {
            throw new UnsupportedOperationException();
        }

        public MutableDocument removeModificationMark(MutableDocument document) {
            throw new UnsupportedOperationException();
        }

        public Date getDate() {
            return date;
        }

        public String getSource() {
            return source;
        }

        public String getUserName() {
            return userName;
        }

        public boolean isAllowed(Set<String> roles) {
            return true;
        }

        public String getDetails() {
            return "Change type to " + typeName;
        }
    }
}
