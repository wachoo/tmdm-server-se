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
public class XQueryCollection implements Expression {

    private final String name;

    private final List<Condition> conditions;

    public XQueryCollection(String name) {
        this(name, Collections.<Condition>emptyList());
    }

    private XQueryCollection(String name, List<Condition> conditions) {
        this.name = name;
        this.conditions = conditions;
    }

    public void accept(ExpressionVisitor visitor, ExpressionVisitorContext context) {
        visitor.visit(this, context);
    }

    public List<Condition> getConditions() {
        return conditions;
    }

    public String getName() {
        return name;
    }
}
