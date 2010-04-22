// ============================================================================
//
// Copyright (C) 2006-2010 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.util;

import java.util.ArrayList;
import java.util.List;

import org.talend.mdm.commmon.util.webapp.XSystemObjects;

import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;


/**
 * DOC mhirt class global comment. Detailled comment <br/>
 * 
 */
public final class UserHelper {
    private UserHelper() {
    }

    private static UserHelper instance;

    public static UserHelper getInstance() {
        if (instance == null) {
            instance = new UserHelper();
        }
        
        return instance;
    }

    public static void clearInstance() {
        instance = null;
    }
    
    /**
     * get viewer users.
     * @return
     */
    public int getViewerUsers() {
       List<User> viewers = new ArrayList<User>();
       
       for(User user : listUsers()) {
          if(user.getRoleNames().contains(XSystemObjects.ROLE_DEFAULT_VIEWER.getName())) {
              viewers.add(user); 
          }
       }
       
       return viewers.size();
    }
    
    /**
     * get the number of normal users.
     * @return
     */
    public int getNormalUsers() {
       List<User> normalUsers = new ArrayList<User>();
       
       for(User user : listUsers()) {
          if(!user.getRoleNames().contains(XSystemObjects.ROLE_DEFAULT_ADMIN.getName()) && 
                  !user.getRoleNames().contains(XSystemObjects.ROLE_DEFAULT_VIEWER.getName())) {
              normalUsers.add(user);
          }
       }
       
       return normalUsers.size();
    }
    
    /**
     * Get the number of admin users.
     * @return
     */
    public int getNBAdminUsers() {
       List<User> admins = new ArrayList<User>();
       
       for(User user : listUsers()) {
          if(user.getRoleNames().contains(XSystemObjects.ROLE_DEFAULT_ADMIN.getName())) {
              admins.add(user); 
          }
       }
       
       return admins.size();
    }
    
    /**
     * Get the number of active users.
     * @return
     */
    public int getActiveUsers() {
       List<User> activeUsers = new ArrayList<User>();
       List<User> allUsers = listUsers();
       
       for(User user : allUsers) {
          if(user.enabled) {
             activeUsers.add(user);
          }
       }
       
       return activeUsers.size();
    }
    /**
     * list all users.
     * @return
     */
    public List<User> listUsers() {
       String dataclusterPK = XSystemObjects.DC_PROVISIONING.getName();
       List<String> results = new ArrayList<String>();
       List<User> users = new ArrayList<User>();
       
       try {
          results = Util.getItemCtrl2Local().getItems(
             new DataClusterPOJOPK(dataclusterPK), "User", null, 0, 0, Integer.MAX_VALUE);
      
          for(String userXML : results) {
             User user = User.parse(userXML);
             users.add(user);
          }
       }
       catch(Exception e) {
           e.printStackTrace();
       }
       
       return users;
    }
    
    /**
     * Check if exist the specify user.
     * @param user
     * @return
     */
    public boolean isExistUser(User user) {
        boolean result = false;
        
        for(User existUser : listUsers()) {
            if(user.getUserName().equals(existUser.getUserName())) {
                return true;
            }
        }
        
        return result;
    }
    
    /**
     * Check if update cluster or model of specify user. 
     * @param user
     * @return
     */
    public boolean isUpdateDCDM(User user) {
        boolean result = false;
        
        for(User existUser : listUsers()) {
            if(user.getUserName().equals(existUser.getUserName())) {
                String cluster = user.getDynamic().get("cluster");
                String model = user.getDynamic().get("model");
                
                if(cluster == null && model == null) {
                    return false;
                }
                else if(cluster != null && cluster.equals(existUser.getDynamic().get("cluster")) ||
                   model != null && model.equals(existUser.getDynamic().get("model"))) {
                   return true;
                }
            }
        }
        
        return result;
    }
    
    /**
     * Check if update active attribute of specify user.
     * @param user
     * @return
     */
    public boolean isActiveUser(User user) {
        boolean result = false;
        
        if(!user.isEnabled()) {
            return false;
        }
        
        for(User existUser : listUsers()) {
            if(existUser.getUserName().equals(user.getUserName())) {
                return existUser.isEnabled() != user.isEnabled();
            }
        }
        return result;
    }
    
    /**
     * check is update role of specify user.
     * @param user
     * @return
     */
    public boolean isUpdateRole(User user) {
        boolean result = false;
        
        for(User existUser : listUsers()) {
            if(existUser.getUserName().equals(user.getUserName())) {
                return !existUser.getRoleNames().equals(user.getRoleNames());
            }
        }
        return result;
    }
}
