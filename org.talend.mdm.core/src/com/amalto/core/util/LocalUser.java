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
	
	static ILocalUser localUser=null;
	
	private static ILocalUser findLocalUser() {
		if(localUser==null){
			localUser=BeanDelegatorContainer.getUniqueInstance().getLocalUserDelegator();
		}
		return localUser;
	}
	
	public HashSet<String> getRoles() {
		return findLocalUser().getRoles();
	}
	
	public void setRoles(HashSet<String> roles) {
		findLocalUser().setRoles(roles);
	}
	public String getUsername() {
		return findLocalUser().getUsername();
	}
	public void setUsername(String username) {
		findLocalUser().setUsername(username);
	}
	public UniversePOJO getUniverse() {
    	return findLocalUser().getUniverse();
    }
	public void setUniverse(UniversePOJO universe) {
    	findLocalUser().setUniverse(universe);
    }
	/**
	 * The User in XML form as stored in the DB
	 * @return
	 * 		The user in the DB XML form
	 */
	public String getUserXML() {
    	return findLocalUser().getUserXML();
    }
	public void setUserXML(String userXML) {
    	findLocalUser().setUserXML(userXML);
    }

	
	/**
     * Fetch the current user and its roles -  check XtentisLoginModule
     * @return The Local User
     */
    public static ILocalUser getLocalUser() throws XtentisException{
    	
		return findLocalUser().getILocalUser();			

    }
    public static Subject getCurrentSubject() throws XtentisException {
		return findLocalUser().getICurrentSubject();
	}
    
    public static void resetLocalUsers() throws XtentisException{
    	findLocalUser().resetILocalUsers();
	}
    

    /**
     * Logs out the user by removing it from the cache an invalidating the session
     * @throws XtentisException
     */
    public void logout() throws XtentisException{
    	findLocalUser().logout();    	
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
    	return findLocalUser().isAdmin(objectTypeClass);
    }
    /**
     * 
     * @param item
     * @return
     * @throws XtentisException
     */
    public boolean userItemCanWrite(ItemPOJO item,String datacluster, String concept)throws XtentisException{
		return findLocalUser().userItemCanWrite(item, datacluster, concept);
    }
    
     /**
     * 
     * @param item
     * @return
     * @throws XtentisException
     */
    public boolean userItemCanRead(ItemPOJO item)throws XtentisException{
    	return findLocalUser().userItemCanRead(item);
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
    	return findLocalUser().userCanWrite(objectTypeClass, instanceId);
    }


    /**
     * Checks if the user can read the instance of the object specified
     * @param objectTypeClass
     * @param instanceId
     * @return true is the user can read
     * @throws XtentisException
     */
    public boolean userCanRead(Class<?> objectTypeClass, String instanceId) throws XtentisException{
    	return findLocalUser().userCanRead(objectTypeClass, instanceId);
    	
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
