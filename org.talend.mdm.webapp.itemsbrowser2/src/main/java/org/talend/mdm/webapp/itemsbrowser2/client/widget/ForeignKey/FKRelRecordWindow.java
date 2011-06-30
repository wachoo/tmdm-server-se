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
package org.talend.mdm.webapp.itemsbrowser2.client.widget.ForeignKey;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsServiceAsync;
import org.talend.mdm.webapp.itemsbrowser2.client.Itemsbrowser2;
import org.talend.mdm.webapp.itemsbrowser2.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ForeignKeyDrawer;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBasePageLoadResult;
import org.talend.mdm.webapp.itemsbrowser2.client.model.Restriction;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.client.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield.ComboBoxField;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.data.BaseListLoadResult;
import com.extjs.gxt.ui.client.data.BaseListLoader;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoadResult;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ListLoadResult;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.RpcProxy;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FlowData;
import com.extjs.gxt.ui.client.widget.toolbar.PagingToolBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

/**
 * DOC Administrator class global comment. Detailled comment
 */
public class FKRelRecordWindow extends Window {

    private static final int COLUMN_WIDTH = 100;

    private static final int WINDOW_WIDTH = 450;

    private static final int WINDOW_HEIGH = 300;

    private Grid<ForeignKeyBean> relatedRecordGrid;

    private String fkKey;

    private ReturnCriteriaFK returnCriteriaFK;
    
    private ListStore<BaseModel> typeList = new ListStore<BaseModel>();

    private ItemsServiceAsync service = (ItemsServiceAsync) Registry.get(Itemsbrowser2.ITEMS_SERVICE);

    private PagingLoader<PagingLoadResult<ModelData>> loader;

    private int pageSize = 20;

    TextField<String> filter = new TextField<String>();

	private ComboBoxField<BaseModel> typeComboBox;
    
    public FKRelRecordWindow() {

    }
    
    public FKRelRecordWindow(String fkKey, ReturnCriteriaFK returnCriteriaFK) {
        this.fkKey = fkKey;
        this.returnCriteriaFK = returnCriteriaFK;
    }

    public String getFkKey() {
        return fkKey;
    }

    public void setFkKey(String fkKey) {
        this.fkKey = fkKey;
    }

    public ReturnCriteriaFK getReturnCriteriaFK() {
        return returnCriteriaFK;
    }

    public void setReturnCriteriaFK(ReturnCriteriaFK returnCriteriaFK) {
        this.returnCriteriaFK = returnCriteriaFK;
    }

    private String getFilterValue(){
        String value = filter.getRawValue();
        if (value == null || value.trim().equals("")){
            value = ".*"; //$NON-NLS-1$
        }
        return value;
    }
    
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        final TypeModel typeModel= Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey);
        RpcProxy<PagingLoadResult<ForeignKeyBean>> proxy = new RpcProxy<PagingLoadResult<ForeignKeyBean>>() {

            public void load(final Object loadConfig, final AsyncCallback<PagingLoadResult<ForeignKeyBean>> callback) {
                service.getForeignKeyList((PagingLoadConfig) loadConfig, typeModel, Itemsbrowser2.getSession().getAppHeader().getDatacluster(), false, getFilterValue(),
                        new AsyncCallback<ItemBasePageLoadResult<ForeignKeyBean>>() {

                            public void onFailure(Throwable caught) {
                                callback.onFailure(caught);
                            }

                            public void onSuccess(ItemBasePageLoadResult<ForeignKeyBean> result) {
                                callback.onSuccess(new BasePagingLoadResult<ForeignKeyBean>(result.getData(), result.getOffset(),
                                        result.getTotalLength()));
                            }

                        });

            }
        };

        RpcProxy<BaseListLoadResult<BaseModel>> proxy1 = new RpcProxy<BaseListLoadResult<BaseModel>>() {

            public void load(final Object loadConfig, final AsyncCallback<BaseListLoadResult<BaseModel>> callback) {
                service.getForeignKeyPolymTypeList(typeModel.getForeignkey(), "en", new AsyncCallback<List<Restriction>>() {//$NON-NLS-1$

                    public void onFailure(Throwable caught) {
                        callback.onFailure(caught);
                    }

                    public void onSuccess(List<Restriction> result) {
                    	List<BaseModel> list=new ArrayList<BaseModel>();
                    	for(Restriction re: result){
                    		BaseModel model=new BaseModel();
                    		model.set("name", re.getName());//$NON-NLS-1$
                    		model.set("value", re.getValue());//$NON-NLS-1$
                    		list.add(model);
                    	}
                        callback.onSuccess(new BaseListLoadResult<BaseModel>(list));
                    }
                });
            }
        };
        BaseListLoader<ListLoadResult<Restriction>> loader1 = new BaseListLoader<ListLoadResult<Restriction>>(proxy1);
        typeList=new ListStore<BaseModel>(loader1);        
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        final ListStore<ForeignKeyBean> store = new ListStore<ForeignKeyBean>(loader);

        FormPanel panel = new FormPanel();
        panel.setFrame(false);
        panel.setLayout(new FitLayout());
        panel.setHeaderVisible(false);
        panel.setSize(WINDOW_WIDTH, WINDOW_HEIGH);
        panel.setHeaderVisible(false);
        
        filter.addListener(Events.KeyUp, new Listener<FieldEvent>() {

            public void handleEvent(FieldEvent be) {
                if (be.getKeyCode() == KeyCodes.KEY_UP){
                    ForeignKeyBean fkBean = relatedRecordGrid.getSelectionModel().getSelectedItem();
                    if (fkBean == null){
                        relatedRecordGrid.getSelectionModel().select(store.getCount() - 1, true);
                    } else {
                        relatedRecordGrid.getSelectionModel().selectPrevious(false);
                        filter.focus();
                    }
                    return;
                }
                if (be.getKeyCode() == KeyCodes.KEY_DOWN){
                    ForeignKeyBean fkBean = relatedRecordGrid.getSelectionModel().getSelectedItem();
                    if (fkBean == null){
                        relatedRecordGrid.getSelectionModel().select(0, true);
                    } else {
                        relatedRecordGrid.getSelectionModel().selectNext(false);
                        filter.focus();
                    }
                    return;
                }
                if (be.getKeyCode() == KeyCodes.KEY_ENTER){
                    returnCriteriaFK.setCriteriaFK(relatedRecordGrid.getSelectionModel().getSelectedItem());
                    close();
                }
                if (be.getKeyCode() == KeyCodes.KEY_LEFT || be.getKeyCode() == KeyCodes.KEY_RIGHT){
                    return;
                }
                loader.load(0, pageSize);
            }
        });
        filter.setWidth(WINDOW_WIDTH - 80);

        ToolBar toolBar = new ToolBar();
        
        typeComboBox = new ComboBoxField<BaseModel>();
        typeComboBox.setDisplayField("name"); //$NON-NLS-1$
        typeComboBox.setValueField("value"); //$NON-NLS-1$
        typeComboBox.setStore(typeList);
        typeComboBox.setTriggerAction(TriggerAction.ALL);
        typeComboBox.setEmptyText(MessagesFactory.getMessages().label_select_type());
        typeComboBox.setId("DerivedTypeComboBox"); //$NON-NLS-1$
        
        typeComboBox.addSelectionChangedListener(new SelectionChangedListener<BaseModel>() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent<BaseModel> se) {
				String targetType=se.getSelectedItem().get("value").toString();//$NON-NLS-1$
				String fkInfo=typeModel.getForeignKeyInfo().size()>0?typeModel.getForeignKeyInfo().get(0):null;
				service.switchForeignKeyType(targetType, typeModel.getForeignkey(), fkInfo, getFilterValue(), new AsyncCallback<ForeignKeyDrawer>(){

                            public void onFailure(Throwable arg0) {

                     }

					public void onSuccess(ForeignKeyDrawer fkDrawer) {
						typeModel.setForeignkey(fkDrawer.getXpathForeignKey());
						List<String> fkinfo=new ArrayList<String>();
						fkinfo.add(fkDrawer.getXpathInfoForeignKey());
						typeModel.setForeignKeyInfo(fkinfo);
						loader.load(0, pageSize);
					}					
				});				
			}
		});
        toolBar.add(typeComboBox);
        
        Button filterBtn = new Button();
        filterBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.funnel()));
        filterBtn.setWidth(30);
        filter.setWidth(200);
        typeComboBox.setWidth(WINDOW_WIDTH-250);
        toolBar.add(filterBtn);
        toolBar.add(filter);
        panel.setTopComponent(toolBar);

        List<ColumnConfig> columns = new ArrayList<ColumnConfig>();
        // build columns by specify store
        final PagingToolBar pageToolBar = new PagingToolBar(pageSize);
        pageToolBar.bind(loader);

        // change label display
        boolean retrieveFKinfos = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey)
                .isRetrieveFKinfos();
        String foreignKey = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey).getForeignkey();
        if (retrieveFKinfos) {
            List<String> foreignKeyInfo = Itemsbrowser2.getSession().getCurrentEntityModel().getMetaDataTypes().get(fkKey)
                    .getForeignKeyInfo();
            for (String info : foreignKeyInfo) {
                columns.add(new ColumnConfig(CommonUtil.getElementFromXpath(info), CommonUtil.getElementFromXpath(info),
                        COLUMN_WIDTH));
            }
        } else
            columns.add(new ColumnConfig("i", CommonUtil.getElementFromXpath(foreignKey), COLUMN_WIDTH)); //$NON-NLS-1$

        ColumnModel cm = new ColumnModel(columns);
        relatedRecordGrid = new Grid<ForeignKeyBean>(store, cm);
        relatedRecordGrid.getView().setForceFit(true);
        relatedRecordGrid.setLoadMask(true);
        relatedRecordGrid.setBorders(false);
        relatedRecordGrid.addListener(Events.Attach, new Listener<GridEvent<ForeignKeyBean>>() {

            public void handleEvent(GridEvent<ForeignKeyBean> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                config.setLimit(pageSize);
                loader.load(config);
            }
        });

        relatedRecordGrid.addListener(Events.OnDoubleClick, new Listener<GridEvent<ForeignKeyBean>>() {

            public void handleEvent(final GridEvent<ForeignKeyBean> be) {
                returnCriteriaFK.setCriteriaFK(be.getModel());
                close();
            }
        });

        panel.add(relatedRecordGrid);
        panel.setBottomComponent(pageToolBar);

        Button cancelBtn = new Button(MessagesFactory.getMessages().cancel_btn());
        cancelBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            public void componentSelected(ButtonEvent ce) {
                close();
            }
        });
        addButton(cancelBtn);
        add(panel, new FlowData(5));

    }
}
