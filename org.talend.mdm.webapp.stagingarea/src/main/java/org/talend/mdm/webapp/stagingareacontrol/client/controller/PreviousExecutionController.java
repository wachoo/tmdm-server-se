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
package org.talend.mdm.webapp.stagingareacontrol.client.controller;

import java.util.Date;
import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.RestDataProxy;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.view.PreviousExecutionView;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreviousExecutionController extends AbstractController {

    private static DataProxy<PagingLoadResult<StagingAreaExecutionModel>> proxy;

    private static BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>> loader;

    private static ListStore<StagingAreaExecutionModel> taskStore;

    private static String dataContainer;

    private PreviousExecutionView view;

    private static Date beforeDate;

    static {
        proxy = new RestDataProxy<PagingLoadResult<StagingAreaExecutionModel>>() {

            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<StagingAreaExecutionModel>> callback) {
                final PagingLoadConfig config = (PagingLoadConfig) loadConfig;
                RestServiceHandler.get().countStagingAreaExecutions(dataContainer, null,
                        new SessionAwareAsyncCallback<Integer>() {

                            public void onSuccess(final Integer total) {
                                RestServiceHandler.get().getStagingAreaExecutionsWithPaging(dataContainer,
                                        config.getOffset() + 1, config.getLimit(), beforeDate,
                                        new SessionAwareAsyncCallback<List<StagingAreaExecutionModel>>() {
                                            public void onSuccess(List<StagingAreaExecutionModel> result) {
                                                BasePagingLoadResult<StagingAreaExecutionModel> pagingResult = new BasePagingLoadResult<StagingAreaExecutionModel>(
                                                        result, config.getOffset(), total);
                                                callback.onSuccess(pagingResult);
                                            }
                                        });
                            }
                        });
            }
        };

        loader = new BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>>(proxy);
        loader.setRemoteSort(true);


    }
    
    public PreviousExecutionController(PreviousExecutionView view) {
        setBindingView(view);
        this.view = (PreviousExecutionView) bindingView;
    }

    public void searchByBeforeDate() {
        PreviousExecutionController.beforeDate = view.getBeforeDate();
        loader.load();
    }

    public static BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>> getLoader() {
        return loader;
    }

    public static ListStore<StagingAreaExecutionModel> getClearStore() {
        taskStore = new ListStore<StagingAreaExecutionModel>(loader);
        taskStore.setKeyProvider(new ModelKeyProvider<StagingAreaExecutionModel>() {

            public String getKey(StagingAreaExecutionModel model) {
                return model.getId();
            }
        });
        return taskStore;
    }

    public void setDataContainer(String dataContainer) {
        this.dataContainer = dataContainer;
        searchByBeforeDate();
    }

}
