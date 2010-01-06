package com.amalto.webapp.v3.universemanager.dwr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.menu.ejb.MenuPOJO;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.routing.v2.ejb.RoutingRulePOJO;
import com.amalto.core.objects.storedprocedure.ejb.StoredProcedurePOJO;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.webapp.core.bean.ComboItemBean;
import com.amalto.webapp.core.bean.ListRange;
import com.amalto.webapp.core.util.Util;
import com.amalto.webapp.core.util.XtentisWebappException;
import com.amalto.webapp.util.webservices.WSPutSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSPutUniverse;
import com.amalto.webapp.util.webservices.WSSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanAction;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanActionCode;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanPK;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanStatus;
import com.amalto.webapp.util.webservices.WSUniverse;
import com.amalto.webapp.util.webservices.WSUniverseXtentisObjectsRevisionIDs;
import com.amalto.webapp.v3.universemanager.bean.RoleEntry;
import com.amalto.webapp.v3.universemanager.bean.RoutingRuleEntry;
import com.amalto.webapp.v3.universemanager.bean.StoredProcedureEntry;
import com.amalto.webapp.v3.universemanager.bean.SyncStatus;
import com.amalto.webapp.v3.universemanager.bean.ToCopyItemEntry;
import com.amalto.webapp.v3.universemanager.bean.TransformEntry;
import com.amalto.webapp.v3.universemanager.bean.UniverseQuickStartStore;
import com.amalto.webapp.v3.universemanager.bean.UniverseSummaryEntry;
import com.amalto.webapp.v3.universemanager.util.QuickStartWizzardHelper;
import com.amalto.webapp.v3.universemanager.util.UniverseHandler;

public class UniverseManagerDWR {
	 
	 public static final String DEFAULT_URL="http://localhost:8080/talend/TalendPort";
		 
	 public static final String DEFAULT_USERNAME="admin";
    	 
	 public static final String DEFAULT_PASSWORD="talend";	 
	
     public ListRange getAllUniversePksList() throws XtentisWebappException{
		
		ListRange listRange = new ListRange();
		
		try {
			
			List<ComboItemBean> items = new ArrayList<ComboItemBean>();
			items.add(new ComboItemBean("[HEAD]", "[HEAD]"));
			String[] universePks = UniverseHandler.getAllUniversePks();
			
			if(universePks!=null){
				for (int i = 0; i < universePks.length; i++) {
					String universePK = universePks[i];
					items.add(new ComboItemBean(universePK, universePK));
				}
			}
			
			listRange.setData(items.toArray());
			listRange.setTotalSize(items.size());
			
		} catch (Exception e) {
			e.printStackTrace();
			throw new XtentisWebappException(e);
		}
		
		return listRange;
		
	}
     
     public UniverseQuickStartStore loadUniverseQuickStartStore() {
    	
    	UniverseQuickStartStore universeQuickStartStore=null; 
    	try {
    		WebContext ctx = WebContextFactory.get();
    		if(QuickStartWizzardHelper.isInTransaction(ctx)){
    			universeQuickStartStore=QuickStartWizzardHelper.getStore(ctx);
    		}else{
    			universeQuickStartStore=QuickStartWizzardHelper.initStore(ctx);
    		}
			return universeQuickStartStore;
		} catch (Exception e) {
			e.printStackTrace();
			//return null;
			return new UniverseQuickStartStore();
		}
		
	}
     
     public void synchroFieldChangesWithStore(String fieldName,String newValue) throws XtentisWebappException{
 		
 		try {
 			
 			QuickStartWizzardHelper.updateStore(WebContextFactory.get(), fieldName, newValue);
 			
 		} catch (Exception e) {
 			e.printStackTrace();
 			throw new XtentisWebappException(e);
 		}
 		
 	}
         
     public String getCurrentUniverseName() throws XtentisWebappException {
    	
    	 try {
   			
    		 return UniverseHandler.getCurrentUniverseName();
   			
   		} catch (Exception e) {
   			e.printStackTrace();
   			throw new XtentisWebappException(e);
   		}
 	}
     
     public void resetUniverseQuickStartStoreAndContext() throws XtentisWebappException {
    	 
    	try {
   			
     		QuickStartWizzardHelper.resetStoreAndContext(WebContextFactory.get());
   			
   		} catch (Exception e) {
   			e.printStackTrace();
   			throw new XtentisWebappException(e);
   		}
     }
     
     public void resetUniverseContextInSession(String newUniverseName) throws XtentisWebappException {
    	 
     	if(newUniverseName!=null&&(newUniverseName.equals("")||newUniverseName.equals("[HEAD]")))newUniverseName=null;

     	try {
   			
     		QuickStartWizzardHelper.updateContext(WebContextFactory.get(), newUniverseName);
   			
   		} catch (Exception e) {
   			e.printStackTrace();
   			throw new XtentisWebappException(e);
   		}

 	}
     
     public void resetUniverseStoreWithoutBasicInfos() throws XtentisWebappException {
    	 
      	try {
    			
      		QuickStartWizzardHelper.refreshStore(WebContextFactory.get(), false);
    			
    	} catch (Exception e) {
    			e.printStackTrace();
    			throw new XtentisWebappException(e);
    	}
  	} 
     
     public ListRange getSourceRoutingRuleList()throws Exception{ 
     	ListRange listRange = new ListRange();
     	String revision=QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(), RoutingRulePOJO.class);
     	String[] routingRules=UniverseHandler.getRoutingRules(revision);
     	
     	List<RoutingRuleEntry> enties=new ArrayList<RoutingRuleEntry>();
     	for (int i = 0; i < routingRules.length; i++) {
     		enties.add(new RoutingRuleEntry(routingRules[i],revision));
		}
 		listRange.setData(enties.toArray(new RoutingRuleEntry[enties.size()]));
 		listRange.setTotalSize(enties.size());
 		
 		return listRange;
 		
     }
     
     public ListRange getSourceTransformList()throws Exception{ 
      	ListRange listRange = new ListRange();
      	String revision=QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(), TransformerV2POJO.class);
      	String[] transforms=UniverseHandler.getTransforms(revision);
      	
      	List<TransformEntry> enties=new ArrayList<TransformEntry>();
      	for (int i = 0; i < transforms.length; i++) {
      		enties.add(new TransformEntry(transforms[i],revision));
 		}
  		listRange.setData(enties.toArray(new TransformEntry[enties.size()]));
  		listRange.setTotalSize(enties.size());
  		
  		return listRange;
  		
      }
     
     public ListRange getSourceRoleList()throws Exception{ 
       	ListRange listRange = new ListRange();
       	String revision=QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(), RolePOJO.class);
       	String[] roles=UniverseHandler.getRoles(revision);
       	
       	List<RoleEntry> enties=new ArrayList<RoleEntry>();
       	for (int i = 0; i < roles.length; i++) {
       		enties.add(new RoleEntry(roles[i],revision));
  		}
   		listRange.setData(enties.toArray(new RoleEntry[enties.size()]));
   		listRange.setTotalSize(enties.size());
   		
   		return listRange;
   		
     }
     
     public ListRange getUniverseSummaryList()throws Exception{ 
        	ListRange listRange = new ListRange();
        	
        	String targetRevision=QuickStartWizzardHelper.getTargetRevisionName(WebContextFactory.get());
        	
        	String itemRevision=targetRevision;
        	String dataModelRevision=cleanRevision(QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(),DataModelPOJO.class));
        	String menuRevision=cleanRevision(QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(),MenuPOJO.class));
        	String roleRevision=targetRevision;
        	String routingRuleRevision=targetRevision;
        	String storedProcedureRevision=targetRevision;
        	String transformerRevision=targetRevision;
        	String viewRevision=cleanRevision(QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(),ViewPOJO.class));
        	
        	List<UniverseSummaryEntry> enties=new ArrayList<UniverseSummaryEntry>();

        	enties.add(new UniverseSummaryEntry("Items",itemRevision));
        	enties.add(new UniverseSummaryEntry("Data Model",dataModelRevision));
        	enties.add(new UniverseSummaryEntry("Menu",menuRevision));
        	enties.add(new UniverseSummaryEntry("Role",roleRevision));
        	enties.add(new UniverseSummaryEntry("Routing rules",routingRuleRevision));
        	enties.add(new UniverseSummaryEntry("Stored Procedure",storedProcedureRevision));
        	enties.add(new UniverseSummaryEntry("Transformer",transformerRevision));
        	enties.add(new UniverseSummaryEntry("View",viewRevision));
   		   
    		listRange.setData(enties.toArray(new UniverseSummaryEntry[enties.size()]));
    		listRange.setTotalSize(enties.size());
    		
    		return listRange;
    		
      }
     
     private String cleanRevision(String input){
    	 
    	 String output=input;
    	 if(output==null||output.equals(""))output="[HEAD]";
    	 
    	 return output;
    	 
     }
     
     public void recordRoleEnties(RoleEntry[] selEnties){ 
    	 List<RoleEntry> selEntiesList = Arrays.asList(selEnties);
    	 if(selEntiesList.size()==0){
    		 QuickStartWizzardHelper.updateStore(WebContextFactory.get(), selEntiesList,true);
    	 }else{
    		 QuickStartWizzardHelper.updateStore(WebContextFactory.get(), selEntiesList,false);
    	 }
    	 
     }
     
     public void recordRoutingRuleEnties(RoutingRuleEntry[] selEnties){ 
    	 List<RoutingRuleEntry> selEntiesList = Arrays.asList(selEnties);
    	 if(selEntiesList.size()==0){
    		 QuickStartWizzardHelper.updateStore4RoutingRule(WebContextFactory.get(), selEntiesList, true);
    	 }else{
    		 QuickStartWizzardHelper.updateStore4RoutingRule(WebContextFactory.get(), selEntiesList, false);
    	 }
     }
     
     public void recordTransformerEnties(TransformEntry[] selEnties){ 
    	 List<TransformEntry> selEntiesList = Arrays.asList(selEnties);
    	 if(selEntiesList.size()==0){
    		 QuickStartWizzardHelper.updateStore4Transformer(WebContextFactory.get(), selEntiesList,true);
    	 }else{
    		 QuickStartWizzardHelper.updateStore4Transformer(WebContextFactory.get(), selEntiesList,false);
    	 }
     }
     
     public void recordSPEntry(boolean checked){ 
    	try {
			 StoredProcedureEntry entry=new StoredProcedureEntry(QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(),StoredProcedurePOJO.class),checked);
			 QuickStartWizzardHelper.updateStore(WebContextFactory.get(), entry);
		} catch (Exception e) {
			e.printStackTrace();
		}
     }
     
     public int initSPEntry(){ 
    	 UniverseQuickStartStore store=QuickStartWizzardHelper.getStore(WebContextFactory.get());
    	 if(store.getStoredProcedureEntry()==null){
    		 return -1;
    	 }else if(store.getStoredProcedureEntry().isChecked()){
    		 return 1;
    	 }else{
    		 return 0;
    	 }
      }
     
     public String[] initRoleEnties(){
    	 UniverseQuickStartStore store=QuickStartWizzardHelper.getStore(WebContextFactory.get());
    	 if(store.getToCopyRoleEntries().size()==0&&!store.isDeselectAllRoles()){
    		 //init
    		 return null;
    	 }else if(store.getToCopyRoleEntries().size()==0&&store.isDeselectAllRoles()){
    		 //deselectAll
    		 return new String[0];
    	 }else {
    		 //init selected
    		 List<RoleEntry> roleEntries = store.getToCopyRoleEntries();
    		 String[] roleIDs=new String[roleEntries.size()];
    		 for (int j = 0; j < roleIDs.length; j++) {
    			RoleEntry roleEntry = (RoleEntry) roleEntries.get(j);
 				roleIDs[j]=roleEntry.getRoleName();
			 }
    		 return roleIDs;
    	 }
     }
     
     public String[] initRoutinngRuleEnties(){
    	 UniverseQuickStartStore store=QuickStartWizzardHelper.getStore(WebContextFactory.get());
    	 if(store.getToCopyRoutingRuleEntries().size()==0&&!store.isDeselectAllRoutingRules()){
    		 return null;
    	 }else if(store.getToCopyRoleEntries().size()==0&&store.isDeselectAllRoutingRules()){
    		 //deselectAll
    		 return new String[0];
    	 }else{
    		 List<RoutingRuleEntry> routingRuleEntries = store.getToCopyRoutingRuleEntries();
    		 String[] routingRuleIDs=new String[routingRuleEntries.size()];
    		 for (int j = 0; j < routingRuleIDs.length; j++) {
    			 RoutingRuleEntry routingRuleEntry = (RoutingRuleEntry) routingRuleEntries.get(j);
    			 routingRuleIDs[j]=routingRuleEntry.getRoutingRuleName();
			 }
    		 return routingRuleIDs;
    	 }
     }
     
     public String[] initTransformerEnties(){
    	 UniverseQuickStartStore store=QuickStartWizzardHelper.getStore(WebContextFactory.get());
    	 if(store.getToCopyTransformerEntries().size()==0&&!store.isDeselectAllTransformers()){
    		 return null;
    	 }else if(store.getToCopyTransformerEntries().size()==0&&store.isDeselectAllTransformers()){
    		 //deselectAll
    		 return new String[0];
    	 }else{
    		 List<TransformEntry> transformEntries = store.getToCopyTransformerEntries();
    		 String[] transformIDs=new String[transformEntries.size()];
    		 for (int j = 0; j < transformIDs.length; j++) {
    			 TransformEntry transformEntry = (TransformEntry) transformEntries.get(j);
    			 transformIDs[j]=transformEntry.getTransformName();
			 }
    		 return transformIDs;
    	 }
     }
     
     public boolean createNewUniverse() throws XtentisWebappException {
    	 try {
	    	 UniverseQuickStartStore store=QuickStartWizzardHelper.getStore(WebContextFactory.get());
	    	 if(store!=null){
	    		// create new universe
		    	WSUniverse wsUniverse=buildUniverse(store);
		    	Util.getPort().putUniverse(new WSPutUniverse(wsUniverse));
	    		// create synchronize plan     
	    		WSSynchronizationPlan wsSynchronizationPlan=store.buildSynchronizationPlan();
	    		Util.getPort().putSynchronizationPlan(new WSPutSynchronizationPlan(wsSynchronizationPlan)); 
		     	// execute synchronize plan 
	    		Util.getPort().synchronizationPlanAction(new WSSynchronizationPlanAction(
	                	new WSSynchronizationPlanPK(wsSynchronizationPlan.getName()),
	                	WSSynchronizationPlanActionCode.START_FULL
	                )); 
		     	
	    		return true;
	    	 }else{
	    		return false;
	    	 }
    	} catch (Exception e) {
 			e.printStackTrace();
 		    throw new XtentisWebappException(e);
 		}		
	}
     
     private WSUniverse buildUniverse(UniverseQuickStartStore store) throws Exception {
     	
     	WSUniverse ws = new WSUniverse();    	
 		ws.setName(store.getUniverseName());
 		ws.setDescription(store.getUniverseDesc());
 		
 		String targetRevision=store.getTargetRevisionName();
 		ws.setDefaultItemsRevisionID(targetRevision);
 		
 		List<WSUniverseXtentisObjectsRevisionIDs> xtentisObjectsRevisionIDs=new ArrayList<WSUniverseXtentisObjectsRevisionIDs>();			
 		xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs("Data Model",QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(),DataModelPOJO.class)));
 		xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs("Menu",QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(),MenuPOJO.class)));
 		xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs("Role",targetRevision));
 		xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs("Routing Rule",targetRevision));
 		xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs("Stored Procedure",targetRevision));
 		xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs("Transformer V2",targetRevision));
 		xtentisObjectsRevisionIDs.add(new WSUniverseXtentisObjectsRevisionIDs("View",QuickStartWizzardHelper.getObjectRevisionFromContext(WebContextFactory.get(),ViewPOJO.class)));	
 		ws.setXtentisObjectsRevisionIDs(xtentisObjectsRevisionIDs.toArray(new WSUniverseXtentisObjectsRevisionIDs[xtentisObjectsRevisionIDs.size()] ));
		return ws;
 	}
     
 	public SyncStatus getStatus()throws Exception {
 		UniverseQuickStartStore store=QuickStartWizzardHelper.getStore(WebContextFactory.get());
		WSSynchronizationPlanStatus wsStatus = null;
		try {
	        wsStatus = Util.getPort(UniverseManagerDWR.DEFAULT_URL,UniverseManagerDWR.DEFAULT_USERNAME,UniverseManagerDWR.DEFAULT_PASSWORD,Util._FORCE_WEB_SERVICE_).synchronizationPlanAction(new WSSynchronizationPlanAction(
	        	new WSSynchronizationPlanPK(store.getSyncPlanName()),
	        	WSSynchronizationPlanActionCode.STATUS
	        ));
	        SyncStatus status=new SyncStatus();
	        status.setValue(wsStatus.getWsStatusCode().getValue());
	        status.setMessage(wsStatus.getStatusMessage());
	        return status;
        } catch (Exception e) {
        	e.printStackTrace();
        	throw new Exception(e.getClass().getName() + ": "
					+ e.getLocalizedMessage());
        }
	}
 	
 	public void updateItemBoxTreeNode(String nodeID,boolean isChecked) throws Exception{
 		try {
			//paser node Id
	 		String[] nodeIDParts=nodeID.split("@");
			String clusterName=nodeIDParts[0];
			String conceptName=nodeIDParts[1];
			String revision=nodeIDParts[2];
			ToCopyItemEntry itemEntry=new ToCopyItemEntry(clusterName,conceptName,revision);
			
			//get from store
			UniverseQuickStartStore store=QuickStartWizzardHelper.getStore(WebContextFactory.get());
			List<ToCopyItemEntry> toCopyItemEntries=store.getToCopyItemEntries();
			
			if(toCopyItemEntries.contains(itemEntry)){
				if(!isChecked)toCopyItemEntries.remove(itemEntry);
			}else{
				if(isChecked)toCopyItemEntries.add(itemEntry);
			}
 		} catch (Exception e) {
        	e.printStackTrace();
        	throw new Exception(e.getClass().getName() + ": "+ e.getLocalizedMessage());
        }
	}

}
