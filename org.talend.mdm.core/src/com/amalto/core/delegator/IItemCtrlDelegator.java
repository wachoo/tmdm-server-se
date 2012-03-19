package com.amalto.core.delegator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import javax.naming.InitialContext;

import org.apache.log4j.Logger;
import org.talend.mdm.commmon.util.core.MDMConfiguration;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.amalto.core.ejb.ItemPOJO;
import com.amalto.core.ejb.ItemPOJOPK;
import com.amalto.core.ejb.ObjectPOJO;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocal;
import com.amalto.core.ejb.local.XmlServerSLWrapperLocalHome;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJO;
import com.amalto.core.objects.datacluster.ejb.DataClusterPOJOPK;
import com.amalto.core.objects.role.ejb.RolePOJO;
import com.amalto.core.objects.role.ejb.RolePOJOPK;
import com.amalto.core.objects.universe.ejb.UniversePOJO;
import com.amalto.core.objects.view.ejb.ViewPOJO;
import com.amalto.core.objects.view.ejb.ViewPOJOPK;
import com.amalto.core.util.CVCException;
import com.amalto.core.util.LocalUser;
import com.amalto.core.util.RoleSpecification;
import com.amalto.core.util.RoleWhereCondition;
import com.amalto.core.util.Util;
import com.amalto.core.util.XSDKey;
import com.amalto.core.util.XtentisException;
import com.amalto.xmlserver.interfaces.IWhereItem;
import com.amalto.xmlserver.interfaces.WhereAnd;

public abstract class IItemCtrlDelegator implements IBeanDelegator,
		IItemCtrlDelegatorService {
	Logger logger = Logger.getLogger(IItemCtrlDelegator.class);

	// methods from ItemCtrl2Bean
	public ArrayList<String> getItemsPivotIndex(String clusterName,
			String mainPivotName,
			LinkedHashMap<String, String[]> pivotWithKeys, String[] indexPaths,
			IWhereItem whereItem, String[] pivotDirections,
			String[] indexDirections, int start, int limit)
			throws XtentisException {
		try {

			// validate parameters
			if (pivotWithKeys.size() == 0) {
				String err = "The Map of pivots must contain at least one element";
				logger.error(err);
				throw new XtentisException(err);
			}

			if (indexPaths.length == 0) {
				String err = "The Array of Index Paths must contain at least one element";
				logger.error(err);
				throw new XtentisException(err);
			}

			// get the universe and revision ID
			ILocalUser localuser = getLocalUser();
			UniversePOJO universe = localuser.getUniverse();
			if (universe == null) {
				String err = "ERROR: no Universe set for user '"
						+ localuser.getUsername() + "'";
				logger.error(err);
				throw new XtentisException(err);
			}

			ViewPOJOPK viewPOJOPK = new ViewPOJOPK("Browse_items_"
					+ mainPivotName);
			ViewPOJO view = getViewPOJO(viewPOJOPK);

			// Create an ItemWhere which combines the search and and view wheres
			IWhereItem fullWhere;
			ArrayList conditions = view.getWhereConditions().getList();
			Util.fixCondtions(conditions);
			fullWhere = getFullWhereCondition(whereItem, conditions);

			// Add View Filters from the Roles
			ArrayList<IWhereItem> roleWhereConditions = getViewWCFromRole(viewPOJOPK);
			fullWhere = getFullWhereCondition(whereItem, roleWhereConditions);

			// add recordsSecurity filters for the Role
			roleWhereConditions = getRecordsSecurityFromRole(mainPivotName);
			fullWhere = getFullWhereCondition(whereItem, roleWhereConditions);

			return runPivotIndexQuery(clusterName, mainPivotName,
					pivotWithKeys, universe.getItemsRevisionIDs(),
					universe.getDefaultItemRevisionID(), indexPaths, fullWhere,
					pivotDirections, indexDirections, start, limit);

		} catch (XtentisException e) {
			throw (e);
		} catch (Exception e) {
			String err = "Unable to search: " + ": " + e.getClass().getName()
					+ ": " + e.getLocalizedMessage();
			logger.error(err, e);
			throw new XtentisException(err);
		}
	}

	public ArrayList<String> getChildrenItems(String clusterName,
			String conceptName, String[] PKXpaths, String FKXpath,
			String labelXpath, String fatherPK, IWhereItem whereItem,
			int start, int limit) throws XtentisException {
		try {
			// get the universe and revision ID
			ILocalUser localuser = getLocalUser();
			UniversePOJO universe = localuser.getUniverse();
			if (universe == null) {
				String err = "ERROR: no Universe set for user '"
						+ localuser.getUsername() + "'";
				logger.error(err);
				throw new XtentisException(err);
			}

			ViewPOJOPK viewPOJOPK = new ViewPOJOPK("Browse_items_"
					+ conceptName);
			ViewPOJO view = getViewPOJO(viewPOJOPK);

			// Create an ItemWhere which combines the search and and view wheres
			IWhereItem fullWhere;
			ArrayList conditions = view.getWhereConditions().getList();
			Util.fixCondtions(conditions);
			fullWhere = getFullWhereCondition(whereItem, conditions);

			// add recordsSecurity filters for the Role
			ArrayList<IWhereItem> roleWhereConditions = getRecordsSecurityFromRole(conceptName);
			fullWhere = getFullWhereCondition(whereItem, roleWhereConditions);

			return runChildrenItemsQuery(clusterName, conceptName, PKXpaths,
					FKXpath, labelXpath, fatherPK,
					universe.getItemsRevisionIDs(),
					universe.getDefaultItemRevisionID(), fullWhere, start,
					limit);

		} catch (XtentisException e) {
			throw (e);
		} catch (Exception e) {
			String err = "Unable to search: " + ": " + e.getClass().getName()
					+ ": " + e.getLocalizedMessage();
			logger.error(err, e);
			throw new XtentisException(err);
		}
	}

	public void resendFailtSvnMessage() throws Exception {

	}

	public ArrayList<String> viewSearch(DataClusterPOJOPK dataClusterPOJOPK,
			ViewPOJOPK viewPOJOPK, IWhereItem whereItem, int spellThreshold,
			String orderBy, String direction, int start, int limit)
			throws XtentisException {
		// get the universe and revision ID
		ILocalUser localuser = getLocalUser();
		UniversePOJO universe = localuser.getUniverse();
		if (universe == null) {
			String err = "ERROR: no Universe set for user '"
					+ localuser.getUsername() + "'";
			logger.error(err);
			throw new XtentisException(err);
		}

		// build the patterns to revision ID map
		LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(
				universe.getItemsRevisionIDs());
		if (universe.getDefaultItemRevisionID() != null
				&& universe.getDefaultItemRevisionID().length() > 0)
			conceptPatternsToRevisionID.put(".*",
					universe.getDefaultItemRevisionID());

		// build the patterns to cluster map - only one cluster at this stage
		LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
		conceptPatternsToClusterName.put(".*", dataClusterPOJOPK.getUniqueId());

		try {

			ViewPOJO view = getViewPOJO(viewPOJOPK);
			// ViewLocal view =
			// ViewUtil.getLocalHome().findByPrimaryKey(viewPK);
			// ///////////////////////////////////////////////////////
			whereItem = Util.fixWebCondtions(whereItem);
			// Create an ItemWhere which combines the search and and view wheres
			IWhereItem fullWhere;
			ArrayList conditions = view.getWhereConditions().getList();
			// fix conditions:value of condition do not generate xquery.
			Util.fixCondtions(conditions);
			// add view where conditions
			fullWhere = getFullWhereCondition(whereItem, conditions);

			// add View Filters from the Roles
			ArrayList<IWhereItem> roleWhereConditions = getViewWCFromRole(viewPOJOPK);
			fullWhere = getFullWhereCondition(whereItem, roleWhereConditions);

			// add recordsSecurity filters for the Role
			roleWhereConditions = getRecordsSecurityFromRole(null);
			fullWhere = getFullWhereCondition(whereItem, roleWhereConditions);

			Map<String, ArrayList<String>> metaDataTypes = getMetaTypes(fullWhere);
			return runItemsQuery(conceptPatternsToRevisionID,
					conceptPatternsToClusterName, null, view
							.getViewableBusinessElements().getList(),
							fullWhere, orderBy, direction, start, limit,
					spellThreshold, true, metaDataTypes, true);

		} catch (XtentisException e) {
			throw (e);
		} catch (Exception e) {
			String err = "Unable to single search: " + ": "
					+ e.getClass().getName() + ": " + e.getLocalizedMessage();
			org.apache.log4j.Logger.getLogger(this.getClass()).error(err, e);
			throw new XtentisException(err);
		}
	}

	public ItemPOJOPK putItem(ItemPOJO item, String schema, String dataModelName)
			throws XtentisException {
		logger.trace("putItem() " + item.getItemPOJOPK().getUniqueID());
		String concept = item.getConceptName();
		String dataCluster = item.getDataClusterPOJOPK().getIds()[0];
		try {
			if (schema != null) {

				if (Util.getUUIDNodes(schema, concept).size() > 0) { // check
																		// uuid
																		// key
																		// exists

					Document schema1 = Util.parse(schema);
					Node n = Util.processUUID(item.getProjection(), schema,
							dataCluster, concept);
					XSDKey conceptKey = com.amalto.core.util.Util
							.getBusinessConceptKey(schema1, concept);
					// get key values
					String[] itemKeyValues = com.amalto.core.util.Util
							.getKeyValuesFromItem((Element) n, conceptKey);
					// reset item projection & itemids
					item.setProjectionAsString(Util.nodeToString(n));
					item.setItemIds(itemKeyValues);
				}

				Util.validate(item.getProjection(), schema);
			}

			// FIXME: update the vocabulary . Universe dependent?
			/*
			 * DataClusterLocal dc =
			 * (DataClusterLocal)dataClusters.get(item.getDataClusterPK
			 * ().getName()); if (dc == null) { dc =
			 * DataClusterUtil.getLocalHome
			 * ().findByPrimaryKey(item.getDataClusterPK());
			 * dataClusters.put(item.getDataClusterPK().getName(), dc); } if
			 * (dc.getSpellerRefreshPeriodInSeconds()>-1)
			 * dc.addToVocabulary(item.getProjection());
			 */

			// make sure the plan is reset
			item.setPlanPK(null);
			// used for binding data model
			if (dataModelName != null)
				item.setDataModelName(dataModelName);
			// Store
			ItemPOJOPK pk = item.store();
			if (pk == null)
				throw new XtentisException("Could not put item "
						+ Util.joinStrings(item.getItemIds(), ".")
						+ ".Check the XML Server logs");

			return pk;
		} catch (XtentisException e) {
			throw (e);
		} catch (CVCException e) {
			throw new XtentisException(e.getLocalizedMessage(), e);
		} catch (Exception e) {
			String prefix = "Unable to create/update the item "
					+ item.getDataClusterPOJOPK().getUniqueId() + "."
					+ Util.joinStrings(item.getItemIds(), ".") + ": ";
			String err = prefix + e.getClass().getName() + ": "
					+ e.getLocalizedMessage();
			logger.error(err, e);
			// simplify the error message
			if (dataModelName.equalsIgnoreCase("Reporting")) {
				if (err.indexOf("One of '{ListOfFilters}'") != -1) {
					err = prefix + "At least one filter must be defined";
				}
			}

			throw new XtentisException(err);
		}
	}

	public ArrayList<String> xPathsSearch(DataClusterPOJOPK dataClusterPOJOPK,
			String forceMainPivot, ArrayList<String> viewablePaths,
			IWhereItem whereItem, int spellThreshold, String orderBy,
			String direction, int start, int limit, boolean returnCount)
			throws XtentisException {
		try {
			if (viewablePaths.size() == 0) {
				String err = "The list of viewable xPaths must contain at least one element";
				logger.error(err);
				throw new XtentisException(err);
			}
			// Check if user is allowed to read the cluster
			ILocalUser user = getLocalUser();
			boolean authorized = false;
			if (MDMConfiguration.getAdminUser().equals(user.getUsername())
					|| LocalUser.UNAUTHENTICATED_USER
							.equals(user.getUsername())) {
				authorized = true;
			} else if (user.userCanRead(DataClusterPOJO.class,
					dataClusterPOJOPK.getUniqueId())) {
				authorized = true;
			}
			if (!authorized) {
				throw new XtentisException(
						"Unauthorized read access on data cluster '"
								+ dataClusterPOJOPK.getUniqueId()
								+ "' by user '" + user.getUsername() + "'");
			}

			// get the universe and revision ID
			UniversePOJO universe = user.getUniverse();
			if (universe == null) {
				String err = "ERROR: no Universe set for user '"
						+ LocalUser.getLocalUser().getUsername() + "'";
				logger.error(err);
				throw new XtentisException(err);
			}

			// build the patterns to revision ID map
			LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(
					universe.getItemsRevisionIDs());
			if (universe.getDefaultItemRevisionID() != null) {
				conceptPatternsToRevisionID.put(".*",
						universe.getDefaultItemRevisionID());
			}

			// build the patterns to cluster map - only one cluster at this
			// stage
			LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
			conceptPatternsToClusterName.put(".*",
					dataClusterPOJOPK.getUniqueId());

			// add recordsSecurity filters for the Role
			ArrayList<IWhereItem> roleWhereConditions = getRecordsSecurityFromRole(forceMainPivot);
			whereItem = getFullWhereCondition(whereItem, roleWhereConditions);
			return runItemsQuery(conceptPatternsToRevisionID,
					conceptPatternsToClusterName, forceMainPivot,
					viewablePaths, whereItem, orderBy, direction, start, limit,
					spellThreshold, returnCount, Collections.emptyMap(), false);

		} catch (XtentisException e) {
			throw (e);
		} catch (Exception e) {
			String err = "Unable to single search: " + ": "
					+ e.getClass().getName() + ": " + e.getLocalizedMessage();
			logger.error(err, e);
			throw new XtentisException(err, e);
		}
	}

	public ArrayList<String> getItems(DataClusterPOJOPK dataClusterPOJOPK,
			String conceptName, IWhereItem whereItem, int spellThreshold,
			String orderBy, String direction, int start, int limit,
			boolean totalCountOnFirstRow) throws XtentisException {

		// get the universe and revision ID
		ILocalUser user = getLocalUser();
		UniversePOJO universe = user.getUniverse();
		if (universe == null) {
			String err = "ERROR: no Universe set for user '"
					+ user.getUsername() + "'";
			logger.error(err);
			throw new XtentisException(err);
		}

		// build the patterns to revision ID map
		LinkedHashMap<String, String> conceptPatternsToRevisionID = new LinkedHashMap<String, String>(
				universe.getItemsRevisionIDs());
		if (universe.getDefaultItemRevisionID() != null
				&& universe.getDefaultItemRevisionID().length() > 0)
			conceptPatternsToRevisionID.put(".*",
					universe.getDefaultItemRevisionID());

		// build the patterns to cluster map - only one cluster at this stage
		LinkedHashMap<String, String> conceptPatternsToClusterName = new LinkedHashMap<String, String>();
		conceptPatternsToClusterName.put(".*", dataClusterPOJOPK.getUniqueId());

		try {
			ArrayList<String> elements = new ArrayList<String>();
			elements.add(conceptName);
			// add recordsSecurity filters for the Role
			ArrayList<IWhereItem> roleWhereConditions = getRecordsSecurityFromRole(conceptName);
			whereItem = getFullWhereCondition(whereItem, roleWhereConditions);
			
			return runItemsQuery(conceptPatternsToRevisionID,
					conceptPatternsToClusterName, null, elements, whereItem,
					orderBy, direction, start, limit, spellThreshold,
					totalCountOnFirstRow, Collections.emptyMap(), false);
		} catch (XtentisException e) {
			throw (e);
		} catch (Exception e) {
			String err = "Unable to get the items: " + ": "
					+ e.getClass().getName() + ": " + e.getLocalizedMessage();
			logger.error(err, e);
			throw new XtentisException(err, e);
		}
	}

	protected Map<String, ArrayList<String>> getMetaTypes(IWhereItem fullWhere)throws Exception{
		return Util.getMetaDataTypes(fullWhere);
	}
	/**
	 * 
	 * get view where conditions from Role CE version return empty
	 * 
	 * @param viewPOJOPK
	 * @return
	 * @throws Exception
	 */
	protected abstract ArrayList<IWhereItem> getViewWCFromRole(ViewPOJOPK viewPOJOPK)throws Exception ;

	/*
	 * get the recordsSecurity where conditions CE version return empty
	 */
	protected abstract ArrayList<IWhereItem> getRecordsSecurityFromRole(String mainPivotName)throws Exception; 

	protected IWhereItem getFullWhereCondition(IWhereItem whereItem,
			ArrayList<IWhereItem> conditions) {
		IWhereItem fullWhere;
		if (conditions == null || conditions.size() == 0) {
			if (whereItem == null)
				fullWhere = null;
			else
				fullWhere = whereItem;
		} else {
			if (whereItem == null) {
				fullWhere = new WhereAnd(conditions);
			} else {
				WhereAnd viewWhere = new WhereAnd(conditions);
				WhereAnd wAnd = new WhereAnd();
				wAnd.add(whereItem);
				wAnd.add(viewWhere);
				fullWhere = wAnd;
			}
		}
		return fullWhere;
	}

	/****************** test interfaces ******************************/

	@Override
	public ViewPOJO getViewPOJO(ViewPOJOPK viewPOJOPK) throws Exception {
		return Util.getViewCtrlLocalHome().create().getView(viewPOJOPK);
	}

	@Override
	public ILocalUser getLocalUser() throws XtentisException {
		return LocalUser.getLocalUser();
	}

	@Override
	public ArrayList<String> runItemsQuery(
			LinkedHashMap conceptPatternsToRevisionID,
			LinkedHashMap conceptPatternsToClusterName, String forceMainPivot,
			ArrayList viewableFullPaths, IWhereItem whereItem, String orderBy,
			String direction, int start, int limit, int spellThreshold,
			boolean firstTotalCount, Map metaDataTypes, boolean withStartLimit)
			throws XtentisException {
		XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
		String query = server.getItemsQuery(conceptPatternsToRevisionID,
				conceptPatternsToClusterName,
				forceMainPivot, // the main pivots will be that of the first
								// element of the viewable list
				viewableFullPaths, whereItem, orderBy, direction, start, limit,
				spellThreshold, firstTotalCount, metaDataTypes);
		logger.debug(query);
		if (withStartLimit)
			return server.runQuery(null, null, query, null, start, limit, true);
		else
			return server.runQuery(null, null, query, null);
	}

	@Override
	public ArrayList<String> runChildrenItemsQuery(String clusterName,
			String conceptName, String[] PKXpaths, String FKXpath,
			String labelXpath, String fatherPK, LinkedHashMap itemsRevisionIDs,
			String defaultRevisionID, IWhereItem whereItem, int start, int limit)
			throws XtentisException {
		XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
		String query = server.getChildrenItemsQuery(clusterName, conceptName,
				PKXpaths, FKXpath, labelXpath, fatherPK, itemsRevisionIDs,
				defaultRevisionID, whereItem, start, limit);

		logger.debug(query);
		return server.runQuery(null, null, query, null);
	}

	@Override
	public ArrayList<String> runPivotIndexQuery(String clusterName,
			String mainPivotName, LinkedHashMap pivotWithKeys,
			LinkedHashMap itemsRevisionIDs, String defaultRevisionID,
			String[] indexPaths, IWhereItem whereItem,
			String[] pivotDirections, String[] indexDirections, int start,
			int limit) throws XtentisException {
		XmlServerSLWrapperLocal server = Util.getXmlServerCtrlLocal();
		String query = server.getPivotIndexQuery(clusterName, mainPivotName,
				pivotWithKeys, itemsRevisionIDs, defaultRevisionID, indexPaths,
				whereItem, pivotDirections, indexDirections, start, limit);
		logger.debug(query);
		return server.runQuery(null, null, query, null, start, limit, false);
	}
}
