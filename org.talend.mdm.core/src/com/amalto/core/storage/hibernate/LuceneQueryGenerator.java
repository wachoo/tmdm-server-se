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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.ContainedComplexTypeMetadata;
import com.amalto.core.metadata.DefaultMetadataVisitor;
import com.amalto.core.metadata.EnumerationFieldMetadata;
import com.amalto.core.metadata.ReferenceFieldMetadata;
import com.amalto.core.metadata.SimpleTypeFieldMetadata;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import org.apache.commons.lang.NotImplementedException;
import org.apache.lucene.analysis.KeywordAnalyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.MultiFieldQueryParser;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.search.*;
import org.apache.lucene.util.Version;

import java.util.*;

class LuceneQueryGenerator extends VisitorAdapter<Query> {

    private final Collection<ComplexTypeMetadata> types;

    private String currentFieldName;

    private Object currentValue;

    LuceneQueryGenerator(List<ComplexTypeMetadata> types) {
        this.types = types;
    }

    @Override
    public Query visit(Compare condition) {
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
            return termQuery;
        } else if (condition.getPredicate() == Predicate.GREATER_THAN
                || condition.getPredicate() == Predicate.GREATER_THAN_OR_EQUALS
                || condition.getPredicate() == Predicate.LOWER_THAN
                || condition.getPredicate() == Predicate.LOWER_THAN_OR_EQUALS) {
            throw new RuntimeException("Greater than, less than are not supported in full text searches.");
        } else {
            throw new NotImplementedException("No support for predicate '" + condition.getPredicate() + "'");
        }
    }

    @Override
    public Query visit(BinaryLogicOperator condition) {
        Query left = condition.getLeft().accept(this);
        Query right = condition.getRight().accept(this);
        BooleanQuery query = new BooleanQuery();
        if (condition.getPredicate() == Predicate.OR) {
            query.add(left, BooleanClause.Occur.SHOULD);
            query.add(right, BooleanClause.Occur.SHOULD);
        } else if (condition.getPredicate() == Predicate.AND) {
            query.add(left, BooleanClause.Occur.MUST);
            query.add(right, BooleanClause.Occur.MUST);
        } else {
            throw new NotImplementedException("No support for '" + condition.getPredicate() + "'.");
        }
        return query;
    }

    @Override
    public Query visit(UnaryLogicOperator condition) {
        condition.getCondition().accept(this);
        return null;
    }

    @Override
    public Query visit(Range range) {
        range.getStart().accept(this);
        Integer currentRangeStart = ((Integer) currentValue) == Integer.MIN_VALUE ? null : (Integer) currentValue;
        range.getEnd().accept(this);
        Integer currentRangeEnd = ((Integer) currentValue) == Integer.MAX_VALUE ? null : (Integer) currentValue;
        return NumericRangeQuery.newIntRange(currentFieldName,
                currentRangeStart,
                currentRangeEnd,
                true,
                true);
    }

    @Override
    public Query visit(Field field) {
        currentFieldName = "x_" + field.getFieldMetadata().getName().toLowerCase(); //$NON-NLS-1$
        return null;
    }

    @Override
    public Query visit(Alias alias) {
        return null;
    }

    @Override
    public Query visit(StringConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(IntegerConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(DateConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(DateTimeConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(BooleanConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(BigDecimalConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(TimeConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(ShortConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(ByteConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(LongConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(DoubleConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(FloatConstant constant) {
        currentValue = constant.getValue();
        return null;
    }

    @Override
    public Query visit(FullText fullText) {
        // TODO Test me on conditions where many types share same field names.
        final Set<String> fields = new HashSet<String>();
        for (final ComplexTypeMetadata type : types) {
            type.accept(new DefaultMetadataVisitor<Void>() {
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
                    if (!Storage.METADATA_TIMESTAMP.equals(simpleField.getName()) && !Storage.METADATA_TASK_ID.equals(simpleField.getName())) {
                        fields.add(simpleField.getName());
                    }
                    return null;
                }

                @Override
                public Void visit(EnumerationFieldMetadata enumField) {
                    fields.add(enumField.getName());
                    return null;
                }
            });
        }

        String[] fieldsAsArray = fields.toArray(new String[fields.size()]);
        StringBuilder queryBuffer = new StringBuilder();
        Iterator<String> fieldsIterator = fields.iterator();
        String fullTextValue = getValue(fullText);
        while (fieldsIterator.hasNext()) {
            String next = fieldsIterator.next();
            queryBuffer.append(next).append(':').append(fullTextValue);
            if (fieldsIterator.hasNext()) {
                queryBuffer.append(" OR "); //$NON-NLS-1$
            }
        }
        String fullTextQuery = queryBuffer.toString();
        return parseQuery(fieldsAsArray, fullTextQuery);
    }

    @Override
    public Query visit(FieldFullText fieldFullText) {
        String fieldName = "x_" + fieldFullText.getField().getFieldMetadata().getName().toLowerCase(); //$NON-NLS-1$
        String[] fieldsAsArray = new String[]{fieldName};
        String fullTextQuery = fieldName + ':' + getValue(fieldFullText);
        return parseQuery(fieldsAsArray, fullTextQuery);
    }

    private static Query parseQuery(String[] fieldsAsArray, String fullTextQuery) {
        MultiFieldQueryParser parser = new MultiFieldQueryParser(Version.LUCENE_29, fieldsAsArray, new KeywordAnalyzer());
        try {
            return parser.parse(fullTextQuery);
        } catch (ParseException e) {
            throw new RuntimeException("Invalid generated Lucene query", e);
        }
    }

    private static String getValue(FullText fullText) {
        String value = fullText.getValue().toLowerCase();
        int index = 0;
        while (value.charAt(index) == '*') { // Skip '*' characters at beginning.
            index++;
        }
        if (index > 0) {
            value = value.substring(index);
        }
        char[] removes = new char[]{'[', ']', '+', '!', '(', ')', '^', '\"', '~', ':', '\\'}; // Removes reserved characters
        for (char remove : removes) {
            value = value.replace(remove, ' ');
        }
        if (!value.endsWith("*")) {
            value += '*';
        }
        return value;
    }
}
