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

import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.metadata.FieldMetadata;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;

public class ManyFieldProjection extends SimpleProjection {

    private final FieldMetadata field;

    private final TableResolver resolver;

    public ManyFieldProjection(FieldMetadata field, TableResolver resolver) {
        this.field = field;
        this.resolver = resolver;
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
        ComplexTypeMetadata containingType = field.getContainingType();
        String containerTable = MappingGenerator.formatSQLName(containingType.getName(), resolver.getNameMaxLength());
        String collectionTable = MappingGenerator.formatSQLName(containerTable + '_' + field.getName(), resolver.getNameMaxLength());
        String containerIdColumn = MappingGenerator.formatSQLName(containingType.getKeyFields().get(0).getName(), resolver.getNameMaxLength());
        StringBuilder sqlFragment = new StringBuilder();
        sqlFragment.append("(select group_concat(") //$NON-NLS-1$
                .append(collectionTable)
                .append(".value separator ',') FROM ").append(containerTable); //$NON-NLS-1$
        for (FieldMetadata currentKey : containingType.getKeyFields()) {
            String keyName = currentKey.getName();
            sqlFragment.append(" INNER JOIN ") //$NON-NLS-1$
                    .append(collectionTable)
                    .append(" on ") //$NON-NLS-1$
                    .append(containerTable).append('.').append(keyName)
                    .append(" = ") //$NON-NLS-1$
                    .append(collectionTable).append('.').append(keyName);
        }
        sqlFragment.append(" WHERE ") //$NON-NLS-1$
                .append(containerTable)
                .append('.')
                .append(containerIdColumn)
                .append(" = ") //$NON-NLS-1$
                .append(criteriaQuery.getSQLAlias(criteria))
                .append('.')
                .append(containerIdColumn).append(") as y").append(position).append('_'); //$NON-NLS-1$
        return sqlFragment.toString();
    }

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new Type[]{new StringType()};
    }
}
