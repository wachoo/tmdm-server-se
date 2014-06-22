package org.talend.mdm.webapp.browserecords.client.widget.inputfield;

import org.talend.mdm.webapp.base.client.widget.ComboBoxEx;

import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.ResizeEvent;
import com.extjs.gxt.ui.client.event.ResizeListener;
import com.extjs.gxt.ui.client.fx.Resizable;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.user.client.Element;

public class ComboBoxField<D extends ModelData> extends ComboBoxEx<D> {

    class Resize extends Resizable {

        public Resize(BoxComponent resize) {
            super(resize);
        }

        public native BoxComponent getBoxComponent() /*-{
            return this.@com.extjs.gxt.ui.client.fx.Resizable::resize;
        }-*/;

    }

    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);

        LayoutContainer list = (LayoutContainer) this.getListView().getParent();
        list.setLayout(new FitLayout());
        final Resize resizable = new Resize(list);

        resizable.addResizeListener(new ResizeListener() {

            public void resizeEnd(ResizeEvent re) {
                setMinListWidth(resizable.getBoxComponent().getWidth());
            }
        });
    }
}
