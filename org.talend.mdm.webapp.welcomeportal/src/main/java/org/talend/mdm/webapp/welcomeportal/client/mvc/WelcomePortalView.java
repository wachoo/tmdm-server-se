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
package org.talend.mdm.webapp.welcomeportal.client.mvc;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.welcomeportal.client.GenerateContainer;
import org.talend.mdm.webapp.welcomeportal.client.MainFramePanel;
import org.talend.mdm.webapp.welcomeportal.client.WelcomePortalEvents;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.mvc.AppEvent;
import com.extjs.gxt.ui.client.mvc.Controller;
import com.extjs.gxt.ui.client.mvc.View;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.custom.Portal;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;

/**
 * DOC Administrator class global comment. Detailled comment
 */

public class WelcomePortalView extends View {

    private static final String USING_DEFAULT_COLUMN_NUM = "defaultColNum"; //$NON-NLS-1$

    private static final String CHARTS_ENABLED = "chartsOn"; //$NON-NLS-1$

    private static final int DEFAULT_COLUMN_NUM = 3;

    private static final int ALTERNATIVE_COLUMN_NUM = 2;

    private int numColumns;

    private boolean chartsOn;

    private Portal portal;

    private boolean chartsSwitcherUpdated;

    private static PortalProperties portalConfigCache;

    public WelcomePortalView(Controller controller) {
        super(controller);
    }

    @Override
    protected void handleEvent(AppEvent event) {
        if (event.getType() == WelcomePortalEvents.InitFrame) {
            onInitFrame(event);
        } else if (event.getType() == WelcomePortalEvents.RefreshPortlet) {
            onRefreshPortlet();
        } else if (event.getType() == WelcomePortalEvents.RefreshPortal) {
            onRefreshPortal(event);
        } else if (event.getType() == WelcomePortalEvents.RevertRefreshPortal) {
            onRevertRefreshPortal(event);
        }
    }

    private void onRefreshPortal(AppEvent event) {
        String dataString = event.getData();

        Map<String, Boolean> parsedUserConfig = parseConfig(dataString);
        Boolean defaultColConfig = parsedUserConfig.get(USING_DEFAULT_COLUMN_NUM);
        chartsSwitcherUpdated = !(chartsOn == parsedUserConfig.get(CHARTS_ENABLED));
        ContentPanel container = GenerateContainer.getContentPanel();
        if ((!chartsSwitcherUpdated)
                && ((defaultColConfig && numColumns == DEFAULT_COLUMN_NUM) || (!defaultColConfig && numColumns == ALTERNATIVE_COLUMN_NUM))) {
            ((MainFramePanel) (container.getItems().get(0))).refresh(parsedUserConfig);
        } else {// for switching to diff column number or chartsSwitherUpdated
            updatePortal(parsedUserConfig);
        }
    }

    private void onRevertRefreshPortal(AppEvent event) {

        if (Log.isInfoEnabled()) {
            Log.info("Revert RefreshPortal due to saving to db failed... ");//$NON-NLS-1$
        }

        ContentPanel container = GenerateContainer.getContentPanel();

        portalConfigCache = (PortalProperties) event.getData();

        numColumns = portalConfigCache.getColumnNum();

        ((MainFramePanel) portal).stopAutoRefresh();
        ((MainFramePanel) portal).removeAllPortlets();

        container.remove(portal);
        portal = new MainFramePanel(numColumns, portalConfigCache);
        container.add(portal);
        container.layout(true);
    }

    private Map<String, Boolean> parseConfig(String dataString) {
        Map<String, Boolean> config = new HashMap<String, Boolean>();
        String temp = dataString.substring(1, dataString.length() - 1);
        String[] nameValues = temp.split(","); //$NON-NLS-1$
        String name;
        Boolean visible;
        String[] nameValuePair;
        for (String nameValue : nameValues) {
            nameValuePair = nameValue.split("="); //$NON-NLS-1$
            name = nameValuePair[0].trim();
            visible = Boolean.parseBoolean(nameValuePair[1]);
            config.put(name, visible);
        }
        return config;
    }

    private void updatePortal(final Map<String, Boolean> userConfig) {
        if (Log.isInfoEnabled()) {
            Log.info("Refresh with different column number... ");//$NON-NLS-1$
        }

        ContentPanel container = GenerateContainer.getContentPanel();
        numColumns = userConfig.get(USING_DEFAULT_COLUMN_NUM) ? DEFAULT_COLUMN_NUM : ALTERNATIVE_COLUMN_NUM;
        chartsOn = userConfig.get(CHARTS_ENABLED);

        ((MainFramePanel) portal).stopAutoRefresh();
        ((MainFramePanel) portal).removeAllPortlets();
        container.remove(portal);

        portal = new MainFramePanel(numColumns, portalConfigCache, userConfig);
        container.add(portal);
        container.layout(true);
    }

    private void onRefreshPortlet() {
        ContentPanel container = GenerateContainer.getContentPanel();
        ((MainFramePanel) (container.getItems().get(0))).refreshPortlets();
    }

    private void onInitFrame(AppEvent event) {
        if (Log.isInfoEnabled()) {
            Log.info("Init frame... ");//$NON-NLS-1$
        }

        portalConfigCache = (PortalProperties) event.getData();
        final ContentPanel container = GenerateContainer.getContentPanel();
        container.setHeaderVisible(false);
        container.setLayout(new FitLayout());
        container.setStyleAttribute("height", "100%");//$NON-NLS-1$ //$NON-NLS-2$

        Integer numColumnsObj = portalConfigCache.getColumnNum();
        numColumns = (numColumnsObj == null) ? DEFAULT_COLUMN_NUM : numColumnsObj;

        Boolean chartsOnObj = portalConfigCache.getChartsOn();
        chartsOn = (chartsOnObj == null) ? true : chartsOnObj;
        portal = new MainFramePanel(numColumns, portalConfigCache);
        container.add(portal);
        container.layout(true);
    }
}
