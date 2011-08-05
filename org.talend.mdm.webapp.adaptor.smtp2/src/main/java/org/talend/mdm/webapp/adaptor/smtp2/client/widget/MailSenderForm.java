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
package org.talend.mdm.webapp.adaptor.smtp2.client.widget;

import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2;
import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2Events;
import org.talend.mdm.webapp.adaptor.smtp2.client.Smtp2ServiceAsync;
import org.talend.mdm.webapp.adaptor.smtp2.client.i18n.MessagesFactory;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * DOC achen  class global comment. Detailled comment
 */
public class MailSenderForm extends FormPanel {

    private FormBinding binding;

    private Smtp2ServiceAsync service = (Smtp2ServiceAsync) Registry.get(Smtp2.Smtp2_SERVICE);

    private TextField<String> fromField;

    private TextField<String> toField;

    private TextField<String> ccField;

    private TextField<String> bccField;

    private TextField<String> subjectField;

    private TextArea bodyField;

    public MailSenderForm() {
        super();
        this.setScrollMode(Scroll.AUTO);
        LayoutContainer main = new LayoutContainer();
        main.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        main.setStyleAttribute("paddingTop", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        TableLayout table = new TableLayout(2);
        main.setLayout(table);
        TableData data1 = new TableData();
        data1.setColspan(2);

        CellFormPanel fromPanel = new CellFormPanel();
        fromField = new TextField<String>();
        fromField.setFieldLabel(MessagesFactory.getMessages().from());
        fromField.setId("from"); //$NON-NLS-1$  
        fromField.setAllowBlank(false);
        fromPanel.add(fromField);
        main.add(fromPanel, data1);

        CellFormPanel toPanel = new CellFormPanel();
        toField = new TextField<String>();
        toField.setFieldLabel(MessagesFactory.getMessages().to());
        toField.setId("to"); //$NON-NLS-1$ 
        toField.setAllowBlank(false);
        toPanel.add(toField);
        main.add(toPanel, data1);

        CellFormPanel ccPanel = new CellFormPanel();
        ccField = new TextField<String>();
        ccField.setFieldLabel(MessagesFactory.getMessages().cc());
        ccField.setId("cc"); //$NON-NLS-1$
        ccPanel.add(ccField);
        main.add(ccPanel, data1);

        CellFormPanel bccPanel = new CellFormPanel();
        bccField = new TextField<String>();
        bccField.setFieldLabel(MessagesFactory.getMessages().bcc());
        bccField.setId("bcc"); //$NON-NLS-1$
        bccPanel.add(bccField);
        main.add(bccPanel, data1);

        data1.setWidth("400px"); //$NON-NLS-1$

        CellFormPanel subjectPanel = new CellFormPanel();
        subjectField = new TextField<String>();
        subjectField.setFieldLabel(MessagesFactory.getMessages().subject());
        subjectField.setId("subject"); //$NON-NLS-1$
        subjectField.setAllowBlank(false);
        subjectPanel.add(subjectField);
        main.add(subjectPanel, data1);

        CellFormPanel bodyPanel = new CellFormPanel();
        bodyField = new TextArea();
        bodyField.setFieldLabel(MessagesFactory.getMessages().body());
        bodyField.setId("body"); //$NON-NLS-1$
        bodyPanel.add(bodyField);

        main.add(bodyPanel, data1);

        ToolBar bar = new ToolBar();
        Button btnSend = new Button(MessagesFactory.getMessages().send());
        btnSend.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                service.sendSampleEmail(fromField.getValue(), toField.getValue(), ccField.getValue(),
                        bccField.getValue(), subjectField.getValue(), bodyField.getValue(), new AsyncCallback<String>() {

                            public void onFailure(Throwable caught) {
                                Dispatcher.forwardEvent(Smtp2Events.Error, caught);
                                MessageBox.alert(MessagesFactory.getMessages().error(), MessagesFactory.getMessages().error()
                                        + ":" + caught.getMessage(), null); //$NON-NLS-1$

                            }

                            public void onSuccess(String arg0) {
                                MessageBox.info(MessagesFactory.getMessages().save(),
                                        MessagesFactory.getMessages().sucessfully(), null);
                            }

                        });
            }

        });
        bar.add(btnSend);
        bar.add(new SeparatorToolItem());


        this.add(main);
        this.setBottomComponent(bar);

        binding = new FormBinding(this, true);
        binding.addFieldBinding(new FieldBinding(fromField, "from")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(toField, "to")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(ccField, "cc")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(bccField, "bcc")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(subjectField, "subject")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(bodyField, "body")); //$NON-NLS-1$

        binding.autoBind();

    }
}
