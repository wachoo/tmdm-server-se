/*
 * Copyright (C) 2006-2013 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.inmemory;

import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.query.user.UserQueryDumpConsole;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.hibernate.CloseableIterator;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import java.io.IOException;
import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.from;

class InMemoryJoinResults implements StorageResults {

    private static final Logger LOGGER = Logger.getLogger(InMemoryJoinResults.class);

    private static final int MEMORY_COST_LIMIT = 10 * 1024 * 1024;

    private static final int UUID_SIZE = 64;

    private final InMemoryJoinNode node;

    private final Storage storage;

    private CloseableIterator<DataRecord> iterator;

    private List list;

    interface Executor {
        Set<Object> execute(Storage storage, InMemoryJoinNode node);
    }

    public InMemoryJoinResults(Storage storage, InMemoryJoinNode node) {
        this.storage = storage;
        this.node = node;
    }

    @Override
    public int getSize() {
        materializeList();
        return list.size();
    }

    @Override
    public int getCount() {
        materializeList();
        return list.size();
    }

    @Override
    public void close() {
        try {
            iterator.close();
        } catch (IOException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("Exception during close.", e);
            }
        }
    }

    @Override
    public Iterator<DataRecord> iterator() {
        materializeIterator();
        return iterator;
    }

    private synchronized void materializeList() {
        materializeIterator();
        if (list == null) {
            list = IteratorUtils.toList(iterator);
        }
    }

    private synchronized void materializeIterator() {
        if (iterator == null) {
            iterator = evaluateResults(storage, node);
        }
    }

    private static int computeTreeSize(InMemoryJoinNode node) {
        int size = node.expression == null ? 1 : 0;
        for (InMemoryJoinNode inMemoryJoinNode : node.children.keySet()) {
            size += computeTreeSize(inMemoryJoinNode);
        }
        return size;
    }

    private static int computeConditionCost(Storage storage, InMemoryJoinNode node) {
        int size = 0;
        if(node.expression != null) {
            StorageResults results = storage.fetch(node.expression);
            try {
                size += results.getCount();
            } finally {
                results.close();
            }
        }
        for (InMemoryJoinNode inMemoryJoinNode : node.children.keySet()) {
            size += computeConditionCost(storage, inMemoryJoinNode);
        }
        return size;
    }

    private static CloseableIterator<DataRecord> evaluateResults(Storage storage, InMemoryJoinNode node) {
        // Computes memory cost for check.
        // {
        //   a = sum of all path length from node
        //   b = sum of all count based on condition in tree.
        //   UUID_SIZE = size in memory (bytes) required to store a UUID.
        // }
        // memoryCost = (((a * b) * (additionalProjectionNodes + 1) * UUID_SIZE)
        int conditionCost = computeTreeSize(node) * computeConditionCost(storage, node);
        int additionalNodes = 1;
        int memoryCost = (conditionCost * (additionalNodes + 1)) * UUID_SIZE;
        if(memoryCost > MEMORY_COST_LIMIT) {
            throw new IllegalStateException("Query execution requires too much memory (" + memoryCost + " > " + MEMORY_COST_LIMIT + ")");
        }
        // Actual evaluation
        State state;
        if (node.type.isInstantiable()) {
            state = new EntityState();
        } else {
            state = new ProjectionState(); // TODO this is not always correct
        }
        return state.process(storage, node);
    }

    static Set<Object> _evaluateConditions(Storage storage, InMemoryJoinNode node) {
        if (node.expression != null) {
            Set<Object> expressionIds = new HashSet<Object>();
            StorageResults results = storage.fetch(node.expression);
            try {
                for (DataRecord result : results) {
                    for (FieldMetadata field : result.getSetFields()) {
                        expressionIds.add(result.get(field));
                    }
                }
            } finally {
                results.close();
            }
            return expressionIds;
        } else {
            Executor executor = getExecutor(node);
            Set<Object> ids = new HashSet<Object>();
            switch (node.merge) {
                case UNION:
                case NONE:
                    for (InMemoryJoinNode child : node.children.keySet()) {
                        ids.addAll(executor.execute(storage, child));
                    }
                    break;
                case INTERSECTION:
                    for (InMemoryJoinNode child : node.children.keySet()) {
                        if (ids.isEmpty()) {
                            ids.addAll(executor.execute(storage, child));
                        } else {
                            ids.retainAll(executor.execute(storage, child));
                        }
                    }
                    break;
                default:
                    throw new NotImplementedException("No support for '" + node.merge + "'.");
            }
            //
            Set<Object> returnIds = new HashSet<Object>();
            if (node.childProperty != null) {
                if (ids.isEmpty()) {
                    return Collections.emptySet();
                }
                long execTime = System.currentTimeMillis();
                {
                    UserQueryBuilder qb = from(node.type)
                            .selectId(node.type)
                            .where(buildConditionFromValues(null, node.childProperty, ids));
                    node.expression = qb.getSelect();
                    StorageResults results = storage.fetch(qb.getSelect());
                    try {
                        for (DataRecord result : results) {
                            for (FieldMetadata field : result.getSetFields()) {
                                returnIds.add(result.get(field));
                            }
                        }
                    } finally {
                        results.close();
                    }
                }
                node.execTime = System.currentTimeMillis() - execTime;
            }
            return returnIds;
        }
    }

    static Condition buildConditionFromValues(Condition existingCondition, FieldMetadata field, Set<Object> values) {
        Condition condition = null;
        for (Object id : values) {
            Condition newCondition = UserQueryBuilder.eq(field, String.valueOf(id));
            if (condition == null) {
                condition = newCondition;
            } else {
                condition = UserQueryBuilder.or(condition, newCondition);
            }
        }
        if(existingCondition != null) {
            return UserQueryBuilder.and(existingCondition, condition);
        }
        return condition;
    }

    private static Executor getExecutor(InMemoryJoinNode node) {
        if (node.children.keySet().size() == 1) {
            return new Executor() {
                @Override
                public Set<Object> execute(Storage storage, InMemoryJoinNode node) {
                    return _evaluateConditions(storage, node);
                }
            };
        } else {
            return new Executor() {
                @Override
                public Set<Object> execute(Storage storage, InMemoryJoinNode node) {
                    return _evaluateConditions(storage, node);
                }
            };
        }
    }

    private static void listNodes(InMemoryJoinNode node, List<InMemoryJoinNode> nodes) {
        nodes.add(node);
        for (InMemoryJoinNode child : node.children.keySet()) {
            listNodes(child, nodes);
        }
    }

    static void debug(InMemoryJoinNode node) {
        List<InMemoryJoinNode> orderedNodesByTime = new LinkedList<InMemoryJoinNode>();
        listNodes(node, orderedNodesByTime);
        Collections.sort(orderedNodesByTime, new Comparator<InMemoryJoinNode>() {
            @Override
            public int compare(InMemoryJoinNode o1, InMemoryJoinNode o2) {
                return (int) (o2.execTime - o1.execTime);
            }
        });
        LOGGER.debug("Execution times:");
        for (InMemoryJoinNode inMemoryJoinNode : orderedNodesByTime) {
            LOGGER.debug(inMemoryJoinNode + ": " + inMemoryJoinNode.execTime + " ms.");
        }
    }

    static void trace(InMemoryJoinNode node) {
        LOGGER.trace("====");
        LOGGER.trace("Node " + node);
        LOGGER.trace("Execution time: " + node.execTime + " ms");
        if (node.expression != null) {
            LOGGER.trace("Query:");
            node.expression.accept(new UserQueryDumpConsole());
        }
        LOGGER.trace("====");
        for (InMemoryJoinNode child : node.children.keySet()) {
            trace(child);
        }
    }

}
