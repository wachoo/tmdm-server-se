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

import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.query.user.Paging;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.TypedExpression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.storage.EmptyIterator;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.EntityMode;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.dialect.Dialect;
import org.hibernate.engine.TypedValue;
import org.hibernate.type.IntegerType;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;

import java.util.*;

import static com.amalto.core.query.user.UserQueryBuilder.*;

public class InClauseOptimization extends StandardQueryHandler {

    public static final Logger LOGGER = Logger.getLogger(InClauseOptimization.class);
    private final Mode mode;

    public static enum Mode {
        SUB_QUERY,
        CONSTANT
    }

    public InClauseOptimization(Storage storage,
                                MappingRepository mappings,
                                TableResolver resolver,
                                StorageClassLoader storageClassLoader,
                                Session session,
                                Select select,
                                List<TypedExpression> selectedFields,
                                Set<EndOfResultsCallback> callbacks,
                                Mode mode) {
        super(storage, mappings, resolver, storageClassLoader, session, select, selectedFields, callbacks);
        this.mode = mode;
    }

    @Override
    public StorageResults visit(Select select) {
        // Standard criteria
        Criteria criteria = createCriteria(select);
        // Create in clause for the id
        ComplexTypeMetadata mainType = select.getTypes().get(0);
        Paging paging = select.getPaging();
        int start = paging.getStart();
        int limit = paging.getLimit();
        switch (mode) {
            case SUB_QUERY:
                throw new NotImplementedException("Not supported in this MDM version");
                //
                // Uncomment lines below for supporting this (NOT SUPPORTED ON ALL DATABASES!!!).
                //
                // ComplexTypeMetadata typeMetadata = (ComplexTypeMetadata) MetadataUtils.getSuperConcreteType(mainType);
                // String idColumnName = typeMetadata.getKeyFields().iterator().next().getName();
                // String tableName = typeMetadata.getName();
                // criteria.add(new IdInSubQueryClause(idColumnName, tableName, start, limit));
            case CONSTANT:
                UserQueryBuilder qb = from(mainType)
                        .selectId(mainType)
                        .start(start)
                        .limit(limit);
                if (select.getCondition() != null) {
                    qb.where(select.getCondition());
                }
                List<String[]> constants;
                if (limit != Integer.MAX_VALUE) {
                    constants = new ArrayList<String[]>(limit);
                } else {
                    constants = new LinkedList<String[]>();
                }
                // TODO Prevent session close (DO NOT REMOVE).
                if (!HibernateStorage.isMDMTransaction.get()) {
                    storage.begin();
                    Set<EndOfResultsCallback> newCallbacks = new HashSet<EndOfResultsCallback>(callbacks);
                    newCallbacks.add(new EndOfResultsCallback() {
                        @Override
                        public void onEndOfResults() {
                            storage.rollback();
                        }
                    });
                    callbacks = newCallbacks;
                }
                // Get ids for constant list
                StorageResults records = storage.fetch(qb.getSelect());
                for (DataRecord record : records) {
                    Set<FieldMetadata> setFields = record.getSetFields();
                    String[] constant = new String[setFields.size()];
                    int i = 0;
                    for (FieldMetadata setField : setFields) {
                        Object o = record.get(setField);
                        constant[i++] = String.valueOf(o);
                    }
                    constants.add(constant);
                }
                if (!constants.isEmpty()) {
                    TypeMapping mapping = mappingMetadataRepository.getMappingFromUser(mainType);
                    criteria.add(new IdInConstantClause(mapping.getDatabase().getKeyFields(), constants));
                } else {
                    return new HibernateStorageResults(storage, select, EmptyIterator.INSTANCE);
                }
                break;
        }
        // Create results
        List list = criteria.list();
        return createResults(list, select.isProjection());
    }

    private static class IdInConstantClause implements Criterion {

        private final Collection<FieldMetadata> keyFields;

        private final List<String[]> values;

        public IdInConstantClause(Collection<FieldMetadata> keyFields, List<String[]> values) {
            this.keyFields = keyFields;
            this.values = values;
        }

        @Override
        public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
            StringBuilder inClause = new StringBuilder();
            String alias = criteriaQuery.getSQLAlias(criteria);
            Iterator<FieldMetadata> keyFieldIterator = keyFields.iterator();
            int i = 0;
            while (keyFieldIterator.hasNext()) {
                inClause.append(alias).append('.').append(keyFieldIterator.next().getName()).append(" IN ").append('('); //$NON-NLS-1$
                Iterator<String[]> valuesIterator = values.iterator();
                while (valuesIterator.hasNext()) {
                    inClause.append('\'').append(valuesIterator.next()[i]).append('\'');
                    if (valuesIterator.hasNext()) {
                        inClause.append(',');
                    }
                }
                inClause.append(')');
                if (keyFieldIterator.hasNext()) {
                    inClause.append(" AND "); //$NON-NLS-1$
                }
                i++;
            }
            return inClause.toString();
        }

        @Override
        public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
            return new TypedValue[0];
        }
    }

    // Not used: but would be interesting to use this on databases that support it.
    private static class IdInSubQueryClause implements Criterion {

        private final String idColumnName;

        private final String tableName;

        private final int start;

        private final int limit;

        public IdInSubQueryClause(String idColumnName, String tableName, int start, int limit) {
            this.idColumnName = idColumnName;
            this.tableName = tableName;
            this.start = start;
            this.limit = limit;
        }

        @Override
        public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
            Dialect dialect = criteriaQuery.getFactory().getDialect();
            boolean useOffset = dialect.supportsLimitOffset();
            if (!dialect.supportsLimit()) {
                throw new HibernateException("Can not use this optimization: database does not support limits.");
            }
            String sql = "SELECT " //$NON-NLS-1$
                    + idColumnName
                    + " FROM " //$NON-NLS-1$
                    + tableName;
            String sqlWithLimitString = dialect.getLimitString(
                    sql,
                    useOffset ? start : 0,
                    dialect.useMaxForLimit() ? Integer.MAX_VALUE : limit);
            String alias = criteriaQuery.getSQLAlias(criteria);
            return alias + "." + idColumnName + " IN (" + sqlWithLimitString + ")"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        }

        @Override
        public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
            Dialect dialect = criteriaQuery.getFactory().getDialect();
            boolean useOffset = dialect.supportsLimitOffset() && start > 0;
            if (useOffset) {
                return new TypedValue[]{new TypedValue(new IntegerType(), limit, EntityMode.POJO)
                        , new TypedValue(new IntegerType(), start, EntityMode.POJO)};
            } else {
                return new TypedValue[]{new TypedValue(new IntegerType(), limit, EntityMode.POJO)};
            }
        }
    }
}
