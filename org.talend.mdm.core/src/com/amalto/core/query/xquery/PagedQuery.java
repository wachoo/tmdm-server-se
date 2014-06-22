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
public class PagedQuery implements Function {

    private final Expression start;

    private final Expression end;

    private final Expression forStatement;

    private PagedQuery(Number start, Number end, Expression forStatement) {
        this.start = start;
        this.end = end;
        this.forStatement = forStatement;
    }

    public PagedQuery(int start, int limit, Expression forStatement) {
        this(new Number(start), new Number(limit), forStatement);
    }

    public String getName() {
        return "x:paged-query"; //NON-NLS
    }

    public List<Expression> getParameters() {
        return Arrays.asList(start, end, forStatement);
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
