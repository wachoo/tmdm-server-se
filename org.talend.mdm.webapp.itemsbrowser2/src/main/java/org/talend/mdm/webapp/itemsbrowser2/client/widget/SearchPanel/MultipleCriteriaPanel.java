// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.itemsbrowser2.client.widget.SearchPanel;

import org.talend.mdm.webapp.itemsbrowser2.client.model.Constants;
import org.talend.mdm.webapp.itemsbrowser2.client.model.MultipleCriteria;
import org.talend.mdm.webapp.itemsbrowser2.client.resources.icon.Icons;
import org.talend.mdm.webapp.itemsbrowser2.shared.ViewBean;

import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.widget.Label;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * DOC stephane class global comment. Detailled comment
 */
public class MultipleCriteriaPanel extends SimplePanel {

    private MultipleCriteriaPanel parent;

    private VerticalPanel rightPanel;

    private Panel separationLeftPanel;

    private Panel separationRightPanel;

    private ComboBox operatorComboBox;

    private ViewBean view;

    public MultipleCriteriaPanel(MultipleCriteriaPanel parent, ViewBean view) {
        super();
        this.parent = parent;
        this.view = view;
        this.add(getMainPanel());
    }

    public MultipleCriteriaPanel(ViewBean view) {
        this(null, view);
    }

    private Panel getMainPanel() {
        final HorizontalPanel main = new HorizontalPanel();
        main.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

        main.add(getOperatorPanel());

        separationLeftPanel = getSeparationPanel("left");
        main.add(separationLeftPanel);

        getRightPanel();
        main.add(rightPanel);

        separationRightPanel = getSeparationPanel("right");
        main.add(separationRightPanel);

        if (parent != null) {
            main.add(new Image(Icons.INSTANCE.remove()) {

                {
                    addClickListener(new ClickListener() {

                        public void onClick(Widget sender) {
                            if (parent != null)
                                parent.removeChildFilter(MultipleCriteriaPanel.this);
                            redraw();
                        }
                    });
                }
            });
        }

        // This timer is there because if we simply call redraw(), it does not work (because getOffsetHeight() doesn't
        // work, probably because panel isn't displayed yet).
        Timer timer = new Timer() {

            @Override
            public void run() {
                redraw();
            }
        };
        timer.schedule(10);

        return main;
    }

    private Panel getOperatorPanel() {
        Panel toReturn = new SimplePanel();

        operatorComboBox = new ComboBox();
        ListStore<BaseModel> list = new ListStore<BaseModel>();
        BaseModel field = null;

        for (String curOper : Constants.groupOperators) {
            field = new BaseModel();
            field.set("name", curOper);
            field.set("value", curOper);
            list.add(field);
        }

        operatorComboBox.setDisplayField("name");
        operatorComboBox.setValueField("value");
        operatorComboBox.setStore(list);
        operatorComboBox.setWidth("75px");
        toReturn.add(operatorComboBox);
        toReturn.setWidth("80px");
        return toReturn;
    }

    private Panel getSeparationPanel(String position) {
        Panel right = new SimplePanel();
        right.add(new Label(" "));
        right.setWidth("10px");
        right.setHeight("97%");
        right.addStyleName("separation-" + position);
        return right;
    }

    private void getRightPanel() {
        rightPanel = new VerticalPanel();
        addSimpleCriterionPanel();
        rightPanel.add(getAddCriterionPanel());
    }

    private Panel getAddCriterionPanel() {
        HorizontalPanel toReturn = new HorizontalPanel();

        toReturn.setSpacing(5);

        final Image image = new Image(Icons.INSTANCE.add());

        image.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                addSimpleCriterionPanel();
            }

        });

        toReturn.add(image);

        final Image button = new Image(Icons.INSTANCE.chart_organisation_add());
        button.setTitle("Add subclause");
        button.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                addMultipleCriteriaPanel();
            }
        });
        toReturn.add(button);

        return toReturn;
    }

    private SimpleCriterionPanel addSimpleCriterionPanel() {
        final SimpleCriterionPanel newPanel = new SimpleCriterionPanel(MultipleCriteriaPanel.this, rightPanel);
        newPanel.updateFields(view);
        final int index = (rightPanel.getWidgetCount() == 0 ? 0 : rightPanel.getWidgetCount() - 1);
        rightPanel.insert(newPanel, index);
        redraw();
        return newPanel;
    }

    private MultipleCriteriaPanel addMultipleCriteriaPanel() {
        final MultipleCriteriaPanel newPanel = new MultipleCriteriaPanel(MultipleCriteriaPanel.this, view);
        final int index = (rightPanel.getWidgetCount() == 0 ? 0 : rightPanel.getWidgetCount() - 1);
        rightPanel.insert(newPanel, index);
        redraw();
        return newPanel;
    }

    protected void redraw() {
        final int offsetHeight = rightPanel.getOffsetHeight();
        if (separationLeftPanel != null)
            separationLeftPanel.setHeight(offsetHeight + "px");
        if (separationRightPanel != null)
            separationRightPanel.setHeight(offsetHeight + "px");

        if (parent != null)
            parent.redraw();
    }

    protected void removeChildFilter(MultipleCriteriaPanel toRemove) {
        rightPanel.remove(toRemove);
    }

    public MultipleCriteria getCriteria() {
        MultipleCriteria toReturn = new MultipleCriteria(operatorComboBox.getValue().get("value").toString());

        for (int i = 0; i < rightPanel.getWidgetCount(); i++) {
            Widget widget = rightPanel.getWidget(i);
            if (widget instanceof SimpleCriterionPanel) {
                SimpleCriterionPanel criterionPanel = (SimpleCriterionPanel) widget;
                toReturn.add(criterionPanel.getCriterion());
            } else if (widget instanceof MultipleCriteriaPanel) {
                MultipleCriteriaPanel filterPanel = (MultipleCriteriaPanel) widget;
                toReturn.add(filterPanel.getCriteria());
            }
        }

        return toReturn;
    }

    public void clear() {
        for (int i = 0; i < rightPanel.getWidgetCount(); i++) {
            Widget widget = rightPanel.getWidget(i);
            if (widget instanceof SimpleCriterionPanel) {
                rightPanel.remove(widget);
            } else if (widget instanceof MultipleCriteriaPanel) {
                rightPanel.remove(widget);
            }
        }
    }

    // public void setCriteria(Criteria criteria) {
    // clear();
    // if (criteria instanceof MultipleCriteria) {
    // MultipleCriteria multipleCriteria = (MultipleCriteria) criteria;
    //
    // operatorComboBox.setSelected(multipleCriteria.getOperator());
    // for (Criteria current : multipleCriteria.getChildren()) {
    // if (current instanceof SimpleCriterion) {
    // SimpleCriterionPanel newPanel = addSimpleCriterionPanel();
    // newPanel.setCriterion((SimpleCriterion) current);
    // } else if (current instanceof MultipleCriteria) {
    // MultipleCriteriaPanel newPanel = addMultipleCriteriaPanel();
    // newPanel.setCriteria((MultipleCriteria) current);
    // }
    // }
    // } else if (criteria instanceof SimpleCriterion) {
    // SimpleCriterionPanel newPanel = addSimpleCriterionPanel();
    // newPanel.setCriterion((SimpleCriterion) criteria);
    // }
    // }
}
