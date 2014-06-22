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
package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import java.util.Date;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.util.DateUtil;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("nls")
public class FormatDateFieldGWTTest extends GWTTestCase{

    @Override
    protected void gwtSetUp() throws Exception {
        super.gwtSetUp();
        UserSession session = new UserSession();
        session.put(UserSession.APP_HEADER, new AppHeader());
        Registry.register(BrowseRecords.USER_SESSION, session);
    }
    
    public void testCompareDateAndString() {
		
		// 1. Date (value = "2012-05-08", date = 2012-05-08)
		String value = "2012-05-08";
        FormatDateField dateField = new FormatDateField();
		dateField.setPropertyEditor(new DateTimePropertyEditor(DateUtil.datePattern));
		Date date = DateUtil.convertStringToDate(value.toString());
		assertEquals(false, compareDateAndString(dateField.getPropertyEditor(), date, value));
		
		// 2. Date (value = "2012-05-09", date = 2012-05-08)
		value = "2012-05-09";
		assertEquals(true, compareDateAndString(dateField.getPropertyEditor(), date, value));
		
		// 3. DateTime (value = "2012-05-08T00:00:00", date = 2012-05-08T00:00:00)
		value = "2012-05-08T00:00:00";
		dateField.setPropertyEditor(new DateTimePropertyEditor(DateUtil.formatDateTimePattern));
		date = DateUtil.convertStringToDate(DateUtil.dateTimePattern,value.toString());
        dateField.setValue(date);
		assertEquals(false, compareDateAndString(dateField.getPropertyEditor(), date, value));
		
		// 4. DateTime (value = "2012-05-09T00:00:00", date = 2012-05-08T00:00:00)
		value = "2012-05-09T00:00:00";
		assertEquals(true, compareDateAndString(dateField.getPropertyEditor(), date, value));
		
		// 5. value = date = null;
		assertEquals(false, compareDateAndString(dateField.getPropertyEditor(), null, null));
		
		// 6. value = null, date != null
		assertEquals(true, compareDateAndString(dateField.getPropertyEditor(), date, null));
		
		// 7. value != null, date = null
		assertEquals(true, compareDateAndString(dateField.getPropertyEditor(), null, value));
		
	}
	
	private boolean compareDateAndString(DateTimePropertyEditor propertyEditor, Date date, String objectValue) {
		if (date == null && objectValue == null)
			return false;
		if (date != null && objectValue == null)
			return true;
		if (date == null && objectValue != null)
			return true;
		// convert date to string according to the DateTimePropertyEditor
		String str = propertyEditor.getStringValue(date);
		if (str.equalsIgnoreCase(objectValue))
			return false;
		else
			return true;
	}
	
	public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords";
    }
	
}