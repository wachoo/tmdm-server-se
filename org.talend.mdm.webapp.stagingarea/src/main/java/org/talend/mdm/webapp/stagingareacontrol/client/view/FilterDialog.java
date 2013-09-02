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
import java.util.List;

import org.talend.mdm.webapp.stagingareacontrol.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.stagingareacontrol.client.model.ConceptRelationshipModel;
import org.talend.mdm.webapp.stagingareacontrol.client.model.FilterModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.Style.SelectionMode;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.ListViewSelectionModel;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FieldSet;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.google.gwt.dom.client.Style.Cursor;
import com.google.gwt.dom.client.Style.FontWeight;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FilterDialog extends Window {

    public interface FilterListener {

        void onFilter(FilterModel filterModel);
    }

    private ContentPanel container = new ContentPanel();

    Button changConcept = new Button(MessagesFactory.getMessages().change_concepts());

    Button deleteAll = new Button(MessagesFactory.getMessages().delete_all_concept());

    FieldSet conceptSet = new FieldSet();

    ListView<BaseModel> conceptList = new ListView<BaseModel>();

    StatusSet statusSet = new StatusSet();

    TimeSet timeSet = new TimeSet();

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

        conceptSet.setHeading(MessagesFactory.getMessages().entity_filter());

        FlexTable conceptLayout = new FlexTable();
        conceptLayout.setWidth("100%"); //$NON-NLS-1$
        conceptList.setStore(new ListStore<BaseModel>());
        conceptList.setWidth("100%"); //$NON-NLS-1$
        conceptList.setHeight(200);
        ListViewSelectionModel<BaseModel> selectModel = new ListViewSelectionModel<BaseModel>();
        selectModel.setSelectionMode(SelectionMode.SINGLE);
        selectModel.setLocked(true);
        conceptList.setSelectionModel(selectModel);

        conceptLayout.setWidget(0, 0, new HTML("<b>" + MessagesFactory.getMessages().entity_filter_title() + "</b>")); //$NON-NLS-1$//$NON-NLS-2$
        conceptLayout.setWidget(1, 0, conceptList);
        conceptLayout.getFlexCellFormatter().setColSpan(0, 0, 2);
        conceptLayout.getFlexCellFormatter().setColSpan(1, 0, 2);

        conceptLayout.setWidget(2, 0, changConcept);
        conceptLayout.setWidget(2, 1, deleteAll);
        conceptLayout.getFlexCellFormatter().setAlignment(2, 1, HasHorizontalAlignment.ALIGN_RIGHT,
                HasVerticalAlignment.ALIGN_MIDDLE);

        conceptSet.add(conceptLayout);

        container.add(conceptSet);
        container.add(statusSet);
        container.add(timeSet);

        ok.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (statusSet.getStatuses().size() == 0) {
                    MessageBox.alert(null, MessagesFactory.getMessages().status_notice(), null);
                    return;
                }
                hide();
                FilterModel filter = new FilterModel();
                List<String> concepts = new ArrayList<String>();

                for (int i = 0; i < conceptList.getItemCount(); i++) {
                    BaseModel item = conceptList.getStore().getAt(i);
                    concepts.add((String) item.get("value")); //$NON-NLS-1$
                }

                filter.setConcepts(concepts);
                filter.setStatusCodes(statusSet.getStatuses());
                filter.setStartDate(timeSet.getStartDate());
                filter.setEndDate(timeSet.getEndDate());
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
        changConcept.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {

                ListStore<BaseModel> store = conceptList.getStore();
                List<String> concepts = new ArrayList<String>();
                for (int i = 0; i < store.getCount(); i++) {
                    BaseModel item = store.getAt(i);
                    concepts.add((String) item.get("value")); //$NON-NLS-1$
                }

                selector.show(new ConceptSelector.SelectorHandler() {

                    @Override
                    public void onSelect(List<String> concepts) {
                        conceptList.getStore().removeAll();
                        for (String concept : concepts) {
                            BaseModel item = new BaseModel();
                            item.set("text", concept); //$NON-NLS-1$
                            item.set("value", concept); //$NON-NLS-1$
                            conceptList.getStore().add(item);
                        }
                    }
                }, concepts);

            }
        });
        deleteAll.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                conceptList.getStore().removeAll();
            }
        });
        setSize(600, 550);
    }

    class StatusSet extends FieldSet {

        CheckBox status000 = new CheckBox("000"); //$NON-NLS-1$

        CheckBox status204 = new CheckBox("204"); //$NON-NLS-1$

        CheckBox status404 = new CheckBox("404"); //$NON-NLS-1$

        FlexTable table = new FlexTable();

        public StatusSet() {

            table.setWidth("100%"); //$NON-NLS-1$
            table.setCellPadding(3);

            HorizontalPanel statusLine = new HorizontalPanel();
            status000.setValue(true);
            status204.setValue(true);
            status404.setValue(true);
            status000.getElement().getStyle().setMarginRight(5D, Unit.PX);
            status204.getElement().getStyle().setMarginRight(5D, Unit.PX);
            status404.getElement().getStyle().setMarginRight(5D, Unit.PX);

            statusLine.add(status000);
            statusLine.add(status204);
            statusLine.add(status404);
            table.setWidget(0, 0, new Label(MessagesFactory.getMessages().status_code()));
            table.getFlexCellFormatter().setWidth(0, 0, "100px"); //$NON-NLS-1$
            table.setWidget(0, 1, statusLine);

            this.add(table);
            this.setHeading(MessagesFactory.getMessages().status_filter());
        }

        public List<String> getStatuses() {
            List<String> statuses = new ArrayList<String>();
            if (status000.getValue()) {
                statuses.add("000"); //$NON-NLS-1$
            }
            if (status204.getValue()) {
                statuses.add("204"); //$NON-NLS-1$
            }
            if (status404.getValue()) {
                statuses.add("404"); //$NON-NLS-1$
            }
            return statuses;
        }
    }

    class TimeSet extends FieldSet {

        Label all = new Label(MessagesFactory.getMessages().all());

        Label today = new Label(MessagesFactory.getMessages().today());

        Label yesterday = new Label(MessagesFactory.getMessages().yesterday());

        Label lastWeek = new Label(MessagesFactory.getMessages().last_week());

        Label lastMonth = new Label(MessagesFactory.getMessages().last_month());

        Label custom = new Label(MessagesFactory.getMessages().customizing());

        Label selectedLabel = all;

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

                    if (timeLabel == all) {
                        startDate.setValue(null);
                        endDate.setValue(null);
                    } else if (timeLabel == today) {
                        startDate.setValue(new Date(now.getTime()));
                        endDate.setValue(null);
                    } else if (timeLabel == yesterday) {
                        startDate.setValue(new Date(now.getTime() - DAY));
                        endDate.setValue(null);
                    } else if (timeLabel == lastWeek) {
                        startDate.setValue(new Date(now.getTime() - (7 * DAY)));
                        endDate.setValue(null);
                    } else if (timeLabel == lastMonth) {
                        startDate.setValue(new Date(now.getTime() - (30 * DAY)));
                        endDate.setValue(null);
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

        public TimeSet() {
            all.getElement().getStyle().setCursor(Cursor.TEXT);
            all.getElement().getStyle().setFontWeight(FontWeight.BOLD);
            all.getElement().getStyle().setColor("black"); //$NON-NLS-1$

            today.getElement().getStyle().setCursor(Cursor.POINTER);
            today.getElement().getStyle().setColor("blue"); //$NON-NLS-1$
            today.getElement().getStyle().setMargin(5D, Unit.PX);

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

            all.addClickHandler(lableHandler);
            today.addClickHandler(lableHandler);
            yesterday.addClickHandler(lableHandler);
            lastWeek.addClickHandler(lableHandler);
            lastMonth.addClickHandler(lableHandler);
            custom.addClickHandler(lableHandler);

            table.setWidget(0, 0, all);
            table.setText(0, 1, "-"); //$NON-NLS-1$
            table.setWidget(0, 2, today);
            table.setText(0, 3, "-"); //$NON-NLS-1$
            table.setWidget(0, 4, yesterday);
            table.setText(0, 5, "-"); //$NON-NLS-1$
            table.setWidget(0, 6, lastWeek);
            table.setText(0, 7, "-"); //$NON-NLS-1$
            table.setWidget(0, 8, lastMonth);

            table.setWidget(0, 9, custom);

            startDate.setValue(null);
            endDate.setValue(null);
            termPanel.setVisible(false);
            termPanel.add(startDate);
            termPanel.add(new Label(MessagesFactory.getMessages().to()));
            termPanel.add(endDate);

            vp.add(table);
            vp.add(termPanel);
            this.setHeading(MessagesFactory.getMessages().datetime_filter());
            this.add(vp);
        }

        public Date getStartDate() {
            return startDate.getValue();
        }

        public Date getEndDate() {
            return endDate.getValue();
        }
    }
}
