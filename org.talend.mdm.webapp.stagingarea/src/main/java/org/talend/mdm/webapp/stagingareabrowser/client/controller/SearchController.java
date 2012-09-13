// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.stagingareabrowser.client.controller;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.util.UrlUtil;
import org.talend.mdm.webapp.stagingareabrowser.client.StagingareaBrowse;
import org.talend.mdm.webapp.stagingareabrowser.client.view.SearchView;

import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ListLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.store.ListStore;
import com.google.gwt.user.client.rpc.AsyncCallback;


public class SearchController extends AbstractController {

    private static RpcProxy<List<BaseModel>> entityProxy;

    private static ListLoader<ListLoadResult<BaseModel>> entityloader;

    private static ListStore<BaseModel> store;

    private SearchView view;

    public SearchController(SearchView view) {
        setBindingView(view);
        this.view = (SearchView) bindingView;
    }

    static {
        entityProxy = new RpcProxy<List<BaseModel>>() {

            @Override
            public void load(Object loadConfig, final AsyncCallback<List<BaseModel>> callback) {
                StagingareaBrowse.service.getConcepts(UrlUtil.getLanguage(), new SessionAwareAsyncCallback<List<BaseModel>>() {

                    public void onSuccess(List<BaseModel> result) {
                        callback.onSuccess(result);
                    }
                });
            }
        };
        entityloader = new BaseListLoader<ListLoadResult<BaseModel>>(entityProxy);
        store = new ListStore<BaseModel>(entityloader);
    }

    public static ListLoader<ListLoadResult<BaseModel>> getLoader() {
        return entityloader;
    }

    public static ListStore<BaseModel> getStore() {
        return store;
    }
}
