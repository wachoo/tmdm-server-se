// ============================================================================
//
// Copyright (C) 2006-2016 Talend Inc. - www.talend.com
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
import java.util.regex.Pattern;

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

import com.amalto.core.util.Util;
import com.amalto.core.webservice.WSPutItemWithReport;

/**
 * created by talend2 on 2013-12-18 Detailled comment
 * 
 */
@PrepareForTest({ Util.class })
public class UploadServiceTest extends TestCase {

    static {
        new UploadServiceTest();
    }

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

    private Pattern removeFormatPattern = Pattern.compile("\t|\r|\n"); //$NON-NLS-1$

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        headersOnFirstLine = true;
    }

    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + UploadServiceTest.class.getSimpleName(), UploadServiceTest.class);
    }

    public void testUploadModel_Polymorphism() throws Exception {
        headerVisibleMap = new HashMap<String, Boolean>();
        headerVisibleMap.put("UploadTestModel_Polymorphism/id", true); //$NON-NLS-1$
        headerVisibleMap.put("UploadTestModel_Polymorphism/info/@xsi:type", true); //$NON-NLS-1$
        inheritanceNodePathList = new LinkedList<String>();
        inheritanceNodePathList.add("UploadTestModel_Polymorphism/info"); //$NON-NLS-1$
        String[] keys = { "UploadTestModel_Polymorphism/id" }; //$NON-NLS-1$
        entityModel = getEntityModel("UploadTestModel.xsd", "UploadTestModel", "UploadTestModel_Polymorphism", keys); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

        String record1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>1</id><info xsi:type=\"SuperInfoType\"><name>1</name></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>2</id><info xsi:type=\"SubInfoType\"><name>2</name></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record3 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>3</id><info xsi:type=\"SuperInfoType\"><name/></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record4 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>4</id><info xsi:type=\"SubInfoType\"><name>4</name></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$
        String record5 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n<UploadTestModel_Polymorphism xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><id>5</id><info xsi:type=\"SubInfoType\"><name/></info></UploadTestModel_Polymorphism>"; //$NON-NLS-1$

        // test upload excel file
        fileType = "xls"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("UploadTestModel_Polymorphism.xls").getFile()); //$NON-NLS-1$
        UploadService service = new TestUploadService(entityModel, fileType, headersOnFirstLine, headerVisibleMap,
                inheritanceNodePathList, multipleValueSeparator, seperator, encoding, textDelimiter, language);

        List<WSPutItemWithReport> wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(record1, wsPutItemWithReportList.get(0).getWsPutItem().getXmlString());
        assertEquals(record2, wsPutItemWithReportList.get(1).getWsPutItem().getXmlString());
        assertEquals(record3, wsPutItemWithReportList.get(2).getWsPutItem().getXmlString());
        assertEquals(record4, wsPutItemWithReportList.get(3).getWsPutItem().getXmlString());
        assertEquals(record5, wsPutItemWithReportList.get(4).getWsPutItem().getXmlString());

        // test upload csv file
        fileType = "csv"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("UploadTestModel_Polymorphism.csv").getFile()); //$NON-NLS-1$
        service = new TestUploadService(entityModel, fileType, headersOnFirstLine, headerVisibleMap, inheritanceNodePathList,
                multipleValueSeparator, seperator, encoding, textDelimiter, language);
        wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(record1, wsPutItemWithReportList.get(0).getWsPutItem().getXmlString());
        assertEquals(record2, wsPutItemWithReportList.get(1).getWsPutItem().getXmlString());
        assertEquals(record3, wsPutItemWithReportList.get(2).getWsPutItem().getXmlString());
        assertEquals(record4, wsPutItemWithReportList.get(3).getWsPutItem().getXmlString());
        assertEquals(record5, wsPutItemWithReportList.get(4).getWsPutItem().getXmlString());
    }

    public void testMultiNode1() throws Exception {
        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Entity xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><field1>1</field1><field2><atr1>1</atr1><atr2>2</atr2><atr3>3</atr3></field2><field2><atr1>11</atr1><atr2>22</atr2><atr3>33</atr3></field2></Entity>"; //$NON-NLS-1$
        // test upload excel file
        fileType = "xls"; //$NON-NLS-1$
        headerVisibleMap = new HashMap<String, Boolean>();
        headerVisibleMap.put("Entity/field1", true); //$NON-NLS-1$
        headerVisibleMap.put("Entity/field2/atr1", true); //$NON-NLS-1$
        headerVisibleMap.put("Entity/field2/atr2", true); //$NON-NLS-1$
        headerVisibleMap.put("Entity/field2/atr3", true); //$NON-NLS-1$
        multipleValueSeparator = "|"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("UploadTestModel_MultiNode1.xls").getFile()); //$NON-NLS-1$
        String[] keys = { "Entity/field1" }; //$NON-NLS-1$
        entityModel = getEntityModel("UploadTestModel_MultiNode1.xsd", "Entity", "Entity", keys); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        UploadService service = new TestUploadService(entityModel, fileType, headersOnFirstLine, headerVisibleMap,
                inheritanceNodePathList, multipleValueSeparator, seperator, encoding, textDelimiter, language);
        List<WSPutItemWithReport> wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(expectedResult, removeFormatPattern.matcher(wsPutItemWithReportList.get(0).getWsPutItem().getXmlString())
                .replaceAll("")); //$NON-NLS-1$

        expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Entity xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><field1>1</field1><field2><atr1>1</atr1><atr2>2</atr2><atr3>3</atr3></field2></Entity>"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("UploadTestModel_MultiNode12.xls").getFile()); //$NON-NLS-1$
        wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(expectedResult, removeFormatPattern.matcher(wsPutItemWithReportList.get(0).getWsPutItem().getXmlString())
                .replaceAll("")); //$NON-NLS-1$
    }

    public void testMultiNode2() throws Exception {
        String expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Product xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Picture/><Id>1</Id><Name>1</Name><Description>1</Description><Features><Sizes><Size>Medium</Size><Size>Small</Size></Sizes><Colors><Color>White</Color><Color>Light Blue</Color><Color>Lemon</Color></Colors></Features><Availability/><Price>1.00</Price><Family/><OnlineStore/><Stores><Store/></Stores></Product>"; //$NON-NLS-1$
        // test upload excel file
        fileType = "xls"; //$NON-NLS-1$
        headerVisibleMap = new HashMap<String, Boolean>();
        headerVisibleMap.put("Product/Id", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Name", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Price", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Description", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Availability", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Features/Sizes/Size", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Features/Colors/Color", true); //$NON-NLS-1$"
        headerVisibleMap.put("Product/Family", true); //$NON-NLS-1$"
        multipleValueSeparator = "|"; //$NON-NLS-1$
        file = new File(this.getClass().getResource("Product.xls").getFile()); //$NON-NLS-1$
        String[] keys = { "Product/Id" }; //$NON-NLS-1$
        entityModel = getEntityModel("Product.xsd", "Product", "Product", keys); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
        UploadService service = new TestUploadService(entityModel, fileType, headersOnFirstLine, headerVisibleMap,
                inheritanceNodePathList, multipleValueSeparator, seperator, encoding, textDelimiter, language);
        List<WSPutItemWithReport> wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(expectedResult, removeFormatPattern.matcher(wsPutItemWithReportList.get(0).getWsPutItem().getXmlString())
                .replaceAll("")); //$NON-NLS-1$

        expectedResult = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><Product xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><Picture/><Id>1</Id><Name>1</Name><Description>1</Description><Features><Sizes><Size/></Sizes><Colors><Color/></Colors></Features><Availability/><Price>1.00</Price><Family/><OnlineStore/><Stores><Store/></Stores></Product>"; //$NON-NLS-1$
        headerVisibleMap = new HashMap<String, Boolean>();
        headerVisibleMap.put("Product/Id", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Name", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Price", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Description", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Availability", true); //$NON-NLS-1$
        headerVisibleMap.put("Product/Family", true); //$NON-NLS-1$"
        file = new File(this.getClass().getResource("Product2.xls").getFile()); //$NON-NLS-1$
        service = new TestUploadService(entityModel, fileType, headersOnFirstLine, headerVisibleMap, inheritanceNodePathList,
                multipleValueSeparator, seperator, encoding, textDelimiter, language);
        wsPutItemWithReportList = service.readUploadFile(file);
        assertEquals(expectedResult, removeFormatPattern.matcher(wsPutItemWithReportList.get(0).getWsPutItem().getXmlString())
                .replaceAll("")); //$NON-NLS-1$
    }

    protected EntityModel getEntityModel(String xsdFileName, String dataModel, String concept, String[] keys) throws Exception {
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

    public class TestUploadService extends UploadService {

        public TestUploadService(EntityModel entityModel, String fileType, boolean headersOnFirstLine,
                Map<String, Boolean> headerVisibleMap, List<String> inheritanceNodePathList, String multipleValueSeparator,
                String seperator, String encoding, char textDelimiter, String language) {
            super(entityModel, fileType, headersOnFirstLine, headerVisibleMap, inheritanceNodePathList, multipleValueSeparator,
                    seperator, encoding, textDelimiter, language);
        }

        @Override
        protected String getCurrentDataCluster() throws Exception {
            return "UploadTestModel"; //$NON-NLS-1$
        }

        @Override
        protected String getCurrentDataModel() throws Exception {
            return "UploadTestModel"; //$NON-NLS-1$
        }

    }
}
