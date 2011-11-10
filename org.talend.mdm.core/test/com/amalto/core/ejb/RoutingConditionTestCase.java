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
package com.amalto.core.ejb;

import junit.framework.TestCase;


/**
 * DOC achen  class global comment. Detailled comment
 */
@SuppressWarnings("nls")
public class RoutingConditionTestCase extends TestCase {

    private String compileCondition(String condition) {
        String compileConditon = condition;
        compileConditon = compileConditon.replaceAll("\\s*And\\s*|\\s*and\\s*|\\s*AND\\s*", " && "); //$NON-NLS-1$ //$NON-NLS-2$
        compileConditon = compileConditon.replaceAll("\\s*Or\\s*|\\s*or\\s*|\\s*OR\\s*", " || "); //$NON-NLS-1$//$NON-NLS-2$
        compileConditon = compileConditon.replaceAll("\\s*Not\\s*|\\s*not\\s*|\\s*NOT\\s*", " !"); //$NON-NLS-1$ //$NON-NLS-2$
        return compileConditon;
    }

    public void testRoutingRuleCondition() {
        String condition = "C1 And Not(C2)";
        condition = compileCondition(condition);
        assertEquals("C1 && !(C2)", condition);

        condition = "C1 and C2 And C3";
        condition = compileCondition(condition);
        assertEquals("C1 && C2 && C3", condition);

        condition = "(C1 And C2) or C3";
        condition = compileCondition(condition);
        assertEquals("(C1 && C2) || C3", condition);


        condition = "C1 and (C2 OR C3)";
        condition = compileCondition(condition);
        assertEquals("C1 && (C2 || C3)", condition);

    }

}
