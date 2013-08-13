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

package com.amalto.core.storage.task;

import com.amalto.core.metadata.MetadataUtils;
import com.amalto.core.query.user.Select;
import org.talend.dataquality.matchmerge.MatchAlgorithm;
import org.talend.dataquality.matchmerge.MergeAlgorithm;
import org.talend.mdm.commmon.metadata.*;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

class DefaultMatchMergeConfiguration implements MatchMergeConfiguration {
    public MergeAlgorithm[] getMergeAlgorithms(List<FieldMetadata> matchFields) {
        MergeAlgorithm[] merges = new MergeAlgorithm[matchFields.size()];
        int i = 0;
        for (FieldMetadata matchField : matchFields) {
            merges[i++] = MergeAlgorithm.UNIFY;
        }
        return merges;
    }

    public float[] getThresholds(List<FieldMetadata> matchFields) {
        float[] thresholds = new float[matchFields.size()];
        int i = 0;
        for (FieldMetadata matchField : matchFields) {
            thresholds[i++] = 0.8f;
        }
        return thresholds;
    }

    public MatchAlgorithm[] getMatchAlgorithms(List<FieldMetadata> matchFields) {
        MatchAlgorithm[] matchAlgorithm = new MatchAlgorithm[matchFields.size()];
        int i = 0;
        for (FieldMetadata matchField : matchFields) {
            matchAlgorithm[i++] = MatchAlgorithm.LEVENSHTEIN;
        }
        return matchAlgorithm;
    }

    public List<FieldMetadata> getMatchFields(ComplexTypeMetadata type) {
        List<FieldMetadata> fields = new LinkedList<FieldMetadata>();
        for (FieldMetadata field : type.getFields()) {
            if (!field.isKey() && !field.isMany() && field instanceof SimpleTypeFieldMetadata && Types.STRING.equals(field.getType().getName())) {
                fields.add(field);
            }
        }
        return fields;
    }

    @Override
    public Collection<Select> getBlocks(ComplexTypeMetadata type, Select select) {
        return Collections.singletonList(select);
    }

    @Override
    public void check(ComplexTypeMetadata type) {
        if(type.getKeyFields().size() > 1) {
            throw new IllegalArgumentException("Type '" + type.getName() + "' is incorrect because it uses composite keys.");
        }
        FieldMetadata keyField = type.getKeyFields().iterator().next();
        if(!Types.STRING.equals(MetadataUtils.getSuperConcreteType(keyField.getType()).getName())) {
            throw new IllegalArgumentException("Key field '" + keyField.getName()
                    + "' of type '" + keyField.getContainingType().getName()
                    + "' is expected to be a sub type of string");
        }
    }

    @Override
    public boolean include(ComplexTypeMetadata type) {
        if (type.getKeyFields().isEmpty()) {
            return false;
        }
        TypeMetadata keyType = MetadataUtils.getSuperConcreteType(type.getKeyFields().iterator().next().getType());
        return type.getKeyFields().size() == 1 && Types.STRING.equals(keyType.getName());
    }

    @Override
    public MergeAlgorithm getDefaultMergeAlgorithm(FieldMetadata field) {
        TypeMetadata fieldType = MetadataUtils.getSuperConcreteType(field.getType());
        if (Types.STRING.equals(fieldType.getName())) {
            return MergeAlgorithm.CONCAT;
        } else {
            return MergeAlgorithm.MAX;
        }
    }
}
