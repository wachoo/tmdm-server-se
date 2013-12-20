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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.DataModelHelper;
import org.talend.mdm.webapp.browserecords.server.bizhelpers.SchemaMockAgent;
import org.xml.sax.SAXException;

import com.amalto.core.util.Util;
import com.amalto.webapp.util.webservices.WSPutItemWithReport;

/**
 * created by talend2 on 2013-12-18 Detailled comment
 * 
 */
@PrepareForTest({ Util.class })
public class UploadServiceTest extends TestCase {

    protected String clusterName = null;

    protected String dataModelName = null;

    protected EntityModel entityModel = null;

    protected File file = null;

    protected String fileType = null;

    protected boolean headersOnFirstLine = false;

    protected Map<String, Boolean> headerVisibleMap = null;

    protected List<String> inheritanceNodePathList = null;

    protected String language = "en"; //$NON-NLS-1$

    protected String multipleValueSeparator = null;

    protected String seperator = null;

    protected String encoding = "utf-8"; //$NON-NLS-1$

    protected char textDelimiter = '\"';

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        headersOnFirstLine = true;
        headerVisibleMap = new HashMap<String, Boolean>();
        headerVisibleMap.put("UploadTestModel_Polymorphism/id", true); //$NON-NLS-1$
        headerVisibleMap.put("UploadTestModel_Polymorphism/info/@xsi:type", true); //$NON-NLS-1$
        inheritanceNodePathList = new LinkedList<String>();
        inheritanceNodePathList.add("UploadTestModel_Polymorphism/info"); //$NON-NLS-1$
        multipleValueSeparator = "|"; //$NON-NLS-1$
        String[] keys = { "UploadTestModel_Polymorphism/id" }; //$NON-NLS-1$
        entityModel = getEntityModel("UploadTestModel.xsd", "UploadTestModel", "UploadTestModel_Polymorphism", keys); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
    }

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + UploadServiceTest.class.getSimpleName(), UploadServiceTest.class);
    }

    public void testUploadModel_Polymorphism() throws Exception {
        // set test parameter value
        clusterName = "UploadTestModel"; //$NON-NLS-1$
        dataModelName = "UploadTestModel"; //$NON-NLS-1$
        String record1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>1</id><info xsi:type=\"SuperInfoType\"><name>1</name></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>2</id><info xsi:type=\"SubInfoType\"><name>2</name></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>3</id><info xsi:type=\"SuperInfoType\"><name/></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>4</id><info xsi:type=\"SubInfoType\"><name>4</name></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>5</id><info xsi:type=\"SubInfoType\"><name/></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$

        // test upload excel file
        fileType = "xls"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("UploadTestModel_Polymorphism.xls").getFile()); //$NON-NLS-1$
        UploadService service = new UploadService(entityModel, fileType, headersOnFirstLine, headerVisibleMap,
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
        file = new File(this.getClass().getResource("UploadTestModel_Polymorphism.csv").getFile()); //$NON-NLS-1$
        service = new UploadService(entityModel, fileType, headersOnFirstLine, headerVisibleMap, inheritanceNodePathList,
                multipleValueSeparator, seperator, encoding, textDelimiter, clusterName, dataModelName, language);
        wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(record1, wsPutItemWithReportList.get(0).getWsPutItem().getXmlString());
        assertEquals(record2, wsPutItemWithReportList.get(1).getWsPutItem().getXmlString());
        assertEquals(record3, wsPutItemWithReportList.get(2).getWsPutItem().getXmlString());
        assertEquals(record4, wsPutItemWithReportList.get(3).getWsPutItem().getXmlString());
        assertEquals(record5, wsPutItemWithReportList.get(4).getWsPutItem().getXmlString());
    }

    protected EntityModel getEntityModel(String xsdFileName, String dataModel, String concept, String[] keys)
            throws SAXException, IOException {
        String xsd = getFileContent(xsdFileName);
        String[] roles = { "System_Admin", "administration" }; //$NON-NLS-1$//$NON-NLS-2$
        entityModel = new EntityModel();
        PowerMockito.mockStatic(Util.class);
        Mockito.when(Util.isEnterprise()).thenReturn(false);
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(dataModel, null)));
        DataModelHelper.parseSchema(dataModel, concept, DataModelHelper.convertXsd2ElDecl(concept, xsd), keys, entityModel,
                Arrays.asList(roles));
        return entityModel;
    }

    private String getFileContent(String fileName) throws IOException {
        InputStream resourceAsStream = this.getClass().getResourceAsStream(fileName);
        BufferedReader in = new BufferedReader(new InputStreamReader(resourceAsStream));
        StringBuffer buffer = new StringBuffer();
        String line = ""; //$NON-NLS-1$
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();
    }

}
