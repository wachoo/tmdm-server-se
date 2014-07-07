package org.talend.mdm.webapp.general.client.layout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.util.UserContextUtil;
import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.model.ActionBean;
import org.talend.mdm.webapp.general.model.ComboBoxModel;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.CheckBox;
import com.extjs.gxt.ui.client.widget.form.CheckBoxGroup;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;

public class ActionsPanel extends ContentPanel {

    private static ActionsPanel instance;

    private final static String LABEL_START = "Start", LABEL_ALERT = "Alert", LABEL_TASKS = "Tasks", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            LABEL_PROCESS = "Process", LABEL_SEARCH = "Search", LABEL_DATA = "Data", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            LABEL_JOURNAL = "Journal", LABEL_ROUTING_EVENT = "Routing Event", LABEL_MATCHING = "Matching"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final String NAME_START = "start", NAME_PROCESS = "process", NAME_ALERT = "alert", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            NAME_SEARCH = "search", NAME_TASKS = "tasks", NAME_CHART_DATA = "chart_data", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            NAME_CHART_ROUTING_EVENT = "chart_routing_event", NAME_CHART_JOURNAL = "chart_journal", NAME_CHART_MATCHING = "chart_matching"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final List<String> DEFAULT_PORTLET_NAMES = Arrays.asList(NAME_START, NAME_PROCESS, NAME_ALERT, NAME_SEARCH,
            NAME_TASKS, NAME_CHART_DATA, NAME_CHART_ROUTING_EVENT, NAME_CHART_JOURNAL, NAME_CHART_MATCHING);

    private static final Map<String, String> NAME_LABEL_MAPPER = new HashMap<String, String>(9) {

        {
            put(NAME_START, LABEL_START);
            put(NAME_PROCESS, LABEL_PROCESS);
            put(NAME_ALERT, LABEL_ALERT);
            put(NAME_SEARCH, LABEL_SEARCH);
            put(NAME_TASKS, LABEL_TASKS);
            put(NAME_CHART_DATA, LABEL_DATA);
            put(NAME_CHART_ROUTING_EVENT, LABEL_ROUTING_EVENT);
            put(NAME_CHART_JOURNAL, LABEL_JOURNAL);
            put(NAME_CHART_MATCHING, LABEL_MATCHING);
        }
    };

    private static final String DEFAULT_COLUMN_NUM = "defaultColNum"; //$NON-NLS-1$

    private ListStore<ComboBoxModel> containerStore = new ListStore<ComboBoxModel>();

    private ComboBox<ComboBoxModel> dataContainerBox = new ComboBox<ComboBoxModel>();

    private ListStore<ComboBoxModel> dataStore = new ListStore<ComboBoxModel>();

    private ComboBox<ComboBoxModel> dataModelBox = new ComboBox<ComboBoxModel>();

    private Map<String, CheckBox> portletCKBoxes;

    private Radio col2Radio;

    private Radio col3Radio;

    private FormData formData;

    private Button saveBtn = new Button(MessageFactory.getMessages().save());

    private Button saveConfigBtn = new Button(MessageFactory.getMessages().save());

    private ComboBoxModel emptyModelValue = new ComboBoxModel();

    private static Boolean modelSelectFlag = true;

    private static Boolean containerSelectFlag = true;

    private ActionsPanel() {
        super();
        this.setHeading(MessageFactory.getMessages().actions());
        FormLayout formLayout = new FormLayout();
        formLayout.setLabelAlign(LabelAlign.TOP);
        this.setLayout(formLayout);

        dataContainerBox.setFieldLabel(MessageFactory.getMessages().data_container());
        dataContainerBox.setDisplayField("value"); //$NON-NLS-1$
        dataContainerBox.setValueField("value"); //$NON-NLS-1$
        dataContainerBox.setAllowBlank(false);
        dataContainerBox.setWidth(windowResizeDelay);
        dataContainerBox.setStore(containerStore);
        dataContainerBox.setTypeAhead(true);
        dataContainerBox.setTriggerAction(TriggerAction.ALL);
        dataContainerBox.setEditable(disabled);
        dataModelBox.setFieldLabel(MessageFactory.getMessages().data_model());
        dataModelBox.setDisplayField("value"); //$NON-NLS-1$
        dataModelBox.setValueField("value"); //$NON-NLS-1$
        dataModelBox.setAllowBlank(false);
        dataModelBox.setWidth(windowResizeDelay);
        dataModelBox.setStore(dataStore);
        dataModelBox.setTypeAhead(true);
        dataModelBox.setTriggerAction(TriggerAction.ALL);
        dataModelBox.setEditable(disabled);
        saveBtn.disable();
        formData = new FormData();
        formData.setMargins(new Margins(5));

        this.add(dataContainerBox, formData);
        this.add(dataModelBox, formData);
        this.add(saveBtn, formData);
        this.addDefaultPortletConfig();
        this.add(saveConfigBtn, formData);
        this.setScrollMode(Scroll.AUTO);

        initEvent();
    }

    private void addDefaultPortletConfig() {
        portletCKBoxes = new HashMap<String, CheckBox>(9);
        FieldSet portalConfig = new FieldSet();
        FormLayout formLayout = new FormLayout();
        formLayout.setLabelAlign(LabelAlign.TOP);
        portalConfig.setLayout(formLayout);
        portalConfig.setHeading("Portal Configuration"); //$NON-NLS-1$
        CheckBoxGroup checkGroup = new CheckBoxGroup();
        checkGroup.setFieldLabel("Portlets"); //$NON-NLS-1$
        checkGroup.setOrientation(Orientation.VERTICAL);
        CheckBox check;

        for (String portletName : DEFAULT_PORTLET_NAMES) {
            check = new CheckBox();
            check.setName(portletName);
            check.setBoxLabel(NAME_LABEL_MAPPER.get(portletName));
            check.setValue(false);
            check.setVisible(false);
            checkGroup.add(check);
            portletCKBoxes.put(portletName, check);
        }

        portalConfig.add(checkGroup);

        RadioGroup radioGroup = new RadioGroup();
        radioGroup.setFieldLabel("Welcome Columns"); //$NON-NLS-1$
        col2Radio = new Radio();
        col2Radio.setBoxLabel("Two"); //$NON-NLS-1$

        col3Radio = new Radio();
        col3Radio.setBoxLabel("Three"); //$NON-NLS-1$

        radioGroup.add(col2Radio);
        radioGroup.add(col3Radio);

        portalConfig.add(radioGroup);
        this.add(portalConfig, formData);
    }

    public static ActionsPanel getInstance() {
        if (instance == null) {
            instance = new ActionsPanel();
        }
        return instance;
    }

    private void initEvent() {
        saveBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(GeneralEvent.SwitchClusterAndModel);
            }
        });

        saveConfigBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(GeneralEvent.SwitchClusterAndModel);
                Map<String, Boolean> configUpdates = getPortalConfigUpdate();
                refreshPortal(configUpdates);

            }
        });

        dataModelBox.addSelectionChangedListener(new SelectionChangedListener<ComboBoxModel>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ComboBoxModel> se) {
                if (se.getSelectedItem() == null) {
                    saveBtn.disable();
                    return;
                }
                String selectedValue = se.getSelectedItem().get("value"); //$NON-NLS-1$
                if (selectedValue != null && !"".equals(selectedValue.trim())) { //$NON-NLS-1$
                    saveBtn.enable();
                    if (!modelSelectFlag) {
                        modelSelectFlag = true;
                        return;
                    }
                    // look for data container
                    for (ComboBoxModel dataModel : dataContainerBox.getStore().getModels()) {
                        if (selectedValue.equals(dataModel.getValue())) {
                            dataContainerBox.setValue(dataModel);
                            containerSelectFlag = true;
                            saveBtn.enable();
                            return;
                        }
                    }
                    containerSelectFlag = false;
                    dataContainerBox.setValue(emptyModelValue);
                    saveBtn.disable();
                }
            }
        });

        dataContainerBox.addSelectionChangedListener(new SelectionChangedListener<ComboBoxModel>() {

            @Override
            public void selectionChanged(SelectionChangedEvent<ComboBoxModel> se) {
                if (se.getSelectedItem() == null) {
                    saveBtn.disable();
                    return;
                }
                String selectedValue = se.getSelectedItem().get("value"); //$NON-NLS-1$
                if (selectedValue != null && !"".equals(selectedValue.trim())) { //$NON-NLS-1$
                    saveBtn.enable();
                    if (!containerSelectFlag) {
                        containerSelectFlag = true;
                        return;
                    }
                    // look for data model
                    for (ComboBoxModel dataModel : dataModelBox.getStore().getModels()) {
                        if (selectedValue.equals(dataModel.getValue())) {
                            dataModelBox.setValue(dataModel);
                            modelSelectFlag = true;
                            return;
                        }
                    }
                    modelSelectFlag = false;
                    dataModelBox.setValue(emptyModelValue);
                    saveBtn.disable();
                }
            }
        });
    }

    public void loadAction(ActionBean action) {
        containerStore.removeAll();
        dataStore.removeAll();

        containerStore.add(action.getClusters());
        dataStore.add(action.getModels());

        ComboBoxModel cluster = containerStore.findModel("value", action.getCurrentCluster()); //$NON-NLS-1$
        if (cluster != null) {
            dataContainerBox.setValue(cluster);
        }
        ComboBoxModel model = dataStore.findModel("value", action.getCurrentModel()); //$NON-NLS-1$
        if (model != null) {
            dataModelBox.setValue(model);
        }

        UserContextUtil.setDataContainer(action.getCurrentCluster());
        UserContextUtil.setDataModel(action.getCurrentModel());
    }

    public String getDataCluster() {
        return dataContainerBox.getValue().getValue();
    }

    public String getDataModel() {
        return dataModelBox.getValue().getValue();
    }

    public void updatePortletConfig(Map<String, Boolean> portletVisibles) {
        Map<String, Boolean> parsedConfig = parseConfig(portletVisibles.toString());
        for (CheckBox check : portletCKBoxes.values()) {
            String name = check.getName();
            if (parsedConfig.containsKey(name)) {
                check.setVisible(true);
                check.setValue(parsedConfig.get(name));
            }
        }
        if (parsedConfig.get(DEFAULT_COLUMN_NUM)) {
            col3Radio.setValue(true);
        } else {
            col2Radio.setValue(true);
        }
        this.layout(true);
    }

    private Map<String, Boolean> parseConfig(String dataString) {
        Map<String, Boolean> config = new HashMap<String, Boolean>();
        String temp = dataString.substring(1, dataString.length() - 1);
        String[] nameValues = temp.split(","); //$NON-NLS-1$
        String name;
        Boolean visible;
        String[] nameValuePair;
        for (String nameValue : nameValues) {
            nameValuePair = nameValue.split("="); //$NON-NLS-1$
            name = nameValuePair[0].trim();
            visible = Boolean.parseBoolean(nameValuePair[1]);
            config.put(name, visible);
        }
        return config;
    }

    public void uncheckPortlet(String portletName) {
        portletCKBoxes.get(portletName).setValue(false);
        this.layout(true);
    }

    private HashMap<String, Boolean> getPortalConfigUpdate() {
        HashMap<String, Boolean> updates = new HashMap<String, Boolean>();
        CheckBox check;
        for (String name : DEFAULT_PORTLET_NAMES) {
            check = portletCKBoxes.get(name);
            if (check.isVisible()) {
                updates.put(name, check.getValue());
            }
        }

        boolean defaultColNum = true;
        if (!col3Radio.getValue()) {
            defaultColNum = false;
        }
        updates.put(DEFAULT_COLUMN_NUM, defaultColNum);
        return updates;
    }

    // record available portlets in Actions panel
    private native void refreshPortal(Map<String, Boolean> portalConfig)/*-{
        $wnd.amalto.core.refreshPortal(portalConfig);
    }-*/;
}
