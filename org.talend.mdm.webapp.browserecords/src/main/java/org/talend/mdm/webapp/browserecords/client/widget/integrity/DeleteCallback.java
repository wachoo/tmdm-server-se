package org.talend.mdm.webapp.browserecords.client.widget.integrity;

import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.shared.FKIntegrityResult;

import java.util.Map;

/**
 *
 */
public class DeleteCallback implements AsyncCallback<Map<ItemBean, FKIntegrityResult>> {

    private final DeleteAction action;

    private final PostDeleteAction postDeleteAction;

    private final BrowseRecordsServiceAsync service;

    public DeleteCallback(DeleteAction action, PostDeleteAction postDeleteAction, BrowseRecordsServiceAsync service) {
        this.action = action;
        this.postDeleteAction = postDeleteAction;
        this.service = service;
    }

    public void onFailure(Throwable caught) {
        Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
    }

    public void onSuccess(Map<ItemBean, FKIntegrityResult> result) {
        DeleteStrategy strategy;
        if (result.size() > 1) {
            strategy = new ListDeleteStrategy(service);
        } else {
            strategy = new SingletonDeleteStrategy(service);
        }

        strategy.delete(result, action);

        // Reload
        postDeleteAction.doAction();
    }

}
