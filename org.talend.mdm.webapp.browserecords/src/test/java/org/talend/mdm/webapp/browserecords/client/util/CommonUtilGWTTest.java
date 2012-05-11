package org.talend.mdm.webapp.browserecords.client.util;

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
        assertEquals(xml, "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo>hello, morning</memo></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringB() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelB());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordB(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(xml, "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name/><age>25</age><memo/></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringC() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelC());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordC(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(xml, "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringD() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelD());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordD(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(xml, "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringE() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelE());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordE(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(xml, "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo/></Test>"); //$NON-NLS-1$
    }

    public void testToXmlStringF() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelF());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordF(), entity);
        String xml = CommonUtil.toXML(nodeModel, viewBean);
        assertEquals(xml, "<Test xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"><subelement>111</subelement><name>zhang</name><age>25</age><memo/><memo>hello</memo><memo/><memo>bye</memo></Test>"); //$NON-NLS-1$
    }

    public void testGetCountOfBrotherOfTheSameName() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelG());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordG(), entity);

        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(nodeModel), 1);

        ItemNodeModel subElement = (ItemNodeModel) nodeModel.getChild(0);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(subElement), 1);

        ItemNodeModel name = (ItemNodeModel) nodeModel.getChild(1);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(name), 1);

        ItemNodeModel age = (ItemNodeModel) nodeModel.getChild(2);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(age), 1);

        ItemNodeModel memo1 = (ItemNodeModel) nodeModel.getChild(3);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(memo1), 4);

        ItemNodeModel memo2 = (ItemNodeModel) nodeModel.getChild(4);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(memo2), 4);

        ItemNodeModel memo3 = (ItemNodeModel) nodeModel.getChild(5);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(memo3), 4);

        ItemNodeModel memo4 = (ItemNodeModel) nodeModel.getChild(6);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(memo4), 4);

        ItemNodeModel cp = (ItemNodeModel) nodeModel.getChild(7);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(cp), 1);

        ItemNodeModel title = (ItemNodeModel) cp.getChild(0);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(title), 1);

        ItemNodeModel address1 = (ItemNodeModel) cp.getChild(1);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(address1), 3);

        ItemNodeModel address2 = (ItemNodeModel) cp.getChild(2);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(address2), 3);

        ItemNodeModel address3 = (ItemNodeModel) cp.getChild(3);
        assertEquals(CommonUtil.getCountOfBrotherOfTheSameName(address3), 3);
    }

    public void testHasChildrenValue() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelG());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordG(), entity);

        assertEquals(CommonUtil.hasChildrenValue(nodeModel), true);

        ItemNodeModel subElement = (ItemNodeModel) nodeModel.getChild(0);
        assertEquals(CommonUtil.hasChildrenValue(subElement), true);

        ItemNodeModel name = (ItemNodeModel) nodeModel.getChild(1);
        assertEquals(CommonUtil.hasChildrenValue(name), true);

        ItemNodeModel age = (ItemNodeModel) nodeModel.getChild(2);
        assertEquals(CommonUtil.hasChildrenValue(age), false);

        ItemNodeModel memo1 = (ItemNodeModel) nodeModel.getChild(3);
        assertEquals(CommonUtil.hasChildrenValue(memo1), false);

        ItemNodeModel memo2 = (ItemNodeModel) nodeModel.getChild(4);
        assertEquals(CommonUtil.hasChildrenValue(memo2), true);

        ItemNodeModel memo3 = (ItemNodeModel) nodeModel.getChild(5);
        assertEquals(CommonUtil.hasChildrenValue(memo3), false);

        ItemNodeModel memo4 = (ItemNodeModel) nodeModel.getChild(6);
        assertEquals(CommonUtil.hasChildrenValue(memo4), true);

        ItemNodeModel cp = (ItemNodeModel) nodeModel.getChild(7);
        assertEquals(CommonUtil.hasChildrenValue(cp), false);

        ItemNodeModel title = (ItemNodeModel) cp.getChild(0);
        assertEquals(CommonUtil.hasChildrenValue(title), false);

        ItemNodeModel address1 = (ItemNodeModel) cp.getChild(1);
        assertEquals(CommonUtil.hasChildrenValue(address1), false);

        ItemNodeModel address2 = (ItemNodeModel) cp.getChild(2);
        assertEquals(CommonUtil.hasChildrenValue(address2), false);

        ItemNodeModel address3 = (ItemNodeModel) cp.getChild(3);
        assertEquals(CommonUtil.hasChildrenValue(address3), false);
    }

    public void testGetRealXpathWithout() {
        ViewBean viewBean = new ViewBean();
        EntityModel entity = CommonUtilTestData.getEntityModel(ClientResourceData.getModelG());
        viewBean.setBindingEntityModel(entity);
        ItemNodeModel nodeModel = CommonUtilTestData.getItemNodeModel(ClientResourceData.getRecordG(), entity);

        assertEquals(CommonUtil.getRealXPath(nodeModel), "Test"); //$NON-NLS-1$

        ItemNodeModel subElement = (ItemNodeModel) nodeModel.getChild(0);
        assertEquals(CommonUtil.getRealXPath(subElement), "Test/subelement[1]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(subElement), "Test/subelement"); //$NON-NLS-1$

        ItemNodeModel name = (ItemNodeModel) nodeModel.getChild(1);
        assertEquals(CommonUtil.getRealXPath(name), "Test/name[1]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(name), "Test/name"); //$NON-NLS-1$

        ItemNodeModel age = (ItemNodeModel) nodeModel.getChild(2);
        assertEquals(CommonUtil.getRealXPath(age), "Test/age[1]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(age), "Test/age"); //$NON-NLS-1$

        ItemNodeModel memo1 = (ItemNodeModel) nodeModel.getChild(3);
        assertEquals(CommonUtil.getRealXPath(memo1), "Test/memo[1]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(memo1), "Test/memo"); //$NON-NLS-1$

        ItemNodeModel memo2 = (ItemNodeModel) nodeModel.getChild(4);
        assertEquals(CommonUtil.getRealXPath(memo2), "Test/memo[2]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(memo2), "Test/memo"); //$NON-NLS-1$

        ItemNodeModel memo3 = (ItemNodeModel) nodeModel.getChild(5);
        assertEquals(CommonUtil.getRealXPath(memo3), "Test/memo[3]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(memo3), "Test/memo"); //$NON-NLS-1$

        ItemNodeModel memo4 = (ItemNodeModel) nodeModel.getChild(6);
        assertEquals(CommonUtil.getRealXPath(memo4), "Test/memo[4]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(memo4), "Test/memo"); //$NON-NLS-1$

        ItemNodeModel cp = (ItemNodeModel) nodeModel.getChild(7);
        assertEquals(CommonUtil.getRealXPath(cp), "Test/cp[1]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(cp), "Test/cp"); //$NON-NLS-1$

        ItemNodeModel title = (ItemNodeModel) cp.getChild(0);
        assertEquals(CommonUtil.getRealXPath(title), "Test/cp[1]/title[1]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(title), "Test/cp[1]/title"); //$NON-NLS-1$

        ItemNodeModel address1 = (ItemNodeModel) cp.getChild(1);
        assertEquals(CommonUtil.getRealXPath(address1), "Test/cp[1]/address[1]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(address1), "Test/cp[1]/address"); //$NON-NLS-1$

        ItemNodeModel address2 = (ItemNodeModel) cp.getChild(2);
        assertEquals(CommonUtil.getRealXPath(address2), "Test/cp[1]/address[2]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(address2), "Test/cp[1]/address"); //$NON-NLS-1$

        ItemNodeModel address3 = (ItemNodeModel) cp.getChild(3);
        assertEquals(CommonUtil.getRealXPath(address3), "Test/cp[1]/address[3]"); //$NON-NLS-1$
        assertEquals(CommonUtil.getRealXpathWithoutLastIndex(address3), "Test/cp[1]/address"); //$NON-NLS-1$
    }
}
