package org.talend.mdm.storage.inmemory.matcher;

import com.amalto.core.query.user.Predicate;
import org.talend.mdm.storage.record.DataRecord;

public class UnaryMatcher implements Matcher {

    public static enum Operator {
        NOT
    }

    private final Matcher matcher;

    private final Predicate predicate;

    public UnaryMatcher(Matcher matcher, Predicate predicate) {
        this.matcher = matcher;
        this.predicate = predicate;
    }

    @Override
    public boolean match(DataRecord record) {
        if (predicate == Predicate.NOT) {
            return !matcher.match(record);
        }
        return matcher.match(record);
    }
}
