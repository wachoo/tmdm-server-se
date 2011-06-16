package org.talend.mdm.webapp.general.client.layout;

import java.util.List;

import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.client.mvc.GeneralEvent;
import org.talend.mdm.webapp.general.model.ComboBoxModel;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel.LabelAlign;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;


public class ActionsPanel extends ContentPanel {

    private static ActionsPanel instance;
    
    private ListStore<ComboBoxModel> containerStore = new ListStore<ComboBoxModel>();
    private ComboBox<ComboBoxModel> dataContainerBox = new ComboBox<ComboBoxModel>();
    
    private ListStore<ComboBoxModel> dataStore = new ListStore<ComboBoxModel>();
    private ComboBox<ComboBoxModel> dataModelBox = new ComboBox<ComboBoxModel>();
    
    private Button saveBtn = new Button(MessageFactory.getMessages().save());
    
    private ActionsPanel(){
        super();
        this.setHeading(MessageFactory.getMessages().actions());
        FormLayout formLayout = new FormLayout();
        formLayout.setLabelAlign(LabelAlign.TOP);
        this.setLayout(formLayout);
        
        dataContainerBox.setFieldLabel(MessageFactory.getMessages().data_container());
        dataContainerBox.setDisplayField("value"); //$NON-NLS-1$
        dataContainerBox.setValueField("value"); //$NON-NLS-1$
        dataContainerBox.setWidth(100);
        dataContainerBox.setStore(containerStore);
        dataModelBox.setFieldLabel(MessageFactory.getMessages().data_model());
        dataModelBox.setDisplayField("value"); //$NON-NLS-1$
        dataModelBox.setValueField("value"); //$NON-NLS-1$
        dataModelBox.setWidth(100);
        dataModelBox.setStore(dataStore);
        FormData formData = new FormData();
        formData.setMargins(new Margins(5));
        this.add(dataContainerBox, formData);
        this.add(dataModelBox, formData);
        this.add(saveBtn, formData);
        
        initEvent();
    }
    
    public static ActionsPanel getInstance(){
        if (instance == null){
            instance = new ActionsPanel();
        }
        return instance;
    }
    
    private void initEvent(){
        saveBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {
            public void componentSelected(ButtonEvent ce) {
                Dispatcher dispatcher = Dispatcher.get();
                dispatcher.dispatch(GeneralEvent.SwitchClusterAndModel);
            }
        });
    }
    
    public void loadDataContainer(List<ComboBoxModel> containers){
        containerStore.removeAll();
        containerStore.add(containers);
    }
    
    public void loadDataModel(List<ComboBoxModel> models){
        dataStore.removeAll();
        dataStore.add(models);
    }
    
    public String getDataCluster(){
        return dataContainerBox.getValue().getValue();
    }
    
    public String getDataModel(){
        return dataModelBox.getValue().getValue();
    }
}