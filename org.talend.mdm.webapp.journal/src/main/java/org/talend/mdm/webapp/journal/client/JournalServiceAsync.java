// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.journal.client;

import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;
import org.talend.mdm.webapp.journal.shared.JournalSearchCriteria;
import org.talend.mdm.webapp.journal.shared.JournalTreeModel;

import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface JournalServiceAsync {

    void getJournalList(JournalSearchCriteria criteria, PagingLoadConfig load,
            AsyncCallback<PagingLoadResult<JournalGridModel>> callback);

    void getDetailTreeModel(String ids, AsyncCallback<JournalTreeModel> callback);

    void getComparisionTree(JournalParameters parameter, AsyncCallback<JournalTreeModel> callback);

    void isEnterpriseVersion(AsyncCallback<Boolean> callback);

    void restoreRecord(JournalParameters parameter, AsyncCallback<Boolean> callback);

    void checkDCAndDM(String dataContainer, String dataModel, AsyncCallback<Boolean> callback);
}