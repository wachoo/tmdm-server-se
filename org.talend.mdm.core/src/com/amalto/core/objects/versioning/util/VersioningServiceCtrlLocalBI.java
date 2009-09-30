package com.amalto.core.objects.versioning.util;

import com.amalto.core.util.ServiceCtrlLocalBI;
import com.amalto.core.util.XtentisException;


public interface VersioningServiceCtrlLocalBI   extends ServiceCtrlLocalBI
{
	public static final long serialVersionUID = 1L;
	
	/**
	 * Returns the History of an item or object The path is constitued of the clustername/instancename
	 * @throws XtentisException
	 */
   public HistoryInfos getHistory( java.lang.String path ) throws com.amalto.core.util.XtentisException;
	
   /**
    * Returns the Versioning History of an item or object The path is constitued of the clustername/instancename
    * @throws XtentisException
    */
   public HistoryInfos getVersions( java.lang.String path ) throws com.amalto.core.util.XtentisException;

   /**
    * Checkouts The path is constitued of the clustername/instancename If tag is null, the checkout will be made from the head
    * @return the checked out xml
    * @throws XtentisException
    */
   public java.lang.String[] checkOut( java.lang.String path,java.lang.String tag,java.lang.String revision ) throws com.amalto.core.util.XtentisException;

   /**
    * Commits to the head of the repository The path is constitued of the clustername/instancename
    * @throws XtentisException
    */
   public void commit( java.lang.String path,java.lang.String xml,java.lang.String comment,java.lang.String username ) throws com.amalto.core.util.XtentisException;

   /**
    * Branch the head to a particular tag The path is constitued of the clustername/instancename If the path is a clustername only, all instances will be branched
    * @throws XtentisException
    */
   public void branch( java.lang.String path,java.lang.String tag,java.lang.String comment,java.lang.String username ) throws com.amalto.core.util.XtentisException;

   /**
    * Clean the head by keeping on the listed instances of the particular cluster
    * @throws XtentisException
    */
   public void clean( java.lang.String clustername,java.lang.String[] instancenames ) throws com.amalto.core.util.XtentisException;

   /**
    * Sets the default/current versioning system configuration
    * @throws XtentisException
    */
   public void setCurrentVersioningSystemConfiguration( java.lang.String name,java.lang.String url,java.lang.String username,java.lang.String password ) throws com.amalto.core.util.XtentisException;

   /**
    * Get instances name for a particular cluster and a particular tag
    * If tag null, return the instance names of the head
    * @throws XtentisException
    */
   public abstract String[] getInstances(String clustername, String tag) throws XtentisException;

   /**
    * Returns the XML schema for the configuration<br>
    * Can be null
    *  
    */
   public String getConfigurationSchema() throws XtentisException;


   /**
    * checkup the svn configuration
    * @throws XtentisException
    *
    */
    public boolean checkConfigure(String conf)throws com.amalto.core.util.XtentisException;

    /**
     * @author achen
     * @throws XtentisException
     */
    public  String getDocumentation(String twoLettersLanguageCode) throws XtentisException;
    	
  
    /**
     * return default the configuration<br>
     * Can be null
     */
    public String getDefaultConfiguration() throws XtentisException;
   }
