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
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.talend.mdm.webapp.stagingareacontrol.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.stagingareacontrol.client.model.ConceptRelationshipModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.FilterModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

public class FilterDialog extends Window {

    public interface FilterListener {

        void onFilter(FilterModel filterModel);
    }

    private ContentPanel container = new ContentPanel();

    ToolBar toolBar = new ToolBar();

    Button selectConcept = new Button(MessagesFactory.getMessages().select_concept());

    FieldSet conceptSet = new FieldSet();

    FlowPanel conceptSelector = new FlowPanel();

    FilterSet filterSet = new FilterSet();

    private Button ok = new Button(MessagesFactory.getMessages().ok());

    private Button cancel = new Button(MessagesFactory.getMessages().cancel());

    final ConceptSelector selector;

    public static FilterDialog showFilter(ConceptRelationshipModel relation, FilterListener listener) {
        FilterDialog dialog = new FilterDialog(relation, listener);
        dialog.show();
        return dialog;
    }

    public FilterDialog(final ConceptRelationshipModel relation, final FilterListener listener) {
        this.setClosable(false);
        this.setLayout(new FitLayout());
        this.setModal(true);
        selector = new ConceptSelector(relation);

        container.setHeaderVisible(false);
        container.setBodyBorder(false);
        container.setScrollMode(Scroll.AUTOY);

        conceptSet.setHeading(MessagesFactory.getMessages().concept_collection());
        conceptSet.add(conceptSelector);

        container.add(conceptSet);
        container.add(filterSet);

        ok.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
                FilterModel filter = new FilterModel();
                List<String> concepts = new ArrayList<String>();

                Iterator<Widget> conceptIter = conceptSelector.iterator();
                while (conceptIter.hasNext()) {
                    Label conceptlb = (Label) conceptIter.next();
                    concepts.add(conceptlb.getText());
                }

                filter.setConcepts(concepts);
                filter.setStatusCodes(filterSet.getStatuses());
                filter.setStartDate(filterSet.getStartDate());
                filter.setEndDate(filterSet.getEndDate());
                listener.onFilter(filter);
            }
        });
        cancel.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();
            }
        });
        this.addButton(ok);
        this.addButton(cancel);
        this.setButtonAlign(HorizontalAlignment.RIGHT);

        this.add(container);
        selectConcept.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

                selector.show(new ConceptSelector.SelectorHandler() {

                    @Override
                    public void onSelect(List<String> concepts) {
                        conceptSelector.clear();
                        for (String concept : concepts) {
                            Label clb = new Label(concept);
                            clb.getElement().getStyle().setMargin(5D, Unit.PX);
                            clb.getElement().getStyle().setFloat(Float.LEFT);
                            conceptSelector.add(clb);
                        }
                    }
                });

            }
        });
        toolBar.add(selectConcept);
        this.setTopComponent(toolBar);
        setSize(480, 280);
    }

    class FilterSet extends FieldSet {

        CheckBox status000 = new CheckBox("000"); //$NON-NLS-1$

        CheckBox status400 = new CheckBox("400"); //$NON-NLS-1$

        CheckBox status401 = new CheckBox("401"); //$NON-NLS-1$

        CheckBox status402 = new CheckBox("402"); //$NON-NLS-1$

        CheckBox status403 = new CheckBox("403"); //$NON-NLS-1$

        CheckBox status404 = new CheckBox("404"); //$NON-NLS-1$

        FlexTable table = new FlexTable();

        TimePanel timePanel = new TimePanel();

        public FilterSet() {

            table.setWidth("100%"); //$NON-NLS-1$
            table.setCellPadding(3);

            HorizontalPanel statusLine = new HorizontalPanel();
            status000.getElement().getStyle().setMarginRight(5D, Unit.PX);
            status400.getElement().getStyle().setMarginRight(5D, Unit.PX);
            status401.getElement().getStyle().setMarginRight(5D, Unit.PX);
            status402.getElement().getStyle().setMarginRight(5D, Unit.PX);
            status403.getElement().getStyle().setMarginRight(5D, Unit.PX);
            status404.getElement().getStyle().setMarginRight(5D, Unit.PX);

            statusLine.add(status000);
            statusLine.add(status400);
            statusLine.add(status401);
            statusLine.add(status402);
            statusLine.add(status403);
            statusLine.add(status404);
            table.setWidget(0, 0, new Label(MessagesFactory.getMessages().status_code()));
            table.getFlexCellFormatter().setWidth(0, 0, "100px"); //$NON-NLS-1$
            table.setWidget(0, 1, statusLine);

            table.setWidget(1, 0, timePanel);
            table.getFlexCellFormatter().setColSpan(1, 0, 4);

            this.add(table);
            this.setHeading(MessagesFactory.getMessages().condition());
        }

        public List<String> getStatuses() {
            List<String> statuses = new ArrayList<String>();
            if (status000.getValue()) {
                statuses.add("000"); //$NON-NLS-1$
            }
            if (status400.getValue()) {
                statuses.add("400"); //$NON-NLS-1$
            }
            if (status401.getValue()) {
                statuses.add("401"); //$NON-NLS-1$
            }
            if (status402.getValue()) {
                statuses.add("402"); //$NON-NLS-1$
            }
            if (status403.getValue()) {
                statuses.add("403"); //$NON-NLS-1$
            }
            if (status404.getValue()) {
                statuses.add("404"); //$NON-NLS-1$
            }
            return statuses;
        }

        public Date getStartDate() {
            return timePanel.getStartDate();
        }

        public Date getEndDate() {
            return timePanel.getEndDate();
        }
    }

    class TimePanel extends Composite {

        Label today = new Label(MessagesFactory.getMessages().today());

        Label yesterday = new Label(MessagesFactory.getMessages().yesterday());

        Label lastWeek = new Label(MessagesFactory.getMessages().last_week());

        Label lastMonth = new Label(MessagesFactory.getMessages().last_month());

        Label custom = new Label(MessagesFactory.getMessages().customizing());

        Label selectedLabel = today;

        DateField startDate = new DateField();

        DateField endDate = new DateField();

        HorizontalPanel termPanel = new HorizontalPanel();

        FlexTable table = new FlexTable();

        VerticalPanel vp = new VerticalPanel();

        static final long DAY = 1000 * 60 * 60 * 24;

        Date now = new Date();
        {
            now.setHours(0);
            now.setMinutes(0);
            now.setSeconds(0);
        }

        ClickHandler lableHandler = new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                Label timeLabel = (Label) event.getSource();
                if (timeLabel != selectedLabel) {

                    selectedLabel.getElement().getStyle().setCursor(Cursor.POINTER);
                    selectedLabel.getElement().getStyle().setColor("blue"); //$NON-NLS-1$
                    selectedLabel.getElement().getStyle().setFontWeight(FontWeight.NORMAL);

                    timeLabel.getElement().getStyle().setCursor(Cursor.TEXT);
                    timeLabel.getElement().getStyle().setColor("black"); //$NON-NLS-1$
                    timeLabel.getElement().getStyle().setFontWeight(FontWeight.BOLD);

                    if (timeLabel == today) {
                        startDate.setValue(new Date(now.getTime()));
                        endDate.setValue(new Date());
                    } else if (timeLabel == yesterday) {
                        startDate.setValue(new Date(now.getTime() - DAY));
                        endDate.setValue(new Date());
                    } else if (timeLabel == lastWeek) {
                        startDate.setValue(new Date(now.getTime() - (7 * DAY)));
                        endDate.setValue(new Date());
                    } else if (timeLabel == lastMonth) {
                        startDate.setValue(new Date(now.getTime() - (30 * DAY)));
                        endDate.setValue(new Date());
                    }

                    if (timeLabel == custom) {
                        termPanel.setVisible(true);
                    } else {
                        termPanel.setVisible(false);
                    }

                    selectedLabel = timeLabel;
                }
            }
        };

        public TimePanel() {
            today.getElement().getStyle().setCursor(Cursor.TEXT);
            today.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            today.getElement().getStyle().setColor("black"); //$NON-NLS-1$

            yesterday.getElement().getStyle().setCursor(Cursor.POINTER);
            yesterday.getElement().getStyle().setColor("blue"); //$NON-NLS-1$
            yesterday.getElement().getStyle().setMargin(5D, Unit.PX);

            lastWeek.getElement().getStyle().setCursor(Cursor.POINTER);
            lastWeek.getElement().getStyle().setColor("blue"); //$NON-NLS-1$
            lastWeek.getElement().getStyle().setMargin(5D, Unit.PX);

            lastMonth.getElement().getStyle().setCursor(Cursor.POINTER);
            lastMonth.getElement().getStyle().setColor("blue"); //$NON-NLS-1$
            lastMonth.getElement().getStyle().setMargin(5D, Unit.PX);

            custom.getElement().getStyle().setCursor(Cursor.POINTER);
            custom.getElement().getStyle().setColor("blue"); //$NON-NLS-1$
            custom.getElement().getStyle().setMargin(5D, Unit.PX);

            today.addClickHandler(lableHandler);
            yesterday.addClickHandler(lableHandler);
            lastWeek.addClickHandler(lableHandler);
            lastMonth.addClickHandler(lableHandler);
            custom.addClickHandler(lableHandler);

            table.setWidget(0, 0, today);
            table.setText(0, 1, "-"); //$NON-NLS-1$
            table.setWidget(0, 2, yesterday);
            table.setText(0, 3, "-"); //$NON-NLS-1$
            table.setWidget(0, 4, lastWeek);
            table.setText(0, 5, "-"); //$NON-NLS-1$
            table.setWidget(0, 6, lastMonth);

            table.setWidget(0, 7, custom);

            startDate.setValue(new Date(now.getTime()));
            endDate.setValue(new Date(now.getTime() + DAY));
            termPanel.setVisible(false);
            termPanel.add(startDate);
            termPanel.add(new Label(MessagesFactory.getMessages().to()));
            termPanel.add(endDate);

            vp.add(table);
            vp.add(termPanel);

            this.initWidget(vp);
        }

        public Date getStartDate() {
            return startDate.getValue();
        }

        public Date getEndDate() {
            return endDate.getValue();
        }
    }
}
