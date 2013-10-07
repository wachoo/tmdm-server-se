package com.amalto.core.query.user;

import com.amalto.core.query.user.metadata.*;
import org.apache.commons.lang.NotImplementedException;

public class VisitorAdapter<T> implements Visitor<T> {
    public T visit(Select select) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(NativeQuery nativeQuery) {
        throw new NotImplementedException();
    }

    public T visit(Condition condition) {
        throw new NotImplementedException();
    }

    public T visit(Compare condition) {
        throw new NotImplementedException();
    }

    public T visit(BinaryLogicOperator condition) {
        throw new NotImplementedException();
    }

    public T visit(UnaryLogicOperator condition) {
        throw new NotImplementedException();
    }

    public T visit(Range range) {
        throw new NotImplementedException();
    }

    public T visit(Timestamp timestamp) {
        throw new NotImplementedException();
    }

    public T visit(TaskId taskId) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(Type type) {
        throw new NotImplementedException();
    }

    public T visit(StagingStatus stagingStatus) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(StagingError stagingError) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(StagingSource stagingSource) {
        throw new NotImplementedException();
    }

    public T visit(Join join) {
        throw new NotImplementedException();
    }

    public T visit(Expression expression) {
        throw new NotImplementedException();
    }

    public T visit(Predicate predicate) {
        throw new NotImplementedException();
    }

    public T visit(Field field) {
        throw new NotImplementedException();
    }

    public T visit(Alias alias) {
        throw new NotImplementedException();
    }

    public T visit(Id id) {
        throw new NotImplementedException();
    }

    public T visit(StringConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(IntegerConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(DateConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(DateTimeConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(BooleanConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(BigDecimalConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(TimeConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(ShortConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(ByteConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(LongConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(DoubleConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(FloatConstant constant) {
        throw new NotImplementedException();
    }

    public T visit(Predicate.And and) {
        throw new NotImplementedException();
    }

    public T visit(Predicate.Or or) {
        throw new NotImplementedException();
    }

    public T visit(Predicate.Equals equals) {
        throw new NotImplementedException();
    }

    public T visit(Predicate.Contains contains) {
        throw new NotImplementedException();
    }

    public T visit(IsEmpty isEmpty) {
        throw new NotImplementedException();
    }

    public T visit(NotIsEmpty notIsEmpty) {
        throw new NotImplementedException();
    }

    public T visit(IsNull isNull) {
        throw new NotImplementedException();
    }

    public T visit(NotIsNull notIsNull) {
        throw new NotImplementedException();
    }

    public T visit(OrderBy orderBy) {
        throw new NotImplementedException();
    }

    public T visit(Paging paging) {
        throw new NotImplementedException();
    }

    public T visit(Count count) {
        throw new NotImplementedException();
    }

    public T visit(Predicate.GreaterThan greaterThan) {
        throw new NotImplementedException();
    }

    public T visit(Predicate.LowerThan lowerThan) {
        throw new NotImplementedException();
    }

    public T visit(FullText fullText) {
        throw new NotImplementedException();
    }

    public T visit(FieldFullText fieldFullText) {
        throw new NotImplementedException();
    }

    public T visit(Isa isa) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(ComplexTypeExpression expression) {
        throw new NotImplementedException();
    }

    @Override
    public T visit(IndexedField indexedField) {
        throw new NotImplementedException();
    }
}
