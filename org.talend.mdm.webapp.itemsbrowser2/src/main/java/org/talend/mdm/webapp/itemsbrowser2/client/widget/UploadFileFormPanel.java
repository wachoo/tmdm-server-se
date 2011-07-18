// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBaseModel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FormEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FileUploadField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.HiddenField;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class UploadFileFormPanel extends FormPanel implements Listener<FormEvent> {

    private static UploadFileFormPanel panel;

    private static String tableName;

    private ComboBox<ItemBaseModel> fileTypecombo;
    
    private ComboBox<ItemBaseModel> separatorCombo;
    
    private ComboBox<ItemBaseModel> textDelimiterCombo;
    
    private ComboBox<ItemBaseModel> encodingCombo;

    private FileUploadField file;

    private CheckBox headerLine;

    private ItemsToolBar toolbar;

    private ContentPanel container;

    private HiddenField<String> nameField;

    private MessageBox waitBar;
    
    private UploadFileFormPanel() {

        this.setHeading("Upload data"); //$NON-NLS-1$
        this.setFrame(false);
        this.setHeaderVisible(true);
        this.setEncoding(Encoding.MULTIPART);
        this.setMethod(Method.POST);
        this.setWidth("100%"); //$NON-NLS-1$
        this.setAction("/itemsbrowser2/upload"); //$NON-NLS-1$

        this.renderForm();
    }

    public static UploadFileFormPanel getInstance(String name) {
        tableName = name;
        if (panel == null)
            panel = new UploadFileFormPanel();
        panel.getNameField().setValue(tableName);
        return panel;
    }

    private void renderForm() {

        nameField = new HiddenField<String>();
        nameField.setName("concept");//$NON-NLS-1$
        nameField.setValue(tableName);
        this.add(nameField);

        file = new FileUploadField();
        file.setAllowBlank(false);
        file.setName("file"); //$NON-NLS-1$
        file.setId("fileUpload");//$NON-NLS-1$   
        file.setFieldLabel("File"); //$NON-NLS-1$
        this.add(file);

        List<ItemBaseModel> list = new ArrayList<ItemBaseModel>();
        ItemBaseModel excel = new ItemBaseModel();
        excel.set("label", "Excel"); //$NON-NLS-1$ //$NON-NLS-2$
        excel.set("key", "excel"); //$NON-NLS-1$ //$NON-NLS-2$
        list.add(excel);

        ItemBaseModel csv = new ItemBaseModel();
        csv.set("label", "CSV"); //$NON-NLS-1$ //$NON-NLS-2$
        csv.set("key", "csv"); //$NON-NLS-1$ //$NON-NLS-2$
        list.add(csv);
        ListStore<ItemBaseModel> typeList = new ListStore<ItemBaseModel>();
        typeList.add(list);

        fileTypecombo = new ComboBox<ItemBaseModel>();
        fileTypecombo.setId("typeCom"); //$NON-NLS-1$
        fileTypecombo.setName("fileType"); //$NON-NLS-1$
        fileTypecombo.setEmptyText(MessagesFactory.getMessages().label_combo_filetype_select());
        fileTypecombo.setFieldLabel(MessagesFactory.getMessages().label_field_filetype());
        fileTypecombo.setEditable(false);
        fileTypecombo.setTriggerAction(TriggerAction.ALL);
        fileTypecombo.setDisplayField("label"); //$NON-NLS-1$
        fileTypecombo.setValueField("key"); //$NON-NLS-1$
        fileTypecombo.setStore(typeList);
        fileTypecombo.setAllowBlank(false);
        this.add(fileTypecombo);


        headerLine = new CheckBox();
        headerLine.setId("headersOnFirstLine");//$NON-NLS-1$
        headerLine.setName("headersOnFirstLine");//$NON-NLS-1$
        headerLine.setValueAttribute("on"); //$NON-NLS-1$
        headerLine.setFieldLabel(MessagesFactory.getMessages().label_field_header_first());
        headerLine.setValue(true);
        headerLine.setInputStyleAttribute("left", "10px"); //$NON-NLS-1$ //$NON-NLS-2$
        headerLine.setInputStyleAttribute("position", "absolute"); //$NON-NLS-1$ //$NON-NLS-2$
        this.add(headerLine);

        List<ItemBaseModel> separatorList = new ArrayList<ItemBaseModel>();
        ItemBaseModel comma = new ItemBaseModel();
        comma.set("label", "comma"); //$NON-NLS-1$ //$NON-NLS-2$
        comma.set("key", "comma"); //$NON-NLS-1$ //$NON-NLS-2$
        separatorList.add(comma);

        ItemBaseModel semicolon = new ItemBaseModel();
        semicolon.set("label", "semicolon"); //$NON-NLS-1$ //$NON-NLS-2$
        semicolon.set("key", "semicolon"); //$NON-NLS-1$ //$NON-NLS-2$
        separatorList.add(semicolon);

        ListStore<ItemBaseModel> separatorStoreList = new ListStore<ItemBaseModel>();
        separatorStoreList.add(separatorList);

        separatorCombo = new ComboBox<ItemBaseModel>();
        separatorCombo.setId("sep");//$NON-NLS-1$
        separatorCombo.setName("sep");//$NON-NLS-1$
        separatorCombo.setFieldLabel("Separator"); //$NON-NLS-1$
        separatorCombo.setDisplayField("label"); //$NON-NLS-1$
        separatorCombo.setValueField("key"); //$NON-NLS-1$
        separatorCombo.setStore(separatorStoreList);
        separatorCombo.setTriggerAction(TriggerAction.ALL);
        this.add(separatorCombo);

        List<ItemBaseModel> textDelimiterList = new ArrayList<ItemBaseModel>();
        ItemBaseModel doubleDelimiter = new ItemBaseModel();
        doubleDelimiter.set("label", "\""); //$NON-NLS-1$ //$NON-NLS-2$
        doubleDelimiter.set("key", "\""); //$NON-NLS-1$ //$NON-NLS-2$
        textDelimiterList.add(doubleDelimiter);

        ItemBaseModel singleDelimiter = new ItemBaseModel();
        singleDelimiter.set("label", "\'"); //$NON-NLS-1$ //$NON-NLS-2$
        singleDelimiter.set("key", "\'"); //$NON-NLS-1$ //$NON-NLS-2$
        textDelimiterList.add(singleDelimiter);

        ListStore<ItemBaseModel> textDelimiterStoreList = new ListStore<ItemBaseModel>();
        textDelimiterStoreList.add(textDelimiterList);

        textDelimiterCombo = new ComboBox<ItemBaseModel>();
        textDelimiterCombo.setId("delimiter");//$NON-NLS-1$
        textDelimiterCombo.setName("delimiter");//$NON-NLS-1$
        textDelimiterCombo.setFieldLabel("Text Delimiter"); //$NON-NLS-1$
        textDelimiterCombo.setDisplayField("label"); //$NON-NLS-1$
        textDelimiterCombo.setValueField("key"); //$NON-NLS-1$
        textDelimiterCombo.setStore(textDelimiterStoreList);
        textDelimiterCombo.setTriggerAction(TriggerAction.ALL);
        this.add(textDelimiterCombo);

        List<ItemBaseModel> encodingList = new ArrayList<ItemBaseModel>();
        ItemBaseModel utf8 = new ItemBaseModel();
        utf8.set("label", "UTF-8"); //$NON-NLS-1$ //$NON-NLS-2$
        utf8.set("key", "utf-8"); //$NON-NLS-1$ //$NON-NLS-2$
        encodingList.add(utf8);

        ItemBaseModel iso88591 = new ItemBaseModel();
        iso88591.set("label", "ISO-8859-1"); //$NON-NLS-1$ //$NON-NLS-2$
        iso88591.set("key", "iso-8859-1"); //$NON-NLS-1$ //$NON-NLS-2$
        encodingList.add(iso88591);

        ItemBaseModel iso885915 = new ItemBaseModel();
        iso885915.set("label", "iso885915"); //$NON-NLS-1$ //$NON-NLS-2$
        iso885915.set("key", "iso-8859-15"); //$NON-NLS-1$ //$NON-NLS-2$
        encodingList.add(iso885915);

        ItemBaseModel cp1252 = new ItemBaseModel();
        cp1252.set("label", "cp1252"); //$NON-NLS-1$ //$NON-NLS-2$
        cp1252.set("key", "cp-1252"); //$NON-NLS-1$ //$NON-NLS-2$
        encodingList.add(cp1252);

        ListStore<ItemBaseModel> encodingStoreList = new ListStore<ItemBaseModel>();
        encodingStoreList.add(encodingList);

        encodingCombo = new ComboBox<ItemBaseModel>();
        encodingCombo.setId("encodings");//$NON-NLS-1$
        encodingCombo.setName("encodings");//$NON-NLS-1$
        encodingCombo.setFieldLabel(MessagesFactory.getMessages().label_field_encoding());
        encodingCombo.setDisplayField("label"); //$NON-NLS-1$
        encodingCombo.setValueField("key"); //$NON-NLS-1$
        encodingCombo.setStore(encodingStoreList);
        encodingCombo.setTriggerAction(TriggerAction.ALL);
        this.add(encodingCombo);

        fileTypecombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ItemBaseModel> event) {
                if (event.getSelectedItem() == null)
                    return;
                String type = (String) event.getSelectedItem().get("key"); //$NON-NLS-1$
                if (type.equalsIgnoreCase("CSV")) { //$NON-NLS-1$
                    separatorCombo.enable();
                    textDelimiterCombo.enable();
                    encodingCombo.enable();
                } else {
                    separatorCombo.disable();
                    textDelimiterCombo.disable();
                    encodingCombo.disable();
                }
            }
        });

        Button submit = new Button(MessagesFactory.getMessages().label_button_submit());
        submit.setId("btnSubmit");//$NON-NLS-1$
        submit.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!panel.isValid())
                    return;
                panel.submit();
                waitBar = MessageBox.wait(MessagesFactory.getMessages().import_progress_bar_title(), MessagesFactory
                        .getMessages().import_progress_bar_message(), MessagesFactory
                        .getMessages().import_progress_bar_laod());
            }
        });

        this.add(submit);

        separatorCombo.disable();
        textDelimiterCombo.disable();
        encodingCombo.disable();

        this.setLabelWidth(200);
        this.addListener(Events.Submit, this);
    }

    public void handleEvent(FormEvent be) {
        String result = be.getResultHtml().replace("pre>", "f>"); //$NON-NLS-1$//$NON-NLS-2$
        waitBar.close();
        if (result.equals("<f>true</f>")) { //$NON-NLS-1$
            toolbar.addDownloadPanel(container);
        } else {
            MessageBox.alert(MessagesFactory.getMessages().error_title(), result, null);
        }
    }

    public void resetForm() {
        file.clear();
        fileTypecombo.reset();
        headerLine.setValue(true);
        separatorCombo.reset();
        textDelimiterCombo.reset();
        textDelimiterCombo.reset();
        encodingCombo.reset();
        separatorCombo.disable();
        textDelimiterCombo.disable();
        encodingCombo.disable();
    }

    public void setToolbar(ItemsToolBar toolbar) {
        this.toolbar = toolbar;
    }

    public void setContainer(ContentPanel container) {
        this.container = container;
    }

    public HiddenField<String> getNameField() {
        return nameField;
    }
}
