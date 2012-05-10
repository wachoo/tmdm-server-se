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
import com.amalto.core.metadata.*;
import org.w3c.dom.Document;

import javax.xml.XMLConstants;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

class ChangeTypeAction implements Action {

    private final Date date;

    private final String source;

    private final String userName;

    private final String path;

    private final ComplexTypeMetadata newType;

    private final Set<String> pathToClean;

    public ChangeTypeAction(Date date, String source, String userName, String path, ComplexTypeMetadata previousType, ComplexTypeMetadata newType) {
        this.date = date;
        this.source = source;
        this.userName = userName;
        this.path = path;
        this.newType = newType;

        pathToClean = new HashSet<String>();
        // Compute paths to fields that changed from previous type (only if type changed).
        if (previousType != null && previousType.getName().equals(newType.getName())) {
            newType.accept(new TypeComparison(previousType, pathToClean));
            previousType.accept(new TypeComparison(newType, pathToClean));
        }
    }

    public MutableDocument perform(MutableDocument document) {
        // Ensure xsi prefix is declared
        Document domDocument = document.asDOM();
        String xsi = domDocument.lookupNamespaceURI("xsi"); //$NON-NLS-1$
        if (xsi == null) {
            domDocument.getDocumentElement().setAttributeNS(XMLConstants.XMLNS_ATTRIBUTE_NS_URI, "xmlns:xsi", XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);  //$NON-NLS-1$
        }

        Accessor typeAccessor = document.createAccessor(path + "/@xsi:type"); //$NON-NLS-1$
        String typeName = newType.getNamespace();
        if (typeAccessor.exist() && !typeName.equals(typeAccessor.get())) {
            for (String currentPathToDelete : pathToClean) {
                Accessor accessor = document.createAccessor(path + '/' + currentPathToDelete);
                if (accessor.exist()) {
                    accessor.delete();
                }
            }
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
        return "Change type to " + newType.getName(); //$NON-NLS-1$
    }

    @Override
    public String toString() {
        return "ChangeTypeAction{" +  //$NON-NLS-1$
                "path='" + path + '\'' + //$NON-NLS-1$
                ", newType=" + newType + //$NON-NLS-1$
                '}';
    }

    private class TypeComparison extends DefaultMetadataVisitor<Set<String>> {

        private final Stack<String> pathPrefix = new Stack<String>();

        private final Set<String> paths;

        private ComplexTypeMetadata type;

        private TypeComparison(ComplexTypeMetadata type, Set<String> paths) {
            this.type = type;
            this.paths = paths;
        }

        private String getCurrentPath(FieldMetadata referenceField) {
            StringBuilder builder = new StringBuilder();
            for (String currentPathElement : pathPrefix) {
                builder.append(currentPathElement);
                builder.append('/');
            }
            return builder.append(referenceField.getName()).toString();
        }

        @Override
        public Set<String> visit(ComplexTypeMetadata complexType) {
            super.visit(complexType);
            return paths;
        }

        @Override
        public Set<String> visit(ReferenceFieldMetadata referenceField) {
            try {
                type.getField(getCurrentPath(referenceField));
            } catch (Exception e) {
                paths.add(getCurrentPath(referenceField));
            }
            return paths;
        }

        @Override
        public Set<String> visit(ContainedTypeFieldMetadata containedField) {
            pathPrefix.push(containedField.getName());
            {
                try {
                    type.getField(containedField.getName());
                    type = containedField.getContainedType();
                    super.visit(containedField);
                    type = containedField.getContainingType();
                } catch (Exception e) {
                    paths.add(containedField.getName());
                }
            }
            pathPrefix.pop();
            return paths;
        }

        @Override
        public Set<String> visit(FieldMetadata fieldMetadata) {
            try {
                type.getField(fieldMetadata.getName());
            } catch (Exception e) {
                paths.add(getCurrentPath(fieldMetadata));
            }
            return paths;
        }

        @Override
        public Set<String> visit(SimpleTypeFieldMetadata simpleField) {
            try {
                type.getField(simpleField.getName());
            } catch (Exception e) {
                paths.add(getCurrentPath(simpleField));
            }
            return paths;
        }

        @Override
        public Set<String> visit(EnumerationFieldMetadata enumField) {
            try {
                type.getField(enumField.getName());
            } catch (Exception e) {
                paths.add(getCurrentPath(enumField));
            }
            return paths;
        }
    }
}
