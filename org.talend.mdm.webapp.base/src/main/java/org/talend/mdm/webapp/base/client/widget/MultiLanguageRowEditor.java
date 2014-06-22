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
package org.talend.mdm.webapp.base.client.widget;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.talend.mdm.webapp.base.client.i18n.BaseMessagesFactory;
import org.talend.mdm.webapp.base.client.util.UrlUtil;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.grid.RowEditor;


public class MultiLanguageRowEditor extends RowEditor<BaseModel> {

    private MultiLanguageField field;

    private Window window;

    private LinkedHashMap<String, String> languageValueMap;

    private int selectedRowIndex;

    public int getSelectedRowIndex() {
        return selectedRowIndex;
    }

    public void setSelectedRowIndex(int selectedRowIndex) {
        this.selectedRowIndex = selectedRowIndex;
    }

    public MultiLanguageRowEditor(MultiLanguageField field, Window window) {
        this.field = field;
        this.window = window;
        this.languageValueMap = field.getMultiLanguageModel().getLanguageValueMap();
    }

    // cancel click Editor
    protected void onRowClick(GridEvent<BaseModel> e) {
    }

    @Override
    public void startEditing(int rowIndex, boolean doFocus) {
        this.setSelectedRowIndex(rowIndex);
        super.startEditing(rowIndex, doFocus);
        grid.getSelectionModel().setLocked(true);
    }

    @Override
    public void stopEditing(boolean saveChanges) {
        super.stopEditing(saveChanges);
        grid.getSelectionModel().setLocked(false);
        if (saveChanges) {
            final BaseModel rowModel = grid.getStore().getAt(this.rowIndex);
            String selectedRowLanguage = rowModel.get("language"); //$NON-NLS-1$
            String selectedRowValue = rowModel.get("value"); //$NON-NLS-1$
            if (selectedRowValue == null || selectedRowValue.trim().length() == 0) {
                MessageBox.alert(BaseMessagesFactory.getMessages().message_fail(), BaseMessagesFactory.getMessages()
                        .multiLanguage_edit_failure(), null);
                return;
            }
            List<String> values = new ArrayList<String>(languageValueMap.values());
            // check language exist
            if (languageValueMap.containsKey(selectedRowLanguage)
                    && this.rowIndex != values.indexOf(languageValueMap.get(selectedRowLanguage))) {
                MessageBox.alert(BaseMessagesFactory.getMessages().message_fail(), BaseMessagesFactory.getMessages()
                        .multiLangauge_language_duplicate(), null);
                return;
            }
            field.getMultiLanguageModel().setValueByLanguage(selectedRowLanguage, selectedRowValue);
            if (UrlUtil.getUpperLanguage().equals(selectedRowLanguage))
                field.setValue(selectedRowValue);
            rowModel.set("isNewNode", false); //$NON-NLS-1$
            field.fireEvent(Events.Change);
            MessageBox.confirm(BaseMessagesFactory.getMessages().message_success(), BaseMessagesFactory.getMessages()
                    .edit_success_info(),
                    new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            if (Dialog.NO.equals(be.getButtonClicked().getItemId())) {
                                if (window != null)
                                    window.hide();
                            }
                        }
                    });
        }
    }
}