// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.HorizontalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class AdvancedSearchPanel extends ContentPanel {

    public AdvancedSearchPanel() {
        setHeaderVisible(false);
        setLayout(new FitLayout());

        FormPanel content = new FormPanel();
        content.setFrame(false);
        content.setBodyBorder(false);
        content.setHeaderVisible(false);
        content.setScrollMode(Scroll.AUTO);

        HorizontalPanel hp = new HorizontalPanel();

        TextField<String> expressionTextField = new TextField<String>();
        expressionTextField.setFieldLabel("Search Expression");
        // expressionTextField.setValidator(new EmailValidator());
        Button filterButton = new Button("...");
        filterButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                Window winFilter = new Window();
                winFilter.setBodyBorder(false);
                winFilter.setFrame(false);
                winFilter.setLayout(new FitLayout());
                winFilter.setModal(true);

                winFilter.show();
            }
        });

        hp.add(expressionTextField);
        hp.add(filterButton);

        content.add(hp);

        ComboBox<BaseModel> cb = new ComboBox<BaseModel>();
        ListStore<BaseModel> list = new ListStore<BaseModel>();
        BaseModel field = new BaseModel();
        field.set("Created By", "createdby");
        list.add(field);
        field = new BaseModel();
        field.set("Created On", "createdon");
        list.add(field);
        field = new BaseModel();
        field.set("Modified By", "modifiedby");
        list.add(field);
        field = new BaseModel();
        field.set("Modified On", "modifiedon");
        list.add(field);
        cb.setDisplayField("name");
        cb.setValueField("value");
        cb.setStore(list);
        cb.setTriggerAction(TriggerAction.ALL);

        content.add(cb);

        add(content);

    }
}
