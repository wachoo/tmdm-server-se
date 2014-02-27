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

package com.amalto.core.history;

/**
 *
 */
public interface DocumentFactory {

    /**
     * Creates a modifiable version of a MDM document.
     *
     * @param dataClusterName The data cluster name where the MDM document is.
     * @param dataModelName data model name of the document.
     * @param conceptName Concept name of the document.
     * @param id Id of the MDM document.
     * @param revisionId Revision id for the document.
     * @return A {@link MutableDocument} that can be modified with{@link Action}
     */
    MutableDocument create(String dataClusterName, String dataModelName, String conceptName, String[] id, String revisionId);

}
