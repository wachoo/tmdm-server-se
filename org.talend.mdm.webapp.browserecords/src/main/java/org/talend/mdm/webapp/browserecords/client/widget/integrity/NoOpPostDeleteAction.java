package org.talend.mdm.webapp.browserecords.client.widget.integrity;

/**
 *
 */
public class NoOpPostDeleteAction implements PostDeleteAction {

    public static final PostDeleteAction INSTANCE = new NoOpPostDeleteAction();

    private NoOpPostDeleteAction() {
    }

    public void doAction() {
    }
}
