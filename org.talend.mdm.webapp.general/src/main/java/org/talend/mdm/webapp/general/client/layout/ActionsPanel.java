package org.talend.mdm.webapp.general.client.layout;

import java.util.List;

import org.talend.mdm.webapp.general.client.i18n.MessageFactory;
import org.talend.mdm.webapp.general.model.ComboBoxModel;

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
        dataContainerBox.setWidth(100);
        dataModelBox.setFieldLabel(MessageFactory.getMessages().data_model());
        dataModelBox.setWidth(100);
        FormData formData = new FormData();
        formData.setMargins(new Margins(5));
        this.add(dataContainerBox, formData);
        this.add(dataModelBox, formData);
        this.add(saveBtn, formData);
    }
    
    public static ActionsPanel getInstance(){
        if (instance == null){
            instance = new ActionsPanel();
        }
        return instance;
    }
    
    public void loadDataContainer(List<ComboBoxModel> containers){
        containerStore.removeAll();
        containerStore.add(containers);
        dataContainerBox.setStore(containerStore);
    }
    
    public void loadDataModel(List<ComboBoxModel> models){
        dataStore.removeAll();
        dataStore.add(models);
        dataModelBox.setStore(dataStore);
    }
}