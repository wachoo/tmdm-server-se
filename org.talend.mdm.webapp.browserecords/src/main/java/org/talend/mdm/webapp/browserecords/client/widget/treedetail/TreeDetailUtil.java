package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail.DynamicTreeItem;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyTreeDetail.TreeItemWidget;
import org.talend.mdm.webapp.browserecords.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.widget.form.Field;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Widget;

/**
 * TreeDetail tool class
 */
public class TreeDetailUtil {

    public static Widget createWidget(ItemNodeModel itemNode, String property, ViewBean viewBean, ClickHandler handler) {
        
    	TreeItemWidget treeItem = new TreeItemWidget();
        // create Field
        String xPath = itemNode.getBindingPath();
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(xPath);
        HTML label = new HTML();
        String html = property;
        if (itemNode.isKey() || typeModel.getMinOccurs() >= 1)
            html = html + "<span style=\"color:red\"> *</span>"; //$NON-NLS-1$
        label.setHTML(html);
        treeItem.setLabel(label);
        if (typeModel.isSimpleType()) {
        	treeItem.setSimpleType(true);
            Field<?> field = TreeDetailGridFieldCreator.createField(typeModel, Locale.getLanguage());
            field.setWidth(260);
            treeItem.setField(field);
            treeItem.setHandler(handler);
            treeItem.paint();

        }
        
        return treeItem;
    }
}
