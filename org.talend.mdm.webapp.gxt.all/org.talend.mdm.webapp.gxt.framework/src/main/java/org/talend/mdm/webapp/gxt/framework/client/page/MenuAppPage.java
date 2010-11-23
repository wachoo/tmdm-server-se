// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package org.talend.mdm.webapp.gxt.framework.client.page;

import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.google.gwt.user.client.Element;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public abstract class MenuAppPage extends LayoutContainer{
    
    protected String checkId;
    
    /* (non-Javadoc)
     * @see com.extjs.gxt.ui.client.widget.LayoutContainer#onRender(com.google.gwt.user.client.Element, int)
     */
    @Override
    protected void onRender(Element parent, int index) {
        super.onRender(parent, index);
        setCheckId(defineCheckId());
        renderPage();
    }
    
    
    protected String getCheckId() {
        return checkId;
    }

    
    public void setCheckId(String checkId) {
        this.checkId = checkId;
    }

    public abstract String defineCheckId();

    public abstract void renderPage();

}
