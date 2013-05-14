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
package org.talend.mdm.webapp.itemsbrowser2.server.bizhelpers;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.talend.mdm.webapp.base.server.util.DynamicLabelUtil;
import org.talend.mdm.webapp.base.shared.EntityModel;
import org.talend.mdm.webapp.base.shared.TypeModel;

import com.amalto.webapp.util.webservices.WSView;

/**
 * DOC HSHU class global comment. Detailled comment TODO In the further, we can migrate helper classes to spring beans
 */
public class ViewHelper {
    
    private static final Logger logger = Logger.getLogger(ViewHelper.class);

    public static final String DEFAULT_VIEW_PREFIX = "Browse_items";//$NON-NLS-1$

    /**
     * DOC HSHU Comment method "getConceptFromDefaultViewName".
     */
    public static String getConceptFromDefaultViewName(String viewName) {

        String concept = viewName.replaceAll(ViewHelper.DEFAULT_VIEW_PREFIX + "_", "").replaceAll("#.*", "");//$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$ //$NON-NLS-4$
        return concept;

    }

    /**
     * DOC HSHU Comment method "getViewLabel".
     * 
     * @param language
     * @param wsview
     * @return
     */
    public static String getViewLabel(String language, WSView wsview) {

        Pattern p = Pattern.compile(".*\\[" + language.toUpperCase() + ":(.*?)\\].*", Pattern.DOTALL);//$NON-NLS-1$ //$NON-NLS-2$ 
        String viewDesc = p.matcher(!wsview.getDescription().equals("") ? wsview.getDescription() : wsview.getName()).replaceAll("$1");//$NON-NLS-1$ //$NON-NLS-2$ 
        viewDesc = viewDesc.equals("") ? wsview.getName() : viewDesc; //$NON-NLS-1$ 
        return viewDesc;

    }

    /**
     * DOC HSHU Comment method "getViewables".
     * @param wsView
     * @return
     */
    public static String[] getViewables(WSView wsView) {
        return wsView.getViewableBusinessElements();
    }

    
    /**
     * DOC HSHU Comment method "getSearchables".
     * @param wsView
     * @param dataModel
     * @param language
     * @return
     */
    public static Map<String, String> getSearchables(WSView wsView, String dataModel, String language, EntityModel entityModel) {
        try {
            String[] searchables = wsView.getSearchableBusinessElements();
            Map<String, String> labelSearchables = new LinkedHashMap<String, String>();
            
            if (wsView.getName().contains(DEFAULT_VIEW_PREFIX+"_")) { //$NON-NLS-1$
                Map<String, TypeModel> labelMapSrc = entityModel.getMetaDataTypes();
                for (int i = 0; i < searchables.length; i++) {
                    String searchableLabel = labelMapSrc.get(searchables[i])==null?searchables[i]:labelMapSrc.get(searchables[i]).getLabel(language);
                    if(searchableLabel==null) {
                        searchableLabel=labelMapSrc.get(searchables[i]).getName();
                    }else {
                        if(DynamicLabelUtil.isDynamicLabel(searchableLabel)) 
                            searchableLabel=labelMapSrc.get(searchables[i]).getName();
                    }
                    labelSearchables.put(searchables[i], searchableLabel);
                }
            }
            
            return labelSearchables;
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            return null;
        }
    }

}
