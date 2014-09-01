/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package org.talend.mdm.storage.hibernate;

import org.talend.mdm.commmon.metadata.InboundReferences;
import org.apache.lucene.search.*;
import org.hibernate.*;
import org.hibernate.Query;
import org.hibernate.criterion.CriteriaSpecification;
import org.hibernate.criterion.Restrictions;
import org.hibernate.search.FullTextFilter;
import org.hibernate.search.FullTextQuery;
import org.hibernate.transform.ResultTransformer;
import org.hibernate.type.Type;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.ReferenceFieldMetadata;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.Clob;
import java.util.*;

public class EntityFinder {

    private EntityFinder() {
    }

    /**
     * Starting from <code>wrapper</code>, goes up the containment tree using references introspection in metadata.
     * @param wrapper A {@link Wrapper} instance (so an object managed by {@link HibernateStorage}.
     * @param storage A {@link HibernateStorage} instance. It will be used to compute references from the internal
     *                data model.
     * @param session A Hibernate {@link Session}.
     * @return The top level (aka the Wrapper instance that represent a MDM entity).
     */
    public static Wrapper findEntity(Wrapper wrapper, HibernateStorage storage, Session session) {
        ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
        if (!(contextClassLoader instanceof StorageClassLoader)) {
            throw new IllegalStateException("Expects method to be called in the context of a storage operation.");
        }
        StorageClassLoader classLoader = (StorageClassLoader) contextClassLoader;
        ComplexTypeMetadata wrapperType = classLoader.getTypeFromClass(wrapper.getClass());
        if (wrapperType == null) {
            throw new IllegalArgumentException("Wrapper '" + wrapper.getClass().getName() + "' isn't known in current storage.");
        }
        if (wrapperType.isInstantiable()) {
            return wrapper;
        }
        InboundReferences incomingReferences = new InboundReferences(wrapperType);
        InternalRepository internalRepository = storage.getTypeEnhancer();
        Set<ReferenceFieldMetadata> references = internalRepository.getInternalRepository().accept(incomingReferences);
        if (references.isEmpty()) {
            throw new IllegalStateException("Cannot find container type for '" + wrapperType.getName() + "'.");
        }
        String keyFieldName = wrapperType.getKeyFields().iterator().next().getName();
        Object id = wrapper.get(keyFieldName);
        for (ReferenceFieldMetadata reference : references) {
            ComplexTypeMetadata containingType = reference.getContainingType();
            Class<? extends Wrapper> clazz = classLoader.getClassFromType(containingType);
            Criteria criteria = session.createCriteria(clazz, "a0"); //$NON-NLS-1$
            criteria.createAlias("a0." + reference.getName(), "a1", CriteriaSpecification.INNER_JOIN); //$NON-NLS-1$
            criteria.add(Restrictions.eq("a1." + keyFieldName, id)); //$NON-NLS-1$
            List list = criteria.list();
            if (!list.isEmpty()) {
                Wrapper container = (Wrapper) list.get(0);
                if (list.size() > 1) {
                    Object previousItem = list.get(0);
                    for(int i = 1; i < list.size(); i++) {
                        Object currentItem = list.get(i);
                        if(!previousItem.equals(currentItem)) {
                            throw new IllegalStateException("Expected contained instance to have only one owner.");
                        }
                        previousItem = currentItem;
                    }
                }
                return findEntity(container, storage, session);
            }
        }
        return null;
    }

    /**
     * Wraps a {@link FullTextQuery} so it returns only "top level" Hibernate objects (iso. of possible technical objects).
     * This method ensures all methods that returns results will return expected results.
     *
     * @see org.hibernate.Query#scroll()
     * @param query The full text query to wrap.
     * @param storage The {@link HibernateStorage} implementation used to perform the query.
     * @param session A open, read for immediate usage Hibernate {@link Session}.
     * @return A wrapper that implements and supports all methods of {@link FullTextQuery}.
     */
    public static FullTextQuery wrap(FullTextQuery query, HibernateStorage storage, Session session) {
        return new QueryWrapper(query, storage, session);
    }

    private static class ScrollableResultsWrapper implements ScrollableResults {
        private final ScrollableResults scrollableResults;

        private final HibernateStorage storage;

        private final Session session;

        public ScrollableResultsWrapper(ScrollableResults scrollableResults, HibernateStorage storage, Session session) {
            this.scrollableResults = scrollableResults;
            this.storage = storage;
            this.session = session;
        }

        public boolean next() throws HibernateException {
            return scrollableResults.next();
        }

        public boolean previous() throws HibernateException {
            return scrollableResults.previous();
        }

        public boolean scroll(int i) throws HibernateException {
            return scrollableResults.scroll(i);
        }

        public boolean last() throws HibernateException {
            return scrollableResults.last();
        }

        public boolean first() throws HibernateException {
            return scrollableResults.first();
        }

        public void beforeFirst() throws HibernateException {
            scrollableResults.beforeFirst();
        }

        public void afterLast() throws HibernateException {
            scrollableResults.afterLast();
        }

        public boolean isFirst() throws HibernateException {
            return scrollableResults.isFirst();
        }

        public boolean isLast() throws HibernateException {
            return scrollableResults.isLast();
        }

        public void close() throws HibernateException {
            scrollableResults.close();
        }

        public Object[] get() throws HibernateException {
            Object[] objects = scrollableResults.get();
            Object[] entities = new Object[objects.length];
            int i = 0;
            for (Object object : objects) {
                entities[i++] = EntityFinder.findEntity((Wrapper) object, storage, session);
            }
            return entities;
        }

        public Object get(int i) throws HibernateException {
            return EntityFinder.findEntity((Wrapper) scrollableResults.get(i), storage, session);
        }

        public Type getType(int i) {
            return scrollableResults.getType(i);
        }

        public Integer getInteger(int col) throws HibernateException {
            return scrollableResults.getInteger(col);
        }

        public Long getLong(int col) throws HibernateException {
            return scrollableResults.getLong(col);
        }

        public Float getFloat(int col) throws HibernateException {
            return scrollableResults.getFloat(col);
        }

        public Boolean getBoolean(int col) throws HibernateException {
            return scrollableResults.getBoolean(col);
        }

        public Double getDouble(int col) throws HibernateException {
            return scrollableResults.getDouble(col);
        }

        public Short getShort(int col) throws HibernateException {
            return scrollableResults.getShort(col);
        }

        public Byte getByte(int col) throws HibernateException {
            return scrollableResults.getByte(col);
        }

        public Character getCharacter(int col) throws HibernateException {
            return scrollableResults.getCharacter(col);
        }

        public byte[] getBinary(int col) throws HibernateException {
            return scrollableResults.getBinary(col);
        }

        public String getText(int col) throws HibernateException {
            return scrollableResults.getText(col);
        }

        public Blob getBlob(int col) throws HibernateException {
            return scrollableResults.getBlob(col);
        }

        public Clob getClob(int col) throws HibernateException {
            return scrollableResults.getClob(col);
        }

        public String getString(int col) throws HibernateException {
            return scrollableResults.getString(col);
        }

        public BigDecimal getBigDecimal(int col) throws HibernateException {
            return scrollableResults.getBigDecimal(col);
        }

        public BigInteger getBigInteger(int col) throws HibernateException {
            return scrollableResults.getBigInteger(col);
        }

        public Date getDate(int col) throws HibernateException {
            return scrollableResults.getDate(col);
        }

        public Locale getLocale(int col) throws HibernateException {
            return scrollableResults.getLocale(col);
        }

        public Calendar getCalendar(int col) throws HibernateException {
            return scrollableResults.getCalendar(col);
        }

        public TimeZone getTimeZone(int col) throws HibernateException {
            return scrollableResults.getTimeZone(col);
        }

        public int getRowNumber() throws HibernateException {
            return scrollableResults.getRowNumber();
        }

        public boolean setRowNumber(int rowNumber) throws HibernateException {
            return scrollableResults.setRowNumber(rowNumber);
        }
    }

    private static class IteratorWrapper implements Iterator {
        private final Iterator iterator;

        private final HibernateStorage storage;

        private final Session session;

        public IteratorWrapper(Iterator iterator, HibernateStorage storage, Session session) {
            this.iterator = iterator;
            this.storage = storage;
            this.session = session;
        }

        public boolean hasNext() {
            return iterator.hasNext();
        }

        public Object next() {
            return EntityFinder.findEntity((Wrapper) iterator.next(), storage, session);
        }

        public void remove() {
            iterator.remove();
        }
    }

    private static class QueryWrapper implements FullTextQuery {

        private final FullTextQuery query;

        private final HibernateStorage storage;

        private final Session session;

        public QueryWrapper(FullTextQuery query, HibernateStorage storage, Session session) {
            this.query = query;
            this.storage = storage;
            this.session = session;
        }

        public String getQueryString() {
            return query.getQueryString();
        }

        public Type[] getReturnTypes() throws HibernateException {
            return query.getReturnTypes();
        }

        public String[] getReturnAliases() throws HibernateException {
            return query.getReturnAliases();
        }

        public String[] getNamedParameters() throws HibernateException {
            return query.getNamedParameters();
        }

        public Iterator iterate() throws HibernateException {
            Iterator iterator = query.iterate();
            return new IteratorWrapper(iterator, storage, session);
        }

        public ScrollableResults scroll() throws HibernateException {
            ScrollableResults scrollableResults = query.scroll();
            return new ScrollableResultsWrapper(scrollableResults, storage, session);
        }

        public ScrollableResults scroll(ScrollMode scrollMode) throws HibernateException {
            ScrollableResults scrollableResults = query.scroll(scrollMode);
            return new ScrollableResultsWrapper(scrollableResults, storage, session);
        }

        public List list() throws HibernateException {
            List list = query.list();
            List newList = new ArrayList(list.size());
            for (Object item : list) {
                newList.add(EntityFinder.findEntity((Wrapper) item, storage, session));
            }
            return newList;
        }

        public Object uniqueResult() throws HibernateException {
            return query.uniqueResult();
        }

        public int executeUpdate() throws HibernateException {
            return query.executeUpdate();
        }

        public FullTextQuery setMaxResults(int maxResults) {
            return query.setMaxResults(maxResults);
        }

        public FullTextQuery setFirstResult(int firstResult) {
            return query.setFirstResult(firstResult);
        }

        public boolean isReadOnly() {
            return query.isReadOnly();
        }

        public Query setReadOnly(boolean readOnly) {
            return query.setReadOnly(readOnly);
        }

        public Query setCacheable(boolean cacheable) {
            return query.setCacheable(cacheable);
        }

        public Query setCacheRegion(String cacheRegion) {
            return query.setCacheRegion(cacheRegion);
        }

        public Query setTimeout(int timeout) {
            return query.setTimeout(timeout);
        }

        public FullTextQuery setFetchSize(int fetchSize) {
            return query.setFetchSize(fetchSize);
        }

        public Query setLockOptions(LockOptions lockOptions) {
            return query.setLockOptions(lockOptions);
        }

        public Query setLockMode(String alias, LockMode lockMode) {
            return query.setLockMode(alias, lockMode);
        }

        public Query setComment(String comment) {
            return query.setComment(comment);
        }

        public Query setFlushMode(FlushMode flushMode) {
            return query.setFlushMode(flushMode);
        }

        public Query setCacheMode(CacheMode cacheMode) {
            return query.setCacheMode(cacheMode);
        }

        public Query setParameter(int position, Object val, Type type) {
            return query.setParameter(position, val, type);
        }

        public Query setParameter(String name, Object val, Type type) {
            return query.setParameter(name, val, type);
        }

        public Query setParameter(int position, Object val) throws HibernateException {
            return query.setParameter(position, val);
        }

        public Query setParameter(String name, Object val) throws HibernateException {
            return query.setParameter(name, val);
        }

        public Query setParameters(Object[] values, Type[] types) throws HibernateException {
            return query.setParameters(values, types);
        }

        public Query setParameterList(String name, Collection vals, Type type) throws HibernateException {
            return query.setParameterList(name, vals, type);
        }

        public Query setParameterList(String name, Collection vals) throws HibernateException {
            return query.setParameterList(name, vals);
        }

        public Query setParameterList(String name, Object[] vals, Type type) throws HibernateException {
            return query.setParameterList(name, vals, type);
        }

        public Query setParameterList(String name, Object[] vals) throws HibernateException {
            return query.setParameterList(name, vals);
        }

        public Query setProperties(Object bean) throws HibernateException {
            return query.setProperties(bean);
        }

        public Query setProperties(Map bean) throws HibernateException {
            return query.setProperties(bean);
        }

        public Query setString(int position, String val) {
            return query.setString(position, val);
        }

        public Query setCharacter(int position, char val) {
            return query.setCharacter(position, val);
        }

        public Query setBoolean(int position, boolean val) {
            return query.setBoolean(position, val);
        }

        public Query setByte(int position, byte val) {
            return query.setByte(position, val);
        }

        public Query setShort(int position, short val) {
            return query.setShort(position, val);
        }

        public Query setInteger(int position, int val) {
            return query.setInteger(position, val);
        }

        public Query setLong(int position, long val) {
            return query.setLong(position, val);
        }

        public Query setFloat(int position, float val) {
            return query.setFloat(position, val);
        }

        public Query setDouble(int position, double val) {
            return query.setDouble(position, val);
        }

        public Query setBinary(int position, byte[] val) {
            return query.setBinary(position, val);
        }

        public Query setText(int position, String val) {
            return query.setText(position, val);
        }

        public Query setSerializable(int position, Serializable val) {
            return query.setSerializable(position, val);
        }

        public Query setLocale(int position, Locale locale) {
            return query.setLocale(position, locale);
        }

        public Query setBigDecimal(int position, BigDecimal number) {
            return query.setBigDecimal(position, number);
        }

        public Query setBigInteger(int position, BigInteger number) {
            return query.setBigInteger(position, number);
        }

        public Query setDate(int position, Date date) {
            return query.setDate(position, date);
        }

        public Query setTime(int position, Date date) {
            return query.setTime(position, date);
        }

        public Query setTimestamp(int position, Date date) {
            return query.setTimestamp(position, date);
        }

        public Query setCalendar(int position, Calendar calendar) {
            return query.setCalendar(position, calendar);
        }

        public Query setCalendarDate(int position, Calendar calendar) {
            return query.setCalendarDate(position, calendar);
        }

        public Query setString(String name, String val) {
            return query.setString(name, val);
        }

        public Query setCharacter(String name, char val) {
            return query.setCharacter(name, val);
        }

        public Query setBoolean(String name, boolean val) {
            return query.setBoolean(name, val);
        }

        public Query setByte(String name, byte val) {
            return query.setByte(name, val);
        }

        public Query setShort(String name, short val) {
            return query.setShort(name, val);
        }

        public Query setInteger(String name, int val) {
            return query.setInteger(name, val);
        }

        public Query setLong(String name, long val) {
            return query.setLong(name, val);
        }

        public Query setFloat(String name, float val) {
            return query.setFloat(name, val);
        }

        public Query setDouble(String name, double val) {
            return query.setDouble(name, val);
        }

        public Query setBinary(String name, byte[] val) {
            return query.setBinary(name, val);
        }

        public Query setText(String name, String val) {
            return query.setText(name, val);
        }

        public Query setSerializable(String name, Serializable val) {
            return query.setSerializable(name, val);
        }

        public Query setLocale(String name, Locale locale) {
            return query.setLocale(name, locale);
        }

        public Query setBigDecimal(String name, BigDecimal number) {
            return query.setBigDecimal(name, number);
        }

        public Query setBigInteger(String name, BigInteger number) {
            return query.setBigInteger(name, number);
        }

        public Query setDate(String name, Date date) {
            return query.setDate(name, date);
        }

        public Query setTime(String name, Date date) {
            return query.setTime(name, date);
        }

        public Query setTimestamp(String name, Date date) {
            return query.setTimestamp(name, date);
        }

        public Query setCalendar(String name, Calendar calendar) {
            return query.setCalendar(name, calendar);
        }

        public Query setCalendarDate(String name, Calendar calendar) {
            return query.setCalendarDate(name, calendar);
        }

        public Query setEntity(int position, Object val) {
            return query.setEntity(position, val);
        }

        public Query setEntity(String name, Object val) {
            return query.setEntity(name, val);
        }

        public FullTextQuery setResultTransformer(ResultTransformer transformer) {
            return query.setResultTransformer(transformer);
        }

        @Override
        public <T> T unwrap(Class<T> tClass) {
            return query.unwrap(tClass);
        }

        @Override
        public FullTextQuery setSort(Sort sort) {
            return query.setSort(sort);
        }

        @Override
        public FullTextQuery setFilter(org.apache.lucene.search.Filter filter) {
            return query.setFilter(filter);
        }

        @Override
        public int getResultSize() {
            return query.getResultSize();
        }

        @Override
        public FullTextQuery setCriteriaQuery(Criteria criteria) {
            return query.setCriteriaQuery(criteria);
        }

        @Override
        public FullTextQuery setProjection(String... strings) {
            return query.setProjection(strings);
        }

        @Override
        public FullTextFilter enableFullTextFilter(String s) {
            return query.enableFullTextFilter(s);
        }

        @Override
        public void disableFullTextFilter(String s) {
            query.disableFullTextFilter(s);
        }

        @Override
        public Explanation explain(int i) {
            return query.explain(i);
        }
    }
}
