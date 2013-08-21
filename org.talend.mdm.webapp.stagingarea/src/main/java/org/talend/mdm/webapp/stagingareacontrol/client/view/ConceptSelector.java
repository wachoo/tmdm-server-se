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

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;

public class ConceptSelector extends Dialog {

    public interface SelectorHandler {

        void onSelect(List<String> concepts);
    }

    SelectorHandler selectHandler;

    ConceptRelationshipModel relationModel;

    ScrollPanel container = new ScrollPanel();

    FlexTable flextable = new FlexTable();

    List<CheckBox> boxes = new ArrayList<CheckBox>();

    ConceptSelector(ConceptRelationshipModel relationModel) {
        this.relationModel = relationModel;
        this.setLayout(new FitLayout());
        this.setModal(true);
        this.setBlinkModal(true);
        this.setHeading(MessagesFactory.getMessages().concept_selector());
        this.setButtons(OKCANCEL);
        this.setSize(600, 300);
        initLayout();
        initBoxesEVent();
    }

    ClickHandler selectedTogetherHandler = new ClickHandler() {

        @Override
        public void onClick(ClickEvent event) {
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
        this.setScrollMode(Scroll.AUTO);

        flextable.setWidth("100%"); //$NON-NLS-1$

        flextable.getElement().getStyle().setFontSize(12D, Unit.PX);
        flextable.setCellSpacing(3);
        flextable.setCellPadding(5);

        flextable.getElement().getStyle().setBackgroundColor("white"); //$NON-NLS-1$
        flextable.getColumnFormatter().setWidth(0, "200px"); //$NON-NLS-1$
        Label conceptLb = new Label(MessagesFactory.getMessages().concept());
        conceptLb.getElement().getStyle().setColor("rgb(102,102,102)"); //$NON-NLS-1$
        Label fkRelation = new Label(MessagesFactory.getMessages().fk_relation());
        fkRelation.getElement().getStyle().setColor("rgb(255,255,251)"); //$NON-NLS-1$
        flextable.setWidget(0, 0, conceptLb);
        flextable.setWidget(0, 1, fkRelation);

        flextable.getCellFormatter().getElement(0, 0).getStyle().setBackgroundColor("rgb(223,223,223)"); //$NON-NLS-1$
        flextable.getCellFormatter().getElement(0, 1).getStyle().setBackgroundColor("rgb(154,189,234)"); //$NON-NLS-1$

        flextable.getRowFormatter().getElement(0).getStyle().setFontWeight(FontWeight.BOLD);
        flextable.getRowFormatter().getElement(0).getStyle().setFontSize(24D, Unit.PX);
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
                fksPanel.add(fkBox);
            }
            conceptCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

                @Override
                public void onValueChange(ValueChangeEvent<Boolean> event) {
                    fksPanel.setVisible(conceptCheck.getValue());
                }
            });

            flextable.setWidget(i, 0, conceptCheck);
            flextable.setWidget(i, 1, fksPanel);
            flextable.getCellFormatter().getElement(i, 0).getStyle().setBackgroundColor("rgb(233,233,233)"); //$NON-NLS-1$
            flextable.getCellFormatter().getElement(i, 1).getStyle().setBackgroundColor("rgb(243,243,243)"); //$NON-NLS-1$
            flextable.getRowFormatter().getElement(i).setAttribute("height", "24"); //$NON-NLS-1$//$NON-NLS-2$
            i++;
        }
        container.setWidget(flextable);
        container.getElement().getStyle().setBackgroundColor("white"); //$NON-NLS-1$
        this.add(container);
    }

    private void initBoxesEVent() {
        for (CheckBox box : boxes) {
            box.addClickHandler(selectedTogetherHandler);
        }
    }

    private List<String> getSelectedConcepts() {
        List<String> concepts = new ArrayList<String>();
        for (int i = 1; i < flextable.getRowCount(); i++) {
            CheckBox conceptBox = (CheckBox) flextable.getWidget(i, 0);
            if (conceptBox.getValue()) {
                concepts.add(conceptBox.getText());
            }
        }
        return concepts;
    }

    public void show(SelectorHandler selectHandler) {
        this.show();
        this.selectHandler = selectHandler;
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
}
