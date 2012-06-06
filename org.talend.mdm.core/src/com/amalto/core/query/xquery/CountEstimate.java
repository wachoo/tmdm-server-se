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

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public class CountEstimate implements Function {
    private final Expression expression;
    private final Expression estimatedCountSampleSize;

    public CountEstimate(Expression expression) {
        this.expression = expression;

        Object size = MDMConfiguration.getConfiguration().get("test");
        if (size != null && !size.toString().isEmpty()) {
            estimatedCountSampleSize = new Constant(size.toString());
        } else {
            estimatedCountSampleSize = null;
        }

    }

    public String getName() {
        return "x:count-estimate"; //NON-NLS
    }

    public List<Expression> getParameters() {
        if (estimatedCountSampleSize == null) {
            return Collections.singletonList(expression);
        } else {
            return Arrays.asList(expression, estimatedCountSampleSize);
        }
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }
}
