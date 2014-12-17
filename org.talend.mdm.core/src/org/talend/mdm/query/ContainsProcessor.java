package org.talend.mdm.query;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.TypedExpression;

import static com.amalto.core.query.user.UserQueryBuilder.contains;

class ContainsProcessor extends BasicConditionProcessor {

    static ConditionProcessor INSTANCE = new ContainsProcessor();

    private ContainsProcessor() {
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, String value) {
        return contains(expression, value);
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, TypedExpression value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getConditionElement() {
        return "contains"; //$NON-NLS-1
    }
}
