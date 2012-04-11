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

package com.amalto.core.save;

import com.amalto.core.history.Action;
import com.amalto.core.history.MutableDocument;
import com.amalto.core.metadata.ComplexTypeMetadata;
import com.amalto.core.save.context.DocumentSaver;

import java.util.List;

public interface DocumentSaverContext {

    DocumentSaver createSaver();

    /**
     * @return The document as it is present (or not) in database.
     */
    MutableDocument getDatabaseDocument();

    /**
     * @return Document to be used for XML schema validation (doesn't contain MDM internal technical annotations).
     */
    MutableDocument getDatabaseValidationDocument();

    /**
     * @return Document provided by user for save
     */
    MutableDocument getUserDocument();

    /**
     * Changes document provided by user
     * @param document New user provided document
     */
    void setUserDocument(MutableDocument document);

    /**
     * @return List of actions to be performed to the database
     */
    List<Action> getActions();

    /**
     * Set actions performed by the user.
     * @param actions A list of {@link Action} to be performed.
     */
    void setActions(List<Action> actions);

    /**
     * @return {@link ComplexTypeMetadata} of the entity being saved.
     */
    ComplexTypeMetadata getType();

    /**
     * @return Id of the document being saved.
     */
    String[] getId();

    /**
     * Set the id of the soon-to-be-saved document.
     * @param id Id of the document.
     */
    void setId(String[] id);

    String getDataCluster();

    String getDataModelName();

    String getRevisionID();

    void setDatabaseDocument(MutableDocument databaseDocument);

    void setDatabaseValidationDocument(MutableDocument databaseValidationDocument);

    void setRevisionId(String revisionID);

    void setType(ComplexTypeMetadata type);

}
