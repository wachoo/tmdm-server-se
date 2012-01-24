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
package com.amalto.core.objects.configurationinfo.assemble;

import java.util.Collection;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;

import com.amalto.commons.core.datamodel.synchronization.DMUpdateEvent;
import com.amalto.commons.core.datamodel.synchronization.DatamodelChangeNotifier;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocalHome;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class InitDataModelPoolsSubProc extends AssembleSubProc {

    private static final Logger logger = Logger.getLogger(InitDataModelPoolsSubProc.class);

    @Override
    public void run() throws Exception {

        DataModelCtrlLocal dataModelCtrl = null;
        try {
            dataModelCtrl = ((DataModelCtrlLocalHome) new InitialContext().lookup(DataModelCtrlLocalHome.JNDI_NAME)).create();
        } catch (Exception e) {
            org.apache.log4j.Logger.getLogger(this.getClass()).error(e);
        }

        Collection<DataModelPOJOPK> dmpks = dataModelCtrl.getDataModelPKs(".*");
        for (DataModelPOJOPK dataModelPOJOPK : dmpks) {
            // FIXME get data models from HEAD only
            // synchronize with outer agents
            DatamodelChangeNotifier dmUpdateEventNotifer = new DatamodelChangeNotifier();
            dmUpdateEventNotifer.addUpdateMessage(new DMUpdateEvent(dataModelPOJOPK.getUniqueId(), null,
                    DMUpdateEvent.EVENT_TYPE_INIT));
            dmUpdateEventNotifer.sendMessages();
            // logger.info("Initialized datamodel " + dataModelPOJOPK.getUniqueId() + " to DM-Pool ");
        }

    }

}
