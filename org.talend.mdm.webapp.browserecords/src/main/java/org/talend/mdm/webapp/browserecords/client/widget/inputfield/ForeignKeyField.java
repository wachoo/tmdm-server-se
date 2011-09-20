package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import java.util.List;

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKeyFieldList;
import org.talend.mdm.webapp.browserecords.client.widget.ForeignKey.ReturnCriteriaFK;
import org.talend.mdm.webapp.browserecords.client.widget.treedetail.ForeignKeyListWindow;

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

public class ForeignKeyField extends TextField<ForeignKeyBean> implements ReturnCriteriaFK {

    private Image selectFKBtn = new Image(Icons.INSTANCE.link());

    private Image addFKBtn = new Image(Icons.INSTANCE.link_add());

    private Image cleanFKBtn = new Image(Icons.INSTANCE.link_delete());

    private Image relationFKBtn = new Image(Icons.INSTANCE.link_go());

    private String foreignKeyName;

    private ForeignKeyListWindow fkWindow = new ForeignKeyListWindow();

    private boolean isFkList;

    private ForeignKeyFieldList fkFieldList;

    public ForeignKeyField(String foreignKey, List<String> foreignKeyInfo) {
        this.foreignKeyName = foreignKey.split("/")[0]; //$NON-NLS-1$
        this.setFireChangeEventOnSetValue(true);
        this.setReturnCriteriaFK();
        fkWindow.setForeignKeyInfos(foreignKey, foreignKeyInfo);
        fkWindow.setSize(470, 340);
        fkWindow.setResizable(false);
        fkWindow.setModal(true);
        fkWindow.setBlinkModal(true);
    }

    public ForeignKeyField(String foreignKey, List<String> foreignKeyInfo, ForeignKeyFieldList fkFieldList) {
        this(foreignKey, foreignKeyInfo);
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
        Element addTD = DOM.createTD();
        Element cleanTD = DOM.createTD();
        Element relationTD = DOM.createTD();

        foreignDiv.appendChild(body);
        body.appendChild(tr);
        tr.appendChild(selectTD);
        tr.appendChild(addTD);
        tr.appendChild(cleanTD);
        tr.appendChild(relationTD);

        wrap.appendChild(foreignDiv);

        setElement(wrap.dom, target, index);
        selectTD.appendChild(selectFKBtn.getElement());
        addTD.appendChild(addFKBtn.getElement());
        cleanTD.appendChild(cleanFKBtn.getElement());
        relationTD.appendChild(relationFKBtn.getElement());
        addListener();

        super.onRender(target, index);
    }

    private void addListener() {
        addFKBtn.setTitle(MessagesFactory.getMessages().fk_add_title());
        addFKBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                Dispatcher dispatch = Dispatcher.get();
                AppEvent event = new AppEvent(BrowseRecordsEvents.CreateForeignKeyView, foreignKeyName);
                dispatch.dispatch(event);
            }
        });
        selectFKBtn.setTitle(MessagesFactory.getMessages().fk_select_title());
        selectFKBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                Dispatcher dispatch = Dispatcher.get();
                AppEvent event = new AppEvent(BrowseRecordsEvents.SelectForeignKeyView, foreignKeyName);
                event.setSource(ForeignKeyField.this.getFkWindow());
                dispatch.dispatch(event);
            }
        });
        cleanFKBtn.setTitle(MessagesFactory.getMessages().fk_del_title());
        cleanFKBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                if (!isFkList)
                    clear();
                else
                    fkFieldList.removeForeignKeyWidget(ForeignKeyField.this.getValue());
            }
        });
        relationFKBtn.setTitle(MessagesFactory.getMessages().fk_open_title());
        relationFKBtn.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent arg0) {
                if(ForeignKeyField.this.getValue() == null)
                    return;
                Dispatcher dispatch = Dispatcher.get();
                AppEvent event = new AppEvent(BrowseRecordsEvents.ViewForeignKey);
                event.setData("ids", ForeignKeyField.this.getValue().getId().replace("[", "").replace("]", "")); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$ //$NON-NLS-5$
                event.setData("concept", ForeignKeyField.this.foreignKeyName); //$NON-NLS-1$
                dispatch.dispatch(event);
            }
        });
    }

    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(addFKBtn);
        ComponentHelper.doAttach(selectFKBtn);
        ComponentHelper.doAttach(cleanFKBtn);
        ComponentHelper.doAttach(relationFKBtn);
    }

    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(addFKBtn);
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
