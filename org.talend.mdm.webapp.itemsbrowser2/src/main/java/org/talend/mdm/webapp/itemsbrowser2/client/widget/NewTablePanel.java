// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rthis.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.base.client.SessionAwareAsyncCallback;
import org.talend.mdm.webapp.base.client.model.ItemBaseModel;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsEvents;
import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.util.Locale;
import org.talend.mdm.webapp.itemsbrowser2.client.util.UserSession;
import org.talend.mdm.webapp.itemsbrowser2.client.util.ViewUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.DualListField;
import com.extjs.gxt.ui.client.widget.form.DualListField.Mode;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.ListField;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.FormData;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class NewTablePanel extends FormPanel {

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    private ItemsToolBar toolbar;

    private TextField<String> tableName;

    private ComboBox<ItemBaseModel> viewCombo;
    
    private DualListField<ItemBaseModel> detailedList;
    
    private ListField<ItemBaseModel> from;
    
    private ListField<ItemBaseModel> to;
    
    private ViewBean currentBean;
    
    public NewTablePanel() {
        this.setCollapsible(true);
        this.setFrame(false);
        this.setHeaderVisible(false);
        this.setBodyBorder(false);
        this.setWidth("100%"); //$NON-NLS-1$
        this.setScrollMode(Scroll.AUTO);

        this.addFormPanel();
    }

    private void addFormPanel() {

        tableName = new TextField<String>();
        tableName.setFieldLabel(MessagesFactory.getMessages().label_field_table_name());     
        tableName.setAllowBlank(false);
        this.add(tableName, new FormData("20%")); //$NON-NLS-1$

        this.add(this.getViewCombo(), new FormData("20%")); //$NON-NLS-1$
        
        detailedList = new DualListField<ItemBaseModel>();
        detailedList.setMode(Mode.INSERT);  
        detailedList.setFieldLabel("Fields");   //$NON-NLS-1$
        
        from = detailedList.getFromList();
        from.setDisplayField("name"); //$NON-NLS-1$
        ListStore<ItemBaseModel> fromStore = new ListStore<ItemBaseModel>();  
        from.setStore(fromStore);
        
        to = detailedList.getToList();
        to.setDisplayField("name");   //$NON-NLS-1$
        ListStore<ItemBaseModel> store = new ListStore<ItemBaseModel>();  
        to.setStore(store);
        
        this.add(detailedList, new FormData("50%"));  //$NON-NLS-1$
        
        Button save = new Button(MessagesFactory.getMessages().save_btn());
        save.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent event) {
                if(!NewTablePanel.this.isValid())
                    return;
                
                List<ItemBaseModel> entityModelList = Itemsbrowser2.getSession().getEntitiyModelList();
                for(ItemBaseModel model : entityModelList){
                    if(tableName.getValue().trim().equalsIgnoreCase((String)model.get("name"))){ //$NON-NLS-1$
                        MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().add_table_duplicated(), null);
                        return;
                    }
                }
                
                if(Itemsbrowser2.getSession().getCustomizeModelList() != null){
                    entityModelList = Itemsbrowser2.getSession().getCustomizeModelList();
                    for(ItemBaseModel model : entityModelList){
                        if(tableName.getValue().trim().equalsIgnoreCase((String)model.get("name"))){ //$NON-NLS-1$
                            MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().add_table_duplicated(), null);
                            return;
                        }
                    }
                }
                
                final ListStore<ItemBaseModel> list = to.getStore();
                if(list.getModels().size() == 0){
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().add_table_empty_field(), null);
                    return;
                }
                
                String conceptName = currentBean.getBindingEntityModel().getConceptName();               
                
                service.getMandatoryFieldList(conceptName, new SessionAwareAsyncCallback<List<String>>() {
                    
                    public void onSuccess(List<String> fieldList) {
                        
                        List<ItemBaseModel> fromModelList = from.getStore().getModels();
                        Set<String> fromSet = new HashSet<String>();
                        for(ItemBaseModel model : fromModelList)
                            fromSet.add((String)model.get("name")); //$NON-NLS-1$
                        
                        for(String name : fieldList){
                            if(fromSet.contains(name)){
                                MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages().add_table_primary_key(), null);
                                return;
                            }                                
                        }
                        
                        ViewBean vb = new ViewBean();
                        vb.setBindingEntityModel(currentBean.getBindingEntityModel());
                        vb.setSearchables(currentBean.getSearchables());
                        vb.setViewPK(currentBean.getViewPK());
                        vb.setDescription(currentBean.getDescription());
                        vb.setDescriptionLocalized(currentBean.getDescriptionLocalized());

                        String[] viewables = new String[list.getModels().size()];
                        int i = 0;
                        for(ItemBaseModel model : list.getModels()){
                            String path = (String) model.get("value"); //$NON-NLS-1$
                            vb.addViewableXpath(path);
                            viewables[i] = path;
                            i++;
                        }
                        vb.setViewables(viewables);
                        
                        if(Itemsbrowser2.getSession().getCustomizeModelList() == null){
                            Itemsbrowser2.getSession().put(UserSession.CUSTOMIZE_MODEL_LIST, new ArrayList<ItemBaseModel>());
                        }
                        
                        if(Itemsbrowser2.getSession().getCustomizeModelViewMap() == null){
                            Itemsbrowser2.getSession().put(UserSession.CUSTOMIZE_MODEL_VIEW_MAP, new HashMap<ItemBaseModel, ViewBean>());
                        }
                        
                        ItemBaseModel model = new ItemBaseModel();
                        model.set("name", tableName.getValue()); //$NON-NLS-1$
                        model.set("value", vb.getViewPK()); //$NON-NLS-1$
                        Itemsbrowser2.getSession().getCustomizeModelList().add(model);
                        Itemsbrowser2.getSession().getCustomizeModelViewMap().put(model, vb);
                        toolbar.addOption(model);
                    }
                    
                    @Override
                    protected void doOnFailure(Throwable caught) {
                        Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                    }
                });                
            }
        });
        this.add(save);
    }

    public void setToolbar(ItemsToolBar toolbar) {
        this.toolbar = toolbar;
    }

    private ComboBox<ItemBaseModel> getViewCombo(){
        List<ItemBaseModel> viewList = Itemsbrowser2.getSession().getEntitiyModelList();
        ListStore<ItemBaseModel> viewStoreList = new ListStore<ItemBaseModel>();
        viewStoreList.add(viewList);
        
        viewCombo = new ComboBox<ItemBaseModel>();
        viewCombo.setId("viewCombo"); //$NON-NLS-1$
        viewCombo.setFieldLabel("View"); //$NON-NLS-1$
        viewCombo.setDisplayField("name");//$NON-NLS-1$
        viewCombo.setValueField("value");//$NON-NLS-1$
        viewCombo.setStore(viewStoreList);
        viewCombo.setTypeAhead(true);
        viewCombo.setTriggerAction(TriggerAction.ALL);
        viewCombo.setAllowBlank(false);
        
        viewCombo.addSelectionChangedListener(new SelectionChangedListener<ItemBaseModel>() {
            
            @Override
            public void selectionChanged(SelectionChangedEvent<ItemBaseModel> se) {
                String viewName = (String) se.getSelectedItem().get("value"); //$NON-NLS-1$
                final String language = Locale.getLanguage(Itemsbrowser2.getSession().getAppHeader());
                service.getView(viewName, language, new SessionAwareAsyncCallback<ViewBean>() {
                    
                    public void onSuccess(ViewBean viewBean) {
                        currentBean = viewBean;
                        List<ItemBaseModel> modelList = NewTablePanel.this.convertViewBean2ModelList(viewBean, language);
                        from.getStore().removeAll();
                        from.getStore().add(modelList);
                                                
                        to.getStore().removeAll();
                        to.reset();
                    }
                    
                    @Override
                    protected void doOnFailure(Throwable caught) {
                        Dispatcher.forwardEvent(ItemsEvents.Error, caught);
                    }
                });
            }
        });
        
        return viewCombo;
    }
    
    private List<ItemBaseModel> convertViewBean2ModelList(ViewBean viewBean, String language){
        List<String> viewableXpaths = viewBean.getViewableXpaths();
        EntityModel entityModel = viewBean.getBindingEntityModel();
        Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
        List<ItemBaseModel> storeList = new ArrayList<ItemBaseModel>();
        
        for (String xpath : viewableXpaths) {
            ItemBaseModel model = new ItemBaseModel();
            TypeModel typeModel = dataTypes.get(xpath);
            model.set("name", ViewUtil.getViewableLabel(language, typeModel)); //$NON-NLS-1$
            model.set("value", xpath); //$NON-NLS-1$
            storeList.add(model);
        }
        return storeList;
    }
     
}
