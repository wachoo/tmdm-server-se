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
package org.talend.mdm.webapp.browserecordsinstaging.client.widget;

import org.talend.mdm.webapp.browserecords.client.widget.UploadFileFormPanel;
import org.talend.mdm.webapp.browserecords.shared.ViewBean;

import com.extjs.gxt.ui.client.widget.Window;

/**
 * created by yjli on 2013-10-18 Detailled comment
 * 
 */
public class UploadFileFormPanel4Staging extends UploadFileFormPanel {

    public static final String TITLE_STAGING_TASKID = "Match Group"; //$NON-NLS-1$

    public static final String TITLE_STAGING_STATUS = "Status"; //$NON-NLS-1$

    public static final String TITLE_STAGING_SOURCE = "Source"; //$NON-NLS-1$

    public static final String TITLE_STAGING_ERROR = "Error"; //$NON-NLS-1$

    /**
     * DOC talend2 UploadFileFormPanel4Staging constructor comment.
     * 
     * @param dataCluster
     * @param viewBean
     * @param window
     */
    public UploadFileFormPanel4Staging(String dataCluster, ViewBean viewBean, Window window) {
        super(dataCluster, viewBean, window);
    }

    @Override
    protected String getHeaderString() {
        StringBuilder header = new StringBuilder();
        header.append(super.getHeaderString());
        header.append(TITLE_STAGING_TASKID);
        header.append(TITLE_STAGING_STATUS);
        header.append(TITLE_STAGING_SOURCE);
        header.append(TITLE_STAGING_ERROR);
        return header.toString();
    }

    @Override
    protected String getViewableXpathString() {
        StringBuilder viewableXpath = new StringBuilder();
        viewableXpath.append(super.getViewableXpathString());
        viewableXpath.append(TITLE_STAGING_TASKID);
        viewableXpath.append("@true"); //$NON-NLS-1$
        viewableXpath.append(TITLE_STAGING_STATUS);
        viewableXpath.append("@true"); //$NON-NLS-1$
        viewableXpath.append(TITLE_STAGING_SOURCE);
        viewableXpath.append("@true"); //$NON-NLS-1$
        viewableXpath.append(TITLE_STAGING_ERROR);
        viewableXpath.append("@true"); //$NON-NLS-1$
        return viewableXpath.toString();
    }

    @Override
    protected String getActionUrl() {
        return "/browserecords/upload4Staging"; //$NON-NLS-1$
    }
}
