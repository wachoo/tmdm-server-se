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
package org.talend.mdm.webapp.browserecords.server.util;

import junit.framework.TestCase;

import org.dom4j.Document;
import org.talend.mdm.webapp.browserecords.server.defaultrule.DefVRule;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.amalto.webapp.core.util.XmlUtil;

@SuppressWarnings("nls")
public class XSLTUtilTest extends TestCase {

    /**
     * set Agency/Name's defaultValue = liang, <li>when you run the following testcase, saxon-8.7.jar and
     * jaxen-1.1.1.jar can not be found in the test environment, so if you want to test it, please link the two jar
     * package. DOC Administrator Comment method "test_setDefaultValue".
     * 
     * @throws Exception
     */
    public void test_setDefaultValue() throws Exception {
        // EntityModel entityModel = BrowseRecordsActionTest.getEntityModel();
        //
        // XSLTUtil xsltUtil = new XSLTUtil(entityModel.getMetaDataTypes());
        // String expression = "\"liang\"";
        // String xpath = "Agency/Name";
        // String concept = "Agency";
        // Document d = getDocument(entityModel);
        // assertNotNull(d.asXML());
        //
        // xsltUtil.setDefaultValue(xpath, concept, d, expression);
        // assertEquals("liang", d.selectSingleNode(xpath).getText());

    }

    @SuppressWarnings("unused")
    private Document getDocument(EntityModel entityModel) throws Exception {
        DefVRule defVRule = new DefVRule(entityModel.getMetaDataTypes());
        ViewBean viewBean = new ViewBean();
        viewBean.setBindingEntityModel(entityModel);
        String language = "en";
        org.w3c.dom.Document doc = defVRule.getDefaultXML(viewBean, language);
        return XmlUtil.parseDocument(doc);
    }
}