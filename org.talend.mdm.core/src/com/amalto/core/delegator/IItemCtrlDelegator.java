package com.amalto.core.delegator;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.InitialContext;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;

public abstract class IItemCtrlDelegator implements IBeanDelegator{
	//methods from ItemCtrl2Bean
	public ArrayList<String> getItemsPivotIndex(String clusterName,
			String mainPivotName,
			LinkedHashMap<String, String[]> pivotWithKeys, String[] indexPaths,
			IWhereItem whereItem, String[] pivotDirections,
			String[] indexDirections, int start, int limit)
			throws XtentisException {
        try {
        	
            //validate parameters
        	if (pivotWithKeys.size()==0) {
        	    String err = "The Map of pivots must contain at least one element";
        	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        	    throw new XtentisException(err);
        	}
        	
        	if (indexPaths.length==0) {
        	    String err = "The Array of Index Paths must contain at least one element";
        	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        	    throw new XtentisException(err);
        	}
        	
        	//get the universe and revision ID
        	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
        	if (universe == null) {
        		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	
        	XmlServerSLWrapperLocal server = null;
    		try {
    			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
    		} catch (Exception e) {
    			String err = "Unable to search items in data cluster '"+clusterName+"': unable to access the XML Server wrapper";
    			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    			throw new XtentisException(err);
    		}
    		// ctoum - 20100110
    		ViewPOJOPK viewPOJOPK=new ViewPOJOPK("Browse_items_" + mainPivotName);
        	ViewPOJO view = Util.getViewCtrlLocalHome().create().getView(viewPOJOPK);
        	//ViewLocal view = ViewUtil.getLocalHome().findByPrimaryKey(viewPK);

        	//Create an ItemWhere which combines the search and and view wheres 
        	IWhereItem fullWhere;
        	if(		(view.getWhereConditions()==null) 
        			|| (view.getWhereConditions().getList().size()==0)
        	) {
        		if (whereItem==null)
        			fullWhere = null;
        		else
        			fullWhere = whereItem;
        	} else {
        		if (whereItem==null){
        			fullWhere = new WhereAnd(view.getWhereConditions().getList());
        		} else {
        			WhereAnd viewWhere = new WhereAnd(view.getWhereConditions().getList());
                    WhereAnd wAnd = new WhereAnd();
        			wAnd.add(whereItem);
        			wAnd.add(viewWhere);
        			fullWhere = wAnd;
        		}
        	}
            String query = server.getPivotIndexQuery(
            		                clusterName, 
            		                mainPivotName, 
            		                pivotWithKeys,
            		                universe.getItemsRevisionIDs(),
            		                universe.getDefaultItemRevisionID(),
            		                indexPaths, 
            		                fullWhere, 
            		                pivotDirections,
            		                indexDirections, 
            		                start,
            		                limit);
            
            org.apache.log4j.Logger.getLogger(this.getClass()).debug(query);
            
            return server.runQuery(null, null, query, null);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to search: "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }

	}
	
	public ArrayList<String> getChildrenItems(
			String clusterName, 
			String conceptName,
			String[] PKXpaths,
			String FKXpath,
			String labelXpath,
			String fatherPK,
			IWhereItem whereItem
	) throws XtentisException{
		
		try {
				//get the universe and revision ID
		    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
		    	if (universe == null) {
		    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
		    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
		    		throw new XtentisException(err);
		    	}
		    	
		    	XmlServerSLWrapperLocal server = null;
				try {
					server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
				} catch (Exception e) {
					String err = "Unable to search items in data cluster '"+clusterName+"': unable to access the XML Server wrapper";
					org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
					throw new XtentisException(err);
				}
				
				ViewPOJOPK viewPOJOPK=new ViewPOJOPK("Browse_items_" + conceptName);
		    	ViewPOJO view = Util.getViewCtrlLocalHome().create().getView(viewPOJOPK);
		
		    	//Create an ItemWhere which combines the search and and view wheres 
		    	IWhereItem fullWhere;
		    	if((view.getWhereConditions()==null) || (view.getWhereConditions().getList().size()==0)
		    	) {
		    		if (whereItem==null)
		    			fullWhere = null;
		    		else
		    			fullWhere = whereItem;
		    	} else {
		    		if (whereItem==null){
		    			fullWhere = new WhereAnd(view.getWhereConditions().getList());
		    		} else {
		    			WhereAnd viewWhere = new WhereAnd(view.getWhereConditions().getList());
		                WhereAnd wAnd = new WhereAnd();
		    			wAnd.add(whereItem);
		    			wAnd.add(viewWhere);
		    			fullWhere = wAnd;
		    		}
		    	}
				
				String query = server.getChildrenItemsQuery
				        (clusterName, 
				         conceptName,  
				         PKXpaths, 
				         FKXpath, 
				         labelXpath, 
				         fatherPK, 
				         universe.getItemsRevisionIDs(),
			             universe.getDefaultItemRevisionID(),
			             fullWhere);
		                
		
		       org.apache.log4j.Logger.getLogger(this.getClass()).debug(query);
			
			   return server.runQuery(null, null, query, null);
	   
		} catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to search: " + ": " + e.getClass().getName()+ ": " + e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
		
	}

	public void resendFailtSvnMessage()throws Exception {
		// TODO Auto-generated method stub

	}
	public ArrayList<String> viewSearch(DataClusterPOJOPK dataClusterPOJOPK,
			ViewPOJOPK viewPOJOPK, IWhereItem whereItem, int spellThreshold,
			String orderBy, String direction, int start, int limit)
			throws XtentisException {
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    		throw new XtentisException(err);
    	}
    	
    	//build the patterns to revision ID map
    	LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(universe.getItemsRevisionIDs());
    	if (universe.getDefaultItemRevisionID() != null&&universe.getDefaultItemRevisionID().length()>0) conceptPatternsToRevisionID.put(".*", universe.getDefaultItemRevisionID());
    	
    	//build the patterns to cluster map - only one cluster at this stage
    	LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
    	conceptPatternsToClusterName.put(".*", dataClusterPOJOPK.getUniqueId());
    	
    	XmlServerSLWrapperLocal server = null;
		try {
			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Unable to search items in data cluster '"+dataClusterPOJOPK.getUniqueId()+"': unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
			throw new XtentisException(err);
		}
    	
        try {            
        	ViewPOJO view = Util.getViewCtrlLocalHome().create().getView(viewPOJOPK);
        	//ViewLocal view = ViewUtil.getLocalHome().findByPrimaryKey(viewPK);

        	//Create an ItemWhere which combines the search and and view wheres 
        	whereItem = Util.fixWebCondtions(whereItem);
			IWhereItem fullWhere;
			ArrayList conditions = view.getWhereConditions().getList();
			Util.fixCondtions(conditions);
			if(conditions == null || conditions.size() == 0) {
        		if (whereItem==null)
        			fullWhere = null;
        		else
        			fullWhere = whereItem;
        	} else {
        		if (whereItem==null){
        			fullWhere = new WhereAnd(conditions);
        		} else {
        			WhereAnd viewWhere = new WhereAnd(conditions);
                    WhereAnd wAnd = new WhereAnd();
        			wAnd.add(whereItem);
        			wAnd.add(viewWhere);
        			fullWhere = wAnd;
        		}
        	}
        	Map<String, ArrayList<String>> metaDataTypes=Util.getMetaDataTypes(fullWhere);
            String query = server.getItemsQuery(
            	conceptPatternsToRevisionID, 
            	conceptPatternsToClusterName, 
            	null, //the main pivots will be that of the first element of the viewable list
            	view.getViewableBusinessElements().getList(), 
            	fullWhere, 
            	orderBy, 
            	direction, 
            	start, 
            	limit, 
            	spellThreshold,
            	true,
            	metaDataTypes
            );
            
            return server.runQuery(null, null, query, null);
            
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to single search: "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
	}

	
	public ItemPOJOPK putItem(ItemPOJO item, String schema, String dataModelName)
			throws XtentisException {
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putItem() "+item.getItemPOJOPK().getUniqueID());
    	
        try {
        	if (schema!=null) {            	
    	    	String concept=item.getConceptName();
    	    	if(Util.getUUIDNodes(schema, concept).size()>0){ //check uuid key exists
    		    	String dataCluster=item.getDataClusterPOJOPK().getIds()[0];
    		    	Document schema1=Util.parse(schema);
    				Node n=Util.processUUID(item.getProjection(), schema, dataCluster, concept);
    				XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(
    						schema1,
    						concept					
    				);	       
    				//get key values
    				String[] itemKeyValues = com.amalto.core.util.Util.getKeyValuesFromItem(
    						(Element)n,
    						conceptKey
    				);			
    				//reset item projection & itemids
    				item.setProjectionAsString(Util.nodeToString(n));
    				item.setItemIds(itemKeyValues);
    	    	}
    	    	
        		Util.validate(item.getProjection(),schema);
        	}
        	
            //FIXME: update the vocabulary . Universe dependent?
        	/*
            DataClusterLocal dc =  (DataClusterLocal)dataClusters.get(item.getDataClusterPK().getName());
            if (dc == null) {
            	dc = DataClusterUtil.getLocalHome().findByPrimaryKey(item.getDataClusterPK());
            	dataClusters.put(item.getDataClusterPK().getName(), dc);
            }
            if (dc.getSpellerRefreshPeriodInSeconds()>-1)
            	dc.addToVocabulary(item.getProjection());
            */
            
        	//make sure the plan is reset
        	item.setPlanPK(null);
        	//used for binding data model
        	if(dataModelName!=null)item.setDataModelName(dataModelName);
        	//Store
            ItemPOJOPK pk = item.store();
            if (pk == null) throw new XtentisException("Could not put item "+Util.joinStrings(item.getItemIds(),".")+".Check the XML Server logs");
            
            return pk;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
	    	String prefix = "Unable to create/update the item "+item.getDataClusterPOJOPK().getUniqueId()+"."+Util.joinStrings(item.getItemIds(), ".")
    	    		+": ";
    	    String err = prefix +e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    // simplify the error message
    	    if (dataModelName.equalsIgnoreCase("Reporting"))
    	    {
    	    	if (err.indexOf("One of '{ListOfFilters}'") !=-1)
    	    	{
    	    		err = prefix + "At least one filter must be defined";
    	    	}
    	    }
    	    throw new XtentisException(err);
	    }
	}

	
}
