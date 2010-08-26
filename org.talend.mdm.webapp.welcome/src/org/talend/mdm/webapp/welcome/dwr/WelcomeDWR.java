package org.talend.mdm.webapp.welcome.dwr;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.util.AccessorUtil;

import com.amalto.core.enterpriseutil.EnterpriseUtil;
import com.amalto.core.objects.license.ejb.local.LicenseCtrlLocal;
import com.amalto.core.util.license.LicenseHelper;
import com.amalto.core.util.license.LicenseUtil;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.dwr.ExtJSFormResponse;
import com.amalto.webapp.core.util.dwr.ExtJSFormSuccessResponse;

public class WelcomeDWR {
   public WelcomeDWR() {
      super();
   }
   
   /**
    * get license informations.
    * @return
    */
   public ExtJSFormResponse getLicenseMsg(String language){
       LicenseVO license = new LicenseVO();

       try {
           license.setLicense(LicenseHelper.getInstance().getLicense().getLicense());
           license.setLicenseValid(LicenseUtil.getInstance().isLicenseDateValid());
       }
       catch(Exception e) {
           e.printStackTrace();
           license.setLicense(null);
       }
       
       return new ExtJSFormSuccessResponse("", license);   
   }
   
   /**
    * get label of link on welcome page.
    * @param language
    * @return
    */
   public Map<String,String> getLabels(String language) {
       Map<String, String> labels = new HashMap<String, String>();
       
       try {
           TreeMap<String, Menu> subMenus = Menu.getRootMenu().getSubMenus();
           for(Iterator<String> iter = subMenus.keySet().iterator();iter.hasNext();) {
               String key = iter.next();
               Menu subMenu= subMenus.get(key);
               
               labels.put(subMenu.getApplication(), subMenu.getLabels().get(language));
           }
       }
       catch(Exception e) {
           e.printStackTrace();
       }
       
       return labels;
   }
   
   /**
    * get workflow task informations.
    * @return
    */
   public int getTaskMsg() {
       try {
           Collection tasks = EnterpriseUtil.getWorkflowService().getTaskList(
               AccessorUtil.getAPIAccessor().getManagementAPI().getLoggedUser(), 
               ActivityState.READY);
           return tasks.size();
       }
       catch(Exception e) {
           e.printStackTrace();
       }
       
       return 0;
   }
   
   /**
    * check if is show license link.
    * @return
    */
   public boolean isHiddenLicense() {
       return isHiddenMenu("License");
   }
   
   /**
    * check if is show specify menu.
    * @param menu
    * @return
    */
   private boolean isHiddenMenu(String menu) {
       try {
           TreeMap<String, Menu> subMenus = Menu.getRootMenu().getSubMenus();
           for(Iterator<String> iter = subMenus.keySet().iterator();iter.hasNext();) {
               String key = iter.next();
               Menu subMenu= subMenus.get(key);
               
               if(menu.equals(subMenu.getApplication())) {
                   return false;
               }
           }
       }
       catch(Exception e) {
           e.printStackTrace();
       }
       
       return true;
   }
   
   /**
    * check if is show task link.
    * @return
    */
   public boolean isHiddenTask() {
       return isHiddenMenu("WorkflowTasks");
   }
}