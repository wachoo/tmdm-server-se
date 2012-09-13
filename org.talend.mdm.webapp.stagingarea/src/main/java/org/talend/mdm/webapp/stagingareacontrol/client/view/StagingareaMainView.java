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
package org.talend.mdm.webapp.stagingareacontrol.client.view;

import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;

import com.extjs.gxt.ui.client.Style.Scroll;
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
        mainPanel.setBodyBorder(false);
        mainPanel.setScrollMode(Scroll.AUTOY);
        mainPanel.setStyleAttribute("font-size", "12px"); //$NON-NLS-1$//$NON-NLS-2$
        mainPanel.setStyleAttribute("margin", "3px"); //$NON-NLS-1$//$NON-NLS-2$
        summaryView = new StagingContainerSummaryView();
        currentValidationView = new CurrentValidationView();
        previousExecutionValidationView = new PreviousExecutionView();
        ControllerContainer.initController(this, summaryView, currentValidationView, previousExecutionValidationView);
    }

    @Override
    protected void initLayout() {
        mainPanel.setLayout(new RowLayout());
        mainPanel.add(wrapFieldSet(summaryView, messages.status()), new RowData(1, -1, new Margins(0, 23, 0, 0)));
        mainPanel.add(wrapFieldSet(currentValidationView, messages.current_validation()), new RowData(1, -1,  new Margins(0, 23, 0, 0)));
        mainPanel.add(wrapFieldSet(previousExecutionValidationView, messages.previous_validation()), new RowData(1, -1,  new Margins(0, 23, 0, 0)));
    }

    public void doLayout() {
        mainPanel.layout(true);
    }
}
