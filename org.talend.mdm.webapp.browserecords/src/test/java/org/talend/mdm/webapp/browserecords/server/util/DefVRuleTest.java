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
package org.talend.mdm.webapp.browserecords.server.util;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.server.defaultrule.DefVRule;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;

import com.amalto.webapp.core.util.XmlUtil;

@SuppressWarnings("nls")
public class DefVRuleTest extends TestCase {

    /**
     * set Agency/Name's defaultValue = liang, <li>when you run the defVRule.setDefaultValue(), the following codes:<li>
     * DefVRule defVRule = new DefVRule(entityModel.getMetaDataTypes()); <li>String expression = "\"liang\""; <li>String
     * xpath = "Agency/Name"; <li>String concept = "Agency"; <li>defVRule.setDefaultValue(xpath, concept, d,
     * expression); <li>
     * assertEquals("liang", d.selectSingleNode(xpath).getText()); <li>saxon-8.7.jar and jaxen-1.1.1.jar can not be
     * found in the test environment, so if you want to test it, please link the two jar package. DOC Administrator
     * Comment method "test_setDefaultValue".
     * 
     * @throws Exception
     */
    public void test_setDefaultValue() throws Exception {
        EntityModel entityModel = TestData.getEntityModel();
        Document d = getDocument(entityModel);
        assertNotNull(d.asXML());
    }

    private Document getDocument(EntityModel entityModel) throws Exception {
        DefVRule defVRule = new DefVRule(entityModel.getMetaDataTypes());
        String language = "en";
        TypeModel typeModel = entityModel.getMetaDataTypes().get("Agency");
        org.w3c.dom.Document doc = defVRule.getSubXML(typeModel, null, language);
        return XmlUtil.parseDocument(doc);
    }
}
