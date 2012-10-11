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

import com.extjs.gxt.ui.client.event.*;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToggleButton;
import com.extjs.gxt.ui.client.widget.form.LabelField;
import com.extjs.gxt.ui.client.widget.layout.*;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import org.talend.mdm.webapp.stagingareacontrol.client.controller.ControllerContainer;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.BoxComponent;
import com.extjs.gxt.ui.client.widget.form.FieldSet;

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
        RowLayout layout = new RowLayout();
        mainPanel.setLayout(layout);

        ContentPanel refreshPanel = new ContentPanel();
        refreshPanel.setHeaderVisible(false);
        refreshPanel.setBodyBorder(false);
        HBoxLayout refreshLayout = new HBoxLayout();
        refreshLayout.setHBoxLayoutAlign(HBoxLayout.HBoxLayoutAlign.TOP);
        refreshLayout.setPack(BoxLayout.BoxLayoutPack.END);
        refreshPanel.setLayout(refreshLayout);
        Button refresh = new Button(messages.refresh());
        refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {
            @Override
            public void componentSelected(ButtonEvent ce) {
                ControllerContainer.get().getSummaryController().refreshView();
                ControllerContainer.get().getCurrentValidationController().refreshView();
            }
        });
        refreshPanel.add(refresh);
        final ToggleButton autoRefreshToggle = new ToggleButton(messages.off());
        autoRefreshToggle.addListener(Events.Toggle, new Listener<BaseEvent>() {
            public void handleEvent(BaseEvent be) {
                autoRefreshToggle.setText(autoRefreshToggle.isPressed() ? messages.on() : messages.off());
                ControllerContainer.get().getCurrentValidationController().autoRefresh(autoRefreshToggle.isPressed());
            }
        });
        refreshPanel.add(autoRefreshToggle);
        mainPanel.add(refreshPanel, new RowData(1, -1, new Margins(0, 23, 0, 0)));
        mainPanel.add(wrapFieldSet(summaryView, messages.status()), new RowData(1, -1, new Margins(0, 23, 0, 0)));
        mainPanel.add(wrapFieldSet(currentValidationView, messages.current_validation()), new RowData(1, -1, new Margins(0, 23, 0, 0)));
        mainPanel.add(wrapFieldSet(previousExecutionValidationView, messages.previous_validation()), new RowData(1, -1, new Margins(0, 23, 0, 0)));
    }

    public void doLayout() {
        mainPanel.layout(true);
    }
}
