package org.talend.mdm.webapp.browserecords.client.util;


import junit.framework.TestCase;

public class LabelUtilTest extends TestCase {


    public void testabc() {

        assertEquals(LabelUtil.convertSpecialHTMLCharacterByBrowser("'", false), "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(LabelUtil.convertSpecialHTMLCharacterByBrowser("'", true), "&#39;"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(LabelUtil.convertSpecialHTMLCharacterByBrowser("\"", false), "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(LabelUtil.convertSpecialHTMLCharacterByBrowser("\"", true), "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$

    }

}
