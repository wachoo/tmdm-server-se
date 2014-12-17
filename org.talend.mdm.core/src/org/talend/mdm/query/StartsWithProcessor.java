package org.talend.mdm.query;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.TypedExpression;

import static com.amalto.core.query.user.UserQueryBuilder.startsWith;

class StartsWithProcessor extends BasicConditionProcessor {

    static ConditionProcessor INSTANCE = new StartsWithProcessor();

    private StartsWithProcessor() {
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, String value) {
        return startsWith(expression, value);
    }

    @Override
    protected Condition buildCondition(TypedExpression expression, TypedExpression value) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected String getConditionElement() {
        return "startsWith"; //$NON-NLS-1
    }
}
