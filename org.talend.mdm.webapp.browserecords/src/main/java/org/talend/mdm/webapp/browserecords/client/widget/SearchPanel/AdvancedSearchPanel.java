// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.SearchPanel;

import java.util.Date;

import org.talend.mdm.webapp.base.client.exception.ParserException;
import org.talend.mdm.webapp.base.client.model.MultipleCriteria;
import org.talend.mdm.webapp.base.client.util.Parser;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsSearchContainer;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
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
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.DateTimePropertyEditor;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.ColumnData;
import com.extjs.gxt.ui.client.widget.layout.ColumnLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.google.gwt.event.dom.client.KeyCodes;
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

    private static String ge = "GREATER_THAN_OR_EQUAL";//$NON-NLS-1$

    private static String le = "LOWER_THAN_OR_EQUAL";//$NON-NLS-1$

    public static String modifiedON = "../../t";//$NON-NLS-1$

    private static String blank = " ";//$NON-NLS-1$

    private Button filterButton = new Button();

    private Button searchBtn;

    final public void setCriteria(String c) {
        if (c.indexOf(modifiedON) > -1) {
            // modified on condition
            String express = c.indexOf(modifiedON) - 5 > -1 ? c.substring(0, c.indexOf(modifiedON) - 5) + ")" : "";//$NON-NLS-1$ //$NON-NLS-2$ 
            parseSearchExpression(express);
            expressionTextField.setValue(express);
            String condition = c.indexOf(")") > -1 ? c.substring(c.indexOf(modifiedON), c.length() - 1) : c.substring(c.indexOf(modifiedON), c.length()); //$NON-NLS-1$
            if (instance.getItemByItemId("modifiedon") == null) { //$NON-NLS-1$
                instance.insert(addCriteriaContainer("modifiedon"), instance.getItemCount() - 1, new FormData("75%")); //$NON-NLS-1$  //$NON-NLS-2$
                instance.layout(true);
            }
            DateField fromfield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon")) //$NON-NLS-1$
                    .getItem(0)).getItemByItemId("modifiedonField1"); //$NON-NLS-1$
            DateField tofield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon")).getItem(1)) //$NON-NLS-1$
                    .getItemByItemId("modifiedonField2"); //$NON-NLS-1$
            fromfield.setValue(null);
            tofield.setValue(null);

            if (condition.indexOf(ge) > -1) {
                Date d = new Date();
                int index = condition.indexOf(ge) + ge.length() + 1;
                if (condition.indexOf(blank, index) == -1) {
                    d.setTime(Long.valueOf(condition.substring(index)));
                } else {
                    d.setTime(Long.valueOf(condition.substring(index, condition.indexOf(blank, index))));
                }
                fromfield.setValue(d);
            }
            if (condition.indexOf(le) > -1) {
                Date d = new Date();
                int index = condition.indexOf(le) + le.length() + 1;
                if (condition.indexOf(blank, index) == -1) {
                    d.setTime(Long.valueOf(condition.substring(index)));
                } else {
                    d.setTime(Long.valueOf(condition.substring(index, condition.indexOf(blank, index))));
                }
                tofield.setValue(d);
            }
        } else {
            if (instance.getItemByItemId("modifiedon") != null) { //$NON-NLS-1$
                instance.remove(instance.getItemByItemId("modifiedon")); //$NON-NLS-1$
            }
            parseSearchExpression(c);
            expressionTextField.setValue(c);
            cb.setValue(null);
        }
    }

    public static void parseSearchExpression(String s) {
        try {
            if (!s.isEmpty()) {
                if (!s.startsWith("(") && !s.endsWith(")")) { //$NON-NLS-1$ //$NON-NLS-2$
                    s = "((" + s + "))"; //$NON-NLS-1$ //$NON-NLS-2$
                }
                char[] sa = s.toCharArray();
                BrowseRecords.getSession().put(
                        UserSession.CUSTOMIZE_CRITERION_STORE_ADVANCE,
                        sa[1] == '(' ? CommonUtil.parseMultipleSearchExpression(sa, 0).cr : CommonUtil
                                .parseSimpleSearchExpression(sa, 0).cr);
            }
        } catch (Exception e) {
        }
    }

    public String RemoveUnsafeCriteria() {
        String curCriteria = getCriteria();
        return curCriteria.replaceAll("[\\s]+) OR ([\\s]+", " OR ").replaceAll("[\\s]+) AND ([\\s]+", " AND ");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
    }

    public String getCriteria() {
        MultipleCriteria criteriaStore = (MultipleCriteria) (BrowseRecords.getSession().get(
                UserSession.CUSTOMIZE_CRITERION_STORE_ADVANCE) == null ? BrowseRecords.getSession().get(
                UserSession.CUSTOMIZE_CRITERION_STORE) : BrowseRecords.getSession().get(
                UserSession.CUSTOMIZE_CRITERION_STORE_ADVANCE));
        String express = criteriaStore != null ? criteriaStore.toString() : null;// expressionTextField.getValue();
        String curCriteria = null, curDate = null;
        if (instance.getItemByItemId("modifiedon") != null) { //$NON-NLS-1$ 
            DateField fromfield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon")) //$NON-NLS-1$
                    .getItem(0)).getItemByItemId("modifiedonField1"); //$NON-NLS-1$
            DateField tofield = (DateField) ((LayoutContainer) ((LayoutContainer) this.getItemByItemId("modifiedon")).getItem(1)) //$NON-NLS-1$
                    .getItemByItemId("modifiedonField2"); //$NON-NLS-1$
            if (fromfield.getValue() != null) {
                curDate = modifiedON + blank + ge + blank + fromfield.getValue().getTime();
            }
            if (tofield.getValue() != null) {
                if (curDate != null) {
                    curDate += " AND " + modifiedON + blank + le + blank + tofield.getValue().getTime(); //$NON-NLS-1$
                } else {
                    curDate = modifiedON + blank + le + blank + tofield.getValue().getTime();
                }
            }

            if (curDate != null) {
                curCriteria = (express == null) ? curDate : express.substring(0, express.lastIndexOf(")")) + " AND " + curDate //$NON-NLS-1$  //$NON-NLS-2$
                        + ")"; //$NON-NLS-1$
            } else {
                curCriteria = (express == null) ? curDate : express;
            }
        } else {
            curCriteria = express;
        }

        return curCriteria;
    }

    public void cleanCriteria() {
        setCriteria(""); //$NON-NLS-1$    
    }

    private LayoutContainer addCriteriaContainer(String id) {
        LayoutContainer main = new LayoutContainer();
        main.setLayout(new ColumnLayout());
        main.setId("modifiedon"); //$NON-NLS-1$
        if (id.equals("modifiedon")) { //$NON-NLS-1$
            LayoutContainer left = new LayoutContainer();
            left.setStyleAttribute("paddingRight", "10px"); //$NON-NLS-1$  //$NON-NLS-2$
            FormLayout layout = new FormLayout();
            layout.setLabelWidth(110);
            left.setLayout(layout);
            DateField modifiedonField1 = new DateField();
            modifiedonField1.setWidth(120);
            modifiedonField1.setFieldLabel(MessagesFactory.getMessages().search_modifiedon());
            modifiedonField1.setId("modifiedonField1"); //$NON-NLS-1$
            modifiedonField1.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd")); //$NON-NLS-1$
            left.add(modifiedonField1);

            LayoutContainer right = new LayoutContainer();
            right.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$  //$NON-NLS-2$
            layout = new FormLayout();
            layout.setLabelWidth(50);
            right.setLayout(layout);
            DateField modifiedonField2 = new DateField();
            modifiedonField2.setWidth(120);
            modifiedonField2.setFieldLabel(MessagesFactory.getMessages().search_modifiedto());
            modifiedonField2.setId("modifiedonField2"); //$NON-NLS-1$
            modifiedonField2.setPropertyEditor(new DateTimePropertyEditor("yyyy-MM-dd")); //$NON-NLS-1$
            right.add(modifiedonField2);

            main.add(left, new ColumnData(.5));
            main.add(right, new ColumnData(.5));
        }

        return main;
    }

    public void setView(ViewBean viewbean) {
        this.view = viewbean;
    }

    public AdvancedSearchPanel(ViewBean viewbean, Button search) {
        this.searchBtn = search;
        this.view = viewbean;
        setHeaderVisible(false);
        // setLayout(new FitLayout());

        this.setFrame(true);
        this.setBodyBorder(false);
        this.setHeaderVisible(false);
        this.setScrollMode(Scroll.AUTO);
        this.setLabelWidth(110);
        this.setAutoHeight(true);

        filterButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Edit()));
        filterButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                final Window winFilter = new Window();
                winFilter.setHeading(MessagesFactory.getMessages().advsearch_filter());
                winFilter.setModal(true);
                winFilter.setScrollMode(Scroll.AUTO);
                // winFilter.setAutoHeight(true);
                // winFilter.setAutoWidth(true);
                // winFilter.setWidth(645);
                // winFilter.setHeight(162);
                ContentPanel root = new ContentPanel();
                root.setHeaderVisible(false);
                root.setBodyBorder(false);
                root.setFrame(false);
                root.setScrollMode(Scroll.AUTO);
                final MultipleCriteriaPanel multiCriteria = new MultipleCriteriaPanel(null, view, winFilter);
                multiCriteria.addStyleName("filter-panel"); //$NON-NLS-1$
                root.add(multiCriteria);
                winFilter.add(root);

                Button searchBtn = new Button(MessagesFactory.getMessages().ok_btn());
                searchBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    @Override
                    public void componentSelected(ButtonEvent ce) {
                        MultipleCriteria mutilCriteria = multiCriteria.getCriteria();
                        BrowseRecords.getSession().put(UserSession.CUSTOMIZE_CRITERION_STORE_ADVANCE, mutilCriteria);
                        setCriteria(mutilCriteria.toString());
                        winFilter.close();
                    }

                });
                winFilter.addButton(searchBtn);
                winFilter.show();
                String curField = expressionTextField.getValue();
                if (curField != null && !curField.equals("")) { //$NON-NLS-1$
                    try {
                        parseSearchExpression(curField);
                        MultipleCriteria criteriaStore = (MultipleCriteria) BrowseRecords.getSession().get(
                                UserSession.CUSTOMIZE_CRITERION_STORE_ADVANCE);
                        multiCriteria.setCriteria(criteriaStore);
                    } catch (Exception e) {
                        Log.error(e.getMessage(), e);
                    }
                }
                DOM.setStyleAttribute(winFilter.getBody().dom, "backgroundColor", "white"); //$NON-NLS-1$  //$NON-NLS-2$
            }
        });

        final Button validButton = new Button();
        validButton.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Valid()));
        validButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                try {
                    String curField = expressionTextField.getValue();
                    if (curField != null && curField.length() != 0) {
                        Parser.parse(curField);
                        MessageBox.info(MessagesFactory.getMessages().info_title(), MessagesFactory.getMessages()
                                .valid_expression(), null);
                    }
                } catch (ParserException e) {
                    Log.error(e.getMessage(), e);
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                            .invalid_expression() + e.getMessage(), null);
                }
            }
        });

        expressionTextField = new TextField<String>() {

            @Override
            protected void onRender(Element target, int index) {
                // add button
                El wrap = new El(DOM.createDiv());
                wrap.addStyleName("x-form-field-wrap"); //$NON-NLS-1$
                wrap.addStyleName("x-form-file-wrap"); //$NON-NLS-1$

                input = new El(DOM.createInputText());
                input.addStyleName(fieldStyle);
                input.addStyleName("x-form-file-text"); //$NON-NLS-1$
                input.setId(XDOM.getUniqueId());

                if (GXT.isIE && target.getTagName().equals("TD")) { //$NON-NLS-1$
                    input.setStyleAttribute("position", "static"); //$NON-NLS-1$  //$NON-NLS-2$
                }

                wrap.appendChild(input.dom);

                setElement(wrap.dom, target, index);

                filterButton.addStyleName("x-form-filter-btn"); //$NON-NLS-1$
                filterButton.render(wrap.dom);
                validButton.addStyleName("x-form-valid-btn"); //$NON-NLS-1$
                validButton.render(wrap.dom);
                super.onRender(target, index);
                input.setEnabled(false);
            }

            @Override
            protected void onResize(int width, int height) {
                super.onResize(width, height);
                filterButton.setSize(25, 25);
                validButton.setSize(25, 25);
                input.setWidth(width - 50 - 6, true);
            }

            @Override
            protected void doAttachChildren() {
                super.doAttachChildren();
                ComponentHelper.doAttach(filterButton);
                ComponentHelper.doAttach(validButton);
            }

            @Override
            protected void doDetachChildren() {
                super.doDetachChildren();
                ComponentHelper.doDetach(filterButton);
                ComponentHelper.doDetach(validButton);
            }

        };

        expressionTextField.addListener(Events.KeyDown, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_ENTER) {
                    if (searchBtn != null) {
                        searchBtn.fireEvent(Events.Select);
                    }
                }
            }
        });

        expressionTextField.setFieldLabel(MessagesFactory.getMessages().search_expression());
        this.add(expressionTextField, new FormData("70%")); //$NON-NLS-1$

        cb = new ComboBox<BaseModel>();
        cb.setEditable(false);
        cb.setFieldLabel(MessagesFactory.getMessages().advsearch_morelabel());
        cb.setAllowBlank(true);
        ListStore<BaseModel> list = new ListStore<BaseModel>();
        BaseModel field = new BaseModel();
        field = new BaseModel();
        field.set("name", MessagesFactory.getMessages().search_modifiedon()); //$NON-NLS-1$
        field.set("value", "modifiedon"); //$NON-NLS-1$  //$NON-NLS-2$
        list.add(field);
        cb.setDisplayField("name"); //$NON-NLS-1$
        cb.setValueField("value"); //$NON-NLS-1$
        cb.setStore(list);
        cb.setTriggerAction(TriggerAction.ALL);

        cb.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
                if (se.getSelectedItem() != null) {
                    String selvalue = se.getSelectedItem().get("value"); //$NON-NLS-1$                   
                    if (selvalue.equals("modifiedon") && instance.getItemByItemId("modifiedonField1") == null) { //$NON-NLS-1$  //$NON-NLS-2$
                        instance.insert(addCriteriaContainer("modifiedon"), instance.getItemCount() - 1, new FormData("75%")); //$NON-NLS-1$  //$NON-NLS-2$
                    }
                    instance.layout(true);
                    ItemsSearchContainer.getInstance().resizeTop(30 + instance.getOffsetHeight());
                }
            }

        });
        this.add(cb, new FormData("20%")); //$NON-NLS-1$

    }
}
