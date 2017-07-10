/*
 * Copyright (C) 2006-2016 Talend Inc. - www.talend.com
 * 
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 * 
 * You should have received a copy of the agreement along with this program; if not, write to Talend SA 9 rue Pages
 * 92150 Suresnes, France
 */

package com.amalto.core.storage.hibernate;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;

import com.amalto.core.query.user.Field;
import com.amalto.core.query.user.Paging;
import com.amalto.core.query.user.Select;
import com.amalto.core.query.user.TypedExpression;
import com.amalto.core.query.user.UserQueryBuilder;
import com.amalto.core.storage.CloseableIterator;
import com.amalto.core.storage.Counter.CountKey;
import com.amalto.core.storage.EntityCountUtil;
import com.amalto.core.storage.Storage;
import com.amalto.core.storage.StorageResults;
import com.amalto.core.storage.record.DataRecord;

class HibernateStorageResults implements StorageResults {

    private final int size;

    private final CloseableIterator<DataRecord> iterator;

    private final Storage storage;

    private final Select select;

    public HibernateStorageResults(Storage storage, Select select, CloseableIterator<DataRecord> iterator) {
        this.storage = storage;
        this.select = select;
        this.size = select.getPaging().getLimit();
        this.iterator = iterator;
    }

    @Override
    public int getSize() {
        // Size = Integer.MAX_VALUE means no paging, so results size is then the count.
        if (size == Integer.MAX_VALUE) {
            return getCount();
        }
        return size;
    }

    @Override
    public int getCount() {
        Select countSelect = select.copy();
        // Try to get from cache first
        CountKey countKey = new CountKey(storage, countSelect);
        Integer count = EntityCountUtil.getCount(countKey);
        if (count == null) {
            try {
                List<TypedExpression> selectedFields = countSelect.getSelectedFields();
                selectedFields.clear();
                for (TypedExpression typedExpression : select.getSelectedFields()) {
                    if (typedExpression instanceof Field) {
                        Field field = (Field) typedExpression;
                        if (field.getFieldMetadata() instanceof SimpleTypeFieldMetadata) {
                            SimpleTypeFieldMetadata fileFieldMetadata = (SimpleTypeFieldMetadata) field.getFieldMetadata();
                            FieldMetadata container = fileFieldMetadata.getContainingType().getContainer();
                            if (container != null && container.isMany()) {
                                selectedFields.add(typedExpression);
                            }
                        }
                    }
                }
                selectedFields.add(UserQueryBuilder.count());
                Paging paging = countSelect.getPaging();
                paging.setLimit(1);
                paging.setStart(0);
                countSelect.getOrderBy().clear();
                DataRecord countRecord = storage.fetch(countSelect).iterator().next();
                count = Integer.valueOf(countRecord.get("count").toString()); //$NON-NLS-1$
                // Add count data to cache
                EntityCountUtil.putCount(countKey, count);
                return count;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return count;
    }

    @Override
    public Iterator<DataRecord> iterator() {
        return iterator;
    }

    @Override
    public void close() {
        try {
            iterator.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
