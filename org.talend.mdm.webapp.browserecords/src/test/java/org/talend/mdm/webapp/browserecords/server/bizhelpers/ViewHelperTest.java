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
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.talend.mdm.webapp.base.client.model.DataTypeCustomized;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSView;

@SuppressWarnings("nls")
public class ViewHelperTest extends TestCase {
    
    private WSView wsView;
    private String dataModel;
    private String language;
    private EntityModel entityModel;
    private String[] searchableBusinessElements = {"ProductFamily","ProductFamily/Id","ProductFamily/Name", "ProductFamily/ChangeStatus"};
    private String[] viewableBusinessElements = {"ProductFamily/Id","ProductFamily/Name", "ProductFamily/ChangeStatus"};
    
    public ViewHelperTest(){
        initData();
    }

    public void testBuilderLayout() throws Exception {
        String xml = inputStream2String(this.getClass().getResourceAsStream("temp_ColumnTreeLayout.xml"));
        Document doc = Util.parse(xml);
        Element root = doc.getDocumentElement();
        ColumnTreeLayoutModel result = ViewHelper.builderLayout(root);
        assertNotNull(result);
        assertEquals(2, result.getColumnTreeModels().size());
        assertTrue(result.getColumnTreeModels().get(0).getStyle().equals(""));
        assertTrue(result.getColumnTreeModels().get(1).getStyle().equals(""));
        assertTrue(result.getColumnTreeModels().get(0).getStyle().equals(""));
        assertNotNull(result.getColumnTreeModels().get(0).getColumnElements());
        assertNotNull(result.getColumnTreeModels().get(1).getColumnElements());
        assertEquals(3, result.getColumnTreeModels().get(0).getColumnElements().size());
        assertEquals(2, result.getColumnTreeModels().get(1).getColumnElements().size());
        assertEquals("/1", result.getColumnTreeModels().get(0).getColumnElements().get(0).getParent());
        assertNull(result.getColumnTreeModels().get(0).getColumnElements().get(0).getChildren());
        assertEquals("/Cusomer/Name", result.getColumnTreeModels().get(0).getColumnElements().get(1).getxPath());
        assertEquals("Name", result.getColumnTreeModels().get(0).getColumnElements().get(1).getLabel());
        assertEquals("/1", result.getColumnTreeModels().get(0).getColumnElements().get(2).getParent());
        assertNotNull(result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren());
        assertEquals(2, result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().size());
        assertEquals("/1/@children.2", result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().get(0).getParent());
        assertEquals("/Customer/Address/zipCode", result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().get(0).getxPath());
        assertEquals("zipCode", result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().get(0).getLabel());
        assertTrue(result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().get(1).getHtmlSnippet().equals(""));
        assertTrue(result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().get(1).getLabelStyle().equals(""));
        assertTrue(result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().get(1).getStyle().equals(""));
        assertTrue(result.getColumnTreeModels().get(0).getColumnElements().get(2).getChildren().get(1).getValueStyle().equals(""));
        assertEquals(2, result.getColumnTreeModels().get(1).getColumnElements().size());
        assertEquals("/2", result.getColumnTreeModels().get(1).getColumnElements().get(0).getParent());
        assertEquals("/Customer/picture", result.getColumnTreeModels().get(1).getColumnElements().get(0).getxPath());
        assertEquals("/2", result.getColumnTreeModels().get(1).getColumnElements().get(1).getParent());
        assertEquals("/Customer/country", result.getColumnTreeModels().get(1).getColumnElements().get(1).getxPath());
        
        xml = inputStream2String(this.getClass().getResourceAsStream("productform.xml"));
        doc = Util.parse(xml);
        root = doc.getDocumentElement();
        result = ViewHelper.builderLayout(root);
        assertNotNull(result);
        assertEquals(2, result.getColumnTreeModels().size());
        assertEquals(8, result.getColumnTreeModels().get(0).getColumnElements().size());
        assertEquals(3, result.getColumnTreeModels().get(1).getColumnElements().size());
        assertEquals("_4DuYgGKFEeG_l-qIWXZr4g", result.getColumnTreeModels().get(0).getColumnElements().get(0).getParent());
        assertNotNull(result.getColumnTreeModels().get(0).getColumnElements().get(3).getChildren());
        assertEquals("/Product/Features", result.getColumnTreeModels().get(0).getColumnElements().get(3).getxPath());
        assertEquals("a", result.getColumnTreeModels().get(1).getColumnElements().get(0).getLabelStyle());
        assertEquals("b", result.getColumnTreeModels().get(1).getColumnElements().get(0).getValueStyle());
        assertEquals("d", result.getColumnTreeModels().get(1).getColumnElements().get(1).getStyle());
        assertEquals("_4EnJUmKFEeG_l-qIWXZr4g", result.getColumnTreeModels().get(1).getColumnElements().get(1).getParent());
        assertNotNull(result.getColumnTreeModels().get(1).getColumnElements().get(2).getHtmlSnippet());
        assertNotSame(0, result.getColumnTreeModels().get(1).getColumnElements().get(2).getHtmlSnippet().trim().length());
        
        xml = inputStream2String(this.getClass().getResourceAsStream("Store.xml"));
        doc = Util.parse(xml);
        root = doc.getDocumentElement();
        result = ViewHelper.builderLayout(root);
        assertNotNull(result);
        assertEquals(2, result.getColumnTreeModels().size());
        assertEquals(5, result.getColumnTreeModels().get(0).getColumnElements().size());
        assertNull(result.getColumnTreeModels().get(1).getColumnElements());
        
        xml = inputStream2String(this.getClass().getResourceAsStream("MyTest.xml"));
        doc = Util.parse(xml);
        root = doc.getDocumentElement();
        result = ViewHelper.builderLayout(root);
        assertNotNull(result);
        assertEquals(3, result.getColumnTreeModels().size());
        assertNotNull(result.getColumnTreeModels().get(0).getColumnElements());
        assertNull(result.getColumnTreeModels().get(1).getColumnElements());
        assertNull(result.getColumnTreeModels().get(2).getColumnElements());

    }
    
    private String inputStream2String(InputStream is) {
        BufferedReader in = new BufferedReader(new InputStreamReader(is));
        StringBuffer buffer = new StringBuffer();
        String line = "";
        try {
            while ((line = in.readLine()) != null) {
                buffer.append(line);
            }
        } catch (IOException e) {
            fail();
        }
        return buffer.toString();
    }
    
    public void testGetConceptFromDefaultViewName(){
        String viewName = "Browse_items_ProductFamily";        
        assertEquals("ProductFamily", ViewHelper.getConceptFromDefaultViewName(viewName));
        viewName = "#Browse_items_ProductFamily";
        assertEquals("", ViewHelper.getConceptFromDefaultViewName(viewName));
        viewName = "Browse_items_ProductFamily#Family2";
        assertEquals("ProductFamily", ViewHelper.getConceptFromDefaultViewName(viewName));
    }
    
    public void testGetViewLabel(){
        String language = "en";
        assertEquals("Product Family", ViewHelper.getViewLabel(language, wsView));
        language = "fr";
        assertEquals("Famille Produit", ViewHelper.getViewLabel(language, wsView));
    }
    
    public void testGetViewables(){       
        assertEquals(viewableBusinessElements, ViewHelper.getViewables(wsView));      
    }
    
    public void testGetSearchables(){        
        Map<String, String> result = new HashMap<String, String>();
        result.put("ProductFamily", "Whole content");
        result.put("ProductFamily/Id", "id");
        result.put("ProductFamily/Name", "name");
        result.put("ProductFamily/ChangeStatus", "changeSatus");
        language = "en";
        assertEquals(result, ViewHelper.getSearchables(wsView, dataModel, language, entityModel));
        result.clear();
        
        result.put("ProductFamily", "Tout le contenu");
        result.put("ProductFamily/Id", "rf-id");
        result.put("ProductFamily/Name", "rf-name");
        result.put("ProductFamily/ChangeStatus", "rf-changeSatus");
        language = "fr";
        assertEquals(result, ViewHelper.getSearchables(wsView, dataModel, language, entityModel));
    }
    
    /**
     * DOC Administrator Comment method "initData".
     */
    private void initData(){
        wsView = new WSView();
        wsView.setName("Browse_items_ProductFamily");
        wsView.setDescription("[FR:Famille Produit][EN:Product Family]");
        wsView.setSearchableBusinessElements(searchableBusinessElements);
        wsView.setViewableBusinessElements(viewableBusinessElements);
        
        dataModel = "Product";
        language = "en";
        entityModel = new EntityModel();
        entityModel.setConceptName("ProductFamily");
        String[] keys = {"1"};
        entityModel.setKeys(keys);
        Map<String, TypeModel> metaDataTypes = new HashMap<String, TypeModel>();
        ComplexTypeModel family = new ComplexTypeModel();
        
        SimpleTypeModel id = new SimpleTypeModel("id",new DataTypeCustomized("",""));
        id.addLabel("en", "id");
        id.addLabel("fr", "rf-id");
        SimpleTypeModel name = new SimpleTypeModel("name",new DataTypeCustomized("",""));
        name.addLabel("en", "name");
        name.addLabel("fr", "rf-name");
        SimpleTypeModel changeSatus = new SimpleTypeModel("changeSatus",new DataTypeCustomized("",""));
        changeSatus.addLabel("en", "changeSatus");
        changeSatus.addLabel("fr", "rf-changeSatus");
        
        metaDataTypes.put("ProductFamily", family);
        metaDataTypes.put("ProductFamily/Id", id);
        metaDataTypes.put("ProductFamily/Name", name);
        metaDataTypes.put("ProductFamily/ChangeStatus", changeSatus);        
        entityModel.setMetaDataTypes(metaDataTypes);    
    }
}
