/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query;

import org.talend.mdm.commmon.metadata.*;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;

import java.util.LinkedList;
import java.util.List;

import static com.amalto.core.query.user.UserQueryBuilder.from;

class JoinQueryGenerator extends DefaultMetadataVisitor<List<Expression>> {

    private final List<Expression> expressions = new LinkedList<Expression>();

    private UserQueryBuilder currentBuilder;

    @Override
    public List<Expression> visit(SimpleTypeFieldMetadata simpleField) {
        if (!simpleField.isMany()) {
            currentBuilder.select(simpleField);
        }
        return expressions;
    }

    @Override
    public List<Expression> visit(ContainedTypeFieldMetadata containedField) {
        return expressions;
    }

    @Override
    public List<Expression> visit(ReferenceFieldMetadata referenceField) {
        if (!currentBuilder.getSelect().getTypes().get(0).equals(referenceField.getReferencedType())) {
            currentBuilder.and(referenceField.getReferencedType());
            currentBuilder.join(referenceField);
            for (FieldMetadata current : referenceField.getReferencedType().getFields()) {
                if (!current.isKey()) {
                    currentBuilder.select(current); // Select first non-key field
                    break;
                }
            }
        }
        return expressions;
    }

    @Override
    public List<Expression> visit(ComplexTypeMetadata complexType) {
        currentBuilder = from(complexType);
        for (FieldMetadata currentField : complexType.getFields()) {
            currentField.accept(this);
            expressions.add(currentBuilder.getSelect());
            currentBuilder = from(complexType);
        }
        return expressions;
    }
}
