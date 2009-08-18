package com.amalto.workbench.providers;

import java.lang.reflect.InvocationTargetException;
import java.net.URL;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import com.amalto.workbench.models.TreeObject;
import com.amalto.workbench.models.TreeParent;
import com.amalto.workbench.utils.EXtentisObjects;
import com.amalto.workbench.utils.IConstants;
import com.amalto.workbench.utils.UserInfo;
import com.amalto.workbench.utils.Util;
import com.amalto.workbench.utils.XtentisException;
import com.amalto.workbench.webservices.WSComponent;
import com.amalto.workbench.webservices.WSDataClusterPK;
import com.amalto.workbench.webservices.WSDataModelPK;
import com.amalto.workbench.webservices.WSGetComponentVersion;
import com.amalto.workbench.webservices.WSGetCurrentUniverse;
import com.amalto.workbench.webservices.WSGetMenuPKs;
import com.amalto.workbench.webservices.WSGetRolePKs;
import com.amalto.workbench.webservices.WSGetRoutingRulePKs;
import com.amalto.workbench.webservices.WSGetSynchronizationPlanPKs;
import com.amalto.workbench.webservices.WSGetTransformerV2PKs;
import com.amalto.workbench.webservices.WSGetUniversePKs;
import com.amalto.workbench.webservices.WSGetViewPKs;
import com.amalto.workbench.webservices.WSMenuPK;
import com.amalto.workbench.webservices.WSPing;
import com.amalto.workbench.webservices.WSRegexDataClusterPKs;
import com.amalto.workbench.webservices.WSRegexDataModelPKs;
import com.amalto.workbench.webservices.WSRegexStoredProcedure;
import com.amalto.workbench.webservices.WSRolePK;
import com.amalto.workbench.webservices.WSRoutingRulePK;
import com.amalto.workbench.webservices.WSStoredProcedurePK;
import com.amalto.workbench.webservices.WSSynchronizationPlanPK;
import com.amalto.workbench.webservices.WSTransformerV2PK;
import com.amalto.workbench.webservices.WSUniverse;
import com.amalto.workbench.webservices.WSUniversePK;
import com.amalto.workbench.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.workbench.webservices.WSVersion;
import com.amalto.workbench.webservices.WSViewPK;
import com.amalto.workbench.webservices.XtentisPort;

public class XtentisServerObjectsRetriever implements IRunnableWithProgress {

	//ServerView view;
	private String endpointaddress;
	private String username;
	private String password;
	private String universe;
    private TreeParent serverRoot;
	
    private boolean isExistUniverse=true;
    
	public XtentisServerObjectsRetriever(String endpointaddress, String username, String password, String universe) {
		this.endpointaddress = endpointaddress;
		this.username = username;
		this.password = password;
		this.universe=universe;
	}
	
	public boolean isExistUniverse() {
		return isExistUniverse;
	}

	public synchronized void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		try {
			monitor.beginTask("Loading "+IConstants.TALEND+" Server Objects", "admin".equals(username)? 12 : 9);
			//Access to server and get port
			XtentisPort port = Util.getPort(new URL(endpointaddress), universe, username, password);
			port.ping(new WSPing("Hello MDM!"));
			monitor.worked(1);
			
			
			String displayName=endpointaddress;
			//fetch version info
			try {
				WSVersion version = port.getComponentVersion(new WSGetComponentVersion(WSComponent.DataManager,null));				
				displayName += " (v"+version.getMajor()+"."+version.getMinor()+"."+version.getRevision()+"_"+version.getBuild()+")";	
			} catch (Exception e) {
				e.printStackTrace();
			}
			WSUniverse wUuniverse=null;
			wUuniverse=port.getCurrentUniverse(new WSGetCurrentUniverse());
			if (monitor.isCanceled()) throw new InterruptedException("User Cancel");		
		
			//server
			serverRoot = new TreeParent(
                    displayName,
                    null,
                    TreeObject._SERVER_,
                    endpointaddress,
                    ("".equals(universe)? "" : universe+"/")+username+":"+(password==null?"":password)
            );       
			
			monitor.subTask("Accessing server....");
			UserInfo user=new UserInfo();
			user.setUsername(username);
			user.setPassword(password);
			user.setServerUrl(endpointaddress);
			user.setUniverse(universe);
			user.setWsUuniverse(wUuniverse);

			serverRoot.setUser(user);
			
			
			//Data Models
			TreeParent models = new TreeParent(EXtentisObjects.DataMODEL.getDisplayName(),serverRoot,TreeObject.DATA_MODEL,null,null);			
			//WSDataModel[] xdm = port.getDataModels(new WSRegexDataModels("*")).getWsDataModels();
			WSDataModelPK[] xdmPKs = port.getDataModelPKs(new WSRegexDataModelPKs("")).getWsDataModelPKs();
			if (xdmPKs != null) {
				monitor.subTask("Loading Data Models");
				for (int i = 0; i < xdmPKs.length; i++) {
					String name = xdmPKs[i].getPk();
					if (!name.startsWith("XMLSCHEMA")) {
							TreeObject obj = new TreeObject(
									name,
									serverRoot,
									TreeObject.DATA_MODEL,
									xdmPKs[i],
									null   //no storage to save space
							);
							models.addChild(obj);
					}
				}
			}
			monitor.worked(1);
			if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			
			//DataClusters
			TreeParent dataClusters = new TreeParent(EXtentisObjects.DataCluster.getDisplayName(),serverRoot,TreeObject.DATA_CLUSTER,null,null);
			//WSDataCluster[] xdc = port.getDataClusters(new WSRegexDataClusters("*")).getWsDataClusters();
			WSDataClusterPK[] xdcPKs = port.getDataClusterPKs(new WSRegexDataClusterPKs("")).getWsDataClusterPKs();
			if (xdcPKs != null) {
				monitor.subTask("Loading Data Clusters");
				for (int i = 0; i < xdcPKs.length; i++) {
					String name = xdcPKs[i].getPk();
					if (! (
							"CACHE".equals(name)
							)
						) {	//FIXME: Hardcoded CACHE
						TreeObject obj = new TreeObject(
								name,
								serverRoot,
								TreeObject.DATA_CLUSTER,
								xdcPKs[i],
								null   //no storage to save space
						);
						dataClusters.addChild(obj);
					}
				}
			}
			monitor.worked(1);
			if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			


			//Views
			TreeParent views = new TreeParent(EXtentisObjects.View.getDisplayName(),serverRoot,TreeObject.VIEW,null,null);
			WSViewPK[] viewPKs = port.getViewPKs((new WSGetViewPKs(""))).getWsViewPK();
			if (viewPKs!=null) {
				monitor.subTask("Loading Views");
				for (int i = 0; i < viewPKs.length; i++) {
					String name = viewPKs[i].getPk();
					TreeObject obj = new TreeObject(
							name,
							serverRoot,
							TreeObject.VIEW,
							new WSViewPK(name),
							null   //no storage to save space
					);
					views.addChild(obj);
				}
			}
			monitor.worked(1);
			if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			

			//Stored Procedures
			TreeParent storedProcedures = new TreeParent(EXtentisObjects.StoredProcedure.getDisplayName(),serverRoot,TreeObject.STORED_PROCEDURE,null,null);
			WSStoredProcedurePK[] spk = port.getStoredProcedurePKs(new WSRegexStoredProcedure("")).getWsStoredProcedurePK();
			if (spk!=null) {
				monitor.subTask("Loading Stored Procedures");
				for (int i = 0; i < spk.length; i++) {
					String name = spk[i].getPk();
					TreeObject obj = new TreeObject(
							name,
							serverRoot,
							TreeObject.STORED_PROCEDURE,
							new WSStoredProcedurePK(name),
							null   //no storage to save space
					);
					storedProcedures.addChild(obj);
				}
			}
			monitor.worked(1);
			if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			
//			Roles
			WSRolePK[] rolePKs = null;
			boolean hasRoles = true;
			try {
				rolePKs = port.getRolePKs(new WSGetRolePKs("*")).getWsRolePK();
			} catch (Exception e) {
				System.out.println("NO ROLES");
				// This server does not handle roles (pre 2.13)
				hasRoles = false;
			}
			TreeParent roles = null;
			if (hasRoles) {
				roles = new TreeParent(EXtentisObjects.Role.getDisplayName(),serverRoot,TreeObject.ROLE,null,null);
				if (rolePKs!=null) {
					monitor.subTask("Loading Roles");
					for (int i = 0; i < rolePKs.length; i++) {
						String name =rolePKs[i].getPk();
						TreeObject obj = new TreeObject(
								name,
								serverRoot,
								TreeObject.ROLE,
								new WSRolePK(name),
								null   //no storage to save space
						);
						roles.addChild(obj);
					}
				}
				monitor.worked(1);
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			}
			

//			Routing Rules
			WSRoutingRulePK[] routingRulePKs = null;
			boolean hasRoutingRules = true;
			try {
				routingRulePKs = port.getRoutingRulePKs(new WSGetRoutingRulePKs("")).getWsRoutingRulePKs();
			} catch (Exception e) {
				System.out.println("NO ROUTING RULES");
				// This server does not handle roles (pre 2.13)
				hasRoutingRules = false;
			}
			TreeParent rules = null;
			if (hasRoutingRules) {
				rules = new TreeParent(EXtentisObjects.RoutingRule.getDisplayName(),serverRoot,TreeObject.ROUTING_RULE,null,null);
				if (routingRulePKs!=null) {
					monitor.subTask("Loading Routing Rules");
					for (int i = 0; i < routingRulePKs.length; i++) {
						String id =routingRulePKs[i].getPk();
						TreeObject obj = new TreeObject(
								id,
								serverRoot,
								TreeObject.ROUTING_RULE,
								new WSRoutingRulePK(id),
								null   //no storage to save space
						);
						rules.addChild(obj);
					}
				}
				monitor.worked(1);
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			}

			
			//Subscription Engine
			TreeObject  engine = new TreeObject(
					EXtentisObjects.SubscriptionEngine.getDisplayName(),
					serverRoot,
					TreeObject.SUBSCRIPTION_ENGINE,
					null,
					null
			);
			monitor.worked(1);
			if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			
			
//			Transformers
			WSTransformerV2PK[] transformerPKs = null;
			boolean hasTransformers = true;
			try {
				
				transformerPKs = port.getTransformerV2PKs(new WSGetTransformerV2PKs("")).getWsTransformerV2PK();
			} catch (Exception e) {
				System.out.println("No Transformers");
				// This server IS old
				hasTransformers = false;
			}
			TreeParent transformers = null;
			if (hasTransformers) {
				transformers = new TreeParent(EXtentisObjects.Transformer.getDisplayName(),serverRoot,TreeObject.TRANSFORMER,null,null);
				if (transformerPKs!=null) {
					monitor.subTask("Loading Transfomers");
					for (int i = 0; i < transformerPKs.length; i++) {
						String id =transformerPKs[i].getPk();
						TreeObject obj = new TreeObject(
								id,
								serverRoot,
								TreeObject.TRANSFORMER,
								new WSTransformerV2PK(id),
								null   //no storage to save space
						);
						transformers.addChild(obj);
					}
				}
				monitor.worked(1);
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			}
			
//			Menus
			WSMenuPK[] menuPKs = null;
			boolean hasMenus = true;
			try {
				menuPKs = port.getMenuPKs(new WSGetMenuPKs("*")).getWsMenuPK();
			} catch (Exception e) {
				System.out.println("No Menus");
				// This server IS old
				hasMenus = false;
			}
			TreeParent menus = null;
			if (hasMenus) {
				menus = new TreeParent(EXtentisObjects.Menu.getDisplayName(),serverRoot,TreeObject.MENU,null,null);
				if (menuPKs!=null) {
					monitor.subTask("Loading Menus");
					for (int i = 0; i < menuPKs.length; i++) {
						String id =menuPKs[i].getPk();
						TreeObject obj = new TreeObject(
								id,
								serverRoot,
								TreeObject.MENU,
								new WSMenuPK(id),
								null   //no storage to save space
						);
						menus.addChild(obj);
					}
				}
				monitor.worked(1);
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			}

//			Universe
			WSUniversePK[] universePKs = null;
			//boolean hasUniverses = true;
			try {
				universePKs = port.getUniversePKs(new WSGetUniversePKs("*")).getWsUniversePK();
			} catch (Exception e) {
				System.out.println("No Universes");
				// This server IS old
				//hasUniverses = false;
			}
			TreeParent Universes = null;
			//if (hasUniverses) {
				Universes = new TreeParent(EXtentisObjects.Universe.getDisplayName(),serverRoot,TreeObject.UNIVERSE,null,null);
				if (universePKs!=null) {
					monitor.subTask("Loading Universes");
					for (int i = 0; i < universePKs.length; i++) {
						String id =universePKs[i].getPk();
						TreeObject obj = new TreeObject(
								id,
								serverRoot,
								TreeObject.UNIVERSE,
								new WSUniversePK(id),
								null   //no storage to save space
						);
						Universes.addChild(obj);
					}
				}
				monitor.worked(1);
				if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			//}
				
//				SynchronizationPlan
				WSSynchronizationPlanPK[] SynchronizationPlanPKs = null;
				//boolean hasSynchronizationPlans = true;
				try {
					SynchronizationPlanPKs = port.getSynchronizationPlanPKs(new WSGetSynchronizationPlanPKs("*")).getWsSynchronizationPlanPK();
				} catch (Exception e) {
					System.out.println("No SynchronizationPlans");
					// This server IS old
					//hasSynchronizationPlans = false;
				}
				TreeParent synchronizationPlans = null;
				//if (hasSynchronizationPlans) {
				synchronizationPlans = new TreeParent(EXtentisObjects.SynchronizationPlan.getDisplayName(),serverRoot,TreeObject.SYNCHRONIZATIONPLAN,null,null);
					if (SynchronizationPlanPKs!=null) {
						monitor.subTask("Loading SynchronizationPlans");
						for (int i = 0; i < SynchronizationPlanPKs.length; i++) {
							String id =SynchronizationPlanPKs[i].getPk();
							TreeObject obj = new TreeObject(
									id,
									serverRoot,
									TreeObject.SYNCHRONIZATIONPLAN,
									new WSSynchronizationPlanPK(id),
									null   //no storage to save space
							);
							synchronizationPlans.addChild(obj);
						}
					}
					monitor.worked(1);
					if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
				//}

			serverRoot.addChild(models);
			serverRoot.addChild(dataClusters);
			serverRoot.addChild(views);
			serverRoot.addChild(storedProcedures);
			serverRoot.addChild(engine);
			serverRoot.addChild(Universes);
			serverRoot.addChild(synchronizationPlans);
			if (hasTransformers)serverRoot.addChild(transformers);
			if (hasRoles) serverRoot.addChild(roles);
			if (hasRoutingRules) serverRoot.addChild(rules);
			if (hasMenus) serverRoot.addChild(menus);
			

			
			addRevision(wUuniverse);
			
			monitor.done();			
		} catch (Exception e) {
			if (monitor.isCanceled()) throw new InterruptedException("User Cancel");
			e.printStackTrace();
			throw new InvocationTargetException(new XtentisException("Could not login: "+e.getLocalizedMessage()));
		}		
	}//run
	
	/**
	 * add revisionID to each treeobject
	 * @param universe
	 */
	private void addRevision(WSUniverse universe){

		WSUniverseXtentisObjectsRevisionIDs[] ids=universe.getXtentisObjectsRevisionIDs();
		for(TreeObject node: serverRoot.getChildren()){
			EXtentisObjects object=EXtentisObjects.getXtentisObjexts().get(String.valueOf(node.getType()));
			if(object==null || !object.isRevision()){
				continue;
			}
			boolean isSet=false;
			for(WSUniverseXtentisObjectsRevisionIDs id: ids){					
				if(id.getXtentisObjectName().equals(object.getName())){
					if(id.getRevisionID()!=null && id.getRevisionID().length()>0){
						node.setDisplayName(node.getDisplayName() + " ["+id.getRevisionID().replaceAll("\\[", "").replaceAll("\\]", "").trim() +"]");
					}else{
						node.setDisplayName(node.getDisplayName() + " ["+IConstants.HEAD+"]");
					}
					isSet=true;
					break;
				}
			}
			if(!isSet){
				node.setDisplayName(node.getDisplayName() + " ["+IConstants.HEAD+"]");
			}
		}	
		String name = serverRoot.getDisplayName()+" ["+universe.getName().replaceAll("\\[", "").replaceAll("\\]", "").trim()+"]" +" " +username;
		serverRoot.setDisplayName(name);		
	}
    public TreeParent getServerRoot() {
        return serverRoot;
    }
}
