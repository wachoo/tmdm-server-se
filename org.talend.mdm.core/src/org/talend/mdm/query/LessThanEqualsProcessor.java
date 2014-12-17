package org.talend.mdm.query;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.TypedExpression;

import static com.amalto.core.query.user.UserQueryBuilder.lte;

class LessThanEqualsProcessor extends BasicConditionProcessor {

    static ConditionProcessor INSTANCE = new LessThanEqualsProcessor();

    private LessThanEqualsProcessor() {
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, String value) {
        return lte(expression, value);
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, TypedExpression value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getConditionElement() {
        return "lte"; //$NON-NLS-1
    }
}
