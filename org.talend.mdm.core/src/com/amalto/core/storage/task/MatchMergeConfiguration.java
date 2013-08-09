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

    /**
     * Perform asserts on the type. In case of error/non compliance, a {@link IllegalArgumentException} is expected.
     * @param type An entity type.
     * @throws IllegalArgumentException If the type does not comply to the rules.
     */
    void check(ComplexTypeMetadata type);

    /**
     * @param type An entity type.
     * @return <code>true</code> if configuration includes match merge details for the type, <code>false</code> otherwise.
     */
    boolean include(ComplexTypeMetadata type);

    /**
     * @return The default merge strategy to apply for fields outside match & merge configuration.
     */
    MergeAlgorithm getDefaultMergeAlgorithm();
}
