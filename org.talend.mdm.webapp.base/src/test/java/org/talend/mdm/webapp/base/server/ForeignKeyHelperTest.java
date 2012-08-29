// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.base.server;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.talend.mdm.commmon.util.core.EDBType;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.datamodel.management.BusinessConcept;
import org.talend.mdm.webapp.base.client.model.DataTypeConstants;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSStringPredicate;
import com.amalto.webapp.util.webservices.WSWhereCondition;
import com.amalto.webapp.util.webservices.WSWhereItem;
import com.amalto.webapp.util.webservices.WSWhereOperator;

@SuppressWarnings("nls")
public class ForeignKeyHelperTest extends TestCase {
    
    
    public void testGetForeignKeyHolder() throws Exception {

        TypeModel model = new SimpleTypeModel("family", DataTypeConstants.STRING); //$NON-NLS-1$
        model.setForeignkey("ProductFamily/Id"); //$NON-NLS-1$
        List<String> foreignKeyInfos = new ArrayList<String>();
        foreignKeyInfos.add("ProductFamily/Name"); //$NON-NLS-1$
        model.setForeignKeyInfo(foreignKeyInfos);
        model.setXpath("Product/Family"); //$NON-NLS-1$
        boolean ifFKFilter = false;
        String value = "Hats"; //$NON-NLS-1$
        String xml = null;
        String dataCluster = "Product";
        String currentXpath = "Product/Family";

        // 1. ForeignKeyInfo = ProductFamily/Name
        MDMConfiguration.getConfiguration().setProperty("xmldb.type", EDBType.QIZX.getName()); //$NON-NLS-1$
        ForeignKeyHelper.ForeignKeyHolder result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model,
                ifFKFilter, value);
        WSWhereItem whereItem = result.whereItem;
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereAnd());
        assertNotNull(whereItem.getWhereAnd().getWhereItems());

        WSWhereItem whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        WSWhereCondition condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd()
                .getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        WSWhereItem whereItem2 = whereItem1.getWhereOr().getWhereItems()[1];
        WSWhereCondition condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath()); //$NON-NLS-1$

        // 2. foreignKeyInfo = ProductFamily/Name,ProductFaimly/Description
        foreignKeyInfos.add("ProductFamily/Description"); //$NON-NLS-1$
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        whereItem2 = whereItem1.getWhereOr().getWhereItems()[1];
        condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/Description", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath()); //$NON-NLS-1$

        WSWhereItem whereItem3 = whereItem1.getWhereOr().getWhereItems()[2];
        WSWhereCondition condition3 = whereItem3.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition3.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition3.getOperator().getValue());
        assertEquals("Hats", condition3.getRightValueOrPath()); //$NON-NLS-1$

        // 3. foreignKeyInfo is null
        foreignKeyInfos.clear();
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        whereItem1 = whereItem.getWhereAnd().getWhereItems()[0];
        condition1 = whereItem1.getWhereOr().getWhereItems()[0].getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/../*", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        // 4. foreignKey = ProductFamily, foreignKeyInfo = ProductFamily/Name
        String xpathForeignKey = "ProductFamily"; //$NON-NLS-1$
        String xpathInfoForeignKey = "ProductFamily/Name"; //$NON-NLS-1$
        String realXpathForeignKey = "ProductFamily/Id"; //$NON-NLS-1$
        whereItem = mock_SpecialForeignKeyWhereCondition(xpathForeignKey, xpathInfoForeignKey, realXpathForeignKey, value);
        
        assertNotNull(whereItem);
        assertNotNull(whereItem.getWhereOr());
        assertNotNull(whereItem.getWhereOr().getWhereItems());
        assertEquals(2, whereItem.getWhereOr().getWhereItems().length);

        whereItem1 = whereItem.getWhereOr().getWhereItems()[0];
        condition1 = whereItem1.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Hats", condition1.getRightValueOrPath()); //$NON-NLS-1$

        whereItem2 = whereItem.getWhereOr().getWhereItems()[1];
        condition2 = whereItem2.getWhereAnd().getWhereItems()[0].getWhereAnd().getWhereItems()[0]
                .getWhereCondition();
        assertEquals("ProductFamily/Id", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Hats", condition2.getRightValueOrPath()); //$NON-NLS-1$

        // 5. ifFKFilter = true,fkFilter = Product/Family$$<$$Product/Family$$#,foreignKeyInfo is null,value is null,
        value = "";
        ifFKFilter = true;
        model.setFkFilter("Product/Family$$<$$Product/Family$$#");
        xml = "<Product><id>1</id><Name>Shirts</Name><Family>[3]</Family></Product>";
        foreignKeyInfos.clear();
        ForeignKeyHelper.setUseSchemaWebAgent(false);
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        condition1 = whereItem.getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("Product/Family", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._LOWER_THAN, condition1.getOperator().getValue());
        assertEquals("[3]", condition1.getRightValueOrPath()); //$NON-NLS-1$

        model.setFkFilter("Product/Family$$<$$Product/Family$$And#Product/Name$$Contains$$Product/Name$$#");
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        assertEquals(2, whereItem.getWhereAnd().getWhereItems().length);
        condition1 = whereItem.getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("Product/Family", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._LOWER_THAN, condition1.getOperator().getValue());
        assertEquals("[3]", condition1.getRightValueOrPath()); //$NON-NLS-1$
        assertEquals(WSStringPredicate._AND, condition1.getStringPredicate().getValue());
        condition2 = whereItem.getWhereAnd().getWhereItems()[1].getWhereCondition();
        assertEquals("Product/Name", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Shirts", condition2.getRightValueOrPath()); //$NON-NLS-1$

        // 6. ifFKFilter = true,fkFilter = 'ProductFamily/Name$$Contains$$Product/Name$$#',foreignKeyInfo is null
        value = "";
        ifFKFilter = true;
        model.setFkFilter("ProductFamily/Name$$Contains$$Product/Name$$#");
        xml = "<Product><id>1</id><Name>Shirts</Name><Family>[3]</Family></Product>";
        foreignKeyInfos.clear();
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        condition1 = whereItem.getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductFamily/Name", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition1.getOperator().getValue());
        assertEquals("Shirts", condition1.getRightValueOrPath()); //$NON-NLS-1$

        // 7. FK inheritance: ifFKFilter = true,fkFilter = 'ProductType/Type/@xsi:type$$=$$ProductTypeOne$$#'
        /*******************************************************************************************************
         * FK filter note<br>
         * Product/Type is a foreign key<br>
         * -----Foreign Key : ProductType/Id<br>
         * -----Foreign Key Filter : ProductType/Type/@xsi:type$$=$$ProductTypeOne$$#<br>
         * ProductType is an entity(exist three attributes: Id,Name and Type, Type is a comlexType(ComplexProductType))<br>
         * ComplexProductType is an abstract complex type<br>
         * ProductTypeOne inherited ComplexProductType<br>
         * ProductTypeTwo inherited ComplexProductType<br>
         ********************************************************************************************************/
        ifFKFilter = true;
        xml = "";
        dataCluster = "Product";
        currentXpath = "Product/Type";
        model.setFkFilter("ProductType/Type/@xsi:type$$=$$ProductTypeOne$$#");
        foreignKeyInfos.clear();
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        condition1 = whereItem.getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductType/Type/@xsi:type", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._EQUALS, condition1.getOperator().getValue());
        assertEquals("ProductTypeOne", condition1.getRightValueOrPath()); //$NON-NLS-1$

        xml = "<Product><id>1</id><Name>Shirts</Name><Family>[3]</Family></Product>";
        model.setFkFilter("ProductType/Type/@xsi:type$$=$$ProductTypeTwo$$And#ProductType/Name$$Contains$$Product/Name$$#");
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        assertEquals(2, whereItem.getWhereAnd().getWhereItems().length);
        condition1 = whereItem.getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("ProductType/Type/@xsi:type", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._EQUALS, condition1.getOperator().getValue());
        assertEquals("ProductTypeTwo", condition1.getRightValueOrPath()); //$NON-NLS-1$
        assertEquals(WSStringPredicate._AND, condition1.getStringPredicate().getValue());
        condition2 = whereItem.getWhereAnd().getWhereItems()[1].getWhereCondition();
        assertEquals("ProductType/Name", condition2.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._CONTAINS, condition2.getOperator().getValue());
        assertEquals("Shirts", condition2.getRightValueOrPath()); //$NON-NLS-1$

        // 8. ifFKFilter = true,fkFilter = 'CategoryOrga/TypeOrgaFK$$=$$OrganisationOperationnelle/OpOrgType$$#'
        ifFKFilter = true;
        value = "";
        dataCluster = "OrganisationOperationnelle";
        currentXpath = "OrganisationOperationnelle/OpOrgCategory";
        xml = "<OrganisationOperationnelle><OpOrgIdentifiantNoeud>1</OpOrgIdentifiantNoeud><OpOrgType>[1]</OpOrgType></OrganisationOperationnelle>";
        model = new SimpleTypeModel("OpOrgCategory", DataTypeConstants.STRING); //$NON-NLS-1$
        foreignKeyInfos.clear();
        foreignKeyInfos.add("CategoryOrga/CategoryOrgaName");
        model.setForeignKeyInfo(foreignKeyInfos);
        model.setForeignkey("CategoryOrga/Id");
        model.setFkFilter("CategoryOrga/TypeOrgaFK$$=$$OrganisationOperationnelle/OpOrgType$$");
        result = ForeignKeyHelper.getForeignKeyHolder(xml, dataCluster, currentXpath, model, ifFKFilter, value);
        whereItem = result.whereItem;
        condition1 = whereItem.getWhereAnd().getWhereItems()[0].getWhereCondition();
        assertEquals("CategoryOrga/TypeOrgaFK", condition1.getLeftPath()); //$NON-NLS-1$
        assertEquals(WSWhereOperator._EQUALS, condition1.getOperator().getValue());
        assertEquals("[1]", condition1.getRightValueOrPath()); //$NON-NLS-1$
    }
    
    
    public void testFormatForeignKeyValue() {
        String rightPathOrValue = "\"[foo]\"";
        String origiRightValueOrPath = "Foo/foo";
        Map<String, String> mockFkMap = new HashMap<String, String>();        
        mockFkMap.put("/" + origiRightValueOrPath, origiRightValueOrPath);
        
        String result = ForeignKeyHelper.formatForeignKeyValue(rightPathOrValue, origiRightValueOrPath, mockFkMap);
        assert(result.equals("foo"));
        
        result = ForeignKeyHelper.formatForeignKeyValue(rightPathOrValue, "abc", mockFkMap);
        assert(result.equals(rightPathOrValue));
    }
    
    /**
     * Mock org.talend.mdm.webapp.base.server.ForeignKeyHelper.getForeignKeyHolder(TypeModel, boolean, String) <li>when
     * the foreignKey = conceptName, using the method to test where condition.
     * @return
     * @throws Exception
     */
    private WSWhereItem mock_SpecialForeignKeyWhereCondition(String xpathForeignKey, String xpathInfoForeignKey,
            String realXpathForeignKey, String value)
            throws Exception {
        String initXpathForeignKey = Util.getForeignPathFromPath(xpathForeignKey);
        initXpathForeignKey = initXpathForeignKey.split("/")[0]; //$NON-NLS-1$
        String[] fkInfos = xpathInfoForeignKey.split(","); //$NON-NLS-1$
        String fkWhere = initXpathForeignKey + "/../* CONTAINS " + value; //$NON-NLS-1$
        if (xpathInfoForeignKey.trim().length() > 0) {
            StringBuffer ids = new StringBuffer();
            String realForeignKey = null;
            if (xpathForeignKey.indexOf("/") == -1) { //$NON-NLS-1$
                String[] fks = new String[] { realXpathForeignKey };
                if (fks != null && fks.length > 0) {
                    realForeignKey = fks[0];
                    for (int i = 0; i < fks.length; i++) {
                        ids.append(fks[i] + " CONTAINS " + value); //$NON-NLS-1$
                        if (i != fks.length - 1)
                            ids.append(" OR "); //$NON-NLS-1$
                    }
                }
            }
            StringBuffer sb = new StringBuffer();
            for (String fkInfo : fkInfos) {
                sb.append((fkInfo.startsWith(".") ? Util.convertAbsolutePath((realForeignKey != null && realForeignKey.trim() //$NON-NLS-1$
                        .length() > 0) ? realForeignKey : xpathForeignKey, fkInfo) : fkInfo) + " CONTAINS " + value); //$NON-NLS-1$
                sb.append(" OR "); //$NON-NLS-1$
            }
            if (realForeignKey != null)
                sb.append(ids.toString());
            else
                sb.append(xpathForeignKey + " CONTAINS " + value); //$NON-NLS-1$
            fkWhere = sb.toString();
        }

        return Util.buildWhereItems(fkWhere);
    }
    
    public void testConvertFKInfo2DisplayInfo(){
        ForeignKeyBean bean1 = new ForeignKeyBean();
        bean1.set("Name", "Apple Store");
        bean1.set("Id", "1");
        bean1.set("Code", "10001");
        bean1.set("i", "1");
        bean1.getForeignKeyInfo().put("Store/Id", "1");
        bean1.getForeignKeyInfo().put("Store/Code", "10001");
        bean1.getForeignKeyInfo().put("Store/Name", "Apple Store");
        
        List<String> fkInfoList = new ArrayList<String>();
        fkInfoList.add("Store/Name");
        fkInfoList.add("Store/Code");
        fkInfoList.add("Store/Id");
        
        ForeignKeyHelper.convertFKInfo2DisplayInfo(bean1, fkInfoList);
        
        assertNotNull(bean1.getDisplayInfo());
        assertEquals("Apple Store-10001-1", bean1.getDisplayInfo());
        
        
        ForeignKeyBean bean2 = new ForeignKeyBean();
        bean2.set("Name", "Google Market");
        bean2.set("Id", "2");
        bean2.set("Code", "10002");
        bean2.set("i", "2");
        bean2.getForeignKeyInfo().put("Store/Id", "2");
        bean2.getForeignKeyInfo().put("Store/Code", "10002");
        bean2.getForeignKeyInfo().put("Store/Name", "Google Market");
        
        fkInfoList.clear();
        
        ForeignKeyHelper.convertFKInfo2DisplayInfo(bean2, fkInfoList);
        assertNull(bean2.getDisplayInfo());
    }
}