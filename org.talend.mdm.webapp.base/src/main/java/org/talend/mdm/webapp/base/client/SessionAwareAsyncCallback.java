// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.client;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.util.Format;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;

public abstract class SessionAwareAsyncCallback<T> implements AsyncCallback<T> {

    public final void onFailure(Throwable caught) {
        if (Log.isErrorEnabled()) {
            Log.error(caught.toString());
        }
        doOnFailure(caught);
    }

    protected void doOnFailure(Throwable caught) {
        String errorMsg = caught.getLocalizedMessage();
        if (errorMsg == null || "".equals(errorMsg)) { //$NON-NLS-1$
            if (Log.isDebugEnabled()) {
                errorMsg = caught.toString(); // for debugging purpose
            } else {
                errorMsg = BaseMessagesFactory.getMessages().unknown_error();
            }
            // see TMDM-TMDM-4411 if call async method,StatusCodeException will be thrown when mdmserver down
        } else if (caught instanceof com.google.gwt.user.client.rpc.StatusCodeException) {
            if (Log.isDebugEnabled()) {
                errorMsg = caught.toString(); // for debugging purpose
            } else {
                errorMsg = BaseMessagesFactory.getMessages().unknown_error();
            }
        }
        errorMsg = Format.htmlEncode(errorMsg);
        MessageBox.alert(BaseMessagesFactory.getMessages().error_title(), errorMsg, null);
    }
}
