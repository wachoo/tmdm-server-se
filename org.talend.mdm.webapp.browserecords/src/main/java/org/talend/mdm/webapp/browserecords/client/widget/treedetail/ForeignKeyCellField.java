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
package org.talend.mdm.webapp.browserecords.client.widget.treedetail;

import java.util.List;

import org.talend.mdm.webapp.base.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKeyFieldList;
import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKey.ReturnCriteriaFK;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Dispatcher;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Image;


/**
 * DOC Administrator  class global comment. Detailled comment
 */
public class ForeignKeyCellField extends TextField<ForeignKeyBean> implements ReturnCriteriaFK {

    private Image selectFKBtn = new Image(Icons.INSTANCE.link());

    private Image cleanFKBtn = new Image(Icons.INSTANCE.link_delete());

    private Image relationFKBtn = new Image(Icons.INSTANCE.link_go());

    private String foreignKeyName;

    private ForeignKeyListWindow fkWindow = new ForeignKeyListWindow();

    private boolean isFkList;

    private ForeignKeyFieldList fkFieldList;
    
    private ItemsDetailPanel itemsDetailPanel;

    public ForeignKeyCellField(String foreignKey, List<String> foreignKeyInfo, ItemsDetailPanel itemsDetailPanel) {
        this.foreignKeyName = foreignKey.split("/")[0]; //$NON-NLS-1$
        this.setFireChangeEventOnSetValue(true);
        this.setReturnCriteriaFK();
        this.setWidth(400);
        this.itemsDetailPanel = itemsDetailPanel;
        fkWindow.setForeignKeyInfos(foreignKey, foreignKeyInfo);
        fkWindow.setSize(470, 340);
        fkWindow.setResizable(false);
        fkWindow.setModal(true);
        fkWindow.setBlinkModal(true);
    }

    public ForeignKeyCellField(String foreignKey, List<String> foreignKeyInfo, ForeignKeyFieldList fkFieldList, ItemsDetailPanel itemsDetailPanel) {
        this(foreignKey, foreignKeyInfo, itemsDetailPanel);
        this.fkFieldList = fkFieldList;
        this.isFkList = true;
    }

    public void initForeignKeyListWindow() {

    }

    public ForeignKeyListWindow getFkWindow() {
        return fkWindow;
    }

    protected void onRender(Element target, int index) {
        El wrap = new El(DOM.createDiv());
        wrap.addStyleName("x-form-field-wrap"); //$NON-NLS-1$
        wrap.addStyleName("x-form-file-wrap"); //$NON-NLS-1$

        input = new El(DOM.createInputText());
        input.addStyleName(fieldStyle);
        input.addStyleName("x-form-file-text"); //$NON-NLS-1$
        input.setId(XDOM.getUniqueId());
        input.setEnabled(false);

        if (GXT.isIE && target.getTagName().equals("TD")) { //$NON-NLS-1$
            input.setStyleAttribute("position", "static"); //$NON-NLS-1$  //$NON-NLS-2$
        }

        wrap.appendChild(input.dom);
        input.setStyleAttribute("float", "left");//$NON-NLS-1$  //$NON-NLS-2$
        Element foreignDiv = DOM.createTable();

        Element tr = DOM.createTR();
        Element body = DOM.createTBody();
        Element selectTD = DOM.createTD();
        Element cleanTD = DOM.createTD();
        Element relationTD = DOM.createTD();

        foreignDiv.appendChild(body);
        body.appendChild(tr);
        tr.appendChild(selectTD);
        tr.appendChild(cleanTD);
        tr.appendChild(relationTD);

        wrap.appendChild(foreignDiv);

        setElement(wrap.dom, target, index);
        selectTD.appendChild(selectFKBtn.getElement());
        cleanTD.appendChild(cleanFKBtn.getElement());
        relationTD.appendChild(relationFKBtn.getElement());
        addListener();

        this.setAutoWidth(true);
        super.onRender(target, index);
    }

    private void addListener() {
        selectFKBtn.setTitle(MessagesFactory.getMessages().fk_select_title());
        selectFKBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                Dispatcher dispatch = Dispatcher.get();
                AppEvent event = new AppEvent(BrowseRecordsEvents.SelectForeignKeyView, foreignKeyName);
                event.setSource(ForeignKeyCellField.this.getFkWindow());
                dispatch.dispatch(event);
            }
        });
        cleanFKBtn.setTitle(MessagesFactory.getMessages().fk_del_title());
        cleanFKBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                if (!isFkList)
                    clear();
                else
                    fkFieldList.removeForeignKeyWidget(ForeignKeyCellField.this.getValue());
            }
        });
        relationFKBtn.setTitle(MessagesFactory.getMessages().fk_open_title());
        relationFKBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                ForeignKeyBean fkBean = ForeignKeyCellField.this.getValue();
                if (fkBean == null || fkBean.getId() == null || "".equals(fkBean.getId())) //$NON-NLS-1$
                    return;
                String ids = ForeignKeyCellField.this.getValue().getId().replaceAll("^\\[|\\]$", "").replace("][", ".");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ 
                ForeignKeyUtil.checkChange(false, ForeignKeyCellField.this.foreignKeyName, ids, itemsDetailPanel);
            }
        });
    }

    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(selectFKBtn);
        ComponentHelper.doAttach(cleanFKBtn);
        ComponentHelper.doAttach(relationFKBtn);
    }

    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(selectFKBtn);
        ComponentHelper.doDetach(cleanFKBtn);
        ComponentHelper.doDetach(relationFKBtn);
    }

    public void setCriteriaFK(final ForeignKeyBean fk) {
        setValue(fk);
    }

    public void setValue(ForeignKeyBean fk) {
        super.setValue(fk);
    }

    public void clear() {
        super.clear();
    }

    public ForeignKeyBean getValue() {
        return value;
    }

    public void setReturnCriteriaFK() {
        fkWindow.setReturnCriteriaFK(this);
        fkWindow.setHeading(MessagesFactory.getMessages().fk_RelatedRecord());
    }
}
