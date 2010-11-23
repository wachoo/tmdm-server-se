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
package org.talend.mdm.webapp.gxt.common;


/**
 * DOC HSHU class global comment. Detailled comment
 */
public class JAASHelper {

    public static AjaxSubject getAjaxSubject() {
        //TODO Mock the subject
        AjaxSubject sub = new AjaxSubject();
        sub.setMDMServer("localhost");
        sub.setMDMUsername("administrator");
        sub.setMDMPassword("administrator");

        return sub;
    }

}
