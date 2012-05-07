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
package org.talend.mdm.webapp.browserecords.client.util;

import junit.framework.TestCase;

public class FormatUtilTest  extends TestCase {
    
    public void testConvertHtmlCharacter(){
        StringBuffer valueString =new StringBuffer();
        valueString.append("<div id=\"1\">"); //$NON-NLS-1$
        valueString.append("<font size='10'>"); //$NON-NLS-1$
        valueString.append("font format"); //$NON-NLS-1$
        valueString.append("</font>"); //$NON-NLS-1$
        valueString.append("<div>"); //$NON-NLS-1$
        StringBuffer resultString =new StringBuffer();
        resultString.append("&lt;div id=&quot;1&quot;&gt;"); //$NON-NLS-1$
        resultString.append("&lt;font size=&apos;10&apos;&gt;"); //$NON-NLS-1$
        resultString.append("font format"); //$NON-NLS-1$
        resultString.append("&lt;/font&gt;"); //$NON-NLS-1$
        resultString.append("&lt;div&gt;"); //$NON-NLS-1$
        assertEquals(resultString.toString(),FormatUtil.convertHtmlCharacter(valueString.toString()));
        
           
    }
}
