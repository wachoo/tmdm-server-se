package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import org.talend.mdm.webapp.browserecords.client.BrowseRecordsEvents;
import org.talend.mdm.webapp.browserecords.client.model.ForeignKeyBean;
import org.talend.mdm.webapp.browserecords.client.resources.icon.Icons;

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

public class ForeignKeyField extends TextField<ForeignKeyBean> {

    private Image selectFKBtn = new Image(Icons.INSTANCE.link());

    private Image addFKBtn = new Image(Icons.INSTANCE.link_add());

    private Image cleanFKBtn = new Image(Icons.INSTANCE.link_delete());

    private Image relationFKBtn = new Image(Icons.INSTANCE.link_go());

    private String foreignKeyName;

    public ForeignKeyField(String foreignKeyName) {
        this.foreignKeyName = foreignKeyName;
        this.setFireChangeEventOnSetValue(true);
        // relWindow.setSize(470, 340);
        // relWindow.setResizable(false);
        // relWindow.setModal(true);
        // relWindow.setBlinkModal(true);
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

        addFKBtn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent arg0) {
                Dispatcher dispatch = Dispatcher.get();
                AppEvent event = new AppEvent(BrowseRecordsEvents.CreateForeignKeyView, foreignKeyName);
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
}
