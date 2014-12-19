package org.talend.mdm.query;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.TypedExpression;

import static com.amalto.core.query.user.UserQueryBuilder.lt;

class LessThanProcessor extends BasicConditionProcessor {

    static ConditionProcessor INSTANCE = new LessThanProcessor();

    private LessThanProcessor() {
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, String value) {
        return lt(expression, value);
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, TypedExpression value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getConditionElement() {
        return "lt"; //$NON-NLS-1
    }
}
