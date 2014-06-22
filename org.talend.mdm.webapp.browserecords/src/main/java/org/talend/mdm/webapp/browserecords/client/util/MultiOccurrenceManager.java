package org.talend.mdm.webapp.browserecords.client.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyUtil;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.MultiOccurrenceChangeItem;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.MultiOccurrenceChangeItem.AddRemoveHandler;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.DynamicTreeItem;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.GhostTreeItem;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetailGridFieldCreator;

import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.MessageBoxEvent;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.Widget;

public class MultiOccurrenceManager {

    TreeDetail treeDetail;

    Map<String, TypeModel> metaDataTypes;

    private Map<String, List<DynamicTreeItem>> multiOccurrence = new HashMap<String, List<DynamicTreeItem>>();

    public MultiOccurrenceManager(Map<String, TypeModel> metaDataTypes, TreeDetail treeDetail) {
        this.metaDataTypes = metaDataTypes;
        this.treeDetail = treeDetail;
    }

    public void addMultiOccurrenceNode(DynamicTreeItem item) {

        ItemNodeModel nodeModel = item.getItemNodeModel();
        TypeModel typeModel = metaDataTypes.get(nodeModel.getTypePath());
        boolean isLazyLoading = !typeModel.isAutoExpand() && nodeModel.getParent() != null && nodeModel.getChildCount() > 0
                && item.getChild(0) instanceof GhostTreeItem;
        if (typeModel.isAutoExpand() || isLazyLoading || nodeModel.getChildCount() == 0 || item.getChildCount() == 0) {
            if (typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1) {
                String xpath = CommonUtil.getRealXpathWithoutLastIndex(nodeModel);
                List<DynamicTreeItem> multiNodes = multiOccurrence.get(xpath);
                if (multiNodes == null) {
                    multiNodes = new ArrayList<DynamicTreeItem>();
                    multiOccurrence.put(xpath, multiNodes);
                }
                setAddRemoveEvent(item);
                int index = getIndexOfMultiItem(item);
                multiNodes.add(index, item);
            }
        }

        if (isLazyLoading)
            return;
        for (int i = 0; i < item.getChildCount(); i++) {
            DynamicTreeItem childItem = (DynamicTreeItem) item.getChild(i);
            addMultiOccurrenceNode(childItem);
        }
    }

    private int getIndexOfMultiItem(DynamicTreeItem item) {
        int index = -1;
        ItemNodeModel itemModel = item.getItemNodeModel();
        TreeItem parent = item.getParentItem();
        int count = parent.getChildCount();
        for (int i = 0; i < count; i++) {
            DynamicTreeItem childItem = (DynamicTreeItem) parent.getChild(i);
            ItemNodeModel childModel = childItem.getItemNodeModel();
            if (childModel.getTypePath().equals(itemModel.getTypePath())) {
                index++;
                if (childItem.equals(item)) {
                    return index;
                }
            }
        }
        return index;
    }

    public void removeMultiOccurrenceNode(DynamicTreeItem item) {
        ItemNodeModel nodeModel = item.getItemNodeModel();
        String xpath = CommonUtil.getRealXpathWithoutLastIndex(nodeModel);
        List<DynamicTreeItem> multiNodes = multiOccurrence.get(xpath);
        if (multiNodes != null) {
            multiNodes.remove(item);
            clearAddRemoveEvent(item);
        }

        for (int i = 0; i < item.getChildCount(); i++) {
            if (item.getChild(i) instanceof DynamicTreeItem) {
                DynamicTreeItem childItem = (DynamicTreeItem) item.getChild(i);
                removeMultiOccurrenceNode(childItem);
            }
        }
    }

    private void setAddRemoveEvent(DynamicTreeItem item) {
        Widget w = item.getWidget();
        if (w instanceof MultiOccurrenceChangeItem) {
            MultiOccurrenceChangeItem multiItem = (MultiOccurrenceChangeItem) w;
            multiItem.setAddRemoveHandler(new AddRemoveHandler() {

                public void removedNode(DynamicTreeItem selectedItem) {
                    handleRemoveNode(selectedItem);
                }

                public void addedNode(DynamicTreeItem selectedItem, String optId) {
                    handleAddNode(selectedItem, optId);
                }

                public void clearNodeValue(DynamicTreeItem selectedItem) {
                    handleClearNodeValue(selectedItem);
                }
            });
        }
    }

    private void clearAddRemoveEvent(DynamicTreeItem item) {
        Widget w = item.getWidget();
        if (w instanceof MultiOccurrenceChangeItem) {
            MultiOccurrenceChangeItem multiItem = (MultiOccurrenceChangeItem) w;
            multiItem.setAddRemoveHandler(null);
        }
    }

    public void warningBrothers(ItemNodeModel nodeModel) {
        ItemNodeModel parentNode = (ItemNodeModel) nodeModel.getParent();
        for (int i = 0; i < parentNode.getChildCount(); i++) {
            ItemNodeModel childNode = (ItemNodeModel) parentNode.getChild(i);
            if (childNode.isLeaf()) {
                warningItems(childNode);
            }
        }
    }

    public void warningItems(ItemNodeModel nodeModel) {
        ItemNodeModel current = nodeModel;
        while (current != null) {
            String realPath = CommonUtil.getRealXPath(current);
            warningItems(realPath);
            current = (ItemNodeModel) current.getParent();
        }
    }

    private void warningItems(String realPath) {
        String xpath = CommonUtil.getRealXpathWithoutLastIndex(realPath);
        List<DynamicTreeItem> items = multiOccurrence.get(xpath);
        if (items == null)
            return;
        String mandatory = checkMandatory(items);
        for (int i = 0; i < items.size(); i++) {
            DynamicTreeItem childItem = items.get(i);
            MultiOccurrenceChangeItem multiItem = (MultiOccurrenceChangeItem) childItem.getWidget();
            ItemNodeModel nodeModel = childItem.getItemNodeModel();
            multiItem.clearWarning();
            childItem.getElement().setTitle(null);
            nodeModel.setValid(true);
            if (mandatory != null) {
                multiItem.warnMandatory(); 
                childItem.setTitle(mandatory);
                nodeModel.setValid(false);
            }

        }
    }

    public void warningAllItems() {
        Iterator<String> keyIter = multiOccurrence.keySet().iterator();
        while (keyIter.hasNext()) {
            String xpath = keyIter.next();
            warningItems(xpath);
        }
    }

    public void handleOptIcons() {
        Iterator<String> keyIter = multiOccurrence.keySet().iterator();
        while (keyIter.hasNext()) {
            String xpath = keyIter.next();
            handleOptIcon(xpath);
        }

    }

    private void handleOptIcon(String realPath) {
        String xpath = CommonUtil.getRealXpathWithoutLastIndex(realPath);
        List<DynamicTreeItem> items = multiOccurrence.get(xpath);
        if (items == null)
            return;
        if (items.size() > 0) {

            TypeModel tm = metaDataTypes.get(items.get(0).getItemNodeModel().getTypePath());
            boolean isFk = tm.getForeignkey() != null && tm.getForeignkey().trim().length() > 0;
            if (items.size() == 1) {
                MultiOccurrenceChangeItem itemWidget = (MultiOccurrenceChangeItem) items.get(0).getWidget();
                itemWidget.switchRemoveOpt(false, isFk);
            } else {
                MultiOccurrenceChangeItem itemWidget = (MultiOccurrenceChangeItem) items.get(0).getWidget();
                itemWidget.switchRemoveOpt(true, isFk);
            }

            boolean canAdd;
            if (tm.getMaxOccurs() <= 0) {
                canAdd = true;
            } else {
                canAdd = items.size() < tm.getMaxOccurs();
            }
            for (int i = 0; i < items.size(); i++) {
                DynamicTreeItem item = items.get(i);
                MultiOccurrenceChangeItem itemWidget = (MultiOccurrenceChangeItem) item.getWidget();
                itemWidget.setAddIconVisible(canAdd);
            }
        }
    }

    private String checkMandatory(List<DynamicTreeItem> items) {
        int validValueCount = 0;

        if (items.size() >= 1) {
            DynamicTreeItem firstItem = items.get(0);
            ItemNodeModel firstModel = firstItem.getItemNodeModel();
            ItemNodeModel parentModel = (ItemNodeModel) firstModel.getParent();
            TypeModel parentType = metaDataTypes.get(parentModel.getTypePath());
            boolean parentMandatory = true;
            if (parentModel.getParent() != null) {
                parentMandatory = parentType.getMinOccurs() == 1 && parentType.getMaxOccurs() == 1;
            }

            int minOccurs = metaDataTypes.get(firstModel.getTypePath()).getMinOccurs();
            for (DynamicTreeItem item : items) {
                ItemNodeModel nodeModel = item.getItemNodeModel();
                if (CommonUtil.hasChildrenValue(nodeModel)) {
                    validValueCount++;
                }
            }
            if (parentMandatory) {
                if (validValueCount < minOccurs) {

                    return MessagesFactory.getMessages().multiOccurrence_minimize_title(minOccurs,
                            firstItem.getItemNodeModel().getName());
                } else {
                    return null;
                }
            } else {
                for (int i = 0; i < parentModel.getChildCount(); i++) {
                    ItemNodeModel childModel = (ItemNodeModel) parentModel.getChild(i);
                    if (CommonUtil.hasChildrenValue(childModel)) {
                        if (validValueCount < minOccurs) {
                            return MessagesFactory.getMessages().multiOccurrence_minimize_title(minOccurs,
                                    firstItem.getItemNodeModel().getName());
                        }
                    }
                }
            }
        }
        return null;
    }

    private void handleAddNode(DynamicTreeItem selectedItem, String optId) {

        ItemNodeModel selectedModel = selectedItem.getItemNodeModel();
        TypeModel typeModel = metaDataTypes.get(selectedModel.getTypePath());
        int count = CommonUtil.getCountOfBrotherOfTheSameName(selectedModel);

        if (typeModel.getMaxOccurs() < 0 || count < typeModel.getMaxOccurs()) {
            ItemNodeModel model = null;
            if ("Clone".equals(optId)) { //$NON-NLS-1$
                model = selectedModel.clone(true);
            } else {
                List<ItemNodeModel> modelList = CommonUtil.getDefaultTreeModel(typeModel, Locale.getLanguage(), false, false, false);
                if (modelList.size() > 0)
                    model = modelList.get(0);
            }
            model.setDynamicLabel(LabelUtil.getNormalLabel(model.getLabel()));
            model.setMandatory(selectedModel.isMandatory());
            ItemNodeModel parentModel = (ItemNodeModel) selectedModel.getParent();
            int selectModelIndex = parentModel.indexOf(selectedModel);
            parentModel.insert(model, selectModelIndex + 1);
            parentModel.setChangeValue(true);
            // if it has default value
            if (typeModel.getDefaultValue() != null)
                model.setObjectValue(typeModel.getDefaultValue());
            DynamicTreeItem treeItem = treeDetail.buildGWTTree(model, null, true, null);
            ViewUtil.copyStyleToTreeItem(selectedItem, treeItem);

            DynamicTreeItem parentItem = (DynamicTreeItem) selectedItem.getParentItem();
            parentItem.insertItem(treeItem, parentItem.getChildIndex(selectedItem) + 1);
            treeDetail.adjustFieldWidget(treeItem);

            MultiOccurrenceManager multiManager = treeDetail.getMultiManager();
            multiManager.addMultiOccurrenceNode((DynamicTreeItem) treeItem);

            warningItems(treeItem.getItemNodeModel());

            handleOptIcon(CommonUtil.getRealXPath(selectedModel));

        } else {
            MessageBox.alert(MessagesFactory.getMessages().status(), MessagesFactory.getMessages()
                    .multiOccurrence_maximize(count), null);
        }
    }

    private void handleRemoveNode(final DynamicTreeItem selectedItem) {

        final DynamicTreeItem parentItem = (DynamicTreeItem) selectedItem.getParentItem();
        final ItemNodeModel selectedModel = selectedItem.getItemNodeModel();
        final ItemNodeModel parentModel = (ItemNodeModel) selectedModel.getParent();
        final TypeModel typeModel = metaDataTypes.get(selectedModel.getTypePath());
        final int count = CommonUtil.getCountOfBrotherOfTheSameName(selectedModel);

        final Map<String, Field<?>> fieldMap = treeDetail.getFieldMap();
        MessageBox.confirm(MessagesFactory.getMessages().confirm_title(), MessagesFactory.getMessages().delete_confirm(),
                new Listener<MessageBoxEvent>() {

                    public void handleEvent(MessageBoxEvent be) {
                        if (be.getButtonClicked().getItemId().equals(Dialog.YES)) {
                            if (count > 1 && count > typeModel.getMinOccurs()) {
                                TreeDetailGridFieldCreator.deleteField(selectedModel, fieldMap);

                                removeMultiOccurrenceNode(selectedItem);
                                selectedModel.setObjectValue(null);
                                warningItems(selectedModel);
                                String selectedXpath = CommonUtil.getRealXPath(selectedModel);
                                parentItem.removeItem(selectedItem);
                                parentModel.remove(selectedModel);
                                parentModel.setChangeValue(true);

                                Set<ItemNodeModel> fkContainers = ForeignKeyUtil.getAllForeignKeyModelParent(
                                        treeDetail.getViewBean(), selectedModel);
                                for (ItemNodeModel fkContainer : fkContainers) {
                                    treeDetail.getFkRender().removeRelationFkPanel(fkContainer);
                                }

                                handleOptIcon(selectedXpath);

                                if (parentModel.getChildCount() > 0) {
                                    ItemNodeModel child = (ItemNodeModel) parentModel.getChild(0);
                                    Field<?> field = fieldMap.get(child.getId().toString());
                                    if (field != null) {
                                        TreeDetailGridFieldCreator.updateMandatory(field, child, fieldMap);
                                    }
                                }
                            } else {
                                MessageBox.alert(MessagesFactory.getMessages().status(), MessagesFactory.getMessages()
                                        .multiOccurrence_minimize(count), null);
                            }
                        }
                    }
                });
    }

    private void handleClearNodeValue(final DynamicTreeItem selectedItem) {
        ItemNodeModel nodeModel = selectedItem.getItemNodeModel();
        // Clear all the leaf node's value when the current node isAutoExpand = false and children have not been
        // rendered
        TypeModel typeModel = metaDataTypes.get(nodeModel.getTypePath());
        if (!typeModel.isAutoExpand() && nodeModel.getChildCount() > 0 && selectedItem.getChild(0) instanceof GhostTreeItem)
            nodeModel.clearNodeValue();
        else {
            if (nodeModel.isLeaf()) {
                MultiOccurrenceChangeItem itemWidget = (MultiOccurrenceChangeItem) selectedItem.getWidget();
                itemWidget.clearValue();
            }
            for (int i = 0; i < selectedItem.getChildCount(); i++) {
                handleClearNodeValue((DynamicTreeItem) selectedItem.getChild(i));
            }
        }

    }

}
