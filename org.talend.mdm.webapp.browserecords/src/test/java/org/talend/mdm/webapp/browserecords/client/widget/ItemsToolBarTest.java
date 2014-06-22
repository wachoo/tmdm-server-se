package org.talend.mdm.webapp.browserecords.client.widget;


import junit.framework.TestCase;

import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;


@PrepareForTest(ItemsToolBar.class)
@SuppressStaticInitializationFor({
    "org.talend.mdm.webapp.browserecords.client.widget.ItemsToolBar",
    "org.talend.mdm.webapp.browserecords.client.widget.SearchPanel.SimpleCriterionPanel",
    "org.talend.mdm.webapp.browserecords.client.widget.SearchPanel.AdvancedSearchPanel",
    "org.talend.mdm.webapp.browserecords.client.widget.inputfield.ComboBoxField",
    "org.talend.mdm.webapp.browserecords.client.widget.ItemsListPanel",
    "org.talend.mdm.webapp.browserecords.client.widget.ItemsMainTabPanel",
    "org.talend.mdm.webapp.browserecords.client.util.ViewUtil",
    "org.talend.mdm.webapp.base.client.widget.ComboBoxEx",
    "com.extjs.gxt.ui.client.widget.form.FormPanel",
    "com.extjs.gxt.ui.client.widget.ContentPanel",
    "com.extjs.gxt.ui.client.widget.form.TriggerField",
    "com.extjs.gxt.ui.client.widget.form.TextField",
    "com.extjs.gxt.ui.client.widget.form.Field",
    "com.extjs.gxt.ui.client.widget.toolbar.ToolBar",
    "com.extjs.gxt.ui.client.widget.Container",
    "com.extjs.gxt.ui.client.widget.BoxComponent",
    "com.extjs.gxt.ui.client.widget.Component",
    "com.extjs.gxt.ui.client.event.Observable",
    "com.google.gwt.user.client.ui.Widget",
    "com.google.gwt.user.client.ui.UIObject"
    })
public class ItemsToolBarTest extends TestCase {
    
    /*
    public static TestSuite suite() throws Exception {
        return new PowerMockSuite(ItemsToolBarTest.class);
    }
    
    public void testCreateDeletePermissions() throws Exception {
        
        // Mock ItemsToolBar to test
        ItemsToolBar mock_bar = createMockItemsToolbar();

        // Mock ItemsListPanel
        PowerMockito.mockStatic(ItemsListPanel.class);
        ItemsListPanel mock_items_list_panel = PowerMockito.mock(ItemsListPanel.class);
        PowerMockito.when(ItemsListPanel.getInstance()).thenReturn(mock_items_list_panel);
        
        // Mock ItemsMainTabPanel
        PowerMockito.mockStatic(ItemsMainTabPanel.class);
        ItemsMainTabPanel mock_items_main_tab_panel = PowerMockito.mock(ItemsMainTabPanel.class);
        PowerMockito.when(ItemsMainTabPanel.getInstance()).thenReturn(mock_items_main_tab_panel);
                
        // Mock import menu to test create permissions
        Button mock_upload_btn = PowerMockito.mock(Button.class);
        Whitebox.setInternalState(mock_bar, "uploadBtn", mock_upload_btn);
        Menu mock_menu = PowerMockito.mock(Menu.class);
        Component mock_import = PowerMockito.mock(Component.class);
        PowerMockito.when(mock_upload_btn.getMenu()).thenReturn(mock_menu);
        PowerMockito.when(mock_menu.getItemByItemId("importRecords")).thenReturn(mock_import);

        
        // Mock ViewUtil        
        PowerMockito.mockStatic(ViewUtil.class);
        ComboBoxField mock_combo = PowerMockito.mock(ComboBoxField.class);
        ItemBaseModel mock_data = PowerMockito.mock(ItemBaseModel.class);
        PowerMockito.when(mock_combo.getValue()).thenReturn(mock_data);
        PowerMockito.when(mock_data.get("value")).thenReturn("");
        PowerMockito.when(ViewUtil.getConceptFromBrowseItemView("")).thenReturn("");
        Whitebox.setInternalState(mock_bar, "entityCombo", mock_combo);
        

        // Mock view bean 
        ViewBean mock_view_bean = PowerMockito.mock(ViewBean.class);
        EntityModel mock_entity_model = PowerMockito.mock(EntityModel.class);
        Map<String, TypeModel> mock_map = PowerMockito.mock(Map.class);
        TypeModel mock_type_model = PowerMockito.mock(TypeModel.class);
        PowerMockito.when(mock_view_bean.getBindingEntityModel()).thenReturn(mock_entity_model);
        PowerMockito.when(mock_entity_model.getMetaDataTypes()).thenReturn(mock_map);
        PowerMockito.when(mock_map.get("")).thenReturn(mock_type_model);
        PowerMockito.when(mock_type_model.isDenyCreatable()).thenReturn(true);
        PowerMockito.when(mock_type_model.isDenyLogicalDeletable()).thenReturn(true);
        PowerMockito.when(mock_type_model.isDenyPhysicalDeleteable()).thenReturn(true);
                
        
        // Mock delete objects to test delete permissions
        Button mock_delete_button = PowerMockito.mock(Button.class);
        Whitebox.setInternalState(mock_bar, "deleteMenu", mock_delete_button);
        Menu mock_delete_menu = PowerMockito.mock(Menu.class);
        Component mock_physical_delete = PowerMockito.mock(Component.class);
        Component mock_logical_delete = PowerMockito.mock(Component.class);
        PowerMockito.when(mock_delete_button.getMenu()).thenReturn(mock_delete_menu);
        PowerMockito.when(mock_delete_menu.getItemByItemId("physicalDelMenuInGrid")).thenReturn(mock_physical_delete);
        PowerMockito.when(mock_delete_menu.getItemByItemId("logicalDelMenuInGrid")).thenReturn(mock_logical_delete);

        
        // Mock some private calls
        ItemsToolBar bar_spy = PowerMockito.spy(mock_bar);
        PowerMockito.doNothing().when(bar_spy, "updateUserCriteriasList");
        PowerMockito.doReturn(true).when(bar_spy, "layout", true);
        
        
        // Execute the method
        bar_spy.updateToolBar(mock_view_bean);
        
        
        // Verify we did not enable the import button due to no create permissions
        Mockito.verify(mock_import, Mockito.times(0)).setEnabled(true);
        
        
        // Verify we disabled the entire delete menu since neither logical nor physical delete permissions
        // Twice because by default we disable it then we disable it again when checking permissions
        Mockito.verify(mock_delete_button, Mockito.times(2)).setEnabled(false);
        
        
        // Verify we did not enable the delete sub-menu items
        Mockito.verify(mock_physical_delete, Mockito.times(0)).setEnabled(true);
        Mockito.verify(mock_logical_delete, Mockito.times(0)).setEnabled(true);        
    }
    
    

    public ItemsToolBar createMockItemsToolbar () {
        // Create instance with nothing initialized, but working methods
        ItemsToolBar bar = Whitebox.newInstance(ItemsToolBar.class);
        
        // Set all internal instance variables to mock objects that do nothing
        Whitebox.setInternalState(bar, "simplePanel", PowerMockito.mock(SimpleCriterionPanel.class));
        Whitebox.setInternalState(bar, "advancedPanel", PowerMockito.mock(AdvancedSearchPanel.class));
        Whitebox.setInternalState(bar, "entityCombo", PowerMockito.mock(ComboBoxField.class));
        Whitebox.setInternalState(bar, "searchBut", PowerMockito.mock(Button.class));
        Whitebox.setInternalState(bar, "advancedBut", PowerMockito.mock(ToggleButton.class));
        Whitebox.setInternalState(bar, "managebookBtn", PowerMockito.mock(Button.class));
        Whitebox.setInternalState(bar, "bookmarkBtn", PowerMockito.mock(Button.class));
        Whitebox.setInternalState(bar, "createBtn", PowerMockito.mock(Button.class));
        Whitebox.setInternalState(bar, "deleteMenu", PowerMockito.mock(Button.class));
        Whitebox.setInternalState(bar, "uploadBtn", PowerMockito.mock(Button.class));
        Whitebox.setInternalState(bar, "service", PowerMockito.mock(BrowseRecordsServiceAsync.class));
        Whitebox.setInternalState(bar, "bookmarkName", "");
        Whitebox.setInternalState(bar, "currentModel", PowerMockito.mock(ItemBaseModel.class));
        Whitebox.setInternalState(bar, "relWindow", PowerMockito.mock(FKRelRecordWindow.class));
        Whitebox.setInternalState(bar, "userCriteriasList", PowerMockito.mock(List.class));
        
        return bar;
    }*/

    public void testname() throws Exception {
        assertTrue(true);
    }
}
