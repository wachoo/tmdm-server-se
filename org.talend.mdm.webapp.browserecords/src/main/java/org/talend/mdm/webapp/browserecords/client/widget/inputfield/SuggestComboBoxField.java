// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import java.util.List;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.BasePagingLoadConfigImpl;
import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.widget.ComboBoxEx;
import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.util.Locale;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.event.BaseEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.DelayedTask;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;

public class SuggestComboBoxField extends ComboBoxEx<ForeignKeyBean> {

    private BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    private ListStore<ForeignKeyBean> foreignKeyStore = new ListStore<ForeignKeyBean>();

    private String foreignKey;

    private List<String> foreignKeyInfo;

    private String foreignKeyFilter;

    private String displayFieldName = "displayInfo"; //$NON-NLS-1$

    private ForeignKeyField foreignKeyField;

    private int getListDelay = 200;

    public SuggestComboBoxField() {
        super();
        init();
    }

    public SuggestComboBoxField(ForeignKeyField foreignKeyField) {
        super();
        this.foreignKeyField = foreignKeyField;
        foreignKey = foreignKeyField.getForeignKey();
        foreignKeyInfo = foreignKeyField.getForeignKeyInfo();
        init();
    }

    protected void init() {

        setDisplayField(displayFieldName);
        setTypeAhead(true);
        setTriggerAction(TriggerAction.ALL);
        setFireChangeEventOnSetValue(true);

        setDefaultStore();
        setStore(store);
        setListener();
    }

    private DelayedTask task = new DelayedTask(new Listener<BaseEvent>() {

        @Override
        public void handleEvent(BaseEvent be) {
            String inputValue = getInputValue();
            if (inputValue != null && inputValue.length() > 0) {
                final boolean hasForeignKeyFilter = foreignKeyFilter != null && foreignKeyFilter.trim().length() > 0 ? true
                        : false;
                BasePagingLoadConfigImpl config = new BasePagingLoadConfigImpl();

                service.getSuggestInformation(config, foreignKey, foreignKeyInfo, BrowseRecords.getSession().getAppHeader()
                        .getDatacluster(), hasForeignKeyFilter, inputValue, Locale.getLanguage(),
                        new SessionAwareAsyncCallback<List<ForeignKeyBean>>() {

                            @Override
                            public void onSuccess(List<ForeignKeyBean> result) {
                                updateListStore(result);
                            }
                        });
            }
        }
    });

    @Override
    protected void onRender(Element target, int index) {
        super.onRender(target, index);
        this.setMinChars(1000);
        getInputEl().dom.setAttribute("autocomplete", "off"); //$NON-NLS-1$ //$NON-NLS-2$

    }

    @Override
    public int getWidth() {
        return GXT.isChrome ? getOffsetWidth() + 75 : getOffsetWidth();
    }

    protected void setListener() {

        addListener(Events.KeyUp, new Listener<FieldEvent>() {

            @Override
            public void handleEvent(FieldEvent e) {
                String inputValue = getInputValue();
                if (e.getKeyCode() != KeyCodes.KEY_UP && e.getKeyCode() != KeyCodes.KEY_DOWN
                        && e.getKeyCode() != KeyCodes.KEY_ESCAPE && e.getKeyCode() != KeyCodes.KEY_ENTER
                        && e.getKeyCode() != KeyCodes.KEY_SHIFT && e.getKeyCode() != KeyCodes.KEY_HOME
                        && e.getKeyCode() != KeyCodes.KEY_END && e.getKeyCode() != KeyCodes.KEY_TAB
                        && e.getKeyCode() != KeyCodes.KEY_CTRL) {

                    if (e.getKeyCode() == KeyCodes.KEY_BACKSPACE) {
                        if (inputValue.contains("[") || inputValue.contains("]")) { //$NON-NLS-1$ //$NON-NLS-2$
                            setInputValue(""); //$NON-NLS-1$
                        }
                    }

                    if (inputValue != null && !"".equals(inputValue.trim()) && !"[".equals(inputValue.trim())) { //$NON-NLS-1$ //$NON-NLS-2$
                        task.delay(getListDelay);
                        setFieldValue();
                    }
                }
            }
        });

        addSelectionChangedListener(new SelectionChangedListener<ForeignKeyBean>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ForeignKeyBean> se) {
                setFieldValue();
            }

        });
    }

    class Resize extends Resizable {

        public Resize(BoxComponent resize) {
            super(resize);
        }

        public native BoxComponent getBoxComponent() /*-{
			return this.@com.extjs.gxt.ui.client.fx.Resizable::resize;
        }-*/;

    }

    @Override
    protected void onFocus(ComponentEvent be) {
        super.onFocus(be);
    }

    @Override
    public void disable() {
        if (foreignKeyField != null) {
            super.disable();
            disabled = true;
            fireEvent(Events.Disable);
        }
    }

    protected void setDefaultStore() {
        store = foreignKeyStore;
    }

    public void updateListStore(List<ForeignKeyBean> suggestionList) {
        foreignKeyStore.removeAll();

        if (suggestionList != null) {
            for (int i = 0; i < suggestionList.size(); i++) {
                ForeignKeyBean bean = suggestionList.get(i);
                if (bean != null && "".equals(bean.getDisplayInfo().trim())) { //$NON-NLS-1$
                    bean.setDisplayInfo(bean.getId());
                }
                foreignKeyStore.add(bean);
            }
        }

        if (!this.isExpanded() && foreignKeyStore.getCount() > 0) {
            this.expand();
        }
    }

    public void setFieldValue() {
        foreignKeyField.setValue(this.getValue());
    }

    public String getInputValue() {
        return getRawValue();
    }

    public void setInputValue(String input) {
        this.setRawValue(input);
    }

    public ListStore<ForeignKeyBean> getForeignKeyStore() {
        return this.foreignKeyStore;
    }

    public void setForeignKeyStore(ListStore<ForeignKeyBean> foreignKeyStore) {
        this.foreignKeyStore = foreignKeyStore;
    }

    @Override
    public void setValue(ForeignKeyBean fk) {
        if (fk != null) {
            super.setValue(fk);
            setRawValue(fk.getId());
        }
    }

    @Override
    public ForeignKeyBean getValue() {
        if (getRawValue() != null && !"".equals(getRawValue().trim())) { //$NON-NLS-1$
            if (foreignKeyStore != null && foreignKeyStore.getCount() > 0) {
                for (ForeignKeyBean bean : foreignKeyStore.getModels()) {
                    if (this.getRawValue().trim().equals(bean.get(displayFieldName))) {
                        return bean;
                    }
                }
            }
            return null;
        } else {
            return null;
        }

    }

    public String getForeignKey() {
        return this.foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }

    @Override
    public void clear() {
        super.clear();
    }

}