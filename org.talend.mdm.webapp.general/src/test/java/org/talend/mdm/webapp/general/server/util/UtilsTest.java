// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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
import org.talend.mdm.webapp.general.server.actions.GeneralAction;



@SuppressWarnings("nls")
public class UtilsTest extends TestCase {

    public void testGetLanguages() throws Exception {

        List<LanguageBean> langs = (new GeneralAction()).getLanguages("en");
        assertTrue(langs.size() > 0);
        
        LanguageBean lang = langs.get(0);
        assertEquals("en", lang.getValue());
        assertEquals("English", lang.getText());
        assertEquals("MM/dd/yyyy HH:mm:ss", lang.getDateTimeFormat());
        assertTrue(lang.isSelected());

        lang = langs.get(1);
        assertEquals("fr", lang.getValue());
        assertEquals("Français", lang.getText());
        assertFalse(lang.isSelected());
        
    }

}
