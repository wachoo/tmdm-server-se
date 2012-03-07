// ============================================================================
//
// Copyright (C) 2006-2012 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core;

import java.util.HashMap;
import java.util.Properties;

import javax.ejb.EJBHome;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import junit.framework.TestCase;

/**
 * DOC Administrator  class global comment. Detailled comment
 */
public  class EJBTestUtil {
    private static String login = "administrator";

    private static String password = "administrator";

    
    private static HashMap<String, EJBHome> ejbHomes = new HashMap<String, javax.ejb.EJBHome>();


    private static Properties getContextProperties() {
        Properties p = new Properties();
        p.put("api-type", "EJB2");
        p.put(InitialContext.INITIAL_CONTEXT_FACTORY, "org.jnp.interfaces.NamingContextFactory");
        p.put(InitialContext.URL_PKG_PREFIXES, "org.jboss.naming:org.jnp.interfaces");
        p.put(InitialContext.PROVIDER_URL, "jnp://127.0.0.1:1199");
        p.put(InitialContext.SECURITY_PRINCIPAL, login);
        p.put(InitialContext.SECURITY_CREDENTIALS, password);
        return p;
    }

    public static EJBHome getHome(String jndi) throws NamingException {
        EJBHome home = null;
        if (true) {
            home = ejbHomes.get(jndi);
            if (home == null) {
                home = (EJBHome) new InitialContext(getContextProperties()).lookup(jndi);
                ejbHomes.put(jndi, home);
            }
        } else {
            home = (EJBHome) new InitialContext().lookup(jndi);
        }
        // dumpClass(localHome.getClass());
        return home;
    }
}
