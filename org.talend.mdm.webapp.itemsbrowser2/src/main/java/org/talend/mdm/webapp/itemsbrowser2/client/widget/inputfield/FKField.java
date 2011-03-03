package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import com.extjs.gxt.ui.client.GXT;
import com.extjs.gxt.ui.client.core.El;
import com.extjs.gxt.ui.client.core.XDOM;
import com.extjs.gxt.ui.client.widget.ComponentHelper;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;

public class FKField extends TextField<String> {

    // TODO use image
    Button btn1 = new Button();

    Button btn2 = new Button();

    protected void onRender(Element target, int index) {
        // add button
        El wrap = new El(DOM.createDiv());
        wrap.addStyleName("x-form-field-wrap"); //$NON-NLS-1$
        wrap.addStyleName("x-form-file-wrap"); //$NON-NLS-1$

        input = new El(DOM.createInputText());
        input.addStyleName(fieldStyle);
        input.addStyleName("x-form-file-text"); //$NON-NLS-1$
        input.setId(XDOM.getUniqueId());

        if (GXT.isIE && target.getTagName().equals("TD")) { //$NON-NLS-1$
            input.setStyleAttribute("position", "static"); //$NON-NLS-1$  //$NON-NLS-2$
        }

        wrap.appendChild(input.dom);

        setElement(wrap.dom, target, index);

        btn1.addStyleName("x-form-filter-btn"); //$NON-NLS-1$
        btn1.render(wrap.dom);
        btn2.addStyleName("x-form-valid-btn"); //$NON-NLS-1$
        btn2.render(wrap.dom);
        super.onRender(target, index);
    }

    protected void onResize(int width, int height) {
        super.onResize(width, height);
        input.setWidth(width - btn1.getWidth() - btn2.getWidth() - 6, true);
    }

    protected void doAttachChildren() {
        super.doAttachChildren();
        ComponentHelper.doAttach(btn1);
        ComponentHelper.doAttach(btn2);
    }

    protected void doDetachChildren() {
        super.doDetachChildren();
        ComponentHelper.doDetach(btn1);
        ComponentHelper.doDetach(btn2);
    }

}
