package org.talend.mdm.webapp.stagingarea.client;

import org.talend.mdm.webapp.stagingarea.client.view.AbstractView;
import org.talend.mdm.webapp.stagingarea.client.view.CurrentValidationView;
import org.talend.mdm.webapp.stagingarea.client.view.PreviousValidationView;
import org.talend.mdm.webapp.stagingarea.client.view.StatusView;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class StagingareaMainPanel extends AbstractView {
    
    private StatusView statusView;

    private CurrentValidationView currentValidationView;

    private PreviousValidationView previousValidationView;

    private FieldSet wrapFieldSet(BoxComponent comp, String caption){
        FieldSet fieldSet = new FieldSet();
        fieldSet.setLayout(new FitLayout());
        fieldSet.setHeading(caption);
        fieldSet.add(comp);
        return fieldSet;
    }

    @Override
    protected void initView() {
        statusView = new StatusView();
        currentValidationView = new CurrentValidationView();
        previousValidationView = new PreviousValidationView();
        mainPanel.setLayout(new RowLayout(Orientation.VERTICAL));

        mainPanel.add(wrapFieldSet(statusView, "Status"), new RowData(1, -1, new Margins(4))); //$NON-NLS-1$
        mainPanel.add(wrapFieldSet(currentValidationView, "Current Validation"), new RowData(1, -1, new Margins(0, 4, 0, 4))); //$NON-NLS-1$
        mainPanel.add(wrapFieldSet(previousValidationView, "Previous Validation(s)"), new RowData(1, 1, new Margins(4))); //$NON-NLS-1$
    }
}
