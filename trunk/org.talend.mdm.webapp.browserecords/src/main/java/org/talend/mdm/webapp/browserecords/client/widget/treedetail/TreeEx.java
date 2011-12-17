package org.talend.mdm.webapp.browserecords.client.widget.treedetail;


import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Tree;

public class TreeEx extends Tree {

    public void onBrowserEvent(Event event) {
        int eventType = DOM.eventGetType(event);
        if (Event.ONCLICK == eventType || Event.ONMOUSEDOWN == eventType) {
            super.onBrowserEvent(event);
        }
    }
}
