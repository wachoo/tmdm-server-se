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

package com.amalto.core.storage.hibernate;

import com.amalto.core.query.user.Expression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections.iterators.CollatingIterator;
import org.apache.commons.lang.NotImplementedException;

import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.eq;
import static com.amalto.core.query.user.UserQueryBuilder.from;

class InMemoryJoinResults implements StorageResults {

    private final InMemoryJoinNode node;

    private final Storage storage;

    private boolean isMaterialized = false;

    private CollatingIterator iterator;

    interface Executor {
        Set<Object> execute(Storage storage, InMemoryJoinNode node);
    }

    public InMemoryJoinResults(Storage storage, InMemoryJoinNode node) {
        this.storage = storage;
        this.node = node;
    }

    @Override
    public int getSize() {
        materialize();
        return IteratorUtils.toList(iterator).size();
    }

    @Override
    public int getCount() {
        materialize();
        return IteratorUtils.toList(iterator).size();
    }

    @Override
    public void close() {
    }

    @Override
    public Iterator<DataRecord> iterator() {
        materialize();
        return iterator;
    }

    private synchronized void materialize() {
        if (!isMaterialized) {
            materialize(storage, node);
            iterator = new CollatingIterator();
            for (Expression expression : node.expression) {
                iterator.addIterator(storage.fetch(expression).iterator());
            }
            isMaterialized = true;
        }
    }

    private static Set<Object> materialize(Storage storage, InMemoryJoinNode node) {
        if (!node.expression.isEmpty()) {
            Set<Object> expressionIds = new HashSet<Object>();
            for (Expression expression : node.expression) {
                StorageResults results = storage.fetch(expression);
                try {
                    for (DataRecord result : results) {
                        expressionIds.add(result.get(result.getSetFields().iterator().next()));
                    }
                } finally {
                    results.close();
                }
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
            node.ids = ids;
            //
            Set<Object> returnIds = new HashSet<Object>();
            for (Object id : ids) {
                if (node.childProperty != null) {
                    UserQueryBuilder qb = from(node.type)
                            .selectId(node.type)
                            .where(eq(node.childProperty, String.valueOf(id)));
                    node.expression.add(qb.getSelect());
                    StorageResults results = storage.fetch(qb.getSelect());
                    try {
                        for (DataRecord result : results) {
                            returnIds.add(result.get(result.getSetFields().iterator().next()));
                        }
                    } finally {
                        results.close();
                    }
                } else {
                    UserQueryBuilder qb = from(node.type)
                            .where(eq(node.type.getKeyFields().iterator().next(), String.valueOf(id)));
                    node.expression.add(qb.getSelect());
                }
            }
            return returnIds;
        }
    }

    private static Executor getExecutor(InMemoryJoinNode node) {
        if (node.children.keySet().size() == 1) {
            return new Executor() {
                @Override
                public Set<Object> execute(Storage storage, InMemoryJoinNode node) {
                    return materialize(storage, node);
                }
            };
        } else {
            return new Executor() {
                @Override
                public Set<Object> execute(Storage storage, InMemoryJoinNode node) {
                    return materialize(storage, node);
                }
            };
        }
    }
}
