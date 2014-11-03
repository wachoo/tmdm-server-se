// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.talend.mdm.webapp.base.client.widget.MultiLanguageField;
import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeLayoutModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.inputfield.FormatTextField;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.DynamicTreeItem;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.IncrementalCommand;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Widget;

public class IncrementalBuildTree implements IncrementalCommand {

    private TreeDetail treeDetail;
    
    private ItemNodeModel itemNode;
    
    private List<ModelData> itemNodeChildren;
    
    private ViewBean viewBean;
    
    private Map<String, TypeModel> metaDataTypes;
    
    private boolean withDefaultValue;
    
    private Map<TypeModel, List<ItemNodeModel>> foreighKeyMap = new HashMap<TypeModel, List<ItemNodeModel>>();
    
    private Map<String, Field<?>> fieldMap = new HashMap<String, Field<?>>();
    
    private ItemsDetailPanel itemsDetailPanel;
    
    private String operation;
    
    private DynamicTreeItem item;
    
    int level = 0;
    
    private int index = 0;
    
    private int itemWidth = 0;
    private int offset = 0;
    
    public static final int GROUP_SIZE = 5;
    
    public int getChildCount(){
        return itemNode.getChildCount();
    }
    
    public IncrementalBuildTree(TreeDetail treeDetail, ItemNodeModel itemNode, ViewBean viewBean, boolean withDefaultValue, 
            String operation, DynamicTreeItem item, Map<String, Field<?>> fieldMap, ItemsDetailPanel itemsDetailPanel) {
        this.treeDetail = treeDetail;
        this.itemNode = itemNode;
        this.withDefaultValue = withDefaultValue;
        this.operation = operation;
        this.item = item;
        this.fieldMap = fieldMap;
        this.itemsDetailPanel = itemsDetailPanel;
        itemNodeChildren = itemNode.getChildren();
        this.viewBean = viewBean;
        metaDataTypes = viewBean.getBindingEntityModel().getMetaDataTypes();
        level = getLevel();
        
        ColumnTreeLayoutModel columnLayoutModel = viewBean.getColumnLayoutModel();
        if (columnLayoutModel != null) {
            int columnWidth = treeDetail.getWidth() / columnLayoutModel.getColumnTreeModels().size(); 
            itemWidth = columnWidth;
            offset = 300;
        } else {
            itemWidth = treeDetail.getWidth();
            offset = 400;
        }
        treeDetail.stepRenderCounter();
    }
    
    private int getLevel(){
        TreeItemEx current = item;
        int leval = 0;
        while (current != null){
            leval++;
            current = current.getParentItem();
        }
        return leval;
    }
    
    private void initItemWidth(TreeItemEx childItem, int leval) {
        if (childItem.getWidget() instanceof HorizontalPanel) {
            HorizontalPanel hp = (HorizontalPanel) childItem.getWidget();
            if(hp.getWidgetCount() > 1) {
                Widget field = hp.getWidget(1);
                int size = itemWidth - (offset + 19 * leval);
                if (field instanceof FormatTextField) {
                    ((FormatTextField)field).setWidth(size > 200 ? size : 200);
                } else if (field instanceof SimpleComboBox) {
                    ((SimpleComboBox) field).setWidth(size > 200 ? size : 200);
                } else if (field instanceof MultiLanguageField) {
                    ((MultiLanguageField) field).setWidth(size > 200 ? size : 200);
                }
            }
        }
    }

    
    private void tryRenderFks(){
        if (foreighKeyMap.size() > 0) {
            for (TypeModel model : foreighKeyMap.keySet()) {
                treeDetail.getFkRender().RenderForeignKey(itemNode, foreighKeyMap.get(model), model, treeDetail.getToolBar(), viewBean, treeDetail, treeDetail.getItemsDetailPanel());
            }
            foreighKeyMap.clear();
        }
    }
    
    private void executeGroup(){
        while (index < itemNodeChildren.size()){
            ItemNodeModel node = (ItemNodeModel) itemNodeChildren.get(index++);
            Serializable nodeObjectValue = node.getObjectValue();
            String nodeBindingPath = node.getBindingPath();
            String typePath = node.getTypePath();

            TypeModel typeModel = metaDataTypes.get(typePath);
            String typeModelDefaultValue = typeModel.getDefaultValue();
            if (withDefaultValue && typeModelDefaultValue != null && (nodeObjectValue == null || nodeObjectValue.equals(""))) { //$NON-NLS-1$
                node.setObjectValue(typeModelDefaultValue);
            }

            boolean isFKDisplayedIntoTab = TreeDetail.isFKDisplayedIntoTab(node, typeModel, metaDataTypes);

            if (isFKDisplayedIntoTab) {
                if (!foreighKeyMap.containsKey(typeModel)) {
                    foreighKeyMap.put(typeModel, new ArrayList<ItemNodeModel>());
                }
                foreighKeyMap.get(typeModel).add(node);             
            } else {
                DynamicTreeItem childItem;              
                if(node != null && node.getChildCount() > 0 ){
                    childItem = buildGWTTreeChildItem(node, null, withDefaultValue, operation);
                } else {
                    childItem = treeDetail.buildGWTTree(node, null, withDefaultValue, operation);
                }
                MultiOccurrenceChangeItem itemWidget = TreeDetailUtil.createWidget(node, viewBean, fieldMap, null, operation,
                        itemsDetailPanel);
                boolean isAutoExpand = typeModel.isAutoExpand();
                childItem.setWidget(itemWidget);
                childItem.setItemNodeModel(node);
                childItem.setUserObject(node);
                childItem.setVisible(node.isVisible());
                childItem.setState(isAutoExpand);
                childItem.getElement().getStyle().setPaddingLeft(3.0, Unit.PX);
                
                if (childItem != null && !childItem.getElement().getPropertyBoolean("EmptyFkContainer")) { //$NON-NLS-1$
                    initItemWidth(childItem, level);
                    item.addItem(childItem);
                }
            }

            if (index % GROUP_SIZE == 0){
                return;
            }
        }
    }
    
    public DynamicTreeItem buildGWTTreeChildItem(ItemNodeModel itemNode, DynamicTreeItem treeItem, boolean withDefaultValue, String operation) {
        List<ModelData> itemNodeChildren = itemNode.getChildren();
        if(treeItem == null){
            treeItem = new DynamicTreeItem();
        }
        if(itemNodeChildren != null && itemNodeChildren.size() > 0){
            for(int i=0;i < itemNodeChildren.size();i++){
                if(itemNodeChildren.get(i) != null){
                    ItemNodeModel node = (ItemNodeModel)itemNodeChildren.get(i);
                    if(node != null && node.getChildCount() > 0){
                        buildGWTTreeChildItem(node, treeItem, withDefaultValue, operation);
                    } else {                        
                        treeItem.addItem(createNewTreeNode(node));
                    }
                }
            }
        } else {            
            treeItem.addItem(createNewTreeNode(itemNode));
        }
        return treeItem;        
    }
    
    private DynamicTreeItem createNewTreeNode(ItemNodeModel itemNode){
        DynamicTreeItem treeNode = new DynamicTreeItem();
        String typePath = itemNode.getTypePath();
        TypeModel typeModel = metaDataTypes.get(typePath);
        MultiOccurrenceChangeItem itemWidget = TreeDetailUtil.createWidget(itemNode, viewBean, fieldMap, null, operation,
                itemsDetailPanel);
        boolean isAutoExpand = typeModel.isAutoExpand();
        itemWidget.setTreeDetail(treeDetail);
        treeNode.setWidget(itemWidget);
        treeNode.setItemNodeModel(itemNode);
        treeNode.setUserObject(itemNode);
        treeNode.setVisible(itemNode.isVisible());
        treeNode.setState(isAutoExpand);
        treeNode.getElement().getStyle().setPaddingLeft(3.0, Unit.PX);
        return treeNode;
    }
    
    @Override
    public boolean execute() {
        try {
            executeGroup();
        } catch(Exception e) {
            treeDetail.resetRenderCounter();
            Log.info("render tree item generate error:", e); //$NON-NLS-1$
        }
        
        if (index < itemNodeChildren.size()){
            return true;
        } else { 
            try {
                tryRenderFks();
            } catch (Exception e){
                treeDetail.resetRenderCounter();
                Log.info("render foreign key generate error:", e); //$NON-NLS-1$
            } finally {
                DeferredCommand.addCommand(new Command() {
                    @Override
                    public void execute() {
                        treeDetail.endRender();
                    }
                });
            }
            return false;
        }
    }

}
