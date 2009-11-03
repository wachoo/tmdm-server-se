package com.amalto.core.objects.configurationinfo.localutil;

import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import javax.ejb.CreateException;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.talend.mdm.commmon.util.core.ICoreConstants;

import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.migration.MigrationRepository;
import com.amalto.core.objects.configurationinfo.ejb.ConfigurationInfoPOJO;
import com.amalto.core.objects.configurationinfo.ejb.ConfigurationInfoPOJOPK;
import com.amalto.core.objects.configurationinfo.ejb.local.ConfigurationInfoCtrlLocal;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datacluster.ejb.local.DataClusterCtrlLocal;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.datamodel.ejb.local.DataModelCtrlLocal;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocalHome;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2CtrlBean;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocalHome;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.transformers.v2.util.TransformerVariablesMapping;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.util.Util;
import com.amalto.core.util.Version;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.WSDataModel;
import com.amalto.core.webservice.WSDataModelPK;
import com.amalto.core.webservice.WSExistsDataModel;
import com.amalto.core.webservice.WSPutDataModel;


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
    /**
     * init cross referencing datamodel/datacluster
     * @throws Exception
     */
    private static void initCrossReferencing()throws XtentisException{

        org.apache.log4j.Logger.getLogger(CoreUpgrades.class).debug("init() Cross-Referencing - checking cluster and data model");
    	//create the cluster and data model  if they do not exist
		try {
			DataClusterCtrlLocal local=com.amalto.core.util.Util.getDataClusterCtrlLocal();
			if (local.existsDataCluster(new DataClusterPOJOPK(ICoreConstants.datacluster)) ==null) {
				local.putDataCluster(new DataClusterPOJO(ICoreConstants.datacluster, "MDM Cross Referencing Data",""));
			}
			DataModelCtrlLocal datamodelLocal=Util.getDataModelCtrlLocal();
			if (datamodelLocal.existsDataModel(new DataModelPOJOPK(ICoreConstants.datamodel))==null) {
				datamodelLocal.putDataModel(
						new DataModelPOJO(ICoreConstants.datamodel, "MDM Cross Referencing Table Definitions",""));
			}
		} catch (Exception e) {
			String err = "Unable to initialize the crossreferencing data cluster and data model.";
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).error(err, e);
			throw new XtentisException(err);
		}   	
    }
    
    public static void autoUpgrade(ConfigurationInfoCtrlLocal ctrl) throws XtentisException,NamingException,CreateException{
    	//init crossreferencing datamodel/datacluster
    	initCrossReferencing(); //aiming added
    	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("execute initCrossReferencing  sucessfully!");
    	//execute migration task, aiming added
    	MigrationRepository.getInstance().connect();
    	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("execute migration tasks sucessfully!");
    	ConfigurationInfoPOJO previousCoreConf = getPreviousCoreConfigurationInfo(ctrl);
    	//We must make sure we use a Class NOT present in z.com.amalto.core.jar
    	Version thisVersion = Version.getVersion(ConfigurationInfoCtrlLocal.class);
    	if (previousCoreConf.getMajor()!=-1) {
    		org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("Last run Core version was "+previousCoreConf.getVersionString()+"....");
    	}
    	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("This Core version is "+thisVersion.toString()+"....");
    	if  (needUpgrade(
    			previousCoreConf.getMajor(), 
    			previousCoreConf.getMinor(), 
    			previousCoreConf.getRevision(), 
    			thisVersion.getMajor(),
    			thisVersion.getMinor(), 
    			thisVersion.getRevision())
    	)  {
        	//upgrading
        	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("...upgrading....");
        	upgradeFrom(previousCoreConf.getMajor(), previousCoreConf.getMinor(), previousCoreConf.getRevision());
    	} else {
    		org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("...no core upgrade needed");
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
    static File f = new File("mdm.conf");
    /**
	 * Returns the previously run Core Configuration
	 * @return a {@link ConfigurationInfoPOJO} cntainin the core Configuration
	 */
	private static ConfigurationInfoPOJO getPreviousCoreConfigurationInfo(ConfigurationInfoCtrlLocal ctrl) throws XtentisException{
		//The Core Object exists from version 2.18.0
		ConfigurationInfoPOJO coreConfigurationInfo = ctrl.existsConfigurationInfo(new ConfigurationInfoPOJOPK("Core"));
		if (coreConfigurationInfo == null) {
			//assume the previous core version was a 2.13.0
			coreConfigurationInfo = new ConfigurationInfoPOJO("Core");
			coreConfigurationInfo.setMajor(-1);
			coreConfigurationInfo.setMinor(0);
			coreConfigurationInfo.setRevision(0);
			coreConfigurationInfo.setReleaseNote("");

			Properties properties = new Properties();
			if (f.exists()) {
				try {
					properties.load(new FileInputStream(f));
					Set<Object> keys = properties.keySet();
					for (Iterator<Object> iter = keys.iterator(); iter.hasNext(); ) {
						String key = (String) iter.next();
						coreConfigurationInfo.setProperty(key, properties.getProperty(key));
					}
				} catch (Exception e) {
					String err= "Unable to read mdm.conf and assign the properties to the core"+
										e.getClass().getName()+": "+e.getMessage();
					org.apache.log4j.Logger.getLogger(CoreUpgrades.class).error(err,e);
					throw new XtentisException(err);
				}
			}
		}
		return coreConfigurationInfo;
	}
	

    private static void upgradeFrom(int previousMajor, int previousMinor, int previousRevision) throws XtentisException{

		if (needUpgrade(previousMajor, previousMinor, previousRevision, 2, 20, 1)) {
			upgrade_to_2_20_1();
		}
		
		org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("Core upgrades completed.");
                
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
	
	private static XmlServerSLWrapperLocal createCluster(String revisionID, Class<? extends ObjectPOJO> objectClass) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).trace("createCluster() check for "+objectClass.getName());
    	try {
    		boolean exist = false;
    		XmlServerSLWrapperLocal server = null;
    		try {
    			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
    		} catch (Exception e) {
    			String err = "Unable to access the XML Server wrapper";
    			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).error(err,e);
    			throw new XtentisException(err);
    		}
    		String[] clusters = server.getAllClusters(revisionID);
    		if (clusters != null) {
    			exist = Arrays.asList(clusters).contains(ObjectPOJO.getCluster(objectClass));
    		}
    		if (! exist) server.createCluster(null, ObjectPOJO.getCluster(objectClass));
    		return server;
    	} catch (Exception e) {
    		throw new XtentisException(e);
    	}
    	
    
    }
    
	
	private static void upgrade_to_2_20_1() throws XtentisException{
		try {
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("----Upgrading the core to 2.20.1");
			
			//Create all clusters in case - these are not called statically in the classes any more
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("--------Creating Universe cluster");
			XmlServerSLWrapperLocal server = createCluster(null, UniversePOJO.class);
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("--------done Creating Universe");
			
			//Update Menu POJOS
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("--------Updating Menus");
			String[] ids = server.getAllDocumentsUniqueID(null, "amaltoOBJECTSMenu");
			if (ids != null) {
				
				MenuCtrlLocal menuCtrl = ((MenuCtrlLocalHome)new InitialContext().lookup(MenuCtrlLocalHome.JNDI_NAME)).create();
				
				for (int i = 0; i < ids.length; i++) {
					String xml = server.getDocumentAsString(null, "amaltoOBJECTSMenu", ids[i]);
					xml = xml.replaceAll("java:com.amalto.core.ejb.MenuEntryPOJO", "java:com.amalto.core.objects.menu.ejb.MenuEntryPOJO");
					MenuPOJO menu = ObjectPOJO.unmarshal(MenuPOJO.class, xml);
					menuCtrl.putMenu(menu);
					org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("------------processed '"+menu.getName()+"'");
				}
			}
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("--------done Updating Menus");
			
			//Update Transformer POJOS
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("--------Updating Transformers");
			ids = server.getAllDocumentsUniqueID(null, ObjectPOJO.getCluster(TransformerV2POJO.class));
			if (ids != null) {
				
				TransformerV2CtrlLocal tCtrl = ((TransformerV2CtrlLocalHome)new InitialContext().lookup(TransformerV2CtrlLocalHome.JNDI_NAME)).create();
				
				for (int i = 0; i < ids.length; i++) {
			
					String xml = server.getDocumentAsString(null, ObjectPOJO.getCluster(TransformerV2POJO.class), ids[i]);
					TransformerV2POJO transformer = ObjectPOJO.unmarshal(TransformerV2POJO.class, xml);

					
					
					ArrayList<TransformerProcessStep> steps = transformer.getProcessSteps();
                	if (steps!=null) {
        	        	for (Iterator<TransformerProcessStep> iter = steps.iterator(); iter.hasNext(); ) {
        	        		TransformerProcessStep step = iter.next();
        	        		//add some text to empty descriptions
        	        		if (step.getDescription() == null || "".equals(step.getDescription().trim()))
        	        			step.setDescription("[no description]");
        	        		
        	        		//Replace empty variable with "_DEFAULT_" variable
        			    	ArrayList<TransformerVariablesMapping> inputMappings = step.getInputMappings();
        			    	for (Iterator<TransformerVariablesMapping> iterator = inputMappings.iterator(); iterator.hasNext(); ) {
        						TransformerVariablesMapping transformerVariablesMapping = iterator.next();
        						String pipelineVariable = transformerVariablesMapping.getPipelineVariable();
        						if (pipelineVariable==null || "".equals(pipelineVariable.trim()))
        							transformerVariablesMapping.setPipelineVariable(TransformerV2CtrlBean.DEFAULT_VARIABLE);
        					}
        			    	ArrayList<TransformerVariablesMapping> outputMappings = step.getOutputMappings();
        			    	for (Iterator<TransformerVariablesMapping> iterator = outputMappings.iterator(); iterator.hasNext(); ) {
        						TransformerVariablesMapping transformerVariablesMapping = iterator.next();
        						String pipelineVariable = transformerVariablesMapping.getPipelineVariable();
        						if (pipelineVariable==null || "".equals(pipelineVariable.trim()))
        							transformerVariablesMapping.setPipelineVariable(TransformerV2CtrlBean.DEFAULT_VARIABLE);
        					}
        				}//for
                	}
                	tCtrl.putTransformer(transformer);
                	org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("------------processed '"+transformer.getName()+"'");
				}
			}
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("--------done Updating Transformers");
				
			
			org.apache.log4j.Logger.getLogger(CoreUpgrades.class).info("---- done upgrade to 2.20.0");

    	} catch (Exception e) {
    		StringWriter sw = new StringWriter();
    		e.printStackTrace(new PrintWriter(sw));
    		String err= "Core Upgrade to 2.20.0 Failed:\n"+sw.toString();
    		org.apache.log4j.Logger.getLogger(CoreUpgrades.class).error(err,e);
    		throw new XtentisException(err);
    	}
	}
    
}