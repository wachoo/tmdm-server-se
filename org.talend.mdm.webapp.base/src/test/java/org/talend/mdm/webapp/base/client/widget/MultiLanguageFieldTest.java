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
package org.talend.mdm.webapp.base.client.widget;

import org.talend.mdm.webapp.base.client.model.MultiLanguageModel;
import org.talend.mdm.webapp.base.shared.OperatorValueConstants;

import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("nls")
public class MultiLanguageFieldTest extends GWTTestCase {

    private MultiLanguageField field;

    private String value;

    private String operator;

    @Override
    protected void gwtSetUp() throws Exception {
        field = new MultiLanguageField();
        value = "Talend";
        field.setValue(value);
        super.gwtSetUp();
    }

    public void testGetValueWithLanguage() {
        // 1. operator = CONTAINS
        operator = OperatorValueConstants.CONTAINS;
        String formatValue = field.getValueWithLanguage(operator);
        assertEquals("*[EN:*Talend*]*", formatValue);
        // 2. operator = STRICTWITH
        operator = OperatorValueConstants.STARTSWITH;
        formatValue = field.getValueWithLanguage(operator);
        assertEquals("*[EN:Talend*]*", formatValue);
        // 3. operator = STRICTCONTAINS
        operator = OperatorValueConstants.STRICTCONTAINS;
        formatValue = field.getValueWithLanguage(operator);
        assertEquals("*[EN:*Talend*]*", formatValue);
    }

    public void testGetInputValue() {
        // 1. operator = CONTAINS
        operator = OperatorValueConstants.CONTAINS;
        String formatValue = "*[EN:*Talend*]*";
        String inputValue = field.getInputValue(operator, formatValue);
        assertEquals("Talend", inputValue);
        // 2. operator = STRICTWITH
        operator = OperatorValueConstants.STARTSWITH;
        formatValue = "*[EN:Talend*]*";
        inputValue = field.getInputValue(operator, formatValue);
        assertEquals("Talend", inputValue);
        // 3. operator = STRICTCONTAINS
        operator = OperatorValueConstants.STRICTCONTAINS;
        formatValue = "*[EN:*Talend*]*";
        inputValue = field.getInputValue(operator, formatValue);
        assertEquals("Talend", inputValue);
    }

    public void testClear() {
        String multiLanguageString = "[EN:Talend England][FR:Talend Franch]";
        MultiLanguageModel multiLanguageModel = new MultiLanguageModel(multiLanguageString);
        field = new MultiLanguageField(multiLanguageModel);
        field.setValue("Talend England");
        assertEquals(multiLanguageString, field.getMultiLanguageStringValue());
        // clear
        field.clear();
        assertTrue(field.getMultiLanguageStringValue().trim().length() == 0);
    }

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.base.TestBase";
    }

}
