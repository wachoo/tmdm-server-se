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

import java.util.Collection;
import java.util.List;

interface MatchMergeConfiguration {
    MergeAlgorithm[] getMergeAlgorithms(List<FieldMetadata> matchFields);

    float[] getThresholds(List<FieldMetadata> matchFields);

    MatchAlgorithm[] getMatchAlgorithms(List<FieldMetadata> matchFields);

    List<FieldMetadata> getMatchFields(ComplexTypeMetadata type);

    Collection<Select> getBlocks(ComplexTypeMetadata type, Select select);
}
