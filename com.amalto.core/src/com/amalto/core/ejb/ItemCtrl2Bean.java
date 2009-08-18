package com.amalto.core.ejb;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Set;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;

import org.apache.commons.lang.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.datamodel.ejb.DataModelPOJO;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2CtrlBean;
import com.amalto.core.objects.transformers.v2.ejb.TransformerV2POJOPK;
import com.amalto.core.objects.transformers.v2.util.TransformerCallBack;
import com.amalto.core.objects.transformers.v2.util.TransformerContext;
import com.amalto.core.objects.transformers.v2.util.TypedContent;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.util.JazzyConfiguration;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.RoleSpecification;
import com.amalto.core.util.RoleWhereCondition;
import com.amalto.core.util.TransformerPluginCallBack;
import com.amalto.core.util.TransformerPluginContext;
import com.amalto.core.util.UUIDItemContent;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.IXmlServerSLWrapper;
import com.amalto.xmlserver.interfaces.WhereAnd;
import com.amalto.xmlserver.interfaces.WhereCondition;
import com.amalto.xmlserver.interfaces.WhereOr;

/**
 * @author Bruno Grieder
 * 
 * @ejb.bean name="ItemCtrl2"
 *          display-name="Name for ItemCtrl2"
 *          description="Description for ItemCtrl2"
 *          jndi-name="amalto/remote/core/itemctrl2"
 * 		  	local-jndi-name = "amalto/local/core/itemctrl2"
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

@SuppressWarnings("deprecation")
public class ItemCtrl2Bean implements SessionBean {
  
	public static final long serialVersionUID = 200;
	
	//private SessionContext context;
	
    /**
     * ItemCtrlBean.java
     * Constructor
     * 
     */
    public ItemCtrl2Bean() {
        super();
    }

    /* (non-Javadoc)
     * @see javax.ejb.SessionBean#setSessionContext(javax.ejb.SessionContext)
     */
    public void setSessionContext(SessionContext ctx)
        throws EJBException,
        RemoteException {
    	//context=ctx;
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
     * Creates or updates a item
     * @throws XtentisException 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ItemPOJOPK putItem(ItemPOJO item, DataModelPOJO datamodel) throws XtentisException{
    	return putItem(item, (datamodel == null ? null : datamodel.getSchema()),(datamodel == null ? null : datamodel.getName()));
    }
    
    protected ItemPOJOPK putItem(ItemPOJO item, String schema,String dataModelName) throws XtentisException{
    	
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace("putItem() "+item.getItemPOJOPK().getUniqueID());
    	
        try {
        	//parse the item against the datamodel - no parsing for cache items
        	
        	if (schema!=null) {            	
        		Document schema1=Util.parse(schema);
    	    	String concept=item.getConceptName();
    	    	if(Util.getUUIDNodes(schema1, concept).getLength()>0){ //check uuid key exists
    		    	String dataCluster=item.getDataClusterPOJOPK().getIds()[0];
    		    	Element root=(Element)item.getProjection().cloneNode(true);
    		        XSDKey conceptKey = com.amalto.core.util.Util.getBusinessConceptKey(
    		        		schema1,
    						concept					
    				);	       
    				//get key values
    				String[] itemKeyValues = com.amalto.core.util.Util.getKeyValuesFromItem(
    		   			root,
    						conceptKey
    				);			
    				UUIDItemContent content=Util.processUUID(root, schema1, dataCluster, concept, conceptKey, itemKeyValues);
    				//reset item projection & itemids
    				item.setProjectionAsString(content.getItemContent());    				
    				item.setItemIds(content.getItemKeyValues());
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
    
     
    /**
     * Get item
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ItemPOJO getItem(ItemPOJOPK pk) throws XtentisException{

        try {
        	ItemPOJO pojo = ItemPOJO.load(pk);
        	if (pojo==null) {
        		String err= "The item '"+pk.getUniqueID()+"' cannot be found.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return pojo;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the item "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get item with revisionID
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ItemPOJO getItem(String revisionID,ItemPOJOPK pk) throws XtentisException{

        try {
        	ItemPOJO pojo = ItemPOJO.load(revisionID,pk);
        	if (pojo==null) {
        		String err= "The item '"+pk.getUniqueID()+"' cannot be found.";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}
        	return pojo;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the item "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Get an item - no exception is thrown: returns null if not found 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ItemPOJO existsItem(ItemPOJOPK pk)    throws XtentisException{
    	
        try {
        	return ItemPOJO.load(pk);        	
	    } catch (XtentisException e) {
	    	return null;
	    } catch (Exception e) {
    	    String info = "Could not check whether this item exists:  "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).debug(info, e);
    	   return null;
	    }
    }
    
    /**
     * Remove an item - returns null if no item was deleted
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ItemPOJOPK deleteItem(ItemPOJOPK pk) 
    throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).trace(
 		       "Deleting "+pk.getDataClusterPOJOPK().getUniqueId()+"."+Util.joinStrings(pk.getIds(), "."));
        try {
        	return ItemPOJO.remove(pk);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to remove the item "+pk.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }    
    
    /**
	 * Delete items in a stateless mode: open a connection --> perform delete
	 * --> close the connection
	 * 
	 * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public int deleteItems(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		String conceptName,
			IWhereItem search,
			int spellTreshold
    	) throws XtentisException{
    	
    	//get the universe and revision ID
    	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
    	if (universe == null) {
    		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
    		throw new XtentisException(err);
    	}
    	
    	//build the patterns to revision ID map
    	LinkedHashMap<String, String> patternsToRevisionID = new LinkedHashMap<String, String>(universe.getItemsRevisionIDs());
    	if (universe.getDefaultItemRevisionID() != null) patternsToRevisionID.put(".*", universe.getDefaultItemRevisionID());
    	
    	//build the patterns to cluster map - only one cluster at this stage
    	LinkedHashMap<String, String> patternsToClusterName = new LinkedHashMap<String, String>();
    	patternsToClusterName.put(".*", dataClusterPOJOPK.getUniqueId());
    	
    	XmlServerSLWrapperLocal server = null;
		try {
			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Unable to delete items of concept '"+conceptName+"': unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
			throw new XtentisException(err);
		}
		
        try {               
        	return server.deleteItems(patternsToRevisionID, patternsToClusterName, conceptName, search);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to delete the items: "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }    
    
    /**
     * Drop an item - returns null if no item was dropped
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public DroppedItemPOJOPK dropItem(ItemPOJOPK itemPOJOPK,String partPath) throws XtentisException{
    	org.apache.log4j.Logger.getLogger(this.getClass()).debug(
 		       "Dropping "+itemPOJOPK.getDataClusterPOJOPK().getUniqueId()+"."+Util.joinStrings(itemPOJOPK.getIds(), "."));
        try {
        	return ItemPOJO.drop(itemPOJOPK, partPath);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to drop the item "+itemPOJOPK.toString()
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }

    /**
     * Get unordered items of a Concept using an optional where condition
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
     * @param conceptName
     * 		The name of the concept
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     *  
     * @throws XtentisException
     */
    public ArrayList<String> getItems(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		String conceptName,
			IWhereItem whereItem,
			int spellThreshold,
			int start,
			int limit 
    	) throws XtentisException{
    	return getItems(dataClusterPOJOPK, conceptName, whereItem, spellThreshold, null, null, start, limit);
    }
    
    
    /**
     * Get potentially ordered items of a Concept using an optional where condition
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
     * @param conceptName
     * 		The name of the concept
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param orderBy
     * 		The full path of the item user to order
     * @param direction
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method
     *  
     * @throws XtentisException
     */
    public ArrayList<String> getItems(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		String conceptName,
			IWhereItem whereItem,
			int spellThreshold,
			String orderBy,
			String direction,
			int start,
			int limit 
    	) 
    	throws XtentisException{
            
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
			String err = "Unable to get items of concept '"+conceptName+"': unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
			throw new XtentisException(err);
		}
		
		try {            
            ArrayList<String> elements = new ArrayList<String>();
            elements.add(conceptName);
            
            String query = server.getItemsQuery(
            	conceptPatternsToRevisionID, 
            	conceptPatternsToClusterName, 
            	null,  //no need to force a pivot
            	elements, 
            	whereItem, 
            	orderBy, 
            	direction, 
            	start, 
            	limit, 
            	spellThreshold
            );

            return server.runQuery(null, null, query, null);
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to get the items: "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }    


    /**
     * Search Items thru a view in a cluster and specifying a condition
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
     * @param viewPOJOPK
     * 		The View
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ArrayList<String> viewSearch(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		ViewPOJOPK viewPOJOPK,
			IWhereItem whereItem, 
			int spellThreshold,
			int start,
			int limit
		) throws XtentisException{
    	return viewSearch(dataClusterPOJOPK, viewPOJOPK, whereItem, spellThreshold, null, null, start, limit);
    }
    
    /**
     * Search ordered Items thru a view in a cluster and specifying a condition
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
     * @param viewPOJOPK
     * 		The View
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param orderBy
     * 		The full path of the item user to order
     * @param direction
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ArrayList<String> viewSearch(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		ViewPOJOPK viewPOJOPK,
			IWhereItem whereItem, 
			int spellThreshold,
			String orderBy,
			String direction,
			int start,
			int limit
		) throws XtentisException{
    	
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
        	
        	//2.13.1 - Add Filters from the Roles
        	LocalUser user = LocalUser.getLocalUser();
        	HashSet<String> roleNames = user.getRoles();
        	if (! roleNames.contains("administration")) {
        		ArrayList<IWhereItem> roleWhereConditions = new ArrayList<IWhereItem>();
	           	String objectType = "View";
	        	for (Iterator<String> iter = roleNames.iterator(); iter.hasNext(); ) {
	    			String roleName = iter.next();
	    			if ("authenticated".equals(roleName)) continue;
	    			//load Role
	    			RolePOJO role = ObjectPOJO.load(RolePOJO.class, new RolePOJOPK(roleName));
	    			//get Specifications for the View Object
	    			RoleSpecification specification = role.getRoleSpecifications().get(objectType);
	    			if (specification!=null) {
	    				if (!specification.isAdmin()) {
	    					Set<String> regexIds = specification.getInstances().keySet();
	    					for (Iterator<String> iterator = regexIds.iterator(); iterator.hasNext(); ) {
	    						String regexId = iterator.next();
	    						if (viewPOJOPK.getIds()[0].matches(regexId)) {
	    							HashSet<String> parameters = specification.getInstances().get(regexId).getParameters();
	    							for (Iterator<String> it = parameters.iterator(); it.hasNext(); ) {
										String marshalledWC = it.next();
										roleWhereConditions.add(RoleWhereCondition.parse(marshalledWC).toWhereCondition());
									}
	    						}
	    					}
	    				}
	    			}
	        	}//for role Names
	        	//add collected additional conditions
	        	if (roleWhereConditions.size()>0) {
		        	if (fullWhere==null){
	        			fullWhere = new WhereAnd(roleWhereConditions);
	        		} else {
	        			WhereAnd viewWhere = new WhereAnd(roleWhereConditions);
	                    WhereAnd wAnd = new WhereAnd();
	        			wAnd.add(fullWhere);
	        			wAnd.add(viewWhere);
	        			fullWhere = wAnd;
	        		}
	        	}
        	}
        	
       	
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
            	spellThreshold
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

    
    
    /**
     * Returns an ordered collection of results searched in a cluster and specifying an optional condition<br/>
     * The results are xml objects made of elements constituted by the specified viewablePaths
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
	 * @param forceMainPivot
	 * 			An optional pivot that will appear first in the list of pivots in the query<br>:
	 * 			This allows forcing cartesian products: for instance Order Header vs Order Line
	 * @param viewablePaths
	 * 		The list of elements returned in each result 
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ArrayList<String> xPathsSearch(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		String forceMainPivot,
    		ArrayList<String> viewablePaths,
			IWhereItem whereItem, 
			int spellThreshold,
			int start,
			int limit
		) throws XtentisException{
    	return xPathsSearch(dataClusterPOJOPK, forceMainPivot, viewablePaths, whereItem, spellThreshold, null, null, start, limit);
    }
    
    
    /**
     * Returns an ordered collection of results searched in a cluster and specifying an optional condition<br/>
     * The results are xml objects made of elements constituted by the specified viewablePaths
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
	 * @param forceMainPivot
	 * 			An optional pivot that will appear first in the list of pivots in the query<br>:
	 * 			This allows forcing cartesian products: for instance Order Header vs Order Line
	 * @param viewablePaths
	 * 		The list of elements returned in each result 
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param orderBy
     * 		The full path of the item user to order
     * @param direction
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ArrayList<String> xPathsSearch(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		String forceMainPivot,
    		ArrayList<String> viewablePaths,
			IWhereItem whereItem, 
			int spellThreshold,
			String orderBy,
			String direction,
			int start,
			int limit
		) throws XtentisException{
        try {            

        	if (viewablePaths.size()==0) {
        	    String err = "The list of viewable xPaths must contain at least one element";
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
        	
        	//build the patterns to revision ID map
        	LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(universe.getItemsRevisionIDs());
        	if (universe.getDefaultItemRevisionID() != null) conceptPatternsToRevisionID.put(".*", universe.getDefaultItemRevisionID());
        	
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
            
            String query = server.getItemsQuery(
            	conceptPatternsToRevisionID, 
            	conceptPatternsToClusterName, 
            	forceMainPivot, 
            	viewablePaths, 
            	whereItem, 
            	orderBy, 
            	direction, 
            	start, 
            	limit, 
            	spellThreshold
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
    
    /**
     * Get items hierarchical tree according to pivots
     * 
     * @param clusterName
     * 		The Data Cluster where to run the query
	 * @param mainPivotName
     * 		The main Business Concept name
	 * @param pivotWithKeys
	 * 		The pivots with their IDs which selected to be the catalog of the hierarchical tree
     * @param indexPaths
     * 		The title as the content of each leaf node of the hierarchical tree
     * @param whereItem
     * 		The condition
     * @param pivotDirections
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param indexDirections
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ArrayList<String> getItemsPivotIndex(
    		String clusterName, 
			String mainPivotName,
			LinkedHashMap<String, String[]> pivotWithKeys, 
			String[] indexPaths,
			IWhereItem whereItem, 
			String[] pivotDirections,
			String[] indexDirections, 
			int start, 
			int limit
		) throws XtentisException{
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
            
            String query = server.getPivotIndexQuery(
            		                clusterName, 
            		                mainPivotName, 
            		                pivotWithKeys, 
            		                indexPaths, 
            		                whereItem, 
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

    
    /**
     * Count the items denoted by concept name meeting the optional condition whereItem
     * @param dataClusterPOJOPK
     * @param conceptName
     * @param whereItem
     * @param spellThreshold
     * @return
     * 		The number of items found
     * @throws XtentisException
     * 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public long count(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		String conceptName,
			IWhereItem whereItem, 
			int spellThreshold
    ) throws XtentisException{
        try {            
        	
        	//get the universe and revision ID
        	UniversePOJO universe = LocalUser.getLocalUser().getUniverse();
        	if (universe == null) {
        		String err = "ERROR: no Universe set for user '"+LocalUser.getLocalUser().getUsername()+"'";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);
        	}

        	//build the patterns to revision ID map
        	LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(universe.getItemsRevisionIDs());
        	if (universe.getDefaultItemRevisionID() != null) conceptPatternsToRevisionID.put(".*", universe.getDefaultItemRevisionID());
        	
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
    		
    		return server.countItems(conceptPatternsToRevisionID, conceptPatternsToClusterName, conceptName, whereItem);
			
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
    	    String err = "Unable to single search: "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }    
    
    
	

    
    /**
     * Search ordered Items thru a view in a cluster and specifying a condition
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
     * @param viewPOJOPK
     * 		The View
     * @param searchValue
     * 		The value/sentenced searched
     * @param matchAllWords
     * 		If <code>true</code>, the items must match all the words in the sentence
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param orderBy
     * 		The full path of the item user to order
     * @param direction
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * @return	
     * 		The ordered list of results
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ArrayList<String> quickSearch(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		ViewPOJOPK viewPOJOPK,
			String searchValue, 
			boolean matchAllWords,
			int spellThreshold,
			String orderBy,
			String direction,
			int start,
			int limit
			) throws XtentisException{
    
    	ArrayList<String> result = new ArrayList<String>();
    	boolean isSpellCheck = (spellThreshold >= (new JazzyConfiguration()).getMinTreshold());

    	try {
    		ViewPOJO view = Util.getViewCtrlLocalHome().create().getView(viewPOJOPK);
        	//ViewLocal view = ViewUtil.getLocalHome().findByPrimaryKey(viewPK);       	
    		ArrayList<String> searchable = view.getSearchableBusinessElements().getList();

        	//check if there actually is a search value
        	if (	(searchValue == null) ||
        			"".equals(searchValue) ||
        			"*".equals(searchValue)
        		)
        		return viewSearch(
        			dataClusterPOJOPK,
        			viewPOJOPK, 
        			null, 
        			spellThreshold, 
        			orderBy, 
        			direction, 
        			start, 
        			limit
        		); 
        		
        	
        	//if there is search Value, incoporate it in the Where Clause
        	
        	boolean isNumber =  searchValue.matches("[1-9][0-9]*[\\.[0-9]+]?");
        	
        	//loop over searchable elements
        	WhereOr mainOR = new WhereOr();
        	for (Iterator<String> iter = searchable.iterator(); iter.hasNext(); ) {
            	String be = iter.next();
            	if (isNumber) {
            		//it is a number
            		WhereOr numOR = new WhereOr();
            		numOR.add(new WhereCondition(
            			be,
            			WhereCondition.CONTAINS,
            			searchValue,
            			WhereCondition.PRE_NONE,
            			isSpellCheck
            		));
            		numOR.add(new WhereCondition(
                			be,
                			WhereCondition.EQUALS,
                			searchValue,
                			WhereCondition.PRE_NONE,
                			false
                	));
            		mainOR.add(numOR);
            	} else {
            		//Not A Number
            		mainOR.add(new WhereCondition(
                			be,
                			WhereCondition.CONTAINS,
                			searchValue,
                			(matchAllWords ? WhereCondition.PRE_AND : WhereCondition.PRE_OR),
                			isSpellCheck
                	));
            	}//end if number
            }//end for viewable elements
        	
        	if (mainOR.getSize()==0) return result; //HUHH!
        	
        	IWhereItem searchItem = mainOR;
        	if (mainOR.getSize() == 1) {
        		// gain one level
        		searchItem = mainOR.getItem(0);
        	}
        	       	
    		return viewSearch(
    			dataClusterPOJOPK,
    			viewPOJOPK, 
    			searchItem, 
    			spellThreshold, 
    			orderBy, 
    			direction, 
    			start, 
    			limit
    		); 
	    } catch (XtentisException e) {
	    	throw(e);
 	    } catch (Exception e) {
     	    String err = "Unable to quick serach  "+searchValue
     	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
     	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
     	    throw new XtentisException(err);
 	    } 
     }
 

    
    /**
     * Get the possible value for the business Element Path, optionally filtered by a condition
     * @param dataClusterPOJOPK
     * 			The data cluster where to run the query
     * @param businessElementPath
     * 			The business element path. Must be of the form <code>ConceptName/[optional sub elements]/element</code>
     * @param whereItem
     * 			The optional condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param orderBy
     * 		The full path of the item user to order
     * @param direction
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @return
     * 		The list of values
     * @throws XtentisException
     * 
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public ArrayList<String> getFullPathValues(
    		DataClusterPOJOPK dataClusterPOJOPK,
//    		ViewPOJOPK viewPOJOPK,
			String businessElementPath,
			IWhereItem whereItem,
			int spellThreshold,
			String orderBy,
			String direction
	) throws XtentisException {
		
    	
		ArrayList<String> res= new ArrayList<String>();
        try {
        	//find the conceptName
        	String conceptName = ItemPOJO.getConceptFromPath(businessElementPath);
        	if (conceptName == null) {
        		String err = "Unable to recover the concept from business Element path '"+businessElementPath+"'";
        		org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
        		throw new XtentisException(err);        		
        	}

        	
        	ArrayList<String> col = xPathsSearch(
        		dataClusterPOJOPK, 
        		null, 
        		new ArrayList<String>(Arrays.asList(new String[] {businessElementPath})), 
        		whereItem, 
        		spellThreshold, 
        		orderBy, 
        		direction, 
        		0, 
        		-1
        	);
        	
           	Pattern p = Pattern.compile("<.*>(.*?)</.*>",Pattern.DOTALL);
           	for (Iterator<String> iter = col.iterator(); iter.hasNext(); ) {
				String li = iter.next();
				Matcher m = p.matcher(li);
				if (m.matches())
					res.add(StringEscapeUtils.unescapeXml(m.group(1)));
				else
					throw new XtentisException("Result values were not understood for business element: "+conceptName);
			}

        	return res;
	    } catch (XtentisException e) {
	    	throw(e);
	    } catch (Exception e) {
		    String err = "Unable to get values for the Business Element \""+businessElementPath+"\"";
		    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
		    throw new XtentisException(err);
	    }
    }
    
 

  
    
    
 

    /*****************************************************************
     *  E X T R A C T I O N
    *****************************************************************/

    /**
     * Extracts data using a transformer
     * In addtion to any entry added by the plugins, the returned context contains at least one entry
     * <code>com.amalto.core.pipeline</code> which holds a Hashmap with entries containing
     * the outputs of the various plugins as TypedContent
     * 
     * @deprecated - Use a combination of {@link ItemCtrl2Bean#getItem(ItemPOJOPK)} and {@link TransformerV2CtrlBean#executeUntilDone(TransformerContext)}
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerPluginContext extractUsingTransformer(
    		ItemPOJOPK pojoPK,
    		TransformerPOJOPK transformerPOJOPK
    ) 
    throws XtentisException{
    	TransformerPluginContext context = new TransformerPluginContext();
        try {
        	TransformerContext newContext = TransformerPluginContext.getNewTransformerContext(context, transformerPOJOPK);
        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("extractUsingTransformer "+newContext.getTransformerV2POJOPK().getUniqueId());           	
        	
    		ItemPOJO item =getItem(pojoPK);
            if (item==null) {
            	String err = 
            		"Unable to extract item " +pojoPK.getConceptName()+"["+Util.joinStrings(pojoPK.getIds(),".")+"]"+
            		" using transformer "+newContext.getTransformerV2POJOPK().getUniqueId()+". The item cannot be found.";
            	org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            	throw new XtentisException(err);
            }

        	TransformerContext finalNewContext = Util.getTransformerV2CtrlLocal().executeUntilDone(
        			newContext, 
        			new TypedContent(item.getProjectionAsString().getBytes("utf-8"),"text/xml; charset=\"utf-8\"")
        	);
        	
        	
        	return TransformerPluginContext.getOldTransformerContext(finalNewContext);
  
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	String err = 
        		"Unable to extract item " +pojoPK.getConceptName()+"["+Util.joinStrings(pojoPK.getIds(),".")+"]"+
        		" using transformer "+transformerPOJOPK.getUniqueId()+
    	    	": "+e.getClass().getName()+": "+e.getLocalizedMessage();
	    	if ("logging_event".equals(pojoPK.getConceptName())) 
	    		err+="\nTo avoid infinite looping, no other error will be thrown.";
	    	else
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Extract an Item thru a transformer
     * 
     * @deprecated - Use a combination of {@link ItemCtrl2Bean#getItem(ItemPOJOPK)} and {@link TransformerV2CtrlBean#execute(TransformerContext, TransformerCallBack)}
     * 
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void extractUsingTransformer(
    		ItemPOJOPK pojoPK,
    		TransformerPOJOPK transformerPOJOPK,
    		TransformerPluginContext context,
    		TransformerPluginCallBack globalCallBack
    ) 
    throws XtentisException{                 
    	
        try {
        	TransformerContext newContext = TransformerPluginContext.getNewTransformerContext(context, transformerPOJOPK);
        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("extractUsingTransformer "+newContext.getTransformerV2POJOPK().getUniqueId());           	
        	newContext.put("com.amalto.core.ejb.itemctrl.globalCallBack", globalCallBack);
        	
    		ItemPOJO item =getItem(pojoPK);
            if (item==null) {
            	String err = 
            		"Unable to extract item " +pojoPK.getConceptName()+"["+Util.joinStrings(pojoPK.getIds(),".")+"]"+
            		" using transformer "+newContext.getTransformerV2POJOPK().getUniqueId()+". The item cannot be found.";
            	org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
            	throw new XtentisException(err);
            }

        	Util.getTransformerV2CtrlLocal().execute(
        			newContext, 
        			new TypedContent(item.getProjectionAsString().getBytes("utf-8"),"text/xml; charset=\"utf-8\""),
        			new TransformerCallBack() {
        				public void contentIsReady(TransformerContext context) throws XtentisException {
        					TransformerPluginCallBack callBack = (TransformerPluginCallBack)context.get("com.amalto.core.ejb.itemctrl.globalCallBack");
        					try {
	        					callBack.contentIsReady(
	        							0, 
	        							com.amalto.core.util.TypedContent.getOldTypedContent(context.getFromPipeline(TransformerV2CtrlBean.DEFAULT_VARIABLE)), 
	        							TransformerPluginContext.getOldTransformerContext(context)
	        					);
        					} catch (IOException e) {
        				    	e.printStackTrace();
        			    	    String err = "Unable to call the execute call back 'content is ready' of Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
        			    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
        			    	    throw new XtentisException(err);

        					}
        				}
        				public void done(TransformerContext context) throws XtentisException {
        					TransformerPluginCallBack callBack = (TransformerPluginCallBack)context.get("com.amalto.core.ejb.itemctrl.globalCallBack");
        					try {
	        					callBack.done(
	        							0, 
	        							TransformerPluginContext.getOldTransformerContext(context)
	        					);
        					} catch (IOException e) {
        				    	e.printStackTrace();
        			    	    String err = "Unable to call the execute the call back 'done' of Transformer: "+context.getTransformerV2POJOPK().getUniqueId()
        			    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
        			    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
        			    	    throw new XtentisException(err);
        					}
        				}//done
        			}
        	);//execute
        	
        	//do not wait for projection to finish even if it is asynchronous
        	//this is handled by the calling object

  
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
	    	e.printStackTrace();
	    	String err = 
        		"Unable to extract item " +pojoPK.getConceptName()+"["+Util.joinStrings(pojoPK.getIds(),".")+"]"+
        		" using transformer "+transformerPOJOPK.getUniqueId()+
    	    	": "+e.getClass().getName()+": "+e.getLocalizedMessage();
	    	if ("logging_event".equals(pojoPK.getConceptName())) 
	    		err+="\nTo avoid infinite looping, no other error will be thrown.";
	    	else
	    		org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }

    }
    
    

    /**
     * Extract results thru a view and transform them using a transformer<br/>
     * This call is asynchronous and results will be pushed via the passed {@link TransformerCallBack}
     * 
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
	 * @param context
	 * 		The {@link TransformerContext} containi the inital context and the transformer name
	 * @param globalCallBack
	 * 		The callback function called by the transformer when it completes a step
	 * @param viewPOJOPK
	 * 		A filtering view  
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param orderBy
     * 		The full path of the item user to order
     * @param direction
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public void extractUsingTransformerThroughView(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		TransformerContext context,
    		TransformerCallBack globalCallBack,
    		ViewPOJOPK viewPOJOPK,
			IWhereItem whereItem,
			int spellThreshold,
			String orderBy,
			String direction,
			int start,
			int limit
    ) 
    throws XtentisException{          
    	
        try {
        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("extractUsingTransformerThroughView() ");

        	context.put("com.amalto.core.ejb.itemctrl.globalCallBack", globalCallBack);
        	context.put("com.amalto.core.ejb.itemctrl.count", new Integer(0));
        	
    		//perform search 
        	ArrayList<String> raws = viewSearch(dataClusterPOJOPK, viewPOJOPK, whereItem, spellThreshold, orderBy, direction, start, limit);
        	
        	//transform
        	for (Iterator<String> iter = raws.iterator(); iter.hasNext(); ) {
				String raw = iter.next();			
	        	Util.getTransformerV2CtrlLocal().execute(
	        			context, 
	        			new TypedContent(raw.getBytes("utf-8"),"text/xml; charset=\"utf-8\""), 
	        			new TransformerCallBack() {
	        				public void contentIsReady(TransformerContext context) throws XtentisException {
	        					//add numberd content to the pipeline
	        					TypedContent content = context.getFromPipeline(TransformerV2CtrlBean.DEFAULT_VARIABLE);
	        					int count = ((Integer)context.get("com.amalto.core.ejb.itemctrl.count")).intValue()+1;
	        					context.putInPipeline("com.amalto.core.extract."+count, content);
	        					//context.put(TransformerCtrlBean.CTX_PIPELINE, pipeline);
	        					context.put("com.amalto.core.ejb.itemctrl.count",new Integer(count));
	        					TransformerCallBack globalCallBack =(TransformerCallBack) context.get("com.amalto.core.ejb.itemctrl.globalCallBack");
	        					globalCallBack.contentIsReady(context);
	        				}
	        				public void done(TransformerContext context) throws XtentisException {
	        					//do not notify
	        				}
	        			}
	        	);//execute
			}
        	
        	//notify that it is the end
        	globalCallBack.done(context);
 
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
	    	String err = 
        		"Unable to extract items using transformer "+context.getTransformerV2POJOPK().getUniqueId()+" through view "+viewPOJOPK.getUniqueId()+
    	    	": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }
    
    /**
     * Extract results thru a view and transform them using a transformer<br/>
     * This call is asynchronous and results will be pushed via the passed {@link TransformerCallBack}
     * 
     * @param dataClusterPOJOPK
     * 		The Data Cluster where to run the query
	 * @param transformerPOJOPK
	 * 		The transformer to use
	 * @param viewPOJOPK
	 * 		A filtering view  
     * @param whereItem
     * 		The condition
     * @param spellThreshold
     * 		The condition spell checking threshold. A negative value de-activates spell
     * @param orderBy
     * 		The full path of the item user to order
     * @param direction
     * 		One of {@link IXmlServerSLWrapper#ORDER_ASCENDING} or {@link IXmlServerSLWrapper#ORDER_DESCENDING}
     * @param start
     * 		The first item index (starts at zero)
     * @param limit
     * 		The maximum number of items to return
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TransformerContext extractUsingTransformerThroughView(
    		DataClusterPOJOPK dataClusterPOJOPK,
    		TransformerV2POJOPK transformerPOJOPK,
    		ViewPOJOPK viewPOJOPK,
			IWhereItem whereItem,
			int spellThreshold,
			String orderBy,
			String direction,
			int start,
			int limit
    ) 
    throws XtentisException{          
    	
        try {
        	org.apache.log4j.Logger.getLogger(this.getClass()).debug("extractUsingTransformerThroughView() ");
           	
        	TransformerContext context = new TransformerContext(transformerPOJOPK);
        	ArrayList<TypedContent> content = new ArrayList<TypedContent>();
        	context.put("com.amalto.core.itemctrl2.content", content);
        	context.put("com.amalto.core.itemctrl2.ready", new Boolean(false));
    		TransformerCallBack globalCallBack = new TransformerCallBack() {
    			public void contentIsReady(TransformerContext context) throws XtentisException {
    			}
    			public void done(TransformerContext context) throws XtentisException {
    				context.put("com.amalto.core.itemctrl2.ready", new Boolean(true));
    			}
    		};

    		extractUsingTransformerThroughView(dataClusterPOJOPK, context, globalCallBack, viewPOJOPK, whereItem, spellThreshold, orderBy, direction, start, limit);

    		while (! ((Boolean)context.get("com.amalto.core.itemctrl2.ready")).booleanValue()) {
    			try {Thread.sleep(50);} catch (InterruptedException e) {}
    		}
    		return context;
 
	    } catch (XtentisException e) {
	    	throw (e);
	    } catch (Exception e) {
	    	String err = 
        		"Unable to extract items using transformer "+transformerPOJOPK.getUniqueId()+" through view "+viewPOJOPK.getUniqueId()+
    	    	": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }

    }
    
  
    
    
    /*****************************************************************
     *  D I R E C T   Q U E R Y
    *****************************************************************/

    /**
	 * @param revisionID
	 * 			The ID of the revision, <code>null</code> to run from the head
	 * @param dataClusterPOJOPK
	 * 			The unique ID of the cluster,  <code>null</code> to run from the head of the revision ID
	 * @param query 
	 * 			The query in the native language
	 * @param parameters 
	 * 			Optional parameter values to replace the %n in the query before execution
	 * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */   
    public ArrayList<String> runQuery(
    		String revisionID,
    		DataClusterPOJOPK dataClusterPOJOPK,
    		String query,
    		String[] parameters
    	)
    	throws XtentisException{

    	XmlServerSLWrapperLocal server = null;
		try {
			server  =  ((XmlServerSLWrapperLocalHome)new InitialContext().lookup(XmlServerSLWrapperLocalHome.JNDI_NAME)).create();
		} catch (Exception e) {
			String err = "Unable to search items in data cluster '"+dataClusterPOJOPK.getUniqueId()+"': unable to access the XML Server wrapper";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err);
			throw new XtentisException(err);
		}
    	
    	try {	
      
    		ArrayList<String> res =  server.runQuery(
            	revisionID,
        		(dataClusterPOJOPK == null ? null : dataClusterPOJOPK.getUniqueId()), 
        		query,
        		parameters
            );
    		
    		org.apache.log4j.Logger.getLogger(this.getClass()).trace("runQuery() in '"+revisionID+"/"+dataClusterPOJOPK.getUniqueId()+"' " +
    				"returned "+res.size()+" items \n"+query
    		);
    		
    		return res;

	    } catch (Exception e) {
    	    String err = "Unable to perform a direct query: "
    	    		+": "+e.getClass().getName()+": "+e.getLocalizedMessage();
    	    org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
    	    throw new XtentisException(err);
	    }
    }    
    
    
    /**
     * Returns a map with keys being the concepts found in the Data Cluster and as value the revisionID  
     * @param dataClusterPOJOPK
     * @return
     * 		A {@link TreeMap} of concept names to revision IDs
     * @throws XtentisException
     * 
     * @ejb.interface-method view-type = "both"
     * @ejb.facade-method 
     */
    public TreeMap<String, String> getConceptsInDataCluster(DataClusterPOJOPK dataClusterPOJOPK) throws XtentisException {
    	try {
			LocalUser user = LocalUser.getLocalUser();
			boolean authorized = false;
	    	if ("admin".equals(user.getUsername()) || LocalUser.UNAUTHENTICATED_USER.equals(user.getUsername())) { 
	    		authorized = true;
	    	} else if (user.userCanRead(DataClusterPOJO.class, dataClusterPOJOPK.getUniqueId())) {
	    		authorized = true;
	    	}
	    	if (! authorized) {
	    		throw new RemoteException("Unauthorized read access on data cluster "+dataClusterPOJOPK.getUniqueId()+" by user "+user.getUsername());
	    	}
			
			//FIXME: getConceptsInDataCluster works with xQuery only
			//This should be moved to ItemCtrl
	 		String query = "distinct-values(/ii/n/text())";
			
	 		//get the concepts
	 		TreeMap<String, String> concepts = new TreeMap<String, String>();
	 		//get the universe
	 		UniversePOJO universe = user.getUniverse();
	 		//make sure we do not check a revision twice
	 		ArrayList<String> revisionsChecked = new ArrayList<String>();
	 		
	 		//First go through every revision
	 		Set<String> patterns = universe.getItemsRevisionIDs().keySet();
	 		for (Iterator<String> iterator = patterns.iterator(); iterator.hasNext(); ) {
				String pattern = iterator.next();
				String revisionID = universe.getConceptRevisionID(pattern);
				String revisionKey = (revisionID == null) || "".equals(revisionID) ? "__$DEFAULT$__" : revisionID;
				if (revisionsChecked.contains(revisionKey)) continue;
				//fetch all the concepts
				ArrayList<String> conceptsFound = runQuery(
					revisionID,
					dataClusterPOJOPK,
					query, 
					null
				);
				//validate the concepts found
				for (Iterator<String> iterator2 = conceptsFound.iterator(); iterator2.hasNext(); ) {
					String concept = iterator2.next();
					if (concept.matches(pattern) && (concepts.get(concept)==null)) 
						concepts.put(concept, revisionID == null ? "" : revisionID);
				}
			}
	 		
	 		//Then validate the concepts found in the default revision
	 		String revisionID = universe.getDefaultItemRevisionID();
			String revisionKey = (revisionID == null) || "".equals(revisionID) ? "__$DEFAULT$__" : revisionID;
			
			if (! revisionsChecked.contains(revisionKey)) {
				//fetch all the concepts
				ArrayList<String> conceptsFound = runQuery(
					revisionID,
					dataClusterPOJOPK,
					query, 
					null
				);
				//validate the concepts found
				for (Iterator<String> iterator2 = conceptsFound.iterator(); iterator2.hasNext(); ) {
					String concept = iterator2.next();
					if (concepts.get(concept) == null) concepts.put(concept, revisionID == null ? "" : revisionID);
				}				
			}
							
	 		return concepts;

		} catch (Exception e) {
			String err = "Unable to search for concept names in the data cluster '"+dataClusterPOJOPK.getUniqueId()+"'";
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err,e);
			throw new XtentisException(err);		
		}
    }
	
	
	

}