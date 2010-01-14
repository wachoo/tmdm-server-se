package com.amalto.core.util;

import java.util.HashSet;

import javax.security.auth.Subject;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;

public class LocalUser{
	/*
	 * A very special user that is triggered by scheduled, timeout or startup processes
	 */
	public final static String UNAUTHENTICATED_USER = "anonymous"; 
	
	static ILocalUser localUser=BeanDelegatorContainer.getUniqueInstance().getLocalUserDelegator();
	
	public HashSet<String> getRoles() {
		return localUser.getRoles();
	}
	public void setRoles(HashSet<String> roles) {
		localUser.setRoles(roles);
	}
	public String getUsername() {
		return localUser.getUsername();
	}
	public void setUsername(String username) {
		localUser.setUsername(username);
	}
	public UniversePOJO getUniverse() {
    	return localUser.getUniverse();
    }
	public void setUniverse(UniversePOJO universe) {
    	localUser.setUniverse(universe);
    }
	/**
	 * The User in XML form as stored in the DB
	 * @return
	 * 		The user in the DB XML form
	 */
	public String getUserXML() {
    	return localUser.getUserXML();
    }
	public void setUserXML(String userXML) {
    	localUser.setUserXML(userXML);
    }

	
	/**
     * Fetch the current user and its roles -  check XtentisLoginModule
     * @return The Local User
     */
    public static ILocalUser getLocalUser() throws XtentisException{
    	
		return localUser.getILocalUser();			

    }
    public static Subject getCurrentSubject() throws XtentisException {
		return localUser.getICurrentSubject();
	}
    
    public static void resetLocalUsers() throws XtentisException{
    	localUser.resetILocalUsers();
	}
    

    /**
     * Logs out the user by removing it from the cache an invalidating the session
     * @throws XtentisException
     */
    public void logout() throws XtentisException{
    	localUser.logout();    	
    }
    
    
    /*****************************************************************************************
     * 
     * Roles Checking
     * 
     * ****************************************************************************************/
    
    /**
     * Check is the user is Admin wrt the Object Type
     * @param objectTypeClass
     * @return true or false
     * @throws XtentisException
     */
    public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException{
    	return localUser.isAdmin(objectTypeClass);
    }
    /**
     * 
     * @param item
     * @return
     * @throws XtentisException
     */
    public boolean userItemCanWrite(ItemPOJO item,String datacluster, String concept)throws XtentisException{
		return localUser.userItemCanWrite(item, datacluster, concept);
    }
    
     /**
     * 
     * @param item
     * @return
     * @throws XtentisException
     */
    public boolean userItemCanRead(ItemPOJO item)throws XtentisException{
    	return localUser.userItemCanRead(item);
    }
    /**
     * Checks if the user can change the instance of the object specified
     * Ability to change implies ability to read
     * @param objectTypeClass
     * @param instanceId
     * @return true is the user can change
     * @throws XtentisException
     */
    public boolean userCanWrite(Class<?> objectTypeClass, String instanceId) throws XtentisException{
    	return localUser.userCanWrite(objectTypeClass, instanceId);
    }


    /**
     * Checks if the user can read the instance of the object specified
     * @param objectTypeClass
     * @param instanceId
     * @return true is the user can read
     * @throws XtentisException
     */
    public boolean userCanRead(Class<?> objectTypeClass, String instanceId) throws XtentisException{
    	return localUser.userCanRead(objectTypeClass, instanceId);
    	
    }
    
    //No need to implement the following methods
	public Subject getICurrentSubject() throws XtentisException {
		// TODO Auto-generated method stub
		return null;
	}
	public ILocalUser getILocalUser() throws XtentisException {
		// TODO Auto-generated method stub
		return null;
	}
	public void resetILocalUsers() throws XtentisException {
		// TODO Auto-generated method stub
		
	}
    
}
