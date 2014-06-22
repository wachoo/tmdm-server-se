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

import java.util.Collections;
import java.util.List;

/**
 *
 */
public class If implements Function {
    private final Expression expression;
    private final Expression onSuccess;
    private final Expression onFail;

    public If(Expression expression, Expression onSuccess, Expression onFail) {
        this.expression = expression;
        this.onSuccess = onSuccess;
        this.onFail = onFail;
    }

    public String getName() {
        return "if"; //NON-NLS
    }

    public List<Expression> getParameters() {
        return Collections.singletonList(expression);
    }

    public Expression getOnSuccess() {
        return onSuccess;
    }

    public Expression getOnFail() {
        return onFail;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
