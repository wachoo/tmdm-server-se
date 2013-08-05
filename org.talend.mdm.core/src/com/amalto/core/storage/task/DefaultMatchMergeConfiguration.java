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

import com.amalto.core.query.user.Select;
import org.talend.dataquality.matchmerge.MatchAlgorithm;
import org.talend.dataquality.matchmerge.MergeAlgorithm;
import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;
import org.talend.mdm.commmon.metadata.FieldMetadata;
import org.talend.mdm.commmon.metadata.SimpleTypeFieldMetadata;
import org.talend.mdm.commmon.metadata.Types;

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
}
