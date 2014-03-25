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
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.junit.client.GWTTestCase;
import com.google.gwt.user.client.ui.RootPanel;

public class ForeignKeyListWindowGWTTest extends GWTTestCase {

    public void testFilterValue() {

        ForeignKeyListWindow foreignKeyListWindow = new ForeignKeyListWindow();
        TextField<String> filter = new TextField<String>();
        RootPanel.get().add(filter);

        filter.setRawValue(""); //$NON-NLS-1$
        _setFilter(foreignKeyListWindow, filter);
        assertEquals(".*", _getFilterRawValue(foreignKeyListWindow)); //$NON-NLS-1$            

        filter.setRawValue("/"); //$NON-NLS-1$
        _setFilter(foreignKeyListWindow, filter);
        assertEquals("'/'", _getFilterRawValue(foreignKeyListWindow)); //$NON-NLS-1$                 

        filter.setRawValue("1/2"); //$NON-NLS-1$
        assertEquals("'1/2'", _getFilterRawValue(foreignKeyListWindow)); //$NON-NLS-1$
    }

    public native void _setFilter(ForeignKeyListWindow foreignKeyListWindow, TextField<String> filter)/*-{
        foreignKeyListWindow.@org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyListWindow::filter = filter;
    }-*/;

    public native String _getFilterRawValue(ForeignKeyListWindow foreignKeyListWindow)/*-{
        return foreignKeyListWindow.@org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyListWindow::getFilterValue()();
    }-*/;

    @Override
    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }

}
