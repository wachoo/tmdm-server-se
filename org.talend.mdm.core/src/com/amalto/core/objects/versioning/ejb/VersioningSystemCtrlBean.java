package com.amalto.core.objects.versioning.ejb;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.EJBLocalHome;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerHandle;
import javax.ejb.TimerService;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.talend.mdm.commmon.util.core.ICoreConstants;
import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.versioning.util.CommitItemsInfo;
import com.amalto.core.objects.versioning.util.HistoryInfos;
import com.amalto.core.objects.versioning.util.RestoreItemsInfo;
import com.amalto.core.objects.versioning.util.RestoreObjectsInfo;
import com.amalto.core.objects.versioning.util.RestoreUniverseInfo;
import com.amalto.core.objects.versioning.util.TagItemsInfo;
import com.amalto.core.objects.versioning.util.TagObjectsInfo;
import com.amalto.core.objects.versioning.util.TagStructureInfo;
import com.amalto.core.objects.versioning.util.TagUniverseInfo;
import com.amalto.core.objects.versioning.util.VersioningServiceCtrlLocalBI;
import com.amalto.core.util.JobActionInfo;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder,Starkey Shu
 * 
 * @ejb.bean 	name="VersioningSystemCtrl"
 *           	display-name="Name for VersioningSystemCtrl"
 *           	description="Description for VersioningSystemCtrl"
 *           	jndi-name="amalto/remote/core/versioningsystemctrl"
 * 		  		local-jndi-name = "amalto/local/core/versioningsystemctrl"
 *           	type="Stateless"
 *           	view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission
 * 	view-type = "remote"
 * 	role-name = "administration"
 * @ejb.permission
 * 	view-type = "local"
 * 	unchecked = "true"
 *  
 */
public class VersioningSystemCtrlBean implements SessionBean, TimedObject{
	
    public static String DEFAULT_JNDI = "amalto/local/service/svn";
    public static String DEFAULT_URL = "https://hshu:8443/svn/starkeylib";
    public static String DEFAULT_USERNAME = "admin";
    public static String DEFAULT_PASSWORD = "admin";
    
	static {

		try {
			Properties properties = MDMConfiguration.getConfiguration();
			if (properties.getProperty("versioning.service.jndi")!=null)
				DEFAULT_JNDI =  properties.getProperty("versioning.service.jndi");
			if(properties.getProperty("versioning.service.url")!=null)
				DEFAULT_URL = properties.getProperty("versioning.service.url");
			if (properties.getProperty("versioning.service.username")!=null)
				DEFAULT_USERNAME = properties.getProperty("versioning.service.username");
			if (properties.getProperty("versioning.service.password")!=null)
				DEFAULT_PASSWORD = properties.getProperty("versioning.service.password");
		} catch (Exception e) {
			//e.printStackTrace();
		}
	}
	
	public static final long serialVersionUID = 1L;
		
	private SessionContext sessionContext;
	
	
	
    /**
     * VersioningSystemCtrlBean.java
     * Constructor
     * 
     */
    public VersioningSystemCtrlBean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	sessionContext=ctx;
    	BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().setSessionContext(ctx);
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbRemove()
     */
    public void ejbRemove() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbActivate()
     */
    public void ejbActivate() throws EJBException, RemoteException {
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPassivate()
     */
    public void ejbPassivate() throws EJBException, RemoteException {
    }
    
    /**
     * Create method
     * @ejb.create-method  view-type = "local"
     */
    public void ejbCreate() throws javax.ejb.CreateException {}
    
    /**
     * Post Create method
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    
    
    
    /**
     * Creates or updates a VersioningSystem
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public VersioningSystemPOJOPK putVersioningSystem(VersioningSystemPOJO versioningSystem) throws XtentisException{  
     	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().putVersioningSystem(versioningSystem);
    }
    
     
    /**
     * Get item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public VersioningSystemPOJO getVersioningSystem(VersioningSystemPOJOPK pk) throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getVersioningSystem(pk);
    }
    
    /**
     * Get a VersioningSystem - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public VersioningSystemPOJO existsVersioningSystem(VersioningSystemPOJOPK pk)    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().existsVersioningSystem(pk);
    }
    

    /**
     * Remove an item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public VersioningSystemPOJOPK removeVersioningSystem(VersioningSystemPOJOPK pk) 
    throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().removeVersioningSystem(pk);
    }    
    
    
    /**
	 * Retrieve all VersioningSystem PKS 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public ArrayList<VersioningSystemPOJOPK> getVersioningSystemPKs(String regex) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getVersioningSystemPKs(regex);
    }
    
    
    /**
	 * Sets the default versioning system 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public VersioningServiceCtrlLocalBI setDefaultVersioningSystem(VersioningSystemPOJOPK pk) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().setDefaultVersioningSystem(pk);
    }
    
    /**
	 * Returns a string starting with OK if the service is available else returns the reason why it is not
	 * Pass null for the pk, if you want to test the availability of the default system
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public String getVersioningSystemAvailability(VersioningSystemPOJOPK pk) throws XtentisException {
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getVersioningSystemAvailability(pk);
    }
    
    /**
	 * Tag Universe
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJOPK tagUniverseAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String comment,
    		Map<String,String[]> typeInstances
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().tagUniverseAsJob(versioningSystemPOJOPK, tag, comment, typeInstances);
    }
 
    
    /**
	 * Tag Objects
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJOPK tagObjectsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String comment,
    		String type,
    		String[] instances
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().tagObjectsAsJob(versioningSystemPOJOPK, tag, comment, type, instances);
    }
  
    /**
	 * Tag Items
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public BackgroundJobPOJOPK tagItemsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String comment,
    		ItemPOJOPK[] itemPKs
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().tagItemsAsJob(versioningSystemPOJOPK, tag, comment, itemPKs);
    }
    
 
    /**
	 * Restore Objects
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public BackgroundJobPOJOPK restoreObjectsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String type,
    		String[] instances
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().restoreObjectsAsJob(versioningSystemPOJOPK, tag, type, instances);
    }
    
    /**
	 * Restore Universe
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public BackgroundJobPOJOPK restoreUniverseAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		String[] encodedClusterNames
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().restoreUniverseAsJob(versioningSystemPOJOPK, tag, encodedClusterNames);
    }
    

    /**
	 * Restore Items
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public BackgroundJobPOJOPK restoreItemsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String tag,
    		ItemPOJOPK[] itemPKs
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().restoreItemsAsJob(versioningSystemPOJOPK, tag, itemPKs);
    }
    
 
    /**
	 * Commit Item
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ItemPOJOPK commitItem(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPK,
    		String comment
    )throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().commitItem(versioningSystemPOJOPK, itemPK, comment);
    }
    
    /**
	 * Commit Items
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public BackgroundJobPOJOPK commitItemsAsJob(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK[] itemPKs,
    		String comment
    )throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().commitItemsAsJob(versioningSystemPOJOPK, itemPKs, comment);
    }
    
    
    /**
	 * Restore Single Item From Revision
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void restoreItemByRevision(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPK,
    		String revision
    )throws XtentisException{
    	 BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().restoreItemByRevision(versioningSystemPOJOPK, itemPK, revision);
    }

	private void resetItemsCache() {
		//FIXME:cost expensive
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("Cleanning item(s) cache...");
		//ItemPOJO.clearCache();
	}
	
	private void resetObjectsCache() {
		//FIXME:cost expensive
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("Cleanning object(s) cache...");
		//ObjectPOJO.clearCache();
	}
    
    /**
	 * Get Objects Versions
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public HistoryInfos getObjectsVersions(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		String type,
    		String instances[]
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getObjectsVersions(versioningSystemPOJOPK, type, instances);
	}
    
    /**
	 * Get Universe Versions
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TagStructureInfo[] getUniverseVersions(
    		VersioningSystemPOJOPK versioningSystemPOJOPK
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getUniverseVersions(versioningSystemPOJOPK);
	}
    
    /**
	 * Get Items Versions
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */ 
    public HistoryInfos getItemsVersions(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK[] itemPOJOPKs
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getItemsVersions(versioningSystemPOJOPK, itemPOJOPKs);
	}
    
    /**
	 * Get Item History
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */ 
    public HistoryInfos getItemHistory(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPOJOPK
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getItemHistory(versioningSystemPOJOPK, itemPOJOPK);
	}
    
    /**
	 * Get Item Content
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */ 
    public String getItemContent(
    		VersioningSystemPOJOPK versioningSystemPOJOPK,
    		ItemPOJOPK itemPK,
    		String revision
		)throws XtentisException{
    	return BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().getItemContent(versioningSystemPOJOPK, itemPK, revision);
	}
    

    
 
    /**
     * Run execute as a background job
     */
	public void ejbTimeout(Timer timer) {
		BeanDelegatorContainer.getUniqueInstance().getVersioningSystemCtrlBeanDelegator().ejbTimeout(timer);
	}
}