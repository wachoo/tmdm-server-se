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

package com.amalto.core.query;

import com.amalto.core.metadata.*;
import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;

import java.util.LinkedList;
import java.util.List;

import static com.amalto.core.query.user.UserQueryBuilder.from;

class SimpleQueryGenerator extends DefaultMetadataVisitor<List<Expression>> {

    private final List<Expression> expressions = new LinkedList<Expression>();

    private UserQueryBuilder currentBuilder;

    @Override
    public List<Expression> visit(SimpleTypeFieldMetadata simpleField) {
        currentBuilder.select(simpleField);
        return expressions;
    }

    @Override
    public List<Expression> visit(ContainedTypeFieldMetadata containedField) {
        if (!containedField.isMany()) {
            containedField.getContainedType().accept(this);
        }
        return expressions;
    }

    @Override
    public List<Expression> visit(ReferenceFieldMetadata referenceField) {
        if (!referenceField.isMany()) {
            currentBuilder.select(referenceField);
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
