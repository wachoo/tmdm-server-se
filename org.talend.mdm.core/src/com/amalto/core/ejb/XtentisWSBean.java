package com.amalto.core.ejb;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NameClassPair;
import javax.naming.NamingEnumeration;
import javax.resource.ResourceException;
import javax.resource.cci.Connection;
import javax.resource.cci.ConnectionFactory;
import javax.resource.cci.Interaction;
import javax.resource.cci.MappedRecord;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.jboss.security.Base64Encoder;
import org.ow2.bonita.facade.def.majorElement.ProcessDefinition;
import org.ow2.bonita.facade.runtime.ProcessInstance;
import org.ow2.bonita.facade.runtime.TaskInstance;
import org.ow2.bonita.facade.uuid.ProcessDefinitionUUID;
import org.ow2.bonita.facade.uuid.ProcessInstanceUUID;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.talend.mdm.commmon.util.webapp.XSystemObjects;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import sun.misc.BASE64Decoder;

import com.amalto.connector.jca.InteractionSpecImpl;
import com.amalto.connector.jca.RecordFactoryImpl;
import com.amalto.core.delegator.BeanDelegatorContainer;
import com.amalto.core.ejb.local.TransformerCtrlLocal;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJO;
import com.amalto.core.objects.backgroundjob.ejb.BackgroundJobPOJOPK;
import com.amalto.core.objects.backgroundjob.ejb.local.BackgroundJobCtrlUtil;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJOPK;
import com.amalto.core.objects.menu.ejb.MenuEntryPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJOPK;
import com.amalto.core.objects.menu.ejb.local.MenuCtrlLocal;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.role.ejb.local.RoleCtrlLocal;
import com.amalto.core.objects.role.ejb.local.RoleCtrlUtil;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.AbstractRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.ActiveRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.CompletedRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJO;
import com.amalto.core.objects.routing.v2.ejb.FailedRoutingOrderV2POJOPK;
import com.amalto.core.objects.routing.v2.ejb.RoutingEngineV2POJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRuleExpressionPOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJOPK;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingEngineV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingOrderV2CtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlLocal;
import com.amalto.core.objects.routing.v2.ejb.local.RoutingRuleCtrlUtil;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJOPK;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlLocal;
import com.amalto.core.objects.storedprocedure.ejb.local.StoredProcedureCtrlUtil;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationItemPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanItemLine;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanObjectLine;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJO;
import com.amalto.core.objects.synchronization.ejb.SynchronizationPlanPOJOPK;
import com.amalto.core.objects.synchronization.ejb.SynchronizationRemoteInstance;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationItemCtrlUtil;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlLocal;
import com.amalto.core.objects.synchronization.ejb.local.SynchronizationPlanCtrlUtil;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.ejb.local.TransformerV2CtrlLocal;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TransformerPluginVariableDescriptor;
import com.amalto.core.objects.transformers.v2.util.TransformerProcessStep;
import com.amalto.core.objects.transformers.v2.util.TransformerVariablesMapping;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.universe.ejb.UniversePOJOPK;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlLocal;
import com.amalto.core.objects.universe.ejb.local.UniverseCtrlUtil;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJO;
import com.amalto.core.objects.versioning.ejb.VersioningSystemPOJOPK;
import com.amalto.core.objects.versioning.ejb.local.VersioningSystemCtrlLocal;
import com.amalto.core.objects.versioning.util.HistoryInfos;
import com.amalto.core.objects.versioning.util.TagStructureInfo;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.objects.view.ejb.local.ViewCtrlUtil;
import com.amalto.core.util.ArrayListHolder;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.RoleInstance;
import com.amalto.core.util.RoleSpecification;
import com.amalto.core.util.TransformerPluginContext;
import com.amalto.core.util.TransformerPluginSpec;
import com.amalto.core.util.UpdateReportItem;
import com.amalto.core.util.Util;
import com.amalto.core.util.Version;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.core.webservice.*;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereOr;


/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="XtentisWS"   
 * 					display-name="The Xtentis"
 * 					description="The Xtentis WebServices"
 *					jndi-name="amalto/ws/xtentis" 
 * 					type="Stateless"
 *                  view-type="service-endpoint"
 *
 * @jboss.port-component 
 * 		auth-method = "BASIC" 
 * 		name = "XtentisPort" 
 * 		uri = "/talend/TalendPort"
 *                   
 *  @wsee.port-component 
 * 					description = "The Xtentis Port" 
 * 					display-name ="XtentisPort" 
 * 					name = "XtentisPort"				
 * 
 * 
 * Not generated by xdoclet:just an indication for the deployment descriptor but
 * we use the one generated by jwsdp
 * @ejb.interface service-endpoint-class =
 *                          "com.amalto.core.webservice.XtentisPort"
 *
  * @ejb.ejb-ref 
 * 					ejb-name = "DataModelCtrl" 
 * 					ref-name = "ejb/DataModelCtrlLocal" 
 * 					view-type = "local"
 * @ejb.ejb-ref 
 * 					ejb-name = "DataClusterCtrl" 
 * 					ref-name = "ejb/DataClusterCtrlLocal" 
 * 					view-type = "local"
 * @ejb.ejb-ref 
 * 					ejb-name = "ItemCtrl2" 
 * 					ref-name = "ejb/ItemCtrl2Local" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "ViewCtrl" 
 * 					ref-name = "ejb/ViewCtrlLocal" 
 * 					view-type = "local"
 *
 * @ejb.ejb-ref 
 * 					ejb-name = "StoredProcedureCtrl" 
 * 					ref-name = "ejb/StoredProcedureCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "RoleCtrl" 
 * 					ref-name = "ejb/RoleCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "MenuCtrl" 
 * 					ref-name = "ejb/MenuCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "BackgroundJobCtrl" 
 * 					ref-name = "ejb/BackgroundJobCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "UniverseCtrl" 
 * 					ref-name = "ejb/UniverseCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "RoutingRuleCtrl" 
 * 					ref-name = "ejb/RoutingRuleCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "SynchronizationPlanCtrl" 
 * 					ref-name = "ejb/SynchronizationPlanCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "SynchronizationObjectCtrl" 
 * 					ref-name = "ejb/SynchronizationObjectCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "SynchronizationItemCtrl" 
 * 					ref-name = "ejb/SynchronizationItemCtrlLocal" 
 * 					view-type = "local"
 * 
 * @ejb.ejb-ref 
 * 					ejb-name = "DroppedItemCtrl" 
 * 					ref-name = "ejb/DroppedItemCtrlLocal" 
 * 					view-type = "local"
 */
@SuppressWarnings({"deprecation", "unchecked"})
public class XtentisWSBean implements SessionBean, XtentisPort {


	/**
	 *  
	 */
	public XtentisWSBean() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
	 */
	public void setSessionContext(SessionContext ctx) throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.essionBean#ejbRemove()
	 */
	public void ejbRemove() throws EJBException, RemoteException {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbActivate()
	 */
	public void ejbActivate() throws EJBException, RemoteException {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.ejb.SessionBean#ejbPassivate()
	 */
	public void ejbPassivate() throws EJBException, RemoteException {
	}

	/**
	 * Default create method
	 * 
	 * @throws CreateException
	 * @ejb.create-method
	 */
	public void ejbCreate() throws CreateException {
	}

	/***************************************************************************
	 * 
	 * S E R V I C E S
	 *  
	 *	 **************************************************************************/

	/***************************************************************************
	 * Components Management
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersion getComponentVersion(WSGetComponentVersion wsGetComponentVersion) throws RemoteException {
		try {
			if (WSComponent.DataManager.equals(wsGetComponentVersion.getComponent())) {	
				Version version = Version.getVersion(XtentisWSBean.class);
				return new WSVersion(
						version.getMajor(),
						version.getMinor(),
						version.getRevision(),
						version.getBuild(),
						version.getDescription(),
						version.getDate()
				);
			}
			throw new RemoteException ("Version information is not available yet for "+wsGetComponentVersion.getComponent().getValue()+" components"); 
		} catch (RemoteException e) {
			throw(e);
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	
	/***************************************************************************
	 * Ping
	 * **************************************************************************/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString ping(WSPing wsPing) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().ping(wsPing);
	}

	/***************************************************************************
	 * Logout
	 * **************************************************************************/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString logout(WSLogout logout) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().logout(logout);
	}
	

	
	/***************************************************************************
	 * Initialize
	 * **************************************************************************/
	

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSInt initMDM(WSInitData initData) throws RemoteException {
//		try {
//			DoInitializeObjectsLocal initialize =DoInitializeObjectsUtil.getLocalHome().create(); 
//			if (initData.isZap()) initialize.zapXmlServer();
//			initialize.initializeObjects(initData.getXmlSchema());
//			return new WSInt(initData.getXmlSchema().length());
//		} catch (Exception e) {
//			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
//		}
		return new WSInt(0);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMDMConfig getMDMConfiguration() throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getMDMConfiguration();
	}
	/***************************************************************************
	 * Data Model
	 * **************************************************************************/
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSDataModel getDataModel(WSGetDataModel wsDataModelget)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getDataModel(wsDataModelget);
    }
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSBoolean existsDataModel(WSExistsDataModel wsExistsDataModel)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsDataModel(wsExistsDataModel);
    }
    

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 * 	
	 */       
    public WSDataModelPKArray getDataModelPKs(WSRegexDataModelPKs regexp)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getDataModelPKs(regexp);
    }
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSDataModelPK deleteDataModel(WSDeleteDataModel wsDeleteDataModel)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteDataModel(wsDeleteDataModel);
    }	
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSDataModelPK putDataModel(WSPutDataModel wsDataModel)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putDataModel(wsDataModel);
    }
 
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
	public WSString checkSchema(WSCheckSchema wsSchema) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().checkSchema(wsSchema);

	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSString putBusinessConcept(WSPutBusinessConcept wsPutBusinessConcept)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putBusinessConcept(wsPutBusinessConcept);
	}
     
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * 
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSString putBusinessConceptSchema(WSPutBusinessConceptSchema wsPutBusinessConceptSchema)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putBusinessConceptSchema(wsPutBusinessConceptSchema);
	}
    
	    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */ 
    public WSString deleteBusinessConcept(
            WSDeleteBusinessConcept wsDeleteBusinessConcept)
            throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteBusinessConcept(wsDeleteBusinessConcept);
    }
    
    
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSStringArray getBusinessConcepts(
            WSGetBusinessConcepts wsGetBusinessConcepts) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getBusinessConcepts(wsGetBusinessConcepts);
    }
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSConceptKey getBusinessConceptKey(
            WSGetBusinessConceptKey wsGetBusinessConceptKey) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getBusinessConceptKey(wsGetBusinessConceptKey);
    }
	
	
	/***************************************************************************
	 * DataCluster
	 * **************************************************************************/
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	   public WSDataCluster getDataCluster(WSGetDataCluster wsDataClusterGet)
	    throws RemoteException {
		   return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getDataCluster(wsDataClusterGet);
	    }
	    
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */	
		   public WSBoolean existsDataCluster(WSExistsDataCluster wsExistsDataCluster)
		    throws RemoteException {
			   return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsDataCluster(wsExistsDataCluster);
		    }
			/**
			 * @ejb.interface-method view-type = "service-endpoint"
			 * @ejb.permission 
			 * 	role-name = "authenticated"
			 * 	view-type = "service-endpoint"
			 */	
			   public WSBoolean existsDBDataCluster(WSExistsDBDataCluster wsExistsDataCluster)
			    throws RemoteException {
				   return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsDBDataCluster(wsExistsDataCluster);
			    }


		/**
		* @ejb.interface-method view-type = "service-endpoint"
		* @ejb.permission 
		* 	role-name = "authenticated"
		* 	view-type = "service-endpoint"
		*/    
	    public WSDataClusterPKArray getDataClusterPKs(WSRegexDataClusterPKs regexp)
	    throws RemoteException {
	    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getDataClusterPKs(regexp);
	    }

	    
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */
	    public WSDataClusterPK deleteDataCluster(WSDeleteDataCluster wsDeleteDataCluster)
	    throws RemoteException {
	    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteDataCluster(wsDeleteDataCluster);
	    }	
	    
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */   
	    public WSDataClusterPK putDataCluster(WSPutDataCluster wsDataCluster)
	    throws RemoteException {
	    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putDataCluster(wsDataCluster);
	    }
		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */   
	    public WSBoolean putDBDataCluster(WSPutDBDataCluster wsDataCluster)
	    throws RemoteException {
	    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putDBDataCluster(wsDataCluster);
	    }

	    /**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */	
		public WSStringArray getConceptsInDataCluster(WSGetConceptsInDataCluster wsGetConceptsInDataCluster) throws RemoteException {
			return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getConceptsInDataCluster(wsGetConceptsInDataCluster);
		}
		

		/**
		 * @ejb.interface-method view-type = "service-endpoint"
		 * @ejb.permission 
		 * 	role-name = "authenticated"
		 * 	view-type = "service-endpoint"
		 */	
		public WSConceptRevisionMap getConceptsInDataClusterWithRevisions(WSGetConceptsInDataClusterWithRevisions wsGetConceptsInDataClusterWithRevisions) throws RemoteException {
					
			return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getConceptsInDataClusterWithRevisions(wsGetConceptsInDataClusterWithRevisions);
		}

		

	
	
	/***************************************************************************
	 * View
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
   public WSView getView(WSGetView wsViewGet)
    throws RemoteException {
	   return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getView(wsViewGet);
    }

   
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
  public WSBoolean existsView(WSExistsView wsExistsView)
   throws RemoteException {
	  return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsView(wsExistsView);
   }
   

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
    public WSViewPKArray getViewPKs(WSGetViewPKs regexp) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getViewPKs(regexp);
    }
		    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSViewPK deleteView(WSDeleteView wsDeleteView)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteView(wsDeleteView);
    }	
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSViewPK putView(WSPutView wsView)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putView(wsView);
    }

	
	

	/***************************************************************************
	 * Search
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray viewSearch(WSViewSearch wsViewSearch) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().viewSearch(wsViewSearch);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray xPathsSearch(WSXPathsSearch wsXPathsSearch) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().xPathsSearch(wsXPathsSearch);
	}

	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray getItemsPivotIndex(WSGetItemsPivotIndex wsGetItemsPivotIndex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getItemsPivotIndex(wsGetItemsPivotIndex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString count(WSCount wsCount) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().count(wsCount);
	}

	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	public WSStringArray getItems(WSGetItems wsGetItems) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getItems(wsGetItems);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	public WSItemPKsByCriteriaResponse getItemPKsByCriteria(WSGetItemPKsByCriteria wsGetItemPKsByCriteria) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getItemPKsByCriteria(wsGetItemPKsByCriteria);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItem getItem(WSGetItem wsGetItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getItem(wsGetItem);
	}	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsItem(WSExistsItem wsExistsItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsItem(wsExistsItem);
	}	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray quickSearch(WSQuickSearch wsQuickSearch) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().quickSearch(wsQuickSearch);
	}

	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */		
	public WSString getBusinessConceptValue(
			WSGetBusinessConceptValue wsGetBusinessConceptValue)
			throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getBusinessConceptValue(wsGetBusinessConceptValue);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	public WSStringArray getFullPathValues(WSGetFullPathValues wsGetFullPathValues)
			throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getFullPathValues(wsGetFullPathValues);
	}




	/***************************************************************************
	 *Put Item
	 * **************************************************************************/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK putItem(WSPutItem wsPutItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putItem(wsPutItem);
	}	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPKArray putItemArray(WSPutItemArray wsPutItemArray) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putItemArray(wsPutItemArray);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPKArray putItemWithReportArray(com.amalto.core.webservice.WSPutItemWithReportArray wsPutItemWithReportArray) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putItemWithReportArray(wsPutItemWithReportArray);
	}	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK putItemWithReport(com.amalto.core.webservice.WSPutItemWithReport wsPutItemWithReport) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putItemWithReport(wsPutItemWithReport);
		
		
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK putItemWithCustomReport(com.amalto.core.webservice.WSPutItemWithCustomReport wsPutItemWithCustomReport) throws RemoteException{
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putItemWithCustomReport(wsPutItemWithCustomReport);				
	}
    
	/***************************************************************************
	 *Extract Items
	 * **************************************************************************/

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSPipeline extractUsingTransformer(WSExtractUsingTransformer wsExtractUsingTransformer) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().extractUsingTransformer(wsExtractUsingTransformer);	
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSPipeline extractUsingTransformerThruView(WSExtractUsingTransformerThruView wsExtractUsingTransformerThruView) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().extractUsingTransformerThruView(wsExtractUsingTransformerThruView);		
	}


    
	
	/***************************************************************************
	 * Delete Items
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK deleteItem(WSDeleteItem wsDeleteItem)
	throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteItem(wsDeleteItem);
	}    
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSInt deleteItems(WSDeleteItems wsDeleteItems)
	throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteItems(wsDeleteItems);
	} 
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItemPK dropItem(WSDropItem wsDropItem)
		throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().dropItem(wsDropItem);
	}
	
	
	/***************************************************************************
	 * DirectQuery
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "administration, DataManagerAdministration"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray runQuery(WSRunQuery wsRunQuery) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().runQuery(wsRunQuery);
	}    
	
	
	/***************************************************************************
	 * RoutingRule
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
   public WSRoutingRule getRoutingRule(WSGetRoutingRule wsRoutingRuleGet)
    throws RemoteException {
	   return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getRoutingRule(wsRoutingRuleGet);
    }
  
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
	  public WSBoolean existsRoutingRule(WSExistsRoutingRule wsExistsRoutingRule)
	   throws RemoteException {
		  return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsRoutingRule(wsExistsRoutingRule);
	   }	    

		    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSRoutingRulePK deleteRoutingRule(WSDeleteRoutingRule wsDeleteRoutingRule)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteRoutingRule(wsDeleteRoutingRule);
    }	
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */   
    public WSRoutingRulePK putRoutingRule(WSPutRoutingRule wsRoutingRule)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putRoutingRule(wsRoutingRule);
    }
    
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingRulePKArray getRoutingRulePKs(WSGetRoutingRulePKs regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getRoutingRulePKs(regex);
	}

    

	
	/***************************************************************************
	 * SERVICES
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSServiceGetDocument getServiceDocument(WSString serviceName) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getServiceDocument(serviceName);
	}

	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString getServiceConfiguration(WSServiceGetConfiguration wsGetConfiguration) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getServiceConfiguration(wsGetConfiguration);
	}
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSCheckServiceConfigResponse checkServiceConfiguration(WSCheckServiceConfigRequest serviceName) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().checkServiceConfiguration(serviceName);
	}
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString putServiceConfiguration(WSServicePutConfiguration wsPutConfiguration) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putServiceConfiguration(wsPutConfiguration);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString serviceAction(WSServiceAction wsServiceAction) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().serviceAction(wsServiceAction);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSServicesList getServicesList(WSGetServicesList wsGetServicesList) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getServicesList(wsGetServicesList);
	}
	
	
	
	/***************************************************************************
	 * Ping - test that we can authenticate by getting a server response
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString ping()	throws RemoteException {
		try {
			return new WSString("OK");
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}    
	
    	

	/***************************************************************************
	 * Xtentis JCA Connector support
	 * **************************************************************************/

	private transient ConnectionFactory cxFactory = null;
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSConnectorInteractionResponse connectorInteraction(WSConnectorInteraction wsConnectorInteraction) throws RemoteException {
		// This one does not call an EJB
		
		WSConnectorInteractionResponse response = new WSConnectorInteractionResponse();
		Connection conx = null;
		try {

			String JNDIName = wsConnectorInteraction.getJNDIName();
			conx = getConnection(JNDIName);
			
			Interaction interaction = conx.createInteraction();
	    	InteractionSpecImpl interactionSpec = new InteractionSpecImpl();
	    	
			MappedRecord recordIn = new RecordFactoryImpl().createMappedRecord(RecordFactoryImpl.RECORD_IN);
			
			WSConnectorFunction cf = wsConnectorInteraction.getFunction();
			if ((WSConnectorFunction.GET_STATUS).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_GET_STATUS);
			} else 	if ((WSConnectorFunction.PULL).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PULL);
			} else 	if ((WSConnectorFunction.PUSH).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_PUSH);
			} else 	if ((WSConnectorFunction.START).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_START);
			} else 	if ((WSConnectorFunction.STOP).equals(cf)) {
				interactionSpec.setFunctionName(InteractionSpecImpl.FUNCTION_STOP);
			}
			
			recordIn.put(RecordFactoryImpl.PARAMS_HASHMAP_IN, getMapFromKeyValues(wsConnectorInteraction.getParameters()));
						
			MappedRecord recordOut = (MappedRecord)interaction.execute(interactionSpec, recordIn);

			String code = (String)recordOut.get(RecordFactoryImpl.STATUS_CODE_OUT);
			HashMap map = (HashMap)recordOut.get(RecordFactoryImpl.PARAMS_HASHMAP_OUT);
			
			if ("OK".equals(code)) {
				response.setCode(WSConnectorResponseCode.OK);
			} else if ("STOPPED".equals(code)) {
				response.setCode(WSConnectorResponseCode.STOPPED);
			} else if ("ERROR".equals(code)) {
				response.setCode(WSConnectorResponseCode.ERROR);
			} else {
				throw new RemoteException("Unknown code: "+code);
			}
			response.setParameters(getKeyValuesFromMap(map));
			
		} catch (ResourceException e) {
			throw new RemoteException(e.getLocalizedMessage());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		} finally {
			try {conx.close();} catch (Exception cx) {
				org.apache.log4j.Category.getInstance(this.getClass()).debug("connectorInteraction() Connection close exception: "+cx.getLocalizedMessage());
			}
		}
		return response;		
		
	}

    private Connection getConnection(String JNDIName) throws RemoteException {
    	try {
    		if (cxFactory == null)
    			cxFactory = (ConnectionFactory)(new InitialContext()).lookup(JNDIName);
	    	return cxFactory.getConnection();
    	} catch (Exception e) {
    		throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
    	}
    }
    
	private HashMap getMapFromKeyValues(WSBase64KeyValue[] params) throws RemoteException{
		try {
	    	HashMap map = new HashMap();
	    	if (params != null) {
				for (int i = 0; i < params.length; i++) {
					if (params[i]!=null) {
						String key = params[i].getKey();
						byte[] bytes = (new BASE64Decoder()).decodeBuffer(params[i].getBase64StringValue());
						if (bytes!=null) {
							ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
							ObjectInputStream ois = new ObjectInputStream(bais);
							map.put(key, ois.readObject());
						} else {
							map.put(key, null);
						}
					}
				}
	    	}
			return map;
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}    	
    }
    

	private WSBase64KeyValue[] getKeyValuesFromMap(HashMap params) throws RemoteException{    	
    	try {
    		if (params==null) return null;
    		WSBase64KeyValue[] keyValues = new WSBase64KeyValue[params.size()];
    		Set keys = params.keySet();
    		int i=0;
    		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
				String key = (String) iter.next();
				Object value = params.get(key);
				if (value!=null) {
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ObjectOutputStream oos = new ObjectOutputStream(baos);
					oos.writeObject(value);
					String base64Value = Base64Encoder.encode(baos.toByteArray());
					keyValues[i] = new WSBase64KeyValue();
					keyValues[i].setKey(key);
					keyValues[i].setBase64StringValue(base64Value);
					i++;
				}
			}
			return keyValues;
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}    	
    }
    
    

	/***************************************************************************
	 * Stored Procedure
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSStoredProcedurePK deleteStoredProcedure(WSDeleteStoredProcedure wsStoredProcedureDelete) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteStoredProcedure(wsStoredProcedureDelete);
	}
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStringArray executeStoredProcedure(WSExecuteStoredProcedure wsExecuteStoredProcedure) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().executeStoredProcedure(wsExecuteStoredProcedure);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStoredProcedure getStoredProcedure(WSGetStoredProcedure wsGetStoredProcedure) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getStoredProcedure(wsGetStoredProcedure);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsStoredProcedure(WSExistsStoredProcedure wsExistsStoredProcedure) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsStoredProcedure(wsExistsStoredProcedure);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStoredProcedurePKArray getStoredProcedurePKs(WSRegexStoredProcedure regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getStoredProcedurePKs(regex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSStoredProcedurePK putStoredProcedure(WSPutStoredProcedure wsStoredProcedure) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putStoredProcedure(wsStoredProcedure);
	}

	/***************************************************************************
	 * TransformerV2
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSTransformerV2PK deleteTransformerV2(WSDeleteTransformerV2 wsTransformerV2Delete) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteTransformerV2(wsTransformerV2Delete);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2 getTransformerV2(WSGetTransformerV2 wsGetTransformerV2) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getTransformerV2(wsGetTransformerV2);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformerV2(WSExistsTransformerV2 wsExistsTransformerV2) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsTransformerV2(wsExistsTransformerV2);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2PKArray getTransformerV2PKs(WSGetTransformerV2PKs regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getTransformerV2PKs(regex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerV2PK putTransformerV2(WSPutTransformerV2 wsTransformerV2) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putTransformerV2(wsTransformerV2);
	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerContext executeTransformerV2(WSExecuteTransformerV2 wsExecuteTransformerV2) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().executeTransformerV2(wsExecuteTransformerV2);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK executeTransformerV2AsJob(WSExecuteTransformerV2AsJob wsExecuteTransformerV2AsJob) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().executeTransformerV2AsJob(wsExecuteTransformerV2AsJob);
	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerContext extractThroughTransformerV2(WSExtractThroughTransformerV2 wsExtractThroughTransformerV2) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().extractThroughTransformerV2(wsExtractThroughTransformerV2);
	}
	
	
	/***************************************************************************
	 * TRANSFORMER PLUGINS V2
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformerPluginV2(WSExistsTransformerPluginV2 wsExistsTransformerPlugin) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsTransformerPluginV2(wsExistsTransformerPlugin);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString getTransformerPluginV2Configuration(WSTransformerPluginV2GetConfiguration wsGetConfiguration) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getTransformerPluginV2Configuration(wsGetConfiguration);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString putTransformerPluginV2Configuration(WSTransformerPluginV2PutConfiguration wsPutConfiguration) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putTransformerPluginV2Configuration(wsPutConfiguration);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPluginV2Details getTransformerPluginV2Details(WSGetTransformerPluginV2Details wsGetTransformerPluginDetails) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getTransformerPluginV2Details(wsGetTransformerPluginDetails);
	}


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 	 
	 *  	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPluginV2SList getTransformerPluginV2SList(WSGetTransformerPluginV2SList wsGetTransformerPluginsList) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getTransformerPluginV2SList(wsGetTransformerPluginsList);
	}
	

	/***************************************************************************
	 * Role
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSRolePK deleteRole(WSDeleteRole wsRoleDelete) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteRole(wsRoleDelete);
	}
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRole getRole(WSGetRole wsGetRole) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getRole(wsGetRole);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsRole(WSExistsRole wsExistsRole) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsRole(wsExistsRole);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRolePKArray getRolePKs(WSGetRolePKs regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getRolePKs(regex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRolePK putRole(WSPutRole wsRole) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putRole(wsRole);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSStringArray getObjectsForRoles(WSGetObjectsForRoles wsRoleDelete) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getObjectsForRoles(wsRoleDelete);
	}

	
	   
	
	/***************************************************************************
	 * Menu
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSMenuPK deleteMenu(WSDeleteMenu wsMenuDelete) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteMenu(wsMenuDelete);
	}
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMenu getMenu(WSGetMenu wsGetMenu) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getMenu(wsGetMenu);
	}


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsMenu(WSExistsMenu wsExistsMenu) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsMenu(wsExistsMenu);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMenuPKArray getMenuPKs(WSGetMenuPKs regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getMenuPKs(regex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSMenuPK putMenu(WSPutMenu wsMenu) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putMenu(wsMenu);
	}

	/***************************************************************************
	 * Versioning
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK versioningCommitItems(WSVersioningCommitItems wsVersioningCommitItems) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningCommitItems(wsVersioningCommitItems);
	};
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean versioningRestoreItemByRevision(WSVersioningRestoreItemByRevision wsVersioningRestoreItemByRevision) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningRestoreItemByRevision(wsVersioningRestoreItemByRevision);
	};
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersioningItemHistory versioningGetItemHistory(WSVersioningGetItemHistory wsVersioningGetItemHistory) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningGetItemHistory(wsVersioningGetItemHistory);
	};
	

	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersioningItemsVersions versioningGetItemsVersions(WSVersioningGetItemsVersions wsVersioningGetItemsVersions) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningGetItemsVersions(wsVersioningGetItemsVersions);
	};
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString versioningGetItemContent(WSVersioningGetItemContent wsVersioningGetItemContent) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningGetItemContent(wsVersioningGetItemContent);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersioningObjectsVersions versioningGetObjectsVersions(WSVersioningGetObjectsVersions wsVersioningGetObjectsVersions) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningGetObjectsVersions(wsVersioningGetObjectsVersions);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersioningUniverseVersions versioningGetUniverseVersions(WSVersioningGetUniverseVersions wsVersioningGetUniverseVersions) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningGetUniverseVersions(wsVersioningGetUniverseVersions);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK versioningRestoreItems(WSVersioningRestoreItems wsVersioningRestoreItems) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningRestoreItems(wsVersioningRestoreItems);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK versioningRestoreObjects(WSVersioningRestoreObjects wsVersioningRestoreObjects) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningRestoreObjects(wsVersioningRestoreObjects);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK versioningRestoreUniverse(WSVersioningRestoreUniverse wsVersioningRestoreUniverse) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningRestoreUniverse(wsVersioningRestoreUniverse);
		
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK versioningTagItems(WSVersioningTagItems wsVersioningTagItems) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningTagItems(wsVersioningTagItems);
	}

	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK versioningTagObjects(WSVersioningTagObjects wsVersioningTagObjects) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningTagObjects(wsVersioningTagObjects);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK versioningTagUniverse(WSVersioningTagUniverse wsVersioningTagUniverse) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningTagUniverse(wsVersioningTagUniverse);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersioningInfo versioningGetInfo(WSVersioningGetInfo wsVersioningGetInfo) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().versioningGetInfo(wsVersioningGetInfo);
	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString putVersioningSystemConfiguration(WSPutVersioningSystemConfiguration wsPutVersioningSystemConfiguration) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putVersioningSystemConfiguration(wsPutVersioningSystemConfiguration);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSVersioningSystemConfiguration getVersioningSystemConfiguration(WSGetVersioningSystemConfiguration wsGetVersioningSystemConfiguration) throws RemoteException {
		throw new RemoteException("Not Supported Yet");
	};
	
	
	/***************************************************************************
	 * BackgroundJob
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
   public WSBackgroundJob getBackgroundJob(WSGetBackgroundJob wsBackgroundJobGet)
    throws RemoteException {
	   return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getBackgroundJob(wsBackgroundJobGet);
    }
	    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
    public WSBackgroundJobPKArray findBackgroundJobPKs(WSFindBackgroundJobPKs wsFindBackgroundJobPKs)
    throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().findBackgroundJobPKs(wsFindBackgroundJobPKs);
    }

    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */    
	public WSBackgroundJobPK putBackgroundJob(WSPutBackgroundJob wsputjob)
			throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putBackgroundJob(wsputjob);
	}


	
	/***************************************************************************
	 * Routing Order V2
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2 getRoutingOrderV2(WSGetRoutingOrderV2 wsGetRoutingOrder) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getRoutingOrderV2(wsGetRoutingOrder);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2 existsRoutingOrderV2(WSExistsRoutingOrderV2 wsExistsRoutingOrder) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsRoutingOrderV2(wsExistsRoutingOrder);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2PK deleteRoutingOrderV2(WSDeleteRoutingOrderV2 wsDeleteRoutingOrder) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteRoutingOrderV2(wsDeleteRoutingOrder);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2PK executeRoutingOrderV2Asynchronously(WSExecuteRoutingOrderV2Asynchronously wsExecuteRoutingOrderAsynchronously) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().executeRoutingOrderV2Asynchronously(wsExecuteRoutingOrderAsynchronously);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSString executeRoutingOrderV2Synchronously(WSExecuteRoutingOrderV2Synchronously wsExecuteRoutingOrderSynchronously) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().executeRoutingOrderV2Synchronously(wsExecuteRoutingOrderSynchronously);
	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2PKArray getRoutingOrderV2PKsByCriteria(WSGetRoutingOrderV2PKsByCriteria wsGetRoutingOrderV2PKsByCriteria) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getRoutingOrderV2PKsByCriteria(wsGetRoutingOrderV2PKsByCriteria);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingOrderV2Array getRoutingOrderV2SByCriteria(WSGetRoutingOrderV2SByCriteria wsGetRoutingOrderV2SByCriteria) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getRoutingOrderV2SByCriteria(wsGetRoutingOrderV2SByCriteria);
	}
	

	/***************************************************************************
	 * Routing Engine V2
	 * **************************************************************************/
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingRulePKArray routeItemV2(WSRouteItemV2 wsRouteItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().routeItemV2(wsRouteItem);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSRoutingEngineV2Status routingEngineV2Action(WSRoutingEngineV2Action wsRoutingEngineAction) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().routingEngineV2Action(wsRoutingEngineAction);
	}
	
	/***************************************************************************
	 * Universe
	 * **************************************************************************/
		
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSUniversePK deleteUniverse(WSDeleteUniverse wsUniverseDelete) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteUniverse(wsUniverseDelete);
	}
    

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSUniverse getUniverse(WSGetUniverse wsGetUniverse) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getUniverse(wsGetUniverse);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsUniverse(WSExistsUniverse wsExistsUniverse) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsUniverse(wsExistsUniverse);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSUniversePKArray getUniverseByRevision(WSGetUniverseByRevision revision) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getUniverseByRevision(revision);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSUniversePKArray getUniversePKs(WSGetUniversePKs regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getUniversePKs(regex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSUniversePK putUniverse(WSPutUniverse wsUniverse) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putUniverse(wsUniverse);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSStringArray getObjectsForUniverses(WSGetObjectsForUniverses wsUniverseDelete) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getObjectsForUniverses(wsUniverseDelete);
	}

    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */	
	public WSUniverse getCurrentUniverse(WSGetCurrentUniverse wsGetCurrentUniverse) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getCurrentUniverse(wsGetCurrentUniverse);
	}
	
		
	/***************************************************************************
	 * SynchronizationPlan
	 * **************************************************************************/
		
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSSynchronizationPlanPK deleteSynchronizationPlan(WSDeleteSynchronizationPlan wsSynchronizationPlanDelete) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteSynchronizationPlan(wsSynchronizationPlanDelete);
	}
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSSynchronizationPlan getSynchronizationPlan(WSGetSynchronizationPlan wsGetSynchronizationPlan) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getSynchronizationPlan(wsGetSynchronizationPlan);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsSynchronizationPlan(WSExistsSynchronizationPlan wsExistsSynchronizationPlan) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsSynchronizationPlan(wsExistsSynchronizationPlan);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSSynchronizationPlanPKArray getSynchronizationPlanPKs(WSGetSynchronizationPlanPKs regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getSynchronizationPlanPKs(regex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSSynchronizationPlanPK putSynchronizationPlan(WSPutSynchronizationPlan wsSynchronizationPlan) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putSynchronizationPlan(wsSynchronizationPlan);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSStringArray getObjectsForSynchronizationPlans(WSGetObjectsForSynchronizationPlans wsSynchronizationPlanDelete) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getObjectsForSynchronizationPlans(wsSynchronizationPlanDelete);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSStringArray getSynchronizationPlanObjectsAlgorithms(WSGetSynchronizationPlanObjectsAlgorithms regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getSynchronizationPlanObjectsAlgorithms(regex);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSStringArray getSynchronizationPlanItemsAlgorithms(WSGetSynchronizationPlanItemsAlgorithms regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getSynchronizationPlanItemsAlgorithms(regex);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSSynchronizationPlanStatus synchronizationPlanAction(WSSynchronizationPlanAction wsSynchronizationPlan) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().synchronizationPlanAction(wsSynchronizationPlan);
	}

	
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSStringArray synchronizationGetUnsynchronizedObjectsIDs(WSSynchronizationGetUnsynchronizedObjectsIDs wsSynchronizationGetUnsynchronizedObjectsIDs) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().synchronizationGetUnsynchronizedObjectsIDs(wsSynchronizationGetUnsynchronizedObjectsIDs);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSString synchronizationGetObjectXML(WSSynchronizationGetObjectXML wsSynchronizationGetObjectXML) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().synchronizationGetObjectXML(wsSynchronizationGetObjectXML);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSString synchronizationPutObjectXML(WSSynchronizationPutObjectXML wsSynchronizationPutObjectXML) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().synchronizationPutObjectXML(wsSynchronizationPutObjectXML);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSItemPKArray synchronizationGetUnsynchronizedItemPKs(WSSynchronizationGetUnsynchronizedItemPKs wsSynchronizationGetUnsynchronizedItemPKs) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().synchronizationGetUnsynchronizedItemPKs(wsSynchronizationGetUnsynchronizedItemPKs);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSString synchronizationGetItemXML(WSSynchronizationGetItemXML wsSynchronizationGetItemXML) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().synchronizationGetItemXML(wsSynchronizationGetItemXML);
	}
	
    /**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
     */
	public WSItemPK synchronizationPutItemXML(WSSynchronizationPutItemXML wsSynchronizationPutItemXML) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().synchronizationPutItemXML(wsSynchronizationPutItemXML);
	}
	
	
	
	/***************************************************************************
	 * Synchronization Item
	 * **************************************************************************/
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSSynchronizationItemPK deleteSynchronizationItem(WSDeleteSynchronizationItem wsSynchronizationItemDelete) throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteSynchronizationItem(wsSynchronizationItemDelete);
	}
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSSynchronizationItem getSynchronizationItem(WSGetSynchronizationItem wsGetSynchronizationItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getSynchronizationItem(wsGetSynchronizationItem);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsSynchronizationItem(WSExistsSynchronizationItem wsExistsSynchronizationItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().existsSynchronizationItem(wsExistsSynchronizationItem);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSSynchronizationItemPKArray getSynchronizationItemPKs(WSGetSynchronizationItemPKs regex) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getSynchronizationItemPKs(regex);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSSynchronizationItemPK putSynchronizationItem(WSPutSynchronizationItem wsSynchronizationItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putSynchronizationItem(wsSynchronizationItem);
	}
	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSSynchronizationItem resolveSynchronizationItem(WSResolveSynchronizationItem wsResolveSynchronizationItem) throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().resolveSynchronizationItem(wsResolveSynchronizationItem);
	}
	
	
	/***************************************************************************
	 * 
	 * 
	 *   D E P R E C A T E D    S T U F F
	 * 
	 * 
	 * **************************************************************************/
	
	
	
	/***************************************************************************
	 * Transformer DEPRECATED
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSTransformerPK deleteTransformer(WSDeleteTransformer wsTransformerDelete) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
			return
				new WSTransformerPK(
					ctrl.removeTransformer(
						new TransformerPOJOPK(
								wsTransformerDelete.getWsTransformerPK().getPk()
						)
					).getUniqueId()
				);
	
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
    


	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformer getTransformer(WSGetTransformer wsGetTransformer) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
			TransformerPOJO pojo =
				ctrl.getTransformer(
					new TransformerPOJOPK(
							wsGetTransformer.getWsTransformerPK().getPk()
					)
				);
			return POJO2WS(pojo);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	protected WSTransformer POJO2WS(TransformerPOJO transformerPOJO) throws Exception{
		WSTransformer ws = new WSTransformer();
		ws.setName(transformerPOJO.getName());
		ws.setDescription(transformerPOJO.getDescription());
		ArrayList<WSTransformerPluginSpec> wsSpecs = new ArrayList<WSTransformerPluginSpec>();
		ArrayList< TransformerPluginSpec> pluginSpecs = transformerPOJO.getPluginSpecs();
		if (pluginSpecs!=null) {
			for (Iterator iter = pluginSpecs.iterator(); iter.hasNext(); ) {
				TransformerPluginSpec pluginSpec = (TransformerPluginSpec) iter.next();
				WSTransformerPluginSpec wsSpec = new WSTransformerPluginSpec(
						pluginSpec.getPluginJNDI(),
						pluginSpec.getDescription(),
						pluginSpec.getInput(),
						pluginSpec.getOutput(),
						pluginSpec.getParameters()
				);
				wsSpecs.add(wsSpec);
			}
		}
		ws.setPluginSpecs(wsSpecs.toArray(new WSTransformerPluginSpec[wsSpecs.size()]));
		return ws;
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean existsTransformer(WSExistsTransformer wsExistsTransformer) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
			TransformerPOJO pojo =
				ctrl.existsTransformer(
					new TransformerPOJOPK(
							wsExistsTransformer.getWsTransformerPK().getPk()
					)
				);
			return new WSBoolean(pojo!=null);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPKArray getTransformerPKs(WSGetTransformerPKs regex) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
			Collection c =
				ctrl.getTransformerPKs(
					regex.getRegex()
				);
			if (c==null) return null;
			WSTransformerPK[] pks = new WSTransformerPK[c.size()];
			int i=0;
			for (Iterator iter = c.iterator(); iter.hasNext(); ) {
				pks[i++] = new WSTransformerPK(
						((TransformerPOJOPK) iter.next()).getUniqueId()
				);
			}
			return new WSTransformerPKArray(pks);
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSTransformerPK putTransformer(WSPutTransformer wsTransformer) throws RemoteException {
		try {
			TransformerCtrlLocal ctrl = Util.getTransformerCtrlLocal();
			TransformerPOJOPK pk =
				ctrl.putTransformer(
					WS2POJO(wsTransformer.getWsTransformer())
				);
			return new WSTransformerPK(pk.getUniqueId());
		} catch (Exception e) {
			throw new RemoteException(e.getClass().getName()+": "+e.getLocalizedMessage());
		}
	}
	protected TransformerPOJO WS2POJO(WSTransformer wsTransformer) throws Exception{
		TransformerPOJO pojo = new TransformerPOJO();
		pojo.setName(wsTransformer.getName());
		pojo.setDescription(wsTransformer.getDescription());
		ArrayList<TransformerPluginSpec> specs = new ArrayList<TransformerPluginSpec>();
		WSTransformerPluginSpec[] wsSpecs = wsTransformer.getPluginSpecs();
		if (wsSpecs!=null) {
			for (int i = 0; i < wsSpecs.length; i++) {
				TransformerPluginSpec spec = new TransformerPluginSpec(
						wsSpecs[i].getPluginJNDI(),
						wsSpecs[i].getDescription(),
						wsSpecs[i].getInput(),
						wsSpecs[i].getOutput(),
						wsSpecs[i].getParameters()
				);
				specs.add(spec);		
			}
		}
		pojo.setPluginSpecs(specs);

		return pojo;
	}  
	protected HashMap<String, String> WS2POJO(WSOutputDecisionTable table) {
		HashMap<String, String> decisions = new HashMap<String, String>();
		if ((table == null) || (table.getDecisions()==null) || (table.getDecisions().length == 0)) return decisions;
		WSProcessBytesUsingTransformerWsOutputDecisionTableDecisions[] wsDecisions = table.getDecisions();
		for (int i = 0; i < wsDecisions.length; i++) {
			String name = wsDecisions[i].getOutputVariableName();
			if (name == null) name = TransformerCtrlBean.DEFAULT_VARIABLE;
			decisions.put(name, wsDecisions[i].getDecision());
		}
		return decisions;
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSPipeline processBytesUsingTransformer(WSProcessBytesUsingTransformer wsProjectBytes) throws RemoteException {
    	try {	
			TransformerPluginContext context = 
				Util.getTransformerCtrlLocal().process(
						new com.amalto.core.util.TypedContent(
								null,
								wsProjectBytes.getWsBytes().getBytes(),
								wsProjectBytes.getContentType()
						),
						new TransformerPOJOPK(wsProjectBytes.getWsTransformerPK().getPk()),
						WS2POJO(wsProjectBytes.getWsOutputDecisionTable())
				);
			HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			//Add the Item PKs to the pipeline as comma seperated lines
			String pksAsLine = "";
			Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>)context.get(TransformerCtrlBean.CTX_PKS);
			for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
				ItemPOJOPK pk = (ItemPOJOPK) iter.next();
				if(!"".equals(pksAsLine)) pksAsLine += "\n";
				pksAsLine += pk.getConceptName()+","+Util.joinStrings(pk.getIds(), ",");
			}
			pipeline.put(
					TransformerCtrlBean.CTX_PKS, 
					new com.amalto.core.util.TypedContent(
							null,
							pksAsLine.getBytes("UTF-8"),
							"text/plain; charset=\"utf-8\""
					)
			);
			//return the pipeline
			return POJO2WSOLD(pipeline);
			
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSPipeline processFileUsingTransformer(WSProcessFileUsingTransformer wsProcessFile) throws RemoteException {
    	try {
    		//read the entire file into bytes
    		
			TransformerPluginContext context = 
				Util.getTransformerCtrlLocal().process(
						new com.amalto.core.util.TypedContent(
								new FileInputStream(new File(wsProcessFile.getFileName())),
								null,
								wsProcessFile.getContentType()
						),
						new TransformerPOJOPK(wsProcessFile.getWsTransformerPK().getPk()),
						WS2POJO(wsProcessFile.getWsOutputDecisionTable())
				);
			HashMap<String, com.amalto.core.util.TypedContent> pipeline = (HashMap<String, com.amalto.core.util.TypedContent>)context.get(TransformerCtrlBean.CTX_PIPELINE);
			//Add the Item PKs to the pipeline as comma seperated lines
			String pksAsLine = "";
			Collection<ItemPOJOPK> pks = (Collection<ItemPOJOPK>)context.get(TransformerCtrlBean.CTX_PKS);
			for (Iterator iter = pks.iterator(); iter.hasNext(); ) {
				ItemPOJOPK pk = (ItemPOJOPK) iter.next();
				if(!"".equals(pksAsLine)) pksAsLine += "\n";
				pksAsLine += pk.getConceptName()+","+Util.joinStrings(pk.getIds(), ",");
			}
			pipeline.put(
					TransformerCtrlBean.CTX_PKS, 
					new com.amalto.core.util.TypedContent(
							null,
							pksAsLine.getBytes("UTF-8"),
							"text/plain; charset=\"utf-8\""
					)
			);
			//return the pipeline
			return POJO2WSOLD(pipeline);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	protected WSPipeline POJO2WSOLD(HashMap<String,com.amalto.core.util.TypedContent> pipeline) throws Exception{
		ArrayList<WSPipelineTypedContentEntry> entries = new ArrayList<WSPipelineTypedContentEntry>();
		Set keys = pipeline.keySet();
		for (Iterator iter = keys.iterator(); iter.hasNext(); ) {
			String output = (String) iter.next();
			com.amalto.core.util.TypedContent content = pipeline.get(output);
			byte[] bytes = content.getBytes();
			if (bytes == null) {
				ByteArrayOutputStream bos = new ByteArrayOutputStream();
				int c;
				while ((c=content.getStream().read())!=-1) bos.write(c);
				bytes = bos.toByteArray();
			}
			WSExtractedContent wsContent = new WSExtractedContent(
					new WSByteArray(bytes),
					content.getContentType()
			);
			WSPipelineTypedContentEntry wsEntry = new WSPipelineTypedContentEntry(
					TransformerCtrlBean.DEFAULT_VARIABLE.equals(output) ? "" : output,
					wsContent
			);
			entries.add(wsEntry);
		}
		return new WSPipeline(entries.toArray(new WSPipelineTypedContentEntry[entries.size()]));
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK processBytesUsingTransformerAsBackgroundJob(WSProcessBytesUsingTransformerAsBackgroundJob wsProcessBytesUsingTransformerAsBackgroundJob) throws RemoteException {
    	try {
			return new WSBackgroundJobPK(
				Util.getTransformerCtrlLocal().processBytesAsBackgroundJob(
						wsProcessBytesUsingTransformerAsBackgroundJob.getWsBytes().getBytes(),
						wsProcessBytesUsingTransformerAsBackgroundJob.getContentType(),
						new TransformerPOJOPK(wsProcessBytesUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
						WS2POJO(wsProcessBytesUsingTransformerAsBackgroundJob.getWsOutputDecisionTable())
				).getUniqueId()
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBackgroundJobPK processFileUsingTransformerAsBackgroundJob(WSProcessFileUsingTransformerAsBackgroundJob wsProcessFileUsingTransformerAsBackgroundJob) throws RemoteException {
    	try {
			return new WSBackgroundJobPK(
				Util.getTransformerCtrlLocal().processFileAsBackgroundJob(
						wsProcessFileUsingTransformerAsBackgroundJob.getFileName(),
						wsProcessFileUsingTransformerAsBackgroundJob.getContentType(),
						new TransformerPOJOPK(wsProcessFileUsingTransformerAsBackgroundJob.getWsTransformerPK().getPk()),
						WS2POJO(wsProcessFileUsingTransformerAsBackgroundJob.getWsOutputDecisionTable())
				).getUniqueId()
			);
		} catch (XtentisException e) {
			throw(new RemoteException(e.getLocalizedMessage()));
		} catch (Exception e) {
			throw new RemoteException((e.getCause() == null ? e.getLocalizedMessage() : e.getCause().getLocalizedMessage()));
		}
	}
	
	/***************************************************************************
	 * Drop Item
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItemPKArray findAllDroppedItemsPKs(WSFindAllDroppedItemsPKs regex)
			throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().findAllDroppedItemsPKs(regex);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItem loadDroppedItem(WSLoadDroppedItem wsLoadDroppedItem)
			throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().loadDroppedItem(wsLoadDroppedItem);
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSItemPK recoverDroppedItem(WSRecoverDroppedItem wsRecoverDroppedItem)
			throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().recoverDroppedItem(wsRecoverDroppedItem);
 
	}

	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSDroppedItemPK removeDroppedItem(WSRemoveDroppedItem wsRemoveDroppedItem)
			throws RemoteException {
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().removeDroppedItem(wsRemoveDroppedItem);
	}
	
	
	/***************************************************************************
	 * Workflow
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSWorkflowProcessDefinitionUUIDArray workflowGetProcessDefinitions(WSWorkflowGetProcessDefinitions wsWorkflowGetProcessDefinitions) throws RemoteException{
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().workflowGetProcessDefinitions(wsWorkflowGetProcessDefinitions);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSWorkflowProcessDefinitionUUID workflowDeploy(WSWorkflowDeploy deploy) throws RemoteException{
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().workflowDeploy(deploy);
	}
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSBoolean workflowUnDeploy(WSWorkflowUnDeploy undeploy) throws RemoteException{
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().workflowUnDeploy(undeploy);
	}	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSProcessInstanceArray workflowGetProcessInstances(WSWorkflowGetProcessInstances instance) throws RemoteException{
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().workflowGetProcessInstances(instance);
	}	
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
    public WSBoolean workflowDeleteProcessInstances(WSWorkflowDeleteProcessInstancesRequest deleteWolkflowRequest) throws RemoteException{
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().workflowDeleteProcessInstances(deleteWolkflowRequest);
    }
	
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */
	public WSProcessTaskInstanceArray workflowGetTaskList(WSWorkflowGetTaskList tasklist) throws RemoteException{
		return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().workflowGetTaskList(tasklist);
	}	
	/***************************************************************************
	 * Job
	 * **************************************************************************/
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
    public WSBoolean putMDMJob(WSPUTMDMJob job)throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().putMDMJob(job);
    }
	   
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
    public WSBoolean deleteMDMJob(WSDELMDMJob job)throws RemoteException {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().deleteMDMJob(job);
    }
    
    
	/**
	 * @ejb.interface-method view-type = "service-endpoint"
	 * @ejb.permission 
	 * 	role-name = "authenticated"
	 * 	view-type = "service-endpoint"
	 */	
    
    public WSMDMJobArray getMDMJob(WSMDMNULL job)throws RemoteException
    {
    	return BeanDelegatorContainer.getUniqueInstance().getXtentisWSBeanDelegator().getMDMJob(job);
    }
}
