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
import com.amalto.core.query.user.*;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

import java.util.*;

public class InMemoryJoinStrategy implements Visitor<StorageResults> {

    private final List<FieldMetadata> fields = new LinkedList<FieldMetadata>();

    private final Storage storage;

    private final Map<FieldMetadata ,Compare> groupedCompare = new HashMap<FieldMetadata, Compare>();

    public InMemoryJoinStrategy(Storage storage) {
        this.storage = storage;
    }

    private InMemoryJoinNode buildExecutionTree(ComplexTypeMetadata type, List<FieldMetadata> fields) {
        InMemoryJoinNode root = new InMemoryJoinNode();
        root.name = type.getName();
        root.type = type;
        // Build nodes (based on path)
        for (FieldMetadata field : fields) {
            InMemoryJoinNode current = root;
            List<FieldMetadata> path = MetadataUtils.path(type, field);
            for (FieldMetadata fieldMetadata : path) {
                // TODO intersection / union / ?
                InMemoryJoinNode node = new InMemoryJoinNode();
                node.name = fieldMetadata.getName();
                node.type = fieldMetadata.getContainingType();
                node.childProperty = fieldMetadata;
                if (!current.children.containsKey(node)) {
                    current.children.put(node, node);
                    current = node;
                } else {
                    current = current.children.get(node);
                }
            }
            current.expression.add(UserQueryBuilder.from(current.type)
                    .selectId(current.type)
                    .where(groupedCompare.get(current.childProperty))
                    .getExpression());
        }
        return root;
    }

    @Override
    public StorageResults visit(Select select) {
        // Get main type
        List<ComplexTypeMetadata> types = select.getTypes();
        if (types.isEmpty()) {
            throw new IllegalArgumentException("Select clause must select one type.");
        }
        if (types.size() > 1) {
            throw new IllegalArgumentException("Select clause must select only one type (was " + types.size() + ").");
        }
        ComplexTypeMetadata type = types.get(0);
        // Get conditions paths
        if (select.getCondition() != null) {
            select.getCondition().accept(this);
        }
        // Build tree
        InMemoryJoinNode node = buildExecutionTree(type, fields);
        // Return Storage Results
        return new InMemoryJoinResults(storage, node);
    }

    @Override
    public StorageResults visit(NativeQuery nativeQuery) {
        return null;
    }

    @Override
    public StorageResults visit(Condition condition) {
        return null;
    }

    @Override
    public StorageResults visit(Compare condition) {
        condition.getLeft().accept(this);
        if (condition.getLeft() instanceof Field) {
            FieldMetadata fieldMetadata = ((Field) condition.getLeft()).getFieldMetadata();
            groupedCompare.put(fieldMetadata, condition);
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
        return null;
    }

    @Override
    public StorageResults visit(Timestamp timestamp) {
        return null;
    }

    @Override
    public StorageResults visit(TaskId taskId) {
        return null;
    }

    @Override
    public StorageResults visit(Type type) {
        fields.add(type.getField().getFieldMetadata());
        return null;
    }

    @Override
    public StorageResults visit(StagingStatus stagingStatus) {
        return null;
    }

    @Override
    public StorageResults visit(StagingError stagingError) {
        return null;
    }

    @Override
    public StorageResults visit(StagingSource stagingSource) {
        return null;
    }

    @Override
    public StorageResults visit(Join join) {
        return null;
    }

    @Override
    public StorageResults visit(Expression expression) {
        return null;
    }

    @Override
    public StorageResults visit(Predicate predicate) {
        return null;
    }

    @Override
    public StorageResults visit(Field field) {
        fields.add(field.getFieldMetadata());
        return null;
    }

    @Override
    public StorageResults visit(Alias alias) {
        return null;
    }

    @Override
    public StorageResults visit(Id id) {
        return null;
    }

    @Override
    public StorageResults visit(StringConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(IntegerConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(DateConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(DateTimeConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(BooleanConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(BigDecimalConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(TimeConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(ShortConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(ByteConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(LongConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(DoubleConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(FloatConstant constant) {
        return null;
    }

    @Override
    public StorageResults visit(Predicate.And and) {
        return null;
    }

    @Override
    public StorageResults visit(Predicate.Or or) {
        return null;
    }

    @Override
    public StorageResults visit(Predicate.Equals equals) {
        return null;
    }

    @Override
    public StorageResults visit(Predicate.Contains contains) {
        return null;
    }

    @Override
    public StorageResults visit(IsEmpty isEmpty) {
        isEmpty.getField().accept(this);
        return null;
    }

    @Override
    public StorageResults visit(NotIsEmpty notIsEmpty) {
        notIsEmpty.getField().accept(this);
        return null;
    }

    @Override
    public StorageResults visit(IsNull isNull) {
        isNull.getField().accept(this);
        return null;
    }

    @Override
    public StorageResults visit(NotIsNull notIsNull) {
        notIsNull.getField().accept(this);
        return null;
    }

    @Override
    public StorageResults visit(OrderBy orderBy) {
        return null;
    }

    @Override
    public StorageResults visit(Paging paging) {
        return null;
    }

    @Override
    public StorageResults visit(Count count) {
        return null;
    }

    @Override
    public StorageResults visit(Predicate.GreaterThan greaterThan) {
        return null;
    }

    @Override
    public StorageResults visit(Predicate.LowerThan lowerThan) {
        return null;
    }

    @Override
    public StorageResults visit(FullText fullText) {
        return null;
    }

    @Override
    public StorageResults visit(Isa isa) {
        // TODO to do ?
        return null;
    }

    @Override
    public StorageResults visit(ComplexTypeExpression expression) {
        return null;
    }

    @Override
    public StorageResults visit(IndexedField indexedField) {
        fields.add(indexedField.getFieldMetadata());
        return null;
    }
}
