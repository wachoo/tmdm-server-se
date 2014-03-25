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
package org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.util.DateUtil;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatDateField;
import org.talend.mdm.webapp.browserecords.shared.AppHeader;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.NumberField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridSelectionModel;
import com.google.gwt.junit.client.GWTTestCase;

@SuppressWarnings("nls")
public class FormatCellEditorGWTTest extends GWTTestCase {

    Field<?> field;

    CellEditor cellEditor;

    final String xml = "<result>1<subelement>1111</subelement><name>qqqqq8</name><d1>Thu Nov 01 00:00:00 CST 2012</d1><dt1>Fri Nov 02 12:00:00 CST 2012</dt1><num>055</num></result>";

    ItemBean selectedItem;

    @Override
    protected void gwtSetUp() throws Exception {
        mockGrid();
    }

    public void testNumberFieldCellEditor() {
        field = new NumberField();
        field.setName("FormatTest/num");
        field.setData("numberType", "integer");
        cellEditor = new NumberFieldCellEditor((NumberField) field);

        assertEquals(55, selectedItem.getOriginalMap().get(field.getName()));
        
        Object num = cellEditor.preProcessValue("55"); //$NON-NLS-1$
        assertEquals(55, num);

        String numStr = (String) cellEditor.postProcessValue(66);
        assertEquals("66", numStr);
        assertEquals(66, selectedItem.getOriginalMap().get(field.getName()));

        num = cellEditor.preProcessValue("66"); //$NON-NLS-1$
        assertEquals(66, num);

        numStr = (String) cellEditor.postProcessValue(77);
        assertEquals("77", numStr);
        assertEquals(77, selectedItem.getOriginalMap().get(field.getName()));
    }

    public void testDateFieldCellEditor() {
        UserSession session = new UserSession();
        AppHeader appHeader = new AppHeader();
        appHeader.setAutoValidate(false);
        session.put(UserSession.APP_HEADER, appHeader);
        Registry.register(BrowseRecords.USER_SESSION, session);

        field = new FormatDateField();
        field.setName("FormatTest/d1");
        cellEditor = new DateFieldCellEditor((FormatDateField) field);

        assertEquals(DateUtil.tryConvertStringToDate("2012-11-01"), selectedItem.getOriginalMap().get(field.getName()));

        Date date = (Date) cellEditor.preProcessValue("Thu Nov 01 00:00:00 CST 2012");
        assertEquals(DateUtil.tryConvertStringToDate("2012-11-01"), date);


        String dateStr = (String) cellEditor.postProcessValue(DateUtil.tryConvertStringToDate("2012-12-02"));
        assertEquals("2012-12-02", dateStr);
        assertEquals(DateUtil.tryConvertStringToDate("2012-12-02"), selectedItem.getOriginalMap().get(field.getName()));

        date = (Date) cellEditor.preProcessValue("Sun Dec 02 00:00:00 CST 2012");
        assertEquals(DateUtil.tryConvertStringToDate("2012-12-02"), date);

        dateStr = (String) cellEditor.postProcessValue(DateUtil.tryConvertStringToDate("2012-12-03"));
        assertEquals("2012-12-03", dateStr);
        assertEquals(DateUtil.tryConvertStringToDate("2012-12-03"), selectedItem.getOriginalMap().get(field.getName()));
    }

    public void testDateTimeFieldCellEditor() {
        UserSession session = new UserSession();
        AppHeader appHeader = new AppHeader();
        appHeader.setAutoValidate(false);
        session.put(UserSession.APP_HEADER, appHeader);
        Registry.register(BrowseRecords.USER_SESSION, session);

        field = new FormatDateField();
        field.setName("FormatTest/dt1");
        cellEditor = new DateTimeFieldCellEditor((FormatDateField) field);

        assertEquals(DateUtil.tryConvertStringToDate("2012-11-02T12:00:00"), selectedItem.getOriginalMap().get(field.getName()));

        Date date = (Date) cellEditor.preProcessValue("Fri Nov 02 12:00:00 CST 2012");
        assertEquals(DateUtil.tryConvertStringToDate("2012-11-02T12:00:00"), date);

        String dateStr = (String) cellEditor.postProcessValue(DateUtil.tryConvertStringToDate("2012-12-02T12:00:00"));
        assertEquals("2012-12-02T12:00:00", dateStr);
        assertEquals(DateUtil.tryConvertStringToDate("2012-12-02T12:00:00"), selectedItem.getOriginalMap().get(field.getName()));

        date = (Date) cellEditor.preProcessValue("Sun Dec 02 12:00:00 CST 2012");
        assertEquals(DateUtil.tryConvertStringToDate("2012-12-02T12:00:00"), date);

        dateStr = (String) cellEditor.postProcessValue(DateUtil.tryConvertStringToDate("2012-12-03T12:30:55"));
        assertEquals("2012-12-03T12:30:55", dateStr);
        assertEquals(DateUtil.tryConvertStringToDate("2012-12-03T12:30:55"), selectedItem.getOriginalMap().get(field.getName()));
    }

    private void mockGrid() {
        selectedItem = new ItemBean("FormatTest", "111", xml);
        Map<String, Object> originalMap = new HashMap<String, Object>();
        originalMap.put("FormatTest/dt1", DateUtil.tryConvertStringToDate("2012-11-02T12:00:00"));
        originalMap.put("FormatTest/d1", DateUtil.tryConvertStringToDate("2012-11-01"));
        originalMap.put("FormatTest/num", 55);
        selectedItem.setOriginalMap(originalMap);
        Map<String, String> formatMap = new HashMap<String, String>();
        formatMap.put("FormatTest/dt1", "Fri Nov 02 12:00:00 CST 2012");
        formatMap.put("FormatTest/d1", "Thu Nov 01 00:00:00 CST 2012");
        formatMap.put("FormatTest/num", "055");
        selectedItem.setFormateMap(formatMap);
        List<ColumnConfig> conlumns = new ArrayList<ColumnConfig>();
        ColumnModel cm = new ColumnModel(conlumns);
        
        ListStore<ItemBean> store = new ListStore<ItemBean>();
        Grid<ItemBean> grid = new Grid<ItemBean>(store, cm);
        grid.setSelectionModel(new GridSelectionModel<ItemBean>() {
            public ItemBean getSelectedItem() {
                return selectedItem;
            }
        });
        injectGrid(ItemsListPanel.getInstance(), grid);
    }

    private native void injectGrid(ItemsListPanel listPanel, Grid<ItemBean> grid)/*-{
		listPanel.@org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel::grid = grid;
    }-*/;

    public String getModuleName() {
        return "org.talend.mdm.webapp.browserecords.TestBrowseRecords"; //$NON-NLS-1$
    }
}
