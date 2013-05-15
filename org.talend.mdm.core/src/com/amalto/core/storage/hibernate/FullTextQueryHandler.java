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

import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.exception.FullTextQueryCompositeKeyException;
import com.amalto.core.storage.record.DataRecord;
import com.amalto.core.storage.record.metadata.UnsupportedDataRecordMetadata;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.StringUtils;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;
import org.hibernate.ScrollMode;
import org.hibernate.ScrollableResults;
import org.hibernate.Session;
import org.hibernate.search.FullTextQuery;
import org.hibernate.search.FullTextSession;
import org.hibernate.search.Search;
import org.talend.mdm.commmon.metadata.*;

import javax.xml.XMLConstants;
import java.io.IOException;
import java.util.*;


class FullTextQueryHandler extends AbstractQueryHandler {

    private final LinkedList<Query> subQueries = new LinkedList<Query>();

    private final Set<Class> classes = new HashSet<Class>();

    private FullTextQuery query;

    private List<ComplexTypeMetadata> types;

    private int pageSize;

    private String[] fieldsAsArray;

    private String fullTextQuery;

    private String currentFieldName;

    private Object currentValue;

    private final MappingRepository mappings;

    public FullTextQueryHandler(Storage storage,
                                MappingRepository mappings,
                                StorageClassLoader storageClassLoader,
                                Session session,
                                Select select,
                                List<TypedExpression> selectedFields,
                                Set<EndOfResultsCallback> callbacks) {
        super(storage, storageClassLoader, session, select, selectedFields, callbacks);
        this.mappings = mappings;
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
                ComplexTypeMetadata compositeKeyType = (ComplexTypeMetadata) it.next();
                message.append(compositeKeyType.getName());
                if (it.hasNext()) {
                    message.append(","); //$NON-NLS-1$
                }
            }
            throw new FullTextQueryCompositeKeyException(message.toString());
        }
        // Removes Joins and joined fields.
        types = select.getTypes();
        List<Join> joins = select.getJoins();
        if (!joins.isEmpty()) {
            Set<TypeMetadata> joinedTypes = new HashSet<TypeMetadata>();
            for (Join join : joins) {
                joinedTypes.add(join.getRightField().getFieldMetadata().getContainingType());
            }
            for (TypeMetadata joinedType : joinedTypes) {
                types.remove(joinedType);
            }
            List<TypedExpression> filteredFields = new LinkedList<TypedExpression>();
            for (TypedExpression expression : select.getSelectedFields()) {
                if (expression instanceof Field) {
                    FieldMetadata fieldMetadata = ((Field) expression).getFieldMetadata();
                    if (joinedTypes.contains(fieldMetadata.getContainingType())) {
                        TypeMapping mapping = mappings.getMappingFromDatabase(fieldMetadata.getContainingType());
                        filteredFields.add(new Alias(new StringConstant(StringUtils.EMPTY), mapping.getUser(fieldMetadata).getName()));
                    } else {
                        filteredFields.add(expression);
                    }
                } else {
                    filteredFields.add(expression);
                }
            }
            selectedFields.clear();
            selectedFields.addAll(filteredFields);
        }
        // Handle condition
        Condition condition = select.getCondition();
        if (condition == null) {
            throw new IllegalArgumentException("Expected a condition in select clause but got 0.");
        }
        condition.accept(this);
        // Create Lucene query (concatenates all sub queries together).
        FullTextSession fullTextSession = Search.getFullTextSession(session);
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, fieldsAsArray, new KeywordAnalyzer());
        BooleanQuery parsedQuery;
        try {
            parsedQuery = (BooleanQuery) parser.parse(fullTextQuery);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid generated Lucene query", e);
        }
        if (!subQueries.isEmpty()) {
            Query fullTextQuery = parsedQuery;
            parsedQuery = new BooleanQuery();
            parsedQuery.add(fullTextQuery, BooleanClause.Occur.SHOULD);
            for (Query subQuery : subQueries) {
                parsedQuery.add(subQuery, BooleanClause.Occur.MUST);
            }
        }
        // Create Hibernate Search query
        FullTextQuery fullTextQuery = fullTextSession.createFullTextQuery(parsedQuery,
                classes.toArray(new Class<?>[classes.size()]));
        // Very important to leave this null (would disable ability to search across different types)
        fullTextQuery.setCriteriaQuery(null);
        fullTextQuery.setSort(Sort.RELEVANCE);
        query = EntityFinder.wrap(fullTextQuery, (HibernateStorage) storage, session); // ensures only MDM entity objects are returned.
        // Order by
        OrderBy orderBy = select.getOrderBy();
        if (orderBy != null) {
            orderBy.accept(this);
        }
        // Paging
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
            iterator = new ListIterator(mappings, storageClassLoader, list.iterator(), callbacks);
        } else {
            iterator = new ListIterator(mappings, storageClassLoader, list.iterator(), callbacks) {
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
                            TypeMapping mapping = mappings.getMappingFromDatabase(fieldMetadata.getContainingType());
                            if (mapping != null && mapping.getUser(fieldMetadata) != null) {
                                fieldMetadata = mapping.getUser(fieldMetadata);
                            }
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
                            if (aliasName != null) {
                                SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, typeName);
                                FieldMetadata fieldMetadata = new SimpleTypeFieldMetadata(explicitProjectionType, false, false, false, aliasName, fieldType, Collections.<String>emptyList(), Collections.<String>emptyList());
                                explicitProjectionType.addField(fieldMetadata);
                                nextRecord.set(fieldMetadata, constant.getValue());
                            } else {
                                throw new IllegalStateException("Expected an alias for a constant expression.");
                            }
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
            iterator = new ScrollableIterator(mappings,
                    storageClassLoader,
                    scrollableResults,
                    callbacks);
        } else {
            iterator = new ScrollableIterator(mappings,
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
                        if (selectedField instanceof Field) {
                            FieldMetadata field = ((Field) selectedField).getFieldMetadata();
                            TypeMapping mapping = mappings.getMappingFromDatabase(field.getContainingType());
                            if (mapping != null && mapping.getUser(field) != null) {
                                field = mapping.getUser(field);
                            }
                            explicitProjectionType.addField(field);
                            if (field instanceof ReferenceFieldMetadata) {
                                nextRecord.set(field, getReferencedId(next, (ReferenceFieldMetadata) field));
                            } else {
                                nextRecord.set(field, next.get(field));
                            }
                        } else if (selectedField instanceof Alias) {
                            SimpleTypeMetadata fieldType = new SimpleTypeMetadata(XMLConstants.W3C_XML_SCHEMA_NS_URI, selectedField.getTypeName());
                            Alias alias = (Alias) selectedField;
                            SimpleTypeFieldMetadata newField = new SimpleTypeFieldMetadata(explicitProjectionType,
                                    false,
                                    false,
                                    false,
                                    alias.getAliasName(),
                                    fieldType,
                                    Collections.<String>emptyList(),
                                    Collections.<String>emptyList());
                            explicitProjectionType.addField(newField);
                            TypedExpression typedExpression = alias.getTypedExpression();
                            if (typedExpression instanceof StringConstant) {
                                nextRecord.set(newField, ((StringConstant) typedExpression).getValue());
                            } else {
                                throw new IllegalArgumentException("Aliased expression '" + typedExpression + "' is not supported.");
                            }
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
        if (record != null) {
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
        } else {
            return StringUtils.EMPTY;
        }
    }

    @Override
    public StorageResults visit(Compare condition) {
        condition.getLeft().accept(this);
        Expression right = condition.getRight();
        right.accept(this);
        if (condition.getPredicate() == Predicate.EQUALS
                || condition.getPredicate() == Predicate.CONTAINS
                || condition.getPredicate() == Predicate.STARTS_WITH) {
            StringTokenizer tokenizer = new StringTokenizer(String.valueOf(currentValue));
            BooleanQuery termQuery = new BooleanQuery();
            while (tokenizer.hasMoreTokens()) {
                TermQuery newTermQuery = new TermQuery(new Term(currentFieldName, tokenizer.nextToken().toLowerCase()));
                termQuery.add(newTermQuery, BooleanClause.Occur.MUST);
                if (condition.getPredicate() == Predicate.STARTS_WITH) {
                    break;
                }
            }
            subQueries.addLast(termQuery);
        } else if (condition.getPredicate() == Predicate.GREATER_THAN
                || condition.getPredicate() == Predicate.GREATER_THAN_OR_EQUALS
                || condition.getPredicate() == Predicate.LOWER_THAN
                || condition.getPredicate() == Predicate.LOWER_THAN_OR_EQUALS) {
            throw new RuntimeException("Greater than, less than are not supported in full text searches.");
        } else {
            throw new NotImplementedException("No support for predicate '" + condition.getPredicate() + "'");
        }
        return null;
    }

    @Override
    public StorageResults visit(BinaryLogicOperator condition) {
        condition.getLeft().accept(this);
        condition.getRight().accept(this);
        return null;
    }

    @Override
    public StorageResults visit(UnaryLogicOperator condition) {
        condition.getCondition().accept(this);
        return null;
    }

    @Override
    public StorageResults visit(Range range) {
        range.getStart().accept(this);
        Integer currentRangeStart = ((Integer) currentValue) == Integer.MIN_VALUE ? null : (Integer) currentValue;
        range.getEnd().accept(this);
        Integer currentRangeEnd = ((Integer) currentValue) == Integer.MAX_VALUE ? null : (Integer) currentValue;
        NumericRangeQuery subQuery = NumericRangeQuery.newIntRange(currentFieldName,
                currentRangeStart,
                currentRangeEnd,
                true,
                true);
        subQueries.add(subQuery);
        return null;
    }

    @Override
    public StorageResults visit(Field field) {
        currentFieldName = field.getFieldMetadata().getName();
        return null;
    }

    @Override
    public StorageResults visit(Alias alias) {
        return null;
    }

    @Override
    public StorageResults visit(StringConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(IntegerConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(DateConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(DateTimeConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(BooleanConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(BigDecimalConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(TimeConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(ShortConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(ByteConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(LongConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(DoubleConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public StorageResults visit(FloatConstant constant) {
        currentValue = constant.getValue();
        return null;
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
        final Set<String> fields = new HashSet<String>();
        for (final ComplexTypeMetadata type : types) {
            type.accept(new DefaultMetadataVisitor<Void>() {

                private void addClass(ComplexTypeMetadata complexType) {
                    String name = complexType.getName();
                    if(!complexType.isInstantiable() && !complexType.getName().startsWith("X_")) { //$NON-NLS-1$
                        name = "X_" + complexType.getName(); //$NON-NLS-1$
                    }
                    String className = ClassCreator.getClassName(name);
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
                public Void visit(ContainedComplexTypeMetadata containedType) {
                    super.visit(containedType);
                    for (ComplexTypeMetadata subType : containedType.getSubTypes()) {
                        subType.accept(this);
                    }
                    return null;
                }

                @Override
                public Void visit(ReferenceFieldMetadata referenceField) {
                    ComplexTypeMetadata referencedType = referenceField.getReferencedType();
                    if (!referencedType.isInstantiable()) {
                        referencedType.accept(this);
                    }
                    return null;
                }

                @Override
                public Void visit(SimpleTypeFieldMetadata simpleField) {
                    addClass(simpleField.getContainingType());
                    if (!Storage.METADATA_TIMESTAMP.equals(simpleField.getName()) && !Storage.METADATA_TASK_ID.equals(simpleField.getName())) {
                        fields.add(getFieldName(simpleField, false, false));
                    }
                    return null;
                }

                @Override
                public Void visit(EnumerationFieldMetadata enumField) {
                    addClass(enumField.getContainingType());
                    fields.add(getFieldName(enumField, false, false));
                    return null;
                }
            });
        }

        fieldsAsArray = fields.toArray(new String[fields.size()]);
        StringBuilder queryBuffer = new StringBuilder();
        Iterator<String> fieldsIterator = fields.iterator();
        String fullTextValue = fullText.getValue().toLowerCase();
        while (fieldsIterator.hasNext()) {
            String next = fieldsIterator.next();
            queryBuffer.append(next).append(':').append(fullTextValue).append('*');
            if (fieldsIterator.hasNext()) {
                queryBuffer.append(" OR "); //$NON-NLS-1$
            }
        }
        fullTextQuery = queryBuffer.toString();
        return null;
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
