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
package org.talend.mdm.webapp.stagingareacontrol.client.controller;

import org.restlet.client.data.MediaType;
import org.restlet.client.ext.xml.DomRepresentation;
import org.restlet.client.representation.Representation;
import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.UserContextModel;
import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.stagingareacontrol.client.model.FilterModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingContainerModel;
import org.talend.mdm.webapp.stagingareacontrol.client.rest.StagingRestServiceHandler;
import org.talend.mdm.webapp.stagingareacontrol.client.view.StagingContainerSummaryView;

import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class StagingContainerSummaryController extends AbstractController {

    private StagingContainerSummaryView view;

    public StagingContainerSummaryController(StagingContainerSummaryView view) {
        setBindingView(view);
        this.view = (StagingContainerSummaryView) bindingView;
    }

    public void refreshView() {
        final UserContextModel ucx = UserContextUtil.getUserContext();
        StagingRestServiceHandler.get().getStagingContainerSummary(ucx.getDataContainer(), ucx.getDataModel(),
                new SessionAwareAsyncCallback<StagingContainerModel>() {

                    @Override
                    public void onSuccess(StagingContainerModel result) {
                        view.refresh(result);
                    }
                });
    }

    private Document buildFilterDocument(FilterModel filterModel) {
        Document filterDoc = XMLParser.createDocument();
        Element root = filterDoc.createElement("config"); //$NON-NLS-1$
        Element startdate = filterDoc.createElement("start-date"); //$NON-NLS-1$
        if (filterModel.getStartDate() != null) {
            startdate.appendChild(filterDoc.createTextNode(String.valueOf(filterModel.getStartDate().getTime())));
        }
        Element enddate = filterDoc.createElement("end-date"); //$NON-NLS-1$
        if (filterModel.getEndDate() != null) {
            enddate.appendChild(filterDoc.createTextNode(String.valueOf(filterModel.getEndDate().getTime())));
        }
        root.appendChild(startdate);
        root.appendChild(enddate);

        Element statuscodes = filterDoc.createElement("status-codes"); //$NON-NLS-1$
        for (String statusCode : filterModel.getStatusCodes()) {
            Element codeEl = filterDoc.createElement("code"); //$NON-NLS-1$
            codeEl.appendChild(filterDoc.createTextNode(statusCode));
            statuscodes.appendChild(codeEl);
        }

        Element concepts = filterDoc.createElement("concepts"); //$NON-NLS-1$
        for (String concept : filterModel.getConcepts()) {
            Element conceptEl = filterDoc.createElement("concept"); //$NON-NLS-1$
            conceptEl.appendChild(filterDoc.createTextNode(concept));
            concepts.appendChild(conceptEl);
        }
        root.appendChild(statuscodes);
        root.appendChild(concepts);
        filterDoc.appendChild(root);
        return filterDoc;
    }

    public void startValidation() {
        startValidation(null);
    }

    public void startValidation(FilterModel filterModel) {
        final UserContextModel ucx = UserContextUtil.getUserContext();
        Representation entity = null;
        if (filterModel != null) {
            entity = new DomRepresentation(MediaType.APPLICATION_XML, buildFilterDocument(filterModel));
        }
        StagingRestServiceHandler.get().runValidationTask(ucx.getDataContainer(), ucx.getDataModel(), entity,
                new SessionAwareAsyncCallback<String>() {

                    @Override
                    public void onSuccess(String result) {
                        ControllerContainer.get().getCurrentValidationController().refreshView();
                    }
                });
    }

    public native void openInvalidRecordToBrowseRecord(int state)/*-{
		if ($wnd.amalto.stagingareabrowse
				&& $wnd.amalto.stagingareabrowse.StagingareaBrowse) {
			$wnd.amalto.stagingareabrowse.StagingareaBrowse.init(state);
		}
    }-*/;

    public void setEnabledStartValidation(boolean enabled) {
        view.setEnabledStartValidation(enabled);
    }

    public boolean isEnabledStartValidation() {
        return view.isEnabledStartValidation();
    }
}
