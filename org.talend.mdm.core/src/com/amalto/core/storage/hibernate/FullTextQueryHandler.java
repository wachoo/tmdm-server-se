/*
 * Copyright (C) 2006-2012 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.ContainedTypeFieldMetadata;
import com.amalto.core.metadata.FieldMetadata;
import com.amalto.core.metadata.TypeMetadata;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.hibernate.enhancement.HibernateClassCreator;
import com.amalto.core.storage.record.DataRecord;
import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.Sort;
import org.apache.lucene.util.Version;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


class FullTextQueryHandler extends AbstractQueryHandler {

    private FullTextQuery query;

    private List<ComplexTypeMetadata> types;

    private int pageSize;

    private String revisionId;

    public FullTextQueryHandler(Storage storage,
                                MappingMetadataRepository repository,
                                StorageClassLoader storageClassLoader,
                                Session session,
                                Select select,
                                List<TypedExpression> selectedFields,
                                Set<EndOfResultsCallback> callbacks) {
        super(storage, repository, storageClassLoader, session, select, selectedFields, callbacks);
    }

    @Override
    public StorageResults visit(Select select) {
        List<Join> joins = select.getJoins();
        if (!joins.isEmpty()) {
            throw new IllegalArgumentException("Cannot perform join clause(s) when doing full text search.");
        }
        if (select.isProjection()) {
            Expression projectedField = select.getSelectedFields().get(0);
            if (!(projectedField instanceof Count)) {
                throw new UnsupportedOperationException("No support for field projections in full text query builder.");
            }
        }
        revisionId = select.getRevisionId();

        Condition condition = select.getCondition();
        if (condition == null) {
            throw new IllegalArgumentException("Expected a condition in select clause but got 0.");
        }

        boolean isValidFullTextQuery = condition.accept(new VisitorAdapter<Boolean>() {
            @Override
            public Boolean visit(Condition condition) {
                return false;
            }

            @Override
            public Boolean visit(UnaryLogicOperator condition) {
                return condition.getCondition().accept(this);
            }

            @Override
            public Boolean visit(BinaryLogicOperator condition) {
                return condition.getLeft().accept(this) && condition.getRight().accept(this);
            }

            @Override
            public Boolean visit(Compare condition) {
                return false;
            }

            @Override
            public Boolean visit(FullText fullText) {
                return true;
            }
        });
        if (!isValidFullTextQuery) {
            throw new IllegalArgumentException("Expected a full text search clause and *only* a full text search.");
        }

        types = select.getTypes();
        condition.accept(this);

        OrderBy orderBy = select.getOrderBy();
        if (orderBy != null) {
            orderBy.accept(this);
        }

        Paging paging = select.getPaging();
        paging.accept(this);

        pageSize = paging.getLimit();
        boolean hasPaging = pageSize < Integer.MAX_VALUE;
        if (!hasPaging) {
            return createResults(query.scroll(ScrollMode.FORWARD_ONLY));
        } else {
            return createResults(query.list());
        }
    }

    private StorageResults createResults(List list) {
        CloseableIterator<DataRecord> iterator = new ListIterator(mappingMetadataRepository, storageClassLoader, storageName, revisionId, list.iterator(), callbacks);
        return new FullTextStorageResults(pageSize, query.getResultSize(), iterator);
    }

    private StorageResults createResults(ScrollableResults scrollableResults) {
        CloseableIterator<DataRecord> iterator = new ScrollableIterator(mappingMetadataRepository,
                storageClassLoader,
                storageName,
                revisionId,
                scrollableResults,
                callbacks);
        return new FullTextStorageResults(pageSize, query.getResultSize(), iterator);
    }

    @Override
    public StorageResults visit(Paging paging) {
        query.setFirstResult(paging.getStart());
        query.setFetchSize(JDBC_FETCH_SIZE);
        query.setMaxResults(paging.getLimit());
        return null;
    }

    @Override
    public StorageResults visit(OrderBy orderBy) {
        throw new NotImplementedException("No support for order by for full text search.");
    }

    @Override
    public StorageResults visit(FullText fullText) {
        // TODO Test me on conditions where many types share same field names.
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        try {
            List<Class> classes = new LinkedList<Class>();
            List<String> fields = new LinkedList<String>();
            for (TypeMetadata type : types) {
                try {
                    if (type instanceof ComplexTypeMetadata) {
                        for (FieldMetadata currentField : ((ComplexTypeMetadata) type).getFields()) {
                            if (!(currentField instanceof ContainedTypeFieldMetadata)) {
                                if (!currentField.isMany()) {
                                    fields.add(getFieldName(currentField, mappingMetadataRepository, false, false));
                                } else {
                                    // TODO Support for search on many fields
                                }
                            } // TODO Support for contained types
                        }
                        Class<?> clazz = Thread.currentThread().getContextClassLoader().loadClass(HibernateClassCreator.PACKAGE_PREFIX + type.getName());
                        classes.add(clazz);
                    } else {
                        throw new NotImplementedException("No support for full text queries on simple types.");
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalArgumentException("Type '" + type.getName() + "' has no generated class in current class loader.");
                }
            }

            String[] fieldsAsArray = fields.toArray(new String[fields.size()]);
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, fieldsAsArray, new KeywordAnalyzer());
            StringBuilder queryBuffer = new StringBuilder();
            Iterator<String> fieldsIterator = fields.iterator();
            while (fieldsIterator.hasNext()) {
                queryBuffer.append(fieldsIterator.next()).append(':').append(fullText.getValue()).append("*");
                if (fieldsIterator.hasNext()) {
                    queryBuffer.append(" OR ");
                }
            }
            org.apache.lucene.search.Query parse = parser.parse(queryBuffer.toString());

            FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(parse, classes.toArray(new Class<?>[classes.size()]));
            // Very important to leave this null (would disable ability to search across different types)
            fullTextQuery.setCriteriaQuery(null);
            fullTextQuery.setSort(Sort.RELEVANCE);
            this.query = fullTextQuery;

            return null;
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }

    private static class FullTextStorageResults implements StorageResults {
        
        private final int size;
        
        private final int count;
        
        private final CloseableIterator<DataRecord> iterator;

        public FullTextStorageResults(int size, int count, CloseableIterator<DataRecord> iterator) {
            this.size = size;
            this.count = count;
            this.iterator = iterator;
        }

        public int getSize() {
            return size;
        }

        public int getCount() {
            return count;
        }

        public void close() {
            try {
                iterator.close();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public Iterator<DataRecord> iterator() {
            return iterator;
        }
    }
}
