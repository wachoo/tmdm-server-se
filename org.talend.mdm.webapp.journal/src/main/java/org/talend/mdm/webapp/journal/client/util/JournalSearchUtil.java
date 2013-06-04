// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.journal.client.util;

import org.talend.mdm.webapp.journal.shared.JournalGridModel;
import org.talend.mdm.webapp.journal.shared.JournalParameters;


/**
 * created by yjli on 2013-4-25
 * Detailled comment
 *
 */
public class JournalSearchUtil {
    
    public static JournalParameters buildParameter(JournalGridModel gridModel, String action, boolean isAuth){
        JournalParameters parameter = new JournalParameters();
        parameter.setDataClusterName(gridModel.getDataContainer());
        parameter.setDataModelName(gridModel.getDataModel());
        parameter.setConceptName(gridModel.getEntity());
        parameter.setDate(Long.parseLong(gridModel.getOperationTime()));
        parameter.setRevisionId(gridModel.getRevisionId());
        parameter.setIds(gridModel.getIds());
        parameter.setAction(action);
        String[] id = gridModel.getKey().split("\\."); //$NON-NLS-1$
        parameter.setId(id);
        parameter.setAuth(isAuth);
        parameter.setOperationType(gridModel.getOperationType());
        return parameter;
    }
}
