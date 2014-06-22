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
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.user.UserQueryBuilder;

import java.util.LinkedList;
import java.util.List;

import static com.amalto.core.query.user.UserQueryBuilder.from;

class OrderByQueryGenerator extends DefaultMetadataVisitor<List<Expression>> {

    private final List<Expression> expressions = new LinkedList<Expression>();

    private UserQueryBuilder currentBuilder;

    @Override
    public List<Expression> visit(SimpleTypeFieldMetadata simpleField) {
        if (!simpleField.isKey() && !simpleField.isMany()) {
            currentBuilder.select(simpleField);
            currentBuilder.orderBy(simpleField, OrderBy.Direction.ASC);
            expressions.add(currentBuilder.getSelect());

            currentBuilder.orderBy(simpleField, OrderBy.Direction.DESC);
            expressions.add(currentBuilder.getSelect());
        }

        return expressions;
    }

    @Override
    public List<Expression> visit(ContainedTypeFieldMetadata containedField) {
        containedField.getContainedType().accept(this);
        return expressions;
    }

    @Override
    public List<Expression> visit(ReferenceFieldMetadata referenceField) {
        return expressions;
    }

    @Override
    public List<Expression> visit(ComplexTypeMetadata complexType) {
        currentBuilder = from(complexType);
        for (FieldMetadata currentField : complexType.getFields()) {
            currentField.accept(this);
            currentBuilder = from(complexType);
        }
        return expressions;
    }
}
