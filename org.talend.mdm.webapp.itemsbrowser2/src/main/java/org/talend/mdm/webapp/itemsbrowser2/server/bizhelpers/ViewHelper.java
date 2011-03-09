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
package org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers;

import java.util.regex.Pattern;

import com.amalto.webapp.util.webservices.WSView;



/**
 * DOC HSHU  class global comment. Detailled comment
 * TODO In the further, we can migrate helper classes to spring beans
 */
public class ViewHelper {
    
    public static final String DEFAULT_VIEW_PREFIX = "Browse_items";
    
    
    /**
     * DOC HSHU Comment method "getConceptFromDefaultViewName".
     */
    public static String getConceptFromDefaultViewName(String viewName) {
        
        String concept = viewName.replaceAll(ViewHelper.DEFAULT_VIEW_PREFIX + "_", "").replaceAll("#.*", "");
        return concept;

    }
    
    /**
     * DOC HSHU Comment method "getViewLabel".
     * @param language
     * @param wsview
     * @return
     */
    public static String getViewLabel(String language, WSView wsview) {
        
        Pattern p = Pattern.compile(".*\\[" + language.toUpperCase() + ":(.*?)\\].*", Pattern.DOTALL);
        String viewDesc = p.matcher(!wsview.getDescription().equals("") ? wsview.getDescription() : wsview.getName()).replaceAll("$1");
        viewDesc = viewDesc.equals("") ? wsview.getName() : viewDesc;
        return viewDesc;
        
    }

}
