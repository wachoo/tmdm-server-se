package org.talend.mdm.webapp.general.client.layout;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.MessageBox;
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

    private final static String PORTLET_START = "Start", PORTLET_ALERT = "Alert", PORTLET_TASKS = "Tasks", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            PORTLET_PROCESS = "Process", PORTLET_SEARCH = "Search", CHART_DATA = "Data", //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
            CHART_JOURNAL = "Journal", CHART_ROUTING_EVENT = "Routing Event", CHART_MATCHING = "Matching"; //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$

    private static final List<String> DEFAULT_PORTLET_LABELS = Arrays.asList(PORTLET_START, PORTLET_PROCESS, PORTLET_ALERT,
            PORTLET_SEARCH, PORTLET_TASKS, CHART_DATA, CHART_ROUTING_EVENT, CHART_JOURNAL, CHART_MATCHING);

    private static final Map<String, String> NAME_LABEL_MAPPER = new HashMap<String, String>(9) {

        {
            put("start", PORTLET_START); //$NON-NLS-1$
            put("process", PORTLET_PROCESS); //$NON-NLS-1$
            put("alert", PORTLET_ALERT); //$NON-NLS-1$
            put("search", PORTLET_SEARCH); //$NON-NLS-1$
            put("tasks", PORTLET_TASKS); //$NON-NLS-1$
            put("chart_data", CHART_DATA); //$NON-NLS-1$
            put("chart_routing_event", CHART_ROUTING_EVENT); //$NON-NLS-1$
            put("chart_journal", CHART_JOURNAL); //$NON-NLS-1$
            put("chart_matching", CHART_MATCHING); //$NON-NLS-1$
        }
    };

    private ListStore<ComboBoxModel> containerStore = new ListStore<ComboBoxModel>();

    private ComboBox<ComboBoxModel> dataContainerBox = new ComboBox<ComboBoxModel>();

    private ListStore<ComboBoxModel> dataStore = new ListStore<ComboBoxModel>();

    private ComboBox<ComboBoxModel> dataModelBox = new ComboBox<ComboBoxModel>();

    private Map<String, CheckBox> portletCKBoxes;

    private FormData formData;

    private Button saveBtn = new Button(MessageFactory.getMessages().save());

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
        this.addDefaultPortletConfig();
        this.add(saveBtn, formData);
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
        
        for (String portletLabel : DEFAULT_PORTLET_LABELS) {
            check = new CheckBox();
            check.setBoxLabel(portletLabel);
            check.setValue(false);            
            check.setVisible(false);
            checkGroup.add(check);
            portletCKBoxes.put(portletLabel, check);
        }
        
        portalConfig.add(checkGroup);

        RadioGroup radioGroup = new RadioGroup();
        radioGroup.setFieldLabel("Welocme Columns"); //$NON-NLS-1$
        Radio radio1 = new Radio();
        radio1.setBoxLabel("Two"); //$NON-NLS-1$

        Radio radio2 = new Radio();
        radio2.setBoxLabel("Three"); //$NON-NLS-1$
        radio2.setValue(true);

        radioGroup.add(radio1);
        radioGroup.add(radio2);

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

    public void updatePortletConfig(Set<String> portlets) {
        for (String name : portlets) {
            portletCKBoxes.get(NAME_LABEL_MAPPER.get(name)).setVisible(true);
            portletCKBoxes.get(NAME_LABEL_MAPPER.get(name)).setValue(true);
        }
        this.layout(true);
    }

    public void uncheckPortlet(String portletName) {
        portletCKBoxes.get(NAME_LABEL_MAPPER.get(portletName)).setValue(false);
        this.layout(true);
    }
}
