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
import java.util.Stack;

import static com.amalto.core.query.user.UserQueryBuilder.from;

class SimpleQueryGenerator extends DefaultMetadataVisitor<List<Expression>> {

    private final List<Expression> expressions = new LinkedList<Expression>();

    private final Stack<String> fields = new Stack<String>();

    private UserQueryBuilder currentBuilder;

    private ComplexTypeMetadata type;

    @Override
    public List<Expression> visit(SimpleTypeFieldMetadata simpleField) {
        if (!simpleField.isMany()) {
            String fields = getFields();
            currentBuilder.select(type.getField(fields + simpleField.getName()));
        }
        return expressions;
    }

    private String getFields() {
        StringBuilder buffer = new StringBuilder();
        for (String field : fields) {
            buffer.append(field);
            buffer.append('/');
        }
        return buffer.toString();
    }

    @Override
    public List<Expression> visit(ContainedTypeFieldMetadata containedField) {
        fields.push(containedField.getName());
        containedField.getContainedType().accept(this);
        fields.pop();
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
        type = complexType;
        currentBuilder = from(type);
        for (FieldMetadata currentField : type.getFields()) {
            currentField.accept(this);
            expressions.add(currentBuilder.getSelect());
            currentBuilder = from(type);
        }
        return expressions;
    }
}
