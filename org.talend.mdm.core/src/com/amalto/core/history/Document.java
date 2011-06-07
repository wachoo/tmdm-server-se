package com.amalto.core.history;

/**
 * Representation of a MDM document when browsing its history.
 */
public interface Document {
    /**
     * @return Returns the document as string (only the user's document, not the MDM specific XML header).
     */
    String getAsString();

    /**
     * @return true is the document has just been created, false otherwise.
     */
    boolean isCreated();

    /**
     * @return true is the document has just been deleted, false otherwise.
     */
    boolean isDeleted();

    /**
     * <p>
     * Restore this document to the MDM database.
     * </p>
     * <p>
     * <b>Note:</b>The logged user must be admin to call this method.
     * </p>
     * @throws IllegalStateException If the user is not an admin user.
     */
    void restore();
}
