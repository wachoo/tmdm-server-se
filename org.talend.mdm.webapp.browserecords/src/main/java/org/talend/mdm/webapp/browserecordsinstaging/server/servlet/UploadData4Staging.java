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
package org.talend.mdm.webapp.browserecordsinstaging.server.servlet;

import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.browserecords.server.service.UploadService;
import org.talend.mdm.webapp.browserecords.server.servlet.UploadData;
import org.talend.mdm.webapp.browserecordsinstaging.server.service.UploadService4Staging;

/**
 * created by yjli on 2013-10-24 Detailled comment
 * 
 */
public class UploadData4Staging extends UploadData {

    private static final long serialVersionUID = -4815767737453028749L;

    @Override
    protected UploadService generateUploadService(String concept, String fileType, boolean headersOnFirstLine,
            Map<String, Boolean> headerVisibleMap, List<String> inheritanceNodePathList, String multipleValueSeparator,
            String seperator, String encoding, char textDelimiter, String clusterName, String dataModelName, String language)
            throws Exception {
        return new UploadService4Staging(getEntityModel(concept), fileType, headersOnFirstLine, headerVisibleMap,
                inheritanceNodePathList, multipleValueSeparator, seperator, encoding, textDelimiter, clusterName, dataModelName,
                language);
    }

}
