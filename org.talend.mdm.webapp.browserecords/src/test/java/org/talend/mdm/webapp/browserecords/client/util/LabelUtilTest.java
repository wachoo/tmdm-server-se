package org.talend.mdm.webapp.browserecords.client.util;

import com.google.gwt.junit.client.GWTTestCase;

public class LabelUtilTest extends GWTTestCase {

    public void testConvertSpecialHTMLCharacter(){
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("'"), "&acute;"); //$NON-NLS-1$ //$NON-NLS-2$
        assertEquals(LabelUtil.convertSpecialHTMLCharacter("\""), "&quot;"); //$NON-NLS-1$ //$NON-NLS-2$
    }
    
    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }    
}
