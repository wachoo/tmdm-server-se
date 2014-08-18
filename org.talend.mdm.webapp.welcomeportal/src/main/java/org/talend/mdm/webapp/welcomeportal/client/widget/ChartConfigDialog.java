// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.welcomeportal.client.widget;

import org.talend.mdm.webapp.welcomeportal.client.i18n.MessagesFactory;
import org.talend.mdm.webapp.welcomeportal.client.mvc.ConfigModel;
import org.talend.mdm.webapp.welcomeportal.client.mvc.EntityConfigModel;
import org.talend.mdm.webapp.welcomeportal.client.mvc.TimeframeConfigModel;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.VerticalPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

public class ChartConfigDialog extends Window {

    private static final String TOP_ALL = "all"; //$NON-NLS-1$

    private static final String TOP_TEN = "10"; //$NON-NLS-1$

    private static final String TOP_FIVE = "5"; //$NON-NLS-1$

    private static final String TIMEFRAME_ALL = "all"; //$NON-NLS-1$

    private static final String TIMEFRAME_WEEK = "week"; //$NON-NLS-1$

    private static final String TIMEFRAME_DAY = "day"; //$NON-NLS-1$

    public interface ChartConfigListener {

        void onConfigUpdate(ConfigModel configModel);
    }

    private LayoutContainer container = new VerticalPanel();

    private String top;

    private String timeframe;

    private ConfigModel configModel;

    private boolean isForTopEntities;

    private Button ok = new Button(MessagesFactory.getMessages().chart_config_ok());

    private Button cancel = new Button(MessagesFactory.getMessages().chart_config_cancel());

    public static ChartConfigDialog showConfig(ConfigModel configModel, ChartConfigListener listener) {
        ChartConfigDialog dialog = new ChartConfigDialog(configModel, listener);
        dialog.show();
        return dialog;
    }

    public ChartConfigDialog(ConfigModel configModel, final ChartConfigListener listener) {
        if (configModel instanceof EntityConfigModel) {
            isForTopEntities = true;
            this.configModel = new EntityConfigModel(configModel.getSetting());
        } else {
            this.configModel = new TimeframeConfigModel(configModel.getSetting());
        }

        this.setClosable(false);
        this.setLayout(new FitLayout());
        this.setModal(true);
        this.setHeading(MessagesFactory.getMessages().chart_config_title());
        container.setBorders(false);
        container.setStyleAttribute("padding", "10px"); //$NON-NLS-1$//$NON-NLS-2$

        final RadioGroup configGroup = new RadioGroup() {

            @Override
            protected void onRadioSelected(Radio radio) {
                // Oddly, radio.getName returns RadioGroup's name, so use FieldLabel iso.
                String rdName = radio.getFieldLabel();
                if (isForTopEntities) {
                    top = rdName;
                } else {
                    timeframe = rdName;
                }
            }
        };

        if (isForTopEntities) {

            configGroup.setFieldLabel("Entities"); //$NON-NLS-1$

            Radio top5Rd = new Radio();
            top5Rd.setFieldLabel(TOP_FIVE);
            top5Rd.setBoxLabel(MessagesFactory.getMessages().chart_config_top5());
            configGroup.add(top5Rd);

            Radio top10Rd = new Radio();
            top10Rd.setFieldLabel(TOP_TEN);
            top10Rd.setBoxLabel(MessagesFactory.getMessages().chart_config_top10());
            configGroup.add(top10Rd);

            Radio topAllRd = new Radio();
            topAllRd.setFieldLabel(TOP_ALL);
            topAllRd.setBoxLabel(MessagesFactory.getMessages().chart_config_all());
            configGroup.add(topAllRd);

            if (TOP_FIVE.equals(configModel.getSetting())) {
                top5Rd.setValue(true);
            } else if (TOP_TEN.equals(configModel.getSetting())) {
                top10Rd.setValue(true);
            } else {
                topAllRd.setValue(true);
            }
        } else {
            configGroup.setFieldLabel("Time Frame"); //$NON-NLS-1$

            Radio lastWeekRd = new Radio();
            lastWeekRd.setFieldLabel(TIMEFRAME_WEEK);
            lastWeekRd.setBoxLabel(MessagesFactory.getMessages().chart_config_week());
            configGroup.add(lastWeekRd);

            Radio lastDayRd = new Radio();
            lastDayRd.setFieldLabel(TIMEFRAME_DAY);
            lastDayRd.setBoxLabel(MessagesFactory.getMessages().chart_config_day());
            configGroup.add(lastDayRd);

            Radio allTimeRd = new Radio();
            allTimeRd.setFieldLabel(TIMEFRAME_ALL);
            allTimeRd.setBoxLabel(MessagesFactory.getMessages().chart_config_all());
            configGroup.add(allTimeRd);

            if (TIMEFRAME_DAY.equals(configModel.getSetting())) {
                lastDayRd.setValue(true);
            } else if (TIMEFRAME_WEEK.equals(configModel.getSetting())) {
                lastWeekRd.setValue(true);
            } else {
                allTimeRd.setValue(true);
            }
        }

        container.add(configGroup);

        ok.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                hide();

                if (isForTopEntities) {
                    ((EntityConfigModel) ChartConfigDialog.this.configModel).setTopEntities(top);
                } else {
                    ((TimeframeConfigModel) ChartConfigDialog.this.configModel).setTimeFrame(timeframe);
                }

                listener.onConfigUpdate(ChartConfigDialog.this.configModel);
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

        setSize(300, 110);
    }

}
