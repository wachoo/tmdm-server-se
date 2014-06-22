package com.amalto.core.util;

import java.security.Principal;
import java.security.acl.Group;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import javax.security.auth.Subject;
import javax.security.jacc.PolicyContext;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.delegator.ILocalUser;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJO;

public class LocalUser {

    /*
     * A very special user that is triggered by scheduled, timeout or startup processes
     */
    public final static String UNAUTHENTICATED_USER = "anonymous";

    static ILocalUser localUser = null;

    private static ILocalUser findLocalUser() {
        if (localUser == null) {
            localUser = BeanDelegatorContainer.getInstance().getLocalUserDelegator();
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
     *
     * @return The user in the DB XML form
     */
    public String getUserXML() {
        return findLocalUser().getUserXML();
    }

    public void setUserXML(String userXML) {
        findLocalUser().setUserXML(userXML);
    }

    /**
     * Fetch the current user and its roles - check XtentisLoginModule
     *
     * @return The Local User
     */
    public static ILocalUser getLocalUser() throws XtentisException {
        return findLocalUser().getILocalUser();
    }

    public static Subject getCurrentSubject() throws XtentisException {
        return findLocalUser().getICurrentSubject();
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

    /*****************************************************************************************
     *
     * Roles Checking
     *
     * ****************************************************************************************/

    /**
     * Check is the user is Admin wrt the Object Type
     */
    public boolean isAdmin(Class<?> objectTypeClass) throws XtentisException {
        return findLocalUser().isAdmin(objectTypeClass);
    }

    public boolean userItemCanWrite(ItemPOJO item, String datacluster, String concept) throws XtentisException {
        return findLocalUser().userItemCanWrite(item, datacluster, concept);
    }

    public boolean userItemCanRead(ItemPOJO item) throws XtentisException {
        return findLocalUser().userItemCanRead(item);
    }

    /**
     * Checks if the user can change the instance of the object specified Ability to change implies ability to read
     */
    public boolean userCanWrite(Class<?> objectTypeClass, String instanceId) throws XtentisException {
        return findLocalUser().userCanWrite(objectTypeClass, instanceId);
    }

    /**
     * Checks if the user can read the instance of the object specified
     *
     * @return true is the user can read
     */
    public boolean userCanRead(Class<?> objectTypeClass, String instanceId) throws XtentisException {
        return findLocalUser().userCanRead(objectTypeClass, instanceId);

    }

    public Subject getICurrentSubject() throws XtentisException {
        // No need to implement this method.
        return null;
    }

    public ILocalUser getILocalUser() throws XtentisException {
        // No need to implement this method.
        return null;
    }

    public void resetILocalUsers() throws XtentisException {
        // No need to implement this method.
    }

    public static String getPrincipalMember(String key) throws Exception {
        String result = "";
        // Get the Authenticated Subject
        Subject subject = (Subject) PolicyContext.getContext("javax.security.auth.Subject.container");
        // Now look for a Group
        Set principals = subject.getPrincipals(Principal.class);
        for (Object principal : principals) {
            Principal p = (Principal) principal;
            if (p instanceof Group) {
                Group sg = (Group) p;
                if (key.equals(sg.getName())) {
                    Enumeration en = sg.members();
                    while (en.hasMoreElements()) {
                        String info = en.nextElement().toString();
                        result = result + "," + info;
                    }
                }
            }
        }
        if (result.length() > 0) {
            result = result.substring(1);
        }
        return result;
    }

}
