package org.talend.mdm.webapp.general.server.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.security.jacc.PolicyContextException;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.general.client.GeneralService;
import org.talend.mdm.webapp.general.gwt.GWTConfigurationContext;
import org.talend.mdm.webapp.general.gwt.GwtWebContextFactory;
import org.talend.mdm.webapp.general.gwt.ProxyGWTServiceImpl;
import org.talend.mdm.webapp.general.model.ActionBean;
import org.talend.mdm.webapp.general.model.ComboBoxModel;
import org.talend.mdm.webapp.general.model.ItemBean;
import org.talend.mdm.webapp.general.model.MenuBean;
import org.talend.mdm.webapp.general.model.UserBean;
import org.talend.mdm.webapp.general.server.util.Utils;
import org.w3c.dom.Document;

import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.util.webservices.WSDataCluster;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataCluster;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSRegexDataClusterPKs;
import com.amalto.webapp.util.webservices.WSRegexDataModelPKs;

public class GeneralAction implements GeneralService {

    private static final Logger LOG = Logger.getLogger(GeneralAction.class);
    
    private static final GWTConfigurationContext configurationContext = new GWTConfigurationContext();

    public List<MenuBean> getMenus(String language) throws Exception {
        List<MenuBean> menus = new ArrayList<MenuBean>();
        try {
            Utils.getSubMenus(Menu.getRootMenu(), language, menus, 1, 1);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        List<String> list = (List<String>) GwtWebContextFactory.get().getSession().getAttribute(ProxyGWTServiceImpl.SESSION_LIST_ATTRIBUTE);
        if(LOG.isDebugEnabled())
            LOG.debug(list.toString());
        return menus;
    }

    private List<ComboBoxModel> getClusters() {
        try {
            List<ComboBoxModel> clusters = new ArrayList<ComboBoxModel>();

            WSDataClusterPK[] wsDataClustersPKs = Util.getPort().getDataClusterPKs(new WSRegexDataClusterPKs("*") //$NON-NLS-1$
                    ).getWsDataClusterPKs();
            Map<String, XSystemObjects> xDataClustersMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_CLUSTER);
            for (int i = 0; i < wsDataClustersPKs.length; i++) {
                if (!XSystemObjects.isXSystemObject(xDataClustersMap, XObjectType.DATA_CLUSTER, wsDataClustersPKs[i].getPk())) {
                    WSDataCluster wsGetDataCluster = Util.getPort().getDataCluster(new WSGetDataCluster(wsDataClustersPKs[i]));
                    clusters.add(new ComboBoxModel(wsGetDataCluster.getDescription(), wsDataClustersPKs[i].getPk()));
                }
            }

            return clusters;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    private List<ComboBoxModel> getModels() {
        try {
            List<ComboBoxModel> models = new ArrayList<ComboBoxModel>();
            WSDataModelPK[] wsDataModelsPKs = Util.getPort().getDataModelPKs(new WSRegexDataModelPKs("*") //$NON-NLS-1$
                    ).getWsDataModelPKs();

            Map<String, XSystemObjects> xDataModelsMap = XSystemObjects.getXSystemObjects(XObjectType.DATA_MODEL);
            for (int i = 0; i < wsDataModelsPKs.length; i++) {
                if (!XSystemObjects.isXSystemObject(xDataModelsMap, XObjectType.DATA_MODEL, wsDataModelsPKs[i].getPk())) {
                    WSDataModel wsDataModel = Util.getPort().getDataModel(new WSGetDataModel(wsDataModelsPKs[i]));
                    models.add(new ComboBoxModel(wsDataModel.getDescription(), wsDataModelsPKs[i].getPk()));
                }
            }

            return models;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public ActionBean getAction() {
        ActionBean action = new ActionBean();
        action.setClusters(getClusters());
        action.setModels(getModels());
        try {
            Configuration configuration = Configuration.getInstance(configurationContext);
            action.setCurrentCluster(configuration.getCluster());
            action.setCurrentModel(configuration.getModel());
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
        }
        return action;
    }

    public String setClusterAndModel(String cluster, String model) {
        try {
            Configuration.initialize(cluster, model, configurationContext);
            // Used by javascript as a status code
            return "DONE"; //$NON-NLS-1$
        } catch (Exception e) {
            return e.getLocalizedMessage();
        }
    }

    public UserBean getUsernameAndUniverse() throws Exception {
        UserBean userBean = new UserBean();
        userBean.setEnterprise(com.amalto.core.util.Util.isEnterprise());
        if (!com.amalto.core.util.Util.isEnterprise()) {
            userBean.setName(Util.getLoginUserName());
            userBean.setUniverse("UNKNOWN"); //$NON-NLS-1$
            return userBean;
        }
        try {
            String givenname = null;
            String familyname = null;
            String xml = Util.getAjaxSubject().getXml();
            if (xml != null) {
                Document d = Util.parse(xml);
                givenname = Util.getFirstTextNode(d, "//givenname"); //$NON-NLS-1$
                familyname = Util.getFirstTextNode(d, "//familyname"); //$NON-NLS-1$
            }

            String universe = Util.getLoginUniverse();
            if (familyname != null && givenname != null) {
                userBean.setName(givenname + " " + familyname); //$NON-NLS-1$
            } else {
                userBean.setName(Util.getAjaxSubject().getUsername());
            }
            userBean.setUniverse(universe);
            return userBean;
        } catch (PolicyContextException e) {
            LOG.error(e.getMessage(), e);
            return null;
        }
    }

    public List<ItemBean> getLanguages() {
        return Utils.getLanguages();
    }


}
