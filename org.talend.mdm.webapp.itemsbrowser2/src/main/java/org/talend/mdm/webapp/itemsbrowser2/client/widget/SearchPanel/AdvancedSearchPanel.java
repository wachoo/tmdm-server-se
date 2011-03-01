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

import java.util.Date;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.Parser;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ParserException;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
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
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class AdvancedSearchPanel extends FormPanel {

    private ViewBean view;

    private TextField<String> expressionTextField;

    private ComboBox<BaseModel> cb;

    private AdvancedSearchPanel instance = this;

    private static String ge = "GREATER_THAN_OR_EQUAL";

    private static String le = "LOWER_THAN_OR_EQUAL";

    final public void setCriteria(String c) {
        if (c.indexOf("../../t") > -1) {// modified on condition
            String express = c.substring(0, c.indexOf("../../t") - 5) + ")";
            expressionTextField.setValue(express);
            String condition = c.substring(c.indexOf("../../t"), c.length() - 1);
            if (instance.getItemByItemId("modifiedon") == null) {
                instance.insert(addCriteriaContainer("modifiedon"), instance.getItemCount() - 1, new FormData("90%"));
                instance.layout(true);
            }
            DateField fromfield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon"))
                    .getItem(0)).getItemByItemId("modifiedonField1");
            DateField tofield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon")).getItem(1))
                    .getItemByItemId("modifiedonField2");
            fromfield.setValue(null);
            tofield.setValue(null);

            if (condition.indexOf(ge) > -1) {
                Date d = new Date();
                int index = condition.indexOf(ge) + ge.length() + 1;
                if (condition.indexOf(" ", index) == -1)
                    d.setTime(Long.valueOf(condition.substring(index)));
                else
                    d.setTime(Long.valueOf(condition.substring(index, condition.indexOf(" ", index))));
                fromfield.setValue(d);
            }
            if (condition.indexOf(le) > -1) {
                Date d = new Date();
                int index = condition.indexOf(le) + le.length() + 1;
                if (condition.indexOf(" ", index) == -1)
                    d.setTime(Long.valueOf(condition.substring(index)));
                else
                    d.setTime(Long.valueOf(condition.substring(index, condition.indexOf(" ", index))));
                tofield.setValue(d);
            }
        } else {
            if (instance.getItemByItemId("modifiedon") != null) {
                instance.remove(instance.getItemByItemId("modifiedon"));
            }
            expressionTextField.setValue(c);
            cb.setValue(null);
        }
    }

    public String getCriteria() {
        String express = expressionTextField.getValue();
        String curCriteria = null, curDate = null;
        if (instance.getItemByItemId("modifiedon") != null) {
            DateField fromfield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon"))
                    .getItem(0)).getItemByItemId("modifiedonField1");
            DateField tofield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon")).getItem(1))
                    .getItemByItemId("modifiedonField2");
            if (fromfield.getValue() != null)
                curDate = "../../t " + ge + " " + fromfield.getValue().getTime();
            if (tofield.getValue() != null)
                if (curDate != null)
                    curDate += " AND ../../t " + le + " " + tofield.getValue().getTime();
                else
                    curDate = "../../t " + le + " " + tofield.getValue().getTime();

            if (curDate != null)
                curCriteria = (express == null) ? curDate : express.substring(0, express.lastIndexOf(")")) + " AND " + curDate
                        + ")";
            else
                curCriteria = (express == null) ? curDate : express;
        } else
            curCriteria = express;

        return curCriteria;
    }

    public void cleanCriteria() {
        setCriteria("");
    }

    private LayoutContainer addCriteriaContainer(String id) {
        LayoutContainer main = new LayoutContainer();
        main.setLayout(new ColumnLayout());
        main.setId("modifiedon");
        if (id.equals("modifiedon")) {
            LayoutContainer left = new LayoutContainer();
            left.setStyleAttribute("paddingRight", "10px");
            FormLayout layout = new FormLayout();
            layout.setLabelWidth(110);
            left.setLayout(layout);
            DateField modifiedonField1 = new DateField();
            modifiedonField1.setWidth(120);
            modifiedonField1.setFieldLabel(MessagesFactory.getMessages().search_modifiedon());
            modifiedonField1.setId("modifiedonField1");
            modifiedonField1.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));
            left.add(modifiedonField1);

            LayoutContainer right = new LayoutContainer();
            right.setStyleAttribute("paddingLeft", "10px");
            layout = new FormLayout();
            layout.setLabelWidth(110);
            right.setLayout(layout);
            DateField modifiedonField2 = new DateField();
            modifiedonField2.setWidth(120);
            modifiedonField2.setFieldLabel(MessagesFactory.getMessages().search_modifiedto());
            modifiedonField2.setId("modifiedonField2");
            modifiedonField2.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd"));
            right.add(modifiedonField2);

            main.add(left, new ColumnData(.5));
            main.add(right, new ColumnData(.5));
        }

        return main;
    }

    public AdvancedSearchPanel(ViewBean viewbean) {
        this.view = viewbean;
        setHeaderVisible(false);
        // setLayout(new FitLayout());

        this.setFrame(true);
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setScrollMode(Scroll.AUTO);
        this.setLabelWidth(110);
        this.setAutoHeight(true);

        final FormData formData = new FormData("-20");

        final Button filterButton = new Button();
        filterButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Edit()));
        filterButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                final Window winFilter = new Window();
                winFilter.setHeading(MessagesFactory.getMessages().advsearch_filter());
                winFilter.setModal(true);
                winFilter.setAutoHeight(true);
                winFilter.setAutoWidth(true);
                ContentPanel root = new ContentPanel();
                root.addStyleName("filter-panel");
                root.setAutoHeight(true);
                root.setHeaderVisible(false);
                root.setBodyBorder(false);
                root.setFrame(false);
                final MultipleCriteriaPanel multiCriteria = new MultipleCriteriaPanel(null, view);
                root.add(multiCriteria);
                winFilter.add(root);

                Button searchBtn = new Button(MessagesFactory.getMessages().ok_btn());
                searchBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        setCriteria(multiCriteria.getCriteria().toString());
                        winFilter.close();
                    }

                });
                winFilter.addButton(searchBtn);
                winFilter.show();
                String curField = expressionTextField.getValue();
                if (curField != null && !curField.equals("")) {
                    try {
                        multiCriteria.setCriteria(Parser.parse(curField));
                    } catch (ParserException e) {
                        Log.error(e.getMessage(), e);
                    }
                }
                DOM.setStyleAttribute(winFilter.getBody().dom, "backgroundColor", "white");
            }
        });

        final Button validButton = new Button();
        validButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Valid()));
        validButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                try {
                    String curField = expressionTextField.getValue();
                    if (curField != null && !curField.equals("")) {
                        Parser.parse(curField);
                        MessageBox.info(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                                .valid_expression(), null);
                    }
                } catch (ParserException e) {
                    Log.error(e.getMessage(), e);
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                            .invalid_expression()
                            + e.getMessage(), null);
                }
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

                filterButton.addStyleName("x-form-filter-btn");
                filterButton.render(wrap.dom);
                validButton.addStyleName("x-form-valid-btn");
                validButton.render(wrap.dom);
                super.onRender(target, index);
            }

            protected void onResize(int width, int height) {
                super.onResize(width, height);
                input.setWidth(width - filterButton.getWidth() - validButton.getWidth() - 6, true);
            }

            protected void doAttachChildren() {
                super.doAttachChildren();
                ComponentHelper.doAttach(filterButton);
                ComponentHelper.doAttach(validButton);
            }

            protected void doDetachChildren() {
                super.doDetachChildren();
                ComponentHelper.doDetach(filterButton);
                ComponentHelper.doDetach(validButton);
            }

        };
        expressionTextField.setFieldLabel(MessagesFactory.getMessages().search_expression());
        // expressionTextField.setAllowBlank(false);
        this.add(expressionTextField, new FormData("80%"));

        cb = new ComboBox<BaseModel>();
        cb.setEditable(false);
        cb.setWidth(120);
        cb.setFieldLabel("Add more criteria");
        cb.setAllowBlank(true);
        ListStore<BaseModel> list = new ListStore<BaseModel>();
        BaseModel field = new BaseModel();
        // field.set("name", "Created By");
        // field.set("value", "createdby");
        // list.add(field);
        // field = new BaseModel();
        // field.set("name", "Created On");
        // field.set("value", "createdon");
        // list.add(field);
        // field = new BaseModel();
        // field.set("name", "Modified By");
        // field.set("value", "modifiedby");
        // list.add(field);
        field = new BaseModel();
        field.set("name", MessagesFactory.getMessages().search_modifiedon());
        field.set("value", "modifiedon");
        list.add(field);
        cb.setDisplayField("name");
        cb.setValueField("value");
        cb.setStore(list);
        cb.setTriggerAction(TriggerAction.ALL);

        cb.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

            public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                if (se.getSelectedItem() != null) {
                    String selvalue = se.getSelectedItem().get("value");
                    // if (selvalue.equals("createdby") && instance.getItemByItemId("createdbyField") == null) {
                    // TextField<String> createdbyField = new TextField<String>();
                    // createdbyField.setFieldLabel("Created By");
                    // createdbyField.setId("createdbyField");
                    // instance.insert(createdbyField, this.getItemCount() - 1, formData);
                    // } else if (selvalue.equals("createdon") && instance.getItemByItemId("createdonField") == null) {
                    // DateField createdonField = new DateField();
                    // createdonField.setFieldLabel("Created On");
                    // createdonField.setId("createdonField");
                    // instance.insert(createdonField, this.getItemCount() - 1, formData);
                    // } else if (selvalue.equals("modifiedby") && instance.getItemByItemId("modifedbyField") == null) {
                    // TextField<String> modifiedbyField = new TextField<String>();
                    // modifiedbyField.setFieldLabel("Modified By");
                    // modifiedbyField.setId("modifedbyField");
                    // instance.insert(modifiedbyField, this.getItemCount() - 1, formData);
                    // } else
                    if (selvalue.equals("modifiedon") && instance.getItemByItemId("modifiedonField1") == null) {
                        instance.insert(addCriteriaContainer("modifiedon"), instance.getItemCount() - 1, new FormData("90%"));
                    }
                    instance.layout(true);
                }
            }

        });
        this.add(cb, new FormData("40%"));

    }
}
