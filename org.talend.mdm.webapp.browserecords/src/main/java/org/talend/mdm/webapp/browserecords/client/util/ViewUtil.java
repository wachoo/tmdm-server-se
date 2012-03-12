// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.browserecords.client.util;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.model.ColumnElement;
import org.talend.mdm.webapp.browserecords.client.model.ColumnTreeModel;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeEx;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.TreeDetail.DynamicTreeItem;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class ViewUtil {

    public static String getConceptFromBrowseItemView(String viewPK) {
        String concept = viewPK.replaceAll("Browse_items_", "");//$NON-NLS-1$ //$NON-NLS-2$
        concept = concept.replaceAll("#.*", "");//$NON-NLS-1$ //$NON-NLS-2$
        return concept;
    }

    /**
     * DOC HSHU Comment method "getSearchableLabel".
     */
    public static String getViewableLabel(String language, TypeModel typeModel) {

        String label = typeModel.getLabel(language);
        if (LabelUtil.isDynamicLabel(label)) {
            label = typeModel.getName();
        }
        return label;

    }

    public static Tree transformToCustomLayout(TreeItem originalRoot, ColumnTreeModel columnLayoutModel, ViewBean viewBean) {

        // In case of custom layout, which displays some elements and not others,
        // we store the DynamicTreeItem corresponding to the displayed elements in
        // this set.
        Set<TreeItem> customLayoutDisplayedElements = new HashSet<TreeItem>();

        Tree tree = new TreeEx();
        DynamicTreeItem treeRootNode = new DynamicTreeItem();
        tree.addItem(treeRootNode);

        List<TreeItem> children = getChildren(originalRoot);
        List<ColumnElement> columnLayoutModels = columnLayoutModel.getColumnElements();
        if (columnLayoutModels != null) {
            for (ColumnElement ce : columnLayoutModels) {
                ListIterator<TreeItem> iter = children.listIterator();
                while (iter.hasNext()) {
                    TreeItem child = iter.next();
                    ItemNodeModel node = (ItemNodeModel) child.getUserObject();
                    String xpath = node.getBindingPath();

                    String typePath = node.getTypePath();
                    TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(typePath);
                    if (typeModel.getForeignkey() != null) {
                        continue;
                    }
                    if (("/" + xpath).equals(ce.getxPath())) { //$NON-NLS-1$
                        iter.remove();
                        treeRootNode.addItem(child);
                        child.getElement().getStyle().setPaddingLeft(23D, Unit.PX);
                        __transformToCustomLayout(child, ce, customLayoutDisplayedElements, viewBean);
                    }
                }
                if (ce.getHtmlSnippet() != null && ce.getHtmlSnippet().trim().length() > 0) {
                    treeRootNode.addItem(new HTML(ce.getHtmlSnippet()));
                }
            }
        }
        setValidFlags((DynamicTreeItem) originalRoot, customLayoutDisplayedElements);

        if (treeRootNode.getElement().getFirstChildElement() != null)
            treeRootNode.getElement().getFirstChildElement().setClassName("rootNode"); //$NON-NLS-1$
        treeRootNode.setState(true);
        return tree;
    }

    private static void __transformToCustomLayout(TreeItem item, ColumnElement columnEl, Set<TreeItem> customLayoutDisplayedElements, ViewBean viewBean) {
        customLayoutDisplayedElements.add(item);
        applyStyleTreeItem(item, columnEl.getLabelStyle(), columnEl.getValueStyle(), columnEl.getStyle());
        if (columnEl.getChildren() == null)
            return;
        for (ColumnElement ce : columnEl.getChildren()) {

            for (int i = 0; i < item.getChildCount(); i++) {
                TreeItem child = item.getChild(i);
                ItemNodeModel node = (ItemNodeModel) child.getUserObject();
                if (node != null) {
                    TypeModel tm = viewBean.getBindingEntityModel().getMetaDataTypes().get(node.getTypePath());
                    String xpath = node.getBindingPath();
                    if (("/" + xpath).equals(ce.getxPath())) { //$NON-NLS-1$
                        __transformToCustomLayout(child, ce, customLayoutDisplayedElements, viewBean);
                        if (tm.getMaxOccurs() == 1) {
                            break;
                        }
                    }
                }
            }
            if (ce.getHtmlSnippet() != null) {
                item.addItem(new HTML(ce.getHtmlSnippet()));
            }
        }
    }

    public static void applyStyleTreeItem(final TreeItem item, final String labelStyle, final String valueStyle, final String style) {
        DeferredCommand.addCommand(new Command() {

            public void execute() {
                String marginLeft = item.getElement().getStyle().getMarginLeft();
                item.getElement().setAttribute("style", style); //$NON-NLS-1$
                item.getElement().getStyle().setProperty("marginLeft", marginLeft); //$NON-NLS-1$
                if (item.getWidget() instanceof HorizontalPanel) {
                    HorizontalPanel hp = (HorizontalPanel) item.getWidget();
                    HTML label = (HTML) hp.getWidget(0);
                    label.getElement().setAttribute("style", labelStyle); //$NON-NLS-1$
                    if (hp.getWidgetCount() >= 2 && hp.getWidget(1) instanceof Field<?>) {
                        final Field<?> field = (Field<?>) hp.getWidget(1);

                        El inputEl = getInputEl(field);
                        inputEl.setElementAttribute("style", valueStyle); //$NON-NLS-1$
                    }
                }
            }
        });
    }

    public static void copyStyleToTreeItem(TreeItem source, TreeItem target) {
        target.getElement().setAttribute("style", source.getElement().getAttribute("style")); //$NON-NLS-1$//$NON-NLS-2$
        if (source.getWidget() instanceof HorizontalPanel) {
            final HorizontalPanel sourceHp = (HorizontalPanel) source.getWidget();
            final HorizontalPanel targetHp = (HorizontalPanel) target.getWidget();
            HTML sourceLabel = (HTML) sourceHp.getWidget(0);
            HTML targetLabel = (HTML) targetHp.getWidget(0);
            targetLabel.getElement().setAttribute("style", sourceLabel.getElement().getAttribute("style"));  //$NON-NLS-1$//$NON-NLS-2$
            if (sourceHp.getWidgetCount() >= 2 && sourceHp.getWidget(1) instanceof Field<?>) {
                DeferredCommand.addCommand(new Command() {
                    public void execute() {
                        El sourceInputEl = getInputEl((Field<?>) sourceHp.getWidget(1));
                        El targetInputEl = getInputEl((Field<?>) targetHp.getWidget(1));
                        targetInputEl.setElementAttribute("style", sourceInputEl.dom.getAttribute("style")); //$NON-NLS-1$ //$NON-NLS-2$
                    }
                });
            }
        }
    }

    /**
     * Recursively set the valid flags of the ItemNodeModel's corresponding to the dynamicTreeItem and all its children
     * dynamicTreeItem's to true. Used to set the valid flag for all those items excluded from the display because of a
     * custom layout. Because they are not displayed, their valid flags are not set by their attach handlers, which is
     * where the valid flag is normally set for fields that are displayed.
     */
    private static void setValidFlags(DynamicTreeItem dynamicTreeItem, Set<TreeItem> customLayoutDisplayedElements) {
        dynamicTreeItem.getItemNodeModel().setValid(true);
        int childCount = dynamicTreeItem.getChildCount();
        for (int i = 0; i < childCount; ++i) {
            TreeItem child = dynamicTreeItem.getChild(i);
            if (!customLayoutDisplayedElements.contains(child)) {
                if (child instanceof DynamicTreeItem) {
                    setValidFlags((DynamicTreeItem) child, customLayoutDisplayedElements);
                }
            }
        }
    }

    private static native ArrayList<TreeItem> getChildren(TreeItem item)/*-{
        return item.@com.google.gwt.user.client.ui.TreeItem::children;
    }-*/;

    private static native El getInputEl(Field<?> field)/*-{
        return field.@com.extjs.gxt.ui.client.widget.form.Field::getInputEl()();
    }-*/;
}
