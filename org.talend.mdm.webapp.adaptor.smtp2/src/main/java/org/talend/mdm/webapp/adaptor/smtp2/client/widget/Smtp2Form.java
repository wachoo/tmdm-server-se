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
import org.talend.mdm.webapp.adaptor.smtp2.shared.SmtpConfigurationBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.TableData;
import com.extjs.gxt.ui.client.widget.layout.TableLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * DOC achen  class global comment. Detailled comment
 */
public class Smtp2Form extends FormPanel {

    private FormBinding binding;

    private Smtp2ServiceAsync service = (Smtp2ServiceAsync) Registry.get(Smtp2.Smtp2_SERVICE);

    private TextField<String> serverField;

    private TextField<String> portField;

    private TextField<String> usernameField;

    private TextField<String> passwordField;

    private TextArea blindcopyField;

    public Smtp2Form() {
        super();
        this.setScrollMode(Scroll.AUTO);
        LayoutContainer main = new LayoutContainer();
        main.setStyleAttribute("paddingLeft", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        main.setStyleAttribute("paddingTop", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        TableLayout table = new TableLayout(2);
        main.setLayout(table);
        TableData data1 = new TableData();
        data1.setColspan(2);
        CellFormPanel modelPanel = new CellFormPanel();
        serverField = new TextField<String>();
        serverField.setFieldLabel(MessagesFactory.getMessages().smtpserver());
        serverField.setId("smtpserver"); //$NON-NLS-1$  
        serverField.setAllowBlank(false);
        modelPanel.add(serverField);
        main.add(modelPanel, data1);

        CellFormPanel expiredPanel = new CellFormPanel();
        portField = new TextField<String>();
        portField.setFieldLabel(MessagesFactory.getMessages().listenerport());
        portField.setId("listenerport"); //$NON-NLS-1$ 
        portField.setAutoValidate(true);
        portField.setAllowBlank(false);
        portField.setRegex("\\d+{1,5}"); //$NON-NLS-1$
        expiredPanel.add(portField);
        main.add(expiredPanel, data1);

        CellFormPanel companyPanel = new CellFormPanel();
        usernameField = new TextField<String>();
        usernameField.setFieldLabel(MessagesFactory.getMessages().username());
        usernameField.setId("username"); //$NON-NLS-1$
        usernameField.setAllowBlank(false);
        companyPanel.add(usernameField);
        main.add(companyPanel, data1);

        CellFormPanel typePanel = new CellFormPanel();
        passwordField = new TextField<String>();
        passwordField.setFieldLabel(MessagesFactory.getMessages().password());
        passwordField.setId("password"); //$NON-NLS-1$
        passwordField.setAllowBlank(false);
        typePanel.add(passwordField);
        main.add(typePanel, data1);

        data1 = new TableData();
        data1.setWidth("400px"); //$NON-NLS-1$
        CellFormPanel adminPanel = new CellFormPanel();
        blindcopyField = new TextArea();
        blindcopyField.setFieldLabel(MessagesFactory.getMessages().blindcopy());
        blindcopyField.setId("blindcopy"); //$NON-NLS-1$
        adminPanel.add(blindcopyField);
        main.add(adminPanel, data1);

        ToolBar bar = new ToolBar();
        Button btnSave = new Button(MessagesFactory.getMessages().save());
        btnSave.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                if (!Smtp2Form.this.isValid())
                    return;
                SmtpConfigurationBean config = new SmtpConfigurationBean();
                config.setSmtpServer(serverField.getValue());
                config.setSmtpPort(Integer.valueOf(portField.getValue()));
                config.setSmtpUsername(usernameField.getValue());
                config.setSmtpPassword(passwordField.getValue());
                config.setSmtpBCC(blindcopyField.getValue());

                service.saveConfiguration(config, new AsyncCallback<Void>() {

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(Smtp2Events.Error, caught);
                        MessageBox.alert(MessagesFactory.getMessages().error(), MessagesFactory.getMessages().error()
                                + ":" + caught.getMessage(), null); //$NON-NLS-1$

                    }

                    public void onSuccess(Void arg0) {
                        MessageBox.info(MessagesFactory.getMessages().save(), MessagesFactory.getMessages().save_sucessful(),
                                null);
                    }
                });
            }

        });
        bar.add(btnSave);
        bar.add(new SeparatorToolItem());

        Button btnTryMe = new Button(MessagesFactory.getMessages().try_me());
        btnTryMe.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                final Window winEditLicense = new Window();
                winEditLicense.setHeading(MessagesFactory.getMessages().send_sample_email());
                winEditLicense.setModal(true);
                winEditLicense.setSize(420, 500);
                winEditLicense.setLayout(new FitLayout());
                winEditLicense.setBodyBorder(false);
                final MailSenderForm form = new MailSenderForm();
                winEditLicense.add(form);

                Button btnSubmit = new Button(MessagesFactory.getMessages().ok());
                btnSubmit.setId("btnSubmit");//$NON-NLS-1$
                btnSubmit.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        if (!form.isValid())
                            return;
                        form.submit();

                    }
                });
                winEditLicense.addButton(btnSubmit);

                Button btnCancel = new Button(MessagesFactory.getMessages().cancel());
                btnCancel.setId("btnCancel");//$NON-NLS-1$
                btnCancel.addSelectionListener(new SelectionListener<ButtonEvent>() {

                    public void componentSelected(ButtonEvent ce) {
                        winEditLicense.close();
                    }
                });
                winEditLicense.addButton(btnCancel);
                winEditLicense.show();
            }

        });
        bar.add(btnTryMe);
        bar.add(new SeparatorToolItem());

        this.add(main);
        this.setBottomComponent(bar);

        binding = new FormBinding(this, true);
        binding.addFieldBinding(new FieldBinding(serverField, "smtpserver")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(portField, "listenerport")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(usernameField, "username")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(passwordField, "password")); //$NON-NLS-1$
        binding.addFieldBinding(new FieldBinding(blindcopyField, "blindcopy")); //$NON-NLS-1$

        binding.autoBind();

    }
}
