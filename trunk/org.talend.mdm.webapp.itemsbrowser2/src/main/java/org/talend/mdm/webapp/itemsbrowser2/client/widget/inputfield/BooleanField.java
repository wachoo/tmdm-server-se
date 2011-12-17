package org.talend.mdm.webapp.itemsbrowser2.client.widget.inputfield;

import com.extjs.gxt.ui.client.widget.form.SimpleComboBox;


public class BooleanField extends SimpleComboBox<Boolean> {

    public BooleanField(){
        super();
        this.setEnabled(false);
        this.setForceSelection(true);
        this.setTriggerAction(TriggerAction.ALL);
    }
}
