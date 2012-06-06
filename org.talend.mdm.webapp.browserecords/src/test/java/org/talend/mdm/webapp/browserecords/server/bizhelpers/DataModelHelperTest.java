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
package org.talend.mdm.webapp.browserecords.server.bizhelpers;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit3.PowerMockSuite;
import org.talend.mdm.commmon.util.datamodel.management.DataModelID;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;

import com.amalto.core.util.Util;

@PrepareForTest({ Util.class })
@SuppressWarnings("nls")
public class DataModelHelperTest extends TestCase {
	
    @SuppressWarnings("unchecked")
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite("Unit tests for " + DataModelHelperTest.class.getSimpleName(), DataModelHelperTest.class);
    }

    public void testParsingMetadata() throws Exception {

        EntityModel entityModel=new EntityModel();
        String datamodelName="Contract";
        String concept="Contract";
        String[] ids={""};
        String[] roles={"Demo_Manager", "System_Admin", "authenticated", "administration"};
        InputStream stream = getClass().getResourceAsStream("Contract.xsd");
        String xsd = inputStream2String(stream);
        
        DataModelHelper.alwaysEnterprise = true;
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(datamodelName, null)));
        DataModelHelper.parseSchema("Contract", "Contract", DataModelHelper.convertXsd2ElDecl(concept, xsd), ids, entityModel,
                Arrays.asList(roles));
        Map<String, TypeModel> metaDataTypes = entityModel.getMetaDataTypes();
        assertEquals(13, metaDataTypes.size());
        assertTrue(!metaDataTypes.get("Contract/detail").isSimpleType());

        stream = getClass().getResourceAsStream("ContractMultiLevel.xsd");
        xsd = inputStream2String(stream);
        EntityModel newModel = new EntityModel();
        DataModelHelper.alwaysEnterprise = true;
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(datamodelName, null)));
        DataModelHelper.parseSchema("Contract", "Contract", DataModelHelper.convertXsd2ElDecl(concept, xsd), ids, newModel,
                Arrays.asList(roles));
        metaDataTypes = newModel.getMetaDataTypes();
        assertEquals(11, metaDataTypes.size());
        assertFalse(metaDataTypes.get("Contract/detail").isSimpleType());
        assertTrue(metaDataTypes.get("Contract/detail/code").isSimpleType());
        assertTrue(metaDataTypes.get("Contract/detail:ContractDetailSubType/code").isSimpleType());
        assertTrue(metaDataTypes.get("Contract/detail:ContractDetailSubType/subType").isSimpleType());
        assertTrue(metaDataTypes.get("Contract/detail:ContractDetailSubTypeOne/code").isSimpleType());
        assertTrue(metaDataTypes.get("Contract/detail:ContractDetailSubTypeOne/subType").isSimpleType());
        assertTrue(metaDataTypes.get("Contract/detail:ContractDetailSubTypeOne/subTypeOne").isSimpleType());
    }
    
    public void testTypePath() throws Exception {
    
        EntityModel entityModel=new EntityModel();
        String datamodelName="RTE";
        String concept="Contrat";
        String[] ids={""};
        String[] roles={"Demo_Manager", "System_Admin", "authenticated", "administration"};
        InputStream stream = getClass().getResourceAsStream("RTE.xsd");
        String xsd = inputStream2String(stream);
        
        DataModelHelper.alwaysEnterprise = true;
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(datamodelName, null)));
        DataModelHelper.parseSchema(datamodelName, concept, DataModelHelper.convertXsd2ElDecl(concept, xsd), ids, entityModel,
                Arrays.asList(roles));
        Map<String, TypeModel> metaDataTypes = entityModel.getMetaDataTypes();
        TypeModel testModel=metaDataTypes.get("Contrat/detailContrat:AP-RP/Perimetre/entitesPresentes/EDPs/EDP/dateDebutApplication");
        assertEquals("Contrat/detailContrat/Perimetre/entitesPresentes/EDPs/EDP/dateDebutApplication", testModel.getXpath());
        
    }

    private String inputStream2String(InputStream is) throws IOException {

        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        while ((line = in.readLine()) != null) {
            buffer.append(line);
        }
        return buffer.toString();

    }


    public void testParseLabels() throws Exception {
        String datamodelName = "Employee";
        String concept = "Employee";
        String[] ids = { "" };
        String[] roles = { "Emp_Manager", "Emp_User" };
        InputStream stream = getClass().getResourceAsStream("Employee.xsd");
        String language = "fr";
        String xsd = inputStream2String(stream);

        EntityModel employeeModel = new EntityModel();

        PowerMockito.mockStatic(Util.class);
        Mockito.when(Util.isEnterprise()).thenReturn(false);
        DataModelHelper.overrideSchemaManager(new SchemaMockAgent(xsd, new DataModelID(datamodelName, null)));
        DataModelHelper.parseSchema(datamodelName, concept, DataModelHelper.convertXsd2ElDecl(concept, xsd), ids, employeeModel,
                Arrays.asList(roles));

        Map<String, TypeModel> types = employeeModel.getMetaDataTypes();
        TypeModel addressType = types.get("Employee/Address");
        assertEquals(2, addressType.getLabelMap().size());
        assertEquals("adresse", addressType.getLabel(language));

        List<ComplexTypeModel> reusableTypes = ((ComplexTypeModel) addressType).getReusableComplexTypes();
        for (ComplexTypeModel complexTypeModel : reusableTypes) {
        	if(complexTypeModel==null||complexTypeModel.getLabelMap()==null)
        		continue;
            String typeName = complexTypeModel.getName();
            if(typeName.equals("AddressType")){
                assertEquals(2, complexTypeModel.getLabelMap().size());
                assertEquals("adresseType", complexTypeModel.getLabel(language));
            }else if(typeName.equals("CNAddressType")){
                assertEquals(0, complexTypeModel.getLabelMap().size());
                assertEquals(typeName, complexTypeModel.getLabel(language));
            }else if(typeName.equals("EUAddressType")){
                assertEquals(0, complexTypeModel.getLabelMap().size());
                assertEquals(typeName, complexTypeModel.getLabel(language));
            }else if(typeName.equals("USAddressType")){
                assertEquals(2, complexTypeModel.getLabelMap().size());
                assertEquals("USAdresseType", complexTypeModel.getLabel(language));
            }

        }

    }
}
