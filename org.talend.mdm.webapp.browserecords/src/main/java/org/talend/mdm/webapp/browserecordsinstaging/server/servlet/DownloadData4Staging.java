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

import java.util.ArrayList;
import java.util.List;

import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.dom4j.Document;
import org.talend.mdm.webapp.base.server.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.server.servlet.DownloadData;
import org.talend.mdm.webapp.browserecordsinstaging.shared.StagingConstants;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSStringArray;
import com.amalto.webapp.util.webservices.WSXPathsSearch;


/**
 * created by yjli on 2013-10-22
 * Detailled comment
 *
 */
public class DownloadData4Staging extends DownloadData{

    private static final long serialVersionUID = 6201136236958671070L;
    private final String STAGING_SUFFIX_NAME = "-Staging";     //$NON-NLS-1$
    private final String LEFT_PARENTHESIS = "("; //$NON-NLS-1$
    private final String RIGHT_PARENTHESIS = ")"; //$NON-NLS-1$
    private final String SPACE = " "; //$NON-NLS-1$    
    
    private WSStringArray stagingPathArray;
    
    private WSXPathsSearch wsXPathsSearch;
    
    @Override
    protected void fillSheet(HSSFSheet sheet) throws Exception {
        List<String> stagingPathList = new ArrayList<String>();
        stagingPathList.add(concept + StagingConstants.STAGING_TASKID);
        stagingPathList.add(concept + StagingConstants.STAGING_STATUS);
        stagingPathList.add(concept + StagingConstants.STAGING_SOURCE);
        stagingPathList.add(concept + StagingConstants.STAGING_ERROR);
        stagingPathArray = new WSStringArray(stagingPathList.toArray(new String[stagingPathList.size()]));
        super.fillSheet(sheet);
    }
    
    @Override
    protected void fillHeader(HSSFRow row) {
        super.fillHeader(row);
        HSSFCell matchGroupCell = row.createCell(xpathArray.length);
        matchGroupCell.setCellValue(StagingConstants.TITLE_STAGING_TASKID);
        matchGroupCell.setCellStyle(cs);
        HSSFCell statusCell = row.createCell(xpathArray.length + 1);
        statusCell.setCellValue(StagingConstants.TITLE_STAGING_STATUS);
        statusCell.setCellStyle(cs);
        HSSFCell sourceCell = row.createCell(xpathArray.length + 2);
        sourceCell.setCellValue(StagingConstants.TITLE_STAGING_SOURCE);
        sourceCell.setCellStyle(cs);
        HSSFCell errorCell = row.createCell(xpathArray.length + 3);
        errorCell.setCellValue(StagingConstants.TITLE_STAGING_ERROR);
        errorCell.setCellStyle(cs);
    }

    @Override
    protected void fillRow(HSSFRow row, Document document) throws Exception {        
        super.fillRow(row, document);        
        wsXPathsSearch = new WSXPathsSearch(new WSDataClusterPK(dataCluster), null, stagingPathArray, CommonUtil.buildWhereItem(buildStagingCriteria(document)), -1,
                0, maxCount, null, null, false);
        String[] stagingResult = CommonUtil.getPort().xPathsSearch(wsXPathsSearch).getStrings();
        for (int i = 0; i < stagingResult.length; i++) {
            org.w3c.dom.Document stagingDocument = Util.parse(stagingResult[i]);
            row.createCell(columnIndex).setCellValue(Util.getFirstTextNode(stagingDocument, "result/" + StagingConstants.TASKID_ELEMENT_NAME)); //$NON-NLS-1$
            row.createCell(columnIndex + 1).setCellValue(Util.getFirstTextNode(stagingDocument, "result/" + StagingConstants.STATUS_ELEMENT_NAME)); //$NON-NLS-1$
            row.createCell(columnIndex + 2).setCellValue(Util.getFirstTextNode(stagingDocument, "result/" + StagingConstants.SOURCE_ELEMENT_NAME)); //$NON-NLS-1$
            row.createCell(columnIndex + 3).setCellValue(Util.getFirstTextNode(stagingDocument, "result/" + StagingConstants.ERROR_ELEMENT_NAME)); //$NON-NLS-1$
        }        
    }
    
    private String buildStagingCriteria(Document document) {
        String[] keyPathArray = entity.getKeys();
        StringBuilder criteria = new StringBuilder();
        if (keyPathArray.length == 1) {
            criteria.append(buildCondition(keyPathArray[0], getNodeValue(document, keyPathArray[0])));
        } else {
            criteria.append(LEFT_PARENTHESIS);
            for (String keyPath : keyPathArray) {
                if (criteria.length() > 1) {
                    criteria.append(SPACE);
                    criteria.append(CommonUtil.AND);
                    criteria.append(SPACE);
                }
                criteria.append(LEFT_PARENTHESIS);
                criteria.append(keyPath);
                criteria.append(SPACE);
                criteria.append(CommonUtil.EQUALS);
                criteria.append(SPACE);
                criteria.append(getNodeValue(document, keyPath));
                criteria.append(RIGHT_PARENTHESIS);
            }
            criteria.append(RIGHT_PARENTHESIS);
        }
        return criteria.toString();
        
        
    }
    
    private String buildCondition(String path,String value) {
        return path + SPACE + CommonUtil.EQUALS + SPACE + value;
    }

    @Override
    protected void generateFileName(String name) {
        fileName = name + STAGING_SUFFIX_NAME + DOWNLOADFILE_EXTEND_NAME;
    }
}
