/*
 * Generated by XDoclet - Do not edit!
 */
package com.amalto.core.objects.storedprocedure.ejb.local;

/**
 * Local home interface for StoredProcedureCtrl.
 * @xdoclet-generated
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */
public interface StoredProcedureCtrlLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/StoredProcedureCtrlLocal";
   public static final String JNDI_NAME="amalto/local/core/storedprocedurectrl";

   public com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal create()
      throws javax.ejb.CreateException;

}