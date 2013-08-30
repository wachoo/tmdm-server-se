// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
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

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.webapp.stagingareacontrol.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.stagingareacontrol.client.model.ConceptRelationshipModel;

import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;

public class ConceptSelector extends Dialog {

    public interface SelectorHandler {

        void onSelect(List<String> concepts);
    }

    SelectorHandler selectHandler;

    ConceptRelationshipModel relationModel;

    AbsolutePanel wrapPanel = new AbsolutePanel();

    Grid header = new Grid(1, 2);

    ScrollPanel container = new ScrollPanel();

    FlexTable flextable = new FlexTable();

    List<CheckBox> boxes = new ArrayList<CheckBox>();

    ConceptSelector(ConceptRelationshipModel relationModel) {
        this.relationModel = relationModel;
        this.setLayout(new FitLayout());
        this.setModal(true);
        this.setBlinkModal(true);
        this.setHeading(MessagesFactory.getMessages().entity_selector());
        this.setButtons(OKCANCEL);
        this.setSize(800, 400);
        initLayout();
        initBoxesEVent();
    }

    ValueChangeHandler<Boolean> selectedTogetherHandler = new ValueChangeHandler<Boolean>() {

        @Override
        public void onValueChange(ValueChangeEvent<Boolean> event) {
            CheckBox sourceBox = (CheckBox) event.getSource();
            for (CheckBox box : boxes) {
                if (sourceBox.getText().equals(box.getText())) {
                    box.setValue(sourceBox.getValue(), true);
                }
            }
        }
    };

    private void initLayout() {
        this.setClosable(false);
        this.setLayout(new FitLayout());

        flextable.setWidth("100%"); //$NON-NLS-1$

        flextable.getElement().getStyle().setFontSize(12D, Unit.PX);
        flextable.setCellSpacing(3);
        flextable.setCellPadding(5);

        flextable.getElement().getStyle().setBackgroundColor("white"); //$NON-NLS-1$
        flextable.getColumnFormatter().setWidth(0, "300px"); //$NON-NLS-1$

        flextable.setText(0, 0, ""); //$NON-NLS-1$
        flextable.getFlexCellFormatter().setColSpan(0, 0, 2);
        flextable.getRowFormatter().getElement(0).setAttribute("height", "36"); //$NON-NLS-1$//$NON-NLS-2$

        int i = 1;
        for (String concept : relationModel.getConcepts()) {
            final CheckBox conceptCheck = new CheckBox(concept);
            boxes.add(conceptCheck);
            conceptCheck.getElement().getStyle().setMarginRight(5D, Unit.PX);
            final FlowPanel fksPanel = new FlowPanel();
            fksPanel.setVisible(false);
            String[] fkConcepts = relationModel.getRelationShipMap().get(concept);
            for (String fkConcept : fkConcepts) {
                CheckBox fkBox = new CheckBox(fkConcept);
                fkBox.getElement().getStyle().setMarginLeft(5D, Unit.PX);
                boxes.add(fkBox);
                fksPanel.add(new CheckBoxWrap(fkBox));
            }
            conceptCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    fksPanel.setVisible(conceptCheck.getValue());
                }
            });

            flextable.setWidget(i, 0, new CheckBoxWrap(conceptCheck));
            flextable.setWidget(i, 1, fksPanel);
            flextable.getCellFormatter().getElement(i, 0).getStyle().setBackgroundColor("rgb(233,233,233)"); //$NON-NLS-1$
            flextable.getCellFormatter().getElement(i, 1).getStyle().setBackgroundColor("rgb(243,243,243)"); //$NON-NLS-1$
            flextable.getRowFormatter().getElement(i).setAttribute("height", "24"); //$NON-NLS-1$//$NON-NLS-2$
            i++;
        }

        container.setSize("100%", "100%"); //$NON-NLS-1$//$NON-NLS-2$
        container.setWidget(flextable);
        container.getElement().getStyle().setBackgroundColor("white"); //$NON-NLS-1$

        Label conceptLb = new Label(MessagesFactory.getMessages().entity());
        conceptLb.getElement().getStyle().setColor("rgb(102,102,102)"); //$NON-NLS-1$
        Label fkRelation = new Label(MessagesFactory.getMessages().dep_fk());
        fkRelation.getElement().getStyle().setColor("rgb(255,255,251)"); //$NON-NLS-1$

        header.setCellSpacing(3);
        header.setCellPadding(5);
        header.getColumnFormatter().setWidth(0, "300px"); //$NON-NLS-1$
        header.getElement().getStyle().setBackgroundColor("white"); //$NON-NLS-1$
        header.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor("rgb(223,223,223)"); //$NON-NLS-1$
        header.getCellFormatter().getElement(0, 1).getStyle().setBackgroundColor("rgb(154,189,234)"); //$NON-NLS-1$

        header.getRowFormatter().getElement(0).getStyle().setFontWeight(FontWeight.BOLD);
        header.getRowFormatter().getElement(0).getStyle().setFontSize(24D, Unit.PX);
        header.getRowFormatter().getElement(0).setAttribute("height", "36"); //$NON-NLS-1$//$NON-NLS-2$
        header.setWidget(0, 0, conceptLb);
        header.setWidget(0, 1, fkRelation);

        wrapPanel.add(container, 0, 0);
        wrapPanel.add(header, 0, 0);
        this.add(wrapPanel);
    }

    @Override
    protected void onResize(int width, int height) {
        super.onResize(width, height);
        adjust();
    }

    @Override
    protected void onRender(Element parent, int pos) {
        super.onRender(parent, pos);
        adjust();
    }

    private void adjust() {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {
                int cH = container.getElement().getOffsetHeight();
                int fH = flextable.getElement().getOffsetHeight();
                if (fH > cH) {
                    header.setWidth(getBody().dom.getOffsetWidth() - 17 + "px"); //$NON-NLS-1$
                } else {
                    header.setWidth("100%"); //$NON-NLS-1$
                }
            }
        });
    }

    private void initBoxesEVent() {
        for (CheckBox box : boxes) {
            box.addValueChangeHandler(selectedTogetherHandler);
        }
    }

    private List<String> getSelectedConcepts() {
        List<String> concepts = new ArrayList<String>();
        for (int i = 1; i < flextable.getRowCount(); i++) {
            CheckBoxWrap conceptBox = (CheckBoxWrap) flextable.getWidget(i, 0);
            if (conceptBox.getValue()) {
                concepts.add(conceptBox.getText());
            }
        }
        return concepts;
    }

    private void initSelectedConcepts(List<String> concepts) {
        for (int i = 1; i < flextable.getRowCount(); i++) {
            CheckBoxWrap conceptBox = (CheckBoxWrap) flextable.getWidget(i, 0);
            conceptBox.setValue(false, true);
            if (concepts.indexOf(conceptBox.getText()) != -1) {
                conceptBox.setValue(true, true);
            }
        }
    }

    public void show(SelectorHandler selectHandler, List<String> concepts) {
        this.show();
        this.selectHandler = selectHandler;
        initSelectedConcepts(concepts);
    }

    @Override
    protected void onButtonPressed(Button button) {
        if (button == getButtonBar().getItemByItemId(OK)) {
            hide(button);
            if (selectHandler != null) {
                selectHandler.onSelect(getSelectedConcepts());
            }
        } else if (button == getButtonBar().getItemByItemId(CANCEL)) {
            hide(button);
        }
    }

    class CheckBoxWrap extends Composite {

        SimplePanel sp = new SimplePanel();

        CheckBox box;

        public CheckBoxWrap(CheckBox box) {
            sp.setWidget(box);
            this.box = box;
            sp.getElement().getStyle().setFloat(Float.LEFT);
            this.initWidget(sp);
        }

        public CheckBox getBox() {
            return box;
        }

        public boolean getValue() {
            return box.getValue();
        }

        public void setValue(boolean value, boolean fireEvents) {
            box.setValue(value, fireEvents);
        }

        public String getText() {
            return box.getText();
        }
    }
}
