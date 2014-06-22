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
public class Not implements Function {
    private final Expression parameter;

    public Not(Expression parameter) {
        this.parameter = parameter;
    }

    public String getName() {
        return "not";
    }

    public List<Expression> getParameters() {
        return Collections.singletonList(parameter);
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
