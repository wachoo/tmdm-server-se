package org.talend.mdm.webapp.welcome.dwr;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.core.util.dwr.ExtJSFormResponse;
import com.amalto.webapp.core.util.dwr.ExtJSFormSuccessResponse;
import com.amalto.webapp.core.util.dwr.WebappInfo;
import com.amalto.webapp.util.webservices.WSExecuteTransformerV2;
import com.amalto.webapp.util.webservices.WSGetTransformerPKs;
import com.amalto.webapp.util.webservices.WSTransformerContext;
import com.amalto.webapp.util.webservices.WSTransformerPK;
import com.amalto.webapp.util.webservices.WSTransformerV2PK;

public class WelcomeDWR {

    private static final Logger LOG = Logger.getLogger(Webapp.class);

    private static final String standAlongName = "Runnable#";

    public WelcomeDWR() {
        super();
    }

    /**
     * get license informations.
     * 
     * @return
     */
    public ExtJSFormResponse getLicenseMsg(String language) {
        WebappInfo webappInfo = new WebappInfo();
        Webapp.INSTANCE.getInfo(webappInfo, language);
        return new ExtJSFormSuccessResponse("", webappInfo);
    }

    /**
     * get label of link on welcome page.
     * 
     * @param language
     * @return
     */
    public Map<String, String> getLabels(String language) {
        Map<String, String> labels = new HashMap<String, String>();

        try {
            TreeMap<String, Menu> subMenus = Menu.getRootMenu().getSubMenus();
            for (Iterator<String> iter = subMenus.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                Menu subMenu = subMenus.get(key);

                labels.put(subMenu.getApplication(), subMenu.getLabels().get(language));
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        return labels;
    }

    /**
     * get workflow task informations.
     * 
     * @return
     */
    public int getTaskMsg() {
        return Webapp.INSTANCE.getTaskMsg();
    }

    /**
     * check if is show license link.
     * 
     * @return
     */
    public boolean isHiddenLicense() {
        return isHiddenMenu("License");
    }

    /**
     * check if is show specify menu.
     * 
     * @param menu
     * @return
     */
    private boolean isHiddenMenu(String menu) {
        try {
            TreeMap<String, Menu> subMenus = Menu.getRootMenu().getSubMenus();
            for (Iterator<String> iter = subMenus.keySet().iterator(); iter.hasNext();) {
                String key = iter.next();
                Menu subMenu = subMenus.get(key);

                if (menu.equals(subMenu.getApplication())) {
                    return false;
                }
            }
        } catch (Exception e) {
            LOG.error(e);
        }

        return true;
    }

    /**
     * check if is show task link.
     * 
     * @return
     */
    public boolean isHiddenTask() {
        return isHiddenMenu("WorkflowTasks");
    }

    /**
     * get all standalong processes.
     */
    public List getStandalongProcess() {
        ListRange listRange = new ListRange();
        List<String> process = new ArrayList<String>();

        try {
            WSTransformerPK[] wst = Util.getPort().getTransformerPKs(new WSGetTransformerPKs("*")).getWsTransformerPK();

            for (WSTransformerPK wstransformerpk : wst) {
                if (isStandAlongProcess(wstransformerpk.getPk())) {
                    process.add(wstransformerpk.getPk());
                }
            }

            listRange.setData(process.toArray());
            listRange.setTotalSize(process.size());
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (XtentisWebappException e) {
            e.printStackTrace();
        }

        return process;
    }

    /**
     * check if is it standalong process.
     */
    public boolean isStandAlongProcess(String wstransformerpk) {
        return wstransformerpk.startsWith(standAlongName);
    }

    /**
     * run the standalong process.
     * 
     * @param transformerPK
     * @return
     */
    public boolean runProcess(String transformerPK) {
        boolean sucess = true;
        WSTransformerContext wsTransformerContext = new WSTransformerContext(new WSTransformerV2PK(transformerPK), null, null);

        try {
            WSExecuteTransformerV2 wsExecuteTransformerV2 = new WSExecuteTransformerV2(wsTransformerContext, null);
            Util.getPort().executeTransformerV2(wsExecuteTransformerV2);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (XtentisWebappException e) {
            e.printStackTrace();
        }

        return sucess;
    }
}
