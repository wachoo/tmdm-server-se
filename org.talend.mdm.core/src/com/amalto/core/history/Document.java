package com.amalto.core.history;

import org.talend.mdm.commmon.metadata.ComplexTypeMetadata;

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

    /**
     * @return The type metadata information for this Document. This method should <b>NEVER</b> return <code>null</code>.
     */
    ComplexTypeMetadata getType();

    /**
     * @return The data model name in which type information is defined. A call to {@link com.amalto.core.server.MetadataRepositoryAdmin#get(String)}
     * <b>MUST</b> return a non-null value.
     * @see com.amalto.core.server.MetadataRepositoryAdmin
     */
    String getDataModelName();

    /**
     * @return Returns the revision name (or <code>null</code> if HEAD revision).
     */
    String getRevision();
}
