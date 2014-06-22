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

import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

/**
 *
 */
public class ExpressionVisitorContext {

    private final Stack<Integer> contexts = new Stack<Integer>();

    private final Stack<Map<Variable, String>> variableNameCache = new Stack<Map<Variable, String>>();

    public ExpressionVisitorContext() {
        contexts.push(0);
    }

    public void enter() {
        contexts.push(contexts.peek());
        variableNameCache.push(new HashMap<Variable, String>());
    }

    public void leave() {
        contexts.pop();
        variableNameCache.pop();
    }

    public String getVariable(Variable variable) {
        for (Map<Variable, String> currentVariableNameCache : variableNameCache) {
            if (currentVariableNameCache.containsKey(variable)) {
                return currentVariableNameCache.get(variable);
            }
        }

        variableNameCache.peek().put(variable, getVariableName());
        return variableNameCache.peek().get(variable);
    }

    private String getVariableName() {
        int currentCount = contexts.peek();
        contexts.push(currentCount + 1);

        return "var" + currentCount; //NON-NLS NON-NLS
    }
}
