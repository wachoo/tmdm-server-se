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
package org.talend.mdm.webapp.itemsbrowser2.test.server;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.apache.commons.io.FileUtils;
import org.talend.mdm.webapp.base.server.mockup.FakeData;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers.DataModelHelper;

import com.sun.xml.xsom.XSElementDecl;
import com.sun.xml.xsom.XSSchemaSet;
import com.sun.xml.xsom.parser.XSOMParser;
import com.sun.xml.xsom.util.DomAnnotationParserFactory;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class DataModelTest extends TestCase{
    
    public void testParseSchema() throws Exception {

        String xsd = getXsdInput("Product.xsd");//$NON-NLS-1$
        String concept = "Product";//$NON-NLS-1$
        
        XSOMParser reader = new XSOMParser();
        reader.setAnnotationParser(new DomAnnotationParserFactory());
        reader.parse(new StringReader(xsd));
        XSSchemaSet xss = reader.getResult();
        XSElementDecl eleDecl = DataModelHelper.getElementDeclByName(concept, xss);
        
        EntityModel entityModel = new EntityModel();
        // analyst model
        if (eleDecl != null) {
            List<String> roles=new ArrayList<String>();
            roles.add(FakeData.DEFAULT_ROLE);
            DataModelHelper.travelXSElement(eleDecl, eleDecl.getName(), entityModel, null, roles);
        }
        
        //check result
        Map<String, TypeModel> metaDataTypes = entityModel.getMetaDataTypes();
        TypeModel typeModel=metaDataTypes.get("Product/Family");//$NON-NLS-1$
        assertEquals("ProductFamily/Id", typeModel.getForeignkey());//$NON-NLS-1$
    }
    
    private String getXsdInput(String file) throws IOException {
        URL url = getClass().getClassLoader().getResource(file);
        assertNotNull(url);
        File inputXmlFile = new File(url.getFile());
        return FileUtils.readFileToString(inputXmlFile);
    }

}
