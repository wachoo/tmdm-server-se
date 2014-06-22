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

import com.amalto.core.metadata.*;
import org.apache.log4j.Logger;

import java.util.List;

/**
 *
 */
public class UserQueryDumpConsole implements Visitor<Void> {
    private int indent;

    private Logger logger;

    public UserQueryDumpConsole() {
    }

    public UserQueryDumpConsole(Logger logger) {
        this.logger = logger;
    }

    public Void visit(Select select) {
        print("[SELECT]");
        increaseIndent();
        {
            print("Types");
            increaseIndent();
            {
                List<ComplexTypeMetadata> types = select.getTypes();
                for (ComplexTypeMetadata type : types) {
                    print(type.getName());
                }
            }
            decreaseIndent();

            print("Selected fields");
            increaseIndent();
            {
                List<TypedExpression> selectedFields = select.getSelectedFields();
                for (Expression selectedField : selectedFields) {
                    selectedField.accept(this);
                }
            }
            decreaseIndent();

            print("Join");
            increaseIndent();
            {
                List<Join> joins = select.getJoins();
                if (!joins.isEmpty()) {
                    for (Join join : joins) {
                        join.accept(this);
                    }
                } else {
                    print("<NONE>");
                }

            }
            decreaseIndent();

            print("Condition");
            increaseIndent();
            {
                Condition condition = select.getCondition();
                if (condition != null) {
                    condition.accept(this);
                } else {
                    print("<NONE>");
                }
            }
            decreaseIndent();

            print("Order by");
            increaseIndent();
            {
                OrderBy orderBy = select.getOrderBy();
                if (orderBy != null) {
                    orderBy.accept(this);
                } else {
                    print("<NONE>");
                }

            }
            decreaseIndent();

            print("Paging");
            increaseIndent();
            {
                Paging paging = select.getPaging();
                if (paging != null) {
                    paging.accept(this);
                } else {
                    print("<NONE>");
                }

            }
            decreaseIndent();
        }
        decreaseIndent();

        return null;
    }

    @Override
    public Void visit(NativeQuery nativeQuery) {
        print("[NATIVE QUERY]");
        increaseIndent();
        {
            print(nativeQuery.getQueryText());
        }
        decreaseIndent();
        return null;
    }

    private void increaseIndent() {
        indent++;
    }

    private void decreaseIndent() {
        indent--;
    }

    public Void visit(Condition condition) {
        if (condition == UserQueryHelper.NO_OP_CONDITION) {
            print("[NO OP (TRUE)]");
        } else {
            print("Unvisited condition: " + condition.toString());
        }
        return null;
    }

    public Void visit(Compare condition) {
        print("[COMPARE]");
        increaseIndent();
        {
            condition.getLeft().accept(this);
            condition.getPredicate().accept(this);
            condition.getRight().accept(this);
        }
        decreaseIndent();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Void visit(BinaryLogicOperator condition) {
        print("[BINARY LOGIC OPERATOR]");
        increaseIndent();
        {
            condition.getLeft().accept(this);
            condition.getPredicate().accept(this);
            condition.getRight().accept(this);
        }
        decreaseIndent();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Void visit(UnaryLogicOperator condition) {
        print("[UNARY LOGIC OPERATOR]");
        increaseIndent();
        {
            condition.getPredicate().accept(this);
            condition.getCondition().accept(this);
        }
        decreaseIndent();
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public Void visit(Range range) {
        print("[RANGE]");
        increaseIndent();
        {
            print("Expression");
            increaseIndent();
            {
                range.getExpression().accept(this);
            }
            decreaseIndent();
            print("Start");
            increaseIndent();
            {
                range.getStart().accept(this);
            }
            decreaseIndent();
            print("End");
            increaseIndent();
            {
                range.getEnd().accept(this);
            }
            decreaseIndent();
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Timestamp timestamp) {
        print("[Technical field: TIMESTAMP]");
        return null;
    }

    public Void visit(TaskId taskId) {
        print("[Technical field: TASK_ID]");
        return null;
    }

    @Override
    public Void visit(Type type) {
        print("[Field type name]");
        increaseIndent();
        type.getField().accept(this);
        decreaseIndent();
        return null;
    }

    public Void visit(StagingStatus stagingStatus) {
        print("[Technical field: STAGING_STATUS]");
        return null;
    }

    @Override
    public Void visit(StagingError stagingError) {
        print("[Technical field: STAGING_ERROR]");
        return null;
    }

    @Override
    public Void visit(StagingSource stagingSource) {
        print("[Technical field: STAGING_SOURCE]");
        return null;
    }

    public Void visit(Join join) {
        print("[JOIN]");
        increaseIndent();
        {
            print("[LEFT]");
            increaseIndent();
            {
                join.getLeftField().accept(this);
            }
            decreaseIndent();
            print("[RIGHT]");
            increaseIndent();
            {
                join.getRightField().accept(this);
            }
            decreaseIndent();
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Expression expression) {
        print("Unvisited expression: " + expression.toString());
        return null;
    }

    public Void visit(Predicate predicate) {
        if (predicate == Predicate.NOT) {
            print("[PREDICATE] NOT");
        } else if (predicate == Predicate.STARTS_WITH) {
            print("[PREDICATE] STARTS WITH");
        } else if (predicate == Predicate.EQUALS) {
            print("[PREDICATE] EQUALS");
        } else if (predicate == Predicate.GREATER_THAN) {
            print("[PREDICATE] GREATER THAN");
        } else if (predicate == Predicate.GREATER_THAN_OR_EQUALS) {
            print("[PREDICATE] GREATER THAN OR EQUALS");
        } else if (predicate == Predicate.LOWER_THAN) {
            print("[PREDICATE] LOWER THAN");
        } else if (predicate == Predicate.LOWER_THAN_OR_EQUALS) {
            print("[PREDICATE] LOWER THAN OR EQUALS");
        } else {
            print("Unvisited predicate: " + predicate.toString());
        }
        return null;
    }

    public Void visit(Field field) {
        print("[FIELD]");
        increaseIndent();
        {
            field.getFieldMetadata().accept(new DefaultMetadataVisitor<Void>() {
                @Override
                public Void visit(ReferenceFieldMetadata referenceField) {
                    print(getFieldPath(referenceField));
                    return null;
                }

                @Override
                public Void visit(SimpleTypeFieldMetadata simpleField) {
                    print(getFieldPath(simpleField));
                    return null;
                }

                @Override
                public Void visit(EnumerationFieldMetadata enumField) {
                    print(getFieldPath(enumField));
                    return null;
                }

                @Override
                public Void visit(ContainedTypeFieldMetadata containedField) {
                    print(containedField.getName());
                    return null;
                }

                private String getFieldPath(FieldMetadata field) {
                    StringBuilder path = new StringBuilder();
                    ComplexTypeMetadata containingType = field.getContainingType();
                    while (containingType != null) {
                        path.insert(0, containingType.getName() + '/');
                        if (containingType instanceof ContainedComplexTypeMetadata) {
                            containingType = ((ContainedComplexTypeMetadata) containingType).getContainerType();
                        } else {
                            containingType = null;
                        }
                    }
                    path.append(field.getName());
                    return path.toString();
                }
            });
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Alias alias) {
        print("[ALIAS]");
        increaseIndent();
        {
            print("Alias name: " + alias.getAliasName());
            alias.getTypedExpression().accept(this);
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Id id) {
        print("[ID REFERENCE]");
        increaseIndent();
        {
            print("Type -> '" + id.getType().getName() + "'");
            print("Value -> '" + id.getId() + "'");
        }
        decreaseIndent();
        return null;
    }

    public Void visit(StringConstant constant) {
        print("[STRING CONSTANT]");
        increaseIndent();
        {
            print("Value -> '" + constant.getValue() + "'");
        }
        decreaseIndent();
        return null;
    }

    public Void visit(IntegerConstant constant) {
        print("[INTEGER CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(DateConstant constant) {
        print("[DATE CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(DateTimeConstant constant) {
        print("[DATE TIME CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(BooleanConstant constant) {
        print("[BOOLEAN CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(BigDecimalConstant constant) {
        print("[BIG DECIMAL CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(TimeConstant constant) {
        print("[TIME CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(ShortConstant constant) {
        print("[SHORT CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(ByteConstant constant) {
        print("[BYTE CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(LongConstant constant) {
        print("[LONG CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(DoubleConstant constant) {
        print("[DOUBLE CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(FloatConstant constant) {
        print("[FLOAT CONSTANT]");
        increaseIndent();
        {
            print("Value -> " + constant.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Predicate.And and) {
        print("[PREDICATE] AND");
        return null;
    }

    public Void visit(Predicate.Or or) {
        print("[PREDICATE] OR");
        return null;
    }

    public Void visit(Predicate.Equals equals) {
        print("[PREDICATE] EQUALS");
        return null;
    }

    public Void visit(Predicate.Contains contains) {
        print("[PREDICATE] CONTAINS");
        return null;
    }

    public Void visit(IsEmpty isEmpty) {
        print("[IS EMPTY]");
        increaseIndent();
        {
            isEmpty.getField().accept(this);
        }
        decreaseIndent();
        return null;
    }

    public Void visit(NotIsEmpty notIsEmpty) {
        print("[NOT IS EMPTY]");
        increaseIndent();
        {
            notIsEmpty.getField().accept(this);
        }
        decreaseIndent();
        return null;
    }

    public Void visit(IsNull isNull) {
        print("[IS NULL]");
        increaseIndent();
        {
            isNull.getField().accept(this);
        }
        decreaseIndent();
        return null;
    }

    public Void visit(NotIsNull notIsNull) {
        print("[NOT IS NULL]");
        increaseIndent();
        {
            notIsNull.getField().accept(this);
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Predicate.GreaterThan greaterThan) {
        print("[PREDICATE] GREATER THAN");
        return null;
    }

    public Void visit(Predicate.LowerThan lowerThan) {
        print("[PREDICATE] LOWER THAN");
        return null;
    }

    public Void visit(FullText fullText) {
        print("[FULLTEXT]");
        increaseIndent();
        {
            print("Value -> " + fullText.getValue());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Isa isa) {
        print("[IS A]");
        increaseIndent();
        {
            isa.getExpression().accept(this);
            print("Type -> " + isa.getType().getName());
        }
        decreaseIndent();
        return null;
    }

    @Override
    public Void visit(ComplexTypeExpression expression) {
        print("Type: " + expression.getTypeName());
        return null;
    }

    public Void visit(OrderBy orderBy) {
        print("[ORDER BY]");
        increaseIndent();
        {
            orderBy.getField().accept(this);
            print("Direction: " + orderBy.getDirection());
        }
        decreaseIndent();
        return null;
    }

    public Void visit(Paging paging) {
        print("[PAGING]");
        increaseIndent();
        {
            print("Start: " + paging.getStart());
            if (paging.getLimit() == Integer.MAX_VALUE) {
                print("Limit: <NO LIMIT>");
            } else {
                print("Limit: " + paging.getLimit());
            }
        }
        decreaseIndent();
        return null;
    }

    @Override
    public Void visit(FieldFullText fieldFullText) {
        print("[FIELD FULL TEXT]"); //$NON-NLS-1$
        increaseIndent();
        {
            print("Value -> " + fieldFullText.getValue()); //$NON-NLS-1$
            fieldFullText.getField().accept(this);
        }
        decreaseIndent();
        return null;
    }
    public Void visit(Count count) {
        print("[COUNT]");
        return null;
    }

    private void print(String message) {
        if (logger != null) {
            StringBuilder indentString = new StringBuilder();
            for (int i = 0; i < indent; i++) {
                indentString.append('\t');
            }
            indentString.append(message);
            logger.debug(indentString.toString());
        } else {
            for (int i = 0; i < indent; i++) {
                System.out.print('\t');
            }
            System.out.println(message);
        }
    }
}
