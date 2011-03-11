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
package org.talend.mdm.webapp.itemsbrowser2.client.util;

import org.talend.mdm.webapp.itemsbrowser2.client.boundary.GetService;
import org.talend.mdm.webapp.itemsbrowser2.client.mockup.ClientFakeData;
import org.talend.mdm.webapp.itemsbrowser2.shared.AppHeader;


/**
 * DOC HSHU  class global comment. Detailled comment
 */
public class Locale {
    
    
    /**
     * DOC HSHU Comment method "getUsingLanguage".
     */
    public static String getLanguage(AppHeader appHeader) {
        
        if(appHeader.isStandAloneMode()){
            return ClientFakeData.DEFAULT_LANGUAUE;
        }else {
            return GetService.getLanguage();
        }

    }

}
