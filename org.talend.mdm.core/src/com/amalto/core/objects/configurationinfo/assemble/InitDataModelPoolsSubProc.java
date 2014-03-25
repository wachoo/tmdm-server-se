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
package com.amalto.core.objects.configurationinfo.assemble;

import java.util.Collection;

import com.amalto.commons.core.datamodel.synchronization.DataModelChangeNotifier;
import com.amalto.core.server.DataModel;
import com.amalto.core.util.Util;

import com.amalto.commons.core.datamodel.synchronization.DMUpdateEvent;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;

public class InitDataModelPoolsSubProc extends AssembleSubProc {

    @Override
    public void run() throws Exception {
        DataModel dataModelCtrl;
        try {
            dataModelCtrl = Util.getDataModelCtrlLocal();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        Collection<DataModelPOJOPK> dmpks = dataModelCtrl.getDataModelPKs(".*");
        for (DataModelPOJOPK dataModelPOJOPK : dmpks) {
            // FIXME get data models from HEAD only
            // synchronize with outer agents
            DataModelChangeNotifier dmUpdateEventNotifer = new DataModelChangeNotifier();
            DMUpdateEvent event = new DMUpdateEvent(dataModelPOJOPK.getUniqueId(), null, DMUpdateEvent.EVENT_TYPE_INIT);
            dmUpdateEventNotifer.addUpdateMessage(event);
            dmUpdateEventNotifer.sendMessages();
        }

    }

}
