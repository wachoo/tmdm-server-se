// ============================================================================
//
// Copyright (C) 2006-2013 Talend Inc. - www.talend.com
//
// This source code is available under agreement available at
// %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
//
// You should have received a copy of the agreement
// along with this program; if not, write to Talend SA
// 9 rue Pages 92150 Suresnes, France
//
// ============================================================================
package com.amalto.core.delegator;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

public class BeanDelegatorContainer {

    private static final Logger LOGGER = Logger.getLogger(BeanDelegatorContainer.class);

    private static final Map<String, IBeanDelegator> delegatorInstancePool = new HashMap<String, IBeanDelegator>();

    private static final String LOCAL_USER = "LocalUser"; //$NON-NLS-1$

    private static final String VALIDATION = "Validation"; //$NON-NLS-1$

    private static final String ITEM_CTRL = "ItemCtrl"; //$NON-NLS-1$

    private static final String XTENTIS_WS = "XtentisWS"; //$NON-NLS-1$

    private static BeanDelegatorContainer instance;

    private BeanDelegatorContainer() {
        init();
    }

    /**
     * Get the unique instance of this class. In order to improve the performance, removed synchronized, using pseudo
     * singleton mode
     */
    public static synchronized BeanDelegatorContainer getInstance() {
        if (instance == null) {
            instance = new BeanDelegatorContainer();
        }
        return instance;
    }

    private void init() {
        synchronized (delegatorInstancePool) {
            Map<String, String> beanImplNamesMap = BeanDelegatorConfigReader.readConfiguration();
            for (Map.Entry<String, String> currentBean : beanImplNamesMap.entrySet()) {
                try {
                    Class<?> clazz = Class.forName(currentBean.getValue());
                    Constructor<?> cons = clazz.getConstructor();
                    IBeanDelegator beanDelegator = (IBeanDelegator) cons.newInstance();
                    delegatorInstancePool.put(currentBean.getKey(), beanDelegator);
                    LOGGER.info("Init instance:" + currentBean.getValue()); //$NON-NLS-1$
                } catch (Exception e) {
                    LOGGER.error("Init exception for '" + currentBean.getValue() + "'.", e); //$NON-NLS-1$ //$NON-NLS-2$
                }
            }
        }
    }

    public ILocalUser getLocalUserDelegator() {
        synchronized (delegatorInstancePool) {
            return (ILocalUser) delegatorInstancePool.get(LOCAL_USER);
        }
    }

    public IValidation getValidationDelegator() {
        synchronized (delegatorInstancePool) {
            return (IValidation) delegatorInstancePool.get(VALIDATION);
        }
    }

    public IItemCtrlDelegator getItemCtrlDelegator() {
        synchronized (delegatorInstancePool) {
            return (IItemCtrlDelegator) delegatorInstancePool.get(ITEM_CTRL);
        }
    }

    public IXtentisWSDelegator getXtentisWSDelegator() {
        synchronized (delegatorInstancePool) {
            return (IXtentisWSDelegator) delegatorInstancePool.get(XTENTIS_WS);
        }
    }
}
