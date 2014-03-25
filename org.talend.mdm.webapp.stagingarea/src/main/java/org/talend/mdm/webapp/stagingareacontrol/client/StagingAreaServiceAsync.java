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
package org.talend.mdm.webapp.stagingareacontrol.client;

import org.talend.mdm.webapp.stagingareacontrol.client.model.ConceptRelationshipModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.StagingAreaConfiguration;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface StagingAreaServiceAsync {

    void getStagingAreaConfig(AsyncCallback<StagingAreaConfiguration> callback);

    void getConceptRelation(AsyncCallback<ConceptRelationshipModel> callback);

}
