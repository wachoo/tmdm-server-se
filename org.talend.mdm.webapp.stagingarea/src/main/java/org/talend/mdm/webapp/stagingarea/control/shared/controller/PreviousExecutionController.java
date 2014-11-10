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
package org.talend.mdm.webapp.stagingarea.control.shared.controller;

import com.extjs.gxt.ui.client.data.*;
import com.extjs.gxt.ui.client.event.LoadListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.google.gwt.user.client.rpc.AsyncCallback;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.stagingarea.control.client.rest.RestDataProxy;
import org.talend.mdm.webapp.stagingarea.control.client.rest.StagingRestServiceHandler;
import org.talend.mdm.webapp.stagingarea.control.shared.model.StagingAreaExecutionModel;

import java.util.Date;
import java.util.List;

public class PreviousExecutionController {

    private static DataProxy<PagingLoadResult<StagingAreaExecutionModel>>        proxy;

    private BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>> loader;

    private String                                                        dataContainer;

    private Date                                                          beforeDate;

    private boolean                                                       loadDone = true;

    public void setBeforeDate(Date beforeDate) {
        this.beforeDate = beforeDate;
    }

    public void search() {
        loader.load();
    }

    public BasePagingLoader<PagingLoadResult<StagingAreaExecutionModel>> getLoader() {
        return loader;
    }

    public ListStore<StagingAreaExecutionModel> getClearStore() {
        ListStore<StagingAreaExecutionModel> taskStore = new ListStore<StagingAreaExecutionModel>(loader);
        taskStore.setKeyProvider(new ModelKeyProvider<StagingAreaExecutionModel>() {

            @Override
            public String getKey(StagingAreaExecutionModel model) {
                return model.getId();
            }
        });
        return taskStore;
    }

    public void setDataContainer(final String dataContainer) {
        proxy = new RestDataProxy<PagingLoadResult<StagingAreaExecutionModel>>() {

            @Override
            protected void load(Object loadConfig, final AsyncCallback<PagingLoadResult<StagingAreaExecutionModel>> callback) {
                final PagingLoadConfig config = (PagingLoadConfig) loadConfig;
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
        search();
    }

}
