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

import com.amalto.core.metadata.*;
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import org.apache.commons.lang.NotImplementedException;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.Criterion;
import org.hibernate.engine.TypedValue;

import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

abstract class AbstractQueryHandler extends VisitorAdapter<StorageResults> {

    static final Criterion NO_OP_CRITERION = new Criterion() {
        public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
            return "1=1"; //$NON-NLS-1$
        }

        public TypedValue[] getTypedValues(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
            return new TypedValue[0];
        }
    };

    protected static final int JDBC_FETCH_SIZE = 20;

    protected final ValueAdapter VALUE_ADAPTER = new ValueAdapter();

    protected final Session session;

    protected final MappingRepository mappingMetadataRepository;

    protected final FieldAdapter FIELD_VISITOR = new FieldAdapter();

    protected final Storage storage;

    protected final StorageClassLoader storageClassLoader;

    protected final Select select;

    protected final Set<EndOfResultsCallback> callbacks;

    protected final List<TypedExpression> selectedFields;

    AbstractQueryHandler(Storage storage,
                         MappingRepository mappingMetadataRepository,
                         StorageClassLoader storageClassLoader,
                         Session session,
                         Select select,
                         List<TypedExpression> selectedFields,
                         Set<EndOfResultsCallback> callbacks) {
        this.storageClassLoader = storageClassLoader;
        this.session = session;
        this.mappingMetadataRepository = mappingMetadataRepository;
        this.storage = storage;
        this.select = select;
        this.callbacks = callbacks;
        this.selectedFields = selectedFields;
    }

    String getFieldName(Field field, MappingRepository repository) {
        return getFieldName(field.getFieldMetadata(), repository, true, true);
    }

    String getFieldName(FieldMetadata fieldMetadata, MappingRepository repository, boolean includeTypeName, boolean resolveReferencedField) {
        // Move up to the first complex type (contained type do not have any mapping).
        TypeMetadata containingType = fieldMetadata.getContainingType();
        while (containingType != null && containingType instanceof ContainedComplexTypeMetadata) {
            containingType = ((ContainedComplexTypeMetadata) containingType).getContainerType();
        }
        if (containingType == null) {
            throw new IllegalStateException("Could not find containing type mapping for field '" + fieldMetadata.getName() + "'.");
        }

        TypeMapping mapping = repository.getMappingFromUser(containingType);
        if (mapping == null) {
            throw new IllegalArgumentException("Type '" + containingType.getName() + "' does not have a mapping.");
        }

        String fieldName;
        FieldMetadata flattenField = mapping.getDatabase(fieldMetadata);
        if (flattenField == null) {
            // This is an error case, every field should have their flatten field.
            throw new IllegalStateException("Could not find mapping for field '" + fieldMetadata.getName() + "' in type '" + mapping.getName() + "'");
        }
        if (fieldMetadata instanceof ReferenceFieldMetadata) { // Handle query on FK field
            FieldMetadata referencedField = ((ReferenceFieldMetadata) fieldMetadata).getReferencedField();
            if (!(referencedField instanceof CompoundFieldMetadata) && resolveReferencedField) {
                // If asked to resolve referenced field (to return "country.id" instead of "country" -> useful for
                // conditions on FKs).
                fieldName = flattenField.getName() + '.' + getFieldName(referencedField, mappingMetadataRepository, false, true);
            } else {
                fieldName = flattenField.getName();
            }
        } else { // Simple field
            fieldName = flattenField.getName();
        }

        if (includeTypeName) {
            return mapping.getName() + '.' + fieldName;
        } else {
            return fieldName;
        }
    }

    @Override
    public StorageResults visit(Condition condition) {
        return null;
    }

    class ValueAdapter extends VisitorAdapter<Object> {

        @Override
        public Object visit(Id id) {
            List<String> ids = new LinkedList<String>();
            StringBuilder builder = null;
            for (char currentChar : id.getId().toCharArray()) {
                switch (currentChar) {
                    case '[':
                        builder = new StringBuilder();
                        break;
                    case ']':
                        if (builder != null) {
                            ids.add(builder.toString());
                        }
                        break;
                    default:
                        if (builder != null) {
                            builder.append(currentChar);
                        }
                        break;
                }
            }
            if (ids.isEmpty()) {
                throw new IllegalArgumentException("Id '" + id.getId() + "' does not match expected format (no id found).");
            }
            if (ids.size() > 1) {
                throw new NotImplementedException("No support for composite key in condition (yet)."); // TODO Support this (lookup of instance with composite key).
            }
            ComplexTypeMetadata type = id.getType();
            return MetadataUtils.convert(ids.get(0), type.getKeyFields().get(0));
        }

        @Override
        public Object visit(Condition condition) {
            return null;
        }

        @Override
        public Object visit(Compare condition) {
            return condition.getRight().accept(this);
        }

        @Override
        public Object visit(StringConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(IntegerConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(DateConstant constant) {
            return new Timestamp(constant.getValue().getTime());
        }

        @Override
        public Object visit(DateTimeConstant constant) {
            return new Timestamp(constant.getValue().getTime());
        }

        @Override
        public Object visit(BooleanConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(BigDecimalConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(TimeConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(ShortConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(ByteConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(LongConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(DoubleConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(FloatConstant constant) {
            return constant.getValue();
        }

        @Override
        public Object visit(Field field) {
            // If this happens, it is very likely wrong visitor was used (see FIELD_VISITOR).
            throw new UnsupportedOperationException("Can not compare to an other field value.");
        }
    }

    class FieldAdapter extends VisitorAdapter<String> {
        @Override
        public String visit(Revision revision) {
            return Storage.METADATA_REVISION_ID;
        }

        @Override
        public String visit(com.amalto.core.query.user.Timestamp timestamp) {
            return Storage.METADATA_TIMESTAMP;
        }

        @Override
        public String visit(TaskId taskId) {
            return Storage.METADATA_TASK_ID;
        }

        @Override
        public String visit(StagingStatus stagingStatus) {
            return Storage.METADATA_STAGING_STATUS;
        }

        @Override
        public String visit(StagingError stagingError) {
            return Storage.METADATA_STAGING_ERROR;
        }

        @Override
        public String visit(StagingSource stagingSource) {
            return Storage.METADATA_STAGING_SOURCE;
        }

        @Override
        public String visit(Field field) {
            return getFieldName(field, mappingMetadataRepository);
        }

        @Override
        public String visit(Range range) {
            Expression fieldExpression = range.getExpression();
            return fieldExpression.accept(this);
        }
    }

    protected static class FieldCondition {
        boolean isMany;

        String criterionFieldName;

        boolean isProperty;
    }
}
