/*
 * Generated by XDoclet - Do not edit!
 */
package com.amalto.core.plugin.base.groupedlinesreader.ejb.local;

/**
 * Local home interface for GroupedLinesReaderTransformerPlugin.
 * @xdoclet-generated at 16-07-09
 * @copyright The XDoclet Team
 * @author XDoclet
 * @version ${version}
 */
public interface GroupedLinesReaderTransformerPluginLocalHome
   extends javax.ejb.EJBLocalHome
{
   public static final String COMP_NAME="java:comp/env/ejb/GroupedLinesReaderTransformerPluginLocal";
   public static final String JNDI_NAME="amalto/local/transformer/plugin/groupedlinesreader";

   public com.amalto.core.plugin.base.groupedlinesreader.ejb.local.GroupedLinesReaderTransformerPluginLocal create()
      throws javax.ejb.CreateException;

}
