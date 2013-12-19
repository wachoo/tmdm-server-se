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
package org.talend.mdm.webapp.browserecords.server.service;

import java.io.File;
import java.util.List;

import junit.framework.TestSuite;

import org.powermock.modules.junit3.PowerMockSuite;
import org.talend.mdm.webapp.browserecordsinstaging.server.service.UploadService4Staging;

import com.amalto.webapp.util.webservices.WSPutItemWithReport;

/**
 * created by talend2 on 2013-12-19 Detailled comment
 * 
 */
public class UploadService4StagingTest extends UploadServiceTest {

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + UploadService4StagingTest.class.getSimpleName(),
                UploadService4StagingTest.class);
    }

    @Override
    public void testUploadModel_Polymorphism() throws Exception {
        // Set test parameter value
        clusterName = "UploadTestModel"; //$NON-NLS-1$
        dataModelName = "UploadTestModel"; //$NON-NLS-1$
        String record1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:metadata=\"http://www.talend.com/mdm/metadata\"><id>1</id><info xsi:type=\"SuperInfoType\"><name>1</name></info><metadata:task_id></metadata:task_id><metadata:staging_status>0</metadata:staging_status><metadata:staging_source></metadata:staging_source><metadata:staging_error></metadata:staging_error></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:metadata=\"http://www.talend.com/mdm/metadata\"><id>2</id><info xsi:type=\"SubInfoType\"><name>2</name></info><metadata:task_id></metadata:task_id><metadata:staging_status>0</metadata:staging_status><metadata:staging_source></metadata:staging_source><metadata:staging_error></metadata:staging_error></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:metadata=\"http://www.talend.com/mdm/metadata\"><id>3</id><info xsi:type=\"SuperInfoType\"><name/></info><metadata:task_id></metadata:task_id><metadata:staging_status>0</metadata:staging_status><metadata:staging_source></metadata:staging_source><metadata:staging_error></metadata:staging_error></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:metadata=\"http://www.talend.com/mdm/metadata\"><id>4</id><info xsi:type=\"SubInfoType\"><name>4</name></info><metadata:task_id></metadata:task_id><metadata:staging_status>0</metadata:staging_status><metadata:staging_source></metadata:staging_source><metadata:staging_error></metadata:staging_error></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:metadata=\"http://www.talend.com/mdm/metadata\"><id>5</id><info xsi:type=\"SubInfoType\"><name/></info><metadata:task_id></metadata:task_id><metadata:staging_status>0</metadata:staging_status><metadata:staging_source></metadata:staging_source><metadata:staging_error></metadata:staging_error></UploadTestModel_Polymorphism>"; //$NON-NLS-1$

        // test upload excel file
        fileType = "xls"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("UploadTestModel_Polymorphism-Staging.xls").getFile()); //$NON-NLS-1$
        UploadService service = new UploadService4Staging(entityModel, fileType, headersOnFirstLine, headerVisibleMap,
                inheritanceNodePathList, multipleValueSeparator, seperator, encoding, textDelimiter, clusterName, dataModelName,
                language);
        List<WSPutItemWithReport> wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(record1, wsPutItemWithReportList.get(0).getWsPutItem().getXmlString());
        assertEquals(record2, wsPutItemWithReportList.get(1).getWsPutItem().getXmlString());
        assertEquals(record3, wsPutItemWithReportList.get(2).getWsPutItem().getXmlString());
        assertEquals(record4, wsPutItemWithReportList.get(3).getWsPutItem().getXmlString());
        assertEquals(record5, wsPutItemWithReportList.get(4).getWsPutItem().getXmlString());

        // test upload csv file
        fileType = "csv"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("UploadTestModel_Polymorphism-Staging.csv").getFile()); //$NON-NLS-1$
        service = new UploadService4Staging(entityModel, fileType, headersOnFirstLine, headerVisibleMap, inheritanceNodePathList,
                multipleValueSeparator, seperator, encoding, textDelimiter, clusterName, dataModelName, language);
        wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(record1, wsPutItemWithReportList.get(0).getWsPutItem().getXmlString());
        assertEquals(record2, wsPutItemWithReportList.get(1).getWsPutItem().getXmlString());
        assertEquals(record3, wsPutItemWithReportList.get(2).getWsPutItem().getXmlString());
        assertEquals(record4, wsPutItemWithReportList.get(3).getWsPutItem().getXmlString());
        assertEquals(record5, wsPutItemWithReportList.get(4).getWsPutItem().getXmlString());
    }

}
