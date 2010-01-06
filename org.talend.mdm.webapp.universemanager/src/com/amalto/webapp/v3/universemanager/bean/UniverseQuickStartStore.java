package com.amalto.webapp.v3.universemanager.bean;

import java.util.ArrayList;
import java.util.List;

import com.amalto.webapp.util.webservices.WSSynchronizationPlan;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanItemsSynchronizations;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanXtentisObjectsSynchronizations;
import com.amalto.webapp.util.webservices.WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations;
import com.amalto.webapp.v3.universemanager.dwr.UniverseManagerDWR;


public class UniverseQuickStartStore {
	//context field
	private String sourceUniverseName;
	
	//to store fields
	private String universeName;//basic
	
	private String universeDesc;//basic
	
	private List<ToCopyItemEntry> toCopyItemEntries;
	
	private List<RoutingRuleEntry> toCopyRoutingRuleEntries;
	private boolean isDeselectAllRoutingRules;
	
	private List<TransformEntry> toCopyTransformerEntries;
	private boolean isDeselectAllTransformers;
	
	private List<RoleEntry> toCopyRoleEntries;
	private boolean isDeselectAllRoles;
	
	private StoredProcedureEntry storedProcedureEntry;
	
	
	
	public UniverseQuickStartStore() {
		//init
		this.sourceUniverseName="";
		this.universeName="";
		this.universeDesc="";
		this.toCopyItemEntries=new ArrayList<ToCopyItemEntry>();
		this.toCopyRoutingRuleEntries=new ArrayList<RoutingRuleEntry>();
		this.isDeselectAllRoutingRules=false;
		this.toCopyTransformerEntries=new ArrayList<TransformEntry>();
		this.isDeselectAllTransformers=false;
		this.toCopyRoleEntries=new ArrayList<RoleEntry>();
		this.isDeselectAllRoles=false;
		this.storedProcedureEntry=null;
		//TODO[extends]: init more fields here
	}
	
	
	public String getSourceUniverseName() {
		return sourceUniverseName;
	}

	public void setSourceUniverseName(String sourceUniverseName) {
		this.sourceUniverseName = sourceUniverseName;
	}

	public String getUniverseName() {
		return universeName;
	}

	public void setUniverseName(String universeName) {
		this.universeName = universeName;
	}

	public String getUniverseDesc() {
		return universeDesc;
	}

	public void setUniverseDesc(String universeDesc) {
		this.universeDesc = universeDesc;
	}
		
	
	public List<ToCopyItemEntry> getToCopyItemEntries() {
		return toCopyItemEntries;
	}

	public void setToCopyItemEntries(List<ToCopyItemEntry> toCopyItemEntries) {
		this.toCopyItemEntries = toCopyItemEntries;
	}
	
	public List<RoleEntry> getToCopyRoleEntries() {
		return toCopyRoleEntries;
	}


	public void setToCopyRoleEntries(List<RoleEntry> toCopyRoleEntries) {
		this.toCopyRoleEntries = toCopyRoleEntries;
	}
	
	
	public StoredProcedureEntry getStoredProcedureEntry() {
		return storedProcedureEntry;
	}


	public void setStoredProcedureEntry(StoredProcedureEntry storedProcedureEntry) {
		this.storedProcedureEntry = storedProcedureEntry;
	}


	public List<RoutingRuleEntry> getToCopyRoutingRuleEntries() {
		return toCopyRoutingRuleEntries;
	}


	public void setToCopyRoutingRuleEntries(
			List<RoutingRuleEntry> toCopyRoutingRuleEntries) {
		this.toCopyRoutingRuleEntries = toCopyRoutingRuleEntries;
	}


	public List<TransformEntry> getToCopyTransformerEntries() {
		return toCopyTransformerEntries;
	}


	public void setToCopyTransformerEntries(
			List<TransformEntry> toCopyTransformerEntries) {
		this.toCopyTransformerEntries = toCopyTransformerEntries;
	}
	
	
	public boolean isDeselectAllRoutingRules() {
		return isDeselectAllRoutingRules;
	}


	public void setDeselectAllRoutingRules(boolean isDeselectAllRoutingRules) {
		this.isDeselectAllRoutingRules = isDeselectAllRoutingRules;
	}


	public boolean isDeselectAllTransformers() {
		return isDeselectAllTransformers;
	}


	public void setDeselectAllTransformers(boolean isDeselectAllTransformers) {
		this.isDeselectAllTransformers = isDeselectAllTransformers;
	}


	public boolean isDeselectAllRoles() {
		return isDeselectAllRoles;
	}


	public void setDeselectAllRoles(boolean isDeselectAllRoles) {
		this.isDeselectAllRoles = isDeselectAllRoles;
	}


	public String getSyncPlanName() {
		return "Synchronization Plan of Universe "+this.getUniverseName();
	}
	
	public String getTargetRevisionName() {
		return "r_"+this.getUniverseName();
	}


	public void reset(boolean withBasicInfos) {
		if(withBasicInfos){
			this.sourceUniverseName="";
			this.universeName="";
			this.universeDesc="";
		}
		this.toCopyItemEntries=new ArrayList<ToCopyItemEntry>();
		this.toCopyRoutingRuleEntries=new ArrayList<RoutingRuleEntry>();
		this.isDeselectAllRoutingRules=false;
		this.toCopyTransformerEntries=new ArrayList<TransformEntry>();
		this.isDeselectAllTransformers=false;
		this.toCopyRoleEntries=new ArrayList<RoleEntry>();
		this.isDeselectAllRoles=false;
		this.storedProcedureEntry=null;
		//TODO[extends]: reset more fields here
		

	}
	
	public WSSynchronizationPlan buildSynchronizationPlan() {
		    //basic
			WSSynchronizationPlan ws = new WSSynchronizationPlan(); 
			ws.setName(this.getSyncPlanName());
			ws.setDescription("");
			
			ws.setRemoteSystemName("");
			ws.setRemoteSystemURL(UniverseManagerDWR.DEFAULT_URL);
			ws.setRemoteSystemUsername(UniverseManagerDWR.DEFAULT_USERNAME);//very bad hard code
			ws.setRemoteSystemPassword(UniverseManagerDWR.DEFAULT_PASSWORD);//very bad hard code
			
			//xtentisobjects
			WSSynchronizationPlanXtentisObjectsSynchronizations[] xtentisObjSyncs=new WSSynchronizationPlanXtentisObjectsSynchronizations[4];
			
			//routing rule object
			WSSynchronizationPlanXtentisObjectsSynchronizations routingRuleObj=new WSSynchronizationPlanXtentisObjectsSynchronizations();
			routingRuleObj.setXtentisObjectName("Routing Rule");
			WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[] routingRuleSyncs=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[this.toCopyRoutingRuleEntries.size()];
			for (int i = 0; i < routingRuleSyncs.length; i++) {
				RoutingRuleEntry entry = (RoutingRuleEntry) this.toCopyRoutingRuleEntries.get(i);
				
				routingRuleSyncs[i]=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations();
				routingRuleSyncs[i].setInstancePattern(entry.getRoutingRuleName());
				routingRuleSyncs[i].setLocalRevisionID(entry.getLocalRevisionID().replaceAll("\\[HEAD\\]|HEAD", ""));
				routingRuleSyncs[i].setRemoteRevisionID(this.getTargetRevisionName());
				routingRuleSyncs[i].setAlgorithm("Local Wins");
			}
			routingRuleObj.setSynchronizations(routingRuleSyncs);
			xtentisObjSyncs[0]=routingRuleObj;
			
			//transformer object
			WSSynchronizationPlanXtentisObjectsSynchronizations transformerObj=new WSSynchronizationPlanXtentisObjectsSynchronizations();
			transformerObj.setXtentisObjectName("Transformer V2");
			WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[] transformerSyncs=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[this.toCopyTransformerEntries.size()];
			for (int i = 0; i < transformerSyncs.length; i++) {
				TransformEntry entry = (TransformEntry) this.toCopyTransformerEntries.get(i);
				
				transformerSyncs[i]=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations();
				transformerSyncs[i].setInstancePattern(entry.getTransformName());
				transformerSyncs[i].setLocalRevisionID(entry.getLocalRevisionID().replaceAll("\\[HEAD\\]|HEAD", ""));
				transformerSyncs[i].setRemoteRevisionID(this.getTargetRevisionName());
				transformerSyncs[i].setAlgorithm("Local Wins");
			}
			transformerObj.setSynchronizations(transformerSyncs);
			xtentisObjSyncs[1]=transformerObj;
			
			//role object
			WSSynchronizationPlanXtentisObjectsSynchronizations roleObj=new WSSynchronizationPlanXtentisObjectsSynchronizations();
			roleObj.setXtentisObjectName("Role");
			WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[] roleSyncs=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[this.toCopyRoleEntries.size()];
			for (int i = 0; i < roleSyncs.length; i++) {
				RoleEntry entry = (RoleEntry) this.toCopyRoleEntries.get(i);
				
				roleSyncs[i]=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations();
				roleSyncs[i].setInstancePattern(entry.getRoleName());
				roleSyncs[i].setLocalRevisionID(entry.getLocalRevisionID().replaceAll("\\[HEAD\\]|HEAD", ""));
				roleSyncs[i].setRemoteRevisionID(this.getTargetRevisionName());
				roleSyncs[i].setAlgorithm("Local Wins");
			}
			roleObj.setSynchronizations(roleSyncs);
			xtentisObjSyncs[2]=roleObj;
			
			//stored procedure
			if(this.storedProcedureEntry!=null&&this.storedProcedureEntry.isChecked()){
				WSSynchronizationPlanXtentisObjectsSynchronizations spObj=new WSSynchronizationPlanXtentisObjectsSynchronizations();
				spObj.setXtentisObjectName("Stored Procedure");
				WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[] spSyncs=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations[1];
				spSyncs[0]=new WSSynchronizationPlanXtentisObjectsSynchronizationsSynchronizations();
				spSyncs[0].setInstancePattern(".*");
				spSyncs[0].setLocalRevisionID(this.storedProcedureEntry.getLocalRevisionID().replaceAll("\\[HEAD\\]|HEAD", ""));
				spSyncs[0].setRemoteRevisionID(this.getTargetRevisionName());
				spSyncs[0].setAlgorithm("Local Wins");
				spObj.setSynchronizations(spSyncs);
				xtentisObjSyncs[3]=spObj;
			}
			//TODO[extends]: add more objects here
			
			ws.setXtentisObjectsSynchronizations(xtentisObjSyncs);
			
			
			//items
			WSSynchronizationPlanItemsSynchronizations[] itemSyncs=new WSSynchronizationPlanItemsSynchronizations[this.toCopyItemEntries.size()];
			for(int i=0; i<itemSyncs.length; i++){
				ToCopyItemEntry entry=this.toCopyItemEntries.get(i);
				itemSyncs[i]=new WSSynchronizationPlanItemsSynchronizations();
				itemSyncs[i].setLocalCluster(entry.getLocalClusterName());
				itemSyncs[i].setConceptName(entry.getConceptName());
				itemSyncs[i].setLocalRevisionID(entry.getLocalRevisonId().replaceAll("\\[HEAD\\]|HEAD", ""));
				itemSyncs[i].setIdsPattern(".*");
				itemSyncs[i].setRemoteCluster(entry.getLocalClusterName());
				itemSyncs[i].setRemoteRevisionID(this.getTargetRevisionName());
				itemSyncs[i].setAlgorithm("Local Wins");	
			}
			ws.setItemsSynchronizations(itemSyncs);
			
			return ws;
	}

}
