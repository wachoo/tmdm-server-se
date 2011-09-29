package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;

import java.util.Map;

/**
 * A generic {@link AsyncCallback} implementation to handle all deletes in items browser and all specific actions to
 * performed regarding deletes and FK integrity checks.
 */
public class DeleteCallback implements AsyncCallback<Map<ItemBean, FKIntegrityResult>> {

    private final DeleteAction action;

    private final PostDeleteAction postDeleteAction;

    private final BrowseRecordsServiceAsync service;

    /**
     * @param action           The {@link DeleteAction} to perform.
     * @param postDeleteAction The {@link PostDeleteAction} to perform.
     * @param service          A {@link BrowseRecordsServiceAsync} instance to be used for communication with MDM server.
     */
    public DeleteCallback(DeleteAction action, PostDeleteAction postDeleteAction, BrowseRecordsServiceAsync service) {
        this.action = action;
        this.postDeleteAction = postDeleteAction;
        this.service = service;
    }

    public void onFailure(Throwable caught) {
        Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
    }

    /**
     * @param result A {@link Map} of items to be deleted with information on what FK integrity policy should be applied.
     */
    public void onSuccess(Map<ItemBean, FKIntegrityResult> result) {
        if (!result.isEmpty()) { // If empty, do nothing
            DeleteStrategy strategy;
            if (result.size() > 1) {
                strategy = new ListDeleteStrategy(service);
            } else {
                strategy = new SingletonDeleteStrategy(service);
            }

            strategy.delete(result, action, postDeleteAction);
        }
    }

}
