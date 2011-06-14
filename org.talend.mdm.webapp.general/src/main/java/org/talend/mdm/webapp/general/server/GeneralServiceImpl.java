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
package org.talend.mdm.webapp.general.server;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.general.client.GeneralService;
import org.talend.mdm.webapp.general.model.ComboBoxModel;
import org.talend.mdm.webapp.general.model.MenuBean;

import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSDataCluster;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataCluster;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSRegexDataClusterPKs;
import com.amalto.webapp.util.webservices.WSRegexDataModelPKs;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GeneralServiceImpl extends RemoteServiceServlet implements GeneralService {

    private static final Logger LOG = Logger.getLogger(GeneralServiceImpl.class);
    
    @Override
    public List<MenuBean> getMenus(String language) throws Exception {
        List<MenuBean> menus = new ArrayList<MenuBean>();
        try {
            getSubMenus(Menu.getRootMenu(), language, menus, 1, 1);
        } catch (XtentisWebappException e) {
            e.printStackTrace();
        }
        return menus;
    }

    private int getSubMenus(Menu menu, String language, List<MenuBean> rows, int level, int i) {
        for (Iterator<String> iter = menu.getSubMenus().keySet().iterator(); iter.hasNext();) {
            String key = iter.next();
            Menu subMenu = menu.getSubMenus().get(key);

            MenuBean item = new MenuBean();
            item.setId(i);
            item.setLevel(level);
            item.setContext(subMenu.getContext());
            item.setIcon(subMenu.getIcon());
            item.setName(subMenu.getLabels().get(language));
            item.setApplication(subMenu.getApplication() == null ? "" : subMenu.getApplication());
            rows.add(item);
            i++;
            if (subMenu.getSubMenus().size() > 0)
                i = getSubMenus(subMenu, language, rows, level + 1, i);
        }
        return i;

    }
    
    public List<ComboBoxModel> getClusters(){
        try {
            List<ComboBoxModel> clusters = new ArrayList<ComboBoxModel>();
            
            WSDataClusterPK[] wsDataClustersPKs = Util.getPort().getDataClusterPKs(
                    new WSRegexDataClusterPKs("*") //$NON-NLS-1$
                    ).getWsDataClusterPKs();
            
            //CommonDWR.filterSystemClustersPK(wsDataClustersPKs, map);
            
            Map<String, XSystemObjects> xDataClustersMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
            for (int i = 0; i < wsDataClustersPKs.length; i++) {
                if(!XSystemObjects.isXSystemObject(xDataClustersMap,XObjectType.DATA_CLUSTER, wsDataClustersPKs[i].getPk())){
                    WSDataCluster wsGetDataCluster=Util.getPort().getDataCluster(new WSGetDataCluster(wsDataClustersPKs[i]));
                    clusters.add(new ComboBoxModel(wsDataClustersPKs[i].getPk(),wsGetDataCluster.getDescription()==null?"":wsGetDataCluster.getDescription())); //$NON-NLS-1$
                }
            }
            
            return  clusters;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<ComboBoxModel> getModels(){
        try {
            List<ComboBoxModel> models = new ArrayList<ComboBoxModel>();
            WSDataModelPK[] wsDataModelsPKs = Util.getPort().getDataModelPKs(
                    new WSRegexDataModelPKs("*") //$NON-NLS-1$
                    ).getWsDataModelPKs();
            
            //CommonDWR.filterSystemDataModelsPK(wsDataModelsPK, map);
            
            Map<String, XSystemObjects> xDataModelsMap=XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
            for (int i = 0; i < wsDataModelsPKs.length; i++) {
                if(!XSystemObjects.isXSystemObject(xDataModelsMap,XObjectType.DATA_MODEL, wsDataModelsPKs[i].getPk())){
                    WSDataModel wsDataModel=Util.getPort().getDataModel(new WSGetDataModel(wsDataModelsPKs[i]));
                    models.add(new ComboBoxModel(wsDataModelsPKs[i].getPk(), wsDataModel.getDescription()==null?"":wsDataModel.getDescription()));//$NON-NLS-1$
                }
            }
            
            return  models;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }
    
    @Override
    public String getMsg() {
        List<ComboBoxModel> clusters = getClusters();
        List<ComboBoxModel> models = getModels();
        return "server message";
    }
}
