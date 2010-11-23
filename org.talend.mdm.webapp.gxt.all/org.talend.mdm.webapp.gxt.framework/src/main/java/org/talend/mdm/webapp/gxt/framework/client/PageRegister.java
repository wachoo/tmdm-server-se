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
package org.talend.mdm.webapp.gxt.framework.client;

import java.util.HashMap;
import java.util.Map;

import org.talend.mdm.webapp.gxt.framework.client.page.DefaultMenuAppPage;
import org.talend.mdm.webapp.gxt.framework.client.page.MenuAppPage;
import org.talend.mdm.webapp.gxt.itemsbrowser2.client.Itemsbrowser2MenuAppPage;


/**
 * DOC HSHU  class global comment. Detailled comment
 * FIXME: use reflection mechanism instead
 */
public class PageRegister {
    
    public static Map<String,MenuAppPage> registerMap=new HashMap<String,MenuAppPage>();
    
    public static MenuAppPage getDefaultPageClass(){
        return registerMap.get("default.Default");
    }
    
    static{
        registerMap.put("default.Default", new DefaultMenuAppPage());
        registerMap.put("itemsbrowser.ItemsBrowser",new Itemsbrowser2MenuAppPage());
    }
    
}
