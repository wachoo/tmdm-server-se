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

import com.amalto.core.metadata.MetadataUtils;
import org.talend.mdm.commmon.metadata.*;
import org.apache.commons.lang.StringUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SQLCriterion;
import org.hibernate.type.Type;

class ManyFieldCriterion extends SQLCriterion {

    private final Criteria typeCriteria;

    private final FieldMetadata field;

    private final Object value;

    private final int position;

    private final TableResolver resolver;

    ManyFieldCriterion(Criteria typeSelectionCriteria, TableResolver resolver, FieldMetadata field, Object value) {
        this(typeSelectionCriteria, resolver, field, value, -1);
    }

    ManyFieldCriterion(Criteria typeSelectionCriteria, TableResolver resolver, FieldMetadata field, Object value, int position) {
        super(StringUtils.EMPTY, new Object[0], new Type[0]);
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
            String containingType = type.getName().toUpperCase();
            String fieldName = field.getName().toUpperCase();
            String containingTypeKey = type.getKeyFields().iterator().next().getName();
            StringBuilder builder = new StringBuilder();
            String joinedTableName = MappingGenerator.formatSQLName(containingType + '_' + fieldName,
                    resolver.getNameMaxLength());
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
            if (value instanceof Object[]) {
                return ((Object[]) value)[currentIndex++];
            } else {
                return value;
            }
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
                    .append(criteriaQuery.getSQLAlias(typeCriteria)).append(".") //$NON-NLS-1$
                    .append(simpleField.getName()).append(" = "); //$NON-NLS-1$
            String name = MetadataUtils.getSuperConcreteType(simpleField.getType()).getName();
            boolean isStringType = Types.STRING.equals(name);
            // TMDM-7226: Consider date / time types as strings (for quotes).
            if (!isStringType) {
                for (String dateType : Types.DATES) {
                    isStringType = dateType.equals(name);
                    if (isStringType) {
                        break;
                    }
                }
            }
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
