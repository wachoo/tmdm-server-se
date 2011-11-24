package org.talend.mdm.webapp.browserecords.client.util;

import junit.framework.TestCase;

import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;

public class LabelUtilTest extends TestCase {

    public void testConvertSpecialHTMLCharacter(){
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("'"), "&acute;"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("\""), "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
}
