package org.talend.mdm.webapp.browserecords.client.widget.integrity;

/**
 * This interface wraps any action that should be performed after a delete.
 * @see DeleteStrategy
 */
public interface PostDeleteAction {
    /**
     * Performs any 'action(s)' after delete has been performed by a {@link DeleteStrategy} implementation.
     */
    void doAction();
}
