/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.query.user.*;

import java.util.Iterator;

class SecurityQueryCleaner extends VisitorAdapter<Expression> {

    private final Checker checker;

    private final SecuredStorage.UserDelegator delegator;

    public SecurityQueryCleaner(SecuredStorage.UserDelegator delegator) {
        this.delegator = delegator;
        checker = new Checker(delegator);
    }

    @Override
    public Expression visit(Select select) {
        Iterator<Expression> fields = select.getSelectedFields().iterator();
        while (fields.hasNext()) {
            if (!fields.next().accept(checker)) {
                fields.remove();
            }
        }

        Iterator<ComplexTypeMetadata> types = select.getTypes().iterator();
        while (types.hasNext()) {
            if (delegator.hide(types.next())) {
                types.remove();
            }
        }
        if (select.getTypes().isEmpty()) {
            throw new IllegalStateException("Security rules removed all selected types in query.");
        }

        Condition condition = select.getCondition();
        if (condition != null) {
            select.setCondition((Condition) condition.accept(this));
        }

        OrderBy orderBy = select.getOrderBy();
        if (orderBy != null) {
            if (!orderBy.accept(checker)) {
                select.setOrderBy(null);
            }
        }

        return select;
    }

    @Override
    public Expression visit(NotIsEmpty notIsEmpty) {
        if (!notIsEmpty.getField().accept(checker)) {
            return UserQueryHelper.NO_OP_CONDITION;
        } else {
            return notIsEmpty;
        }
    }

    @Override
    public Expression visit(NotIsNull notIsNull) {
        if (!notIsNull.getField().accept(checker)) {
            return UserQueryHelper.NO_OP_CONDITION;
        } else {
            return notIsNull;
        }
    }

    @Override
    public Expression visit(IsEmpty isEmpty) {
        if (!isEmpty.getField().accept(checker)) {
            return UserQueryHelper.NO_OP_CONDITION;
        } else {
            return isEmpty;
        }
    }

    @Override
    public Expression visit(IsNull isNull) {
        if (!isNull.getField().accept(checker)) {
            return UserQueryHelper.NO_OP_CONDITION;
        } else {
            return isNull;
        }
    }

    @Override
    public Expression visit(BinaryLogicOperator condition) {
        if (!condition.getLeft().accept(checker) && !condition.getRight().accept(checker)) {
            return UserQueryHelper.NO_OP_CONDITION;
        } else if (!condition.getLeft().accept(checker)) {
            return new BinaryLogicOperator(UserQueryHelper.NO_OP_CONDITION, condition.getPredicate(), condition.getRight());
        } else if (!condition.getRight().accept(checker)) {
            return new BinaryLogicOperator(condition.getLeft(), condition.getPredicate(), UserQueryHelper.NO_OP_CONDITION);
        } else {
            return condition;
        }
    }

    @Override
    public Expression visit(UnaryLogicOperator condition) {
        if (!condition.getCondition().accept(checker)) {
            return new UnaryLogicOperator(UserQueryHelper.NO_OP_CONDITION, condition.getPredicate());
        } else {
            return condition;
        }
    }

    @Override
    public Expression visit(Compare condition) {
        if (!condition.getLeft().accept(checker) || !condition.getRight().accept(checker)) {
            return UserQueryHelper.NO_OP_CONDITION;
        } else {
            return condition;
        }
    }

    @Override
    public Expression visit(Condition condition) {
        return condition;
    }

    @Override
    public Expression visit(FullText fullText) {
        // TODO Could maybe restrict full text depending on user rights... but could also be in result creation.
        return fullText;
    }
}
