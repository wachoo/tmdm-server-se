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
import org.talend.mdm.webapp.stagingareacontrol.client.rest.StagingRestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.view.PreviousExecutionView;

import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.DataProxy;
import com.extjs.gxt.ui.client.data.LoadEvent;
import com.extjs.gxt.ui.client.data.ModelKeyProvider;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class PreviousExecutionController extends AbstractController {

    private static DataProxy<PagingLoadResult<StagingAreaExecutionModel>> proxy;

    private static BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>> loader;

    private static ListStore<StagingAreaExecutionModel> taskStore;

    private static String dataContainer;

    private PreviousExecutionView view;

    private static Date beforeDate;

    private static Button searchButton;

    private static boolean loadDone = true;

    static {
        proxy = new RestDataProxy<PagingLoadResult<StagingAreaExecutionModel>>() {

            @Override
            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<StagingAreaExecutionModel>> callback) {
                final PagingLoadConfig config = (PagingLoadConfig) loadConfig;
                searchButton.setEnabled(false);
                StagingAreaExecutionModel criteria = new StagingAreaExecutionModel();
                criteria.setStartDate(beforeDate);
                StagingRestServiceHandler.get().countStagingAreaExecutions(dataContainer, criteria,
                        new SessionAwareAsyncCallback<Integer>() {

                            @Override
                            public void onSuccess(final Integer total) {
                                StagingRestServiceHandler.get().getStagingAreaExecutionsWithPaging(dataContainer,
                                        config.getOffset(), config.getLimit(), beforeDate,
                                        new SessionAwareAsyncCallback<List<StagingAreaExecutionModel>>() {

                                            @Override
                                            public void onSuccess(List<StagingAreaExecutionModel> result) {
                                                BasePagingLoadResult<StagingAreaExecutionModel> pagingResult = new BasePagingLoadResult<StagingAreaExecutionModel>(
                                                        result, config.getOffset(), total);
                                                callback.onSuccess(pagingResult);
                                                searchButton.setEnabled(true);
                                            }
                                        });
                            }
                        });
            }
        };

        loader = new BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>>(proxy) {

            @Override
            public boolean load(Object loadConfig) {
                if (loadDone) {
                    loadDone = false;
                    return super.load(loadConfig);
                }
                return false;
            }
        };

        loader.addLoadListener(new LoadListener() {

            @Override
            public void loaderLoad(LoadEvent le) {
                loadDone = true;
            }

            @Override
            public void loaderLoadException(LoadEvent le) {
                loadDone = true;
            }
        });

    }

    public PreviousExecutionController(PreviousExecutionView view) {
        setBindingView(view);
        this.view = (PreviousExecutionView) bindingView;
    }

    public void searchByBeforeDate() {
        PreviousExecutionController.beforeDate = view.getBeforeDate();
        PreviousExecutionController.searchButton = view.getSearchButton();
        loader.load();
    }

    public static BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>> getLoader() {
        return loader;
    }

    public static ListStore<StagingAreaExecutionModel> getClearStore() {
        taskStore = new ListStore<StagingAreaExecutionModel>(loader);
        taskStore.setKeyProvider(new ModelKeyProvider<StagingAreaExecutionModel>() {

            @Override
            public String getKey(StagingAreaExecutionModel model) {
                return model.getId();
            }
        });
        return taskStore;
    }

    public void setDataContainer(String dataContainer) {
        PreviousExecutionController.dataContainer = dataContainer;
        searchByBeforeDate();
    }

}
