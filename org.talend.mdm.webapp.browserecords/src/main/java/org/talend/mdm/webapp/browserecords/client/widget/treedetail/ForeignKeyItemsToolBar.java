package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsServiceAsync;
import org.talend.mdm.webapp.browserecords.client.creator.CellEditorCreator;
import org.talend.mdm.webapp.browserecords.client.creator.CellRendererCreator;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemBaseModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemBean;
import org.talend.mdm.webapp.browserecords.client.model.ItemResult;
import org.talend.mdm.webapp.browserecords.client.model.MultipleCriteria;
import org.talend.mdm.webapp.browserecords.client.model.QueryModel;
import org.talend.mdm.webapp.browserecords.client.model.SimpleCriterion;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.client.util.ViewUtil;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKey.FKRelRecordWindow;
import org.talend.mdm.webapp.browserecords.client.widget.SearchPanel.SimpleCriterionPanel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.creator.FieldCreator;
import org.talend.mdm.webapp.browserecords.shared.EntityModel;
import org.talend.mdm.webapp.browserecords.shared.SimpleTypeModel;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.toolbar.FillToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;


public class ForeignKeyItemsToolBar extends ToolBar {

    private static String userCluster = null;

    private SimpleCriterionPanel<?> simplePanel;

    public final Button searchBut = new Button(MessagesFactory.getMessages().search_btn());

    private Button createBtn = new Button(MessagesFactory.getMessages().create_btn());

    private BrowseRecordsServiceAsync service = (BrowseRecordsServiceAsync) Registry.get(BrowseRecords.BROWSERECORDS_SERVICE);

    @SuppressWarnings("unused")
    private List<ItemBaseModel> userCriteriasList;

    private ListStore<ItemBaseModel> tableList = new ListStore<ItemBaseModel>();

    private ItemBaseModel currentModel = null;

    private ComboBox<ItemBaseModel> combo = null;

    private ViewBean tableView;

    private FKRelRecordWindow relWindow = new FKRelRecordWindow();

    private ForeignKeyTreeDetail listPanel;

    public ForeignKeyTreeDetail getListPanel() {
        return listPanel;
    }

    public void setListPanel(ForeignKeyTreeDetail listPanel) {
        this.listPanel = listPanel;
    }

    private ForeignKeyTablePanel tablePanel;

    public ForeignKeyTablePanel getTablePanel() {
        return tablePanel;
    }

    public void setTablePanel(ForeignKeyTablePanel tablePanel) {
        this.tablePanel = tablePanel;
    }

    protected void onDetach() {
        super.onDetach();
    }

    public ForeignKeyItemsToolBar(ForeignKeyTreeDetail listPanel, ForeignKeyTablePanel tablePanel) {
        this.listPanel = listPanel;
        this.tablePanel = tablePanel;
        // init user saved model
        userCluster = BrowseRecords.getSession().getAppHeader().getDatacluster();
        this.setBorders(false);
        initToolBar();
        tablePanel.fkToolBar = this;
        relWindow.setHeading(MessagesFactory.getMessages().fk_RelatedRecord());
    }

    public void setQueryModel(QueryModel qm) {
        qm.setDataClusterPK(userCluster);
        qm.setView(listPanel.getViewBean());
        qm.setModel(listPanel.getViewBean().getBindingEntityModel());

            SimpleCriterion simpCriterion = simplePanel.getCriteria();
            MultipleCriteria criteriaStore = (MultipleCriteria) BrowseRecords.getSession().get(
                    UserSession.CUSTOMIZE_CRITERION_STORE);
            if (criteriaStore == null) {
                criteriaStore = new MultipleCriteria();
                criteriaStore.setOperator("AND"); //$NON-NLS-1$
            } else {
                BrowseRecords.getSession().getCustomizeCriterionStore().getChildren().clear();
            }
            criteriaStore.add(simpCriterion);
            BrowseRecords.getSession().put(UserSession.CUSTOMIZE_CRITERION_STORE, criteriaStore);
            qm.setCriteria(simplePanel.getCriteria().toString());

    }

    public void updateToolBar(ViewBean viewBean) {
        simplePanel.updateFields(viewBean);
        // reset search results
        tablePanel.resetGrid();

        String concept = viewBean.getBindingEntityModel().getConceptName();
        updateUserCriteriasList(concept);
    }

    public static int getSuccessItemsNumber(List<ItemResult> results) {
        int itemSuccessNumber = 0;
        for (ItemResult result : results) {
            if (result.getStatus() == ItemResult.SUCCESS) {
                itemSuccessNumber++;
            }
        }
        return itemSuccessNumber;
    }

    public static int getFailureItemsNumber(List<ItemResult> results) {
        int itemFailureNumber = 0;
        for (ItemResult result : results) {
            if (result.getStatus() == ItemResult.FAILURE) {
                itemFailureNumber++;
            }
        }
        return itemFailureNumber;
    }

    public int getSelectItemNumber() {
        int number = 0;
        number = tablePanel.getGrid().getSelectionModel().getSelectedItems().size();
        return number;
    }

    @SuppressWarnings("rawtypes")
    private void initToolBar() {
        createBtn.setIcon(AbstractImagePrototype.create(Icons.INSTANCE.Create()));
        createBtn.setEnabled(false);
        add(createBtn);
        createBtn.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

            }

        });

        add(new FillToolItem());

        if (BrowseRecords.getSession().getEntitiyModelList() == null) {
            service.getViewsList(Locale.getLanguage(), new AsyncCallback<List<ItemBaseModel>>() {

                public void onSuccess(List<ItemBaseModel> modelList) {
                    BrowseRecords.getSession().put(UserSession.ENTITY_MODEL_LIST, modelList);
                }

                public void onFailure(Throwable caught) {
                    Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
                }
            });
        }

        simplePanel = new SimpleCriterionPanel(null, null, searchBut);
        add(simplePanel);
        updateToolBar(listPanel.getViewBean());
        // add simple search button
        searchBut.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (simplePanel.getCriteria() != null) {
                    ViewBean viewBean = listPanel.getViewBean();
                    EntityModel entityModel = viewBean.getBindingEntityModel();

                    // TODO update columns
                    List<ColumnConfig> ccList = new ArrayList<ColumnConfig>();
                    CheckBoxSelectionModel<ItemBean> sm = new CheckBoxSelectionModel<ItemBean>();
                    sm.setSelectionMode(SelectionMode.MULTI);
                    ccList.add(sm.getColumn());
                    List<String> viewableXpaths = viewBean.getViewableXpaths();
                    Map<String, TypeModel> dataTypes = entityModel.getMetaDataTypes();
                    List<String> keys = Arrays.asList(entityModel.getKeys());
                    for (String xpath : viewableXpaths) {
                        TypeModel typeModel = dataTypes.get(xpath);

                        ColumnConfig cc = new ColumnConfig(xpath, typeModel == null ? xpath : ViewUtil.getViewableLabel(Locale.getLanguage(),
                                typeModel), 200);
                        if (typeModel instanceof SimpleTypeModel && !keys.contains(xpath)) {
                            Field<?> field = FieldCreator.createField((SimpleTypeModel) typeModel, null, false, Locale.getLanguage());

                            CellEditor cellEditor = CellEditorCreator.createCellEditor(field);
                            if (cellEditor != null) {
                                cc.setEditor(cellEditor);
                            }
                        }

                        if (typeModel != null) {
                            GridCellRenderer<ModelData> renderer = CellRendererCreator.createRenderer(typeModel, xpath);
                            if (renderer != null) {
                                cc.setRenderer(renderer);
                            }
                        }

                        ccList.add(cc);
                    }

                    tablePanel.updateGrid(sm, ccList);
                    resizeAfterSearch();
                } else {
                    MessageBox.alert(MessagesFactory.getMessages().error_title(), MessagesFactory.getMessages()
                            .advsearch_lessinfo(), new Listener<MessageBoxEvent>() {

                        public void handleEvent(MessageBoxEvent be) {
                            simplePanel.focusField();
                        }
                    });
                }
            }

        });
        searchBut.fireEvent(Events.Select);
        add(searchBut);

    }

    private void updateUserCriteriasList(String concept) {
        service.getUserCriterias(concept,
                new AsyncCallback<List<ItemBaseModel>>() {

                    public void onFailure(Throwable caught) {
                        Dispatcher.forwardEvent(BrowseRecordsEvents.Error, caught);
                    }

                    public void onSuccess(List<ItemBaseModel> list) {
                        userCriteriasList = list;
                    }

                });
    }

    private void resizeAfterSearch() {

    }

    public void addOption(ItemBaseModel model) {
        tableList.add(model);
        combo.setStore(tableList);

        combo.setValue(model);
    }

    public ItemBaseModel getCurrentModel() {
        return currentModel;
    }

    public void setCurrentModel(ItemBaseModel currentModel) {
        this.currentModel = currentModel;
    }

    public ViewBean getTableView() {
        return tableView;
    }

    public void setTableView(ViewBean tableView) {
        this.tableView = tableView;
    }

    public void renderTreeDetail(ItemBean itemBean) {
        listPanel.addTreeDetail(itemBean);
    }
}