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
public class XQueryDumpConsole implements ExpressionVisitor {
    private int indent;

    private void increaseIndent() {
        indent++;
    }

    private void decreaseIndent() {
        indent--;
    }

    private void print(String message) {
        for (int i = 0; i < indent; i++) {
            System.out.print('\t');
        }
        System.out.println(message);
    }

    public void visit(Expression expression, ExpressionVisitorContext context) {
        print("Unvisited expression type: " + expression.toString());
    }

    public void visit(Function function, ExpressionVisitorContext context) {
        print("[FUNCTION]");
        increaseIndent();
        print("Name -> " + function.getName());
        {
            List<Expression> expressions = function.getParameters();
            for (Expression currentExpression : expressions) {
                currentExpression.accept(this, context);
            }
        }
        decreaseIndent();
    }

    public void visit(XQueryCollection collection, ExpressionVisitorContext context) {
        print("[COLLECTION]");
        increaseIndent();
        {
            print("Collection name -> " + collection.getName());
        }
        decreaseIndent();
    }

    public void visit(Condition condition, ExpressionVisitorContext context) {
        print("[CONDITION]");
        increaseIndent();
        {
            condition.getRoot().accept(this, context);
            condition.getCondition().accept(this, context);
        }
        decreaseIndent();
    }

    public void visit(Constant constant, ExpressionVisitorContext context) {
        print("[CONSTANT]");
        increaseIndent();
        print("Value -> '" + constant.getValue() + "'");
        decreaseIndent();
    }

    public void visit(Text text, ExpressionVisitorContext context) {
        print("[TEXT]");
        increaseIndent();
        print("Value -> '" + text.getValue() + "'");
        decreaseIndent();
    }

    public void visit(Number number, ExpressionVisitorContext context) {
        print("[NUMBER]");
        increaseIndent();
        print("Value -> " + number.getValue());
        decreaseIndent();
    }

    public void visit(For forExpression, ExpressionVisitorContext context) {
        context.enter();
        print("[FOR]");
        increaseIndent();
        {
            print("Loop variable");
            increaseIndent();
            {
                forExpression.getVariable().accept(this, context);
            }
            decreaseIndent();
            print("Collection");
            increaseIndent();
            {
                forExpression.getCollection().accept(this, context);
            }
            decreaseIndent();
            print("Order by");
            increaseIndent();
            {
                forExpression.getOrderBy().accept(this, context);
            }
            decreaseIndent();
            print("Return");
            increaseIndent();
            {
                forExpression.getReturnExpression().accept(this, context);
            }
            decreaseIndent();
        }
        decreaseIndent();
        context.leave();
    }

    public void visit(Let letExpression, ExpressionVisitorContext context) {
        print("[LET]");
        increaseIndent();
        {
            print("Variable");
            increaseIndent();
            {
                letExpression.getVariable().accept(this, context);
            }
            decreaseIndent();
            print("Value");
            increaseIndent();
            {
                letExpression.getExpression().accept(this, context);
            }
            decreaseIndent();
        }
        decreaseIndent();
    }

    public void visit(OrderBy orderBy, ExpressionVisitorContext context) {
        print("[ORDER BY]");
        increaseIndent();
        {
            orderBy.getExpression().accept(this, context);
        }
        decreaseIndent();
    }

    public void visit(Variable variable, ExpressionVisitorContext context) {
        print("[VARIABLE] " + context.getVariable(variable));
    }

    public void visit(Return returnExpression, ExpressionVisitorContext context) {
        print("[RETURN]");
        increaseIndent();
        {
            returnExpression.getExpression().accept(this, context);
        }
        decreaseIndent();
    }

    public void visit(EvaluatedExpression expression, ExpressionVisitorContext context) {
        print("[EVALUATED EXPRESSION]");
        increaseIndent();
        {
            expression.getInnerExpression().accept(this, context);
        }
        decreaseIndent();
    }

    public void visit(XPathExpression expression, ExpressionVisitorContext context) {
        print("[XPATH]");
        increaseIndent();
        {
            print("Root");
            increaseIndent();
            {
                expression.getRoot().accept(this, context);
            }
            decreaseIndent();
            print("XPath");
            increaseIndent();
            {
                expression.getXPathAsString().accept(this, context);
            }
            decreaseIndent();
        }
        decreaseIndent();
    }

    public void visit(If ifExpression, ExpressionVisitorContext context) {
        print("[IF]");
        increaseIndent();
        {
            print("Condition");
            increaseIndent();
            {
                List<Expression> parameters = ifExpression.getParameters();
                for (Expression parameter : parameters) {
                    parameter.accept(this, context);
                }
            }
            decreaseIndent();

            print("On success");
            increaseIndent();
            {
                ifExpression.getOnSuccess().accept(this, context);
            }
            decreaseIndent();

            print("On fail");
            increaseIndent();
            {
                ifExpression.getOnFail().accept(this, context);
            }
            decreaseIndent();
        }
        decreaseIndent();
    }
}
