package org.talend.mdm.webapp.browserecords.client.util;


import junit.framework.TestCase;

public class LabelUtilTest extends TestCase {


    public void testabc() {
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("'"), "&#39;"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("\""), "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
    }

}
