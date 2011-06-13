package org.talend.mdm.webapp.general.client;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;


public abstract class MdmAsyncCallback<T> implements AsyncCallback<T> {

    @Override
    public void onFailure(Throwable caught) {
        MessageBox.alert("Error", caught.getMessage(), null);
    }

    public abstract void onSuccess(T result);

}
