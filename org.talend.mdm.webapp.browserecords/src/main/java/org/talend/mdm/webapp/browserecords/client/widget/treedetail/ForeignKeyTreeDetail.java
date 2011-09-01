package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.Arrays;

import org.talend.mdm.webapp.browserecords.client.BrowseRecords;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.util.UserSession;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.TabItem;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.layout.FormLayout;
import com.extjs.gxt.ui.client.widget.treegrid.TreeGrid;
import com.extjs.gxt.ui.client.widget.treegrid.WidgetTreeGridCellRenderer;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

public class ForeignKeyTreeDetail extends TabItem {

    ItemNodeModel model = null;

    public ForeignKeyTreeDetail(ItemNodeModel itemNodeModel) {
        model = itemNodeModel;
    }

    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        // setLayout(new FlowLayout(10));

        // ItemNodeModel model = getTreeModel();

        TreeStore<ModelData> store = new TreeStore<ModelData>();
        store.add(model.getChildren(), true);

        ColumnConfig name = new ColumnConfig("name", "Name", 1000); //$NON-NLS-1$ //$NON-NLS-2$
        name.setRenderer(new WidgetTreeGridCellRenderer<ModelData>() {

            @Override
            public Widget getWidget(ModelData model, String property, ColumnData config, int rowIndex, int colIndex,
                    ListStore<ModelData> store, Grid<ModelData> grid) {
                ItemNodeModel itemNode = ((ItemNodeModel) model);
                HorizontalPanel hp = new HorizontalPanel();
                // create Field
                String xPath = itemNode.getBindingPath();
                ViewBean viewBean = (ViewBean) BrowseRecords.getSession().get(UserSession.CURRENT_VIEW);
                TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(xPath);
                if (typeModel.isSimpleType()) {
                    Field field = TreeDetailGridFieldCreator.createField(typeModel, Locale.getLanguage());

                    HTML label = new HTML();
                    String html = (String) model.get(property);
                    if (itemNode.isKey() || itemNode.getMinOccurs() >= 1)
                        html = html + "<span style=\"color:red\"> *</span>";
                    label.setHTML(html);
                    hp.add(label);

                    field.setWidth(260);
                    hp.add(field);

                    Image addNodeImg = new Image("/talendmdm/secure/img/genericUI/add.png");
                    addNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
                    Image removeNodeImg = new Image("/talendmdm/secure/img/genericUI/delete.png");
                    removeNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
                    if (itemNode.getMinOccurs() >= 1 && itemNode.getMaxOccurs() > itemNode.getMinOccurs()) {
                        hp.add(addNodeImg);
                        hp.add(removeNodeImg);
                    }

                    hp.setCellWidth(label, "200px"); //$NON-NLS-1$

                }
                return hp;

            }
        });

        ColumnModel cm = new ColumnModel(Arrays.asList(name));

        ContentPanel cp = new ContentPanel();
        cp.setHeaderVisible(false);
        FormLayout fl = new FormLayout();
        fl.setLabelWidth(100);
        cp.setLayout(fl);
        cp.setBodyBorder(false);
        //        cp.setHeading("Widget Renderer TreeGrid"); //$NON-NLS-1$
        cp.setButtonAlign(HorizontalAlignment.CENTER);
        cp.setFrame(true);
        cp.setSize(700, 300);

        TreeGrid<ModelData> tree = new TreeGrid<ModelData>(store, cm);
        tree.setBorders(true);
        tree.setAutoExpandColumn("name"); //$NON-NLS-1$
        tree.setView(new TreeDetailGridView());
        tree.getTreeView().setRowHeight(26);
        tree.setHideHeaders(false);
        // tree.setAutoWidth(true);
        cp.add(tree);

        add(cp);

        setText("FK Demo"); //$NON-NLS-1$
    }

    // public static ItemNodeModel getTreeModel() {
    // // ItemNodeModel id2Model = new ItemNodeModel("id2");
    // // id2Model.setMinOccurs(1);
    // // id2Model.setMaxOccurs(3);
    //        //        ItemNodeModel productModel = new ItemNodeModel("FKId", new ItemNodeModel[] { new ItemNodeModel("id1"), id2Model, //$NON-NLS-1$//$NON-NLS-2$ 
    // // new ItemNodeModel("id3") });
    // // productModel.setKey(true);
    // // productModel.setMinOccurs(1);
    //        //        ItemNodeModel[] models = new ItemNodeModel[] { productModel, new ItemNodeModel("FKName") }; //$NON-NLS-1$//$NON-NLS-2$
    //        //        ItemNodeModel root = new ItemNodeModel("product", models); //$NON-NLS-1$
    //
    // return root;
    // }

}
