/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.query.user;

/**
 *
 */
public interface Visitor<T> {

    T visit(Select select);

    T visit(NativeQuery nativeQuery);

    T visit(Condition condition);

    T visit(Compare condition);

    T visit(BinaryLogicOperator condition);

    T visit(UnaryLogicOperator condition);

    T visit(Range range);

    T visit(Timestamp timestamp);

    T visit(TaskId taskId);

    T visit(Type type);

    T visit(StagingStatus stagingStatus);

    T visit(StagingError stagingError);

    T visit(StagingSource stagingSource);

    T visit(Join join);

    T visit(Expression expression);

    T visit(Predicate predicate);

    T visit(Field field);

    T visit(Alias alias);

    T visit(Id id);
    
    T visit(StringConstant constant);

    T visit(IntegerConstant constant);

    T visit(DateConstant constant);

    T visit(DateTimeConstant constant);

    T visit(BooleanConstant constant);

    T visit(BigDecimalConstant constant);

    T visit(TimeConstant constant);

    T visit(ShortConstant constant);

    T visit(ByteConstant constant);

    T visit(LongConstant constant);

    T visit(DoubleConstant constant);

    T visit(FloatConstant constant);

    T visit(Predicate.And and);

    T visit(Predicate.Or or);

    T visit(Predicate.Equals equals);

    T visit(Predicate.Contains contains);
    
    T visit(IsEmpty isEmpty);

    T visit(NotIsEmpty notIsEmpty);

    T visit(IsNull isNull);

    T visit(NotIsNull notIsNull);

    T visit(OrderBy orderBy);

    T visit(Paging paging);

    T visit(Count count);

    T visit(Predicate.GreaterThan greaterThan);

    T visit(Predicate.LowerThan lowerThan);

    T visit(FullText fullText);

    T visit(Isa isa);

    T visit(FieldFullText fieldFullText);

    T visit(ComplexTypeExpression expression);
}
