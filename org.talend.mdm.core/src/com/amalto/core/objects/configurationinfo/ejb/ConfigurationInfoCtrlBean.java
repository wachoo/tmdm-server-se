package com.amalto.core.objects.configurationinfo.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.ejb.TimedObject;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.naming.InitialContext;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.configurationinfo.assemble.AssembleConcreteBuilder;
import com.amalto.core.objects.configurationinfo.assemble.AssembleDirector;
import com.amalto.core.objects.configurationinfo.assemble.AssembleProc;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocalHome;
import com.amalto.core.objects.configurationinfo.localutil.CoreUpgrades;
import com.amalto.core.util.XtentisException;



/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="ConfigurationInfoCtrl"
 *          display-name="Name for ConfigurationInfoCtrl"
 *          description="Description for ConfigurationInfoCtrl"
 *          jndi-name="amalto/remote/core/configurationinfoctrl"
 * 		  	local-jndi-name = "amalto/local/core/configurationinfoctrl"
 *          type="Stateless"
 *          view-type="both"
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
public class ConfigurationInfoCtrlBean implements SessionBean, TimedObject {
  
	private static final long serialVersionUID = 45678952987540320L;
    
	private SessionContext context = null;

	
    /**
     * ConfigurationInfoCtrlBean.java
     * Constructor
     * 
     */
    public ConfigurationInfoCtrlBean() {
        super();
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("ConfigurationInfoCtrlBean() ");
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("setSessionContext() ");
    	context = ctx;

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
    public void ejbCreate() throws javax.ejb.CreateException {
    }
    
    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#ejbPostCreate()
     */
    public void ejbPostCreate() throws javax.ejb.CreateException {}
    
    /*****************************************************************************************************
     * Methods
     *****************************************************************************************************/
    
    
    /**
     * Creates or updates a configurationinfo
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ConfigurationInfoPOJOPK putConfigurationInfo(ConfigurationInfoPOJO configurationInfo) throws XtentisException{       
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("putConfigurationInfo() ");
        
        try {
            ObjectPOJOPK pk = configurationInfo.store();
            if (pk == null) throw new XtentisException("Unable to create the Configuration Info. Please check the XML Server logs");
            
            return new ConfigurationInfoPOJOPK(pk);
        
        } catch (XtentisException e) {
        	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to create/update the configurationinfo "+configurationInfo.getName()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }

    }
    
 
     
    /**
     * Get configurationinfo
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ConfigurationInfoPOJO getConfigurationInfo(ConfigurationInfoPOJOPK pk)     throws XtentisException{
        org.apache.log4j.Logger.getLogger(this.getClass()).debug("getConfigurationInfo() ");
        
        try {
        	ConfigurationInfoPOJO configurationInfo =  ObjectPOJO.load(ConfigurationInfoPOJO.class,pk);
        	if (configurationInfo == null) {
        		String err= "The Configuration Info "+pk.getUniqueId()+" does not exist.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);;
        		throw new XtentisException(err);
        	}
        	return configurationInfo;
        } catch (XtentisException e) {
        	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the Configuration Info "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }
    
    
    /**
     * Get a ConfigurationInfo - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ConfigurationInfoPOJO existsConfigurationInfo(ConfigurationInfoPOJOPK pk) throws XtentisException{
    	
        try {
        	return ObjectPOJO.load(ConfigurationInfoPOJO.class,pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this Configuration Info \""+pk.getUniqueId()+"\" exists:  "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
    }
    
    /**
     * Remove a Configuration Info
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ConfigurationInfoPOJOPK removeConfigurationInfo(ConfigurationInfoPOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug("Removing "+pk.getUniqueId());
        try {
        	return new ConfigurationInfoPOJOPK(ObjectPOJO.remove(ConfigurationInfoPOJO.class,pk));
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the ConfigurationInfo "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    /**
	 * Retrieve all ConfigurationInfo PKs 
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */       
    public Collection<ConfigurationInfoPOJOPK> getConfigurationInfoPKs(String regex) throws XtentisException {
    	Collection<ObjectPOJOPK> c = ObjectPOJO.findAllPKs(ConfigurationInfoPOJO.class,regex);
    	ArrayList<ConfigurationInfoPOJOPK> l = new ArrayList<ConfigurationInfoPOJOPK>();
    	for (Iterator<ObjectPOJOPK> iter = c.iterator(); iter.hasNext(); ) {
			l.add(new ConfigurationInfoPOJOPK(iter.next()));
		}
    	return l;
    }
   
    
    
  

	/**
     * Auto Upgrades the core 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void autoUpgrade() throws XtentisException{
        org.apache.log4j.Logger.getLogger(this.getClass()).trace("autoUpgrade ");
        
        try {
        	ConfigurationInfoCtrlLocal ctrl =  ((ConfigurationInfoCtrlLocalHome)new InitialContext().lookup(ConfigurationInfoCtrlLocalHome.JNDI_NAME)).create();
        	CoreUpgrades.autoUpgrade(ctrl);
        } catch (Exception e) {
        	e.printStackTrace();
		    String err = "Unable to upgrade in the background"
		    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
		    throw new XtentisException(err);
	    }    	
    }    
    
    /**
     * Auto Upgrades the core in the background- called on startup
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void autoUpgradeInBackground() throws XtentisException{
    	autoUpgradeInBackground(null);
    } 
    
    /**
     * Auto Upgrades the core in the background- called by servlet
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void autoUpgradeInBackground(AssembleProc assembleProc) throws XtentisException{
        org.apache.log4j.Logger.getLogger(this.getClass()).info("Scheduling upgrade check in 5 seconds ");
        
        try {
	        TimerService timerService =  context.getTimerService();
	        
	        if(assembleProc==null){
	        	final AssembleConcreteBuilder concreteBuilder = new AssembleConcreteBuilder();
				final AssembleDirector director = new AssembleDirector(concreteBuilder);
				director.constructAll();
				assembleProc = concreteBuilder.getAssembleProc();
	        }
			
	        timerService.createTimer(5234,assembleProc);  
	    } catch (Exception e) {
		    String err = "Unable to upgrade in the background"
		    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
		    throw new XtentisException(err);
	    }    	
    } 
    

    /**
     * @see #autoUpgradeInBackground()
     */
	public void ejbTimeout(Timer timer) {
		org.apache.log4j.Logger.getLogger(this.getClass()).debug("ejbTimeout() AutoUpgrade autoUpgradeInBackground ");
		
		//recover assemble Proc
		AssembleProc assembleProc = (AssembleProc) timer.getInfo();

		
			XmlServerSLWrapperLocal server = null;
			try {
				server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
			} catch (Exception e) {
				String err = "Auto Configuration in the background: unable to access the XML Server wrapper";
				org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
				throw new RuntimeException(err);
			}
			
			//cancel all existing timers
			TimerService timerService =  context.getTimerService();
			Collection<Timer> timers = timerService.getTimers();
			for (Iterator<Timer> iterator = timers.iterator(); iterator.hasNext(); ) {
				Timer timer2 = iterator.next();
				org.apache.log4j.Logger.getLogger(this.getClass()).debug("ejbTimeout() Cancelling Timer "+timer2.getHandle());
				timer2.cancel();
			}
			
			if (server.isUpAndRunning()) {
				org.apache.log4j.Logger.getLogger(this.getClass()).info("--Starting configuration...");
				
				assembleProc.run();
				
				org.apache.log4j.Logger.getLogger(this.getClass()).info("--Done configuration.");
			} else {
				org.apache.log4j.Logger.getLogger(this.getClass()).info("Auto Upgrade. XML Server not ready. Retrying in 5 seconds ");
				//TimerService timerService =  context.getTimerService();
	        	timerService.createTimer(5000,assembleProc);
			}
        
	}
    
}