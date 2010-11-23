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

import com.extjs.gxt.ui.client.widget.Html;

/**
 * DOC HSHU class global comment. Detailled comment
 */
public class DefaultMenuAppPage extends MenuAppPage {

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.gxt.framework.client.action.MenuAppPage#defineCheckId()
     */
    @Override
    public String defineCheckId() {
        // TODO Auto-generated method stub
        return "default.Default";
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.talend.mdm.webapp.gxt.framework.client.action.MenuAppPage#renderPage()
     */
    @Override
    public void renderPage() {
        
        Html h = new Html(
                  "<div class=text style='padding:5px'>"
                + "<center><FONT SIZE=\"3px\" COLOR=\"red\"><B>Target menu application is not available!</B> </FONT></center>"
                + "</div>");

        add(h);

    }

}
