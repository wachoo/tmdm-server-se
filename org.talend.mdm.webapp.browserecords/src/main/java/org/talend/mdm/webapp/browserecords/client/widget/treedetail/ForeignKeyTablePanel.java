package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.base.client.widget.PagingToolBarEx;
import org.talend.mdm.webapp.base.shared.SimpleTypeModel;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKeyRowEditor;
import org.talend.mdm.webapp.browserecords.client.widget.ItemDetailToolBar;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKey.ReturnCriteriaFK;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.celleditor.ForeignKeyCellEditor;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.creator.FieldCreator;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Style.HideMode;
import com.extjs.gxt.ui.client.data.BasePagingLoadConfig;
import com.extjs.gxt.ui.client.data.BasePagingLoader;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.PagingLoadConfig;
import com.extjs.gxt.ui.client.data.PagingLoadResult;
import com.extjs.gxt.ui.client.data.PagingLoader;
import com.extjs.gxt.ui.client.data.PagingModelMemoryProxy;
import com.extjs.gxt.ui.client.data.TreeModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.GridEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.grid.CellEditor;
import com.extjs.gxt.ui.client.widget.grid.CheckBoxSelectionModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.google.gwt.user.client.ui.Image;

public class ForeignKeyTablePanel extends ContentPanel implements ReturnCriteriaFK {

    private static final int COLUMN_WIDTH = 100;

    private final static int PAGE_SIZE = 10;

    private PagingToolBarEx pagingBar = null;

    Grid<ItemNodeModel> grid;

    ToolBar toolBar = new ToolBar();

    ToolBar firstToolBar = new ToolBar();

    Button addFkButton = new Button(MessagesFactory.getMessages().add_btn(), AbstractImagePrototype.create(Icons.INSTANCE
            .Create()));

    Button removeFkButton = new Button(MessagesFactory.getMessages().remove_btn(), AbstractImagePrototype.create(Icons.INSTANCE
            .Delete()));

    TypeModel fkTypeModel;

    List<ItemNodeModel> fkModels;

    PagingModelMemoryProxy proxy;

    PagingLoader<PagingLoadResult<ModelData>> loader;

    ListStore<ItemNodeModel> store;

    ItemNodeModel parent;

    ForeignKeyListWindow fkWindow = new ForeignKeyListWindow();

    ViewBean viewBean;

    ItemNodeModel currentNodeModel;

    ItemNodeModel lastFkModel;

    int lastFkIndex;

    public ForeignKeyTablePanel(final ViewBean viewBean, ItemNodeModel parent, final List<ItemNodeModel> fkModels,
            final TypeModel fkTypeModel, final ItemDetailToolBar parentToolBar) {
        this.setHeaderVisible(false);
        this.setLayout(new FitLayout());
        this.setAutoWidth(true);
        this.setBodyBorder(false);
        this.parent = parent;
        this.viewBean = viewBean;
        this.fkTypeModel = fkTypeModel;
        this.fkModels = fkModels;
        toolBar.add(addFkButton);
        toolBar.add(new SeparatorToolItem());
        toolBar.add(removeFkButton);
        addListener();
        LayoutContainer toolBarPanel = new LayoutContainer();
        toolBarPanel.add(toolBar);
        this.setTopComponent(toolBarPanel);

        fkWindow.setForeignKeyInfos(fkTypeModel.getForeignkey(), fkTypeModel.getForeignKeyInfo());
        fkWindow.setSize(470, 340);
        fkWindow.setResizable(false);
        fkWindow.setModal(true);
        fkWindow.setBlinkModal(true);
        fkWindow.setReturnCriteriaFK(this);
        fkWindow.setHeading(MessagesFactory.getMessages().fk_RelatedRecord());

        if (fkTypeModel.getFkFilter() != null && fkTypeModel.getFkFilter().length() != 0) {
            fkWindow.setForeignKeyFilter(fkTypeModel.getFkFilter());
        }

        proxy = new PagingModelMemoryProxy(this.fkModels);
        loader = new BasePagingLoader<PagingLoadResult<ModelData>>(proxy);
        store = new ListStore<ItemNodeModel>(loader);

        List<ColumnConfig> columnConfigs = new ArrayList<ColumnConfig>();
        final CheckBoxSelectionModel<ItemNodeModel> sm = new CheckBoxSelectionModel<ItemNodeModel>();
        columnConfigs.add(sm.getColumn());

        final ColumnConfig idColumn = new ColumnConfig("objectValue", "Id", COLUMN_WIDTH); //$NON-NLS-1$ //$NON-NLS-2$
        columnConfigs.add(idColumn);
        idColumn.setRenderer(new GridCellRenderer<ItemNodeModel>() {

            public Object render(final ItemNodeModel model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ItemNodeModel> store, Grid<ItemNodeModel> grid) {
                model.setValid(false);
                ForeignKeyBean fkBean = (ForeignKeyBean) model.getObjectValue();
                final String value = fkBean != null ? fkBean.getId() : ""; //$NON-NLS-1$
                return value;

            }
        });
        LabelField f = new LabelField();
        CellEditor idCellEditor = new CellEditor(f) {

            @Override
            public Object preProcessValue(Object v) {
                if (v != null) {
                    ForeignKeyBean fk = (ForeignKeyBean) v;
                    return super.preProcessValue(fk.getId());
                }
                return null;
            }

        };
        idColumn.setEditor(idCellEditor);
        List<String> foreignKeyInfo = fkTypeModel.getForeignKeyInfo();
        for (int i = 0; i < foreignKeyInfo.size(); i++) {
            final String info = foreignKeyInfo.get(i);
            final ColumnConfig column = new ColumnConfig("objectValue", //$NON-NLS-1$
                    CommonUtil.getElementFromXpath(info), COLUMN_WIDTH);
            column.setRenderer(new GridCellRenderer<ItemNodeModel>() {

                public Object render(ItemNodeModel model, String property, ColumnData config, int rowIndex, int colIndex,
                        ListStore<ItemNodeModel> store, Grid<ItemNodeModel> grid) {
                    ForeignKeyBean fkBean = (ForeignKeyBean) model.getObjectValue();
                    String value = fkBean != null && fkBean.getForeignKeyInfo().containsKey(info) ? fkBean.getForeignKeyInfo()
                            .get(info) : ""; //$NON-NLS-1$

                    return value;
                }

            });
            TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(info);
            Field<?> field = FieldCreator.createField((SimpleTypeModel) typeModel, null, false, Locale.getLanguage());

            CellEditor cellEditor = new ForeignKeyCellEditor(field, typeModel);
            if (cellEditor != null) {
                column.setEditor(cellEditor);
            }
            columnConfigs.add(column);
        }
        ColumnConfig columnOpt = new ColumnConfig("", "", COLUMN_WIDTH); //$NON-NLS-1$ //$NON-NLS-2$
        columnOpt.setFixed(true);
        columnOpt.setWidth(60);
        columnOpt.setRenderer(optRender);
        columnConfigs.add(columnOpt);
        ColumnModel cm = new ColumnModel(columnConfigs);
        grid = new Grid<ItemNodeModel>(store, cm);
        grid.getView().setForceFit(true);
        if (cm.getColumnCount() > 0) {
            grid.setAutoExpandColumn(cm.getColumn(0).getHeader());
        }

        grid.addListener(Events.Attach, new Listener<GridEvent<ItemNodeModel>>() {

            public void handleEvent(GridEvent<ItemNodeModel> be) {
                PagingLoadConfig config = new BasePagingLoadConfig();
                config.setOffset(0);
                int pageSize = pagingBar.getPageSize();
                config.setLimit(pageSize);
                loader.load(config);
            }
        });
        final ForeignKeyRowEditor re = new ForeignKeyRowEditor(fkTypeModel);
        grid.setSelectionModel(sm);
        grid.addPlugin(sm);
        grid.addPlugin(re);
        // grid.setWidth(Window.getClientWidth() - ItemsListPanel.getInstance().getInnerWidth());
        grid.setBorders(true);
        this.add(grid);

        pagingBar = new PagingToolBarEx(PAGE_SIZE);
        pagingBar.setHideMode(HideMode.VISIBILITY);
        pagingBar.getMessages().setDisplayMsg(MessagesFactory.getMessages().page_displaying_records());
        pagingBar.bind(loader);
        loader.setRemoteSort(true);

        this.setBottomComponent(pagingBar);
    }

    private void addListener() {
        addFkButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                addFk();
            }
        });
        removeFkButton.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

                List<ItemNodeModel> selectedFkModelList = grid.getSelectionModel().getSelectedItems();
                if (selectedFkModelList != null && selectedFkModelList.size() > 0) {
                    boolean tipMinOccurs = (fkModels.size() - selectedFkModelList.size()) < fkTypeModel.getMinOccurs();
                    for (int i = selectedFkModelList.size() - 1; i >= 0; i--) {
                        ItemNodeModel itemNodeModel = selectedFkModelList.get(i);
                        delFk(itemNodeModel);
                    }
                    // minOccurs tip
                    if (tipMinOccurs) {
                        MessageBox.alert(
                                MessagesFactory.getMessages().info_title(),
                                MessagesFactory.getMessages().fk_validate_min_occurence(
                                        fkTypeModel.getLabel(Locale.getLanguage()), fkTypeModel.getMinOccurs()), null);
                    }
                    pagingBar.refresh();
                }

            }
        });
    }

    private void addFk() {
        int min = fkTypeModel.getMinOccurs();
        int max = fkTypeModel.getMaxOccurs();
        int count = 1;
        if (min >= 0 && max > min) {
            // if (fkModels.size() < (max - min)) {
            // count = max - min;
            // }
            count = max;
        }
        if (fkModels.size() < count || max == -1) {
            ItemNodeModel lastRowModel;
            int index;
            if (fkModels.size() > 0 && fkModels.get(fkModels.size() - 1) != null) {
                lastRowModel = fkModels.get(fkModels.size() - 1);
                index = parent.indexOf(lastRowModel);
            } else {
                lastRowModel = lastFkModel;
                index = lastFkIndex;
            }
            ItemNodeModel newFkModel = lastRowModel.clone(false);
            parent.insert(newFkModel, index + 1);
            newFkModel.setParent(parent);
            fkModels.add(newFkModel);
            parent.setChangeValue(true);
            grid.getView().layout();
            pagingBar.last();
        } else {
            // maxOccurs tip
            MessageBox.alert(
                    MessagesFactory.getMessages().info_title(),
                    MessagesFactory.getMessages().fk_validate_max_occurence(fkTypeModel.getLabel(Locale.getLanguage()),
                            fkTypeModel.getMaxOccurs()), null);

        }

    }

    private void delFk(ItemNodeModel currentFkModel) {
        lastFkModel = currentFkModel;
        lastFkIndex = parent.indexOf(currentFkModel) - 1;
        int min = fkTypeModel.getMinOccurs();
        int count = 1;
        if (min > 0)
            count = min;
        else
            count = 0;
        if (fkModels.size() > count) {
            fkModels.remove(currentFkModel);
            TreeModel parent = currentFkModel.getParent();
            parent.remove(currentFkModel);
            ((ItemNodeModel) parent).setChangeValue(true);
        }
    }

    GridCellRenderer<ItemNodeModel> optRender = new GridCellRenderer<ItemNodeModel>() {

        public Object render(final ItemNodeModel model, String property, ColumnData config, final int rowIndex, int colIndex,
                final ListStore<ItemNodeModel> store, Grid<ItemNodeModel> grid) {
            Image selectFKBtn = new Image(Icons.INSTANCE.link());
            selectFKBtn.setTitle(MessagesFactory.getMessages().fk_select_title());
            selectFKBtn.getElement().getStyle().setCursor(Cursor.POINTER);

            selectFKBtn.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    fkWindow.show(viewBean);
                    currentNodeModel = model;
                }
            });
            Image linkFKBtn = new Image(Icons.INSTANCE.link_go());
            linkFKBtn.setTitle(MessagesFactory.getMessages().fk_open_title());
            linkFKBtn.getElement().getStyle().setCursor(Cursor.POINTER);
            linkFKBtn.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    ForeignKeyBean fkBean = (ForeignKeyBean) model.getObjectValue();
                    if (fkBean == null || fkBean.getId() == null || "".equals(fkBean.getId())) //$NON-NLS-1$
                        return;
                    String ids = fkBean.getId().replace("[", "").replace("]", ""); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
                    ForeignKeyUtil.checkChange(false, fkTypeModel.getForeignkey().split("/")[0], ids); //$NON-NLS-1$
                }
            });

            com.google.gwt.user.client.ui.Grid optGrid = new com.google.gwt.user.client.ui.Grid(1, 2);
            optGrid.setCellPadding(0);
            optGrid.setCellSpacing(0);
            optGrid.setWidget(0, 0, selectFKBtn);
            optGrid.setWidget(0, 1, linkFKBtn);
            return optGrid;
        }
    };

    public void setCriteriaFK(ForeignKeyBean fk) {
        // TODO check fk exist
        currentNodeModel.setObjectValue(fk);
        currentNodeModel.setChangeValue(true);
        grid.getView().refresh(false);
    }
}
