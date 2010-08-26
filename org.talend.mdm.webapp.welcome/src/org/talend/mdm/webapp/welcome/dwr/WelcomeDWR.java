package org.talend.mdm.webapp.welcome.dwr;

import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.ow2.bonita.facade.runtime.ActivityState;
import org.ow2.bonita.util.AccessorUtil;

import com.amalto.core.util.license.LicenseHelper;
import com.amalto.core.util.license.LicenseUtil;
import com.amalto.webapp.core.util.Menu;
import com.amalto.webapp.core.util.dwr.ExtJSFormResponse;
import com.amalto.webapp.core.util.dwr.ExtJSFormSuccessResponse;

public class WelcomeDWR {
   private static final String WORKFLOW_SERVICE_JNDINAME = "amalto/local/service/workflow";
   
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
           Object service= com.amalto.core.util.Util.retrieveComponent(null, WORKFLOW_SERVICE_JNDINAME);
           Class[] paramTypes = {String.class, ActivityState.READY.getClass()};
           String methodName = "getTaskList";
           Method getTasksListMethod = service.getClass().getDeclaredMethod(methodName, paramTypes);
           Collection tasks = (Collection)getTasksListMethod.invoke(service, 
                   new Object[] {AccessorUtil.getAPIAccessor().getManagementAPI().getLoggedUser(),
                                 ActivityState.READY
                   });

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