package org.talend.mdm.webapp.browserecords.client.util;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.google.gwt.junit.client.GWTTestCase;

public class CommonUtilGWTTest extends GWTTestCase {

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }

    public void testToXmlStringA() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelA());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordA(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(
                xml,
                "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo>hello, morning</memo></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringB() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelB());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordB(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(xml,
                "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name/><age>25</age><memo/></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringC() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelC());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordC(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(
                xml,
                "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo>I'm zhang yang</memo></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringD() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelD());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordD(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(
                xml,
                "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo>I'm zhang yang</memo></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringE() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelE());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordE(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(
                xml,
                "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo/><memo/><memo/><memo/></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringF() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelF());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordF(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(
                xml,
                "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo/><memo>hello</memo><memo/><memo>bye</memo></Test>"); //$NON-NLS-1$
    }

    public void testConvertList2Xml() {
        List<String> list = new ArrayList<String>();
        list.add("Id"); //$NON-NLS-1$ 
        list.add("Name"); //$NON-NLS-1$ 
        list.add("Family"); //$NON-NLS-1$ 
        list.add("Price"); //$NON-NLS-1$ 
        list.add("Availability"); //$NON-NLS-1$ 
        String xml = "<header><item>Id</item><item>Name</item><item>Family</item><item>Price</item><item>Availability</item></header>"; //$NON-NLS-1$         
        assertEquals(xml, CommonUtil.convertList2Xml(list, "header")); //$NON-NLS-1$ 
    }
}
