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
package org.talend.mdm.webapp.browserecords.client.widget;

import com.allen_sauer.gwt.log.client.Log;
import com.extjs.gxt.ui.client.Style.LayoutRegion;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.layout.BorderLayout;
import com.extjs.gxt.ui.client.widget.layout.BorderLayoutData;

public class BrowseStagingRecordsPanel extends ContentPanel {

    private static BrowseStagingRecordsPanel panel;

    public static BrowseStagingRecordsPanel getInstance() {
        if (panel == null) {
            panel = new BrowseStagingRecordsPanel();
        }
        return panel;
    }

    private BrowseStagingRecordsPanel() {
        if (Log.isInfoEnabled()) {
            Log.info("Init BrowseStagingRecordsPanel... ");//$NON-NLS-1$
        }

        setHeaderVisible(false);
        BorderLayout layout = new BorderLayout();
        setLayout(layout);
        setStyleAttribute("height", "100%");//$NON-NLS-1$ //$NON-NLS-2$  
        BorderLayoutData centerData = new BorderLayoutData(LayoutRegion.CENTER);
        centerData.setMargins(new Margins(0));
    }
}