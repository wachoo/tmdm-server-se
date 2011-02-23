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
package org.talend.mdm.webapp.itemsbrowser2.server.util;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.dwr.CommonDWR;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetBusinessConcepts;
import com.amalto.webapp.util.webservices.WSGetView;
import com.amalto.webapp.util.webservices.WSView;
import com.amalto.webapp.util.webservices.WSViewPK;

public class WSUtil {

    private static final Logger LOG = Logger.getLogger(WSUtil.class);

    private static WSView wsView;

    public static void setWSView(String viewPK) {
        try {
            wsView = getWSView(viewPK);
        } catch (RemoteException e) {
            LOG.error(e.getMessage(), e);
        } catch (XtentisWebappException e) {
            LOG.error(e.getMessage(), e);
        }
    }

    public static String getDescription() {
        return wsView.getDescription();
    }

    public static String[] getViewables() {
        return wsView.getViewableBusinessElements();
    }

    public static Map<String, String> getSearchables(String viewPK, String language) {
        try {
            Configuration config = Configuration.getInstance();
            String[] searchables = wsView.getSearchableBusinessElements();
            Map<String, String> labelSearchables = new LinkedHashMap<String, String>();
            HashMap<String, String> xpathToLabel = new HashMap<String, String>();
            if (viewPK.contains("Browse_items_")) { //$NON-NLS-1$
                String concept = CommonDWR.getConceptFromBrowseItemView(viewPK);
                xpathToLabel = CommonDWR.getFieldsByDataModel(config.getModel(), concept, language, true);
            } else {
                String[] concepts = Util.getPort()
                        .getBusinessConcepts(new WSGetBusinessConcepts(new WSDataModelPK(config.getModel()))).getStrings();
                for (int i = 0; i < concepts.length; i++) {
                    xpathToLabel.putAll(CommonDWR.getFieldsByDataModel(config.getModel(), concepts[i], language, true));
                }
            }

            for (int i = 0; i < searchables.length; i++) {
                String label = xpathToLabel.get(searchables[i]);
                if (label != null)
                    labelSearchables.put(searchables[i], label);
            }

            return labelSearchables;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private static WSView getWSView(String viewPK) throws RemoteException, XtentisWebappException {
        return Util.getPort().getView(new WSGetView(new WSViewPK(viewPK)));
    }
}
