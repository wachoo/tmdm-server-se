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

import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class AdvancedSearchPanel extends ContentPanel {

    private ViewBean view;

    private TextField<String> expressionTextField;

    final private void setCriteria(String c) {
        expressionTextField.setValue(c);
    }

    public String getCriteria() {
        return expressionTextField.getValue();
    }

    public AdvancedSearchPanel(ViewBean viewbean) {
        this.view = viewbean;
        setHeaderVisible(false);
        setLayout(new FitLayout());

        final FormPanel content = new FormPanel();
        content.setFrame(false);
        content.setBodyBorder(false);
        content.setHeaderVisible(false);
        content.setScrollMode(Scroll.AUTO);
        content.setLabelWidth(120);
        content.setAutoHeight(true);

        final FormData formData = new FormData("-10");

        final Button filterButton = new Button();
        filterButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Edit()));
        filterButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                final Window winFilter = new Window();
                // winFilter.setBodyBorder(false);
                // winFilter.setFrame(false);
                winFilter.setHeading("Advanced Filter");
                winFilter.setModal(true);
                winFilter.setAutoHeight(true);
                winFilter.setAutoWidth(true);
                // winFilter.setWidth(400);
                ContentPanel root = new ContentPanel();
                root.addStyleName("filter-panel");
                root.setAutoHeight(true);
                root.setHeaderVisible(false);
                root.setBodyBorder(false);
                root.setFrame(false);
                final MultipleCriteriaPanel multiCriteria = new MultipleCriteriaPanel(null, view);
                root.add(multiCriteria);
                winFilter.add(root);

                Button searchBtn = new Button("OK");
                searchBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        // TODO Auto-generated method stub
                        setCriteria(multiCriteria.getCriteria().toString());
                        winFilter.close();
                    }

                });
                winFilter.addButton(searchBtn);
                winFilter.show();
                DOM.setStyleAttribute(winFilter.getBody().dom, "backgroundColor", "white");
            }
        });

        expressionTextField = new TextField<String>() {

            protected void onRender(Element target, int index) {
                // add button
                El wrap = new El(DOM.createDiv());
                wrap.addStyleName("x-form-field-wrap");
                wrap.addStyleName("x-form-file-wrap");

                input = new El(DOM.createInputText());
                input.addStyleName(fieldStyle);
                input.addStyleName("x-form-file-text");
                input.setId(XDOM.getUniqueId());

                if (GXT.isIE && target.getTagName().equals("TD")) {
                    input.setStyleAttribute("position", "static");
                }

                wrap.appendChild(input.dom);

                setElement(wrap.dom, target, index);

                filterButton.addStyleName("x-form-file-btn");
                filterButton.render(wrap.dom);

                super.onRender(target, index);
            }

            protected void onResize(int width, int height) {
                super.onResize(width, height);
                input.setWidth(width - filterButton.getWidth() - 3, true);
            }

            protected void doAttachChildren() {
                super.doAttachChildren();
                ComponentHelper.doAttach(filterButton);
            }

            protected void doDetachChildren() {
                super.doDetachChildren();
                ComponentHelper.doDetach(filterButton);
            }

        };
        expressionTextField.setFieldLabel("Search Expression");
        expressionTextField.setWidth("65%");
        content.add(expressionTextField, formData);

        ComboBox<BaseModel> cb = new ComboBox<BaseModel>();
        cb.setEditable(false);
        cb.setWidth(120);
        cb.setFieldLabel("Add more criteria");
        ListStore<BaseModel> list = new ListStore<BaseModel>();
        BaseModel field = new BaseModel();
        field.set("name", "Created By");
        field.set("value", "createdby");
        list.add(field);
        field = new BaseModel();
        field.set("name", "Created On");
        field.set("value", "createdon");
        list.add(field);
        field = new BaseModel();
        field.set("name", "Modified By");
        field.set("value", "modifiedby");
        list.add(field);
        field = new BaseModel();
        field.set("name", "Modified On");
        field.set("value", "modifiedon");
        list.add(field);
        cb.setDisplayField("name");
        cb.setValueField("value");
        cb.setStore(list);
        cb.setTriggerAction(TriggerAction.ALL);

        cb.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

            public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                // TODO Auto-generated method stub
                String selvalue = se.getSelectedItem().get("value");
                if (selvalue.equals("createdby") && content.getItemByItemId("createdbyField") == null) {
                    TextField<String> createdbyField = new TextField<String>();
                    createdbyField.setFieldLabel("Created By");
                    createdbyField.setId("createdbyField");
                    content.insert(createdbyField, content.getItemCount() - 1, formData);
                } else if (selvalue.equals("createdon") && content.getItemByItemId("createdonField") == null) {
                    DateField createdonField = new DateField();
                    createdonField.setFieldLabel("Created On");
                    createdonField.setId("createdonField");
                    content.insert(createdonField, content.getItemCount() - 1, formData);
                } else if (selvalue.equals("modifiedby") && content.getItemByItemId("modifedbyField") == null) {
                    TextField<String> modifiedbyField = new TextField<String>();
                    modifiedbyField.setFieldLabel("Modified By");
                    modifiedbyField.setId("modifedbyField");
                    content.insert(modifiedbyField, content.getItemCount() - 1, formData);
                } else if (selvalue.equals("modifiedon") && content.getItemByItemId("modifiedonField") == null) {
                    DateField modifiedonField = new DateField();
                    modifiedonField.setFieldLabel("Modified On");
                    modifiedonField.setId("modifiedonField");
                    content.insert(modifiedonField, content.getItemCount() - 1, formData);
                }
                content.layout(true);
            }

        });

        content.add(cb, formData);

        add(content);

    }
}
