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

import java.util.List;

/**
 *
 */
public class Contains implements Function {
    private final List<Expression> expressions;

    public Contains(List<Expression> expressions) {
        this.expressions = expressions;
    }

    public String getName() {
        return "contains"; //NON-NLS
    }

    public List<Expression> getParameters() {
        return expressions;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
