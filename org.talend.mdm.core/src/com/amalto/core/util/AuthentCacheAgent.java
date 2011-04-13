// ============================================================================
//
// Copyright (C) 2006-2011 Talend Inc. - www.talend.com
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

import java.lang.management.ManagementFactory;
import java.security.Principal;
import java.security.acl.Group;
import java.util.Iterator;
import java.util.Set;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.security.auth.Subject;

import org.jboss.security.plugins.JaasSecurityManagerService;



public class AuthentCacheAgent {
	 private MBeanServer mbs = null;
	 
	 public AuthentCacheAgent(){
		  // Get the platform MBeanServer
	      mbs = ManagementFactory.getPlatformMBeanServer();
	 }
	 
	 public void flushAuthentCache(){

	      ObjectName name = null;

	      try {
	         // Uniquely identify the MBeans and register them with the MBeanServer 
	         name = new ObjectName("jboss.security:service=JaasSecurityManager");//$NON-NLS-1$
	         
	         try{
	        	 MBeanInfo info = mbs.getMBeanInfo( name );
	        	 System.out.println( "Conversion Class: " + info.getClassName() );
	         }catch(InstanceNotFoundException e2){	        	 
	        	 mbs.registerMBean(new JaasSecurityManagerService(), name);
	         }
	         
	         //Invoke convertTo op
	         String opName = "flushAuthenticationCache";//$NON-NLS-1$
	         String sig[] = { "java.lang.String","java.security.Principal" };//$NON-NLS-1$
	         
	         Object user=null;
	         Subject subject = LocalUser.getCurrentSubject();
	            Set<Principal> set = subject.getPrincipals();
	            for (Iterator<Principal> iter = set.iterator(); iter.hasNext();) {
	                Principal principal = iter.next();
	                if (principal instanceof Group) {
	                    Group group = (Group) principal;
	                    if ("Username".equals(group.getName())) {//$NON-NLS-1$
	                    	user=group;
	                    	break;
	                    }
	                 }
	         }
	         Object opArgs[] = { "java:/jaas/xtentisSecurity",user};   //$NON-NLS-1$
	         //The following code throws ReflectionException
	         Object result = mbs.invoke( name, opName, opArgs, sig );		        
	      } catch(Exception e) {
	    	  org.apache.log4j.Logger.getLogger(AuthentCacheAgent.class).error(e);
	      }		 
	 }
}
