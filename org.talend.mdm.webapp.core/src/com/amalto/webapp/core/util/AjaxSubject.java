package com.amalto.webapp.core.util;

import org.apache.log4j.Logger;

import java.io.Serializable;
import java.security.Principal;
import java.security.acl.Group;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Set;

import javax.security.auth.Subject;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

public class AjaxSubject implements Serializable, HttpSessionBindingListener{

    private static final Logger LOGGER = Logger.getLogger(AjaxSubject.class);

    private final ArrayList<String> roles = new ArrayList<String>();

    private String username = null;

    private String password = null;

    private String MDMServer = "localhost"; //$NON-NLS-1$

    private String MDMUsername = null;

    private String MDMPassword = null;

    private String xml = null;

    public AjaxSubject(Subject subject) {
        fetchAuthenticationInfo(subject);
    }

    private void fetchAuthenticationInfo(Subject subject) {
        LOGGER.trace("fetchAuthenticationInfo() " + subject);

        Set set = subject.getPrincipals();
        for (Object aSet : set) {
            Principal principal = (Principal) aSet;
            if (principal instanceof Group) {
                Group group = (Group) principal;
                // @see WebAppLoginModule
                if ("MDMServer".equals(group.getName())) { //$NON-NLS-1$
                    MDMServer = group.members().nextElement().getName();
                } else if ("MDMUsername".equals(group.getName())) { //$NON-NLS-1$
                    MDMUsername = group.members().nextElement().getName();
                } else if ("MDMPassword".equals(group.getName())) { //$NON-NLS-1$
                    MDMPassword = group.members().nextElement().getName();
                } else if ("Credential".equals(group.getName())) { //$NON-NLS-1$
                    password = group.members().nextElement().getName();
                } else if ("XtentisUser".equals(group.getName())) { //$NON-NLS-1$
                    xml = group.members().nextElement().getName();
                } else if ("Roles".equals(group.getName())) { //$NON-NLS-1$
                    Enumeration<? extends Principal> e = group.members();
                    while (e.hasMoreElements()) {
                        String role = e.nextElement().getName();
                        roles.add(role);
                    }
                }
            } else {
                // it is the username
                if (principal.getName() != null) // unfortunate bug of the JACC infrastructrure.....
                    username = principal.getName();
            }
        }
    }

    public String[] getMDMData() {
        if (MDMUsername == null) {
            return new String[] { MDMServer, username, password };
        } else {
            return new String[] { MDMServer, MDMUsername, MDMPassword };
        }
    }

    public void valueBound(HttpSessionBindingEvent event) {
    }

    public void valueUnbound(HttpSessionBindingEvent event) {
    }

    public String getUsername() {
        return username;
    }

    public String getXml() {
        return xml;
    }

    public ArrayList<String> getRoles() {
        return roles;
    }
}
