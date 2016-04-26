package org.talend.mdm.webapp.browserecords.client.util;

import java.math.BigDecimal;

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

        assertNull(FormatUtil.changeNumberToFormatedValue(null)) ;
        assertNotNull(FormatUtil.changeNumberToFormatedValue("2.0"));
        assertEquals(FormatUtil.changeNumberToFormatedValue("2.0"), "2.0");
        assertEquals(FormatUtil.changeNumberToFormatedValue("2.0    "), "2.0");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-2.0"), "-2.0");
        assertEquals(FormatUtil.changeNumberToFormatedValue("2.09876672766"), "2.09876672766");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-2.09876672766"), "-2.09876672766");
        assertEquals(FormatUtil.changeNumberToFormatedValue("2.000"), "2.000");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-2.000"), "-2.000");
        assertEquals(FormatUtil.changeNumberToFormatedValue(".000"), "0.000");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-.000"), "-0.000");
        assertEquals(FormatUtil.changeNumberToFormatedValue("0.000"), "0.000");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-0.000"), "-0.000");
        assertEquals(FormatUtil.changeNumberToFormatedValue("2"), "2");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-2"), "-2");
        assertEquals(FormatUtil.changeNumberToFormatedValue("002"), "2");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-002"), "-2");
        assertEquals(FormatUtil.changeNumberToFormatedValue("200"), "200");
        assertEquals(FormatUtil.changeNumberToFormatedValue("-200"), "-200");
    }

    public void testGetDecimalValue(){
        assertNull(FormatUtil.getDecimalValue(null, 3)) ;
        assertEquals(new BigDecimal("15.00"), FormatUtil.getDecimalValue("15", null)) ;
        assertEquals(new BigDecimal("15.000") , FormatUtil.getDecimalValue("15", 3)) ;
        assertEquals(new BigDecimal("15.365") , FormatUtil.getDecimalValue("15.3653", 3)) ;
        assertEquals(new BigDecimal("15.37") , FormatUtil.getDecimalValue("15.3653", null)) ;
        assertEquals(new BigDecimal("15.366") , FormatUtil.getDecimalValue("15.3655", 3)) ;
        assertEquals(new BigDecimal("15.37") , FormatUtil.getDecimalValue("15.3655", null)) ;
        assertEquals(new BigDecimal("-15.000") , FormatUtil.getDecimalValue("-15.000", 3)) ;
        assertEquals(new BigDecimal("-15.00") , FormatUtil.getDecimalValue("-15.000", null)) ;
        assertEquals(new BigDecimal("0.000") , FormatUtil.getDecimalValue("0", 3)) ;
        assertEquals(new BigDecimal("0.00") , FormatUtil.getDecimalValue("0", null)) ;
        assertEquals(new BigDecimal("0.200") , FormatUtil.getDecimalValue("0.20", 3)) ;
        assertEquals(new BigDecimal("0.20") , FormatUtil.getDecimalValue("0.20", null)) ;
        assertEquals(new BigDecimal("0.200") , FormatUtil.getDecimalValue(".20", 3)) ;
        assertEquals(new BigDecimal("0.20") , FormatUtil.getDecimalValue(".20", null)) ;
    }
    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords";
    }
}
