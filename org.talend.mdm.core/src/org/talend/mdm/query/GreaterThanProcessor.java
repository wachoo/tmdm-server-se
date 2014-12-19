package org.talend.mdm.query;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.TypedExpression;

import static com.amalto.core.query.user.UserQueryBuilder.gt;

class GreaterThanProcessor extends BasicConditionProcessor {

    static ConditionProcessor INSTANCE = new GreaterThanProcessor();

    private GreaterThanProcessor() {
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, String value) {
        return gt(expression, value);
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, TypedExpression value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getConditionElement() {
        return "gt"; //$NON-NLS-1
    }
}
