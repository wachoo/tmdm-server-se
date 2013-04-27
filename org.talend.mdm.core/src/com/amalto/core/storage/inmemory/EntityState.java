package com.amalto.core.storage.inmemory;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.storage.EmptyIterator;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.hibernate.CloseableIterator;
import com.amalto.core.storage.record.DataRecord;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import java.util.HashSet;
import java.util.Set;

import static com.amalto.core.query.user.UserQueryBuilder.from;

/**
*
*/
class EntityState implements State {

    private static final Logger LOGGER = Logger.getLogger(EntityState.class);

    @Override
    public CloseableIterator<DataRecord> process(Storage storage, InMemoryJoinNode node) {
        Set<Object> childIds = new HashSet<Object>();
        for (InMemoryJoinNode child : node.children.keySet()) {
            childIds.addAll(InMemoryJoinResults._evaluateConditions(storage, child));
        }
        if (LOGGER.isDebugEnabled()) {
            InMemoryJoinResults.debug(node);
        }
        if (LOGGER.isTraceEnabled()) {
            InMemoryJoinResults.trace(node);
        }
        if (childIds.isEmpty()) {
            return EmptyIterator.INSTANCE;
        } else {
            FieldMetadata field = node.type.getKeyFields().iterator().next(); // TODO Support compound keys.
            Condition condition = InMemoryJoinResults.buildConditionFromValues(node.expression.getCondition(), field, childIds);
            UserQueryBuilder qb = from(node.type).where(condition);
            return (CloseableIterator<DataRecord>) storage.fetch(qb.getSelect()).iterator();
        }
    }
}
