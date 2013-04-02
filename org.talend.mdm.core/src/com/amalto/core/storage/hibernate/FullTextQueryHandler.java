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

import java.io.IOException;
import java.util.*;

import javax.xml.XMLConstants;

import org.talend.mdm.commmon.metadata.*;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
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

import com.amalto.core.query.user.Alias;
import com.amalto.core.query.user.BinaryLogicOperator;
import com.amalto.core.query.user.Compare;
import com.amalto.core.query.user.Condition;
import com.amalto.core.query.user.Field;
import com.amalto.core.query.user.FullText;
import com.amalto.core.query.user.Join;
import com.amalto.core.query.user.OrderBy;
import com.amalto.core.query.user.Paging;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.StringConstant;
import com.amalto.core.query.user.TaskId;
import com.amalto.core.query.user.Timestamp;
import com.amalto.core.query.user.TypedExpression;
import com.amalto.core.query.user.UnaryLogicOperator;
import com.amalto.core.query.user.VisitorAdapter;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.exception.FullTextQueryCompositeKeyException;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;


class FullTextQueryHandler extends AbstractQueryHandler {

    private FullTextQuery query;

    private List<ComplexTypeMetadata> types;

    private int pageSize;

    public FullTextQueryHandler(Storage storage,
                                MappingRepository repository,
                                StorageClassLoader storageClassLoader,
                                Session session,
                                Select select,
                                List<TypedExpression> selectedFields,
                                Set<EndOfResultsCallback> callbacks) {
        super(storage, repository, storageClassLoader, session, select, selectedFields, callbacks);
    }

    @Override
    public StorageResults visit(Select select) {        
        // TMDM-4654: Checks if entity has a composite PK.
        Set<ComplexTypeMetadata> compositeKeyTypes = new HashSet<ComplexTypeMetadata>();
        for (ComplexTypeMetadata type : select.getTypes()) {
            if (type.getKeyFields().size() > 1) {
                compositeKeyTypes.add(type);
            }
        }        
        if (!compositeKeyTypes.isEmpty()) {
            StringBuilder message = new StringBuilder();
            Iterator it = compositeKeyTypes.iterator();
            while (it.hasNext()) {
                ComplexTypeMetadata compositeKeyType = (ComplexTypeMetadata)it.next();
                message.append(compositeKeyType.getName());
                if (it.hasNext()) {
                    message.append(","); //$NON-NLS-1$
                }
            }          
            throw new FullTextQueryCompositeKeyException(message.toString());
        }
        // TODO Removes Join
        List<Join> joins = select.getJoins();
        if (!joins.isEmpty()) {
            throw new IllegalArgumentException("Cannot perform join clause(s) when doing full text search.");
        }

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
        CloseableIterator<DataRecord> iterator;
        if (selectedFields.isEmpty()) {
            iterator = new ListIterator(mappingMetadataRepository, storageClassLoader, list.iterator(), callbacks);
        } else {
            iterator = new ListIterator(mappingMetadataRepository, storageClassLoader, list.iterator(), callbacks) {
                @Override
                public DataRecord next() {
                    final DataRecord next = super.next();
                    final ComplexTypeMetadata explicitProjectionType = new ComplexTypeMetadataImpl(StringUtils.EMPTY, Storage.PROJECTION_TYPE, false);
                    final DataRecord nextRecord = new DataRecord(explicitProjectionType, UnsupportedDataRecordMetadata.INSTANCE);
                    VisitorAdapter<Void> visitor = new VisitorAdapter<Void>() {
                        private String aliasName;

                        private String typeName;

                        @Override
                        public Void visit(Field field) {
                            FieldMetadata fieldMetadata = field.getFieldMetadata();
                            if (aliasName != null) {
                                SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName == null ? fieldMetadata.getType().getName() : typeName);
                                fieldMetadata = new SimpleTypeFieldMetadata(explicitProjectionType, false, false, false, aliasName, fieldType, Collections.<String>emptyList(), Collections.<String>emptyList());
                                explicitProjectionType.addField(fieldMetadata);
                            } else {
                                explicitProjectionType.addField(fieldMetadata);
                            }
                            Object value;
                            if (fieldMetadata instanceof ReferenceFieldMetadata) {
                                value = getReferencedId(next, (ReferenceFieldMetadata) fieldMetadata);
                            } else {
                                value = next.get(fieldMetadata);
                            }
                            nextRecord.set(fieldMetadata, value);
                            return null;
                        }

                        @Override
                        public Void visit(StringConstant constant) {
                            return null;
                        }

                        @Override
                        public Void visit(Alias alias) {
                            aliasName = alias.getAliasName();
                            typeName = alias.getTypeName();
                            {
                                alias.getTypedExpression().accept(this);
                            }
                            aliasName = null;
                            typeName = null;
                            return null;
                        }

                        @Override
                        public Void visit(Timestamp timestamp) {
                            SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName);
                            SimpleTypeFieldMetadata aliasField = new SimpleTypeFieldMetadata(explicitProjectionType, false, false, false, aliasName, fieldType, Collections.<String>emptyList(), Collections.<String>emptyList());
                            explicitProjectionType.addField(aliasField);
                            nextRecord.set(aliasField, next.getRecordMetadata().getLastModificationTime());
                            return null;
                        }

                        @Override
                        public Void visit(TaskId taskId) {
                            SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName);
                            SimpleTypeFieldMetadata aliasField = new SimpleTypeFieldMetadata(explicitProjectionType, false, false, false, aliasName, fieldType, Collections.<String>emptyList(), Collections.<String>emptyList());
                            explicitProjectionType.addField(aliasField);
                            nextRecord.set(aliasField, next.getRecordMetadata().getTaskId());
                            return null;
                        }
                    };
                    for (TypedExpression selectedField : selectedFields) {
                        selectedField.accept(visitor);
                    }
                    return nextRecord;
                }
            };
        }
        return new FullTextStorageResults(pageSize, query.getResultSize(), iterator);
    }

    private StorageResults createResults(ScrollableResults scrollableResults) {
        CloseableIterator<DataRecord> iterator;
        if (selectedFields.isEmpty()) {
            iterator = new ScrollableIterator(mappingMetadataRepository,
                    storageClassLoader,
                    scrollableResults,
                    callbacks);
        } else {
            iterator = new ScrollableIterator(mappingMetadataRepository,
                    storageClassLoader,
                    scrollableResults,
                    callbacks) {
                @Override
                public DataRecord next() {
                    DataRecord next = super.next();
                    ComplexTypeMetadata explicitProjectionType = new ComplexTypeMetadataImpl(StringUtils.EMPTY,
                            Storage.PROJECTION_TYPE,
                            false);
                    DataRecord nextRecord = new DataRecord(explicitProjectionType, UnsupportedDataRecordMetadata.INSTANCE);
                    for (TypedExpression selectedField : selectedFields) {
                        FieldMetadata field = ((Field) selectedField).getFieldMetadata();
                        explicitProjectionType.addField(field);
                        if (field instanceof ReferenceFieldMetadata) {
                            nextRecord.set(field, getReferencedId(next, (ReferenceFieldMetadata) field));
                        } else {
                            nextRecord.set(field, next.get(field));
                        }
                    }
                    DefaultValidationHandler handler = new DefaultValidationHandler();
                    explicitProjectionType.freeze(handler);
                    handler.end();
                    return nextRecord;
                }
            };
        }

        return new FullTextStorageResults(pageSize, query.getResultSize(), iterator);
    }

    private static Object getReferencedId(DataRecord next, ReferenceFieldMetadata field) {
        DataRecord record = (DataRecord) next.get(field);
        if (record != null){
            Collection<FieldMetadata> keyFields = record.getType().getKeyFields();
            if (keyFields.size() == 1) {
                return record.get(keyFields.iterator().next());
            } else {
                List<Object> compositeKeyValues = new ArrayList<Object>(keyFields.size());
                for (FieldMetadata keyField : keyFields) {
                    compositeKeyValues.add(record.get(keyField));
                }
                return compositeKeyValues;
            }
        }else{
            return StringUtils.EMPTY;
        }        
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
            final Set<Class> classes = new HashSet<Class>();
            final Set<String> fields = new HashSet<String>();
            for (final ComplexTypeMetadata type : types) {
                final TypeMapping typeMapping = mappingMetadataRepository.getMappingFromUser(type);
                type.accept(new DefaultMetadataVisitor<Void>() {

                    private void addClass(ComplexTypeMetadata complexType) {
                        String className = ClassCreator.getClassName(complexType.getName());
                        try {
                            ClassLoader contextClassLoader = Thread.currentThread().getContextClassLoader();
                            classes.add(contextClassLoader.loadClass(className));
                        } catch (ClassNotFoundException e) {
                            throw new RuntimeException("Could not find class '" + className + "'.", e);
                        }
                    }

                    @Override
                    public Void visit(ComplexTypeMetadata complexType) {
                        addClass(complexType);
                        super.visit(complexType);
                        return null;
                    }

                    @Override
                    public Void visit(SimpleTypeFieldMetadata simpleField) {
                        addClass(typeMapping.getDatabase(simpleField).getContainingType());
                        fields.add(getFieldName(simpleField, mappingMetadataRepository, false, false));
                        return null;
                    }

                    @Override
                    public Void visit(EnumerationFieldMetadata enumField) {
                        addClass(typeMapping.getDatabase(enumField).getContainingType());
                        fields.add(getFieldName(enumField, mappingMetadataRepository, false, false));
                        return null;
                    }
                });
            }

            String[] fieldsAsArray = fields.toArray(new String[fields.size()]);
            MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, fieldsAsArray, new KeywordAnalyzer());
            StringBuilder queryBuffer = new StringBuilder();
            Iterator<String> fieldsIterator = fields.iterator();
            while (fieldsIterator.hasNext()) {
                String next = fieldsIterator.next();
                queryBuffer.append(next).append(':').append(fullText.getValue()).append("*"); //$NON-NLS-1$
                if (fieldsIterator.hasNext()) {
                    queryBuffer.append(" OR "); //$NON-NLS-1$
                }
            }
            org.apache.lucene.search.Query parse = parser.parse(queryBuffer.toString());

            FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(parse, classes.toArray(new Class<?>[classes.size()]));
            // Very important to leave this null (would disable ability to search across different types)
            fullTextQuery.setCriteriaQuery(null);
            fullTextQuery.setSort(Sort.RELEVANCE);
            this.query = EntityFinder.wrap(fullTextQuery, (HibernateStorage) storage, session);

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
