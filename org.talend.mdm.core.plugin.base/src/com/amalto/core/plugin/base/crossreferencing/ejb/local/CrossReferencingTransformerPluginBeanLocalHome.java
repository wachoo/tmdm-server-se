/*
 * Generated by XDoclet - Do not edit!
 */
package com.amalto.core.plugin.base.crossreferencing.ejb.local;

/**
 * Local home interface for CrossReferencingTransformerPluginBean.
 * @xdoclet-generated at 16-07-09
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */
public interface CrossReferencingTransformerPluginBeanLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/CrossReferencingTransformerPluginBeanLocal";
   public static final String JNDI_NAME="amalto/local/transformer/plugin/crossreferencing";

   public com.amalto.core.plugin.base.crossreferencing.ejb.local.CrossReferencingTransformerPluginBeanLocal create()
      throws javax.ejb.CreateException;

}
