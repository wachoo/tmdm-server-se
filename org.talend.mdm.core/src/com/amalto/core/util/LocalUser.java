package com.amalto.core.util;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.objects.universe.ejb.UniversePOJO;

import java.util.HashSet;

public class LocalUser {
    /*
     * A very special user that is triggered by scheduled, timeout or startup processes
     */
    public final static String UNAUTHENTICATED_USER = "anonymous"; //$NON-NLS-1$

    private static ILocalUser findLocalUser() {
        return BeanDelegatorContainer.getInstance().getLocalUserDelegator();
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
     * Fetch the current user and its roles -  check XtentisLoginModule
     *
     * @return The Local User
     */
    public static ILocalUser getLocalUser() throws XtentisException {
        return findLocalUser().getILocalUser();
    }

    public static void resetLocalUsers() throws XtentisException {
        findLocalUser().resetILocalUsers();
    }

    /**
     * Logs out the user by removing it from the cache an invalidating the session
     *
     * @throws XtentisException
     */
    public void logout() throws XtentisException {
        findLocalUser().logout();
    }

    /**
     * Check is the user is Admin wrt the Object Type
     */
    public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException {
        return findLocalUser().isAdmin(objectTypeClass);
    }

    /**
     * Checks if the user can read the instance of the object specified
     * @return true is the user can read
     */
    public boolean userCanRead(Class<?> objectTypeClass, String instanceId) throws XtentisException {
        return findLocalUser().userCanRead(objectTypeClass, instanceId);
    }

    public ILocalUser getILocalUser() throws XtentisException {
        return getLocalUser().getILocalUser();
    }
}
