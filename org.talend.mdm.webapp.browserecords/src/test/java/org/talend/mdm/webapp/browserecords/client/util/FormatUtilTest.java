package org.talend.mdm.webapp.browserecords.client.util;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatNumberField;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.validator.NumberFieldValidator;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;

import com.extjs.gxt.ui.client.Registry;
import com.google.gwt.junit.client.GWTTestCase;


public class FormatUtilTest extends GWTTestCase{

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        UserSession session = new UserSession();
        session.put(UserSession.APP_HEADER, new AppHeader());
        Registry.register(BrowseRecords.USER_SESSION, session);
    }

    public void testChangeToFormatedValue() {
        FormatNumberField numberField = new FormatNumberField();

        numberField.setValidator(NumberFieldValidator.getInstance());

        assertEquals(FormatUtil.changeNumberToFormatedValue("2.0"), "2.0");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-2.0"), "-2.0");
        assertEquals(FormatUtil.changeNumberToFormatedValue("2.09876672766"), "2.09876672766");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-2.09876672766"), "-2.09876672766");
        assertEquals(FormatUtil.changeNumberToFormatedValue("2.000"), "2.000");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-2.000"), "-2.000");
    }

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords";
    }
}
