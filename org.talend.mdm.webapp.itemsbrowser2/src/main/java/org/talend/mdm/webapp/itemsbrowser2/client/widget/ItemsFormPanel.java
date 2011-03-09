/*
 * Ext GWT - Ext for GWT Copyright(c) 2007-2009, Ext JS, LLC. licensing@extjs.com
 * 
 * http://extjs.com/license
 */
package org.talend.mdm.webapp.itemsbrowser2.client.widget;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.itemsbrowser2.client.ItemsView;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemBean;
import org.talend.mdm.webapp.itemsbrowser2.client.model.ItemFormBean;
import org.talend.mdm.webapp.itemsbrowser2.client.util.CommonUtil;
import org.talend.mdm.webapp.itemsbrowser2.shared.TypeModel;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.Registry;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.binding.FieldBinding;
import com.extjs.gxt.ui.client.binding.FormBinding;
import com.extjs.gxt.ui.client.binding.SimpleComboBoxFieldBinding;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Component;
import com.extjs.gxt.ui.client.widget.Composite;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Window;
import com.google.gwt.xml.client.Document;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.XMLParser;

public class ItemsFormPanel extends Composite {

    ItemFormBean itemFormBean;

    FormPanel content = new FormPanel();

    RecordToolBar toolbar = new RecordToolBar();

    private FormBinding formBindings;

    public ItemsFormPanel() {
        content.setHeaderVisible(false);
        content.setScrollMode(Scroll.AUTO);
        content.setTopComponent(toolbar);
        this.initComponent(content);
    }

    public ItemsFormPanel(ItemFormBean itemFormBean) {
        this();
        this.itemFormBean = itemFormBean;
    }

    public void setItemFormBean(ItemFormBean itemFormBean) {
        this.itemFormBean = itemFormBean;
    }

    public String getDisplayTitle() {
        String title = "Item's form";
        if (itemFormBean != null)
            title = itemFormBean.getName();
        return title;
    }

    // private Widget buildItemGroup(ItemFormLineBean lineBean){
    // FieldSet fs = new FieldSet();
    // fs.setHeading(lineBean.getFieldLabel());
    // fs.setCollapsible(true);
    //		
    // FormLayout layout = new FormLayout();
    // layout.setLabelWidth(75);
    // fs.setLayout(layout);
    //		
    // List<ItemFormLineBean> children = lineBean.getChildren();
    // for (ItemFormLineBean child : children){
    // Widget field = buildItem(child);
    // fs.add(field);
    // }
    // return fs;
    // }

    // private Widget buildItem(ItemFormLineBean lineBean){
    //		
    // List<ItemFormLineBean> children = lineBean.getChildren();
    // if (children != null && children.size() > 0){
    // return buildItemGroup(lineBean);
    // } else {
    // Field<Serializable> field = FieldCreator.createField(lineBean.getFieldType());
    // field.setFieldLabel(lineBean.getFieldLabel());
    // field.setValue(lineBean.getFieldValue());
    // return field;
    // }
    // }

    // public void showItem() {
    // showItem(null,false);
    // }
    //
    // public void showItem(ItemFormBean _itemForm, boolean override) {
    // if(override)setItemFormBean(_itemForm);
    //
    // content.removeAll();
    // if (itemFormBean != null) {
    // Iterator<ItemFormLineBean> lineIter = itemFormBean.iteratorLine();
    // while (lineIter.hasNext()){
    // ItemFormLineBean lineBean = lineIter.next();
    // Widget w = buildItem(lineBean);
    // content.add(w);
    // }
    // }
    // content.layout(true);
    // }

    public void paint(ViewBean viewBean) {
        content.removeAll();
        List<String> viewableXpaths = viewBean.getViewableXpaths();
        Map<String, TypeModel> dataTypes = viewBean.getMetaDataTypes();
        String concept = CommonUtil.getConceptFromBrowseItemView(viewBean.getViewPK());
        List<SimpleComboBox> comboBoxes = new ArrayList<SimpleComboBox>();

        TypeModel typeModel = dataTypes.get(concept);
        toolbar.updateToolBar();
        Component f = FieldCreator.createField(typeModel, comboBoxes, true);
        if (f != null) {
            content.add(f);
        }
        formBindings = new FormBinding(content);
        for (SimpleComboBox comboBox : comboBoxes) {
            formBindings.addFieldBinding(new SimpleComboBoxFieldBinding(comboBox, comboBox.getName()));
        }
        formBindings.autoBind();
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        if (itemsSearchContainer.getItemsListPanel().getGrid() != null) {
            ListStore<ItemBean> store = itemsSearchContainer.getItemsListPanel().getGrid().getStore();
            formBindings.setStore(store);
        }
        content.layout(true);
    }

    public void refreshGrid() {
        ItemsSearchContainer itemsSearchContainer = Registry.get(ItemsView.ITEMS_SEARCH_CONTAINER);
        if (itemsSearchContainer.getItemsListPanel().getGrid() != null) {
            itemsSearchContainer.getItemsListPanel().getStore().getLoader().load();
        }
    }

    public ItemBean getNewItemBean() {
        ItemBean item = (ItemBean) formBindings.getModel();
        String concept = item.getConcept();

        Document doc = XMLParser.createDocument();
        Map<String, Element> elementSet = new HashMap<String, Element>();
        for (FieldBinding fb : formBindings.getBindings()) {
            Field field = fb.getField();
            Object value = null;
            if (field instanceof SimpleComboBox) {
                value = ((SimpleComboBox) field).getValue().get("value"); //$NON-NLS-1$
            }
            if (field instanceof DateField) {
                value = DateTimeFormat.getFormat("yyyy-MM-dd").format((Date) field.getValue()); //$NON-NLS-1$
            } else {
                value = field.getValue();
            }
            createElements(field.getName(), value == null ? "" : value.toString(), elementSet, doc); //$NON-NLS-1$
        }
        Element el = elementSet.get(concept);
        doc.appendChild(el);
        item.setItemXml(doc.toString());
        Window.alert(item.getItemXml());
        return item;
    }

    private void createElements(String xpath, String value, Map<String, Element> elementSet, Document doc) {
        Element parent = null;
        String[] xps = xpath.split("/"); //$NON-NLS-1$
        StringBuffer sb = new StringBuffer();
        boolean isFirst = true;
        for (String xp : xps) {
            if (isFirst) {
                sb.append(xp);
                isFirst = false;
            } else {
                sb.append("/" + xp); //$NON-NLS-1$
            }
            Element tempEl = elementSet.get(sb.toString());
            if (tempEl == null) {
                tempEl = (Element) doc.createElement(replace(xp));
                elementSet.put(sb.toString(), tempEl);
            }
            if (parent != null) {
                parent.appendChild(tempEl);
            }
            parent = tempEl;
        }
        parent.appendChild(doc.createTextNode(value));
    }

    private String replace(String elName) {
        return elName.replaceAll("\\[\\d+\\]", ""); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public ItemBean getItemBean() {
        return (ItemBean) formBindings.getModel();
    }

    public void bind(ModelData modelData) {
        if (modelData != null) {
            formBindings.bind(modelData);
        } else {
            formBindings.unbind();
        }
    }

    public void unbind() {
        formBindings.unbind();
    }

    public void setReadOnly(ModelData modelData, String[] keys) {
        // primary key is readonly when updating
        if (!((ItemBean) modelData).getIds().equals("")) //$NON-NLS-1$
            for (Field<?> field : content.getFields()) {
                for (String key : keys) {
                    if (field.getName().equals(key)) {
                        field.setEnabled(false);
                        continue;
                    }
                }
            }
    }
}
