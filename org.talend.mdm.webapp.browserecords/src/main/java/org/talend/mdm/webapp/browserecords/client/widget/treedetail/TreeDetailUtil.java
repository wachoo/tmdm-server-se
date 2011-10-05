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
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import org.talend.mdm.webapp.base.shared.TypeModel;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ItemNodeModel;
import org.talend.mdm.webapp.browserecords.client.util.CommonUtil;
import org.talend.mdm.webapp.browserecords.client.util.LabelUtil;
import org.talend.mdm.webapp.browserecords.client.util.Locale;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKeyFieldList;
import org.talend.mdm.webapp.browserecords.shared.ComplexTypeModel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.FieldEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
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

    public static Widget createWidget(final ItemNodeModel itemNode, final ViewBean viewBean, ClickHandler h) {

        HorizontalPanel hp = new HorizontalPanel();
        // create Field
        String xPath = itemNode.getBindingPath();
        TypeModel typeModel = viewBean.getBindingEntityModel().getMetaDataTypes().get(xPath);
        String dynamicLabel = typeModel.getLabel(Locale.getLanguage());
        HTML label = new HTML();
        String html = itemNode.getLabel();

        if (LabelUtil.isDynamicLabel(dynamicLabel)) {
            html = itemNode.getDynamicLabel();
        }

        if (itemNode.isKey() || typeModel.getMinOccurs() >= 1)
            html = html + "<span style=\"color:red\"> *</span>"; //$NON-NLS-1$

        if (null != itemNode.getDescription() && (itemNode.getDescription().trim().length() > 0))
            html = html
                    + "<img style='margin-left:16px;' src='/talendmdm/secure/img/genericUI/information_icon.gif' title='" + itemNode.getDescription() + "' />"; //$NON-NLS-1$ //$NON-NLS-2$
        label.setHTML(html);
        hp.add(label);
        if (typeModel.isSimpleType()
                || (!typeModel.isSimpleType() && ((ComplexTypeModel) typeModel).getReusableComplexTypes().size() > 0)) {

            if (typeModel.getForeignkey() != null && typeModel.getMaxOccurs() > 1) {// FK list
                ForeignKeyFieldList fkList = new ForeignKeyFieldList(itemNode, typeModel);
                //fkList.setSize("400px", "200px"); //$NON-NLS-1$ //$NON-NLS-2$
                fkList.addListener(Events.Focus, new Listener<FieldEvent>() {

                    public void handleEvent(FieldEvent be) {
                        AppEvent app = new AppEvent(BrowseRecordsEvents.ExecuteVisibleRule);
                        app.setData(itemNode.getParent());
                        Dispatcher.forwardEvent(app);
                    }
                });
                hp.add(fkList);
            } else {
                Field<?> field = TreeDetailGridFieldCreator.createField(itemNode, typeModel, Locale.getLanguage());
                field.setWidth(200);
                field.addListener(Events.Focus, new Listener<FieldEvent>() {

                    public void handleEvent(FieldEvent be) {
                        AppEvent app = new AppEvent(BrowseRecordsEvents.ExecuteVisibleRule);
                        ItemNodeModel parent = CommonUtil.recrusiveRoot(itemNode);
                        // maybe need other methods to get entire tree
                        if (parent == null || parent.getChildCount() == 0) {
                            return;
                        }

                        app.setData(parent);
                        app.setData("viewBean", viewBean); //$NON-NLS-1$
                        Dispatcher.forwardEvent(app);
                    }
                });
                hp.add(field);
            }
        }

        if ((typeModel.getMaxOccurs() < 0 || typeModel.getMaxOccurs() > 1) && typeModel.getForeignkey() == null) {
            Image addNodeImg = new Image("/talendmdm/secure/img/genericUI/add.png"); //$NON-NLS-1$
            addNodeImg.getElement().setId("Add"); //$NON-NLS-1$
            addNodeImg.setTitle(MessagesFactory.getMessages().clone_title());
            addNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            addNodeImg.addClickHandler(h);
            Image removeNodeImg = new Image("/talendmdm/secure/img/genericUI/delete.png"); //$NON-NLS-1$
            removeNodeImg.getElement().setId("Remove"); //$NON-NLS-1$
            removeNodeImg.setTitle(MessagesFactory.getMessages().remove_title());
            removeNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
            removeNodeImg.addClickHandler(h);

            hp.add(addNodeImg);
            hp.add(removeNodeImg);
            if (!typeModel.isSimpleType() && itemNode.getParent() != null) {
                Image cloneNodeImg = new Image("/talendmdm/secure/img/genericUI/add-group.png"); //$NON-NLS-1$
                cloneNodeImg.getElement().setId("Clone"); //$NON-NLS-1$
                cloneNodeImg.setTitle(MessagesFactory.getMessages().deepclone_title());
                cloneNodeImg.getElement().getStyle().setMarginLeft(5.0, Unit.PX);
                cloneNodeImg.addClickHandler(h);
                hp.add(cloneNodeImg);
            }
        }

        hp.setCellWidth(label, "200px"); //$NON-NLS-1$

        return hp;
    }
}
