/*
 * Copyright (C) 2006-2011 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */

package com.amalto.core.history;

import java.util.Date;

/**
 * <p>
 * Represents an action performed on a MDM document (usually there's a 1-to-1 mapping between an update report and an
 * action).
 * </p>
 * <p>
 * Implementations of this interface are able to redo and undo the actions a user has done to a MDM document.
 * </p>
 * <p>
 * Note: the {@link #hashCode()} method might be used to order actions chronologically (in case actions have return the same
 * date in {@link #getDate()}). Implementations of this interface must be careful if they override the {@link Object#hashCode()}
 * method.
 * </p>
 */
public interface Action {

    /**
     * Redo the action to the document.
     *
     * @param document A document that can be modified.
     * @return The document after modification. Please note that actions might be actually performed only
     *         when {@link com.amalto.core.history.MutableDocument#applyChanges()} is called.
     */
    MutableDocument perform(MutableDocument document);

    /**
     * Redo the action to the document.
     *
     * @param document A document that can be modified.
     * @return The document after modification. Please note that actions might be actually performed only
     *         when {@link com.amalto.core.history.MutableDocument#applyChanges()} is called.
     */
    MutableDocument undo(MutableDocument document);

    /**
     * @return Returns the date when this action was performed.
     */
    Date getDate();

    /**
     * @return Returns the source of the modification.
     * @see com.amalto.core.ejb.UpdateReportPOJO#getSource()
     */
    String getSource();

    /**
     * @return Returns the user who did the modification.
     * @see com.amalto.core.ejb.UpdateReportPOJO#getUserName()
     */
    String getUserName();
}
