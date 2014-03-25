// ============================================================================
//
// Copyright (C) 2006-2014 Talend Inc. - www.talend.com
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

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

import org.apache.log4j.Logger;

public class LoginModuleDelegator implements LoginModule {

    public static final String PROPERTY_DELEGATE_MODULE = "delegateModule"; //$NON-NLS-1$

    private static final Logger LOG = Logger.getLogger(LoginModuleDelegator.class);

    private static ClassLoader delegateClassLoader;

    private LoginModule delegateLoginModule = null;

    public static void setDelegateClassLoader(ClassLoader classLoader) {
        LOG.info("Initializing delegate LoginModule classloader.."); //$NON-NLS-1$
        delegateClassLoader = classLoader;
    }

    @Override
    public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
        if (delegateClassLoader == null) {
            throw new IllegalStateException("LoginModule not properly initialized."); //$NON-NLS-1$
        }

        Map<String, ?> newOptions = new HashMap<String, Object>(options);
        String delegateModule = (String) newOptions.remove(PROPERTY_DELEGATE_MODULE);
        if (delegateModule == null) {
            throw new IllegalStateException("Option " + PROPERTY_DELEGATE_MODULE + " is missing"); //$NON-NLS-1$ //$NON-NLS-2$
        }
        try {
            delegateLoginModule = (LoginModule) delegateClassLoader.loadClass(delegateModule).newInstance();
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create delegate LoginModule", e); //$NON-NLS-1$
        }
        delegateLoginModule.initialize(subject, callbackHandler, sharedState, newOptions);
    }

    @Override
    public boolean login() throws LoginException {
        return delegateLoginModule.login();
    }

    @Override
    public boolean commit() throws LoginException {
        return delegateLoginModule.commit();
    }

    @Override
    public boolean abort() throws LoginException {
        return delegateLoginModule.abort();
    }

    @Override
    public boolean logout() throws LoginException {
        return delegateLoginModule.logout();
    }
}