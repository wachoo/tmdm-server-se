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

import org.talend.mdm.webapp.browserecords.client.widget.ItemsDetailPanel;

import com.google.gwt.dom.client.Style.Float;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.user.client.ui.Image;

public class ItemsDetailPanel4Staging extends ItemsDetailPanel {

    @Override
    public void clearBanner() {
        super.clearBanner();
        Image img = new Image("/browserecords/secure/img/staging.png"); //$NON-NLS-1$
        img.getElement().getStyle().setFloat(Float.RIGHT);
        img.getElement().getStyle().setMarginTop(6D, Unit.PX);
        banner.add(img);
    }
}
