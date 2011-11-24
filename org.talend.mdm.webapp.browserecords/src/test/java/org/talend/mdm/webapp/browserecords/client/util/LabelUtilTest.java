package org.talend.mdm.webapp.browserecords.client.util;

import junit.framework.TestCase;

public class LabelUtilTest extends TestCase {

    public void testConvertSpecialHTMLCharacter(){
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("'"), "&apos;"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("\""), "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
}
