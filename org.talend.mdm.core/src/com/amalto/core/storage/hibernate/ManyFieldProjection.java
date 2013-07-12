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

import com.amalto.core.storage.datasource.RDBMSDataSource;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.criterion.CriteriaQuery;
import org.hibernate.criterion.SimpleProjection;
import org.hibernate.type.StringType;
import org.hibernate.type.Type;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;

class ManyFieldProjection extends SimpleProjection {

    private final String alias;

    private final FieldMetadata field;

    private final TableResolver resolver;

    private final RDBMSDataSource dataSource;

    ManyFieldProjection(String alias, FieldMetadata field, TableResolver resolver, RDBMSDataSource dataSource) {
        this.alias = alias;
        this.field = field;
        this.resolver = resolver;
        this.dataSource = dataSource;
    }

    @Override
    public String toSqlString(Criteria criteria, int position, CriteriaQuery criteriaQuery) throws HibernateException {
        Criteria subCriteria = StandardQueryHandler.findCriteria(criteria, alias);
        ComplexTypeMetadata containingType = field.getContainingType();
        String containerTable = MappingGenerator.formatSQLName(resolver.get(containingType), resolver.getNameMaxLength());
        String collectionTable = MappingGenerator.formatSQLName((containerTable + '_' + field.getName()).toUpperCase(), resolver.getNameMaxLength());
        String containerIdColumn = MappingGenerator.formatSQLName(containingType.getKeyFields().iterator().next().getName(), resolver.getNameMaxLength());
        StringBuilder sqlFragment = new StringBuilder();
        // SQL Server doesn't support the group_concat function
        if (dataSource.getDialectName() != RDBMSDataSource.DataSourceDialect.SQL_SERVER) {
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
                    .append(criteriaQuery.getSQLAlias(subCriteria))
                    .append('.')
                    .append(containerIdColumn).append(") as y").append(position).append('_'); //$NON-NLS-1$
        } else {
            // SQL Server doesn't support the group_concat function -> returns (value + '...')
            sqlFragment.append("(select ") //$NON-NLS-1$
                    .append(collectionTable)
                    .append(".value + \'...\' FROM ").append(containerTable); //$NON-NLS-1$
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
                    .append(criteriaQuery.getSQLAlias(subCriteria))
                    .append('.')
                    .append(containerIdColumn).append(" and pos=0) as y").append(position).append('_'); //$NON-NLS-1$
        }
        return sqlFragment.toString();
    }

    @Override
    public Type[] getTypes(Criteria criteria, CriteriaQuery criteriaQuery) throws HibernateException {
        return new Type[]{new StringType()};
    }
}
