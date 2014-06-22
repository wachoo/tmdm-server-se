package com.amalto.core.history;

/**
 * Representation of a MDM document when browsing its history.
 */
public interface Document {
    /**
     * @return Returns the document as string (only the user's document, not the MDM specific XML header).
     */
    String exportToString();

    /**
     * Transforms the document into a new one. Instance passed as parameter of {@link DocumentTransformer} is <code>this</code>,
     * so any modification done to the document in the transformer is performed (i.e. transformation isn't necessarily
     * performed on a copy).
     *
     * @param transformer A {@link DocumentTransformer} implementation.
     * @return A document transformed by the transformer.
     */
    Document transform(DocumentTransformer transformer);

    /**
     * <p>
     * "Restore" this document to the MDM database. This means the current state of this document will become the new
     * current document version.
     * </p>
     * <p>
     * <b>Note:</b>The logged user must be admin to call this method.
     * </p>
     *
     * @throws IllegalStateException If the user is not an admin user.
     */
    void restore();
}
