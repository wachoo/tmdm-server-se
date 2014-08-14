package com.amalto.core.objects.configurationinfo.ejb;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collection;

import javax.ejb.*;

import org.apache.log4j.Logger;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.ObjectPOJOPK;
import com.amalto.core.objects.configurationinfo.assemble.AssembleConcreteBuilder;
import com.amalto.core.objects.configurationinfo.assemble.AssembleDirector;
import com.amalto.core.objects.configurationinfo.assemble.AssembleProc;
import com.amalto.core.objects.configurationinfo.localutil.CoreUpgrades;
import com.amalto.core.server.ConfigurationInfo;
import com.amalto.core.server.XmlServer;
import com.amalto.core.util.Util;
import com.amalto.core.util.XtentisException;

/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="ConfigurationInfoCtrl" display-name="Name for ConfigurationInfoCtrl"
 * description="Description for ConfigurationInfoCtrl" jndi-name="amalto/remote/core/configurationinfoctrl"
 * local-jndi-name = "amalto/local/core/configurationinfoctrl" type="Stateless" view-type="both"
 * 
 * @ejb.remote-facade
 * 
 * @ejb.permission view-type = "remote" role-name = "administration"
 * @ejb.permission view-type = "local" unchecked = "true"
 * 
 */
public class ConfigurationInfoCtrlBean implements SessionBean, TimedObject, ConfigurationInfo {

    private static final long serialVersionUID = 45678952987540320L;

    private static final Logger LOGGER = Logger.getLogger(ConfigurationInfoCtrlBean.class);

    private static final Object LOCK = new Object();

    private SessionContext context = null;

    public ConfigurationInfoCtrlBean() {
        super();
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("ConfigurationInfoCtrlBean() ");
        }
    }

    public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("setSessionContext() ");
        }
        context = ctx;
    }

    public void ejbRemove() throws EJBException, RemoteException {
    }

    public void ejbActivate() throws EJBException, RemoteException {
    }

    public void ejbPassivate() throws EJBException, RemoteException {
    }

    public void ejbCreate() throws javax.ejb.CreateException {
    }

    public void ejbPostCreate() throws javax.ejb.CreateException {
    }

    /*****************************************************************************************************
     * Methods
     *****************************************************************************************************/
    /**
     * Creates or updates a configurationinfo
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ConfigurationInfoPOJOPK putConfigurationInfo(ConfigurationInfoPOJO configurationInfo) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("putConfigurationInfo() ");
        }
        try {
            ObjectPOJOPK pk = configurationInfo.store();
            if (pk == null) {
                throw new XtentisException("Unable to create the Configuration Info. Please check the XML Server logs");
            }
            return new ConfigurationInfoPOJOPK(pk);
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to create/update the configurationinfo " + configurationInfo.getName() + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Get configuration information
     * 
     * @throws XtentisException
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ConfigurationInfoPOJO getConfigurationInfo(ConfigurationInfoPOJOPK pk) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("getConfigurationInfo() ");
        }
        try {
            ConfigurationInfoPOJO configurationInfo = ObjectPOJO.load(ConfigurationInfoPOJO.class, pk);
            if (configurationInfo == null) {
                String err = "The Configuration Info " + pk.getUniqueId() + " does not exist.";
                LOGGER.error(err);
                throw new XtentisException(err);
            }
            return configurationInfo;
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to get the Configuration Info " + pk.toString() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Get a ConfigurationInfo - no exception is thrown: returns null if not found
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ConfigurationInfoPOJO existsConfigurationInfo(ConfigurationInfoPOJOPK pk) throws XtentisException {
        try {
            return ObjectPOJO.load(ConfigurationInfoPOJO.class, pk);
        } catch (XtentisException e) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("existsConfigurationInfo error.", e);
            }
            return null;
        } catch (Exception e) {
            String info = "Could not check whether this Configuration Info \"" + pk.getUniqueId() + "\" exists:  " + ": "
                    + e.getClass().getName() + ": " + e.getLocalizedMessage();
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug(info, e);
            }
            return null;
        }
    }

    /**
     * Remove a Configuration Info
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public ConfigurationInfoPOJOPK removeConfigurationInfo(ConfigurationInfoPOJOPK pk) throws XtentisException {
        if (LOGGER.isDebugEnabled()) {
            LOGGER.debug("Removing " + pk.getUniqueId());
        }
        try {
            return new ConfigurationInfoPOJOPK(ObjectPOJO.remove(ConfigurationInfoPOJO.class, pk));
        } catch (XtentisException e) {
            throw (e);
        } catch (Exception e) {
            String err = "Unable to remove the ConfigurationInfo " + pk.toString() + ": " + e.getClass().getName() + ": "
                    + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
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
        Collection<ObjectPOJOPK> configurations = ObjectPOJO.findAllPKs(ConfigurationInfoPOJO.class, regex);
        ArrayList<ConfigurationInfoPOJOPK> l = new ArrayList<ConfigurationInfoPOJOPK>();
        for (ObjectPOJOPK configuration : configurations) {
            l.add(new ConfigurationInfoPOJOPK(configuration));
        }
        return l;
    }

    /**
     * Auto Upgrades the core
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void autoUpgrade() throws XtentisException {
        if (LOGGER.isTraceEnabled()) {
            LOGGER.trace("autoUpgrade ");
        }
        try {
            ConfigurationInfo ctrl = Util.getConfigurationInfoCtrlLocal();
            CoreUpgrades.autoUpgrade(ctrl);
        } catch (Exception e) {
            String err = "Unable to upgrade in the background" + ": " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * Auto Upgrades the core in the background- called on startup
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void autoUpgradeInBackground() throws XtentisException {
        autoUpgradeInBackground(null);
    }

    /**
     * Auto Upgrades the core in the background- called by servlet
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     */
    public void autoUpgradeInBackground(AssembleProc assembleProc) throws XtentisException {
        LOGGER.info("Scheduling upgrade check in 5 seconds ");
        try {
            TimerService timerService = context.getTimerService();
            if (assembleProc == null) {
                AssembleConcreteBuilder concreteBuilder = new AssembleConcreteBuilder();
                AssembleDirector director = new AssembleDirector(concreteBuilder);
                director.constructAll();
                assembleProc = concreteBuilder.getAssembleProc();
            }
            timerService.createTimer(5234, assembleProc);
        } catch (Exception e) {
            String err = "Unable to upgrade in the background: " + e.getClass().getName() + ": " + e.getLocalizedMessage();
            LOGGER.error(err, e);
            throw new XtentisException(err, e);
        }
    }

    /**
     * @see #autoUpgradeInBackground()
     */
    public void ejbTimeout(Timer timer) {
        synchronized (LOCK) {
            if (LOGGER.isDebugEnabled()) {
                LOGGER.debug("ejbTimeout() AutoUpgrade autoUpgradeInBackground ");
            }
            // recover assemble Proc
            AssembleProc assembleProc;
            try {
                assembleProc = (AssembleProc) timer.getInfo();
            } catch (NoSuchObjectLocalException e) {
                // Catching exceptions to detect already cancelled timers is not optimal but there's no way to check
                // if a timer is cancelled.
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("ejbTimeout() Timer is already cancelled.");
                }
                return;
            }
            XmlServer server;
            try {
                server = Util.getXmlServerCtrlLocal();
            } catch (Exception e) {
                String err = "Auto Configuration in the background: unable to access the XML Server wrapper";
                LOGGER.error(err, e);
                throw new RuntimeException(err, e);
            }
            // cancel all existing timers
            TimerService timerService = context.getTimerService();
            Collection<Timer> timers = timerService.getTimers();
            for (Timer currentTimer : timers) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("ejbTimeout() Cancelling Timer " + currentTimer.getHandle());
                }
                try {
                    currentTimer.cancel();
                } catch (NoSuchObjectLocalException e) {
                    if (LOGGER.isDebugEnabled()) {
                        LOGGER.debug("Ignoring already cancelled timer.", e);
                    }
                }
            }
            if (server.isUpAndRunning()) {
                LOGGER.info("--Starting configuration...");
                assembleProc.run();
                LOGGER.info("--Done configuration.");
            } else {
                LOGGER.info("Auto Upgrade. XML Server not ready. Retrying in 5 seconds ");
                timerService.createTimer(5000, assembleProc);
            }
        }
    }
}
