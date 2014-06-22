package com.amalto.core.objects.configurationinfo.localutil;

import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.ejb.CreateException;
import javax.naming.NamingException;

import org.talend.mdm.commmon.util.core.MDMConfiguration;

import com.amalto.core.initdb.InitDBUtil;
import com.amalto.core.migration.AbstractMigrationTask;
import com.amalto.core.migration.MigrationRepository;
import com.amalto.core.objects.configurationinfo.ejb.ConfigurationInfoPOJO;
import com.amalto.core.objects.configurationinfo.ejb.ConfigurationInfoPOJOPK;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.util.Version;
import com.amalto.core.util.XtentisException;


/**
 * Performs the core upgrades
 * @author bgrieder
 * 
 *
 */

public class CoreUpgrades {


    
    /**
     * UploadFile.java
     * Constructor
     * 
     */
    public CoreUpgrades() {
        super();
    }
  
    public static void autoUpgrade(ConfigurationInfoCtrlLocal ctrl) throws XtentisException,NamingException,CreateException{
    	
    	//We must make sure we use a Class NOT present in z.com.amalto.core.jar
    	ConfigurationInfoPOJO previousCoreConf = getPreviousCoreConfigurationInfo(ctrl);
    	
    	
    	Version thisVersion = Version.getVersion(ConfigurationInfoCtrlLocal.class);
    	if (previousCoreConf.getMajor()!=-1) {
    		org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("Last run Core version was "+previousCoreConf.getVersionString()+"...");
    	}
    	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("This Core version is "+thisVersion.toString()+"...");
    	boolean forceupgrade = "true".equals(MDMConfiguration.getConfiguration().getProperty(
				"system.data.force.upgrade", 
				"false"
			));
    	
    	boolean isNeedUpgrade =needUpgrade(
    			previousCoreConf.getMajor(), 
    			previousCoreConf.getMinor(), 
    			previousCoreConf.getRevision(), 
    			thisVersion.getMajor(),
    			thisVersion.getMinor(), 
    			thisVersion.getRevision());
    	if  (forceupgrade||isNeedUpgrade)  {
    		//reset 
    		if(isNeedUpgrade){
    			ConfigurationHelper.removeCluster(null, AbstractMigrationTask.CLUSTER_MIGRATION);
    			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("Reset migration history records...");
    			MDMConfiguration.getConfiguration().setProperty("cluster_override", "true");
    			InitDBUtil.initDB();
    		}
        	//upgrading
        	upgradeFrom(previousCoreConf.getMajor(), previousCoreConf.getMinor(), previousCoreConf.getRevision(),forceupgrade);
    	} else {
    		org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("No core upgrade needed...");
    	}
    	
    	//update saved core conf
    	previousCoreConf.setMajor(thisVersion.getMajor());
    	previousCoreConf.setMinor(thisVersion.getMinor());
    	previousCoreConf.setRevision(thisVersion.getRevision());
    	previousCoreConf.setBuild(thisVersion.getBuild());
    	previousCoreConf.setReleaseNote(thisVersion.getDescription());
    	previousCoreConf.setDate(thisVersion.getDate());
    	ctrl.putConfigurationInfo(previousCoreConf);
    
    }

    /**
	 * Returns the previously run Core Configuration
	 * @return a {@link ConfigurationInfoPOJO} cntainin the core Configuration
	 */
	private static ConfigurationInfoPOJO getPreviousCoreConfigurationInfo(ConfigurationInfoCtrlLocal ctrl) throws XtentisException{

		ConfigurationInfoPOJO coreConfigurationInfo = ctrl.existsConfigurationInfo(new ConfigurationInfoPOJOPK("Core"));
		if (coreConfigurationInfo == null) {
			coreConfigurationInfo = new ConfigurationInfoPOJO("Core");
			coreConfigurationInfo.setMajor(-1);
			coreConfigurationInfo.setMinor(0);
			coreConfigurationInfo.setRevision(0);
			coreConfigurationInfo.setReleaseNote("");

			try {
				
				Properties properties = MDMConfiguration.getConfiguration(true);	
				Set<Object> keys = properties.keySet();
				for (Iterator<Object> iter = keys.iterator(); iter.hasNext(); ) {
					String key = (String) iter.next();
					coreConfigurationInfo.setProperty(key, properties.getProperty(key));
				}
					
			} catch (Exception e) {
				String err= "Unable to read mdm.conf and assign the properties to the core"+e.getClass().getName()+": "+e.getMessage();
				org.apache.log4j.Logger.getLogger(CoreUpgrades.class).error(err,e);
				throw new XtentisException(err);
			}
		
		}
		
		return coreConfigurationInfo;
		
	}
	

    private static void upgradeFrom(int previousMajor, int previousMinor, int previousRevision,boolean forceupgrade) throws XtentisException{

    	//execute migration task
    	MigrationRepository.getInstance().connect(forceupgrade);
		
    	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("Done upgrade from "+previousMajor+"."+previousMinor+"."+previousRevision+" to current version. ");
		org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("Core upgrades completed. ");
                
    }
    
	private static boolean needUpgrade(
			int previousMajor,
			int previousMinor,
			int previousRevision,
			int major,
			int minor,
			int revision
	) {
		return 
			(major>previousMajor) ||
			((major==previousMajor) && (minor>previousMinor)) ||
			((major==previousMajor) && (minor==previousMinor) && (revision>previousRevision));
	}
    
}