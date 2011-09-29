package org.talend.mdm.webapp.browserecords.client.widget.integrity;

/**
 * A "no op" implementation to be used to end a responsibility chain.
 */
public class NoOpPostDeleteAction implements PostDeleteAction {

    /**
     * Singleton instance of the class.
     */
    public static final PostDeleteAction INSTANCE = new NoOpPostDeleteAction();

    private NoOpPostDeleteAction() {
    }

    /**
     * This implementation does nothing (and should not do anything).
     */
    public void doAction() {
    }
}
