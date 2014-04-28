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

package com.amalto.core.storage.hibernate;

import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.talend.mdm.commmon.metadata.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.Type;

class ManyFieldCriterion extends SQLCriterion {

    private final RDBMSDataSource dataSource;

    private final Criteria typeCriteria;

    private final FieldMetadata field;

    private final Object value;

    private final int position;

    private final TableResolver resolver;

    ManyFieldCriterion(RDBMSDataSource dataSource, Criteria typeSelectionCriteria, TableResolver resolver, FieldMetadata field, Object value) {
        this(dataSource, typeSelectionCriteria, resolver, field, value, -1);
    }

    ManyFieldCriterion(RDBMSDataSource dataSource, Criteria typeSelectionCriteria, TableResolver resolver, FieldMetadata field, Object value, int position) {
        super(StringUtils.EMPTY, new Object[0], new Type[0]);
        this.dataSource = dataSource;
        this.typeCriteria = typeSelectionCriteria;
        this.resolver = resolver;
        this.field = field;
        this.value = value;
        this.position = position;
    }

    @Override
    public String toSqlString(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        if (field instanceof ReferenceFieldMetadata) {
            return field.accept(new ForeignKeySQLGenerator(criteriaQuery, value));
        } else {
            if (value instanceof Object[]) {
                throw new UnsupportedOperationException("Do not support collection search criteria with multiple values.");
            }
            ComplexTypeMetadata type = field.getContainingType();
            if (type.getKeyFields().size() > 1) {
                throw new UnsupportedOperationException("Do not support collection search if main type has a composite key.");
            }
            String containingTypeAlias = criteriaQuery.getSQLAlias(typeCriteria);
            String containingType = resolver.get(type);
            String containingTypeKey = resolver.get(type.getKeyFields().iterator().next());
            StringBuilder builder = new StringBuilder();
            String joinedTableName = resolver.getCollectionTable(field);
            builder.append("(SELECT COUNT(1) FROM ") //$NON-NLS-1$
                    .append(containingType)
                    .append(" INNER JOIN ") //$NON-NLS-1$
                    .append(joinedTableName)
                    .append(" ON ") //$NON-NLS-1$
                    .append(containingType)
                    .append(".") //$NON-NLS-1$
                    .append(containingTypeKey)
                    .append(" = ") //$NON-NLS-1$
                    .append(joinedTableName)
                    .append(".") //$NON-NLS-1$
                    .append(containingTypeKey)
                    .append(" WHERE ") //$NON-NLS-1$
                    .append(joinedTableName)
                    .append(".value = '") //$NON-NLS-1$
                    .append(value)
                    .append("'");
            if (position >= 0) {
                builder.append(" AND ")
                        .append(joinedTableName)
                        .append(".pos = ")
                        .append(position);
            }
            builder.append(" AND ") //$NON-NLS-1$
                    .append(containingType)
                    .append(".") //$NON-NLS-1$
                    .append(containingTypeKey)
                    .append(" = ") //$NON-NLS-1$
                    .append(containingTypeAlias)
                    .append(".") //$NON-NLS-1$
                    .append(containingTypeKey)
                    .append(") > 0"); //$NON-NLS-1$
            return builder.toString();
        }
    }

    private class ForeignKeySQLGenerator extends DefaultMetadataVisitor<String> {

        private final CriteriaQuery criteriaQuery;

        private final StringBuilder query = new StringBuilder();

        private final Object value;

        private int currentIndex = 0;

        public ForeignKeySQLGenerator(CriteriaQuery criteriaQuery, Object value) {
            this.criteriaQuery = criteriaQuery;
            this.value = value;
        }

        private Object getValue() {
            Object o = value;
            if (value instanceof Object[]) {
                o = ((Object[]) value)[currentIndex++];
            }
            // DB2 does not like receiving a Boolean for boolean comparison (Hibernate uses a TINYINT)
            if (dataSource.getDialectName() == RDBMSDataSource.DataSourceDialect.DB2 && o instanceof Boolean) {
                Boolean booleanValue = (Boolean) o;
                if (booleanValue) {
                    o = 1;
                } else {
                    o = 0;
                }
            }
            return o;
        }

        @Override
        public String visit(ReferenceFieldMetadata referenceField) {
            referenceField.getReferencedField().accept(this);
            return query.toString();
        }

        @Override
        public String visit(SimpleTypeFieldMetadata simpleField) {
            return handleField(simpleField);
        }

        @Override
        public String visit(EnumerationFieldMetadata enumField) {
            return handleField(enumField);
        }

        private String handleField(FieldMetadata simpleField) {
            if (query.length() > 0) {
                query.append(" AND "); //$NON-NLS-1$
            }
            query.append("(") //$NON-NLS-1$
                    .append(criteriaQuery.getSQLAlias(typeCriteria))
                    .append(".") //$NON-NLS-1$
                    .append(resolver.get(simpleField))
                    .append(" = "); //$NON-NLS-1$
            boolean isStringType = Types.STRING.equals(MetadataUtils.getSuperConcreteType(simpleField.getType()).getName());
            if (isStringType) { //$NON-NLS-1$
                query.append("'"); //$NON-NLS-1$
            }
            query.append(String.valueOf(getValue()));
            if (isStringType) { //$NON-NLS-1$
                query.append("'");
            }
            query.append(")");
            return query.toString();
        }
    }
}
