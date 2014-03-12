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

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.rest.RestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaExecutionModel;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.RestDataProxy;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.StagingModelConvertor;
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
import com.google.gwt.xml.client.Node;
import com.google.gwt.xml.client.NodeList;

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
                RestServiceHandler.get().countStagingAreaExecutions(dataContainer, criteria.getStartDate(),
                        new SessionAwareAsyncCallback<Integer>() {

                            @Override
                            public void onSuccess(final Integer total) {
                                RestServiceHandler.get().getStagingAreaExecutionsWithPaging(dataContainer, config.getOffset(),
                                        config.getLimit(), beforeDate, new SessionAwareAsyncCallback<NodeList>() {

                                            @Override
                                            public void onSuccess(NodeList areaExecutionsNodelist) {
                                                final Map<String, StagingAreaExecutionModel> exeIds = new LinkedHashMap<String, StagingAreaExecutionModel>();
                                                if (areaExecutionsNodelist != null) {
                                                    for (int i = 0; i < areaExecutionsNodelist.getLength(); i++) {
                                                        Node node = areaExecutionsNodelist.item(i);
                                                        if (node.getNodeType() == Node.ELEMENT_NODE) {
                                                            exeIds.put(node.getFirstChild().getNodeValue(), null);
                                                        }
                                                    }
                                                }
                                                final int[] counter = new int[1];
                                                for (final String exeId : exeIds.keySet()) {
                                                    RestServiceHandler.get().getStagingAreaExecution(dataContainer, exeId,
                                                            new SessionAwareAsyncCallback<NodeList>() {

                                                                @Override
                                                                public void onSuccess(NodeList areaExecutionNodelist) {
                                                                    StagingAreaExecutionModel stagingAreaExecutionModel = StagingModelConvertor
                                                                            .convertNodeListToStagingAreaExecutionModel(areaExecutionNodelist);
                                                                    exeIds.put(exeId, stagingAreaExecutionModel);
                                                                    counter[0]++;
                                                                    if (counter[0] == exeIds.size()) {
                                                                        BasePagingLoadResult<StagingAreaExecutionModel> pagingResult = new BasePagingLoadResult<StagingAreaExecutionModel>(
                                                                                new ArrayList<StagingAreaExecutionModel>(exeIds
                                                                                        .values()), config.getOffset(), total);
                                                                        callback.onSuccess(pagingResult);
                                                                        searchButton.setEnabled(true);
                                                                    }
                                                                }

                                                                @Override
                                                                protected void doOnFailure(Throwable caught) {
                                                                    counter[0]++;
                                                                    onFailure(caught);
                                                                }
                                                            });
                                                }

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
