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
package org.talend.mdm.webapp.general.server.util;

import java.util.List;

import junit.framework.TestCase;

import org.talend.mdm.webapp.general.model.LanguageBean;

@SuppressWarnings("nls")
public class UtilsTest extends TestCase {

    public void testGetLanguages() throws Exception {
        List<LanguageBean> langs = Utils.getLanguages("en");
        assertEquals(2, langs.size());
        
        LanguageBean lang = langs.get(0);
        assertEquals("en", lang.getValue());
        assertEquals("English", lang.getText());
        assertTrue(lang.isSelected());

        lang = langs.get(1);
        assertEquals("fr", lang.getValue());
        assertEquals("Francais", lang.getText());
        assertFalse(lang.isSelected());
    }
}
