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

/**
 *
 */
public interface ExpressionVisitor {

    void visit(Expression expression, ExpressionVisitorContext context);

    void visit(Function function, ExpressionVisitorContext context);

    void visit(XQueryCollection collection, ExpressionVisitorContext context);

    void visit(Condition condition, ExpressionVisitorContext context);

    void visit(Constant constant, ExpressionVisitorContext context);

    void visit(Text text, ExpressionVisitorContext context);

    void visit(Number number, ExpressionVisitorContext context);

    void visit(For forExpression, ExpressionVisitorContext context);

    void visit(Let letExpression, ExpressionVisitorContext context);

    void visit(OrderBy orderBy, ExpressionVisitorContext context);

    void visit(Variable variable, ExpressionVisitorContext context);

    void visit(Return returnExpression, ExpressionVisitorContext context);

    void visit(EvaluatedExpression expression, ExpressionVisitorContext context);

    void visit(XPathExpression expression, ExpressionVisitorContext context);

    void visit(If ifExpression, ExpressionVisitorContext context);

}
