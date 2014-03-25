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
package org.talend.mdm.webapp.browserecordsinstaging.server.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Row;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.server.exception.UploadException;
import org.talend.mdm.webapp.browserecords.server.service.UploadService;
import org.talend.mdm.webapp.browserecordsinstaging.shared.StagingConstants;

import com.amalto.webapp.util.webservices.WSPutItemWithReport;

/**
 * created by talend2 on 2013-12-17 Detailled comment
 * 
 */
public class UploadService4Staging extends UploadService {

    private Map<String, String> stagingValueMap;

    public UploadService4Staging(EntityModel entityModel, String fileType, boolean headersOnFirstLine,
            Map<String, Boolean> headerVisibleMap, List<String> inheritanceNodePathList, String multipleValueSeparator,
            String seperator, String encoding, char textDelimiter, String clusterName, String dataModelName, String language) {
        super(entityModel, fileType, headersOnFirstLine, headerVisibleMap, inheritanceNodePathList, multipleValueSeparator,
                seperator, encoding, textDelimiter, clusterName, dataModelName, language);
    }

    @Override
    protected String[] readHeader(Row headerRow, String[] headerRecord) throws UploadException {
        stagingValueMap = new HashMap<String, String>();
        return super.readHeader(headerRow, headerRecord);
    }

    @Override
    protected String handleHeader(String headerName, int index) throws UploadException {
        if (StagingConstants.TITLE_STAGING_TASKID.equals(headerName) || StagingConstants.TITLE_STAGING_STATUS.equals(headerName)
                || StagingConstants.TITLE_STAGING_SOURCE.equals(headerName)
                || StagingConstants.TITLE_STAGING_ERROR.equals(headerName)) {
            return headerName;
        } else {
            return super.handleHeader(headerName, index);
        }
    }

    @Override
    protected WSPutItemWithReport buildWSPutItemWithReport(Document document) throws Exception {
        Namespace namespace = new Namespace(StagingConstants.METADATA_NAME, StagingConstants.METADATA_URI);
        Element rootElement = document.getRootElement();
        rootElement.add(namespace);
        Element taskidElement = rootElement.addElement(StagingConstants.METADATA_NAME
                + ":" + StagingConstants.TASKID_ELEMENT_NAME); //$NON-NLS-1$
        taskidElement.setText(stagingValueMap.get(StagingConstants.TASKID_ELEMENT_NAME) != null ? stagingValueMap
                .get(StagingConstants.TASKID_ELEMENT_NAME) : ""); //$NON-NLS-1$
        Element statusElement = rootElement.addElement(StagingConstants.METADATA_NAME
                + ":" + StagingConstants.STATUS_ELEMENT_NAME); //$NON-NLS-1$
        statusElement.setText(stagingValueMap.get(StagingConstants.STATUS_ELEMENT_NAME) != null ? stagingValueMap
                .get(StagingConstants.STATUS_ELEMENT_NAME) : ""); //$NON-NLS-1$
        Element resourceElement = rootElement.addElement(StagingConstants.METADATA_NAME
                + ":" + StagingConstants.SOURCE_ELEMENT_NAME); //$NON-NLS-1$
        resourceElement.setText(stagingValueMap.get(StagingConstants.SOURCE_ELEMENT_NAME) != null ? stagingValueMap
                .get(StagingConstants.SOURCE_ELEMENT_NAME) : ""); //$NON-NLS-1$
        Element errorElement = rootElement.addElement(StagingConstants.METADATA_NAME + ":" + StagingConstants.ERROR_ELEMENT_NAME); //$NON-NLS-1$
        errorElement.setText(stagingValueMap.get(StagingConstants.ERROR_ELEMENT_NAME) != null ? stagingValueMap
                .get(StagingConstants.ERROR_ELEMENT_NAME) : ""); //$NON-NLS-1$
        stagingValueMap.clear();
        return super.buildWSPutItemWithReport(document);
    }

    @Override
    protected void fillFieldValue(Element currentElement, String fieldPath, String fieldValue, Row row, String[] record)
            throws Exception {
        if (StagingConstants.TITLE_STAGING_TASKID.equals(fieldPath)) {
            stagingValueMap.put(StagingConstants.TASKID_ELEMENT_NAME, fieldValue);
        } else if (StagingConstants.TITLE_STAGING_STATUS.equals(fieldPath)) {
            stagingValueMap.put(StagingConstants.STATUS_ELEMENT_NAME, fieldValue);
        } else if (StagingConstants.TITLE_STAGING_SOURCE.equals(fieldPath)) {
            stagingValueMap.put(StagingConstants.SOURCE_ELEMENT_NAME, fieldValue);
        } else if (StagingConstants.TITLE_STAGING_ERROR.equals(fieldPath)) {
            stagingValueMap.put(StagingConstants.ERROR_ELEMENT_NAME, fieldValue);
        } else {
            super.fillFieldValue(currentElement, fieldPath, fieldValue, row, record);
        }
    }

}
