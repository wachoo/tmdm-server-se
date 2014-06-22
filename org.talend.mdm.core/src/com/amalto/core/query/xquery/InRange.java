/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query.xquery;

import java.util.Arrays;
import java.util.List;

/**
 *
 */
public class InRange implements Function {

    private final Expression expression;

    private final Expression start;

    private final Expression end;

    public InRange(Expression expression, Expression start, Expression end) {
        this.expression = expression;
        this.start = start;
        this.end = end;
    }

    public String getName() {
        return "x:in-range"; //NON-NLS
    }

    public List<Expression> getParameters() {
        return Arrays.asList(expression, start, end);
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
