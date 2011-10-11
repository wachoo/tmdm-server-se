package org.talend.mdm.webapp.general.server.actions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.webapp.XObjectType;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.talend.mdm.webapp.base.client.exception.ServiceException;
import org.talend.mdm.webapp.general.client.GeneralService;
import org.talend.mdm.webapp.general.gwt.GWTConfigurationContext;
import org.talend.mdm.webapp.general.gwt.GwtWebContextFactory;
import org.talend.mdm.webapp.general.model.ActionBean;
import org.talend.mdm.webapp.general.model.ComboBoxModel;
import org.talend.mdm.webapp.general.model.LanguageBean;
import org.talend.mdm.webapp.general.model.MenuBean;
import org.talend.mdm.webapp.general.model.MenuGroup;
import org.talend.mdm.webapp.general.model.UserBean;
import org.talend.mdm.webapp.general.server.util.Utils;
import org.w3c.dom.Document;

import com.amalto.core.delegator.ILocalUser;
import com.amalto.webapp.core.bean.Configuration;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.util.webservices.WSDataCluster;
import com.amalto.webapp.util.webservices.WSDataClusterPK;
import com.amalto.webapp.util.webservices.WSDataModel;
import com.amalto.webapp.util.webservices.WSDataModelPK;
import com.amalto.webapp.util.webservices.WSGetDataCluster;
import com.amalto.webapp.util.webservices.WSGetDataModel;
import com.amalto.webapp.util.webservices.WSLogout;
import com.amalto.webapp.util.webservices.WSRegexDataClusterPKs;
import com.amalto.webapp.util.webservices.WSRegexDataModelPKs;

public class GeneralAction implements GeneralService {

    private static final Logger LOG = Logger.getLogger(GeneralAction.class);

    private static final GWTConfigurationContext configurationContext = new GWTConfigurationContext();

    public MenuGroup getMenus(String language) throws ServiceException {
        MenuGroup result = new MenuGroup();
        try {

            List<MenuBean> menus = new ArrayList<MenuBean>();
            Utils.getSubMenus(Menu.getRootMenu(), language, menus, 1, 1);
            result.setMenuBean(menus);
            result.setGroupItem(Utils.getGroupItems(language));
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
        return result;
    }

    private List<ComboBoxModel> getClusters() throws Exception {
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
    }

    private List<ComboBoxModel> getModels() throws Exception {
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
    }

    public ActionBean getAction() throws ServiceException {
        try {
            ActionBean action = new ActionBean();
            action.setClusters(getClusters());
            action.setModels(getModels());
            Configuration configuration = Configuration.getInstance(configurationContext);
            action.setCurrentCluster(configuration.getCluster());
            action.setCurrentModel(configuration.getModel());
            return action;
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }

    }

    public void setClusterAndModel(String cluster, String model) throws ServiceException {
        try {
            Configuration.initialize(cluster, model, configurationContext);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    public UserBean getUsernameAndUniverse() throws ServiceException {
        try {
            UserBean userBean = new UserBean();
            userBean.setEnterprise(com.amalto.core.util.Util.isEnterprise());
            if (!com.amalto.core.util.Util.isEnterprise()) {
                userBean.setName(Util.getLoginUserName());
                userBean.setUniverse("UNKNOWN"); //$NON-NLS-1$
                return userBean;
            }
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
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    public List<LanguageBean> getLanguages(String language) throws ServiceException {
        try {
            return Utils.getLanguages(language);
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }
    public void logout() throws ServiceException {
        try {
            String username = com.amalto.webapp.core.util.Util.getAjaxSubject().getUsername();
            Util.getPort().logout(new WSLogout("")).getValue(); //$NON-NLS-1$
            ILocalUser.getOnlineUsers().remove(username);
            GwtWebContextFactory.get().getSession().invalidate();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

    public boolean isExpired() throws ServiceException {
        try {
            return Webapp.INSTANCE.isExpired();
        } catch (Exception e) {
            LOG.error(e.getMessage(), e);
            throw new ServiceException(e.getLocalizedMessage());
        }
    }

}
