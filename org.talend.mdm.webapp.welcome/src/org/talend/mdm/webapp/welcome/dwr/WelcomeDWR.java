package org.talend.mdm.webapp.welcome.dwr;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.apache.log4j.Logger;

import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Webapp;
import com.amalto.webapp.core.util.dwr.ExtJSFormResponse;
import com.amalto.webapp.core.util.dwr.ExtJSFormSuccessResponse;
import com.amalto.webapp.core.util.dwr.WebappInfo;

public class WelcomeDWR {

    private static final Logger LOG = Logger.getLogger(Webapp.class);

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
}