/*
 * Generated by XDoclet - Do not edit!
 */
package com.amalto.core.plugin.base.xslt.ejb.local;

/**
 * Local home interface for XSLTTransformerPlugin.
 * @xdoclet-generated at 16-07-09
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */
public interface XSLTTransformerPluginLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/XSLTTransformerPluginLocal";
   public static final String JNDI_NAME="amalto/local/transformer/plugin/xslt";

   public com.amalto.core.plugin.base.xslt.ejb.local.XSLTTransformerPluginLocal create()
      throws javax.ejb.CreateException;

}
