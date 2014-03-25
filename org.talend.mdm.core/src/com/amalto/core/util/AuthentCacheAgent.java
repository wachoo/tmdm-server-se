/*
 * Copyright (C) 2006-2014 Talend Inc. - www.talend.com
 *
 * This source code is available under agreement available at
 * %InstallDIR%\features\org.talend.rcp.branding.%PRODUCTNAME%\%PRODUCTNAME%license.txt
 *
 * You should have received a copy of the agreement
 * along with this program; if not, write to Talend SA
 * 9 rue Pages 92150 Suresnes, France
 */
package com.amalto.core.util;

import org.apache.log4j.Logger;
import org.jboss.security.SimplePrincipal;
import org.jboss.security.plugins.JaasSecurityManagerService;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import java.lang.management.ManagementFactory;

public class AuthentCacheAgent {

    private static final Logger LOGGER = Logger.getLogger(AuthentCacheAgent.class);

    private MBeanServer mbs = null;
	 
	 public AuthentCacheAgent(){
		  // Get the platform MBeanServer
	      mbs = ManagementFactory.getPlatformMBeanServer();
	 }
	 
	 public void flushAuthentCache(String username){
         ObjectName name;
         try {
             // Uniquely identify the MBeans and register them with the MBeanServer
             name = new ObjectName("jboss.security:service=JaasSecurityManager");//$NON-NLS-1$
             try {
                 MBeanInfo info = mbs.getMBeanInfo(name);
                 if (LOGGER.isDebugEnabled()) {
                     LOGGER.debug("Conversion Class: " + info.getClassName());
                 }
             } catch (InstanceNotFoundException e2) {
                 mbs.registerMBean(new JaasSecurityManagerService(), name);
             }

             //Invoke convertTo op
             String opName = "flushAuthenticationCache"; //$NON-NLS-1$
             String sig[] = {"java.lang.String", "java.security.Principal"}; //$NON-NLS-1$

             Object opArgs[] = {"java:/jaas/xtentisSecurity", new SimplePrincipal(username)}; //$NON-NLS-1$
             //The following code throws ReflectionException
             if (username == null || username.trim().length() == 0) {
                 mbs.invoke(name, opName, new String[]{"java:/jaas/xtentisSecurity"}, new String[]{"java.lang.String"}); //$NON-NLS-1$  //$NON-NLS-2$
             } else {
                 mbs.invoke(name, opName, opArgs, sig);
             }
         } catch (Exception e) {
             LOGGER.error(e);
         }
     }
}
