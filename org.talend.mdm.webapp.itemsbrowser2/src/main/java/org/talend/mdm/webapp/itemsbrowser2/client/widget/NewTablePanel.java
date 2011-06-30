// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rthis.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class NewTablePanel extends ContentPanel {

    private static NewTablePanel panel = null;

    private int fieldCount = 1;

    private String[] fieldArray = null;

    private String[] keyArray = null;

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    private NewTablePanel() {
        this.setCollapsible(true);
        this.setFrame(false);
        this.setHeaderVisible(false);
        this.setWidth("100%"); //$NON-NLS-1$
        this.setLayout(new FitLayout());
        this.setBodyBorder(false);
        this.setBorders(false);
        this.setScrollMode(Scroll.AUTO);

        this.addFormPanel();
    }

    public static NewTablePanel getInstance() {
        if (panel == null)
            panel = new NewTablePanel();
        return panel;
    }

    private void addFormPanel() {

        final FormData formData = new FormData("100%"); //$NON-NLS-1$
        final FormPanel addPanel = new FormPanel();
        addPanel.setCollapsible(false);
        addPanel.setFrame(false);
        addPanel.setHeaderVisible(false);
        addPanel.setWidth("100%"); //$NON-NLS-1$
        addPanel.setLabelWidth(200);
        addPanel.setScrollMode(Scroll.AUTO);

        final LayoutContainer main = new LayoutContainer();
        main.setLayout(new ColumnLayout());

        final TextField<String> tableName = new TextField<String>();
        tableName.setFieldLabel(MessagesFactory.getMessages().label_field_table_name());
        addPanel.add(tableName);

        final LayoutContainer left = new LayoutContainer();
        left.setStyleAttribute("paddingRight", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        FormLayout layout = new FormLayout();
        left.setLayout(layout);

        TextField<String> field1 = new TextField<String>();
        field1.setFieldLabel("Field " + fieldCount); //$NON-NLS-1$
        field1.setId("field" + fieldCount); //$NON-NLS-1$
        field1.setHeight(25);
        left.add(field1, formData);

        final LayoutContainer right = new LayoutContainer();
        right.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        layout = new FormLayout();
        right.setLayout(layout);

        CheckBox keycb = new CheckBox();
        keycb.setFieldLabel("Key"); //$NON-NLS-1$
        keycb.setId("key" + fieldCount); //$NON-NLS-1$
        right.add(keycb, formData);

        main.add(left, new com.extjs.gxt.ui.client.widget.layout.ColumnData(.347));
        main.add(right, new com.extjs.gxt.ui.client.widget.layout.ColumnData(.347));
        addPanel.add(main, new FormData("100%")); //$NON-NLS-1$

        this.add(addPanel);
        ToolBar tb = new ToolBar();
        tb.setWidth("100%"); //$NON-NLS-1$
        tb.add(new Button(MessagesFactory.getMessages().save_btn(), new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (tableName == null || !isValidValue(tableName.getValue().trim())) {
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), "", null);
                    return;
                }

                String validResult = ifValidField(left);
                if (validResult != null) {
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), "", null);
                    return;
                }

                if (ifEmptyKey(right)) {
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), "", null);
                    return;
                }

                service.addNewTable(tableName.getValue(), fieldArray, keyArray, new AsyncCallback<Void>() {

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                    }

                    public void onSuccess(Void arg0) {
                        MessageBox.alert("123", "123", null);
                    }
                });

            }

        }));
        tb.add(new SeparatorToolItem());
        tb.add(new Button("Add a Field", new SelectionListener<ButtonEvent>() { //$NON-NLS-1$

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        fieldCount = fieldCount + 1;
                        TextField<String> f = new TextField<String>();
                        f.setFieldLabel("Field " + fieldCount); //$NON-NLS-1$
                        f.setId("field" + fieldCount); //$NON-NLS-1$
                        f.setHeight(25);
                        left.add(f, formData);

                        CheckBox kcb = new CheckBox();
                        kcb.setFieldLabel("Key"); //$NON-NLS-1$
                        kcb.setId("key" + fieldCount); //$NON-NLS-1$
                        right.add(kcb, formData);

                        main.layout();
                        addPanel.layout();
                    }
                }));
        this.setBottomComponent(tb);
    }

    private boolean ifEmptyKey(LayoutContainer container) {
        boolean ifempty = true;
        keyArray = new String[container.getItemCount()];
        int i = 0;
        for (Component c : container.getItems()) {
            keyArray[i] = ((Field) c).getValue().toString();
            if (keyArray[i].equals("true")) //$NON-NLS-1$
                ifempty = false;
            i++;
        }
        return ifempty;
    }

    private String ifValidField(LayoutContainer container) {
        fieldArray = new String[container.getItemCount()];
        int i = 0;
        for (Component c : container.getItems()) {
            fieldArray[i] = ((Field) c).getValue().toString();
            if (!isValidValue(fieldArray[i]))
                return fieldArray[i];
            i++;
        }
        return null;
    }

    public native boolean isValidValue(String inputValue) /*-{
        var re = new RegExp('[a-zA-Z][a-zA-Z0-9]*');
        var m = re.exec(inputValue);
        if (m == null)
        return false;
        else
        return true;
    }-*/;


}
