// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================

package com.amalto.core.storage.services;

import com.amalto.core.query.user.*;
import com.amalto.core.query.user.metadata.*;
import com.amalto.core.storage.StorageType;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

class IncompatibleExpressions implements Visitor<Set<Expression>> {

    private final Set<Expression> incompatibleExpressions = new HashSet<>();

    private final StorageType type;

    public IncompatibleExpressions(StorageType type) {
        this.type = type;
    }

    @Override
    public Set<Expression> visit(Select select) {
        // Check select fields
        List<TypedExpression> selectedFields = select.getSelectedFields();
        for (TypedExpression selectedField : selectedFields) {
            selectedField.accept(this);
        }
        // Check select conditions
        Condition condition = select.getCondition();
        if (condition != null) {
            condition.accept(this);
        }
        // Check select joins
        List<Join> joins = select.getJoins();
        for (Join join : joins) {
            join.accept(this);
        }
        // Check order by
        List<OrderBy> orderBy = select.getOrderBy();
        for (OrderBy by : orderBy) {
            by.accept(this);
        }
        // Check history
        At history = select.getHistory();
        if (history != null) {
            history.accept(this);
        }
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(NativeQuery nativeQuery) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Condition condition) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Max max) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Min min) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Compare condition) {
        condition.getLeft().accept(this);
        condition.getRight().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(BinaryLogicOperator condition) {
        condition.getLeft().accept(this);
        condition.getRight().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(UnaryLogicOperator condition) {
        condition.getCondition().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Range range) {
        range.getExpression().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Type type) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Distinct distinct) {
        distinct.getExpression().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Join join) {
        join.getLeftField().accept(this);
        join.getRightField().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Expression expression) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Predicate predicate) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Field field) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Alias alias) {
        alias.getTypedExpression().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Id id) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(ConstantCollection collection) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(StringConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(IntegerConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(DateConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(DateTimeConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(BooleanConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(BigDecimalConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(TimeConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(ShortConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(ByteConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(LongConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(DoubleConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(FloatConstant constant) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(IsEmpty isEmpty) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(NotIsEmpty notIsEmpty) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(IsNull isNull) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(NotIsNull notIsNull) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(OrderBy orderBy) {
        orderBy.getExpression().accept(this);
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Paging paging) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Count count) {
        final TypedExpression expression = count.getExpression();
        if (expression != null) {
            expression.accept(this);
        }
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(FullText fullText) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Isa isa) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(ComplexTypeExpression expression) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(IndexedField indexedField) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(FieldFullText fieldFullText) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(At at) {
        return incompatibleExpressions;
    }


    @Override
    public Set<Expression> visit(ConstantCondition constantCondition) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(Timestamp timestamp) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(TaskId taskId) {
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(StagingStatus stagingStatus) {
        if (type == StorageType.MASTER) {
            incompatibleExpressions.add(stagingStatus);
        }
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(StagingError stagingError) {
        if (type == StorageType.MASTER) {
            incompatibleExpressions.add(stagingError);
        }
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(StagingSource stagingSource) {
        if (type == StorageType.MASTER) {
            incompatibleExpressions.add(stagingSource);
        }
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(StagingBlockKey stagingBlockKey) {
        if (type == StorageType.MASTER) {
            incompatibleExpressions.add(stagingBlockKey);
        }
        return incompatibleExpressions;
    }

    @Override
    public Set<Expression> visit(GroupSize groupSize) {
        if (type == StorageType.MASTER) {
            incompatibleExpressions.add(groupSize);
        }
        return incompatibleExpressions;
    }
}
