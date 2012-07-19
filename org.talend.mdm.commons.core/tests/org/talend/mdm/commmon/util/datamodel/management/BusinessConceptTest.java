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
package org.talend.mdm.commmon.util.datamodel.management;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Map;

import junit.framework.TestCase;

@SuppressWarnings("nls")
public class BusinessConceptTest extends TestCase {

    public void testParseBusinessConcept() throws Exception {
        SchemaManager schemaManager = new SchemaAgentTestMock();

        String schemaFileName="Product.xsd";
        String schema = loadDataModelSchema(schemaFileName);
        if (schema == null)
            fail("Failed to load " + schemaFileName);

        DataModelBean dataModelBean = schemaManager.instantiateDataModelBean(schema);
        BusinessConcept bizConcept = dataModelBean.getBusinessConcept("Product");

        bizConcept.load();
        
        assertEquals("Product", bizConcept.getName());
        
        Map<String, String> xpathTypeMap = bizConcept.getXpathTypeMap();
        assertNotNull(xpathTypeMap);
        
        // basic simple type
        assertEquals("xsd:boolean", xpathTypeMap.get("Product/Availability"));
        assertEquals("xsd:string", xpathTypeMap.get("Product/Id"));
        assertEquals("xsd:decimal", xpathTypeMap.get("Product/Price"));

        // complex type
        assertEquals(ReusableType.COMPLEX_TYPE, xpathTypeMap.get("Product"));
        assertEquals(ReusableType.COMPLEX_TYPE, xpathTypeMap.get("Product/Features"));

        // derived simple type
        assertEquals("xsd:string", xpathTypeMap.get("Product/Features/Sizes/Size"));
        assertEquals("xsd:string", xpathTypeMap.get("Product/Picture"));

    }

    private String loadDataModelSchema(String fileName) throws IOException {
        if (fileName == null)
            return null;

        InputStream stream = getClass().getResourceAsStream(fileName);
        String output = convertStreamToString(stream);

        return output;
    }

    private String convertStreamToString(InputStream is) throws IOException {
        /*
         * To convert the InputStream to String we use the Reader.read(char[] buffer) method. We iterate until the
         * Reader return -1 which means there's no more data to read. We use the StringWriter class to produce the
         * string.
         */
        if (is != null) {
            Writer writer = new StringWriter();

            char[] buffer = new char[1024];
            try {
                Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
                int n;
                while ((n = reader.read(buffer)) != -1) {
                    writer.write(buffer, 0, n);
                }
            } finally {
                is.close();
            }
            return writer.toString();
        } else {
            return "";
        }
    }

}
