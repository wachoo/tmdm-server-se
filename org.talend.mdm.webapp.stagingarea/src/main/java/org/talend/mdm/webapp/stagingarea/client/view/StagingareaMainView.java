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
package org.talend.mdm.webapp.stagingarea.client.view;

import org.talend.mdm.webapp.stagingarea.client.controller.ControllerContainer;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

public class StagingareaMainView extends AbstractView {
    
    private StagingContainerSummaryView summaryView;

    private CurrentValidationView currentValidationView;

    private PreviousExecutionView previousExecutionValidationView;

    public StagingareaMainView() {
        super();
    }

    private FieldSet wrapFieldSet(BoxComponent comp, String caption){
        FieldSet fieldSet = new FieldSet();
        fieldSet.setLayout(new FitLayout());
        fieldSet.setHeading(caption);
        fieldSet.add(comp);
        return fieldSet;
    }

    @Override
    protected void initComponents() {
        super.initComponents();
        summaryView = new StagingContainerSummaryView();
        currentValidationView = new CurrentValidationView();
        previousExecutionValidationView = new PreviousExecutionView();

        ControllerContainer.initController(this, summaryView, currentValidationView, previousExecutionValidationView);
        ControllerContainer.get().getSummaryController().refreshView();
        ControllerContainer.get().getCurrentValidationController().refreshView();
    }

    @Override
    protected void initLayout() {
        mainPanel.setLayout(new RowLayout(Orientation.VERTICAL));
        mainPanel.add(wrapFieldSet(summaryView, "Status"), new RowData(1, -1, new Margins(4))); //$NON-NLS-1$
        mainPanel.add(wrapFieldSet(currentValidationView, "Current Validation"), new RowData(1, -1, new Margins(0, 4, 0, 4))); //$NON-NLS-1$
        mainPanel.add(wrapFieldSet(previousExecutionValidationView, "Previous Validation(s)"), new RowData(1, 1, new Margins(4))); //$NON-NLS-1$
        mainPanel.setStyleAttribute("font-size", "12px"); //$NON-NLS-1$//$NON-NLS-2$
        mainPanel.setBodyBorder(false);
    }

    public void doLayout() {
        mainPanel.layout(true);
    }
}
